package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    private var memoryChangeRegistered = false

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            refreshReminders()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            ReminderContent(
                records = reminderRecords,
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
                onAddClick = { openAddMemoryPage() }
            )
        }
        refreshReminders()
    }

    override fun onResume() {
        super.onResume()
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
        val all = memoryStore.loadReminderRecords()
        reminderRecords = all.filter { record ->
            when (selectedFilter) {
                FILTER_PENDING -> !record.isReminderDone
                FILTER_DONE -> record.isReminderDone
                else -> true
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
        startActivity(intent)
        overridePendingTransition(R.anim.page_back_enter, R.anim.page_back_exit)
        finish()
    }

    private fun openGroupPage() {
        startActivity(Intent(this, GroupActivity::class.java))
        overridePendingTransition(R.anim.page_back_enter, R.anim.page_back_exit)
        finish()
    }

    private fun openAddMemoryPage() {
        startActivity(Intent(this, AddMemoryActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
        overridePendingTransition(R.anim.page_forward_enter, R.anim.page_forward_exit)
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
        selectedFilter: String,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onFilterSelected: (String) -> Unit,
        onDoneChanged: (MemoryRecord, Boolean) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenGroup: () -> Unit,
        onAddClick: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var selectedRecordId by remember { mutableStateOf<String?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        val selectedRecord = remember(records, selectedRecordId) {
            records.firstOrNull { it.recordId == selectedRecordId }
        }
        LaunchedEffect(records, selectedRecordId) {
            if (selectedRecordId != null && selectedRecord == null) {
                selectedRecordId = null
                showDeleteConfirm = false
            }
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
                                top = spec.pageTopPadding,
                                end = spec.pageHorizontalPadding,
                                bottom = 0.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.reminder_page_title),
                                color = palette.textPrimary,
                                fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_nm_delete,
                                contentDescription = stringResource(R.string.action_delete),
                                onClick = { if (selectedRecord != null) showDeleteConfirm = true },
                                size = if (spec.isNarrow) 44.dp else 48.dp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
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

                        if (records.isEmpty()) {
                            GlassPanelText(
                                text = stringResource(R.string.reminder_empty),
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
                            verticalArrangement = Arrangement.spacedBy(if (spec.widthClass == NoMemoWidthClass.EXPANDED) 12.dp else 10.dp)
                        ) {
                            items(records, key = { it.recordId }) { record ->
                                ReminderItem(
                                    record = record,
                                    selected = selectedRecordId == record.recordId,
                                    onDoneChanged = onDoneChanged,
                                    onLongPressSelect = { selectedRecordId = record.recordId },
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

                    NoMemoBottomDock(
                        selectedTab = NoMemoDockTab.REMINDER,
                        onOpenMemory = onOpenMemory,
                        onOpenGroup = onOpenGroup,
                        onOpenReminder = {},
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
        onDoneChanged: (MemoryRecord, Boolean) -> Unit,
        onLongPressSelect: () -> Unit,
        onClickItem: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
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
                .background(if (selected) palette.glassFillSoft else palette.glassFill)
                .border(
                    if (selected) 2.dp else 0.dp,
                    if (selected) palette.accent else palette.glassStroke,
                    RoundedCornerShape(20.dp)
                )
                .pointerInput(onLongPressSelect, onClickItem) {
                    detectTapGestures(
                        onTap = { onClickItem() },
                        onLongPress = { onLongPressSelect() }
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
