package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class MemoryDetailActivity : BaseComposeActivity() {
    companion object {
        private const val EXTRA_RECORD_ID = "extra_record_id"

        @JvmStatic
        fun createIntent(context: Context, recordId: String): Intent {
            return Intent(context, MemoryDetailActivity::class.java)
                .putExtra(EXTRA_RECORD_ID, recordId)
        }
    }

    private lateinit var memoryStore: MemoryStore
    private lateinit var aiMemoryService: AiMemoryService
    private var record by mutableStateOf<MemoryRecord?>(null)
    private var reanalyzing by mutableStateOf(false)
    private var memoryChangeRegistered = false
    private var loadJob: Job? = null

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadRecordOrFinish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        aiMemoryService = AiMemoryService(this)
        loadRecordOrFinish()
        val statusBarResourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeightPx = if (statusBarResourceId > 0) resources.getDimensionPixelSize(statusBarResourceId) else 0
        setContent {
            DetailContent(
                record = record,
                reanalyzing = reanalyzing,
                statusBarHeightPx = statusBarHeightPx,
                onBack = { finish() },
                onToggleArchive = { currentRecord -> toggleArchive(currentRecord) },
                onDelete = { currentRecord -> deleteRecord(currentRecord) },
                onReanalyze = { currentRecord -> reanalyzeRecord(currentRecord) }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecordOrFinish()
    }

    override fun onStart() {
        super.onStart()
        registerMemoryChangeReceiver()
    }

    override fun onStop() {
        unregisterMemoryChangeReceiver()
        super.onStop()
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

    private fun loadRecordOrFinish() {
        val recordId = intent.getStringExtra(EXTRA_RECORD_ID).orEmpty()
        loadJob?.cancel()
        loadJob = lifecycleScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                memoryStore.findRecordById(recordId)
            }
            record = loaded
            if (loaded == null) {
                finish()
            }
        }
    }

    private fun toggleArchive(currentRecord: MemoryRecord) {
        val nextArchived = !currentRecord.isArchived
        memoryStore.archiveRecord(currentRecord.recordId, nextArchived)
        record = currentRecord.withArchived(nextArchived)
        Toast.makeText(
            this,
            if (nextArchived) R.string.archive_success else R.string.unarchive_success,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteRecord(currentRecord: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(currentRecord.recordId)
        if (deleted) {
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveEditedRecord(
        currentRecord: MemoryRecord,
        title: String,
        summary: String,
        imageUri: String?
    ): Boolean {
        val trimmedDetail = summary.trim()
        val updated = MemoryRecord(
            currentRecord.recordId,
            currentRecord.createdAt,
            currentRecord.mode,
            title.trim().ifBlank { deriveTitle(currentRecord) },
            if (currentRecord.mode == MemoryRecord.MODE_AI) currentRecord.summary else trimmedDetail,
            currentRecord.sourceText,
            currentRecord.note,
            imageUri.orEmpty(),
            if (currentRecord.mode == MemoryRecord.MODE_AI) trimmedDetail else currentRecord.analysis,
            currentRecord.memory,
            currentRecord.engine,
            currentRecord.categoryGroupCode,
            currentRecord.categoryCode,
            currentRecord.categoryName,
            currentRecord.reminderAt,
            currentRecord.isReminderDone,
            currentRecord.isArchived
        )
        val saved = memoryStore.updateRecord(updated)
        if (saved) {
            record = updated
        }
        return saved
    }

    private fun reanalyzeRecord(currentRecord: MemoryRecord) {
        if (reanalyzing) {
            return
        }
        reanalyzing = true
        lifecycleScope.launch {
            val updated = withContext(Dispatchers.IO) {
                try {
                    val input = buildAiInput(currentRecord)
                    val imageUri = currentRecord.imageUri
                        ?.takeIf { it.isNotBlank() }
                        ?.let(Uri::parse)
                    val result = aiMemoryService.generateEnhancedMemory(
                        input,
                        imageUri,
                        buildEnhancedAiContext(currentRecord)
                    )
                    MemoryRecord(
                        currentRecord.recordId,
                        currentRecord.createdAt,
                        currentRecord.mode,
                        result.title.ifBlank { deriveTitle(currentRecord) },
                        result.summary.ifBlank { deriveSummary(currentRecord) },
                        currentRecord.sourceText,
                        currentRecord.note,
                        currentRecord.imageUri,
                        result.analysis,
                        result.memory,
                        result.engine,
                        currentRecord.categoryGroupCode,
                        currentRecord.categoryCode,
                        currentRecord.categoryName,
                        currentRecord.reminderAt,
                        currentRecord.isReminderDone,
                        currentRecord.isArchived
                    )
                } catch (_: Exception) {
                    null
                }
            }
            reanalyzing = false
            if (updated == null) {
                Toast.makeText(this@MemoryDetailActivity, "重新分析失败", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (!memoryStore.updateRecord(updated)) {
                Toast.makeText(this@MemoryDetailActivity, "重新分析失败", Toast.LENGTH_SHORT).show()
                return@launch
            }
            record = updated
            Toast.makeText(this@MemoryDetailActivity, "已重新分析", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildAiInput(record: MemoryRecord): String {
        val parts = buildList {
            record.sourceText?.trim()?.takeIf { it.isNotEmpty() }?.let { add(it) }
            record.note?.trim()?.takeIf { it.isNotEmpty() && it != record.sourceText }?.let { add(it) }
            record.memory?.trim()?.takeIf { it.isNotEmpty() && it != record.sourceText }?.let { add(it) }
        }
        return parts.joinToString("\n").ifBlank { deriveTitle(record) }
    }

    private fun buildEnhancedAiContext(record: MemoryRecord): String {
        val parts = buildList {
            add("当前分类: ${record.categoryName ?: "小记"}")
            record.title?.trim()?.takeIf { it.isNotEmpty() }?.let { add("现有标题: $it") }
            record.summary?.trim()?.takeIf { it.isNotEmpty() }?.let { add("现有摘要: $it") }
            record.analysis?.trim()?.takeIf { it.isNotEmpty() }?.let { add("现有分析: $it") }
            record.memory?.trim()?.takeIf { it.isNotEmpty() }?.let { add("现有记忆正文: $it") }
        }
        return parts.joinToString("\n")
    }

    @Composable
    private fun DetailContent(
        record: MemoryRecord?,
        reanalyzing: Boolean,
        statusBarHeightPx: Int,
        onBack: () -> Unit,
        onToggleArchive: (MemoryRecord) -> Unit,
        onDelete: (MemoryRecord) -> Unit,
        onReanalyze: (MemoryRecord) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val statusBarHeightDp = with(density) { statusBarHeightPx.toDp() }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showImagePreview by remember { mutableStateOf(false) }
        var showPreviewActionMenu by remember { mutableStateOf(false) }
        var editing by remember(record?.recordId) { mutableStateOf(false) }
        var imageCardBounds by remember(record?.recordId) { mutableStateOf(Rect.Zero) }
        val previewRecord = record?.takeIf { !it.imageUri.isNullOrBlank() }
        var previewImageAspectRatio by remember(previewRecord?.recordId, previewRecord?.imageUri) { mutableStateOf<Float?>(null) }
        var previewBitmap by remember(previewRecord?.recordId, previewRecord?.imageUri) { mutableStateOf<Bitmap?>(null) }
        var previewScale by remember(previewRecord?.recordId, previewRecord?.imageUri) { mutableStateOf(1f) }
        var previewOffsetX by remember(previewRecord?.recordId, previewRecord?.imageUri) { mutableStateOf(0f) }
        var previewOffsetY by remember(previewRecord?.recordId, previewRecord?.imageUri) { mutableStateOf(0f) }
        val previewTransition = updateTransition(
            targetState = showImagePreview && previewRecord != null,
            label = "memoryDetailPreview"
        )
        val previewOverlayVisible = previewTransition.currentState || previewTransition.targetState

        LaunchedEffect(previewRecord?.recordId, previewRecord?.imageUri) {
            previewImageAspectRatio = withContext(Dispatchers.IO) {
                resolveImageAspectRatio(previewRecord?.imageUri)
            }
            previewBitmap = withContext(Dispatchers.IO) {
                decodePreviewBitmap(
                    uriString = previewRecord?.imageUri,
                    requestedMaxSize = maxOf(configuration.screenWidthDp, configuration.screenHeightDp) * 3
                )
            }
        }

        LaunchedEffect(showImagePreview) {
            if (!showImagePreview) {
                showPreviewActionMenu = false
            }
        }

        LaunchedEffect(previewOverlayVisible, previewRecord?.recordId) {
            if (!previewOverlayVisible) {
                previewScale = 1f
                previewOffsetX = 0f
                previewOffsetY = 0f
            }
        }

        BackHandler(enabled = moreMenuExpanded || showDeleteConfirm || showImagePreview || editing || showPreviewActionMenu) {
            when {
                showPreviewActionMenu -> showPreviewActionMenu = false
                showImagePreview -> showImagePreview = false
                showDeleteConfirm -> showDeleteConfirm = false
                moreMenuExpanded -> moreMenuExpanded = false
                editing -> editing = false
            }
        }

        if (showImagePreview) {
            DisposableEffect(Unit) {
                val insetsController = WindowInsetsControllerCompat(window, window.decorView).apply {
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
                // 隐藏状态栏（视觉全屏），但布局使用固定 top padding（statusBarHeightPx）保持元素位置不变。
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                onDispose {
                    insetsController.show(WindowInsetsCompat.Type.statusBars())
                    WindowStyleManager.apply(this@MemoryDetailActivity, provideWindowStyleConfig())
                }
            }
        }

        NoMemoBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                val screenAspectRatio = remember(configuration.screenWidthDp, configuration.screenHeightDp) {
                    val width = configuration.screenWidthDp.toFloat().coerceAtLeast(1f)
                    val height = configuration.screenHeightDp.toFloat().coerceAtLeast(1f)
                    width / height
                }
                ResponsiveContentFrame(spec = adaptive) { spec ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = statusBarHeightDp)
                                    .padding(
                                        start = spec.pageHorizontalPadding,
                                        top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                        end = spec.pageHorizontalPadding
                                    )
                            ) {
                        val currentRecord = record
                        if (currentRecord == null) {
                            NoMemoEmptyState(
                                iconRes = R.drawable.ic_nm_memory,
                                title = "正在加载",
                                modifier = Modifier.align(Alignment.Center)
                            )
                            return@Box
                        }

                        val titleText = deriveTitle(currentRecord)
                        val summaryText = deriveInsightText(currentRecord)
                        val createdAtText = rememberTime(currentRecord.createdAt)
                        val detailTextStartPadding = if (spec.isNarrow) 12.dp else 18.dp
                        val detailScrollState = rememberScrollState()
                        val collapsedTitleThresholdPx = with(density) { 64.dp.toPx() }
                        val showCollapsedTopTitle by remember(detailScrollState.value) {
                            derivedStateOf {
                                detailScrollState.value > collapsedTitleThresholdPx
                            }
                        }
                        val pickupInfo = remember(
                            currentRecord.recordId,
                            currentRecord.categoryCode,
                            currentRecord.title,
                            currentRecord.summary,
                            currentRecord.analysis,
                            currentRecord.memory,
                            currentRecord.sourceText,
                            currentRecord.note
                        ) {
                            MemoryDetailParser.parseStructuredPickupInfo(currentRecord)
                        }
                        var draftTitle by remember(currentRecord.recordId, currentRecord.title) {
                            mutableStateOf(titleText)
                        }
                        var draftSummary by remember(currentRecord.recordId, currentRecord.summary, currentRecord.analysis) {
                            mutableStateOf(summaryText)
                        }
                        var draftImageUri by remember(currentRecord.recordId, currentRecord.imageUri) {
                            mutableStateOf(currentRecord.imageUri.orEmpty())
                        }
                        val imagePickerLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.OpenDocument()
                        ) { uri ->
                            if (uri == null) return@rememberLauncherForActivityResult
                            try {
                                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            } catch (_: Exception) {
                            }
                            // 复制选中的图片到应用缓存，保存原图副本
                            this@MemoryDetailActivity.lifecycleScope.launch {
                                val copied = withContext(Dispatchers.IO) {
                                    ImageUtils.copyUriToCache(this@MemoryDetailActivity, uri)
                                }
                                draftImageUri = copied ?: uri.toString()
                            }
                        }
                        val displayImageUri = if (editing) draftImageUri else currentRecord.imageUri.orEmpty()
                        val collapsedTitleText = if (editing) draftTitle.ifBlank { titleText } else titleText
                        val commitEdits = {
                            val saved = saveEditedRecord(
                                currentRecord,
                                draftTitle,
                                draftSummary,
                                draftImageUri
                            )
                            if (saved) {
                                editing = false
                                Toast.makeText(this@MemoryDetailActivity, "已保存修改", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MemoryDetailActivity, "保存失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart)
                                .padding(top = 0.dp)
                                .offset(y = (-4).dp)
                                .zIndex(6f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_back,
                                contentDescription = getString(R.string.back),
                                onClick = onBack,
                                size = spec.topActionButtonSize
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = showCollapsedTopTitle,
                                    enter = fadeIn(animationSpec = tween(durationMillis = 180)),
                                    exit = fadeOut(animationSpec = tween(durationMillis = 140))
                                ) {
                                    Text(
                                        text = collapsedTitleText,
                                        color = palette.textPrimary,
                                        fontSize = if (spec.isNarrow) 18.sp else 19.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            GlassIconCircleButton(
                                iconRes = if (editing) R.drawable.ic_sheet_check else R.drawable.ic_nm_more,
                                contentDescription = if (editing) "保存修改" else getString(R.string.action_more),
                                onClick = {
                                    if (editing) {
                                        commitEdits()
                                    } else {
                                        moreMenuExpanded = !moreMenuExpanded
                                    }
                                },
                                size = spec.topActionButtonSize
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spec.topActionButtonSize + 8.dp)
                                .verticalScroll(detailScrollState)
                        ) {
                        if (displayImageUri.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.64f)
                                    .align(Alignment.CenterHorizontally)
                                    .onGloballyPositioned { coordinates ->
                                        imageCardBounds = coordinates.boundsInRoot()
                                    }
                                    .alpha(if (previewOverlayVisible) 0f else 1f)
                                    .clickable {
                                        if (editing) {
                                            imagePickerLauncher.launch(arrayOf("image/*"))
                                        } else {
                                            showImagePreview = true
                                        }
                                    },
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = noMemoCardSurfaceColor(isSystemInDarkTheme(), palette.glassFill)),
                                border = BorderStroke(1.dp, palette.glassStroke)
                            ) {
                                AndroidView(
                                    factory = { ctx ->
                                        ImageView(ctx).apply {
                                            adjustViewBounds = true
                                            scaleType = ImageView.ScaleType.CENTER_CROP
                                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(28.dp)),
                                    update = { imageView ->
                                        try {
                                            imageView.setImageURI(Uri.parse(displayImageUri))
                                        } catch (_: Exception) {
                                            imageView.setImageDrawable(null)
                                        }
                                    }
                                )
                            }
                            if (editing) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    NoMemoDetailActionButton(
                                        text = "更换图片",
                                        primary = false,
                                        showBorder = false,
                                        onClick = { imagePickerLauncher.launch(arrayOf("image/*")) }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    NoMemoDetailActionButton(
                                        text = "删除图片",
                                        primary = false,
                                        showBorder = false,
                                        onClick = { draftImageUri = "" }
                                    )
                                }
                            }
                        } else if (editing) {
                            NoMemoDetailActionButton(
                                text = "添加图片",
                                primary = false,
                                showBorder = false,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = { imagePickerLauncher.launch(arrayOf("image/*")) }
                            )
                        }

                        if (editing) {
                            BasicTextField(
                                value = draftTitle,
                                onValueChange = { draftTitle = it },
                                textStyle = TextStyle(
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 27.sp else 31.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = if (spec.isNarrow) 35.sp else 39.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = detailTextStartPadding, top = 20.dp, end = 8.dp)
                            ) { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    if (draftTitle.isBlank()) {
                                        Text(
                                            text = "输入标题",
                                            color = palette.textTertiary,
                                            fontSize = if (spec.isNarrow) 27.sp else 31.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        } else {
                            Text(
                                text = titleText,
                                color = palette.textPrimary,
                                fontSize = if (spec.isNarrow) 27.sp else 31.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = if (spec.isNarrow) 35.sp else 39.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = detailTextStartPadding, top = 20.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.padding(start = detailTextStartPadding, top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val metaColor = palette.textSecondary
                            Text(
                                text = createdAtText,
                                color = metaColor,
                                fontSize = 13.sp
                            )
                            DetailMetaDivider(color = metaColor)
                            Text(
                                text = currentRecord.categoryName ?: "小记",
                                color = metaColor,
                                fontSize = 13.sp
                            )
                            if (currentRecord.mode == MemoryRecord.MODE_AI) {
                                DetailMetaDivider(color = metaColor)
                                Text(
                                    text = "AI",
                                    color = palette.accent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = "摘要",
                            color = palette.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = detailTextStartPadding, top = 24.dp)
                        )

                        NoMemoDetailSummaryBox(
                            value = if (editing) draftSummary else summaryText,
                            editing = editing,
                            modifier = Modifier.padding(
                                start = detailTextStartPadding,
                                end = detailTextStartPadding,
                                top = 10.dp
                            ),
                            onValueChange = { draftSummary = it }
                        )

                        if (!editing) {
                            pickupInfo?.let { info ->
                                Text(
                                    text = info.sectionTitle,
                                    color = palette.textPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = detailTextStartPadding, top = 24.dp)
                                )
                                NoMemoPickupCodeCard(
                                    info = info,
                                    modifier = Modifier.padding(
                                        start = detailTextStartPadding,
                                        end = detailTextStartPadding,
                                        top = 10.dp
                                    )
                                )
                                if (!info.locationTitle.isNullOrBlank() || !info.addressDetail.isNullOrBlank()) {
                                    Text(
                                        text = "地点",
                                        color = palette.textPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(start = detailTextStartPadding, top = 24.dp)
                                    )
                                    NoMemoPickupLocationCard(
                                        info = info,
                                        modifier = Modifier.padding(
                                            start = detailTextStartPadding,
                                            end = detailTextStartPadding,
                                            top = 10.dp
                                        ),
                                        onNavigate = { query -> openNavigation(query) }
                                    )
                                }
                            }
                        }

                        if (editing) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = detailTextStartPadding,
                                        end = detailTextStartPadding,
                                        top = 24.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                NoMemoDetailActionButton(
                                    text = "取消",
                                    primary = false,
                                    showBorder = false,
                                    onClick = {
                                        editing = false
                                        draftTitle = titleText
                                        draftSummary = summaryText
                                        draftImageUri = currentRecord.imageUri.orEmpty()
                                    }
                                )
                                NoMemoDetailActionButton(
                                    text = "保存",
                                    primary = true,
                                    onClick = { commitEdits() }
                                )
                            }
                        } else NoMemoDetailReanalyzeButton(
                            text = when {
                                reanalyzing && currentRecord.mode == MemoryRecord.MODE_AI -> "正在重新分析..."
                                reanalyzing -> "正在AI分析..."
                                currentRecord.mode == MemoryRecord.MODE_AI -> "重新分析"
                                else -> "AI分析"
                            },
                            enabled = !reanalyzing,
                            modifier = Modifier.padding(
                                start = detailTextStartPadding,
                                end = detailTextStartPadding,
                                top = 28.dp,
                            ),
                            onClick = { onReanalyze(currentRecord) }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                        NoMemoMenuPopup(
                            expanded = moreMenuExpanded,
                            onDismissRequest = { moreMenuExpanded = false },
                            modifier = Modifier
                                .padding(
                                    top = spec.topActionButtonSize + 2.dp,
                                    end = 0.dp
                                )
                                .offset(x = (-4).dp)
                        ) {
                            DetailMoreMenuPanel(
                                archived = currentRecord.isArchived,
                                editing = editing,
                                onEditToggle = {
                                    moreMenuExpanded = false
                                    editing = !editing
                                    if (!editing) {
                                        draftTitle = titleText
                                        draftSummary = summaryText
                                        draftImageUri = currentRecord.imageUri.orEmpty()
                                    }
                                },
                                onArchiveToggle = {
                                    moreMenuExpanded = false
                                    onToggleArchive(currentRecord)
                                },
                                onDelete = {
                                    moreMenuExpanded = false
                                    showDeleteConfirm = true
                                }
                            )
                        }

                        if (showDeleteConfirm) {
                            NoMemoDeleteConfirmDialog(
                                title = "确认删除",
                                message = "确定删除这条记忆吗？",
                                onConfirm = {
                                    showDeleteConfirm = false
                                    onDelete(currentRecord)
                                },
                                onDismiss = { showDeleteConfirm = false }
                            )
                        }
                    }
                }

                if (previewRecord != null && previewOverlayVisible) {
                    val previewDismissInteraction = remember { MutableInteractionSource() }
                    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
                    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
                    val previewVerticalInsetPx = with(density) {
                        (if (adaptive.isNarrow) 92.dp else 104.dp).toPx()
                    }
                    val sourceRect = if (imageCardBounds.isEmpty) {
                        Rect(
                            left = screenWidthPx * 0.18f,
                            top = screenHeightPx * 0.28f,
                            right = screenWidthPx * 0.82f,
                            bottom = screenHeightPx * 0.52f
                        )
                    } else {
                        imageCardBounds
                    }
                    val targetRect = remember(
                        screenWidthPx,
                        screenHeightPx,
                        previewImageAspectRatio,
                        screenAspectRatio,
                        previewVerticalInsetPx
                    ) {
                        buildPreviewTargetBounds(
                            screenWidthPx = screenWidthPx,
                            screenHeightPx = screenHeightPx,
                            imageAspectRatio = previewImageAspectRatio,
                            screenAspectRatio = screenAspectRatio,
                            verticalInsetPx = previewVerticalInsetPx
                        )
                    }
                    val previewFillScreen = previewImageAspectRatio != null &&
                        isAspectRatioCloseToScreen(previewImageAspectRatio!!, screenAspectRatio)
                    val previewAlpha by previewTransition.animateFloat(
                        transitionSpec = {
                            tween(durationMillis = 280, easing = FastOutSlowInEasing)
                        },
                        label = "previewAlpha"
                    ) { expanded -> if (expanded) 1f else 0f }
                    val previewProgress by previewTransition.animateFloat(
                        transitionSpec = {
                            tween(durationMillis = 280, easing = FastOutSlowInEasing)
                        },
                        label = "previewProgress"
                    ) { expanded -> if (expanded) 1f else 0f }
                    val buttonAlpha by previewTransition.animateFloat(
                        transitionSpec = {
                            tween(durationMillis = 180, delayMillis = if (targetState) 90 else 0)
                        },
                        label = "previewButtonAlpha"
                    ) { expanded -> if (expanded) 1f else 0f }
                    val animatedLeft = sourceRect.left + (targetRect.left - sourceRect.left) * previewProgress
                    val animatedTop = sourceRect.top + (targetRect.top - sourceRect.top) * previewProgress
                    val animatedWidth = sourceRect.width + (targetRect.width - sourceRect.width) * previewProgress
                    val animatedHeight = sourceRect.height + (targetRect.height - sourceRect.height) * previewProgress
                    val animatedCornerRadius = with(density) {
                        ((1f - previewProgress) * 28.dp.value).dp
                    }
                    val previewInteractive = previewProgress >= 0.98f
                    val clampedPreviewScale = previewScale.coerceIn(1f, 4f)
                    val previewButtonSize = adaptive.topActionButtonSize
                    val previewCloseButtonSize = if (adaptive.isNarrow) 68.dp else 76.dp

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(20f)
                    ) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = previewAlpha))
                                .clickable(
                                    interactionSource = previewDismissInteraction,
                                    indication = null,
                                    onClick = { showImagePreview = false }
                                )
                        )
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        x = animatedLeft.roundToInt(),
                                        y = animatedTop.roundToInt()
                                    )
                                }
                                .size(
                                    width = with(density) { animatedWidth.toDp() },
                                    height = with(density) { animatedHeight.toDp() }
                                )
                                .clip(RoundedCornerShape(animatedCornerRadius))
                                .pointerInput(previewInteractive, clampedPreviewScale) {
                                    if (!previewInteractive) return@pointerInput
                                    detectTapGestures(
                                        onDoubleTap = {
                                            if (clampedPreviewScale > 1f) {
                                                previewScale = 1f
                                                previewOffsetX = 0f
                                                previewOffsetY = 0f
                                            } else {
                                                previewScale = 2.2f
                                            }
                                        }
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                                    .pointerInput(previewInteractive, animatedWidth, animatedHeight) {
                                        if (!previewInteractive) return@pointerInput
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            val nextScale = (previewScale * zoom).coerceIn(1f, 4f)
                                            previewScale = nextScale
                                            previewOffsetX = clampPreviewTranslation(
                                                containerSizePx = animatedWidth,
                                                scale = nextScale,
                                                translation = previewOffsetX + pan.x
                                            )
                                            previewOffsetY = clampPreviewTranslation(
                                                containerSizePx = animatedHeight,
                                                scale = nextScale,
                                                translation = previewOffsetY + pan.y
                                            )
                                        }
                                    }
                            ) {
                                val bitmap = previewBitmap
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "预览图片",
                                        contentScale = if (previewFillScreen) ContentScale.Crop else ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                scaleX = clampedPreviewScale
                                                scaleY = clampedPreviewScale
                                                translationX = clampPreviewTranslation(
                                                    containerSizePx = animatedWidth,
                                                    scale = clampedPreviewScale,
                                                    translation = previewOffsetX
                                                )
                                                translationY = clampPreviewTranslation(
                                                    containerSizePx = animatedHeight,
                                                    scale = clampedPreviewScale,
                                                    translation = previewOffsetY
                                                )
                                            }
                                    )
                                } else {
                                    AndroidView(
                                        factory = { ctx ->
                                            ImageView(ctx).apply {
                                                adjustViewBounds = false
                                                scaleType = ImageView.ScaleType.FIT_CENTER
                                                setBackgroundColor(android.graphics.Color.BLACK)
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        update = { imageView ->
                                            try {
                                                imageView.setImageURI(Uri.parse(previewRecord.imageUri))
                                                imageView.scaleType = if (previewFillScreen) {
                                                    ImageView.ScaleType.CENTER_CROP
                                                } else {
                                                    ImageView.ScaleType.FIT_CENTER
                                                }
                                            } catch (_: Exception) {
                                                imageView.setImageDrawable(null)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        MemoryDetailPreviewIconButton(
                            iconRes = R.drawable.ic_nm_more,
                            contentDescription = "更多操作",
                            onClick = { showPreviewActionMenu = !showPreviewActionMenu },
                            size = previewButtonSize,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = statusBarHeightDp)
                                .alpha(buttonAlpha)
                                .padding(
                                    top = (adaptive.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                    end = adaptive.pageHorizontalPadding
                                )
                                .offset(y = (-4).dp)
                        )
                        NoMemoMenuPopup(
                            expanded = showPreviewActionMenu,
                            onDismissRequest = { showPreviewActionMenu = false },
                            modifier = Modifier
                                .padding(top = statusBarHeightDp)
                                .padding(
                                    top = 10.dp + previewButtonSize + 10.dp,
                                    end = adaptive.pageHorizontalPadding
                                )
                        ) {
                            PreviewImageActionMenuPanel(
                                onSaveImage = {
                                    showPreviewActionMenu = false
                                    savePreviewImage(previewRecord.imageUri.orEmpty())
                                },
                                onShareImage = {
                                    showPreviewActionMenu = false
                                    sharePreviewImage(previewRecord.imageUri.orEmpty())
                                }
                            )
                        }
                        MemoryDetailPreviewCloseButton(
                            iconRes = R.drawable.ic_sheet_close,
                            contentDescription = getString(R.string.cancel),
                            onClick = { showImagePreview = false },
                            size = previewCloseButtonSize,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .alpha(buttonAlpha)
                                .padding(bottom = 18.dp)
                        )
                    }
                }
            }
        }
    }

    private fun rememberTime(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timeMillis))
    }

    private fun deriveTitle(record: MemoryRecord): String {
        return record.title?.takeIf { it.isNotBlank() }
            ?: record.memory?.takeIf { it.isNotBlank() }
            ?: record.sourceText?.takeIf { it.isNotBlank() }
            ?: "记忆"
    }

    private fun deriveSummary(record: MemoryRecord): String {
        return record.summary?.takeIf { it.isNotBlank() }
            ?: record.memory?.takeIf { it.isNotBlank() }
            ?: record.sourceText?.takeIf { it.isNotBlank() }
            ?: record.analysis?.takeIf { it.isNotBlank() }
            ?: "暂无摘要"
    }

    private fun deriveInsightText(record: MemoryRecord): String {
        return record.analysis?.takeIf { it.isNotBlank() }
            ?: record.memory?.takeIf { it.isNotBlank() }
            ?: record.summary?.takeIf { it.isNotBlank() }
            ?: record.sourceText?.takeIf { it.isNotBlank() }
            ?: "暂无分析结果"
    }


    private fun openNavigation(query: String) {
        if (query.isBlank()) {
            return
        }
        val encoded = Uri.encode(query)
        val intents = listOf(
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encoded")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$encoded"))
        )
        for (intent in intents) {
            try {
                startActivity(intent)
                return
            } catch (_: Exception) {
            }
        }
        Toast.makeText(this, "未找到可用地图应用", Toast.LENGTH_SHORT).show()
    }

    private fun isAspectRatioCloseToScreen(imageAspectRatio: Float, screenAspectRatio: Float): Boolean {
        if (imageAspectRatio <= 0f || screenAspectRatio <= 0f) {
            return false
        }
        return kotlin.math.abs(imageAspectRatio - screenAspectRatio) <= 0.03f
    }

    private fun buildPreviewTargetBounds(
        screenWidthPx: Float,
        screenHeightPx: Float,
        imageAspectRatio: Float?,
        screenAspectRatio: Float,
        verticalInsetPx: Float
    ): Rect {
        val shouldFillScreen = imageAspectRatio != null &&
            isAspectRatioCloseToScreen(imageAspectRatio, screenAspectRatio)
        if (shouldFillScreen || imageAspectRatio == null || imageAspectRatio <= 0f) {
            return Rect(0f, 0f, screenWidthPx, screenHeightPx)
        }

        val availableWidth = screenWidthPx
        val availableHeight = (screenHeightPx - verticalInsetPx * 2f).coerceAtLeast(1f)
        val availableAspectRatio = availableWidth / availableHeight

        return if (imageAspectRatio >= availableAspectRatio) {
            val targetWidth = availableWidth
            val targetHeight = targetWidth / imageAspectRatio
            val top = (screenHeightPx - targetHeight) / 2f
            Rect(0f, top, targetWidth, top + targetHeight)
        } else {
            val targetHeight = availableHeight
            val targetWidth = targetHeight * imageAspectRatio
            val left = (screenWidthPx - targetWidth) / 2f
            val top = (screenHeightPx - targetHeight) / 2f
            Rect(left, top, left + targetWidth, top + targetHeight)
        }
    }

    private fun resolveImageAspectRatio(uriString: String?): Float? {
        if (uriString.isNullOrBlank()) {
            return null
        }
        return runCatching {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            openImageInputStream(uriString)?.use { input ->
                BitmapFactory.decodeStream(input, null, options)
            }
            if (options.outWidth > 0 && options.outHeight > 0) {
                options.outWidth.toFloat() / options.outHeight.toFloat()
            } else {
                null
            }
        }.getOrNull()
    }

    private fun decodePreviewBitmap(uriString: String?, requestedMaxSize: Int): Bitmap? {
        if (uriString.isNullOrBlank()) {
            return null
        }
        return runCatching {
            val bounds = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            openImageInputStream(uriString)?.use { input ->
                BitmapFactory.decodeStream(input, null, bounds)
            }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                return null
            }
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = calculatePreviewInSampleSize(
                    width = bounds.outWidth,
                    height = bounds.outHeight,
                    requestedMaxSize = requestedMaxSize
                )
            }
            openImageInputStream(uriString)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            }
        }.getOrNull()
    }

    private fun calculatePreviewInSampleSize(width: Int, height: Int, requestedMaxSize: Int): Int {
        var inSampleSize = 1
        var currentWidth = width
        var currentHeight = height
        while (currentWidth > requestedMaxSize || currentHeight > requestedMaxSize) {
            currentWidth /= 2
            currentHeight /= 2
            inSampleSize *= 2
        }
        return inSampleSize.coerceAtLeast(1)
    }

    private fun clampPreviewTranslation(containerSizePx: Float, scale: Float, translation: Float): Float {
        val maxTranslation = ((containerSizePx * scale) - containerSizePx) / 2f
        if (maxTranslation <= 0f) {
            return 0f
        }
        return translation.coerceIn(-maxTranslation, maxTranslation)
    }

    private fun openImageInputStream(uriString: String): InputStream? {
        val parsed = Uri.parse(uriString)
        return when {
            parsed.scheme.isNullOrBlank() -> {
                val file = File(uriString)
                if (file.exists()) file.inputStream() else null
            }
            parsed.scheme.equals("file", ignoreCase = true) -> {
                parsed.path?.let { path ->
                    val file = File(path)
                    if (file.exists()) file.inputStream() else null
                }
            }
            else -> contentResolver.openInputStream(parsed)
        }
    }

    private fun savePreviewImage(uriString: String) {
        if (uriString.isBlank()) {
            Toast.makeText(this, "没有可保存的图片", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val saved = withContext(Dispatchers.IO) {
                runCatching { saveImageToGalleryInternal(uriString) }.getOrNull()
            }
            if (saved != null) {
                Toast.makeText(this@MemoryDetailActivity, "图片已保存", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MemoryDetailActivity, "保存图片失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sharePreviewImage(uriString: String) {
        if (uriString.isBlank()) {
            Toast.makeText(this, "没有可分享的图片", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val shareUri = withContext(Dispatchers.IO) {
                runCatching { preparePreviewShareUri(uriString) }.getOrNull()
            }
            if (shareUri == null) {
                Toast.makeText(this@MemoryDetailActivity, "分享图片失败", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = contentResolver.getType(shareUri) ?: "image/*"
                putExtra(Intent.EXTRA_STREAM, shareUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "分享图片"))
        }
    }

    private fun saveImageToGalleryInternal(uriString: String): Uri? {
        val extension = resolveImageExtension(uriString)
        val mimeType = "image/$extension"
        val fileName = "NoMemo_${System.currentTimeMillis()}.$extension"
        val inputStream = openImageInputStream(uriString) ?: return null
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/NoMemo")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: return null
            inputStream.use { input ->
                contentResolver.openOutputStream(uri)?.use { output ->
                    input.copyTo(output)
                } ?: return null
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
            uri
        } else {
            val picturesDir = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "NoMemo"
            ).apply { mkdirs() }
            val file = File(picturesDir, fileName)
            inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), arrayOf(mimeType), null)
            Uri.fromFile(file)
        }
    }

    private fun preparePreviewShareUri(uriString: String): Uri? {
        val extension = resolveImageExtension(uriString)
        val mimeType = "image/$extension"
        val cacheDir = File(cacheDir, "shared_images").apply { mkdirs() }
        val file = File(cacheDir, "nomemo_share_${System.currentTimeMillis()}.$extension")
        openImageInputStream(uriString)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        } ?: return null
        MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), arrayOf(mimeType), null)
        return FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
    }

    private fun resolveImageExtension(uriString: String): String {
        val parsed = Uri.parse(uriString)
        val mimeType = runCatching { contentResolver.getType(parsed) }.getOrNull().orEmpty()
        return when {
            mimeType.endsWith("png") -> "png"
            mimeType.endsWith("webp") -> "webp"
            else -> "jpg"
        }
    }

    @Composable
    private fun DetailMetaDivider(color: Color) {
        Spacer(modifier = Modifier.width(7.dp))
        Box(
            modifier = Modifier
                .size(3.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.72f))
        )
        Spacer(modifier = Modifier.width(7.dp))
    }

    @Composable
    private fun MemoryDetailPreviewCloseButton(
        iconRes: Int,
        contentDescription: String,
        onClick: () -> Unit,
        size: androidx.compose.ui.unit.Dp,
        modifier: Modifier = Modifier
    ) {
        MemoryDetailPreviewIconButton(
            iconRes = iconRes,
            contentDescription = contentDescription,
            onClick = onClick,
            size = size,
            modifier = modifier
        )
    }

    @Composable
    private fun MemoryDetailPreviewIconButton(
        iconRes: Int,
        contentDescription: String,
        onClick: () -> Unit,
        size: androidx.compose.ui.unit.Dp,
        modifier: Modifier = Modifier
    ) {
        val isDark = isSystemInDarkTheme()
        val buttonSurface = if (isDark) {
            Color(0xFF111111).copy(alpha = 0.92f)
        } else {
            Color.White.copy(alpha = 0.94f)
        }
        val iconTint = if (isDark) Color.White else Color(0xFF121212)
        val borderColor = if (isDark) {
            Color.White.copy(alpha = 0.16f)
        } else {
            Color.Black.copy(alpha = 0.08f)
        }
        val shadowColor = if (isDark) {
            Color.Black.copy(alpha = 0.32f)
        } else {
            Color.Black.copy(alpha = 0.18f)
        }
        PressScaleBox(
            onClick = onClick,
            modifier = modifier
                .size(size)
                .shadow(
                    elevation = 18.dp,
                    shape = CircleShape,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(buttonSurface)
                    .border(1.dp, borderColor, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
        }
    }

    @Composable
    private fun PreviewImageActionMenuPanel(
        onSaveImage: () -> Unit,
        onShareImage: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        NoMemoActionMenuPanel(
            actions = listOf(
                NoMemoMenuActionItem(
                    iconRes = R.drawable.ic_nm_download,
                    label = "保存图片",
                    onClick = onSaveImage
                ),
                NoMemoMenuActionItem(
                    iconRes = R.drawable.ic_nm_share,
                    label = "分享图片",
                    onClick = onShareImage
                )
            ),
            modifier = modifier
        )
    }


    @Composable
    private fun DetailMoreMenuPanel(
        archived: Boolean,
        editing: Boolean,
        onEditToggle: () -> Unit,
        onArchiveToggle: () -> Unit,
        onDelete: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        NoMemoActionMenuPanel(
            actions = listOf(
                NoMemoMenuActionItem(
                    iconRes = R.drawable.ic_nm_edit,
                    label = if (editing) "结束编辑" else "编辑内容",
                    onClick = onEditToggle
                ),
                NoMemoMenuActionItem(
                    iconRes = R.drawable.ic_sheet_calendar,
                    label = if (archived) getString(R.string.action_unarchive) else getString(R.string.action_archive),
                    onClick = onArchiveToggle
                ),
                NoMemoMenuActionItem(
                    iconRes = R.drawable.ic_nm_delete,
                    label = getString(R.string.action_delete),
                    onClick = onDelete,
                    destructive = true
                )
            ),
            modifier = modifier
        )
    }
}
