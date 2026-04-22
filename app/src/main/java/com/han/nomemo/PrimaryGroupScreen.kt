package com.han.nomemo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kyant.backdrop.backdrops.LayerBackdrop
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

@Composable
internal fun GroupPrimaryScreenRoute(
    backdrop: LayerBackdrop,
    isActive: Boolean,
    onPrimaryDockStateChanged: (Boolean, (() -> Unit)?) -> Unit,
    onPrimaryOverlayChanged: (PrimaryHostOverlay?) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAlbumDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? BaseComposeActivity
    val scope = rememberCoroutineScope()
    val memoryStore = remember(context) { MemoryStore(context) }
    val albumStore = remember(context) { GroupAlbumStore(context) }
    var allRecords by remember { mutableStateOf<List<MemoryRecord>>(emptyList()) }
    var albumList by remember { mutableStateOf(albumStore.loadAlbums()) }
    var hasLoadedRecords by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var refreshJob by remember { mutableStateOf<Job?>(null) }

    fun refreshContent() {
        refreshJob?.cancel()
        refreshJob = scope.launch {
            val loadedRecords = withContext(Dispatchers.IO) {
                val result = memoryStore.loadActiveRecords()
                prewarmMemoryThumbnailCache(context.applicationContext, result)
                val validRecordIds = result.map { it.recordId }.toSet()
                albumStore.pruneInvalidRecordIds(validRecordIds)
                result
            }
            allRecords = loadedRecords
            albumList = albumStore.loadAlbums()
            hasLoadedRecords = true
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            activity?.recreate()
        } else {
            refreshContent()
        }
    }

    DisposableEffect(activity, isActive) {
        if (activity == null || !isActive) {
            onDispose { }
        } else {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: android.content.Context?, intent: Intent?) {
                    refreshContent()
                }
            }
            ContextCompat.registerReceiver(
                activity,
                receiver,
                IntentFilter(MemoryStoreNotifier.ACTION_RECORDS_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose {
                activity.unregisterReceiver(receiver)
            }
        }
    }

    DisposableEffect(activity, isActive) {
        if (activity == null || !isActive) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    refreshContent()
                }
            }
            activity.lifecycle.addObserver(observer)
            onDispose {
                activity.lifecycle.removeObserver(observer)
            }
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            refreshContent()
        }
    }

    val adaptive = rememberNoMemoAdaptiveSpec()
    val palette = rememberNoMemoPalette()
    val groupListState = rememberLazyListState()
    var showCreateAlbumDialog by remember { mutableStateOf(false) }
    var showAddExistingSheet by remember { mutableStateOf(false) }
    var addExistingTargetAlbumId by remember { mutableStateOf<String?>(null) }
    var selectedExistingRecordIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var addExistingSearchQuery by remember { mutableStateOf("") }
    var groupListMoreExpanded by remember { mutableStateOf(false) }
    var groupListMoreAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
    var albumNameInput by remember { mutableStateOf("") }
    var albumDescriptionInput by remember { mutableStateOf("") }
    val recordById = remember(allRecords) { allRecords.associateBy { it.recordId } }
    val orderedAlbums = albumList
    val albumRecordsMap = remember(albumList, recordById) {
        albumList.associate { album ->
            album.albumId to album.recordIds.mapNotNull { recordById[it] }
        }
    }
    val addExistingTargetAlbum = remember(albumList, addExistingTargetAlbumId) {
        albumList.firstOrNull { it.albumId == addExistingTargetAlbumId }
    }
    val addExistingCurrentRecordIds = remember(addExistingTargetAlbum) {
        addExistingTargetAlbum?.recordIds?.toSet().orEmpty()
    }
    val availableExistingRecords = remember(allRecords, addExistingCurrentRecordIds) {
        allRecords.filterNot { addExistingCurrentRecordIds.contains(it.recordId) }
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
    val hasGroupHomeContent = orderedAlbums.isNotEmpty()
    val density = LocalDensity.current
    val groupHeaderCollapseDistancePx = with(density) { 84.dp.toPx() }
    val groupHeaderCollapseTarget by remember(
        hasGroupHomeContent,
        groupListState.firstVisibleItemIndex,
        groupListState.firstVisibleItemScrollOffset
    ) {
        derivedStateOf {
            if (!hasGroupHomeContent) {
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
        label = "primaryGroupHeaderCollapse"
    )
    val groupExpandedTitleAlpha = (1f - groupHeaderCollapseProgress).coerceIn(0f, 1f)
    val groupCollapsedTitleAlpha = groupHeaderCollapseProgress.coerceIn(0f, 1f)
    val groupExpandedTitleTranslateY =
        with(density) { (-20).dp.toPx() * groupHeaderCollapseProgress }
    val groupExpandedTitleMaxHeight = if (adaptive.isNarrow) 44.dp else 50.dp
    val groupExpandedTitleHeight by animateDpAsState(
        targetValue = lerp(groupExpandedTitleMaxHeight, 0.dp, groupHeaderCollapseProgress),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "primaryGroupExpandedTitleHeight"
    )
    val groupListSpacing = 14.dp
    val groupListTopPadding by animateDpAsState(
        targetValue = lerp(24.dp, 4.dp, groupHeaderCollapseProgress),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "primaryGroupListTopPadding"
    )
    val albumSortLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        albumList = albumStore.loadAlbums()
    }

    LaunchedEffect(isActive, showAddExistingSheet) {
        if (isActive) {
            onPrimaryDockStateChanged(!showAddExistingSheet) {
                showAddSheet = true
            }
        }
    }
    LaunchedEffect(
        isActive,
        showAddSheet,
        showCreateAlbumDialog,
        albumNameInput,
        albumDescriptionInput
    ) {
        if (isActive) {
            val overlay = when {
                showCreateAlbumDialog -> PrimaryHostOverlay.GroupCreateAlbum(
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
                            Toast.makeText(context, "请输入分组名称", Toast.LENGTH_SHORT).show()
                            false
                        } else {
                            albumStore.addAlbum(finalName, albumDescriptionInput)
                            albumList = albumStore.loadAlbums()
                            showCreateAlbumDialog = false
                            albumNameInput = ""
                            albumDescriptionInput = ""
                            Toast.makeText(context, "分组已创建", Toast.LENGTH_SHORT).show()
                            true
                        }
                    }
                )
                showAddSheet -> PrimaryHostOverlay.AddMemory(
                    onDismiss = { showAddSheet = false }
                )
                else -> null
            }
            onPrimaryOverlayChanged(overlay)
        }
    }

    fun dismissAddExistingSheet() {
        showAddExistingSheet = false
        addExistingTargetAlbumId = null
        selectedExistingRecordIds = emptySet()
        addExistingSearchQuery = ""
    }

    BackHandler(enabled = showAddExistingSheet) {
        dismissAddExistingSheet()
        activity?.clearDoubleBackExitState()
    }

    BackHandler(
        enabled = groupListMoreExpanded &&
            !showCreateAlbumDialog &&
            !showAddSheet &&
            !showAddExistingSheet
    ) {
        groupListMoreExpanded = false
        activity?.clearDoubleBackExitState()
    }

    NoMemoBackground {
        ResponsiveContentFrame(spec = adaptive) { spec ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .layerBackdrop(backdrop)
                        .statusBarsPadding()
                        .padding(
                            top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                            bottom = 0.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spec.topActionButtonSize)
                            .padding(horizontal = spec.pageHorizontalPadding)
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
                            color = palette.textPrimary,
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
                            .padding(horizontal = spec.pageHorizontalPadding)
                            .padding(top = 2.dp)
                            .graphicsLayer {
                                alpha = groupExpandedTitleAlpha
                                translationY = groupExpandedTitleTranslateY
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.group_page_title),
                            color = palette.textPrimary,
                            fontSize = spec.titleSize,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!hasLoadedRecords) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else if (orderedAlbums.isEmpty()) {
                        val dockHeight = if (spec.isNarrow) 56.dp else 60.dp
                        val dockOuterBottomPadding = if (spec.isNarrow) 10.dp else 14.dp
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(
                                    bottom = spec.pageBottomPadding +
                                            dockHeight +
                                            dockOuterBottomPadding +
                                            8.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            NoMemoEmptyState(
                                iconRes = R.drawable.ic_nm_group_dock,
                                title = "还没有分组",
                                subtitle = "点击右上角更多菜单新建分组"
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = groupListState,
                            contentPadding = PaddingValues(
                                top = groupListTopPadding,
                                bottom = spec.pageBottomPadding + 28.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(
                                items = orderedAlbums,
                                key = { it.albumId }
                            ) { album ->
                                Column {
                                    PrimaryGroupSectionHeader(
                                        title = album.name,
                                        modifier = Modifier.padding(horizontal = spec.pageHorizontalPadding),
                                        onClick = {
                                            groupListMoreExpanded = false
                                            onOpenAlbumDetail(album.albumId)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    PrimaryGroupDefaultPreviewStrip(
                                        records = albumRecordsMap[album.albumId].orEmpty(),
                                        compact = spec.widthClass == NoMemoWidthClass.COMPACT,
                                        contentHorizontalPadding = spec.pageHorizontalPadding + 2.dp,
                                        onAddClick = {
                                            groupListMoreExpanded = false
                                            addExistingTargetAlbumId = album.albumId
                                            selectedExistingRecordIds = emptySet()
                                            addExistingSearchQuery = ""
                                            showAddExistingSheet = true
                                        },
                                        onOpenRecord = { record ->
                                            groupListMoreExpanded = false
                                            context.startActivity(
                                                MemoryDetailActivity.createIntent(
                                                    context,
                                                    record.recordId
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PrimaryGroupCustomEntryChip(
                                        onClick = {
                                            groupListMoreExpanded = false
                                            albumSortLauncher.launch(
                                                GroupAlbumSortActivity.createIntent(context)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                NoMemoAnchoredMenu(
                    expanded = groupListMoreExpanded,
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
                            iconRes = R.drawable.ic_nm_memory,
                            label = stringResource(R.string.archived_memory_page_title),
                            onClick = {
                                groupListMoreExpanded = false
                                context.startActivity(ArchivedMemoryActivity.createIntent(context))
                            }
                        ),
                        NoMemoMenuActionItem(
                            iconRes = R.drawable.ic_nm_settings,
                            label = stringResource(R.string.action_settings),
                            onClick = {
                                groupListMoreExpanded = false
                                settingsLauncher.launch(Intent(context, SettingsActivity::class.java))
                            }
                        )
                    )
                )

                if (showAddExistingSheet && addExistingTargetAlbum != null) {
                    PrimaryGroupAddExistingMemorySheet(
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
                        onDismiss = { dismissAddExistingSheet() },
                        onConfirm = {
                            if (selectedExistingRecordIds.isEmpty()) {
                                Toast.makeText(context, "请先选择记忆", Toast.LENGTH_SHORT).show()
                                return@PrimaryGroupAddExistingMemorySheet false
                            }
                            val added = albumStore.addRecordIds(
                                addExistingTargetAlbum.albumId,
                                selectedExistingRecordIds
                            )
                            albumList = albumStore.loadAlbums()
                            if (added) {
                                Toast.makeText(context, "已添加到分组", Toast.LENGTH_SHORT).show()
                                true
                            } else {
                                Toast.makeText(context, "未添加成功，请重试", Toast.LENGTH_SHORT).show()
                                false
                            }
                        }
                    )
                }

            }
        }
    }
}

class GroupAlbumSortActivity : BaseComposeActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, GroupAlbumSortActivity::class.java)
        }
    }

    private lateinit var albumStore: GroupAlbumStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumStore = GroupAlbumStore(this)
        setContent {
            val initialAlbums = remember { albumStore.loadAlbums() }
            var albumList by remember { mutableStateOf(initialAlbums) }
            var draftIds by remember { mutableStateOf(initialAlbums.map { it.albumId }) }

            fun closePage() {
                val changed = albumStore.reorderAlbums(draftIds)
                if (changed) {
                    setResult(Activity.RESULT_OK)
                    albumList = albumStore.loadAlbums()
                }
                finish()
            }

            BackHandler {
                closePage()
            }

            PrimaryGroupSortAlbumsPage(
                albums = draftIds.mapNotNull { id ->
                    albumList.firstOrNull { it.albumId == id }
                },
                onDismiss = { closePage() },
                onAlbumOrderChange = { nextOrder ->
                    draftIds = nextOrder
                }
            )
        }
    }
}

@Composable
private fun PrimaryGroupSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        pressedScale = 1f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = palette.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_sheet_chevron_down),
                contentDescription = null,
                tint = palette.textPrimary.copy(alpha = 0.42f),
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer { rotationZ = -90f }
            )
        }
    }
}

@Composable
private fun PrimaryGroupDefaultPreviewStrip(
    records: List<MemoryRecord>,
    compact: Boolean,
    contentHorizontalPadding: Dp,
    onAddClick: () -> Unit,
    onOpenRecord: (MemoryRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = contentHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        records.forEach { record ->
            PrimaryGroupDefaultMemoryCard(
                record = record,
                compact = compact,
                onClick = { onOpenRecord(record) }
            )
        }
        PrimaryGroupDefaultEmptyTile(
            compact = compact,
            onClick = onAddClick
        )
    }
}

@Composable
private fun PrimaryGroupDefaultEmptyTile(
    compact: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val tileSize = if (compact) 152.dp else 168.dp
    val tileShape = noMemoG2RoundedShape(if (compact) 26.dp else 28.dp)
    val tileSurface = primaryGroupThemeSyncedSurface(
        palette = palette,
        isDark = isDark,
        defaultLight = Color.White.copy(alpha = 0.995f)
    )
    val plusSurface = primaryGroupThemeSyncedInsetSurface(palette, isDark)
    val interactionSource = remember { MutableInteractionSource() }
    val (tapHighlight, triggerHighlight) = rememberPrimaryGroupTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "primaryGroupEmptyTile"
    )
    PressScaleBox(
        onClick = {
            triggerHighlight()
            onClick()
        },
        pressedScale = 1f,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .size(tileSize)
                .clip(tileShape)
                .background(tileSurface)
                .background(tapHighlight),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 54.dp else 58.dp)
                    .clip(NoMemoG2CapsuleShape)
                    .background(plusSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_nm_add),
                    contentDescription = null,
                    tint = palette.textSecondary.copy(alpha = if (isDark) 0.9f else 0.92f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimaryGroupDefaultMemoryCard(
    record: MemoryRecord,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardSize = if (compact) 152.dp else 168.dp
    val cornerRadius = if (compact) 24.dp else 26.dp
    val title = remember(record.recordId, record.title, record.summary) {
        record.title.orEmpty().trim()
            .ifBlank { record.summary.orEmpty().trim() }
            .ifBlank { "未命名记忆" }
    }
    val categoryLabel = record.categoryName?.trim().takeUnless { it.isNullOrEmpty() } ?: "随手记"
    val fallbackBackground = if (isDark) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFF4F6FA)
    }
    val interactionSource = remember(record.recordId) { MutableInteractionSource() }
    val (tapHighlight, triggerHighlight) = rememberPrimaryGroupTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "primaryGroupMemoryCard_${record.recordId}"
    )

    PressScaleBox(
        onClick = {
            triggerHighlight()
            onClick()
        },
        modifier = modifier.size(cardSize),
        pressedScale = 1f,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(noMemoG2RoundedShape(cornerRadius))
                .background(primaryGroupAlbumFallbackTileBrush(record.categoryCode, isDark))
        ) {
            if (!record.imageUri.isNullOrBlank()) {
                MemoryThumbnail(
                    uriString = record.imageUri.orEmpty(),
                    width = cardSize,
                    height = cardSize,
                    modifier = Modifier.fillMaxSize(),
                    backgroundColor = fallbackBackground,
                    cornerRadius = cornerRadius
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = if (isDark) 0.26f else 0.18f),
                                Color.Black.copy(alpha = if (isDark) 0.54f else 0.44f)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(tapHighlight)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.98f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = categoryLabel,
                    color = Color.White.copy(alpha = 0.84f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimaryGroupCustomEntryChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val containerColor = primaryGroupThemeSyncedSurface(
        palette = palette,
        isDark = isDark,
        defaultLight = Color.White.copy(alpha = 0.995f)
    )
    val contentColor = palette.textPrimary.copy(alpha = 0.9f)
    val interactionSource = remember { MutableInteractionSource() }
    val (tapHighlight, triggerHighlight) = rememberPrimaryGroupTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "primaryGroupCustomEntryChip"
    )
    PressScaleBox(
        onClick = {
            triggerHighlight()
            onClick()
        },
        modifier = modifier,
        pressedScale = 1f,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier
                .clip(NoMemoG2CapsuleShape)
                .background(containerColor)
                .background(tapHighlight)
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "自定义",
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

private fun primaryGroupThemeSyncedSurface(
    palette: NoMemoPalette,
    isDark: Boolean,
    defaultLight: Color
): Color {
    return noMemoThemeSyncedContentSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = noMemoCardSurfaceColor(isDark),
        lightDefault = defaultLight
    )
}

private fun primaryGroupThemeSyncedInsetSurface(
    palette: NoMemoPalette,
    isDark: Boolean
): Color {
    return noMemoThemeSyncedInsetSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = Color.White.copy(alpha = 0.08f),
        lightDefault = Color(0xFFE1E4EA),
        darkAlpha = 0.72f,
        lightAlpha = 0.96f
    )
}

@Composable
private fun rememberPrimaryGroupTapHighlightColor(
    interactionSource: MutableInteractionSource,
    isDark: Boolean,
    label: String
): Pair<Color, () -> Unit> {
    val scope = rememberCoroutineScope()
    val pressed by interactionSource.collectIsPressedAsState()
    var holdHighlight by remember { mutableStateOf(false) }
    var holdJob by remember { mutableStateOf<Job?>(null) }

    val highlightActive = pressed || holdHighlight
    val highlightFactor by animateFloatAsState(
        targetValue = if (highlightActive) 1f else 0f,
        animationSpec = if (highlightActive) {
            tween(durationMillis = 75)
        } else {
            tween(durationMillis = 220, easing = FastOutSlowInEasing)
        },
        label = "${label}Factor"
    )

    val backgroundColor = if (isDark) {
        Color.White.copy(alpha = 0.055f * highlightFactor)
    } else {
        Color.Black.copy(alpha = 0.04f * highlightFactor)
    }

    val triggerHighlight = {
        holdJob?.cancel()
        holdHighlight = true
        holdJob = scope.launch {
            delay(150)
            holdHighlight = false
        }
    }
    return backgroundColor to triggerHighlight
}

@Composable
private fun PrimaryGroupAddExistingMemorySheet(
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
    val panelSurface = noMemoThemeSyncedSheetSurface(palette, isDark)
    val searchSurface = noMemoThemeSyncedContentSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f)),
        lightDefault = Color.White.copy(alpha = 0.995f)
    )
    val dragHandleColor = Color(0xFF8E8E93).copy(alpha = if (isDark) 0.72f else 0.68f)
    val bodyHeight = rememberNoMemoSheetHeight(
        compactPreferredHeight = 620.dp,
        regularPreferredHeight = 700.dp,
        compactScreenFraction = 0.86f,
        regularScreenFraction = 0.82f,
        minimumHeight = 340.dp
    )
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

    val tryDismiss = remember {
        {
            visible = false
            true
        }
    }
    val requestConfirm = remember(onConfirm) {
        {
            if (onConfirm()) {
                visible = false
            }
        }
    }
    val sheetDrag = rememberNoMemoSheetDragController(onDismissRequest = tryDismiss)

    BackHandler(enabled = visible) {
        tryDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(45f)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = fadeOut(animationSpec = tween(durationMillis = 180))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(
                            alpha = (if (isDark) 0.56f else 0.28f) * sheetDrag.scrimAlphaFraction
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { tryDismiss() }
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
                    .noMemoSheetDragOffset(sheetDrag)
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (adaptive.isNarrow) 18.dp else 24.dp,
                        shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp)
                    ),
                shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = panelSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = bodyHeight)
                        .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 0.dp)
                ) {
                    NoMemoSheetDragHandle(
                        color = dragHandleColor,
                        controller = sheetDrag,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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
                            onClick = { tryDismiss() },
                            size = adaptive.topActionButtonSize
                        )
                        Text(
                            text = "添加记忆",
                            color = palette.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
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
                        shape = noMemoG2RoundedShape(22.dp),
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
                                    onClick = { onSearchQueryChange("") },
                                    pressedScale = 1f
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
                                iconRes = R.drawable.ic_nm_memory_dock,
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
private fun PrimaryGroupSortAlbumsPage(
    albums: List<GroupAlbumStore.GroupAlbum>,
    onDismiss: () -> Unit,
    onAlbumOrderChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val adaptive = rememberNoMemoAdaptiveSpec()
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val rowHeight = 80.dp
    val rowHeightPx = remember(density) { with(density) { rowHeight.toPx() } }
    var draggingAlbumId by remember { mutableStateOf<String?>(null) }
    var draggingIndex by remember { mutableStateOf(-1) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val pageBackground = if (isDark) Color(0xFF101114) else Color(0xFFF7F5FA)
    val surface = if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f))
    } else {
        Color.White.copy(alpha = 0.995f)
    }
    val dividerColor = if (isDark) {
        Color.White.copy(alpha = 0.07f)
    } else {
        Color.Black.copy(alpha = 0.075f)
    }
    val sectionTextColor = if (isDark) {
        palette.textSecondary.copy(alpha = 0.86f)
    } else {
        Color(0xFF8C8A94)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(50f)
            .background(pageBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    start = adaptive.pageHorizontalPadding,
                    top = (adaptive.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                    end = adaptive.pageHorizontalPadding
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adaptive.topActionButtonSize)
            ) {
                GlassIconCircleButton(
                    iconRes = R.drawable.ic_sheet_back,
                    contentDescription = stringResource(R.string.back),
                    onClick = onDismiss,
                    size = adaptive.topActionButtonSize,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "分组排序",
                    color = palette.textPrimary,
                    fontSize = if (adaptive.isNarrow) 20.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
                contentPadding = PaddingValues(top = 24.dp, bottom = adaptive.pageBottomPadding + 28.dp)
            ) {
                item(key = "sort_section_label") {
                    Text(
                        text = "分组",
                        color = sectionTextColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
                item(key = "sort_album_list") {
                    PrimaryGroupSortSurfaceCard(
                        surface = surface,
                        borderColor = Color.Transparent,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            albums.forEachIndexed { index, album ->
                                key(album.albumId) {
                                    val isDragging = draggingAlbumId == album.albumId
                                    val latestAlbums by rememberUpdatedState(albums)
                                    val latestIndex by rememberUpdatedState(index)
                                    val dragScale by animateFloatAsState(
                                        targetValue = if (isDragging) 1.015f else 1f,
                                        animationSpec = tween(durationMillis = 160),
                                        label = "primaryGroupSortScale_${album.albumId}"
                                    )
                                    val dragBackground by animateColorAsState(
                                        targetValue = if (isDragging) {
                                            if (isDark) {
                                                Color.White.copy(alpha = 0.06f)
                                            } else {
                                                Color.Black.copy(alpha = 0.045f)
                                            }
                                        } else {
                                            Color.Transparent
                                        },
                                        animationSpec = tween(durationMillis = 150),
                                        label = "primaryGroupSortBg_${album.albumId}"
                                    )

                                    PrimaryGroupSortAlbumRow(
                                        album = album,
                                        rowHeight = rowHeight,
                                        titleColor = palette.textPrimary,
                                        subtitleColor = palette.textSecondary,
                                        backgroundColor = dragBackground,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .zIndex(if (isDragging) 1f else 0f)
                                            .graphicsLayer {
                                                translationY = if (isDragging) dragOffsetY else 0f
                                                scaleX = dragScale
                                                scaleY = dragScale
                                            }
                                            .pointerInput(album.albumId, rowHeightPx) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        draggingAlbumId = album.albumId
                                                        draggingIndex = latestIndex
                                                        dragOffsetY = 0f
                                                    },
                                                    onDragCancel = {
                                                        draggingAlbumId = null
                                                        draggingIndex = -1
                                                        dragOffsetY = 0f
                                                    },
                                                    onDragEnd = {
                                                        draggingAlbumId = null
                                                        draggingIndex = -1
                                                        dragOffsetY = 0f
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        if (draggingAlbumId != album.albumId) {
                                                            return@detectDragGesturesAfterLongPress
                                                        }
                                                        change.consume()
                                                        dragOffsetY += dragAmount.y
                                                        val currentAlbums = latestAlbums
                                                        val moveStep = when {
                                                            dragOffsetY > rowHeightPx / 2f &&
                                                                draggingIndex < currentAlbums.lastIndex -> 1
                                                            dragOffsetY < -rowHeightPx / 2f &&
                                                                draggingIndex > 0 -> -1
                                                            else -> 0
                                                        }
                                                        if (moveStep == 0) {
                                                            return@detectDragGesturesAfterLongPress
                                                        }
                                                        val targetIndex = draggingIndex + moveStep
                                                        val nextAlbums = currentAlbums.toMutableList().apply {
                                                            removeAt(draggingIndex)
                                                            add(targetIndex, album)
                                                        }
                                                        onAlbumOrderChange(nextAlbums.map { it.albumId })
                                                        draggingIndex = targetIndex
                                                        dragOffsetY -= moveStep * rowHeightPx
                                                    }
                                                )
                                            }
                                    )
                                    if (index != albums.lastIndex) {
                                        PrimaryGroupSortDivider(
                                            color = dividerColor,
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            thickness = 0.7.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item(key = "sort_hints") {
                    PrimaryGroupSortHints(
                        textColor = sectionTextColor,
                        modifier = Modifier.padding(start = 6.dp, top = 24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryGroupSortSurfaceCard(
    surface: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(noMemoG2RoundedShape(24.dp))
            .background(surface)
            .border(
                width = if (borderColor == Color.Transparent) 0.dp else 1.dp,
                color = borderColor,
                shape = noMemoG2RoundedShape(24.dp)
            )
    ) {
        content()
    }
}

@Composable
private fun PrimaryGroupSortAlbumRow(
    album: GroupAlbumStore.GroupAlbum,
    rowHeight: Dp,
    titleColor: Color,
    subtitleColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val subtitle = "${album.recordIds.size} 条记忆"
    Row(
        modifier = modifier
            .height(rowHeight)
            .background(backgroundColor)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_nm_group_dock),
            contentDescription = null,
            tint = titleColor.copy(alpha = 0.78f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = album.name,
                color = titleColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = subtitleColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            painter = painterResource(R.drawable.ic_dock_drag_handle),
            contentDescription = "拖拽排序",
            tint = subtitleColor.copy(alpha = 0.72f),
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun PrimaryGroupSortDivider(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 0.7.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Composable
private fun PrimaryGroupSortHints(
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        PrimaryGroupSortHintLine(
            text = "长按拖拽调整分组顺序",
            color = textColor
        )
        PrimaryGroupSortHintLine(
            text = "分组顺序会影响分组页面的显示顺序",
            color = textColor
        )
    }
}

@Composable
private fun PrimaryGroupSortHintLine(
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun PrimaryGroupCustomAlbumStrip(
    albums: List<GroupAlbumStore.GroupAlbum>,
    previewRecordsMap: Map<String, List<MemoryRecord>>,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onOpenAlbumDetail: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        albums.forEach { album ->
            PrimaryGroupCompactAlbumCard(
                album = album,
                previewRecords = previewRecordsMap[album.albumId].orEmpty(),
                compact = compact,
                onClick = { onOpenAlbumDetail(album.albumId) }
            )
        }
    }
}

@Composable
private fun PrimaryGroupCompactAlbumCard(
    album: GroupAlbumStore.GroupAlbum,
    previewRecords: List<MemoryRecord>,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val cardWidth = if (compact) 126.dp else 138.dp
    val cardShape = noMemoG2RoundedShape(if (compact) 24.dp else 26.dp)
    val coverShape = noMemoG2RoundedShape(if (compact) 20.dp else 22.dp)
    val coverHeight = if (compact) 96.dp else 106.dp
    val previewRecord = previewRecords.firstOrNull()
    val fallbackBackground = if (isDark) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFF4F6FA)
    }

    PressScaleBox(
        onClick = onClick,
        modifier = modifier.width(cardWidth),
        pressedScale = 1f
    ) {
        Card(
            shape = cardShape,
            colors = CardDefaults.cardColors(
                containerColor = noMemoCardSurfaceColor(
                    isDark = isDark,
                    lightColor = Color.White.copy(alpha = 0.995f)
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(coverHeight)
                        .clip(coverShape)
                        .background(primaryGroupAlbumCoverBrush(album.albumId, isDark))
                ) {
                    if (previewRecord?.imageUri?.isNullOrBlank() == false) {
                        MemoryThumbnail(
                            uriString = previewRecord.imageUri.orEmpty(),
                            width = cardWidth - 16.dp,
                            height = coverHeight,
                            modifier = Modifier.fillMaxSize(),
                            backgroundColor = fallbackBackground,
                            cornerRadius = if (compact) 20.dp else 22.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    previewRecord?.let {
                                        primaryGroupAlbumFallbackTileBrush(it.categoryCode, isDark)
                                    } ?: primaryGroupAlbumCoverBrush(album.albumId, isDark)
                                )
                        )
                        if (previewRecord == null) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_group),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(24.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = if (isDark) 0.12f else 0.08f)
                                    )
                                )
                            )
                    )
                    if (previewRecord != null && previewRecord.imageUri.isNullOrBlank()) {
                        Text(
                            text = primaryGroupAlbumFallbackTileLabel(previewRecord),
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
                }
                Text(
                    text = album.name,
                    color = palette.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp, top = 10.dp, end = 4.dp)
                )
                Text(
                    text = "${album.recordIds.size} 条记忆",
                    color = palette.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun PrimaryGroupAlbumGridCard(
    album: GroupAlbumStore.GroupAlbum,
    compact: Boolean,
    memoryCount: Int,
    previewRecords: List<MemoryRecord>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val dayText = remember(album.createdAt) {
        SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(album.createdAt))
    }
    val cardShape = noMemoG2RoundedShape(if (compact) 24.dp else 26.dp)

    PressScaleBox(
        onClick = onClick,
        modifier = modifier,
        pressedScale = 1f
    ) {
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
            PrimaryGroupAlbumCoverCollage(
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
private fun PrimaryGroupAlbumCoverCollage(
    albumId: String,
    albumName: String,
    previewRecords: List<MemoryRecord>,
    memoryCount: Int,
    dayText: String,
    compact: Boolean,
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
            .clip(noMemoG2RoundedShape(coverCorner))
            .background(primaryGroupAlbumCoverBrush(albumId, isDark))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                .width(if (compact) 8.dp else 9.dp)
                .fillMaxHeight()
                .clip(NoMemoG2CapsuleShape)
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

        PrimaryGroupAlbumPaperTexture(
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
                    .clip(noMemoG2RoundedShape(tileCorner + 2.dp))
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
                    .clip(noMemoG2RoundedShape(tileCorner + 1.dp))
                    .background(
                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.24f)
                    )
            )
        }

        if (previewRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(primaryGroupAlbumCoverBrush(albumId, isDark))
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
                PrimaryGroupAlbumCoverTile(
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
                    PrimaryGroupAlbumCoverTile(
                        record = previewRecords.getOrNull(1),
                        cornerRadius = tileCorner,
                        rotationZ = 2.2f,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    PrimaryGroupAlbumCoverTile(
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
                .clip(NoMemoG2CapsuleShape)
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
                    shape = NoMemoG2CapsuleShape
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
            PrimaryGroupAlbumFoilTitle(
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
                        .clip(NoMemoG2CapsuleShape)
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
private fun PrimaryGroupAlbumFoilTitle(
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
private fun PrimaryGroupAlbumPaperTexture(
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
private fun PrimaryGroupAlbumCoverTile(
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
                shape = noMemoG2RoundedShape(cornerRadius),
                clip = false
            )
            .clip(noMemoG2RoundedShape(cornerRadius))
            .background(frameSurface)
            .border(
                width = 1.dp,
                color = frameBorder,
                shape = noMemoG2RoundedShape(cornerRadius)
            )
            .padding(4.dp)
    ) {
        val currentRecord = record
        val contentWidth = maxWidth - 8.dp
        val contentHeight = maxHeight - 8.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(noMemoG2RoundedShape(innerCorner))
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
                        .background(primaryGroupAlbumFallbackTileBrush(currentRecord?.categoryCode, isDark))
                )
                Text(
                    text = primaryGroupAlbumFallbackTileLabel(currentRecord),
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
internal fun PrimaryGroupEditAlbumSheet(
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
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val panelSurface = noMemoThemeSyncedSheetSurface(palette, isDark)
    val inputSurface = noMemoThemeSyncedContentSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f)),
        lightDefault = Color.White.copy(alpha = 0.995f)
    )
    val dragHandleColor = if (isDark) {
        Color(0xFF8E8E93).copy(alpha = 0.72f)
    } else {
        Color(0xFF8E8E93).copy(alpha = 0.68f)
    }
    val bodyHeight = rememberNoMemoSheetHeight(
        compactPreferredHeight = 450.dp,
        regularPreferredHeight = 500.dp,
        compactScreenFraction = 0.76f,
        regularScreenFraction = 0.70f,
        minimumHeight = 300.dp
    )
    val descriptionScrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    var dismissCommitted by remember { mutableStateOf(false) }

    DisposableEffect(activity) {
        val window = activity?.window
        val previousSoftInputMode = window?.attributes?.softInputMode
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        onDispose {
            if (window != null && previousSoftInputMode != null) {
                window.setSoftInputMode(previousSoftInputMode)
            }
        }
    }

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

    val tryDismiss = remember {
        {
            visible = false
            true
        }
    }
    val requestConfirm = remember(onConfirm) {
        {
            if (onConfirm()) {
                visible = false
            }
        }
    }
    val sheetDrag = rememberNoMemoSheetDragController(onDismissRequest = tryDismiss)

    BackHandler(enabled = visible) {
        tryDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(40f)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = fadeOut(animationSpec = tween(durationMillis = 180))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = (if (isDark) 0.56f else 0.28f) * sheetDrag.scrimAlphaFraction))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { tryDismiss() }
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
                    .noMemoSheetDragOffset(sheetDrag)
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (adaptive.isNarrow) 18.dp else 24.dp,
                        shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp)
                    ),
                shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = panelSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = bodyHeight)
                        .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 0.dp)
                ) {
                    NoMemoSheetDragHandle(
                        color = dragHandleColor,
                        controller = sheetDrag,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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
                            onClick = { tryDismiss() },
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
                            shape = noMemoG2RoundedShape(22.dp),
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
                            shape = noMemoG2RoundedShape(22.dp),
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

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private fun primaryGroupAlbumCoverBrush(albumId: String, isDark: Boolean): Brush {
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

private fun primaryGroupAlbumFallbackTileBrush(categoryCode: String?, isDark: Boolean): Brush {
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

private fun primaryGroupAlbumFallbackTileLabel(record: MemoryRecord?): String {
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
