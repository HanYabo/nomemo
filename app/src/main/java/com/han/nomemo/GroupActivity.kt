package com.han.nomemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
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
                onBack = { openMemoryPage() },
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
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
        finish()
    }

    private fun openReminderPage() {
        startActivity(Intent(this, ReminderActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
        finish()
    }

    private fun openAddMemoryPage() {
        startActivity(Intent(this, AddMemoryActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
    }

    @Composable
    private fun GroupContent(
        allRecords: List<MemoryRecord>,
        selectedCategoryCode: String?,
        onSelectCategory: (String?) -> Unit,
        onBack: () -> Unit,
        onOpenMemory: () -> Unit,
        onOpenReminder: () -> Unit,
        onAddClick: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val filtered = allRecords.filter { selectedCategoryCode == null || selectedCategoryCode == it.categoryCode }

        fun countByCode(code: String): Int = allRecords.count { it.categoryCode == code }
        val lifeCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_LIFE }
        val workCount = allRecords.count { it.categoryGroupCode == CategoryCatalog.GROUP_WORK }
        val summary = getString(R.string.group_summary_format, lifeCount, workCount, allRecords.size)

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pageSwipeNavigation(
                            onSwipeLeft = onOpenReminder,
                            onSwipeRight = onOpenMemory
                        )
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
                                text = stringResource(R.string.group_page_title),
                                color = palette.textPrimary,
                                fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                                fontWeight = FontWeight.Bold
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
                                RecordCard(record = record)
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
