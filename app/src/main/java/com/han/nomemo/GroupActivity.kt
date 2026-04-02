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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class GroupActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private var selectedCategoryCode by mutableStateOf<String?>(null)
    private var allRecords by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null

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
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onOpenMemory = { openMemoryPage() },
                onOpenReminder = { openReminderPage() },
                onOpenSettings = { openSettingsPage() },
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
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            val loadedRecords = withContext(Dispatchers.IO) {
                memoryStore.loadActiveRecords()
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

    private fun openSettingsPage() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
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
        hasLoadedRecords: Boolean,
        selectedCategoryCode: String?,
        onSelectCategory: (String?) -> Unit,
        onDeleteRecord: (MemoryRecord) -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenMemory: () -> Unit,
        onOpenReminder: () -> Unit,
        onOpenSettings: () -> Unit,
        showAddSheet: Boolean,
        onAddClick: () -> Unit,
        onDismissAddSheet: () -> Unit
    ) {
        val albumContext = LocalContext.current
        val albumStore = remember(albumContext) { GroupAlbumStore(albumContext) }
        val albumAdaptive = rememberNoMemoAdaptiveSpec()
        val albumPalette = rememberNoMemoPalette()
        val albumListState = rememberLazyListState()
        val albumDockHasUnderContent = rememberDockHasUnderContent(
            listState = albumListState,
            spec = albumAdaptive
        )
        var albumList by remember { mutableStateOf(albumStore.loadAlbums()) }
        var openedAlbumId by remember { mutableStateOf<String?>(null) }
        var showCreateAlbumDialog by remember { mutableStateOf(false) }
        var showAddExistingSheet by remember { mutableStateOf(false) }
        var selectedExistingRecordIds by remember { mutableStateOf<Set<String>>(emptySet()) }
        var addExistingSearchQuery by remember { mutableStateOf("") }
        var groupListSearchEnabled by remember { mutableStateOf(false) }
        var groupListSearchQuery by remember { mutableStateOf("") }
        var groupListMoreExpanded by remember { mutableStateOf(false) }
        var albumNameInput by remember { mutableStateOf("") }
        var albumDescriptionInput by remember { mutableStateOf("") }

        val albumColumns = if (albumAdaptive.widthClass == NoMemoWidthClass.EXPANDED) 3 else 2
        val filteredAlbumList = remember(albumList, groupListSearchQuery) {
            val query = groupListSearchQuery.trim().lowercase()
            if (query.isBlank()) {
                albumList
            } else {
                albumList.filter { album ->
                    album.name.lowercase().contains(query) ||
                        album.description.lowercase().contains(query)
                }
            }
        }
        val albumRows = remember(filteredAlbumList, albumColumns) { filteredAlbumList.chunked(albumColumns) }
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
        BackHandler(
            enabled = openedAlbum != null &&
                !showAddSheet &&
                !showCreateAlbumDialog &&
                !showAddExistingSheet
        ) {
            openedAlbumId = null
            resetDoubleBackExitState()
        }
        BackHandler(enabled = showAddExistingSheet) {
            showAddExistingSheet = false
            selectedExistingRecordIds = emptySet()
            addExistingSearchQuery = ""
        }
        BackHandler(
            enabled = openedAlbum == null &&
                (groupListSearchEnabled || groupListMoreExpanded) &&
                !showAddSheet &&
                !showCreateAlbumDialog &&
                !showAddExistingSheet
        ) {
            if (groupListMoreExpanded) {
                groupListMoreExpanded = false
            } else {
                groupListSearchEnabled = false
                groupListSearchQuery = ""
            }
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = albumAdaptive) { spec ->
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
                        if (openedAlbum == null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_nm_search,
                                    contentDescription = stringResource(R.string.action_search),
                                    onClick = {
                                        groupListSearchEnabled = true
                                        groupListMoreExpanded = false
                                    },
                                    modifier = Modifier.padding(end = 10.dp),
                                    size = spec.topActionButtonSize
                                )
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_nm_more,
                                    contentDescription = "更多",
                                    onClick = { groupListMoreExpanded = !groupListMoreExpanded },
                                    size = spec.topActionButtonSize
                                )
                            }
                            if (groupListSearchEnabled) {
                                NoMemoSearchBarCard(
                                    value = groupListSearchQuery,
                                    onValueChange = { groupListSearchQuery = it },
                                    onClose = {
                                        groupListSearchEnabled = false
                                        groupListSearchQuery = ""
                                    },
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.group_page_title),
                                    color = albumPalette.textPrimary,
                                    fontSize = spec.titleSize,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
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
                            } else if (filteredAlbumList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NoMemoEmptyState(
                                        iconRes = R.drawable.ic_nm_search,
                                        title = "没有匹配到分组",
                                        subtitle = "试试其他关键词"
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    state = albumListState,
                                    contentPadding = PaddingValues(
                                        top = 14.dp,
                                        bottom = spec.pageBottomPadding + 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                                    modifier = Modifier.weight(1f),
                                                    onClick = {
                                                        groupListMoreExpanded = false
                                                        openedAlbumId = album.albumId
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
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = openedAlbum.name,
                                    color = albumPalette.textPrimary,
                                    fontSize = if (spec.isNarrow) 22.sp else 24.sp,
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
                                        onClick = { openedAlbumId = null },
                                        size = spec.topActionButtonSize
                                    )
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_nm_add,
                                        contentDescription = "添加记忆",
                                        onClick = {
                                            selectedExistingRecordIds = emptySet()
                                            addExistingSearchQuery = ""
                                            showAddExistingSheet = true
                                        },
                                        size = spec.topActionButtonSize
                                    )
                                }
                            }

                            if (openedRecords.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    state = albumListState,
                                    contentPadding = PaddingValues(
                                        top = 14.dp,
                                        bottom = spec.pageBottomPadding + 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(items = openedRecords, key = { it.recordId }) { record ->
                                        RecordCard(
                                            record = record,
                                            palette = albumPalette,
                                            adaptive = albumAdaptive,
                                            allowImageLoading = true,
                                            showShadow = false,
                                            darkCardBackgroundOverride = Color(0xFF1A1A1C),
                                            onClick = { onOpenDetail(record) }
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
                                        subtitle = "点击右上角添加记忆"
                                    )
                                }
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
                        animateFabHalo = !albumListState.isScrollInProgress,
                        showEnhancedOutline = albumDockHasUnderContent,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                end = spec.pageHorizontalPadding,
                                bottom = if (spec.isNarrow) 10.dp else 14.dp
                            )
                    )

                    NoMemoMenuPopup(
                        expanded = openedAlbum == null && groupListMoreExpanded,
                        onDismissRequest = { groupListMoreExpanded = false },
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp) + spec.topActionButtonSize + 8.dp,
                                end = spec.pageHorizontalPadding
                            )
                            .offset(x = (-6).dp)
                    ) {
                        NoMemoActionMenuPanel(
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
                    }

                    if (showCreateAlbumDialog) {
                        AlertDialog(
                            onDismissRequest = { showCreateAlbumDialog = false },
                            title = {
                                Text(
                                    text = "新建分组",
                                    color = albumPalette.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    GroupAlbumInputField(
                                        value = albumNameInput,
                                        onValueChange = { albumNameInput = it },
                                        placeholder = "分组名称"
                                    )
                                    GroupAlbumInputField(
                                        value = albumDescriptionInput,
                                        onValueChange = { albumDescriptionInput = it },
                                        placeholder = "分组描述（可选）",
                                        minHeight = 88.dp
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    enabled = albumNameInput.trim().isNotEmpty(),
                                    onClick = {
                                        val finalName = albumNameInput.trim()
                                        if (finalName.isBlank()) {
                                            Toast.makeText(albumContext, "请输入分组名", Toast.LENGTH_SHORT).show()
                                        } else {
                                            albumStore.addAlbum(finalName, albumDescriptionInput)
                                            albumList = albumStore.loadAlbums()
                                            albumNameInput = ""
                                            albumDescriptionInput = ""
                                            showCreateAlbumDialog = false
                                            Toast.makeText(albumContext, "分组已创建", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text("创建")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showCreateAlbumDialog = false }) {
                                    Text("取消")
                                }
                            }
                        )
                    }

                    if (showAddExistingSheet && openedAlbum != null) {
                        GroupAddExistingMemorySheet(
                            visible = showAddExistingSheet,
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
                                    return@GroupAddExistingMemorySheet
                                }
                                if (albumStore.addRecordIds(openedAlbum.albumId, selectedExistingRecordIds)) {
                                    albumList = albumStore.loadAlbums()
                                    Toast.makeText(albumContext, "已添加到分组", Toast.LENGTH_SHORT).show()
                                }
                                showAddExistingSheet = false
                                selectedExistingRecordIds = emptySet()
                                addExistingSearchQuery = ""
                            }
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
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val dayText = remember(album.createdAt) {
            SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(album.createdAt))
        }

        PressScaleBox(onClick = onClick, modifier = modifier) {
            Card(
                shape = RoundedCornerShape(if (compact) 22.dp else 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = noMemoCardSurfaceColor(
                        isDark = isDark,
                        lightColor = Color.White.copy(alpha = 0.995f)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (compact) 78.dp else 94.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(groupAlbumCoverBrush(album.albumId, isDark))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_nm_group),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = if (isDark) 0.90f else 0.82f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        )
                        Text(
                            text = "${memoryCount}条记忆",
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 9.dp)
                        )
                    }

                    Text(
                        text = album.name,
                        color = palette.textPrimary,
                        fontSize = if (compact) 16.sp else 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    val descriptionText = album.description.ifBlank { "未填写分组描述" }
                    Text(
                        text = descriptionText,
                        color = if (album.description.isBlank()) {
                            palette.textTertiary
                        } else {
                            palette.textSecondary
                        },
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .height(if (compact) 38.dp else 40.dp)
                    )

                    Text(
                        text = "创建于$dayText",
                        color = palette.textTertiary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun BoxScope.GroupAddExistingMemorySheet(
        visible: Boolean,
        records: List<MemoryRecord>,
        selectedRecordIds: Set<String>,
        searchQuery: String,
        onSearchQueryChange: (String) -> Unit,
        onToggleRecord: (String) -> Unit,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val panelSurface = if (isDark) Color(0xFF121316) else palette.memoBgStart
        val searchSurface = if (isDark) Color(0xFF1A1A1C) else Color.White.copy(alpha = 0.995f)
        val bodyHeight = if (adaptive.isNarrow) 620.dp else 700.dp

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 180)),
            exit = fadeOut(animationSpec = tween(durationMillis = 180))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(20f)
                    .background(Color.Black.copy(alpha = if (isDark) 0.56f else 0.30f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
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
                .zIndex(21f)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(if (isDark) Color.White.copy(alpha = 0.16f) else Color(0x24000000))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassIconCircleButton(
                            iconRes = R.drawable.ic_sheet_close,
                            contentDescription = stringResource(R.string.cancel),
                            onClick = onDismiss,
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
                            onClick = onConfirm,
                            size = adaptive.topActionButtonSize
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = searchSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
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
                                title = "暂无可添加的记忆",
                                subtitle = "先在记忆页创建内容再添加到分组"
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                top = 4.dp,
                                bottom = adaptive.pageBottomPadding + 18.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(records, key = { it.recordId }) { record ->
                                val selected = selectedRecordIds.contains(record.recordId)
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    RecordCard(
                                        record = record,
                                        palette = palette,
                                        adaptive = adaptive,
                                        selected = false,
                                        allowImageLoading = true,
                                        showShadow = false,
                                        darkCardBackgroundOverride = Color(0xFF1A1A1C),
                                        onClick = { onToggleRecord(record.recordId) },
                                        onLongPress = { onToggleRecord(record.recordId) }
                                    )
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(top = 12.dp, end = 12.dp)
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(palette.accent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_sheet_check),
                                                contentDescription = null,
                                                tint = palette.onAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
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


