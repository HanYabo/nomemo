package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class ReminderActivity : BaseComposeActivity() {
    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_PENDING = "PENDING"
        private const val FILTER_DONE = "DONE"
    }

    private lateinit var memoryStore: MemoryStore
    private var selectedFilter by mutableStateOf(FILTER_ALL)
    private var reminderRecords by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null
    private var hasHandledInitialResume = false
    private var startupDockPulseTab: NoMemoDockTab? = null

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                recreate()
            } else {
                refreshReminders()
            }
        }

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            refreshReminders()
        }
    }

    override fun enableDoubleBackToDesktop(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(MainActivity.createPrimaryTabIntent(this, NoMemoDockTab.REMINDER))
        overridePendingTransition(R.anim.primary_page_enter, R.anim.primary_page_exit)
        finish()
        return
        startupDockPulseTab = consumePrimaryDockPulse()
        memoryStore = MemoryStore(this)
        setContent {
            ReminderContent(
                records = reminderRecords,
                hasLoadedRecords = hasLoadedRecords,
                selectedFilter = selectedFilter,
                onDeleteRecords = { recordIds -> deleteRecords(recordIds) },
                onFilterSelected = { filter ->
                    selectedFilter = filter
                    refreshReminders()
                },
                onDoneChanged = { record, done ->
                    memoryStore.updateReminderDone(record.recordId, done)
                    refreshReminders()
                    Toast.makeText(this, R.string.reminder_done_success, Toast.LENGTH_SHORT).show()
                },
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onOpenMemory = { openMemoryPage() },
                onOpenGroup = { openGroupPage() },
                onOpenSearch = { openSearchPage() },
                onOpenSettings = { openSettingsPage() },
                showAddSheet = showAddSheet,
                onAddClick = { showAddSheet = true },
                onDismissAddSheet = { showAddSheet = false },
                startupDockPulseTab = startupDockPulseTab
            )
        }
        refreshReminders()
    }

    override fun onResume() {
        super.onResume()
        if (!hasHandledInitialResume) {
            hasHandledInitialResume = true
            return
        }
        refreshReminders()
    }

    override fun onStart() {
        super.onStart()
        registerMemoryChangeReceiver()
    }

    override fun onStop() {
        unregisterMemoryChangeReceiver()
        super.onStop()
    }

    private fun refreshReminders() {
        val filterSnapshot = selectedFilter
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            val filtered = withContext(Dispatchers.IO) {
                val all = memoryStore.loadReminderRecords()
                val result = all.filter { record ->
                    when (filterSnapshot) {
                        FILTER_PENDING -> !record.isReminderDone
                        FILTER_DONE -> record.isReminderDone
                        else -> true
                    }
                }
                prewarmMemoryThumbnailCache(applicationContext, result)
                result
            }
            if (selectedFilter == filterSnapshot) {
                reminderRecords = filtered
                hasLoadedRecords = true
            }
        }
    }

    private fun registerMemoryChangeReceiver() {
        if (memoryChangeRegistered) {
            return
        }
        ContextCompat.registerReceiver(
            this,
            memoryChangeReceiver,
            IntentFilter(MemoryStoreNotifier.ACTION_RECORDS_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        memoryChangeRegistered = true
    }

    private fun unregisterMemoryChangeReceiver() {
        if (!memoryChangeRegistered) {
            return
        }
        unregisterReceiver(memoryChangeReceiver)
        memoryChangeRegistered = false
    }

    private fun openMemoryPage() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        switchPrimaryPage(intent, pulseTab = NoMemoDockTab.MEMORY)
    }

    private fun openGroupPage() {
        switchPrimaryPage(Intent(this, GroupActivity::class.java), pulseTab = NoMemoDockTab.GROUP)
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
    }

    private fun openSettingsPage() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }

    private fun openSearchPage() {
        startActivity(SearchActivity.createIntent(this))
    }

    private fun deleteRecords(recordIds: Set<String>) {
        if (recordIds.isEmpty()) return
        var deletedCount = 0
        recordIds.forEach { recordId ->
            if (memoryStore.deleteRecord(recordId)) {
                deletedCount += 1
            }
        }
        if (deletedCount > 0) {
            refreshReminders()
            Toast.makeText(this, R.string.reminder_delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun ReminderContent(
        records: List<MemoryRecord>,
        hasLoadedRecords: Boolean,
        selectedFilter: String,
        onDeleteRecords: (Set<String>) -> Unit,
        onFilterSelected: (String) -> Unit,
        onDoneChanged: (MemoryRecord, Boolean) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenGroup: () -> Unit,
        onOpenSearch: () -> Unit,
        onOpenSettings: () -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit,
        startupDockPulseTab: NoMemoDockTab?
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val density = LocalDensity.current
        val titleBlockHeight = if (adaptive.isNarrow) 44.dp else 52.dp
        var selectedRecordIds by remember { mutableStateOf(setOf<String>()) }
        var selectionModeActive by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var moreMenuAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var pendingScrollToTopAfterAdd by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val filteredRecords = records
        val headerCollapseDistancePx = with(density) { 68.dp.toPx() }
        val headerCollapseTarget by remember(selectionModeActive, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
            derivedStateOf {
                if (selectionModeActive) {
                    0f
                } else {
                    when {
                        listState.firstVisibleItemIndex > 0 -> 1f
                        headerCollapseDistancePx <= 0f -> 0f
                        else -> (listState.firstVisibleItemScrollOffset / headerCollapseDistancePx).coerceIn(0f, 1f)
                    }
                }
            }
        }
        val headerCollapseProgress by animateFloatAsState(
            targetValue = headerCollapseTarget,
            animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing),
            label = "reminderHeaderCollapseProgress"
        )
        val expandedTitleAlpha = (1f - headerCollapseProgress).coerceIn(0f, 1f)
        val collapsedTitleAlpha = headerCollapseProgress.coerceIn(0f, 1f)
        val expandedTitleTranslateY = with(density) { (-22).dp.toPx() * headerCollapseProgress }
        val chipsTopPadding = lerp(12.dp, 11.dp, headerCollapseProgress)
        val chipBottomPadding = 12.dp
        val selectedRecords = remember(filteredRecords, selectedRecordIds) {
            filteredRecords.filter { selectedRecordIds.contains(it.recordId) }
        }
        val allVisibleRecordsSelected = filteredRecords.isNotEmpty() &&
            filteredRecords.all { selectedRecordIds.contains(it.recordId) }
        val showCenteredEmptyState = hasLoadedRecords && filteredRecords.isEmpty()
        LaunchedEffect(filteredRecords, selectedRecordIds) {
            val visibleIds = filteredRecords.map { it.recordId }.toSet()
            val sanitizedIds = selectedRecordIds.filter { visibleIds.contains(it) }.toSet()
            if (sanitizedIds != selectedRecordIds) {
                selectedRecordIds = sanitizedIds
            }
            if (selectedRecordIds.isNotEmpty() && sanitizedIds.isEmpty()) {
                showDeleteConfirm = false
            }
        }
        LaunchedEffect(showAddSheet, pendingScrollToTopAfterAdd) {
            if (!showAddSheet && pendingScrollToTopAfterAdd) {
                listState.animateScrollToItem(0)
                pendingScrollToTopAfterAdd = false
            }
        }
        BackHandler(enabled = selectionModeActive) {
            selectionModeActive = false
            selectedRecordIds = emptySet()
            showDeleteConfirm = false
            resetDoubleBackExitState()
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(modifier = Modifier.fillMaxSize()) {
                    // 创建全局 backdrop 层，捕获页面内容用于液态玻璃效果
                    val backdrop = rememberLayerBackdrop {
                        drawContent()  // 绘制内容层（Column 会标记为 layerBackdrop）
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .layerBackdrop(backdrop)  // 标记为内容层
                            .statusBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                end = spec.pageHorizontalPadding,
                                bottom = 0.dp
                            )
                    ) {
                        if (selectionModeActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 12.dp)
                            ) {
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_close,
                                    contentDescription = stringResource(R.string.cancel),
                                    onClick = {
                                        selectionModeActive = false
                                        selectedRecordIds = emptySet()
                                        showDeleteConfirm = false
                                    },
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    size = spec.topActionButtonSize
                                )
                                Text(
                                    text = getString(R.string.selected_count_format, selectedRecords.size),
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                GlassIconCircleButton(
                                    iconRes = if (allVisibleRecordsSelected) {
                                        R.drawable.ic_sheet_deselect_all
                                    } else {
                                        R.drawable.ic_sheet_select_all
                                    },
                                    contentDescription = if (allVisibleRecordsSelected) "取消全选" else "全选",
                                    onClick = {
                                        selectedRecordIds = if (allVisibleRecordsSelected) {
                                            emptySet()
                                        } else {
                                            filteredRecords.map { it.recordId }.toSet()
                                        }
                                        showDeleteConfirm = false
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    size = spec.topActionButtonSize
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spec.topActionButtonSize)
                            ) {
                                NoMemoTopActionButtons(
                                    spec = spec,
                                    onSearchClick = onOpenSearch,
                                    onMoreClick = { moreMenuExpanded = !moreMenuExpanded },
                                    onMoreButtonBoundsChanged = { moreMenuAnchorBounds = it },
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                                Text(
                                    text = stringResource(R.string.reminder_page_title),
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 18.sp else 19.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .graphicsLayer {
                                            alpha = collapsedTitleAlpha
                                        }
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(titleBlockHeight)
                                    .padding(top = 2.dp)
                                    .graphicsLayer {
                                        alpha = expandedTitleAlpha
                                        translationY = expandedTitleTranslateY
                                    }
                            ) {
                                Text(
                                    text = stringResource(R.string.reminder_page_title),
                                    color = palette.textPrimary,
                                    fontSize = spec.titleSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = chipsTopPadding, bottom = chipBottomPadding)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            ReminderChip(stringResource(R.string.reminder_filter_all), selectedFilter == FILTER_ALL, spec.chipTextSize) {
                                onFilterSelected(FILTER_ALL)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            ReminderChip(stringResource(R.string.reminder_filter_pending), selectedFilter == FILTER_PENDING, spec.chipTextSize) {
                                onFilterSelected(FILTER_PENDING)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            ReminderChip(stringResource(R.string.reminder_filter_done), selectedFilter == FILTER_DONE, spec.chipTextSize) {
                                onFilterSelected(FILTER_DONE)
                            }
                        }

                        if (!hasLoadedRecords || filteredRecords.isEmpty()) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f),
                                state = listState,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    top = 2.dp,
                                    bottom = if (selectedRecords.isNotEmpty()) {
                                        spec.pageBottomPadding + if (spec.isNarrow) 18.dp else 22.dp
                                    } else {
                                        spec.pageBottomPadding + 20.dp
                                    }
                                ),
                                verticalArrangement = Arrangement.spacedBy(if (spec.widthClass == NoMemoWidthClass.EXPANDED) 12.dp else 10.dp)
                            ) {
                                items(
                                    items = filteredRecords,
                                    key = { it.recordId },
                                    contentType = { "reminder" }
                                ) { record ->
                                    ReminderItem(
                                        record = record,
                                        selected = selectedRecordIds.contains(record.recordId),
                                        adaptive = adaptive,
                                        palette = palette,
                                        onDoneChanged = onDoneChanged,
                                        onLongPressSelect = {
                                            moreMenuExpanded = false
                                            selectionModeActive = true
                                            selectedRecordIds = if (selectedRecordIds.contains(record.recordId)) {
                                                selectedRecordIds - record.recordId
                                            } else {
                                                selectedRecordIds + record.recordId
                                            }
                                        },
                                        onClickItem = {
                                            when {
                                                selectionModeActive && selectedRecordIds.contains(record.recordId) -> {
                                                    selectedRecordIds = selectedRecordIds - record.recordId
                                                }
                                                selectionModeActive -> {
                                                    selectedRecordIds = selectedRecordIds + record.recordId
                                                }
                                                else -> {
                                                    onOpenDetail(record)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showCenteredEmptyState) {
                        NoMemoEmptyState(
                            iconRes = R.drawable.ic_nm_reminder,
                            title = stringResource(R.string.reminder_empty),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = spec.pageHorizontalPadding)
                        )
                    }

                    if (!selectionModeActive) {
                        LiquidGlassDock(
                            selectedTab = NoMemoDockTab.REMINDER,
                            onOpenMemory = onOpenMemory,
                            onOpenGroup = onOpenGroup,
                            onOpenReminder = {},
                            onAddClick = onAddClick,
                            spec = spec,
                            startupPulseTab = startupDockPulseTab,
                            startupPulseDelayMs = 140L,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(
                                    start = spec.pageHorizontalPadding,
                                    end = spec.pageHorizontalPadding,
                                    bottom = if (spec.isNarrow) 10.dp else 14.dp
                                ),
                            sharedBackdrop = backdrop  // 传入共享的 backdrop，实现真实玻璃效果
                        )
                    } else if (selectedRecords.isNotEmpty()) {
                        NoMemoSelectionActionDock(
                            selectedRecords = selectedRecords,
                            onArchiveClick = {},
                            onDeleteClick = { showDeleteConfirm = true },
                            allSelected = allVisibleRecordsSelected,
                            showArchiveAction = false,
                            backdrop = backdrop,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(
                                    start = spec.pageHorizontalPadding,
                                    end = spec.pageHorizontalPadding,
                                    bottom = if (spec.isNarrow) 18.dp else 22.dp
                                )
                        )
                    }

                    if (showDeleteConfirm && selectedRecordIds.isNotEmpty()) {
                        NoMemoDeleteConfirmDialog(
                            title = stringResource(R.string.delete_selected_title),
                            message = getString(R.string.delete_selected_batch_message, selectedRecordIds.size),
                            onConfirm = {
                                onDeleteRecords(selectedRecordIds)
                                selectionModeActive = false
                                selectedRecordIds = emptySet()
                                showDeleteConfirm = false
                            },
                            onDismiss = { showDeleteConfirm = false }
                        )
                    }

                    NoMemoAnchoredMenu(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                        anchorBounds = moreMenuAnchorBounds,
                        actions = listOf(
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_sheet_check,
                                label = stringResource(R.string.action_select_all),
                                onClick = {
                                    moreMenuExpanded = false
                                    showDeleteConfirm = false
                                    selectionModeActive = true
                                    selectedRecordIds = filteredRecords.map { it.recordId }.toSet()
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_memory,
                                label = "已归档记忆",
                                onClick = {
                                    moreMenuExpanded = false
                                    startActivity(ArchivedMemoryActivity.createIntent(this@ReminderActivity))
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_settings,
                                label = stringResource(R.string.action_settings),
                                onClick = {
                                    moreMenuExpanded = false
                                    onOpenSettings()
                                }
                            )
                        )
                    )

                    if (showAddSheet) {
                        AddMemorySheet(
                            onDismiss = onDismissAddSheet,
                            onSaved = { pendingScrollToTopAfterAdd = true }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ReminderChip(
        text: String,
        selected: Boolean,
        chipTextSize: TextUnit,
        onClick: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        GlassChip(
            text = text,
            selected = selected,
            onClick = onClick,
            showBorder = false,
            horizontalPadding = if (adaptive.isNarrow) 18.dp else 24.dp,
            verticalPadding = if (adaptive.isNarrow) 11.dp else 12.dp,
            textStyle = TextStyle(
                fontSize = (chipTextSize.value + 1f).sp,
                fontWeight = FontWeight.Bold
            )
        )
    }

    @Composable
    private fun ReminderItem(
        record: MemoryRecord,
        selected: Boolean,
        adaptive: NoMemoAdaptiveSpec,
        palette: NoMemoPalette,
        onDoneChanged: (MemoryRecord, Boolean) -> Unit,
        onLongPressSelect: () -> Unit,
        onClickItem: () -> Unit
    ) {
        val haptic = LocalHapticFeedback.current
        val isDark = isSystemInDarkTheme()
        val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
        val cardShape = noMemoG2RoundedShape(if (adaptive.isNarrow) 28.dp else 30.dp)
        val cardBackground = if (selected) {
            noMemoSelectedCardGradient(isDark).first()
        } else {
            if (isDark) noMemoCardSurfaceColor(true) else Color.White.copy(alpha = 0.995f)
        }
        val title = when {
            !record.title.isNullOrBlank() -> record.title
            !record.memory.isNullOrBlank() -> record.memory
            !record.sourceText.isNullOrBlank() -> record.sourceText
            else -> getString(R.string.reminder_default_title)
        }
        val hasReminderTime = record.reminderAt > 0L
        val time = if (hasReminderTime) record.reminderAt else record.createdAt
        val meta = "${record.categoryName} | ${dateFormat.format(Date(time))}"
        val countdown = if (hasReminderTime) buildCountdownLabel(time) else getString(R.string.reminder_not_set)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(cardBackground)
                .pointerInput(onLongPressSelect, onClickItem) {
                    detectTapGestures(
                        onTap = { onClickItem() },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPressSelect()
                        }
                    )
                }
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = record.isReminderDone,
                onCheckedChange = { onDoneChanged(record, it) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title ?: "",
                    color = palette.textPrimary.copy(alpha = if (record.isReminderDone) 0.55f else 1f),
                    fontSize = if (adaptive.widthClass == NoMemoWidthClass.EXPANDED) 17.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    textDecoration = if (record.isReminderDone) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(
                    text = meta,
                    color = palette.textSecondary,
                    fontSize = if (adaptive.isNarrow) 11.sp else 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (!record.isReminderDone) {
                    Text(
                        text = countdown,
                        color = if (!hasReminderTime || time >= System.currentTimeMillis()) palette.accent else Color(0xFFB42318),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }

    private fun buildCountdownLabel(targetTimeMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = targetTimeMillis - now
        val absolute = abs(diff)
        val minutes = absolute / 60_000L
        val hours = minutes / 60L
        val days = hours / 24L
        val display = when {
            days > 0L -> "${days}天"
            hours > 0L -> "${hours}小时"
            else -> "${minutes.coerceAtLeast(1L)}分钟"
        }
        return if (diff >= 0L) {
            getString(R.string.reminder_due_in, display)
        } else {
            getString(R.string.reminder_overdue, display)
        }
    }
}
