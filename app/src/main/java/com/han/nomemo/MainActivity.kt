package com.han.nomemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class MainActivity : BaseComposeActivity() {
    companion object {
        private const val FILTER_ALL = "ALL"
        private const val FILTER_LIFE = "LIFE"
        private const val FILTER_WORK = "WORK"
        private const val FILTER_AI = "AI"
    }

    private lateinit var memoryStore: MemoryStore
    private var selectedFilter by mutableStateOf(FILTER_ALL)
    private var records by mutableStateOf<List<MemoryRecord>>(emptyList())

    private val addMemoryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshRecords()
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
                onAddClick = { openAddMemoryPage() },
                onOpenGroup = { openGroupPage() },
                onOpenReminder = { openReminderPage() }
            )
        }
        refreshRecords()
    }

    override fun onResume() {
        super.onResume()
        refreshRecords()
    }

    private fun refreshRecords() {
        val all = memoryStore.loadRecords()
        val filtered = all.filter { record ->
            when (selectedFilter) {
                FILTER_LIFE -> CategoryCatalog.GROUP_LIFE == record.categoryGroupCode
                FILTER_WORK -> CategoryCatalog.GROUP_WORK == record.categoryGroupCode
                FILTER_AI -> MemoryRecord.MODE_AI == record.mode
                else -> true
            }
        }
        records = filtered
    }

    private fun openAddMemoryPage() {
        addMemoryLauncher.launch(Intent(this, AddMemoryActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
    }

    private fun openGroupPage() {
        startActivity(Intent(this, GroupActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
    }

    private fun openReminderPage() {
        startActivity(Intent(this, ReminderActivity::class.java))
        overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
    }

    @Composable
    private fun MainContent(
        records: List<MemoryRecord>,
        selectedFilter: String,
        onFilterSelect: (String) -> Unit,
        onAddClick: () -> Unit,
        onOpenGroup: () -> Unit,
        onOpenReminder: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
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
            val palette = rememberNoMemoPalette()
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pageSwipeNavigation(
                            onSwipeLeft = onOpenGroup
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = spec.pageTopPadding,
                                end = spec.pageHorizontalPadding,
                                bottom = spec.pageBottomPadding
                            )
                    ) {
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
                            Text(
                                text = stringResource(R.string.page_subtitle),
                                color = palette.textSecondary,
                                fontSize = spec.subtitleSize
                            )
                        }
                        GlassIconCircleButton(
                            iconRes = android.R.drawable.ic_menu_search,
                            contentDescription = stringResource(R.string.action_search),
                            onClick = {},
                            modifier = Modifier.padding(end = 10.dp),
                            size = spec.topActionButtonSize
                        )
                        GlassIconCircleButton(
                            iconRes = android.R.drawable.ic_menu_more,
                            contentDescription = stringResource(R.string.action_more),
                            onClick = {},
                            size = spec.topActionButtonSize
                        )
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        GlassChip(
                            text = stringResource(R.string.filter_all),
                            selected = selectedFilter == FILTER_ALL,
                            onClick = { onFilterSelect(FILTER_ALL) },
                            horizontalPadding = if (spec.isNarrow) 16.dp else 22.dp,
                            textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        GlassChip(
                            text = stringResource(R.string.filter_life),
                            selected = selectedFilter == FILTER_LIFE,
                            onClick = { onFilterSelect(FILTER_LIFE) },
                            horizontalPadding = if (spec.isNarrow) 16.dp else 22.dp,
                            textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        GlassChip(
                            text = stringResource(R.string.filter_work),
                            selected = selectedFilter == FILTER_WORK,
                            onClick = { onFilterSelect(FILTER_WORK) },
                            horizontalPadding = if (spec.isNarrow) 16.dp else 22.dp,
                            textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        GlassChip(
                            text = stringResource(R.string.filter_ai),
                            selected = selectedFilter == FILTER_AI,
                            onClick = { onFilterSelect(FILTER_AI) },
                            horizontalPadding = if (spec.isNarrow) 16.dp else 22.dp,
                            textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
                        )
                    }

                    Text(
                        text = stringResource(R.string.history_title),
                        color = palette.textPrimary,
                        fontSize = spec.sectionTitleSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    if (records.isEmpty()) {
                        GlassPanelText(
                            text = stringResource(R.string.no_records),
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(records, key = { it.recordId }) { record ->
                            RecordCard(record = record)
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
            }
        }
    }
    }

    @Composable
    private fun BottomBar(
        spec: NoMemoAdaptiveSpec,
        modifier: Modifier,
        onOpenGroup: () -> Unit,
        onOpenReminder: () -> Unit,
        onAddClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val haloTransition = rememberInfiniteTransition(label = "haloTransition")
        val haloScale by haloTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "haloScale"
        )
        val haloAlpha by haloTransition.animateFloat(
            initialValue = 0.18f,
            targetValue = 0.36f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "haloAlpha"
        )

        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(spec.bottomNavHeight)
                    .clip(RoundedCornerShape(42.dp))
                    .background(palette.glassFill)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    iconRes = android.R.drawable.ic_menu_agenda,
                    text = stringResource(R.string.nav_memory),
                    selected = true,
                    onClick = {}
                )
                NavItem(
                    iconRes = android.R.drawable.ic_menu_sort_by_size,
                    text = stringResource(R.string.nav_group),
                    selected = false,
                    onClick = onOpenGroup
                )
                NavItem(
                    iconRes = android.R.drawable.ic_menu_recent_history,
                    text = stringResource(R.string.nav_reminder),
                    selected = false,
                    onClick = onOpenReminder
                )
            }

                Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier.size(spec.fabFrameSize),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(spec.fabFrameSize)
                        .graphicsLayer {
                            scaleX = haloScale
                            scaleY = haloScale
                            alpha = haloAlpha
                        }
                        .clip(RoundedCornerShape(39.dp))
                        .background(palette.glassFillSoft)
                )
                PressScaleBox(
                    onClick = onAddClick,
                    pressedScale = 0.92f,
                    modifier = Modifier
                        .size(spec.fabButtonSize)
                        .clip(RoundedCornerShape(34.dp))
                        .background(palette.accent)
                ) {
                    Text(
                        text = stringResource(R.string.save_record),
                        color = palette.onAccent,
                        fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    @Composable
    private fun RowScope.NavItem(
        iconRes: Int,
        text: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val adaptive = rememberNoMemoAdaptiveSpec()
        val itemColor = if (selected) palette.accent else palette.textPrimary
        val selectedBackground = if (selected) palette.glassFillSoft else androidx.compose.ui.graphics.Color.Transparent

        PressScaleBox(
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .height(adaptive.bottomNavItemHeight)
                .clip(RoundedCornerShape(32.dp))
                .background(selectedBackground)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = iconRes),
                    contentDescription = text,
                    tint = itemColor,
                    modifier = Modifier.size(if (adaptive.isNarrow) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = text,
                    color = itemColor,
                    fontSize = if (adaptive.isNarrow) 10.sp else 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}
