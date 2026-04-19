package com.han.nomemo

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    var groupListMoreExpanded by remember { mutableStateOf(false) }
    var groupListMoreAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
    var albumNameInput by remember { mutableStateOf("") }
    var albumDescriptionInput by remember { mutableStateOf("") }
    val albumColumns = if (adaptive.widthClass == NoMemoWidthClass.EXPANDED) 3 else 2
    val albumRows = remember(albumList, albumColumns) { albumList.chunked(albumColumns) }
    val recordById = remember(allRecords) { allRecords.associateBy { it.recordId } }
    val albumPreviewRecordsMap = remember(albumList, recordById) {
        albumList.associate { album ->
            album.albumId to album.recordIds.mapNotNull { recordById[it] }.take(3)
        }
    }
    val density = LocalDensity.current
    val groupHeaderCollapseDistancePx = with(density) { 84.dp.toPx() }
    val groupHeaderCollapseTarget by remember(
        albumList.isEmpty(),
        groupListState.firstVisibleItemIndex,
        groupListState.firstVisibleItemScrollOffset
    ) {
        derivedStateOf {
            if (albumList.isEmpty()) {
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
        targetValue = lerp(12.dp, 4.dp, groupHeaderCollapseProgress),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "primaryGroupListTopPadding"
    )

    LaunchedEffect(isActive) {
        if (isActive) {
            onPrimaryDockStateChanged(true) {
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

    BackHandler(enabled = groupListMoreExpanded && !showCreateAlbumDialog && !showAddSheet) {
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
                            start = spec.pageHorizontalPadding,
                            top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                            end = spec.pageHorizontalPadding,
                            bottom = 0.dp
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spec.topActionButtonSize)
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
                    } else if (albumList.isEmpty()) {
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
                                iconRes = R.drawable.ic_nm_group,
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
                                        PrimaryGroupAlbumGridCard(
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
