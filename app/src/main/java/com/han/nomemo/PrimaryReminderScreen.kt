package com.han.nomemo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
internal fun ReminderPrimaryScreenRoute(
    backdrop: LayerBackdrop,
    isActive: Boolean,
    onPrimaryDockStateChanged: (Boolean, (() -> Unit)?) -> Unit,
    onPrimaryOverlayChanged: (PrimaryHostOverlay?) -> Unit,
    onOpenDetail: (MemoryRecord) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? BaseComposeActivity
    val scope = rememberCoroutineScope()
    val memoryStore = remember(context) { MemoryStore(context) }
    var selectedFilter by remember { mutableStateOf(PrimaryReminderFilter.ALL) }
    var reminderRecords by remember { mutableStateOf<List<MemoryRecord>>(emptyList()) }
    var hasLoadedRecords by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var refreshJob by remember { mutableStateOf<Job?>(null) }

    fun refreshReminders() {
        val filterSnapshot = selectedFilter
        refreshJob?.cancel()
        refreshJob = scope.launch {
            val filtered = withContext(Dispatchers.IO) {
                val all = memoryStore.loadReminderRecords()
                val result = all.filter { record ->
                    when (filterSnapshot) {
                        PrimaryReminderFilter.PENDING -> !record.isReminderDone
                        PrimaryReminderFilter.DONE -> record.isReminderDone
                        PrimaryReminderFilter.ALL -> true
                    }
                }
                prewarmMemoryThumbnailCache(context.applicationContext, result)
                result
            }
            if (selectedFilter == filterSnapshot) {
                reminderRecords = filtered
                hasLoadedRecords = true
            }
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            activity?.recreate()
        } else {
            refreshReminders()
        }
    }

    DisposableEffect(activity, isActive) {
        if (activity == null || !isActive) {
            onDispose { }
        } else {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: android.content.Context?, intent: Intent?) {
                    refreshReminders()
                }
            }
            ContextCompat.registerReceiver(
                activity,
                receiver,
                IntentFilter(MemoryStoreNotifier.ACTION_RECORDS_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose {
                activity.unregisterReceiver(receiver)
            }
        }
    }

    DisposableEffect(activity, isActive) {
        if (activity == null || !isActive) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    refreshReminders()
                }
            }
            activity.lifecycle.addObserver(observer)
            onDispose {
                activity.lifecycle.removeObserver(observer)
            }
        }
    }

    LaunchedEffect(isActive, selectedFilter) {
        if (isActive) {
            refreshReminders()
        }
    }

    ReminderPrimaryScreen(
        records = reminderRecords,
        hasLoadedRecords = hasLoadedRecords,
        selectedFilter = selectedFilter,
        onDeleteRecords = { recordIds ->
            if (recordIds.isEmpty()) return@ReminderPrimaryScreen
            var deletedCount = 0
            recordIds.forEach { recordId ->
                if (memoryStore.deleteRecord(recordId)) {
                    deletedCount += 1
                }
            }
            if (deletedCount > 0) {
                refreshReminders()
                Toast.makeText(context, R.string.reminder_delete_success, Toast.LENGTH_SHORT).show()
            }
        },
        onFilterSelected = { filter ->
            selectedFilter = filter
        },
        onDoneChanged = { record, done ->
            memoryStore.updateReminderDone(record.recordId, done)
            refreshReminders()
            Toast.makeText(context, R.string.reminder_done_success, Toast.LENGTH_SHORT).show()
        },
        onOpenDetail = onOpenDetail,
        onOpenSearch = onOpenSearch,
        onOpenSettings = {
            settingsLauncher.launch(Intent(context, SettingsActivity::class.java))
        },
        onOpenArchivedMemory = {
            context.startActivity(ArchivedMemoryActivity.createIntent(context))
        },
        showAddSheet = showAddSheet,
        onDismissAddSheet = { showAddSheet = false },
        onAddClick = { showAddSheet = true },
        backdrop = backdrop,
        isActive = isActive,
        onPrimaryDockStateChanged = onPrimaryDockStateChanged,
        onPrimaryOverlayChanged = onPrimaryOverlayChanged,
        onResetDoubleBackExitState = { activity?.clearDoubleBackExitState() }
    )
}

private enum class PrimaryReminderFilter {
    ALL,
    PENDING,
    DONE
}

@Composable
private fun ReminderPrimaryScreen(
    records: List<MemoryRecord>,
    hasLoadedRecords: Boolean,
    selectedFilter: PrimaryReminderFilter,
    onDeleteRecords: (Set<String>) -> Unit,
    onFilterSelected: (PrimaryReminderFilter) -> Unit,
    onDoneChanged: (MemoryRecord, Boolean) -> Unit,
    onOpenDetail: (MemoryRecord) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenArchivedMemory: () -> Unit,
    showAddSheet: Boolean,
    onDismissAddSheet: () -> Unit,
    onAddClick: () -> Unit,
    backdrop: LayerBackdrop,
    isActive: Boolean,
    onPrimaryDockStateChanged: (Boolean, (() -> Unit)?) -> Unit,
    onPrimaryOverlayChanged: (PrimaryHostOverlay?) -> Unit,
    onResetDoubleBackExitState: () -> Unit
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
    val headerCollapseTarget by remember(
        selectionModeActive,
        listState.firstVisibleItemIndex,
        listState.firstVisibleItemScrollOffset
    ) {
        derivedStateOf {
            if (selectionModeActive) {
                0f
            } else {
                when {
                    listState.firstVisibleItemIndex > 0 -> 1f
                    headerCollapseDistancePx <= 0f -> 0f
                    else -> (listState.firstVisibleItemScrollOffset / headerCollapseDistancePx)
                        .coerceIn(0f, 1f)
                }
            }
        }
    }
    val headerCollapseProgress by animateFloatAsState(
        targetValue = headerCollapseTarget,
        animationSpec = tween(durationMillis = 110, easing = FastOutSlowInEasing),
        label = "primaryReminderHeaderCollapseProgress"
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

    LaunchedEffect(isActive, selectionModeActive, onAddClick) {
        if (isActive) {
            onPrimaryDockStateChanged(!selectionModeActive, onAddClick)
        }
    }
    LaunchedEffect(isActive, showAddSheet, onDismissAddSheet, pendingScrollToTopAfterAdd) {
        if (isActive) {
            val overlay = if (showAddSheet) {
                PrimaryHostOverlay.AddMemory(
                    onDismiss = onDismissAddSheet,
                    onSaved = { pendingScrollToTopAfterAdd = true }
                )
            } else {
                null
            }
            onPrimaryOverlayChanged(overlay)
        }
    }

    BackHandler(enabled = selectionModeActive) {
        selectionModeActive = false
        selectedRecordIds = emptySet()
        showDeleteConfirm = false
        onResetDoubleBackExitState()
    }

    NoMemoBackground {
        ResponsiveContentFrame(spec = adaptive) { spec ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .layerBackdrop(backdrop)
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
                                .height(spec.topActionButtonSize)
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
                                text = stringResource(
                                    R.string.selected_count_format,
                                    selectedRecords.size
                                ),
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
                                contentDescription = if (allVisibleRecordsSelected) {
                                    "取消全选"
                                } else {
                                    "全选"
                                },
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
                        ReminderPrimaryChip(
                            text = stringResource(R.string.reminder_filter_all),
                            selected = selectedFilter == PrimaryReminderFilter.ALL,
                            chipTextSize = spec.chipTextSize
                        ) {
                            onFilterSelected(PrimaryReminderFilter.ALL)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        ReminderPrimaryChip(
                            text = stringResource(R.string.reminder_filter_pending),
                            selected = selectedFilter == PrimaryReminderFilter.PENDING,
                            chipTextSize = spec.chipTextSize
                        ) {
                            onFilterSelected(PrimaryReminderFilter.PENDING)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        ReminderPrimaryChip(
                            text = stringResource(R.string.reminder_filter_done),
                            selected = selectedFilter == PrimaryReminderFilter.DONE,
                            chipTextSize = spec.chipTextSize
                        ) {
                            onFilterSelected(PrimaryReminderFilter.DONE)
                        }
                    }

                    if (!hasLoadedRecords || filteredRecords.isEmpty()) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = listState,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                top = 2.dp,
                                bottom = if (selectedRecords.isNotEmpty()) {
                                    spec.pageBottomPadding + if (spec.isNarrow) 18.dp else 22.dp
                                } else {
                                    spec.pageBottomPadding + 20.dp
                                }
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                if (spec.widthClass == NoMemoWidthClass.EXPANDED) 12.dp else 10.dp
                            )
                        ) {
                            items(
                                items = filteredRecords,
                                key = { it.recordId },
                                contentType = { "reminder" }
                            ) { record ->
                                ReminderPrimaryItem(
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
                                            selectionModeActive &&
                                                selectedRecordIds.contains(record.recordId) -> {
                                                selectedRecordIds = selectedRecordIds - record.recordId
                                            }

                                            selectionModeActive -> {
                                                selectedRecordIds = selectedRecordIds + record.recordId
                                            }

                                            else -> onOpenDetail(record)
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

                if (selectionModeActive && selectedRecords.isNotEmpty()) {
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
                        message = stringResource(
                            R.string.delete_selected_batch_message,
                            selectedRecordIds.size
                        ),
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
                            label = stringResource(R.string.archived_memory_page_title),
                            onClick = {
                                moreMenuExpanded = false
                                onOpenArchivedMemory()
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

            }
        }
    }
}

@Composable
private fun ReminderPrimaryChip(
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
private fun ReminderPrimaryItem(
    record: MemoryRecord,
    selected: Boolean,
    adaptive: NoMemoAdaptiveSpec,
    palette: NoMemoPalette,
    onDoneChanged: (MemoryRecord, Boolean) -> Unit,
    onLongPressSelect: () -> Unit,
    onClickItem: () -> Unit
) {
    val context = LocalContext.current
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
        else -> context.getString(R.string.reminder_default_title)
    }
    val hasReminderTime = record.reminderAt > 0L
    val time = if (hasReminderTime) record.reminderAt else record.createdAt
    val meta = "${record.categoryName} | ${dateFormat.format(Date(time))}"
    val countdown = if (hasReminderTime) {
        context.buildReminderCountdownLabel(time)
    } else {
        context.getString(R.string.reminder_not_set)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(cardBackground)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else palette.glassStroke.copy(alpha = 0.08f),
                shape = cardShape
            )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
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
                    textDecoration = if (record.isReminderDone) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
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
                        color = if (!hasReminderTime || time >= System.currentTimeMillis()) {
                            palette.accent
                        } else {
                            Color(0xFFB42318)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

private fun android.content.Context.buildReminderCountdownLabel(targetTimeMillis: Long): String {
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
