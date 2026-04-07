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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
        memoryStore = MemoryStore(this)
        setContent {
            ReminderContent(
                records = reminderRecords,
                hasLoadedRecords = hasLoadedRecords,
                selectedFilter = selectedFilter,
                onDeleteRecord = { record -> deleteRecord(record) },
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
                onDismissAddSheet = { showAddSheet = false }
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
        switchPrimaryPage(intent)
    }

    private fun openGroupPage() {
        switchPrimaryPage(Intent(this, GroupActivity::class.java))
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

    private fun deleteRecord(record: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(record.recordId)
        if (deleted) {
            refreshReminders()
            Toast.makeText(this, R.string.reminder_delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun ReminderContent(
        records: List<MemoryRecord>,
        hasLoadedRecords: Boolean,
        selectedFilter: String,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onFilterSelected: (String) -> Unit,
        onDoneChanged: (MemoryRecord, Boolean) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenGroup: () -> Unit,
        onOpenSearch: () -> Unit,
        onOpenSettings: () -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var selectedRecordId by remember { mutableStateOf<String?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var moreMenuAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var pendingScrollToTopAfterAdd by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val dockHasUnderContent = rememberDockHasUnderContent(
            listState = listState,
            spec = adaptive
        )
        val filteredRecords = records
        val selectedRecord = remember(filteredRecords, selectedRecordId) {
            filteredRecords.firstOrNull { it.recordId == selectedRecordId }
        }
        val showCenteredEmptyState = hasLoadedRecords && filteredRecords.isEmpty()
        LaunchedEffect(filteredRecords, selectedRecordId) {
            if (selectedRecordId != null && selectedRecord == null) {
                selectedRecordId = null
                showDeleteConfirm = false
            }
        }
        LaunchedEffect(showAddSheet, pendingScrollToTopAfterAdd) {
            if (!showAddSheet && pendingScrollToTopAfterAdd) {
                listState.animateScrollToItem(0)
                pendingScrollToTopAfterAdd = false
            }
        }
        BackHandler(enabled = selectedRecordId != null) {
            selectedRecordId = null
            showDeleteConfirm = false
            resetDoubleBackExitState()
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                end = spec.pageHorizontalPadding,
                                bottom = 0.dp
                            )
                    ) {
                        if (selectedRecord != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = getString(R.string.selected_count_format, 1),
                                        color = palette.textPrimary,
                                        fontSize = spec.titleSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_close,
                                    contentDescription = stringResource(R.string.cancel),
                                    onClick = {
                                        selectedRecordId = null
                                        showDeleteConfirm = false
                                    },
                                    modifier = Modifier.padding(end = 10.dp),
                                    size = spec.topActionButtonSize
                                )
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_nm_delete,
                                    contentDescription = stringResource(R.string.action_delete),
                                    onClick = { showDeleteConfirm = true },
                                    size = spec.topActionButtonSize
                                )
                            }
                        } else {
                            NoMemoTopActionButtons(
                                spec = spec,
                                onSearchClick = onOpenSearch,
                                onMoreClick = { moreMenuExpanded = !moreMenuExpanded },
                                onMoreButtonBoundsChanged = { moreMenuAnchorBounds = it }
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
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
                                .padding(top = 14.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            ReminderChip(stringResource(R.string.reminder_filter_all), selectedFilter == FILTER_ALL, spec.chipTextSize) {
                                onFilterSelected(FILTER_ALL)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ReminderChip(stringResource(R.string.reminder_filter_pending), selectedFilter == FILTER_PENDING, spec.chipTextSize) {
                                onFilterSelected(FILTER_PENDING)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
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
                                    top = if (spec.widthClass == NoMemoWidthClass.EXPANDED) 12.dp else 10.dp,
                                    bottom = spec.pageBottomPadding + 20.dp
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
                                        selected = selectedRecordId == record.recordId,
                                        adaptive = adaptive,
                                        palette = palette,
                                        onDoneChanged = onDoneChanged,
                                        onLongPressSelect = {
                                            moreMenuExpanded = false
                                            selectedRecordId = record.recordId
                                        },
                                        onClickItem = {
                                            when {
                                                selectedRecordId == record.recordId -> {
                                                    selectedRecordId = null
                                                }
                                                selectedRecordId != null -> {
                                                    selectedRecordId = record.recordId
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

                    NoMemoBottomDock(
                        selectedTab = NoMemoDockTab.REMINDER,
                        onOpenMemory = onOpenMemory,
                        onOpenGroup = onOpenGroup,
                        onOpenReminder = {},
                        onAddClick = onAddClick,
                        spec = spec,
                        animateFabHalo = !listState.isScrollInProgress,
                        showEnhancedOutline = dockHasUnderContent,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                end = spec.pageHorizontalPadding,
                                bottom = if (spec.isNarrow) 10.dp else 14.dp
                            )
                    )

                    if (showDeleteConfirm && selectedRecord != null) {
                        NoMemoDeleteConfirmDialog(
                            title = stringResource(R.string.delete_selected_title),
                            message = stringResource(R.string.delete_selected_message),
                            onConfirm = {
                                onDeleteRecord(selectedRecord)
                                selectedRecordId = null
                                showDeleteConfirm = false
                            },
                            onDismiss = { showDeleteConfirm = false }
                        )
                    }

                    NoMemoMenuPopup(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                        anchorBounds = moreMenuAnchorBounds
                    ) {
                        NoMemoMoreMenuPanel(
                            onOpenSettings = {
                                moreMenuExpanded = false
                                onOpenSettings()
                            }
                        )
                    }

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
        GlassChip(
            text = text,
            selected = selected,
            onClick = onClick,
            showBorder = false,
            textStyle = TextStyle(
                fontSize = chipTextSize,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            horizontalPadding = 16.dp
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
        val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
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
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (selected) {
                        if (isSystemInDarkTheme()) noMemoCardSurfaceColor(true) else palette.glassFillSoft
                    } else {
                        if (isSystemInDarkTheme()) noMemoCardSurfaceColor(true) else palette.glassFill
                    }
                )
                .border(
                    if (selected) 2.dp else 0.dp,
                    if (selected) palette.accent else palette.glassStroke,
                    RoundedCornerShape(20.dp)
                )
                .pointerInput(onLongPressSelect, onClickItem) {
                    detectTapGestures(
                        onTap = { onClickItem() },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onLongPressSelect()
                        }
                    )
                }
                .padding(if (adaptive.isNarrow) 10.dp else 12.dp),
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


