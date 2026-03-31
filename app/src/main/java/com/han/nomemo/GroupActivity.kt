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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
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

class GroupActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private var selectedCategoryCode by mutableStateOf<String?>(null)
    private var allRecords by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                recreate()
            } else {
                refreshContent()
            }
        }

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            refreshContent()
        }
    }

    override fun enableDoubleBackToDesktop(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            GroupContent(
                allRecords = allRecords,
                hasLoadedRecords = hasLoadedRecords,
                selectedCategoryCode = selectedCategoryCode,
                onSelectCategory = { selectedCategoryCode = it },
                onDeleteRecord = { record -> deleteRecord(record) },
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onOpenMemory = { openMemoryPage() },
                onOpenReminder = { openReminderPage() },
                onOpenSettings = { openSettingsPage() },
                showAddSheet = showAddSheet,
                onAddClick = { showAddSheet = true },
                onDismissAddSheet = { showAddSheet = false }
            )
        }
        refreshContent()
    }

    override fun onResume() {
        super.onResume()
        refreshContent()
    }

    override fun onStart() {
        super.onStart()
        registerMemoryChangeReceiver()
    }

    override fun onStop() {
        unregisterMemoryChangeReceiver()
        super.onStop()
    }

    private fun refreshContent() {
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            val loadedRecords = withContext(Dispatchers.IO) {
                memoryStore.loadActiveRecords()
            }
            allRecords = loadedRecords
            hasLoadedRecords = true
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

    private fun openReminderPage() {
        switchPrimaryPage(Intent(this, ReminderActivity::class.java))
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
    }

    private fun openSettingsPage() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }

    private fun deleteRecord(record: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(record.recordId)
        if (deleted) {
            refreshContent()
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun GroupContent(
        allRecords: List<MemoryRecord>,
        hasLoadedRecords: Boolean,
        selectedCategoryCode: String?,
        onSelectCategory: (String?) -> Unit,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenReminder: () -> Unit,
        onOpenSettings: () -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var selectedRecordId by remember { mutableStateOf<String?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var searchEnabled by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var pendingScrollToTopAfterAdd by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val dockHasUnderContent = rememberDockHasUnderContent(
            listState = listState,
            spec = adaptive
        )
        val filtered = remember(allRecords, selectedCategoryCode, searchQuery) {
            allRecords.filter { record ->
                val matchesCategory = selectedCategoryCode == null || selectedCategoryCode == record.categoryCode
                if (!matchesCategory) {
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
        val selectedRecord = remember(filtered, selectedRecordId) {
            filtered.firstOrNull { it.recordId == selectedRecordId }
        }
        val showCenteredEmptyState = hasLoadedRecords && filtered.isEmpty()
        LaunchedEffect(filtered, selectedRecordId) {
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
        BackHandler(enabled = searchEnabled) {
            searchEnabled = false
            searchQuery = ""
            resetDoubleBackExitState()
        }

        fun countByCode(code: String): Int = allRecords.count { it.categoryCode == code }
        val quickCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_QUICK }
        val lifeCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_LIFE }
        val workCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_WORK }
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
                                    text = stringResource(R.string.group_page_title),
                                    color = palette.textPrimary,
                                    fontSize = spec.titleSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = 14.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            GroupChip(stringResource(R.string.filter_all), selectedCategoryCode == null, spec.chipTextSize) {
                                onSelectCategory(null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_quick), countByCode(CategoryCatalog.CODE_QUICK_NOTE)), selectedCategoryCode == CategoryCatalog.CODE_QUICK_NOTE, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_QUICK_NOTE)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_pickup), countByCode(CategoryCatalog.CODE_LIFE_PICKUP)), selectedCategoryCode == CategoryCatalog.CODE_LIFE_PICKUP, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_LIFE_PICKUP)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_delivery), countByCode(CategoryCatalog.CODE_LIFE_DELIVERY)), selectedCategoryCode == CategoryCatalog.CODE_LIFE_DELIVERY, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_LIFE_DELIVERY)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_card), countByCode(CategoryCatalog.CODE_LIFE_CARD)), selectedCategoryCode == CategoryCatalog.CODE_LIFE_CARD, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_LIFE_CARD)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_ticket), countByCode(CategoryCatalog.CODE_LIFE_TICKET)), selectedCategoryCode == CategoryCatalog.CODE_LIFE_TICKET, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_LIFE_TICKET)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_todo), countByCode(CategoryCatalog.CODE_WORK_TODO)), selectedCategoryCode == CategoryCatalog.CODE_WORK_TODO, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_WORK_TODO)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(buildChipText(stringResource(R.string.cat_schedule), countByCode(CategoryCatalog.CODE_WORK_SCHEDULE)), selectedCategoryCode == CategoryCatalog.CODE_WORK_SCHEDULE, spec.chipTextSize) {
                                onSelectCategory(CategoryCatalog.CODE_WORK_SCHEDULE)
                            }
                        }

                        if (!hasLoadedRecords || filtered.isEmpty()) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f),
                                state = listState,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    top = if (spec.widthClass == NoMemoWidthClass.EXPANDED) 14.dp else 12.dp,
                                    bottom = spec.pageBottomPadding + 20.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(if (spec.widthClass == NoMemoWidthClass.EXPANDED) 14.dp else 12.dp)
                            ) {
                                items(
                                    items = filtered,
                                    key = { it.recordId },
                                    contentType = {
                                        if (it.imageUri.isNullOrBlank()) "record_plain" else "record_image"
                                    }
                                ) { record ->
                                    RecordCard(
                                        record = record,
                                        selected = selectedRecordId == record.recordId,
                                        palette = palette,
                                        adaptive = spec,
                                        allowImageLoading = true,
                                        onClick = {
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
                                    },
                                    onLongPress = {
                                        searchEnabled = false
                                        moreMenuExpanded = false
                                        selectedRecordId = record.recordId
                                    }
                                )
                            }
                        }
                        }
                    }

                    if (showCenteredEmptyState) {
                        NoMemoEmptyState(
                            iconRes = if (searchQuery.isNotBlank()) R.drawable.ic_nm_search else R.drawable.ic_nm_group,
                            title = if (searchQuery.isNotBlank()) stringResource(R.string.search_empty) else stringResource(R.string.group_empty),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = spec.pageHorizontalPadding)
                        )
                    }

                    NoMemoBottomDock(
                        selectedTab = NoMemoDockTab.GROUP,
                        onOpenMemory = onOpenMemory,
                        onOpenGroup = {},
                        onOpenReminder = onOpenReminder,
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
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp) + spec.topActionButtonSize + 8.dp,
                                end = spec.pageHorizontalPadding
                            )
                            .offset(x = (-6).dp)
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
    private fun GroupChip(
        text: String,
        selected: Boolean,
        chipTextSize: androidx.compose.ui.unit.TextUnit,
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

    private fun buildChipText(label: String, count: Int): String = "$label($count)"
}
