package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArchivedMemoryActivity : BaseComposeActivity() {
    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, ArchivedMemoryActivity::class.java)
        }
    }

    private lateinit var memoryStore: MemoryStore
    private var records by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var showAddSheet by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null
    private var hasHandledInitialResume = false

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshRecords()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            ArchivedContent(
                records = records,
                hasLoadedRecords = hasLoadedRecords,
                showAddSheet = showAddSheet,
                onDismissAddSheet = { showAddSheet = false },
                onAddClick = { showAddSheet = true },
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onOpenSearch = { openSearchPage() },
                onArchiveRecords = { selectedRecords -> toggleArchive(selectedRecords) },
                onDeleteRecords = { recordIds -> deleteRecords(recordIds) },
                onClose = { finish() }
            )
        }
        refreshRecords()
    }

    override fun onResume() {
        super.onResume()
        if (!hasHandledInitialResume) {
            hasHandledInitialResume = true
            return
        }
        refreshRecords()
    }

    override fun onStart() {
        super.onStart()
        registerMemoryChangeReceiver()
    }

    override fun onStop() {
        unregisterMemoryChangeReceiver()
        super.onStop()
    }

    private fun refreshRecords() {
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            val loadedRecords = withContext(Dispatchers.IO) {
                val result = memoryStore.loadArchivedRecords()
                prewarmMemoryThumbnailCache(applicationContext, result)
                result
            }
            records = loadedRecords
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

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
    }

    private fun openSearchPage() {
        startActivity(SearchActivity.createArchivedIntent(this))
    }

    private fun toggleArchive(records: List<MemoryRecord>) {
        if (records.isEmpty()) {
            return
        }
        val nextArchived = records.any { !it.isArchived }
        records.forEach { record ->
            memoryStore.archiveRecord(record.recordId, nextArchived)
        }
        refreshRecords()
        Toast.makeText(
            this,
            if (nextArchived) R.string.archive_success else R.string.unarchive_success,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteRecords(recordIds: Set<String>) {
        if (recordIds.isEmpty()) {
            return
        }
        var deletedAny = false
        recordIds.forEach { recordId ->
            deletedAny = memoryStore.deleteRecord(recordId) || deletedAny
        }
        if (deletedAny) {
            refreshRecords()
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun ArchivedContent(
        records: List<MemoryRecord>,
        hasLoadedRecords: Boolean,
        showAddSheet: Boolean,
        onDismissAddSheet: () -> Unit,
        onAddClick: () -> Unit,
        onOpenDetail: (MemoryRecord) -> Unit,
        onOpenSearch: () -> Unit,
        onArchiveRecords: (List<MemoryRecord>) -> Unit,
        onDeleteRecords: (Set<String>) -> Unit,
        onClose: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val context = this@ArchivedMemoryActivity
        val listState = rememberLazyListState()
        var selectedRecordIds by remember { mutableStateOf(setOf<String>()) }
        var selectionModeActive by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var moreMenuAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var addSheetSearchQuery by remember { mutableStateOf("") }
        var selectedAddRecordIds by remember { mutableStateOf(setOf<String>()) }
        var addableRecords by remember { mutableStateOf<List<MemoryRecord>>(emptyList()) }

        val selectedRecords = remember(records, selectedRecordIds) {
            records.filter { selectedRecordIds.contains(it.recordId) }
        }
        val selectedAddRecords = remember(addableRecords, selectedAddRecordIds) {
            addableRecords.filter { selectedAddRecordIds.contains(it.recordId) }
        }
        val filteredAddableRecords by remember(addableRecords, addSheetSearchQuery) {
            derivedStateOf {
                val query = addSheetSearchQuery.trim()
                if (query.isBlank()) {
                    addableRecords
                } else {
                    addableRecords.filter { record ->
                        listOf(
                            record.title,
                            record.summary,
                            record.sourceText,
                            record.note,
                            record.categoryName
                        ).any { value ->
                            value.contains(query, ignoreCase = true)
                        }
                    }
                }
            }
        }
        val showCenteredEmptyState = hasLoadedRecords && records.isEmpty()
        val allVisibleRecordsSelected = records.isNotEmpty() &&
            records.all { selectedRecordIds.contains(it.recordId) }

        LaunchedEffect(showAddSheet) {
            if (showAddSheet) {
                val loaded = withContext(Dispatchers.IO) {
                    val result = memoryStore.loadActiveRecords()
                    prewarmMemoryThumbnailCache(applicationContext, result)
                    result
                }
                addableRecords = loaded
                selectedAddRecordIds = emptySet()
                addSheetSearchQuery = ""
            }
        }

        LaunchedEffect(records, selectedRecordIds) {
            val validIds = records.map { it.recordId }.toSet()
            val sanitized = selectedRecordIds.filterTo(linkedSetOf()) { validIds.contains(it) }.toSet()
            if (sanitized != selectedRecordIds) {
                selectedRecordIds = sanitized
            }
            if (sanitized.isEmpty()) {
                selectionModeActive = false
                showDeleteConfirm = false
            }
        }

        BackHandler(enabled = selectionModeActive) {
            selectionModeActive = false
            selectedRecordIds = emptySet()
            showDeleteConfirm = false
        }
        BackHandler(enabled = moreMenuExpanded) {
            moreMenuExpanded = false
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(modifier = Modifier.fillMaxSize()) {
                    val backdrop = rememberLayerBackdrop {
                        drawContent()
                    }

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
                        if (selectionModeActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .height(spec.topActionButtonSize)
                            ) {
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_close,
                                    contentDescription = stringResource(R.string.cancel),
                                    onClick = {
                                        selectionModeActive = false
                                        selectedRecordIds = emptySet()
                                        showDeleteConfirm = false
                                    },
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    size = spec.topActionButtonSize
                                )
                                Text(
                                    text = getString(R.string.selected_count_format, selectedRecords.size),
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                GlassIconCircleButton(
                                    iconRes = if (allVisibleRecordsSelected) {
                                        R.drawable.ic_sheet_deselect_all
                                    } else {
                                        R.drawable.ic_sheet_select_all
                                    },
                                    contentDescription = if (allVisibleRecordsSelected) "取消全选" else "全选",
                                    onClick = {
                                        selectedRecordIds = if (allVisibleRecordsSelected) {
                                            emptySet()
                                        } else {
                                            records.map { it.recordId }.toSet()
                                        }
                                        showDeleteConfirm = false
                                    },
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    size = spec.topActionButtonSize
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spec.topActionButtonSize)
                            ) {
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_back,
                                    contentDescription = stringResource(R.string.back),
                                    onClick = onClose,
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    size = spec.topActionButtonSize
                                )
                                Text(
                                    text = stringResource(R.string.archived_memory_page_title),
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                Row(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_nm_search,
                                        contentDescription = stringResource(R.string.action_search),
                                        onClick = {
                                            moreMenuExpanded = false
                                            onOpenSearch()
                                        },
                                        modifier = Modifier.padding(end = 10.dp),
                                        size = spec.topActionButtonSize
                                    )
                                    GlassIconCircleButton(
                                        iconRes = R.drawable.ic_nm_more,
                                        contentDescription = stringResource(R.string.action_more),
                                        onClick = { moreMenuExpanded = !moreMenuExpanded },
                                        size = spec.topActionButtonSize,
                                        onBoundsChanged = { moreMenuAnchorBounds = it }
                                    )
                                }
                            }
                        }

                        if (!hasLoadedRecords || records.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                state = listState,
                                contentPadding = PaddingValues(
                                    top = 14.dp,
                                    bottom = if (selectedRecords.isNotEmpty()) {
                                        spec.pageBottomPadding + if (spec.isNarrow) 18.dp else 22.dp
                                    } else {
                                        spec.pageBottomPadding + 20.dp
                                    }
                                ),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(
                                    items = records,
                                    key = { it.recordId },
                                    contentType = {
                                        if (it.imageUri.isNullOrBlank()) "record_plain" else "record_image"
                                    }
                                ) { record ->
                                    val selected = selectedRecordIds.contains(record.recordId)
                                    RecordCard(
                                        record = record,
                                        selected = selected,
                                        palette = palette,
                                        adaptive = adaptive,
                                        allowImageLoading = true,
                                        showShadow = false,
                                        darkCardBackgroundOverride = Color(0xFF1A1A1C),
                                        onClick = {
                                            when {
                                                selectionModeActive && selected -> {
                                                    selectedRecordIds = selectedRecordIds - record.recordId
                                                }
                                                selectionModeActive -> {
                                                    selectedRecordIds = selectedRecordIds + record.recordId
                                                }
                                                else -> onOpenDetail(record)
                                            }
                                        },
                                        onLongPress = {
                                            moreMenuExpanded = false
                                            selectionModeActive = true
                                            selectedRecordIds = if (selected) {
                                                selectedRecordIds - record.recordId
                                            } else {
                                                selectedRecordIds + record.recordId
                                            }
                                            showDeleteConfirm = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (showCenteredEmptyState) {
                        NoMemoEmptyState(
                            iconRes = R.drawable.ic_nm_memory,
                            title = stringResource(R.string.no_archived_records),
                            subtitle = stringResource(R.string.archived_memory_empty_subtitle),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = spec.pageHorizontalPadding)
                        )
                    }

                    if (selectionModeActive && selectedRecords.isNotEmpty()) {
                        NoMemoSelectionActionDock(
                            selectedRecords = selectedRecords,
                            onArchiveClick = {
                                onArchiveRecords(selectedRecords)
                                selectionModeActive = false
                                selectedRecordIds = emptySet()
                            },
                            onDeleteClick = { showDeleteConfirm = true },
                            allSelected = allVisibleRecordsSelected,
                            backdrop = backdrop,
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

                    if (showDeleteConfirm && selectedRecordIds.isNotEmpty()) {
                        NoMemoDeleteConfirmDialog(
                            title = stringResource(R.string.delete_selected_title),
                            message = getString(R.string.delete_selected_batch_message, selectedRecordIds.size),
                            onConfirm = {
                                onDeleteRecords(selectedRecordIds)
                                selectionModeActive = false
                                selectedRecordIds = emptySet()
                                showDeleteConfirm = false
                            },
                            onDismiss = { showDeleteConfirm = false }
                        )
                    }

                    NoMemoAnchoredMenu(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                        anchorBounds = moreMenuAnchorBounds,
                        actions = listOf(
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_sheet_select_all,
                                label = stringResource(R.string.action_select_all),
                                onClick = {
                                    moreMenuExpanded = false
                                    showDeleteConfirm = false
                                    if (records.isNotEmpty()) {
                                        selectionModeActive = true
                                        selectedRecordIds = records.map { it.recordId }.toSet()
                                    } else {
                                        selectionModeActive = false
                                        selectedRecordIds = emptySet()
                                    }
                                }
                            ),
                            NoMemoMenuActionItem(
                                iconRes = R.drawable.ic_nm_add,
                                label = stringResource(R.string.action_add_memory),
                                onClick = {
                                    moreMenuExpanded = false
                                    onAddClick()
                                }
                            )
                        )
                    )

                    if (showAddSheet) {
                        ArchivedAddExistingMemorySheet(
                            records = filteredAddableRecords,
                            selectedRecordIds = selectedAddRecordIds,
                            searchQuery = addSheetSearchQuery,
                            onSearchQueryChange = { addSheetSearchQuery = it },
                            onToggleRecord = { recordId ->
                                selectedAddRecordIds = if (selectedAddRecordIds.contains(recordId)) {
                                    selectedAddRecordIds - recordId
                                } else {
                                    selectedAddRecordIds + recordId
                                }
                            },
                            onDismiss = {
                                onDismissAddSheet()
                                selectedAddRecordIds = emptySet()
                                addSheetSearchQuery = ""
                            },
                            onConfirm = {
                                if (selectedAddRecords.isEmpty()) {
                                    Toast.makeText(context, "请先选择记忆", Toast.LENGTH_SHORT).show()
                                    return@ArchivedAddExistingMemorySheet false
                                }
                                onArchiveRecords(selectedAddRecords)
                                onDismissAddSheet()
                                selectedAddRecordIds = emptySet()
                                addSheetSearchQuery = ""
                                true
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun BoxScope.ArchivedAddExistingMemorySheet(
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
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
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
                            .height(bodyHeight)
                            .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(56.dp)
                                .height(5.dp)
                                .clip(NoMemoG2CapsuleShape)
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
                                    text = "添加到归档",
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
                                    title = "暂无可加入归档的记忆"
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
}
