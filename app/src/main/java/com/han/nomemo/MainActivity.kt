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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseComposeActivity() {
    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_QUICK = "QUICK"
        private const val FILTER_LIFE = "LIFE"
        private const val FILTER_WORK = "WORK"
        private const val FILTER_AI = "AI"
        private const val FILTER_ARCHIVED = "ARCHIVED"
    }

    private lateinit var memoryStore: MemoryStore
    private var selectedFilter by mutableStateOf(FILTER_ALL)
    private var records by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null

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
                onOpenSettings = { openSettingsPage() }
            )
        }
        refreshRecords()
    }

    override fun onResume() {
        super.onResume()
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
            val loadedRecords = withContext(Dispatchers.IO) {
                if (filterSnapshot == FILTER_ARCHIVED) {
                    memoryStore.loadArchivedRecords()
                } else {
                    memoryStore.loadActiveRecords()
                }
            }
            if (selectedFilter == filterSnapshot) {
                records = loadedRecords
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
        onOpenSettings: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var selectedRecordIds by remember { mutableStateOf(setOf<String>()) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var searchEnabled by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var moreMenuExpanded by remember { mutableStateOf(false) }

        val selectedRecords = remember(records, selectedRecordIds) {
            records.filter { selectedRecordIds.contains(it.recordId) }
        }
        val listState = rememberLazyListState()
        val dockHasUnderContent = rememberDockHasUnderContent(
            listState = listState,
            spec = adaptive
        )
        val filteredRecords = remember(records, selectedFilter, searchQuery) {
            records.filter { record ->
                val matchesFilter = when (selectedFilter) {
                    FILTER_QUICK -> record.categoryGroupCode == CategoryCatalog.GROUP_QUICK
                    FILTER_LIFE -> record.categoryGroupCode == CategoryCatalog.GROUP_LIFE
                    FILTER_WORK -> record.categoryGroupCode == CategoryCatalog.GROUP_WORK
                    FILTER_AI -> record.mode == MemoryRecord.MODE_AI
                    else -> true
                }
                if (!matchesFilter) {
                    return@filter false
                }
                val query = searchQuery.trim()
                if (query.isBlank()) {
                    return@filter true
                }
                val haystack = listOf(
                    record.title,
                    record.summary,
                    record.memory,
                    record.sourceText,
                    record.analysis,
                    record.categoryName
                ).joinToString("\n") { it.orEmpty() }.lowercase()
                haystack.contains(query.lowercase())
            }
        }
        val allFilteredSelected = remember(filteredRecords, selectedRecordIds) {
            filteredRecords.isNotEmpty() &&
                filteredRecords.all { selectedRecordIds.contains(it.recordId) }
        }
        val showCenteredEmptyState = hasLoadedRecords && filteredRecords.isEmpty()
        val recordItems: LazyListScope.() -> Unit = {
            items(
                items = filteredRecords,
                key = { it.recordId },
                contentType = {
                    if (it.imageUri.isNullOrBlank()) "record_plain" else "record_image"
                }
            ) { record ->
                RecordCard(
                    record = record,
                    selected = selectedRecordIds.contains(record.recordId),
                    palette = palette,
                    adaptive = adaptive,
                    allowImageLoading = true,
                    showShadow = false,
                    darkCardBackgroundOverride = Color(0xFF1A1A1C),
                    onClick = {
                        when {
                            selectedRecordIds.contains(record.recordId) -> {
                                selectedRecordIds = selectedRecordIds - record.recordId
                            }
                            selectedRecordIds.isNotEmpty() -> {
                                selectedRecordIds = selectedRecordIds + record.recordId
                            }
                            else -> {
                                onOpenDetail(record)
                            }
                        }
                    },
                    onLongPress = {
                        searchEnabled = false
                        moreMenuExpanded = false
                        selectedRecordIds = if (selectedRecordIds.contains(record.recordId)) {
                            selectedRecordIds - record.recordId
                        } else {
                            selectedRecordIds + record.recordId
                        }
                    }
                )
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
        BackHandler(enabled = selectedRecordIds.isNotEmpty()) {
            selectedRecordIds = emptySet()
            showDeleteConfirm = false
            resetDoubleBackExitState()
        }
        BackHandler(enabled = searchEnabled) {
            searchEnabled = false
            searchQuery = ""
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
                        if (selectedRecords.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = getString(R.string.selected_count_format, selectedRecords.size),
                                        color = palette.textPrimary,
                                        fontSize = spec.titleSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                NoMemoSelectionHeaderButton(
                                    text = if (allFilteredSelected) "\u53D6\u6D88\u5168\u9009" else stringResource(R.string.action_select_all),
                                    onClick = {
                                        selectedRecordIds = if (allFilteredSelected) {
                                            emptySet()
                                        } else {
                                            filteredRecords.map { it.recordId }.toSet()
                                        }
                                        showDeleteConfirm = false
                                    },
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_close,
                                    contentDescription = stringResource(R.string.cancel),
                                    onClick = {
                                        selectedRecordIds = emptySet()
                                        showDeleteConfirm = false
                                    },
                                    size = spec.topActionButtonSize
                                )
                            }
                        } else if (searchEnabled) {
                            NoMemoSearchBarCard(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                onClose = {
                                    searchEnabled = false
                                    searchQuery = ""
                                }
                            )
                        } else {
                            NoMemoTopActionButtons(
                                spec = spec,
                                onSearchClick = { searchEnabled = true },
                                onMoreClick = { moreMenuExpanded = !moreMenuExpanded }
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.page_title),
                                    color = palette.textPrimary,
                                    fontSize = spec.titleSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (selectedRecords.isEmpty()) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 14.dp, bottom = 10.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                FilterChip(spec, stringResource(R.string.filter_all), selectedFilter == FILTER_ALL) {
                                    onFilterSelect(FILTER_ALL)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                FilterChip(spec, stringResource(R.string.filter_quick), selectedFilter == FILTER_QUICK) {
                                    onFilterSelect(FILTER_QUICK)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                FilterChip(spec, stringResource(R.string.filter_life), selectedFilter == FILTER_LIFE) {
                                    onFilterSelect(FILTER_LIFE)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                FilterChip(spec, stringResource(R.string.filter_work), selectedFilter == FILTER_WORK) {
                                    onFilterSelect(FILTER_WORK)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                FilterChip(spec, stringResource(R.string.filter_ai), selectedFilter == FILTER_AI) {
                                    onFilterSelect(FILTER_AI)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                FilterChip(spec, stringResource(R.string.filter_archived), selectedFilter == FILTER_ARCHIVED) {
                                    onFilterSelect(FILTER_ARCHIVED)
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
                                        top = 12.dp,
                                        bottom = if (selectedRecords.isNotEmpty()) 20.dp else spec.pageBottomPadding + 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    content = recordItems
                                )
                            }
                        }

                    }

                    if (showCenteredEmptyState) {
                        NoMemoEmptyState(
                            iconRes = when {
                                searchQuery.isNotBlank() -> R.drawable.ic_nm_search
                                else -> R.drawable.ic_nm_memory
                            },
                            title = when {
                                searchQuery.isNotBlank() -> stringResource(R.string.search_empty)
                                selectedFilter == FILTER_ARCHIVED -> stringResource(R.string.no_archived_records)
                                else -> stringResource(R.string.no_records)
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = spec.pageHorizontalPadding)
                        )
                    }

                    if (selectedRecords.isEmpty()) {
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
                    } else {
                        NoMemoSelectionActionDock(
                            selectedRecords = selectedRecords,
                            onArchiveClick = {
                                onArchiveRecords(selectedRecords)
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
                                selectedRecordIds = emptySet()
                                showDeleteConfirm = false
                            },
                            onDismiss = { showDeleteConfirm = false }
                        )
                    }

                    NoMemoMenuPopup(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp) + spec.topActionButtonSize + 8.dp,
                                end = spec.pageHorizontalPadding
                            )
                            .offset(x = (-6).dp)
                    ) {
                        NoMemoMoreMenuPanel(
                            onSelectAll = {
                                selectedRecordIds = filteredRecords.map { it.recordId }.toSet()
                                moreMenuExpanded = false
                            },
                            onOpenSettings = {
                                moreMenuExpanded = false
                                onOpenSettings()
                            }
                        )
                    }

                    if (showAddSheet) {
                        AddMemorySheet(onDismiss = onDismissAddSheet)
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
            horizontalPadding = if (spec.isNarrow) 16.dp else 22.dp,
            showBorder = false,
            textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
        )
    }

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

