package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false

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
                selectedFilter = selectedFilter,
                onFilterSelect = { filter ->
                    selectedFilter = filter
                    refreshRecords()
                },
                onDeleteRecord = { record -> deleteRecord(record) },
                onArchiveRecord = { record -> toggleArchive(record) },
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

    private fun refreshRecords() {
        records = if (selectedFilter == FILTER_ARCHIVED) {
            memoryStore.loadArchivedRecords()
        } else {
            memoryStore.loadActiveRecords()
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
        startActivity(Intent(this, GroupActivity::class.java))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
    }

    private fun openReminderPage() {
        startActivity(Intent(this, ReminderActivity::class.java))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
    }

    private fun openSettingsPage() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
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

    @Composable
    private fun MainContent(
        records: List<MemoryRecord>,
        selectedFilter: String,
        onFilterSelect: (String) -> Unit,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onArchiveRecord: (MemoryRecord) -> Unit,
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
        var selectedRecordId by remember { mutableStateOf<String?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var searchEnabled by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }

        val selectedRecord = remember(records, selectedRecordId) {
            records.firstOrNull { it.recordId == selectedRecordId }
        }
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

        LaunchedEffect(filteredRecords, selectedRecordId) {
            if (selectedRecordId != null && filteredRecords.none { it.recordId == selectedRecordId }) {
                selectedRecordId = null
                showDeleteConfirm = false
            }
        }

        var entered by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { entered = true }
        val listAlpha by animateFloatAsState(
            targetValue = if (entered) 1f else 0f,
            animationSpec = tween(durationMillis = 460),
            label = "listAlpha"
        )
        val listOffsetY by animateFloatAsState(
            targetValue = if (entered) 0f else 26f,
            animationSpec = tween(durationMillis = 460),
            label = "listOffset"
        )
        val listScale by animateFloatAsState(
            targetValue = if (entered) 1f else 0.985f,
            animationSpec = tween(durationMillis = 460),
            label = "listScale"
        )

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
                        if (searchEnabled) {
                            SearchBarCard(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                onClose = {
                                    searchEnabled = false
                                    searchQuery = ""
                                }
                            )
                        } else {
                            if (selectedRecord != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(R.string.page_title),
                                            color = palette.textPrimary,
                                            fontSize = spec.titleSize,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_sheet_calendar,
                                        contentDescription = if (selectedRecord.isArchived) {
                                            stringResource(R.string.action_unarchive)
                                        } else {
                                            stringResource(R.string.action_archive)
                                        },
                                        onClick = { onArchiveRecord(selectedRecord) },
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 0.dp)
                                        .offset(y = (-4).dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_nm_search,
                                        contentDescription = stringResource(R.string.action_search),
                                        onClick = { searchEnabled = true },
                                        modifier = Modifier.padding(end = 10.dp),
                                        size = spec.topActionButtonSize
                                    )
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_nm_settings,
                                        contentDescription = stringResource(R.string.action_settings),
                                        onClick = onOpenSettings,
                                        size = spec.topActionButtonSize
                                    )
                                }

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
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = 14.dp)
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

                        if (filteredRecords.isEmpty()) {
                            GlassPanelText(
                                text = when {
                                    searchQuery.isNotBlank() -> stringResource(R.string.search_empty)
                                    selectedFilter == FILTER_ARCHIVED -> stringResource(R.string.no_archived_records)
                                    else -> stringResource(R.string.no_records)
                                },
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 10.dp)
                                .graphicsLayer {
                                    alpha = listAlpha
                                    translationY = listOffsetY
                                    scaleX = listScale
                                    scaleY = listScale
                                },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                bottom = spec.pageBottomPadding + 20.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredRecords, key = { it.recordId }) { record ->
                                RecordCard(
                                    record = record,
                                    selected = selectedRecordId == record.recordId,
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
                                    onLongPress = { selectedRecordId = record.recordId }
                                )
                            }
                        }
                    }

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
                        onAddClick = onAddClick
                    )

                    if (showDeleteConfirm && selectedRecord != null) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm = false },
                            title = { Text(stringResource(R.string.delete_selected_title)) },
                            text = { Text(stringResource(R.string.delete_selected_message)) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onDeleteRecord(selectedRecord)
                                        selectedRecordId = null
                                        showDeleteConfirm = false
                                    }
                                ) {
                                    Text(stringResource(R.string.action_delete))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text(stringResource(R.string.cancel))
                                }
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
