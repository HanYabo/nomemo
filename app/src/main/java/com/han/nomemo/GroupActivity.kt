package com.han.nomemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            GroupContent(
                allRecords = allRecords,
                selectedCategoryCode = selectedCategoryCode,
                onSelectCategory = { selectedCategoryCode = it },
                onDeleteRecord = { record -> deleteRecord(record) },
                onOpenMemory = { openMemoryPage() },
                onOpenReminder = { openReminderPage() },
                onAddClick = { openAddMemoryPage() }
            )
        }
        refreshContent()
    }

    override fun onResume() {
        super.onResume()
        refreshContent()
    }

    private fun refreshContent() {
        allRecords = memoryStore.loadRecords()
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

    private fun openAddMemoryPage() {
        startActivity(Intent(this, AddMemoryActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
    }

    private fun deleteRecord(record: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(record.recordId)
        if (deleted) {
            refreshContent()
            Toast.makeText(this, "已删除记忆", Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun GroupContent(
        allRecords: List<MemoryRecord>,
        selectedCategoryCode: String?,
        onSelectCategory: (String?) -> Unit,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenReminder: () -> Unit,
        onAddClick: () -> Unit
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
        val lifeCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_LIFE }
        val workCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_WORK }
        val summary = getString(R.string.group_summary_format, lifeCount, workCount, allRecords.size)

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = spec.pageTopPadding,
                                end = spec.pageHorizontalPadding,
                                bottom = spec.pageBottomPadding
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.group_page_title),
                                color = palette.textPrimary,
                                fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            GlassIconCircleButton(
                                iconRes = android.R.drawable.ic_menu_delete,
                                contentDescription = "删除已选项",
                                onClick = { if (selectedRecord != null) showDeleteConfirm = true },
                                size = if (spec.isNarrow) 44.dp else 48.dp
                            )
                        }

                        GlassPanelText(text = summary, modifier = Modifier.padding(top = 12.dp))

                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            GroupChip(
                                text = stringResource(R.string.filter_all),
                                selected = selectedCategoryCode == null,
                                onClick = { onSelectCategory(null) },
                                chipTextSize = spec.chipTextSize
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(
                                text = buildChipText(getString(R.string.cat_pickup), countByCode(CategoryCatalog.CODE_LIFE_PICKUP)),
                                selected = selectedCategoryCode == CategoryCatalog.CODE_LIFE_PICKUP,
                                onClick = { onSelectCategory(CategoryCatalog.CODE_LIFE_PICKUP) },
                                chipTextSize = spec.chipTextSize
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(
                                text = buildChipText(getString(R.string.cat_delivery), countByCode(CategoryCatalog.CODE_LIFE_DELIVERY)),
                                selected = selectedCategoryCode == CategoryCatalog.CODE_LIFE_DELIVERY,
                                onClick = { onSelectCategory(CategoryCatalog.CODE_LIFE_DELIVERY) },
                                chipTextSize = spec.chipTextSize
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(
                                text = buildChipText(getString(R.string.cat_card), countByCode(CategoryCatalog.CODE_LIFE_CARD)),
                                selected = selectedCategoryCode == CategoryCatalog.CODE_LIFE_CARD,
                                onClick = { onSelectCategory(CategoryCatalog.CODE_LIFE_CARD) },
                                chipTextSize = spec.chipTextSize
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(
                                text = buildChipText(getString(R.string.cat_ticket), countByCode(CategoryCatalog.CODE_LIFE_TICKET)),
                                selected = selectedCategoryCode == CategoryCatalog.CODE_LIFE_TICKET,
                                onClick = { onSelectCategory(CategoryCatalog.CODE_LIFE_TICKET) },
                                chipTextSize = spec.chipTextSize
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(
                                text = buildChipText(getString(R.string.cat_todo), countByCode(CategoryCatalog.CODE_WORK_TODO)),
                                selected = selectedCategoryCode == CategoryCatalog.CODE_WORK_TODO,
                                onClick = { onSelectCategory(CategoryCatalog.CODE_WORK_TODO) },
                                chipTextSize = spec.chipTextSize
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            GroupChip(
                                text = buildChipText(getString(R.string.cat_schedule), countByCode(CategoryCatalog.CODE_WORK_SCHEDULE)),
                                selected = selectedCategoryCode == CategoryCatalog.CODE_WORK_SCHEDULE,
                                onClick = { onSelectCategory(CategoryCatalog.CODE_WORK_SCHEDULE) },
                                chipTextSize = spec.chipTextSize
                            )
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
                            verticalArrangement = Arrangement.spacedBy(if (spec.widthClass == NoMemoWidthClass.EXPANDED) 14.dp else 12.dp)
                        ) {
                            items(filtered, key = { it.recordId }) { record ->
                                RecordCard(
                                    record = record,
                                    selected = selectedRecordId == record.recordId,
                                    onClick = {
                                        if (selectedRecordId == record.recordId) {
                                            selectedRecordId = null
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
                            title = { Text("确认删除") },
                            text = { Text("确定删除这条记忆吗？") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onDeleteRecord(selectedRecord)
                                        selectedRecordId = null
                                        showDeleteConfirm = false
                                    }
                                ) {
                                    Text("删除")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text("取消")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun GroupChip(text: String, selected: Boolean, onClick: () -> Unit, chipTextSize: androidx.compose.ui.unit.TextUnit) {
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
