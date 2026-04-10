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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class GroupActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private val initialOpenedAlbumId: String? by lazy {
        intent.getStringExtra(EXTRA_OPEN_ALBUM_ID)?.trim()?.takeIf { it.isNotEmpty() }
    }
    private var selectedCategoryCode by mutableStateOf<String?>(null)
    private var allRecords by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var albumRefreshTick by mutableIntStateOf(0)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null
    private var hasHandledInitialResume = false

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
                onDeleteRecords = { recordIds -> deleteRecords(recordIds) },
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onOpenMemory = { openMemoryPage() },
                onOpenReminder = { openReminderPage() },
                onOpenSearch = { openSearchPage() },
                onOpenSettings = { openSettingsPage() },
                showAddSheet = showAddSheet,
                onAddClick = { showAddSheet = true },
                onDismissAddSheet = { showAddSheet = false },
                albumRefreshTick = albumRefreshTick,
                initialOpenedAlbumId = initialOpenedAlbumId,
                openedAsStandaloneDetail = initialOpenedAlbumId != null,
                onOpenAlbumDetail = { albumId -> openAlbumDetailPage(albumId) },
                onCloseAlbumDetail = { finish() }
            )
        }
        refreshContent()
    }

    override fun onResume() {
        super.onResume()
        if (!hasHandledInitialResume) {
            hasHandledInitialResume = true
            return
        }
        refreshContent()
        albumRefreshTick += 1
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
                val result = memoryStore.loadActiveRecords()
                prewarmMemoryThumbnailCache(applicationContext, result)
                result
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

    private fun openAlbumDetailPage(albumId: String) {
        startActivity(createGroupActivityIntent(this, albumId))
    }

    private fun openSettingsPage() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }

    private fun openSearchPage() {
        startActivity(SearchActivity.createIntent(this))
    }

    private fun deleteRecord(record: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(record.recordId)
        if (deleted) {
            refreshContent()
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteRecords(recordIds: Set<String>) {
        if (recordIds.isEmpty()) return
        var deletedCount = 0
        recordIds.forEach { recordId ->
            if (memoryStore.deleteRecord(recordId)) {
                deletedCount += 1
            }
        }
        if (deletedCount > 0) {
            refreshContent()
            Toast.makeText(this, getString(R.string.delete_selected_success, deletedCount), Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun GroupContent(
        allRecords: List<MemoryRecord>,
        hasLoadedRecords: Boolean,
        selectedCategoryCode: String?,
        onSelectCategory: (String?) -> Unit,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onDeleteRecords: (Set<String>) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenReminder: () -> Unit,
        onOpenSearch: () -> Unit,
        onOpenSettings: () -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit,
        albumRefreshTick: Int,
        initialOpenedAlbumId: String?,
        openedAsStandaloneDetail: Boolean,
        onOpenAlbumDetail: (String) -> Unit,
        onCloseAlbumDetail: () -> Unit
    ) {
        val albumContext = LocalContext.current
        val albumStore = remember(albumContext) { GroupAlbumStore(albumContext) }
        val albumAdaptive = rememberNoMemoAdaptiveSpec()
        val albumPalette = rememberNoMemoPalette()
        val groupListState = rememberLazyListState()
        val albumDetailListState = rememberLazyListState()
        val albumDockHasUnderContent = rememberDockHasUnderContent(
            listState = groupListState,
            spec = albumAdaptive
        )
        var albumList by remember { mutableStateOf(albumStore.loadAlbums()) }
        var openedAlbumId by remember { mutableStateOf(initialOpenedAlbumId) }
        var showCreateAlbumDialog by remember { mutableStateOf(false) }
        var showAddExistingSheet by remember { mutableStateOf(false) }
        var selectedExistingRecordIds by remember { mutableStateOf<Set<String>>(emptySet()) }
        var addExistingSearchQuery by remember { mutableStateOf("") }
        var groupListMoreExpanded by remember { mutableStateOf(false) }
        var detailMoreExpanded by remember { mutableStateOf(false) }
        var groupListMoreAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var detailMoreAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var selectedAlbumRecordIds by remember { mutableStateOf<Set<String>>(emptySet()) }
        var albumSelectionModeActive by remember { mutableStateOf(false) }
        var showRemoveFromAlbumConfirm by remember { mutableStateOf(false) }
        var showDeleteSelectedConfirm by remember { mutableStateOf(false) }
        var showEditAlbumDialog by remember { mutableStateOf(false) }
        var showDeleteAlbumConfirm by remember { mutableStateOf(false) }
        var editingAlbumId by remember { mutableStateOf<String?>(null) }
        var albumNameInput by remember { mutableStateOf("") }
        var albumDescriptionInput by remember { mutableStateOf("") }
        val validRecordIds = remember(allRecords) { allRecords.map { it.recordId }.toSet() }
        LaunchedEffect(albumRefreshTick) {
            albumList = albumStore.loadAlbums()
        }

        val albumColumns = if (albumAdaptive.widthClass == NoMemoWidthClass.EXPANDED) 3 else 2
        val filteredAlbumList = albumList
        val albumRows = remember(filteredAlbumList, albumColumns) { filteredAlbumList.chunked(albumColumns) }
        val recordById = remember(allRecords) { allRecords.associateBy { it.recordId } }
        val albumPreviewRecordsMap = remember(filteredAlbumList, recordById) {
            filteredAlbumList.associate { album ->
                album.albumId to album.recordIds
                    .mapNotNull { recordById[it] }
                    .take(3)
            }
        }
        val openedAlbum = remember(albumList, openedAlbumId) {
            albumList.firstOrNull { it.albumId == openedAlbumId }
        }
        val currentAlbumRecordIds = remember(albumList, openedAlbumId) {
            albumList
                .firstOrNull { it.albumId == openedAlbumId }
                ?.recordIds
                ?.toSet()
                .orEmpty()
        }
        val openedRecords = remember(allRecords, openedAlbum?.recordIds) {
            val current = openedAlbum ?: return@remember emptyList()
            val byId = allRecords.associateBy { it.recordId }
            current.recordIds.mapNotNull { byId[it] }
        }
        val selectedAlbumRecords = remember(openedRecords, selectedAlbumRecordIds) {
            openedRecords.filter { selectedAlbumRecordIds.contains(it.recordId) }
        }
        val allOpenedRecordsSelected = openedRecords.isNotEmpty() &&
            openedRecords.all { selectedAlbumRecordIds.contains(it.recordId) }
        val availableExistingRecords = remember(allRecords, currentAlbumRecordIds) {
            // A memory can belong to multiple albums.
            // Only exclude items that are already in the currently opened album.
            allRecords.filterNot { currentAlbumRecordIds.contains(it.recordId) }
        }
        val filteredExistingRecords = remember(availableExistingRecords, addExistingSearchQuery) {
            val query = addExistingSearchQuery.trim().lowercase()
            if (query.isBlank()) {
                availableExistingRecords
            } else {
                availableExistingRecords.filter { record ->
                    listOf(
                        record.title,
                        record.summary,
                        record.memory,
                        record.sourceText,
                        record.analysis,
                        record.categoryName
                    )
                        .joinToString("\n") { it.orEmpty() }
                        .lowercase()
                        .contains(query)
                }
            }
        }
        val density = LocalDensity.current
        val groupHeaderCollapseDistancePx = with(density) { 84.dp.toPx() }
        val groupHeaderCollapseTarget by remember(
            openedAlbum?.albumId,
            albumList.isEmpty(),
            groupListState.firstVisibleItemIndex,
            groupListState.firstVisibleItemScrollOffset
        ) {
            derivedStateOf {
                if (openedAlbum != null || albumList.isEmpty()) {
                    0f
                } else {
                    when {
                        groupListState.firstVisibleItemIndex > 0 -> 1f
                        groupHeaderCollapseDistancePx <= 0f -> 0f
                        else -> (groupListState.firstVisibleItemScrollOffset / groupHeaderCollapseDistancePx)
                            .coerceIn(0f, 1f)
                    }
                }
            }
        }
        val groupHeaderCollapseProgress by animateFloatAsState(
            targetValue = groupHeaderCollapseTarget,
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            label = "groupHeaderCollapse"
        )
        val groupExpandedTitleAlpha = (1f - groupHeaderCollapseProgress).coerceIn(0f, 1f)
        val groupCollapsedTitleAlpha = groupHeaderCollapseProgress.coerceIn(0f, 1f)
        val groupExpandedTitleTranslateY =
            with(density) { (-20).dp.toPx() * groupHeaderCollapseProgress }
        val groupExpandedTitleMaxHeight = if (albumAdaptive.isNarrow) 44.dp else 50.dp
        val groupExpandedTitleHeight by animateDpAsState(
            targetValue = lerp(groupExpandedTitleMaxHeight, 0.dp, groupHeaderCollapseProgress),
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            label = "groupExpandedTitleHeight"
        )
        val groupListSpacing = 14.dp
        val groupListTopPadding by animateDpAsState(
            targetValue = lerp(12.dp, 4.dp, groupHeaderCollapseProgress),
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            label = "groupListTopPadding"
        )
        LaunchedEffect(hasLoadedRecords, validRecordIds) {
            if (!hasLoadedRecords) {
                return@LaunchedEffect
            }
            if (albumStore.pruneInvalidRecordIds(validRecordIds)) {
                albumList = albumStore.loadAlbums()
            }
        }
        LaunchedEffect(openedAlbumId) {
            selectedAlbumRecordIds = emptySet()
            albumSelectionModeActive = false
            showRemoveFromAlbumConfirm = false
            showDeleteSelectedConfirm = false
        }
        LaunchedEffect(openedRecords, selectedAlbumRecordIds) {
            val validIds = openedRecords.map { it.recordId }.toSet()
            val sanitized = selectedAlbumRecordIds.filterTo(linkedSetOf()) { validIds.contains(it) }.toSet()
            if (sanitized != selectedAlbumRecordIds) {
                selectedAlbumRecordIds = sanitized
            }
            if (sanitized.isEmpty()) {
                albumSelectionModeActive = false
                showRemoveFromAlbumConfirm = false
                showDeleteSelectedConfirm = false
            }
        }
        BackHandler(
            enabled = openedAlbum != null &&
                !showAddSheet &&
                !showCreateAlbumDialog &&
                !showAddExistingSheet &&
                !showEditAlbumDialog &&
                !detailMoreExpanded &&
                !albumSelectionModeActive
        ) {
            if (openedAsStandaloneDetail) {
                onCloseAlbumDetail()
            } else {
                openedAlbumId = null
            }
            resetDoubleBackExitState()
        }
        BackHandler(enabled = albumSelectionModeActive) {
            albumSelectionModeActive = false
            selectedAlbumRecordIds = emptySet()
            showRemoveFromAlbumConfirm = false
            showDeleteSelectedConfirm = false
        }
        LaunchedEffect(openedAsStandaloneDetail, openedAlbumId, openedAlbum) {
            if (openedAsStandaloneDetail && openedAlbumId != null && openedAlbum == null) {
                onCloseAlbumDetail()
            }
        }
        BackHandler(enabled = detailMoreExpanded) {
            detailMoreExpanded = false
        }
        BackHandler(
            enabled = openedAlbum == null &&
                groupListMoreExpanded &&
                !showAddSheet &&
                !showCreateAlbumDialog &&
                !showAddExistingSheet &&
                !showEditAlbumDialog
        ) {
            groupListMoreExpanded = false
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = albumAdaptive) { spec ->
                Box(modifier = Modifier.fillMaxSize()) {
                    // 创建全局 backdrop 层，捕获页面内容用于液态玻璃效果
                    val backdrop = rememberLayerBackdrop {
                        drawContent()  // 绘制内容层（Column 会标记为 layerBackdrop）
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .layerBackdrop(backdrop)  // 标记为内容层
                            .statusBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                end = spec.pageHorizontalPadding,
                                bottom = 0.dp
                            )
                    ) {
                        if (openedAlbum == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spec.topActionButtonSize)
                                    .padding(top = 2.dp)
                            ) {
                                NoMemoTopActionButtons(
                                    spec = spec,
                                    onSearchClick = {
                                        groupListMoreExpanded = false
                                        onOpenSearch()
                                    },
                                    onMoreClick = { groupListMoreExpanded = !groupListMoreExpanded },
                                    onMoreButtonBoundsChanged = { groupListMoreAnchorBounds = it },
                                    modifier = Modifier.align(Alignment.TopStart)
                                )
                                Text(
                                    text = stringResource(R.string.group_page_title),
                                    color = albumPalette.textPrimary,
                                    fontSize = if (spec.isNarrow) 18.sp else 19.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .graphicsLayer {
                                            alpha = groupCollapsedTitleAlpha
                                        }
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(groupExpandedTitleHeight)
                                    .padding(top = 2.dp)
                                    .graphicsLayer {
                                        alpha = groupExpandedTitleAlpha
                                        translationY = groupExpandedTitleTranslateY
                                    }
                            ) {
                                Text(
                                    text = stringResource(R.string.group_page_title),
                                    color = albumPalette.textPrimary,
                                    fontSize = spec.titleSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (albumList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NoMemoEmptyState(
                                        iconRes = R.drawable.ic_nm_group,
                                        title = "还没有分组",
                                        subtitle = "点击右上角更多菜单新增分组"
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    state = groupListState,
                                    contentPadding = PaddingValues(
                                        top = groupListTopPadding,
                                        bottom = spec.pageBottomPadding + 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(groupListSpacing)
                                ) {
                                    items(albumRows) { rowAlbums ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            rowAlbums.forEach { album ->
                                                GroupAlbumGridCard(
                                                    album = album,
                                                    compact = spec.widthClass == NoMemoWidthClass.COMPACT,
                                                    memoryCount = album.recordIds.size,
                                                    previewRecords = albumPreviewRecordsMap[album.albumId].orEmpty(),
                                                    modifier = Modifier.weight(1f),
                                                    onClick = {
                                                        groupListMoreExpanded = false
                                                        onOpenAlbumDetail(album.albumId)
                                                    }
                                                )
                                            }
                                            repeat(albumColumns - rowAlbums.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (albumSelectionModeActive) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_sheet_close,
                                        contentDescription = stringResource(R.string.cancel),
                                        onClick = {
                                            albumSelectionModeActive = false
                                            selectedAlbumRecordIds = emptySet()
                                            showRemoveFromAlbumConfirm = false
                                            showDeleteSelectedConfirm = false
                                        },
                                        size = spec.topActionButtonSize,
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    )
                                    Text(
                                        text = getString(R.string.selected_count_format, selectedAlbumRecords.size),
                                        color = albumPalette.textPrimary,
                                        fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                    GlassIconCircleButton(
                                        iconRes = if (allOpenedRecordsSelected) {
                                            R.drawable.ic_sheet_deselect_all
                                        } else {
                                            R.drawable.ic_sheet_select_all
                                        },
                                        contentDescription = if (allOpenedRecordsSelected) "取消全选" else "全选",
                                        onClick = {
                                            selectedAlbumRecordIds = if (allOpenedRecordsSelected) {
                                                emptySet()
                                            } else {
                                                openedRecords.map { it.recordId }.toSet()
                                            }
                                            showRemoveFromAlbumConfirm = false
                                            showDeleteSelectedConfirm = false
                                        },
                                        size = spec.topActionButtonSize,
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    Text(
                                        text = openedAlbum.name,
                                        color = albumPalette.textPrimary,
                                        fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(
                                                start = spec.topActionButtonSize + 24.dp,
                                                end = spec.topActionButtonSize + 24.dp
                                            )
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        GlassIconCircleButton(
                                            iconRes = R.drawable.ic_sheet_back,
                                            contentDescription = stringResource(R.string.back),
                                            onClick = {
                                                if (openedAsStandaloneDetail) {
                                                    onCloseAlbumDetail()
                                                } else {
                                                    openedAlbumId = null
                                                }
                                            },
                                            size = spec.topActionButtonSize
                                        )
                                        GlassIconCircleButton(
                                            iconRes = R.drawable.ic_nm_more,
                                            contentDescription = stringResource(R.string.action_more),
                                            onClick = {
                                                detailMoreExpanded = !detailMoreExpanded
                                            },
                                            size = spec.topActionButtonSize,
                                            onBoundsChanged = { detailMoreAnchorBounds = it }
                                        )
                                    }
                                }
                            }

                            if (openedRecords.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    state = albumDetailListState,
                                    contentPadding = PaddingValues(
                                        top = 12.dp,
                                        bottom = if (albumSelectionModeActive && selectedAlbumRecords.isNotEmpty()) {
                                            spec.pageBottomPadding + if (spec.isNarrow) 18.dp else 22.dp
                                        } else {
                                            spec.pageBottomPadding + 24.dp
                                        }
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(items = openedRecords, key = { it.recordId }) { record ->
                                        val selected = selectedAlbumRecordIds.contains(record.recordId)
                                        RecordCard(
                                            record = record,
                                            palette = albumPalette,
                                            adaptive = albumAdaptive,
                                            selected = selected,
                                            allowImageLoading = true,
                                            showShadow = false,
                                            darkCardBackgroundOverride = noMemoCardSurfaceColor(
                                                true,
                                                albumPalette.glassFill.copy(alpha = 0.92f)
                                            ),
                                            onClick = {
                                                if (albumSelectionModeActive) {
                                                    selectedAlbumRecordIds = if (selected) {
                                                        selectedAlbumRecordIds - record.recordId
                                                    } else {
                                                        selectedAlbumRecordIds + record.recordId
                                                    }
                                                    showRemoveFromAlbumConfirm = false
                                                    showDeleteSelectedConfirm = false
                                                } else {
                                                    onOpenDetail(record)
                                                }
                                            },
                                            onLongPress = {
                                                detailMoreExpanded = false
                                                albumSelectionModeActive = true
                                                selectedAlbumRecordIds = if (selected) {
                                                    selectedAlbumRecordIds - record.recordId
                                                } else {
                                                    selectedAlbumRecordIds + record.recordId
                                                }
                                                showRemoveFromAlbumConfirm = false
                                                showDeleteSelectedConfirm = false
                                            }
                                        )
                                    }
                                }
                            }
                            if (openedRecords.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NoMemoEmptyState(
                                        iconRes = R.drawable.ic_nm_memory,
                                        title = "分组里还没有记忆",
                                        subtitle = "点击右上角新增记忆"
                                    )
                                }
                            }
                        }
                    }

                    if (openedAlbum == null) {
                        LiquidGlassDock(
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
                                ),
                            sharedBackdrop = backdrop  // 传入共享的 backdrop，实现真实玻璃效果
                        )
                    } else if (albumSelectionModeActive && selectedAlbumRecords.isNotEmpty()) {
                        NoMemoSelectionActionDock(
                            selectedRecords = selectedAlbumRecords,
                            archiveTextOverride = if (allOpenedRecordsSelected) "全部移出" else "移出",
                            onArchiveClick = { showRemoveFromAlbumConfirm = true },
                            onDeleteClick = { showDeleteSelectedConfirm = true },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(
                                    start = spec.pageHorizontalPadding,
                                    end = spec.pageHorizontalPadding,
                                    bottom = if (spec.isNarrow) 18.dp else 22.dp
                                )
                        )
                    }

                    NoMemoAnchoredMenu(
                        expanded = openedAlbum == null && groupListMoreExpanded,
                        onDismissRequest = { groupListMoreExpanded = false },
                        anchorBounds = groupListMoreAnchorBounds,
                        actions = listOf(
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_add,
                                label = "新增分组",
                                onClick = {
                                    groupListMoreExpanded = false
                                    showCreateAlbumDialog = true
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_settings,
                                label = stringResource(R.string.action_settings),
                                onClick = {
                                    groupListMoreExpanded = false
                                    onOpenSettings()
                                }
                            )
                        )
                    )

                    NoMemoAnchoredMenu(
                        expanded = openedAlbum != null && detailMoreExpanded,
                        onDismissRequest = { detailMoreExpanded = false },
                        anchorBounds = detailMoreAnchorBounds,
                        actions = listOf(
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_sheet_select_all,
                                label = "全选",
                                onClick = {
                                    detailMoreExpanded = false
                                    if (openedRecords.isNotEmpty()) {
                                        albumSelectionModeActive = true
                                        selectedAlbumRecordIds = openedRecords.map { it.recordId }.toSet()
                                        showRemoveFromAlbumConfirm = false
                                        showDeleteSelectedConfirm = false
                                    } else {
                                        albumSelectionModeActive = false
                                        selectedAlbumRecordIds = emptySet()
                                    }
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_add,
                                label = "新增记忆",
                                onClick = {
                                    detailMoreExpanded = false
                                    selectedExistingRecordIds = emptySet()
                                    addExistingSearchQuery = ""
                                    showAddExistingSheet = true
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_edit,
                                label = "编辑分组",
                                onClick = {
                                    detailMoreExpanded = false
                                    openedAlbum?.let { album ->
                                        editingAlbumId = album.albumId
                                        albumNameInput = album.name
                                        albumDescriptionInput = album.description
                                        showEditAlbumDialog = true
                                    }
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_delete,
                                label = "删除分组",
                                destructive = true,
                                onClick = {
                                    detailMoreExpanded = false
                                    showDeleteAlbumConfirm = true
                                }
                            )
                        )
                    )

                    if (showCreateAlbumDialog) {
                        GroupEditAlbumSheet(
                            title = "新建分组",
                            albumName = albumNameInput,
                            albumDescription = albumDescriptionInput,
                            onNameChange = { albumNameInput = it },
                            onDescriptionChange = { albumDescriptionInput = it },
                            onDismiss = {
                                showCreateAlbumDialog = false
                                albumNameInput = ""
                                albumDescriptionInput = ""
                            },
                            onConfirm = {
                                val finalName = albumNameInput.trim()
                                if (finalName.isBlank()) {
                                    Toast.makeText(albumContext, "请输入分组名", Toast.LENGTH_SHORT).show()
                                    return@GroupEditAlbumSheet false
                                }
                                albumStore.addAlbum(finalName, albumDescriptionInput)
                                albumList = albumStore.loadAlbums()
                                Toast.makeText(albumContext, "分组已创建", Toast.LENGTH_SHORT).show()
                                true
                            }
                        )
                    }

                    if (showAddExistingSheet && openedAlbum != null) {
                        GroupAddExistingMemorySheet(
                            records = filteredExistingRecords,
                            selectedRecordIds = selectedExistingRecordIds,
                            searchQuery = addExistingSearchQuery,
                            onSearchQueryChange = { addExistingSearchQuery = it },
                            onToggleRecord = { recordId ->
                                selectedExistingRecordIds = if (selectedExistingRecordIds.contains(recordId)) {
                                    selectedExistingRecordIds - recordId
                                } else {
                                    selectedExistingRecordIds + recordId
                                }
                            },
                            onDismiss = {
                                showAddExistingSheet = false
                                selectedExistingRecordIds = emptySet()
                                addExistingSearchQuery = ""
                            },
                            onConfirm = {
                                if (selectedExistingRecordIds.isEmpty()) {
                                    Toast.makeText(albumContext, "请先选择记忆", Toast.LENGTH_SHORT).show()
                                    return@GroupAddExistingMemorySheet false
                                }
                                val targetAlbumId = openedAlbumId
                                if (targetAlbumId.isNullOrBlank()) {
                                    Toast.makeText(albumContext, "分组不存在，请重试", Toast.LENGTH_SHORT).show()
                                    return@GroupAddExistingMemorySheet false
                                }
                                val added = albumStore.addRecordIds(targetAlbumId, selectedExistingRecordIds)
                                albumList = albumStore.loadAlbums()
                                if (added) {
                                    Toast.makeText(albumContext, "已添加到分组", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(albumContext, "未添加成功，请重试", Toast.LENGTH_SHORT).show()
                                    return@GroupAddExistingMemorySheet false
                                }
                                true
                            }
                        )
                    }

                    if (showEditAlbumDialog && openedAlbum != null) {
                        GroupEditAlbumSheet(
                            title = "编辑分组",
                            albumName = albumNameInput,
                            albumDescription = albumDescriptionInput,
                            onNameChange = { albumNameInput = it },
                            onDescriptionChange = { albumDescriptionInput = it },
                            onDismiss = {
                                showEditAlbumDialog = false
                                editingAlbumId = null
                            },
                            onConfirm = {
                                val targetId = editingAlbumId ?: openedAlbum.albumId
                                val finalName = albumNameInput.trim()
                                if (finalName.isBlank()) {
                                    Toast.makeText(albumContext, "请输入分组名", Toast.LENGTH_SHORT).show()
                                    return@GroupEditAlbumSheet false
                                }
                                if (albumStore.updateAlbum(targetId, finalName, albumDescriptionInput)) {
                                    albumList = albumStore.loadAlbums()
                                    Toast.makeText(albumContext, "分组已更新", Toast.LENGTH_SHORT).show()
                                }
                                true
                            }
                        )
                    }

                    if (showDeleteAlbumConfirm && openedAlbum != null) {
                        NoMemoDeleteConfirmDialog(
                            title = "删除分组",
                            message = "删除后将移除这个分组，但不会删除其中的记忆。确定继续吗？",
                            onConfirm = {
                                val targetAlbum = openedAlbum
                                val deleted = albumStore.deleteAlbum(targetAlbum.albumId)
                                showDeleteAlbumConfirm = false
                                if (deleted) {
                                    albumList = albumStore.loadAlbums()
                                    Toast.makeText(albumContext, "分组已删除", Toast.LENGTH_SHORT).show()
                                    if (openedAsStandaloneDetail) {
                                        onCloseAlbumDetail()
                                    } else {
                                        openedAlbumId = null
                                    }
                                } else {
                                    Toast.makeText(albumContext, "删除失败，请重试", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDismiss = { showDeleteAlbumConfirm = false }
                        )
                    }

                    if (showRemoveFromAlbumConfirm && openedAlbum != null && selectedAlbumRecords.isNotEmpty()) {
                        val removingAll = allOpenedRecordsSelected
                        NoMemoConfirmDialog(
                            title = if (removingAll) "全部移出" else "移出记忆",
                            message = if (removingAll) {
                                "确定将该分组中的全部记忆移出吗？"
                            } else {
                                "确定将选中的 ${selectedAlbumRecordIds.size} 条记忆移出这个分组吗？"
                            },
                            confirmText = if (removingAll) "全部移出" else "移出",
                            dismissText = "取消",
                            destructive = true,
                            onConfirm = {
                                val removed = albumStore.removeRecordIds(openedAlbum.albumId, selectedAlbumRecordIds)
                                showRemoveFromAlbumConfirm = false
                                if (removed) {
                                    albumList = albumStore.loadAlbums()
                                    albumSelectionModeActive = false
                                    selectedAlbumRecordIds = emptySet()
                                    Toast.makeText(
                                        albumContext,
                                        if (removingAll) "已全部移出" else "已移出分组",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(albumContext, "移出失败，请重试", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDismiss = { showRemoveFromAlbumConfirm = false }
                        )
                    }

                    if (showDeleteSelectedConfirm && selectedAlbumRecords.isNotEmpty()) {
                        NoMemoDeleteConfirmDialog(
                            title = stringResource(R.string.delete_selected_title),
                            message = getString(R.string.delete_selected_batch_message, selectedAlbumRecordIds.size),
                            onConfirm = {
                                onDeleteRecords(selectedAlbumRecordIds)
                                showDeleteSelectedConfirm = false
                                albumSelectionModeActive = false
                                selectedAlbumRecordIds = emptySet()
                            },
                            onDismiss = { showDeleteSelectedConfirm = false }
                        )
                    }

                    if (showAddSheet) {
                        AddMemorySheet(
                            onDismiss = onDismissAddSheet
                        )
                    }
                }
            }
        }
        return
    }

    @Composable
    private fun GroupAlbumGridCard(
        album: GroupAlbumStore.GroupAlbum,
        compact: Boolean,
        memoryCount: Int,
        previewRecords: List<MemoryRecord>,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val dayText = remember(album.createdAt) {
            SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(album.createdAt))
        }
        val cardShape = RoundedCornerShape(if (compact) 24.dp else 26.dp)

        PressScaleBox(onClick = onClick, modifier = modifier) {
            Card(
                shape = cardShape,
                colors = CardDefaults.cardColors(
                    containerColor = noMemoCardSurfaceColor(
                        isDark = isDark,
                        lightColor = Color.White.copy(alpha = 0.995f)
                    )
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isDark) 0.dp else 2.dp
                )
            ) {
                GroupAlbumCoverCollage(
                    albumId = album.albumId,
                    albumName = album.name,
                    previewRecords = previewRecords,
                    memoryCount = memoryCount,
                    dayText = dayText,
                    compact = compact,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }

    @Composable
    private fun GroupAlbumCoverCollage(
        albumId: String,
        albumName: String,
        previewRecords: List<MemoryRecord>,
        memoryCount: Int,
        dayText: String,
        compact: Boolean
        ,
        modifier: Modifier = Modifier
    ) {
        val isDark = isSystemInDarkTheme()
        val palette = rememberNoMemoPalette()
        val coverCorner = if (compact) 18.dp else 20.dp
        val tileCorner = if (compact) 15.dp else 16.dp
        val coverHeight = if (compact) 174.dp else 194.dp
        val tileGap = 6.dp
        val collagePadding = if (compact) 8.dp else 9.dp

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(coverHeight)
                .clip(RoundedCornerShape(coverCorner))
                .background(groupAlbumCoverBrush(albumId, isDark))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                    .width(if (compact) 8.dp else 9.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDark) {
                                listOf(
                                    Color.White.copy(alpha = 0.14f),
                                    Color.White.copy(alpha = 0.04f),
                                    Color.Black.copy(alpha = 0.18f)
                                )
                            } else {
                                listOf(
                                    Color.White.copy(alpha = 0.52f),
                                    Color.White.copy(alpha = 0.14f),
                                    Color.Black.copy(alpha = 0.10f)
                                )
                            }
                        )
                    )
            )

            GroupAlbumPaperTexture(
                isDark = isDark,
                modifier = Modifier.matchParentSize()
            )

            if (previewRecords.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 18.dp, top = 18.dp)
                        .fillMaxHeight(0.76f)
                        .fillMaxWidth(0.42f)
                        .graphicsLayer {
                            rotationZ = -4f
                        }
                        .clip(RoundedCornerShape(tileCorner + 2.dp))
                        .background(
                            if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.32f)
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp, bottom = 18.dp)
                        .fillMaxHeight(0.58f)
                        .fillMaxWidth(0.30f)
                        .graphicsLayer {
                            rotationZ = 5f
                        }
                        .clip(RoundedCornerShape(tileCorner + 1.dp))
                        .background(
                            if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.24f)
                        )
                )
            }

            if (previewRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(groupAlbumCoverBrush(albumId, isDark))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_nm_group),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = if (isDark) 0.92f else 0.86f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(if (compact) 28.dp else 32.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(collagePadding),
                    horizontalArrangement = Arrangement.spacedBy(tileGap)
                ) {
                    GroupAlbumCoverTile(
                        record = previewRecords.getOrNull(0),
                        cornerRadius = tileCorner,
                        rotationZ = -2.8f,
                        modifier = Modifier
                            .weight(1.12f)
                            .fillMaxHeight()
                    )
                    Column(
                        modifier = Modifier
                            .weight(0.88f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(tileGap)
                    ) {
                        GroupAlbumCoverTile(
                            record = previewRecords.getOrNull(1),
                            cornerRadius = tileCorner,
                            rotationZ = 2.2f,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                        GroupAlbumCoverTile(
                            record = previewRecords.getOrNull(2),
                            cornerRadius = tileCorner,
                            rotationZ = 1.2f,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = if (isDark) 0.18f else 0.12f),
                                Color.Black.copy(alpha = if (isDark) 0.48f else 0.34f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isDark) {
                            Color.Black.copy(alpha = 0.28f)
                        } else {
                            Color.White.copy(alpha = 0.64f)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.42f),
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${memoryCount}条",
                    color = if (isDark) Color.White.copy(alpha = 0.95f) else palette.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 18.dp, end = 18.dp, bottom = 16.dp)
            ) {
                GroupAlbumFoilTitle(
                    text = albumName,
                    compact = compact
                )
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${memoryCount}条记忆",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.46f))
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = dayText,
                        color = Color.White.copy(alpha = 0.74f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    private fun GroupAlbumFoilTitle(
        text: String,
        compact: Boolean,
        modifier: Modifier = Modifier
    ) {
        val titleSize = if (compact) 18.sp else 19.sp
        Box(modifier = modifier) {
            Text(
                text = text,
                color = Color.Black.copy(alpha = 0.16f),
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.offset(x = 0.6.dp, y = 0.8.dp)
            )
            Text(
                text = text,
                color = Color.White.copy(alpha = 0.34f),
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.offset(x = (-0.4).dp, y = (-0.4).dp)
            )
            Text(
                text = text,
                color = Color(0xFFF6F7FB).copy(alpha = 0.96f),
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White.copy(alpha = 0.18f),
                        offset = Offset(0f, 0f),
                        blurRadius = 7f
                    )
                )
            )
        }
    }

    @Composable
    private fun GroupAlbumPaperTexture(
        isDark: Boolean,
        modifier: Modifier = Modifier
    ) {
        val verticalLineColor = if (isDark) {
            Color.White.copy(alpha = 0.026f)
        } else {
            Color.White.copy(alpha = 0.18f)
        }
        val diagonalLineColor = if (isDark) {
            Color.Black.copy(alpha = 0.12f)
        } else {
            Color(0x14000000)
        }
        val topSheen = if (isDark) {
            Color.White.copy(alpha = 0.055f)
        } else {
            Color.White.copy(alpha = 0.32f)
        }
        val bottomShade = if (isDark) {
            Color.Black.copy(alpha = 0.16f)
        } else {
            Color(0x12000000)
        }

        Box(
            modifier = modifier.drawWithCache {
                val verticalSpacing = 18.dp.toPx()
                val diagonalSpacing = 46.dp.toPx()
                val strokeWidth = 1.dp.toPx()

                onDrawWithContent {
                    drawContent()

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                topSheen,
                                Color.Transparent,
                                bottomShade
                            )
                        )
                    )

                    var x = 0f
                    while (x <= size.width) {
                        drawLine(
                            color = verticalLineColor,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = strokeWidth
                        )
                        x += verticalSpacing
                    }

                    var diagonalX = -size.height
                    while (diagonalX <= size.width) {
                        drawLine(
                            color = diagonalLineColor,
                            start = Offset(diagonalX, size.height),
                            end = Offset(diagonalX + size.height * 0.62f, 0f),
                            strokeWidth = strokeWidth
                        )
                        diagonalX += diagonalSpacing
                    }
                }
            }
        )
    }

    @Composable
    private fun GroupAlbumCoverTile(
        record: MemoryRecord?,
        cornerRadius: Dp,
        rotationZ: Float,
        modifier: Modifier = Modifier
    ) {
        val isDark = isSystemInDarkTheme()
        val palette = rememberNoMemoPalette()
        val frameSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f))
        } else {
            Color.White.copy(alpha = 0.97f)
        }
        val frameBorder = if (isDark) {
            Color.White.copy(alpha = 0.08f)
        } else {
            palette.glassStroke.copy(alpha = 0.78f)
        }
        val fallbackBackground = if (isDark) {
            Color.White.copy(alpha = 0.08f)
        } else {
            Color(0xFFF4F6FA)
        }
        val innerCorner = (cornerRadius - 3.dp).coerceAtLeast(10.dp)

        BoxWithConstraints(
            modifier = modifier
                .graphicsLayer {
                    this.rotationZ = rotationZ
                }
                .shadow(
                    elevation = if (isDark) 6.dp else 10.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    clip = false
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(frameSurface)
                .border(
                    width = 1.dp,
                    color = frameBorder,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(4.dp)
        ) {
            val currentRecord = record
            val contentWidth = maxWidth - 8.dp
            val contentHeight = maxHeight - 8.dp
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(innerCorner))
            ) {
                if (currentRecord?.imageUri?.isNullOrBlank() == false) {
                    MemoryThumbnail(
                        uriString = currentRecord.imageUri.orEmpty(),
                        width = contentWidth,
                        height = contentHeight,
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = fallbackBackground,
                        cornerRadius = innerCorner
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(groupAlbumFallbackTileBrush(currentRecord?.categoryCode, isDark))
                    )
                    Text(
                        text = groupAlbumFallbackTileLabel(currentRecord),
                        color = Color.White.copy(alpha = 0.96f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 10.dp, vertical = 9.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = if (isDark) 0.10f else 0.08f)
                                )
                            )
                        )
                )
            }
        }
    }

    @Composable
    private fun BoxScope.GroupAddExistingMemorySheet(
        records: List<MemoryRecord>,
        selectedRecordIds: Set<String>,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        onToggleRecord: (String) -> Unit,
        onDismiss: () -> Unit,
        onConfirm: () -> Boolean
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val panelSurface = if (isDark) {
            Color(0xFF121316)
        } else {
            Color(0xFFF5F6F8)
        }
        val searchSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f))
        } else {
            Color.White.copy(alpha = 0.995f)
        }
        val bodyHeight = if (adaptive.isNarrow) 620.dp else 700.dp
        var visible by remember { mutableStateOf(false) }
        var dismissCommitted by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }

        LaunchedEffect(visible) {
            if (!visible && !dismissCommitted) {
                dismissCommitted = true
                delay(220)
                onDismiss()
            }
        }

        val requestDismiss = remember {
            { visible = false }
        }
        val requestConfirm = remember(onConfirm) {
            {
                if (onConfirm()) {
                    visible = false
                }
            }
        }

        BackHandler(enabled = visible) {
            requestDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(20f)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 180)),
                exit = fadeOut(animationSpec = tween(durationMillis = 180))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (isDark) 0.56f else 0.28f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = requestDismiss
                        )
                )
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 260)
                ) + fadeIn(animationSpec = tween(durationMillis = 180)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 220)
                ) + fadeOut(animationSpec = tween(durationMillis = 150)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (adaptive.isNarrow) 18.dp else 24.dp,
                            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                        ),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = panelSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(bodyHeight)
                            .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 0.dp)
                    ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(56.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (isDark) {
                                    Color.White.copy(alpha = 0.16f)
                                } else {
                                    palette.glassStroke.copy(alpha = 0.92f)
                                }
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassIconCircleButton(
                            iconRes = R.drawable.ic_sheet_close,
                            contentDescription = stringResource(R.string.cancel),
                            onClick = requestDismiss,
                            size = adaptive.topActionButtonSize
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "添加记忆",
                                color = palette.textPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                        GlassIconCircleButton(
                            iconRes = R.drawable.ic_sheet_check,
                            contentDescription = stringResource(R.string.confirm),
                            onClick = requestConfirm,
                            size = adaptive.topActionButtonSize
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = searchSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_search),
                                contentDescription = stringResource(R.string.action_search),
                                tint = palette.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                textStyle = TextStyle(
                                    color = palette.textPrimary,
                                    fontSize = 15.sp
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 10.dp)
                            ) { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (searchQuery.isBlank()) {
                                        Text(
                                            text = stringResource(R.string.search_placeholder),
                                            color = palette.textTertiary,
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                            if (searchQuery.isNotBlank()) {
                                PressScaleBox(
                                    onClick = { onSearchQueryChange("") }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_sheet_close),
                                        contentDescription = stringResource(R.string.cancel),
                                        tint = palette.textTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            NoMemoEmptyState(
                                iconRes = R.drawable.ic_nm_memory,
                                title = "暂无可添加的记忆"
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                top = 0.dp,
                                bottom = adaptive.pageBottomPadding + 18.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(records, key = { it.recordId }) { record ->
                                val selected = selectedRecordIds.contains(record.recordId)
                                RecordCard(
                                    record = record,
                                    palette = palette,
                                    adaptive = adaptive,
                                    selected = selected,
                                    allowImageLoading = true,
                                    showShadow = false,
                                    darkCardBackgroundOverride = noMemoCardSurfaceColor(
                                        true,
                                        palette.glassFill.copy(alpha = 0.92f)
                                    ),
                                    onClick = { onToggleRecord(record.recordId) },
                                    onLongPress = { onToggleRecord(record.recordId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }

    @Composable
    private fun BoxScope.GroupEditAlbumSheet(
        title: String,
        albumName: String,
        albumDescription: String,
        onNameChange: (String) -> Unit,
        onDescriptionChange: (String) -> Unit,
        onDismiss: () -> Unit,
        onConfirm: () -> Boolean
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val panelSurface = if (isDark) {
            Color(0xFF121316)
        } else {
            Color(0xFFF5F6F8)
        }
        val inputSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f))
        } else {
            Color.White.copy(alpha = 0.995f)
        }
        val bodyHeight = if (adaptive.isNarrow) 450.dp else 500.dp
        val descriptionScrollState = rememberScrollState()
        var visible by remember { mutableStateOf(false) }
        var dismissCommitted by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }

        LaunchedEffect(visible) {
            if (!visible && !dismissCommitted) {
                dismissCommitted = true
                delay(220)
                onDismiss()
            }
        }

        val requestDismiss = remember {
            { visible = false }
        }
        val requestConfirm = remember(onConfirm) {
            {
                if (onConfirm()) {
                    visible = false
                }
            }
        }

        BackHandler(enabled = visible) {
            requestDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(20f)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 180)),
                exit = fadeOut(animationSpec = tween(durationMillis = 180))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = if (isDark) 0.56f else 0.28f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = requestDismiss
                        )
                )
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 260)
                ) + fadeIn(animationSpec = tween(durationMillis = 180)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 220)
                ) + fadeOut(animationSpec = tween(durationMillis = 150)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (adaptive.isNarrow) 18.dp else 24.dp,
                            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                        ),
                    shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                    colors = CardDefaults.cardColors(containerColor = panelSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(bodyHeight)
                            .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(56.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (isDark) {
                                        Color.White.copy(alpha = 0.16f)
                                    } else {
                                        palette.glassStroke.copy(alpha = 0.92f)
                                    }
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_close,
                                contentDescription = stringResource(R.string.cancel),
                                onClick = requestDismiss,
                                size = adaptive.topActionButtonSize
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = title,
                                    color = palette.textPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_check,
                                contentDescription = stringResource(R.string.confirm),
                                onClick = requestConfirm,
                                size = adaptive.topActionButtonSize
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Text(
                                text = "分组名称",
                                color = palette.textSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = inputSurface)
                            ) {
                                BasicTextField(
                                    value = albumName,
                                    onValueChange = onNameChange,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = palette.textPrimary,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .padding(horizontal = 14.dp)
                                ) { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        innerTextField()
                                    }
                                }
                            }

                            Text(
                                text = "分组描述",
                                color = palette.textSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = inputSurface)
                            ) {
                                BasicTextField(
                                    value = albumDescription,
                                    onValueChange = onDescriptionChange,
                                    textStyle = TextStyle(
                                        color = palette.textPrimary,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(148.dp)
                                        .padding(horizontal = 14.dp, vertical = 14.dp)
                                ) { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(descriptionScrollState),
                                        contentAlignment = Alignment.TopStart
                                    ) {
                                        innerTextField()
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(adaptive.pageBottomPadding + 6.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun GroupAlbumInputField(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        minHeight: Dp = 46.dp,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = noMemoCardSurfaceColor(
                    isDark = isDark,
                    lightColor = Color.White.copy(alpha = 0.995f)
                )
            ),
            border = BorderStroke(
                width = 1.dp,
                color = palette.glassStroke.copy(alpha = if (isDark) 0.44f else 0.18f)
            )
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = palette.textPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = palette.textTertiary,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        }
    }

    private fun groupAlbumCoverBrush(albumId: String, isDark: Boolean): Brush {
        val gradients = listOf(
            listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
            listOf(Color(0xFF22C55E), Color(0xFF15803D)),
            listOf(Color(0xFFEF4444), Color(0xFFB91C1C)),
            listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
            listOf(Color(0xFF14B8A6), Color(0xFF0F766E)),
            listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
        )
        val index = abs(albumId.hashCode()) % gradients.size
        val selected = gradients[index]
        val start = if (isDark) selected[0].copy(alpha = 0.48f) else selected[0].copy(alpha = 0.30f)
        val end = if (isDark) selected[1].copy(alpha = 0.34f) else selected[1].copy(alpha = 0.22f)
        return Brush.linearGradient(listOf(start, end))
    }

    private fun groupAlbumFallbackTileBrush(categoryCode: String?, isDark: Boolean): Brush {
        val colors = when (categoryCode) {
            CategoryCatalog.CODE_LIFE_PICKUP -> listOf(Color(0xFFFFB87A), Color(0xFFFF8A4D))
            CategoryCatalog.CODE_LIFE_DELIVERY -> listOf(Color(0xFF88B8FF), Color(0xFF4F8CFF))
            CategoryCatalog.CODE_LIFE_CARD -> listOf(Color(0xFFE0C18D), Color(0xFFC89A52))
            CategoryCatalog.CODE_LIFE_TICKET -> listOf(Color(0xFFB79BFF), Color(0xFF8A67FF))
            CategoryCatalog.CODE_WORK_TODO -> listOf(Color(0xFF7CE2B1), Color(0xFF43C785))
            CategoryCatalog.CODE_WORK_SCHEDULE -> listOf(Color(0xFF78A9FF), Color(0xFF4677F5))
            else -> listOf(Color(0xFFB5BDC9), Color(0xFF8F98A8))
        }
        val start = if (isDark) colors[0].copy(alpha = 0.78f) else colors[0].copy(alpha = 0.92f)
        val end = if (isDark) colors[1].copy(alpha = 0.88f) else colors[1].copy(alpha = 0.98f)
        return Brush.linearGradient(listOf(start, end))
    }

    private fun groupAlbumFallbackTileLabel(record: MemoryRecord?): String {
        if (record == null) return "记忆"
        val title = record.title.orEmpty().trim()
        if (title.isNotEmpty()) {
            return title.take(10)
        }
        val summary = record.summary.orEmpty().trim()
        if (summary.isNotEmpty()) {
            return summary.take(10)
        }
        val category = record.categoryName.orEmpty().trim()
        if (category.isNotEmpty()) {
            return category.take(4)
        }
        return "记忆"
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

private const val EXTRA_OPEN_ALBUM_ID = "extra_open_album_id"

private fun createGroupActivityIntent(
    context: android.content.Context,
    albumId: String? = null
): Intent {
    return Intent(context, GroupActivity::class.java).apply {
        albumId
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let { putExtra(EXTRA_OPEN_ALBUM_ID, it) }
    }
}
