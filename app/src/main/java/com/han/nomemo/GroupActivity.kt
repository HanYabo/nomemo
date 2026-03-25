package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class GroupActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private var selectedCategoryCode by mutableStateOf<String?>(null)
    private var allRecords by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            refreshContent()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            GroupContent(
                allRecords = allRecords,
                selectedCategoryCode = selectedCategoryCode,
                onSelectCategory = { selectedCategoryCode = it },
                onDeleteRecord = { record -> deleteRecord(record) },
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onOpenMemory = { openMemoryPage() },
                onOpenReminder = { openReminderPage() },
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
        allRecords = memoryStore.loadActiveRecords()
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
        startActivity(intent)
        overridePendingTransition(R.anim.page_back_enter, R.anim.page_back_exit)
        finish()
    }

    private fun openReminderPage() {
        startActivity(Intent(this, ReminderActivity::class.java))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
        finish()
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
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
        selectedCategoryCode: String?,
        onSelectCategory: (String?) -> Unit,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenReminder: () -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var selectedRecordId by remember { mutableStateOf<String?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        val filtered = allRecords.filter { selectedCategoryCode == null || selectedCategoryCode == it.categoryCode }
        val selectedRecord = remember(filtered, selectedRecordId) {
            filtered.firstOrNull { it.recordId == selectedRecordId }
        }
        LaunchedEffect(filtered, selectedRecordId) {
            if (selectedRecordId != null && selectedRecord == null) {
                selectedRecordId = null
                showDeleteConfirm = false
            }
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-4).dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_nm_delete,
                                contentDescription = stringResource(R.string.action_delete),
                                onClick = { if (selectedRecord != null) showDeleteConfirm = true },
                                size = spec.topActionButtonSize
                            )
                        }

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

                        if (filtered.isEmpty()) {
                            GlassPanelText(
                                text = stringResource(R.string.group_empty),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                bottom = spec.pageBottomPadding + 20.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(if (spec.widthClass == NoMemoWidthClass.EXPANDED) 14.dp else 12.dp)
                        ) {
                            items(filtered, key = { it.recordId }) { record ->
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
                        selectedTab = NoMemoDockTab.GROUP,
                        onOpenMemory = onOpenMemory,
                        onOpenGroup = {},
                        onOpenReminder = onOpenReminder,
                        onAddClick = onAddClick,
                        spec = spec,
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
            textStyle = TextStyle(
                fontSize = chipTextSize,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            horizontalPadding = 16.dp
        )
    }

    private fun buildChipText(label: String, count: Int): String = "$label($count)"
}
