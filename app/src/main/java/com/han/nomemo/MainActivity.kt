package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class MainActivity : BaseComposeActivity() {
    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_QUICK = "QUICK"
        private const val FILTER_LIFE = "LIFE"
        private const val FILTER_WORK = "WORK"
        private const val FILTER_AI = "AI"
        private const val FILTER_ARCHIVED = "ARCHIVED"
    }
    private data class FilterChipCounts(
        val all: Int = 0,
        val quick: Int = 0,
        val life: Int = 0,
        val work: Int = 0,
        val ai: Int = 0,
        val archived: Int = 0
    )

    private lateinit var memoryStore: MemoryStore
    private var selectedFilter by mutableStateOf(FILTER_ALL)
    private var records by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var filterChipCounts by mutableStateOf(FilterChipCounts())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null
    private var hasHandledInitialResume = false

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            refreshRecords()
        }
    }

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                recreate()
            } else {
                refreshRecords()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            MainContent(
                records = records,
                filterChipCounts = filterChipCounts,
                hasLoadedRecords = hasLoadedRecords,
                selectedFilter = selectedFilter,
                onFilterSelect = { filter ->
                    selectedFilter = filter
                    refreshRecords()
                },
                onDeleteRecords = { recordIds -> deleteRecords(recordIds) },
                onArchiveRecords = { selectedRecords -> toggleArchive(selectedRecords) },
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                showAddSheet = showAddSheet,
                onAddClick = { showAddSheet = true },
                onDismissAddSheet = { showAddSheet = false },
                onOpenGroup = { openGroupPage() },
                onOpenReminder = { openReminderPage() },
                onOpenSearch = { openSearchPage() },
                onOpenSettings = { openSettingsPage() }
            )
        }
        refreshRecords()
    }

    override fun onResume() {
        super.onResume()
        if (!hasHandledInitialResume) {
            hasHandledInitialResume = true
            return
        }
        refreshRecords()
    }

    override fun onStart() {
        super.onStart()
        registerMemoryChangeReceiver()
    }

    override fun onStop() {
        unregisterMemoryChangeReceiver()
        super.onStop()
    }

    override fun enableDoubleBackToDesktop(): Boolean = true

    private fun refreshRecords() {
        val filterSnapshot = selectedFilter
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            val payload = withContext(Dispatchers.IO) {
                val allRecords = memoryStore.loadRecords()
                val activeRecords = allRecords.filter { !it.isArchived }
                val archivedRecords = allRecords.filter { it.isArchived }
                val chipCounts = FilterChipCounts(
                    all = activeRecords.size,
                    quick = activeRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_QUICK },
                    life = activeRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_LIFE },
                    work = activeRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_WORK },
                    ai = activeRecords.count { it.mode == MemoryRecord.MODE_AI },
                    archived = archivedRecords.size
                )
                val displayRecords = if (filterSnapshot == FILTER_ARCHIVED) archivedRecords else activeRecords
                prewarmMemoryThumbnailCache(applicationContext, displayRecords)
                Pair(displayRecords, chipCounts)
            }
            if (selectedFilter == filterSnapshot) {
                records = payload.first
                filterChipCounts = payload.second
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

    private fun openGroupPage() {
        switchPrimaryPage(Intent(this, GroupActivity::class.java))
    }

    private fun openReminderPage() {
        switchPrimaryPage(Intent(this, ReminderActivity::class.java))
    }

    private fun openSettingsPage() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }

    private fun openSearchPage() {
        startActivity(SearchActivity.createIntent(this))
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
    }

    private fun toggleArchive(record: MemoryRecord) {
        val nextArchived = !record.isArchived
        memoryStore.archiveRecord(record.recordId, nextArchived)
        refreshRecords()
        Toast.makeText(
            this,
            if (nextArchived) R.string.archive_success else R.string.unarchive_success,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteRecord(record: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(record.recordId)
        if (deleted) {
            refreshRecords()
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleArchive(records: List<MemoryRecord>) {
        if (records.isEmpty()) {
            return
        }
        val nextArchived = records.any { !it.isArchived }
        records.forEach { record ->
            memoryStore.archiveRecord(record.recordId, nextArchived)
        }
        refreshRecords()
        Toast.makeText(
            this,
            if (nextArchived) R.string.archive_success else R.string.unarchive_success,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteRecords(recordIds: Set<String>) {
        if (recordIds.isEmpty()) {
            return
        }
        var deletedAny = false
        recordIds.forEach { recordId ->
            deletedAny = memoryStore.deleteRecord(recordId) || deletedAny
        }
        if (deletedAny) {
            refreshRecords()
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun MainContent(
        records: List<MemoryRecord>,
        filterChipCounts: FilterChipCounts,
        hasLoadedRecords: Boolean,
        selectedFilter: String,
        onFilterSelect: (String) -> Unit,
        onDeleteRecords: (Set<String>) -> Unit,
        onArchiveRecords: (List<MemoryRecord>) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit,
        onOpenGroup: () -> Unit,
        onOpenReminder: () -> Unit,
        onOpenSearch: () -> Unit,
        onOpenSettings: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var selectedRecordIds by remember { mutableStateOf(setOf<String>()) }
        var selectionModeActive by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var moreMenuAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var pendingScrollToTopAfterAdd by remember { mutableStateOf(false) }
        var selectedSecondaryByPrimary by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
        var expandedPrimaryFilter by remember { mutableStateOf<String?>(null) }

        val selectedRecords = remember(records, selectedRecordIds) {
            records.filter { selectedRecordIds.contains(it.recordId) }
        }
        val listState = rememberLazyListState()
        val dockHasUnderContent = rememberDockHasUnderContent(
            listState = listState,
            spec = adaptive
        )
        val density = LocalDensity.current
        val secondaryCategories = remember(selectedFilter) {
            getSecondaryCategoriesForPrimary(selectedFilter)
        }
        val selectedSecondaryCode = selectedSecondaryByPrimary[selectedFilter]
        val useSecondaryFilter =
            isPrimaryCategoryFilter(selectedFilter) &&
                expandedPrimaryFilter == selectedFilter &&
                secondaryCategories.isNotEmpty()
        val showSecondaryCategoryChips = !selectionModeActive && useSecondaryFilter
        val categoryCountMap = remember(records) {
            records.groupingBy { it.categoryCode }.eachCount()
        }
        LaunchedEffect(selectedFilter, selectedSecondaryCode, secondaryCategories, records) {
            if (!isPrimaryCategoryFilter(selectedFilter) || secondaryCategories.isEmpty()) {
                return@LaunchedEffect
            }
            val hasValidSelection = selectedSecondaryCode != null &&
                secondaryCategories.any { it.categoryCode == selectedSecondaryCode }
            if (hasValidSelection) {
                return@LaunchedEffect
            }
            val preferredCode = secondaryCategories
                .maxByOrNull { option ->
                    records.count { record -> record.categoryCode == option.categoryCode }
                }
                ?.categoryCode
                ?: secondaryCategories.first().categoryCode
            selectedSecondaryByPrimary = selectedSecondaryByPrimary + (selectedFilter to preferredCode)
        }
        LaunchedEffect(selectedFilter) {
            if (!isPrimaryCategoryFilter(selectedFilter)) {
                expandedPrimaryFilter = null
            }
        }
        val filteredRecords = remember(records, selectedFilter, selectedSecondaryCode, useSecondaryFilter) {
            records.filter { record ->
                val matchesPrimaryFilter = when (selectedFilter) {
                    FILTER_QUICK -> record.categoryGroupCode == CategoryCatalog.GROUP_QUICK
                    FILTER_LIFE -> record.categoryGroupCode == CategoryCatalog.GROUP_LIFE
                    FILTER_WORK -> record.categoryGroupCode == CategoryCatalog.GROUP_WORK
                    FILTER_AI -> record.mode == MemoryRecord.MODE_AI
                    else -> true
                }
                val matchesSecondaryFilter = if (useSecondaryFilter) {
                    selectedSecondaryCode?.let { record.categoryCode == it } ?: true
                } else {
                    true
                }
                matchesPrimaryFilter && matchesSecondaryFilter
            }
        }
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
            label = "memoryHeaderCollapseProgress"
        )
        val expandedTitleAlpha = (1f - headerCollapseProgress).coerceIn(0f, 1f)
        val collapsedTitleAlpha = headerCollapseProgress.coerceIn(0f, 1f)
        val expandedTitleTranslateY = with(density) { (-22).dp.toPx() * headerCollapseProgress }
        val recordSpacing = 14.dp
        val chipBottomPadding = 12.dp
        val listTopPadding = (recordSpacing - chipBottomPadding).coerceAtLeast(0.dp)
        val expandedTitleMaxHeight = if (adaptive.isNarrow) 44.dp else 52.dp
        val expandedTitleHeight = lerp(expandedTitleMaxHeight, 0.dp, headerCollapseProgress)
        val chipsTopPadding = lerp(12.dp, 11.dp, headerCollapseProgress)
        val showCenteredEmptyState = hasLoadedRecords && filteredRecords.isEmpty()
        val allVisibleRecordsSelected = filteredRecords.isNotEmpty() &&
            filteredRecords.all { selectedRecordIds.contains(it.recordId) }
        val recordItems: LazyListScope.() -> Unit = {
            items(
                items = filteredRecords,
                key = { it.recordId },
                contentType = {
                    if (it.imageUri.isNullOrBlank()) "record_plain" else "record_image"
                }
            ) { record ->
                val selected = selectedRecordIds.contains(record.recordId)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    RecordCard(
                        record = record,
                        selected = false,
                        palette = palette,
                        adaptive = adaptive,
                        allowImageLoading = true,
                        showShadow = false,
                        darkCardBackgroundOverride = Color(0xFF1A1A1C),
                        onClick = {
                            when {
                                selectionModeActive && selected -> {
                                    selectedRecordIds = selectedRecordIds - record.recordId
                                }
                                selectionModeActive -> {
                                    selectedRecordIds = selectedRecordIds + record.recordId
                                }
                                else -> {
                                    onOpenDetail(record)
                                }
                            }
                        },
                        onLongPress = {
                            moreMenuExpanded = false
                            selectionModeActive = true
                            selectedRecordIds = if (selected) {
                                selectedRecordIds - record.recordId
                            } else {
                                selectedRecordIds + record.recordId
                            }
                        }
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 12.dp, end = 12.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(palette.accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_sheet_check),
                                contentDescription = null,
                                tint = palette.onAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

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
                        if (selectionModeActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 12.dp),
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
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spec.topActionButtonSize)
                                    .padding(top = 2.dp)
                            ) {
                                NoMemoTopActionButtons(
                                    spec = spec,
                                    onSearchClick = onOpenSearch,
                                    onMoreClick = { moreMenuExpanded = !moreMenuExpanded },
                                    onMoreButtonBoundsChanged = { moreMenuAnchorBounds = it },
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                                Text(
                                    text = stringResource(R.string.page_title),
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
                                    .height(expandedTitleHeight)
                                    .padding(top = 2.dp)
                                    .graphicsLayer {
                                        alpha = expandedTitleAlpha
                                        translationY = expandedTitleTranslateY
                                    }
                            ) {
                                Text(
                                    text = stringResource(R.string.page_title),
                                    color = palette.textPrimary,
                                    fontSize = spec.titleSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!selectionModeActive) {
                            Column(
                                modifier = Modifier
                                    .padding(top = chipsTopPadding, bottom = chipBottomPadding)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    FilterChip(
                                        spec = spec,
                                        text = buildFilterChipText(stringResource(R.string.filter_all), filterChipCounts.all),
                                        selected = selectedFilter == FILTER_ALL
                                    ) {
                                        expandedPrimaryFilter = null
                                        onFilterSelect(FILTER_ALL)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    FilterChip(
                                        spec = spec,
                                        text = buildFilterChipText(stringResource(R.string.filter_quick), filterChipCounts.quick),
                                        selected = selectedFilter == FILTER_QUICK
                                    ) {
                                        expandedPrimaryFilter = null
                                        onFilterSelect(FILTER_QUICK)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    PrimaryFilterChip(
                                        spec = spec,
                                        text = buildFilterChipText(stringResource(R.string.filter_life), filterChipCounts.life),
                                        selected = selectedFilter == FILTER_LIFE,
                                        showExpandIndicator = selectedFilter == FILTER_LIFE,
                                        expanded = selectedFilter == FILTER_LIFE && expandedPrimaryFilter == FILTER_LIFE
                                    ) {
                                        val wasSelected = selectedFilter == FILTER_LIFE
                                        val wasExpanded = expandedPrimaryFilter == FILTER_LIFE
                                        if (selectedSecondaryByPrimary[FILTER_LIFE] == null) {
                                            val lifeDefault = CategoryCatalog.getLifeCategories()
                                                .firstOrNull()
                                                ?.categoryCode
                                            if (lifeDefault != null) {
                                                selectedSecondaryByPrimary =
                                                    selectedSecondaryByPrimary + (FILTER_LIFE to lifeDefault)
                                            }
                                        }
                                        expandedPrimaryFilter = if (wasSelected && wasExpanded) null else FILTER_LIFE
                                        onFilterSelect(FILTER_LIFE)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    PrimaryFilterChip(
                                        spec = spec,
                                        text = buildFilterChipText(stringResource(R.string.filter_work), filterChipCounts.work),
                                        selected = selectedFilter == FILTER_WORK,
                                        showExpandIndicator = selectedFilter == FILTER_WORK,
                                        expanded = selectedFilter == FILTER_WORK && expandedPrimaryFilter == FILTER_WORK
                                    ) {
                                        val wasSelected = selectedFilter == FILTER_WORK
                                        val wasExpanded = expandedPrimaryFilter == FILTER_WORK
                                        if (selectedSecondaryByPrimary[FILTER_WORK] == null) {
                                            val workDefault = CategoryCatalog.getWorkCategories()
                                                .firstOrNull()
                                                ?.categoryCode
                                            if (workDefault != null) {
                                                selectedSecondaryByPrimary =
                                                    selectedSecondaryByPrimary + (FILTER_WORK to workDefault)
                                            }
                                        }
                                        expandedPrimaryFilter = if (wasSelected && wasExpanded) null else FILTER_WORK
                                        onFilterSelect(FILTER_WORK)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    FilterChip(
                                        spec = spec,
                                        text = buildFilterChipText(stringResource(R.string.filter_ai), filterChipCounts.ai),
                                        selected = selectedFilter == FILTER_AI
                                    ) {
                                        expandedPrimaryFilter = null
                                        onFilterSelect(FILTER_AI)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    FilterChip(
                                        spec = spec,
                                        text = buildFilterChipText(stringResource(R.string.filter_archived), filterChipCounts.archived),
                                        selected = selectedFilter == FILTER_ARCHIVED
                                    ) {
                                        expandedPrimaryFilter = null
                                        onFilterSelect(FILTER_ARCHIVED)
                                    }
                                }

                                AnimatedVisibility(
                                    visible = showSecondaryCategoryChips,
                                    enter = expandVertically(
                                        expandFrom = Alignment.Top,
                                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                    ) + fadeIn(
                                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                    ),
                                    exit = shrinkVertically(
                                        shrinkTowards = Alignment.Top,
                                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                                    ) + fadeOut(
                                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                                    )
                                ) {
                                    Crossfade(
                                        targetState = selectedFilter,
                                        animationSpec = tween(
                                            durationMillis = 180,
                                            easing = FastOutSlowInEasing
                                        ),
                                        label = "secondaryCategoryCrossfade"
                                    ) { currentPrimary ->
                                        val currentSecondaryCategories = getSecondaryCategoriesForPrimary(currentPrimary)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp)
                                                .horizontalScroll(rememberScrollState())
                                        ) {
                                            currentSecondaryCategories.forEachIndexed { index, option ->
                                                SecondaryFilterChip(
                                                    spec = spec,
                                                    categoryCode = option.categoryCode,
                                                    text = buildSecondaryFilterChipText(
                                                        option.categoryName,
                                                        categoryCountMap[option.categoryCode] ?: 0
                                                    ),
                                                    selected = selectedSecondaryCode == option.categoryCode,
                                                    onClick = {
                                                        selectedSecondaryByPrimary =
                                                            selectedSecondaryByPrimary + (selectedFilter to option.categoryCode)
                                                    }
                                                )
                                                if (index < currentSecondaryCategories.lastIndex) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!hasLoadedRecords || filteredRecords.isEmpty()) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    state = listState,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        top = listTopPadding,
                                        bottom = if (selectedRecords.isNotEmpty()) 20.dp else spec.pageBottomPadding + 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(recordSpacing),
                                    content = recordItems
                                )
                            }
                        }

                    }

                    if (showCenteredEmptyState) {
                        NoMemoEmptyState(
                            iconRes = R.drawable.ic_nm_memory,
                            title = if (selectedFilter == FILTER_ARCHIVED) {
                                stringResource(R.string.no_archived_records)
                            } else {
                                stringResource(R.string.no_records)
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = spec.pageHorizontalPadding)
                        )
                    }

                    if (!selectionModeActive) {
                        NoMemoBottomDock(
                            selectedTab = NoMemoDockTab.MEMORY,
                            spec = spec,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(
                                    start = spec.pageHorizontalPadding,
                                    end = spec.pageHorizontalPadding,
                                    bottom = if (spec.isNarrow) 10.dp else 14.dp
                                ),
                            onOpenMemory = {},
                            onOpenGroup = onOpenGroup,
                            onOpenReminder = onOpenReminder,
                            onAddClick = onAddClick,
                            animateFabHalo = !listState.isScrollInProgress,
                            showEnhancedOutline = dockHasUnderContent
                        )
                    } else if (selectedRecords.isNotEmpty()) {
                        NoMemoSelectionActionDock(
                            selectedRecords = selectedRecords,
                            onArchiveClick = {
                                onArchiveRecords(selectedRecords)
                                selectionModeActive = false
                                selectedRecordIds = emptySet()
                            },
                            onDeleteClick = { showDeleteConfirm = true },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(
                                    start = spec.pageHorizontalPadding,
                                    end = spec.pageHorizontalPadding,
                                    bottom = if (spec.isNarrow) 10.dp else 14.dp
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
                    NoMemoMenuPopup(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                        anchorBounds = moreMenuAnchorBounds
                    ) {
                        NoMemoMoreMenuPanel(
                            onSelectAll = {
                                moreMenuExpanded = false
                                showDeleteConfirm = false
                                if (filteredRecords.isNotEmpty()) {
                                    selectionModeActive = true
                                    selectedRecordIds = filteredRecords.map { it.recordId }.toSet()
                                } else {
                                    selectionModeActive = false
                                    selectedRecordIds = emptySet()
                                }
                            },
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
    private fun FilterChip(
        spec: NoMemoAdaptiveSpec,
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        GlassChip(
            text = text,
            selected = selected,
            onClick = onClick,
            horizontalPadding = if (spec.isNarrow) 18.dp else 24.dp,
            verticalPadding = if (spec.isNarrow) 11.dp else 12.dp,
            showBorder = false,
            textStyle = TextStyle(
                fontSize = (spec.chipTextSize.value + 1f).sp,
                fontWeight = FontWeight.Bold
            )
        )
    }

    @Composable
    private fun PrimaryFilterChip(
        spec: NoMemoAdaptiveSpec,
        text: String,
        selected: Boolean,
        showExpandIndicator: Boolean,
        expanded: Boolean,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = androidx.compose.foundation.isSystemInDarkTheme()
        val bg = if (selected) palette.accent else if (isDark) palette.glassFill else Color.White
        val textColor = if (selected) palette.onAccent else palette.textPrimary
        val chevronRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            label = "primaryFilterChevronRotation"
        )
        PressScaleBox(onClick = onClick) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(bg)
                    .padding(
                        start = if (spec.isNarrow) 18.dp else 24.dp,
                        end = if (spec.isNarrow) 14.dp else 20.dp,
                        top = if (spec.isNarrow) 11.dp else 12.dp,
                        bottom = if (spec.isNarrow) 11.dp else 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    color = textColor,
                    style = TextStyle(
                        fontSize = (spec.chipTextSize.value + 1f).sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (showExpandIndicator) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sheet_chevron_down),
                        contentDescription = null,
                        tint = textColor.copy(alpha = 0.9f),
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(12.dp)
                            .graphicsLayer {
                                rotationZ = chevronRotation
                            }
                    )
                }
            }
        }
    }

    @Composable
    private fun SecondaryFilterChip(
        spec: NoMemoAdaptiveSpec,
        categoryCode: String,
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = androidx.compose.foundation.isSystemInDarkTheme()
        val accentColor = secondaryCategoryAccentColor(categoryCode)
        val bg = if (selected) {
            accentColor.copy(alpha = if (isDark) 0.34f else 0.18f)
        } else if (isDark) {
            palette.glassFill
        } else {
            Color.White
        }
        val textColor = if (selected) {
            if (isDark) Color.White else accentColor
        } else {
            palette.textPrimary
        }
        val iconContainer = if (selected) {
            accentColor.copy(alpha = if (isDark) 0.24f else 0.14f)
        } else {
            accentColor.copy(alpha = if (isDark) 0.22f else 0.16f)
        }

        PressScaleBox(onClick = onClick) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(bg)
                    .padding(
                        start = if (spec.isNarrow) 10.dp else 12.dp,
                        end = if (spec.isNarrow) 14.dp else 18.dp,
                        top = if (spec.isNarrow) 8.dp else 9.dp,
                        bottom = if (spec.isNarrow) 8.dp else 9.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(iconContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = secondaryCategoryIcon(categoryCode),
                        contentDescription = null,
                        tint = if (selected) textColor else accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = text,
                    color = textColor,
                    style = TextStyle(
                        fontSize = spec.chipTextSize,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }

    private fun secondaryCategoryIcon(categoryCode: String): ImageVector {
        return when (categoryCode) {
            CategoryCatalog.CODE_LIFE_PICKUP -> Icons.Outlined.Restaurant
            CategoryCatalog.CODE_LIFE_DELIVERY -> Icons.Outlined.LocalShipping
            CategoryCatalog.CODE_LIFE_CARD -> Icons.Outlined.Badge
            CategoryCatalog.CODE_LIFE_TICKET -> Icons.Outlined.ConfirmationNumber
            CategoryCatalog.CODE_WORK_TODO -> Icons.Outlined.AssignmentTurnedIn
            CategoryCatalog.CODE_WORK_SCHEDULE -> Icons.Outlined.CalendarMonth
            else -> Icons.Outlined.Badge
        }
    }

    private fun secondaryCategoryAccentColor(categoryCode: String): Color {
        return when (categoryCode) {
            CategoryCatalog.CODE_LIFE_PICKUP -> Color(0xFFFFA157)
            CategoryCatalog.CODE_LIFE_DELIVERY -> Color(0xFF69A7FF)
            CategoryCatalog.CODE_LIFE_CARD -> Color(0xFFD2B37C)
            CategoryCatalog.CODE_LIFE_TICKET -> Color(0xFF9C7CFF)
            CategoryCatalog.CODE_WORK_TODO -> Color(0xFF58D89A)
            CategoryCatalog.CODE_WORK_SCHEDULE -> Color(0xFF4F8CFF)
            else -> Color(0xFFB0B0B5)
        }
    }

    private fun isPrimaryCategoryFilter(filter: String): Boolean {
        return filter == FILTER_LIFE || filter == FILTER_WORK
    }

    private fun getSecondaryCategoriesForPrimary(filter: String): List<CategoryCatalog.CategoryOption> {
        return when (filter) {
            FILTER_LIFE -> CategoryCatalog.getLifeCategories()
            FILTER_WORK -> CategoryCatalog.getWorkCategories()
            else -> emptyList()
        }
    }

    private fun buildFilterChipText(label: String, count: Int): String = "$label $count"
    private fun buildSecondaryFilterChipText(label: String, count: Int): String = "$label $count"

    @Composable
    private fun SearchBarCard(
        value: String,
        onValueChange: (String) -> Unit,
        onClose: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = palette.glassFill),
            border = androidx.compose.foundation.BorderStroke(1.dp, palette.glassStroke)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_nm_search),
                    contentDescription = stringResource(R.string.action_search),
                    tint = palette.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        color = palette.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(min = 0.dp)
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = palette.textTertiary,
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
                TextButton(onClick = onClose) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        }
    }

}






