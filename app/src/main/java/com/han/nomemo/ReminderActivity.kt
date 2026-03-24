package com.han.nomemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
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

class ReminderActivity : BaseComposeActivity() {
    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_PENDING = "PENDING"
        private const val FILTER_DONE = "DONE"
    }

    private lateinit var memoryStore: MemoryStore
    private var selectedFilter by mutableStateOf(FILTER_ALL)
    private var reminderRecords by mutableStateOf<List<MemoryRecord>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            ReminderContent(
                records = reminderRecords,
                selectedFilter = selectedFilter,
                onBack = { finish() },
                onFilterSelected = { filter ->
                    selectedFilter = filter
                    refreshReminders()
                },
                onDoneChanged = { record, done ->
                    memoryStore.updateReminderDone(record.recordId, done)
                    refreshReminders()
                }
            )
        }
        refreshReminders()
    }

    override fun onResume() {
        super.onResume()
        refreshReminders()
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

    @Composable
    private fun ReminderContent(
        records: List<MemoryRecord>,
        selectedFilter: String,
        onBack: () -> Unit,
        onFilterSelected: (String) -> Unit,
        onDoneChanged: (MemoryRecord, Boolean) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = spec.pageTopPadding,
                            end = spec.pageHorizontalPadding,
                            bottom = 16.dp
                        )
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PressScaleBox(
                        onClick = onBack,
                        modifier = Modifier
                            .size(if (spec.isNarrow) 40.dp else 44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(palette.glassFill)
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_media_previous),
                            contentDescription = stringResource(R.string.back),
                            tint = palette.textPrimary,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.reminder_page_title),
                        color = palette.textPrimary,
                        fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Start
                ) {
                    ReminderChip(
                        text = stringResource(R.string.reminder_filter_all),
                        selected = selectedFilter == FILTER_ALL,
                        onClick = { onFilterSelected(FILTER_ALL) },
                        chipTextSize = spec.chipTextSize
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReminderChip(
                        text = stringResource(R.string.reminder_filter_pending),
                        selected = selectedFilter == FILTER_PENDING,
                        onClick = { onFilterSelected(FILTER_PENDING) },
                        chipTextSize = spec.chipTextSize
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReminderChip(
                        text = stringResource(R.string.reminder_filter_done),
                        selected = selectedFilter == FILTER_DONE,
                        onClick = { onFilterSelected(FILTER_DONE) },
                        chipTextSize = spec.chipTextSize
                    )
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
                    verticalArrangement = Arrangement.spacedBy(if (spec.widthClass == NoMemoWidthClass.EXPANDED) 12.dp else 10.dp)
                ) {
                    items(records, key = { it.recordId }) { record ->
                        ReminderItem(record = record, onDoneChanged = onDoneChanged)
                    }
                }
            }
        }
    }
    }

    @Composable
    private fun ReminderChip(text: String, selected: Boolean, onClick: () -> Unit, chipTextSize: TextUnit) {
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
        onDoneChanged: (MemoryRecord, Boolean) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val title = if (!record.memory.isNullOrBlank()) {
            record.memory
        } else if (!record.sourceText.isNullOrBlank()) {
            record.sourceText
        } else {
            getString(R.string.reminder_default_title)
        }
        val time = if (record.reminderAt > 0L) record.reminderAt else record.createdAt
        val meta = "${record.categoryName} | ${dateFormat.format(Date(time))}"

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(palette.glassFill)
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
            }
        }
    }
}
