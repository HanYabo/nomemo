package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
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
import androidx.compose.ui.unit.IntRect
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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
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

    private fun saveReminderForRecord(
        currentRecord: MemoryRecord,
        reminderAt: Long
    ): Boolean {
        val normalizedReminderAt = reminderAt.coerceAtLeast(0L)
        val updated = MemoryRecord(
            currentRecord.recordId,
            currentRecord.createdAt,
            currentRecord.mode,
            currentRecord.title,
            currentRecord.summary,
            currentRecord.sourceText,
            currentRecord.note,
            currentRecord.imageUri,
            currentRecord.analysis,
            currentRecord.memory,
            currentRecord.engine,
            currentRecord.categoryGroupCode,
            currentRecord.categoryCode,
            currentRecord.categoryName,
            normalizedReminderAt,
            false,
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
        val previewDecodeMaxSize = remember(configuration.screenWidthDp, configuration.screenHeightDp, density.density) {
            val screenMaxPx = maxOf(configuration.screenWidthDp, configuration.screenHeightDp) * density.density
            (screenMaxPx * 4f).roundToInt().coerceIn(2048, 6144)
        }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var moreMenuAnchorBounds by remember { mutableStateOf<IntRect?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showReminderSetupSheet by remember { mutableStateOf(false) }
        var showImagePreview by remember { mutableStateOf(false) }
        var showPreviewActionMenu by remember { mutableStateOf(false) }
        var previewActionMenuAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
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
                    requestedMaxSize = previewDecodeMaxSize
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

        BackHandler(enabled = moreMenuExpanded || showDeleteConfirm || showReminderSetupSheet || showImagePreview || editing || showPreviewActionMenu) {
            when {
                showPreviewActionMenu -> showPreviewActionMenu = false
                showImagePreview -> showImagePreview = false
                showDeleteConfirm -> showDeleteConfirm = false
                showReminderSetupSheet -> showReminderSetupSheet = false
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
                            Box(
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    val bounds = coordinates.boundsInRoot()
                                    moreMenuAnchorBounds = IntRect(
                                        left = bounds.left.roundToInt(),
                                        top = bounds.top.roundToInt(),
                                        right = bounds.right.roundToInt(),
                                        bottom = bounds.bottom.roundToInt()
                                    )
                                }
                            ) {
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
                            val metaColor = if (isSystemInDarkTheme()) {
                                Color.White.copy(alpha = 0.46f)
                            } else {
                                Color(0xFF98A1AE)
                            }
                            RecordMetaLine(
                                timeText = createdAtText,
                                categoryCode = currentRecord.categoryCode,
                                categoryText = currentRecord.categoryName ?: "小记",
                                metaColor = metaColor
                            )
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
                                if (!info.locationText.isNullOrBlank()) {
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
                            anchorBounds = moreMenuAnchorBounds,
                            anchorBoundsInRoot = true,
                            anchorAdjustment = IntOffset(
                                x = -with(density) { spec.pageHorizontalPadding.roundToPx() },
                                y = -with(density) {
                                    (statusBarHeightDp + (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp)).roundToPx()
                                }
                            )
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
                                onAddToReminder = {
                                    moreMenuExpanded = false
                                    showReminderSetupSheet = true
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
                    val displayedContentSize = remember(
                        animatedWidth,
                        animatedHeight,
                        previewImageAspectRatio,
                        previewFillScreen
                    ) {
                        calculatePreviewContentSize(
                            containerWidthPx = animatedWidth,
                            containerHeightPx = animatedHeight,
                            imageAspectRatio = previewImageAspectRatio,
                            fillScreen = previewFillScreen
                        )
                    }
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
                                        onDoubleTap = { tapOffset ->
                                            if (clampedPreviewScale > 1f) {
                                                previewScale = 1f
                                                previewOffsetX = 0f
                                                previewOffsetY = 0f
                                            } else {
                                                val targetScale = 2.2f
                                                val centerX = animatedWidth / 2f
                                                val centerY = animatedHeight / 2f
                                                val relativeTapX = tapOffset.x - centerX
                                                val relativeTapY = tapOffset.y - centerY
                                                previewScale = targetScale
                                                previewOffsetX = clampPreviewTranslation(
                                                    containerSizePx = animatedWidth,
                                                    contentSizePx = displayedContentSize.first,
                                                    scale = targetScale,
                                                    translation = relativeTapX * (1f - targetScale)
                                                )
                                                previewOffsetY = clampPreviewTranslation(
                                                    containerSizePx = animatedHeight,
                                                    contentSizePx = displayedContentSize.second,
                                                    scale = targetScale,
                                                    translation = relativeTapY * (1f - targetScale)
                                                )
                                            }
                                        }
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                                    .pointerInput(previewInteractive, animatedWidth, animatedHeight, displayedContentSize) {
                                        if (!previewInteractive) return@pointerInput
                                        detectTransformGestures { centroid, pan, zoom, _ ->
                                            val currentScale = previewScale.coerceIn(1f, 4f)
                                            val nextScale = (currentScale * zoom).coerceIn(1f, 4f)
                                            val scaleFactor = if (currentScale == 0f) 1f else nextScale / currentScale
                                            val centroidX = centroid.x - (animatedWidth / 2f)
                                            val centroidY = centroid.y - (animatedHeight / 2f)
                                            val nextOffsetX = (previewOffsetX * scaleFactor) + (centroidX * (1f - scaleFactor)) + pan.x
                                            val nextOffsetY = (previewOffsetY * scaleFactor) + (centroidY * (1f - scaleFactor)) + pan.y
                                            previewScale = nextScale
                                            previewOffsetX = clampPreviewTranslation(
                                                containerSizePx = animatedWidth,
                                                contentSizePx = displayedContentSize.first,
                                                scale = nextScale,
                                                translation = nextOffsetX
                                            )
                                            previewOffsetY = clampPreviewTranslation(
                                                containerSizePx = animatedHeight,
                                                contentSizePx = displayedContentSize.second,
                                                scale = nextScale,
                                                translation = nextOffsetY
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
                                                    contentSizePx = displayedContentSize.first,
                                                    scale = clampedPreviewScale,
                                                    translation = previewOffsetX
                                                )
                                                translationY = clampPreviewTranslation(
                                                    containerSizePx = animatedHeight,
                                                    contentSizePx = displayedContentSize.second,
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
                            onBoundsChanged = { previewActionMenuAnchorBounds = it },
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
                            anchorBounds = previewActionMenuAnchorBounds
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

            record?.let { currentRecord ->
                DetailReminderSetupSheet(
                    visible = showReminderSetupSheet,
                    initialReminderAt = currentRecord.reminderAt,
                    onDismiss = { showReminderSetupSheet = false },
                    onConfirm = { reminderAt ->
                        val saved = saveReminderForRecord(currentRecord, reminderAt)
                        if (saved) {
                            showReminderSetupSheet = false
                            Toast.makeText(this@MemoryDetailActivity, "已添加到提醒事项", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MemoryDetailActivity, "提醒保存失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
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
                inPreferredConfig = Bitmap.Config.ARGB_8888
                inScaled = false
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

    private fun calculatePreviewContentSize(
        containerWidthPx: Float,
        containerHeightPx: Float,
        imageAspectRatio: Float?,
        fillScreen: Boolean
    ): Pair<Float, Float> {
        if (containerWidthPx <= 0f || containerHeightPx <= 0f || imageAspectRatio == null || imageAspectRatio <= 0f) {
            return containerWidthPx to containerHeightPx
        }
        val containerAspectRatio = containerWidthPx / containerHeightPx
        return if (fillScreen) {
            if (imageAspectRatio > containerAspectRatio) {
                val height = containerHeightPx
                val width = height * imageAspectRatio
                width to height
            } else {
                val width = containerWidthPx
                val height = width / imageAspectRatio
                width to height
            }
        } else {
            if (imageAspectRatio > containerAspectRatio) {
                val width = containerWidthPx
                val height = width / imageAspectRatio
                width to height
            } else {
                val height = containerHeightPx
                val width = height * imageAspectRatio
                width to height
            }
        }
    }

    private fun clampPreviewTranslation(
        containerSizePx: Float,
        contentSizePx: Float,
        scale: Float,
        translation: Float
    ): Float {
        val maxTranslation = ((contentSizePx * scale) - containerSizePx) / 2f
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
        onBoundsChanged: ((androidx.compose.ui.unit.IntRect) -> Unit)? = null,
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
                .onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInWindow()
                    onBoundsChanged?.invoke(
                        androidx.compose.ui.unit.IntRect(
                            left = bounds.left.roundToInt(),
                            top = bounds.top.roundToInt(),
                            right = bounds.right.roundToInt(),
                            bottom = bounds.bottom.roundToInt()
                        )
                    )
                }
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

    private data class ReminderLeadOption(
        val label: String,
        val minutes: Int
    )

    @Composable
    private fun DetailReminderSetupSheet(
        visible: Boolean,
        initialReminderAt: Long,
        onDismiss: () -> Unit,
        onConfirm: (Long) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val now = remember { Calendar.getInstance() }
        val initialTime = if (initialReminderAt > 0L) initialReminderAt else defaultDetailReminderTime()
        val initialCalendar = remember(initialTime, visible) { Calendar.getInstance().apply { timeInMillis = initialTime } }

        var year by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.YEAR)) }
        var month by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.MONTH) + 1) }
        var day by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }
        var hour by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
        var minute by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.MINUTE)) }
        var leadMinutes by remember(initialTime, visible) { mutableStateOf(5) }

        val maxDay = remember(year, month) { detailDaysInMonth(year, month) }
        if (day > maxDay) {
            day = maxDay
        }

        val leadOptions = remember {
            listOf(
                ReminderLeadOption("5分钟前", 5),
                ReminderLeadOption("15分钟前", 15),
                ReminderLeadOption("30分钟前", 30),
                ReminderLeadOption("1小时前", 60),
                ReminderLeadOption("1天前", 24 * 60)
            )
        }

        val eventAt = remember(year, month, day, hour, minute) {
            buildReminderDateTime(year, month, day, hour, minute)
        }
        val finalReminderAt = remember(eventAt, leadMinutes) {
            (eventAt - leadMinutes * 60L * 1000L).coerceAtLeast(0L)
        }

        val sheetSurface = palette.memoBgStart
        val cardSurface = if (isDark) noMemoCardSurfaceColor(true) else Color.White
        val sectionSurface = cardSurface
        val optionCardColor = cardSurface
        val optionSelectedColor = if (isDark) palette.accent.copy(alpha = 0.18f) else palette.accent.copy(alpha = 0.13f)
        val dragHandleColor = if (isDark) Color.White.copy(alpha = 0.16f) else Color(0x24000000)
        val wheelHighlightColor = if (isDark) {
            palette.accent.copy(alpha = 0.32f)
        } else {
            Color(0xFFEAF2FF)
        }
        val wheelSelectedTextColor = if (isDark) Color.White else Color(0xFF1344C4)
        val wheelNormalTextColor = if (isDark) Color.White.copy(alpha = 0.70f) else Color(0xFF1E2230).copy(alpha = 0.58f)
        val sheetBodyHeight = if (adaptive.isNarrow) 700.dp else 760.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(28f)
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
                    colors = CardDefaults.cardColors(containerColor = sheetSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sheetBodyHeight)
                            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(width = 56.dp, height = 5.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(dragHandleColor)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_close,
                                contentDescription = "关闭提醒设置",
                                onClick = onDismiss,
                                size = adaptive.topActionButtonSize
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "提醒设置",
                                    color = palette.textPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_check,
                                contentDescription = "确认提醒设置",
                                onClick = { onConfirm(finalReminderAt) },
                                size = adaptive.topActionButtonSize
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    DetailReminderWheelCard(
                                        label = "年",
                                        values = ((now.get(Calendar.YEAR) - 1)..(now.get(Calendar.YEAR) + 5)).toList(),
                                        selectedValue = year,
                                        formatter = { it.toString() },
                                        cardColor = sectionSurface,
                                        highlightColor = wheelHighlightColor,
                                        selectedTextColor = wheelSelectedTextColor,
                                        normalTextColor = wheelNormalTextColor,
                                        onSelected = { year = it },
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    DetailReminderWheelCard(
                                        label = "月",
                                        values = (1..12).toList(),
                                        selectedValue = month,
                                        formatter = { it.toString().padStart(2, '0') },
                                        cardColor = sectionSurface,
                                        highlightColor = wheelHighlightColor,
                                        selectedTextColor = wheelSelectedTextColor,
                                        normalTextColor = wheelNormalTextColor,
                                        onSelected = { month = it },
                                        modifier = Modifier.weight(1f)
                                    )
                                    DetailReminderWheelCard(
                                        label = "日",
                                        values = (1..maxDay).toList(),
                                        selectedValue = day,
                                        formatter = { it.toString().padStart(2, '0') },
                                        cardColor = sectionSurface,
                                        highlightColor = wheelHighlightColor,
                                        selectedTextColor = wheelSelectedTextColor,
                                        normalTextColor = wheelNormalTextColor,
                                        onSelected = { day = it },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DetailReminderWheelCard(
                                        label = "",
                                        values = (0..23).toList(),
                                        selectedValue = hour,
                                        formatter = { it.toString().padStart(2, '0') },
                                        cardColor = sectionSurface,
                                        highlightColor = wheelHighlightColor,
                                        selectedTextColor = wheelSelectedTextColor,
                                        normalTextColor = wheelNormalTextColor,
                                        onSelected = { hour = it },
                                        modifier = Modifier.width(108.dp)
                                    )
                                    Text(
                                        text = ":",
                                        color = palette.textPrimary,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                    DetailReminderWheelCard(
                                        label = "",
                                        values = (0..59).toList(),
                                        selectedValue = minute,
                                        formatter = { it.toString().padStart(2, '0') },
                                        cardColor = sectionSurface,
                                        highlightColor = wheelHighlightColor,
                                        selectedTextColor = wheelSelectedTextColor,
                                        normalTextColor = wheelNormalTextColor,
                                        onSelected = { minute = it },
                                        modifier = Modifier.width(108.dp)
                                    )
                                }
                            }

                            Text(
                                text = "提前提醒",
                                color = palette.textSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 14.dp, start = 2.dp, bottom = 8.dp)
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = optionCardColor),
                                border = BorderStroke(
                                    1.dp,
                                    if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)
                                )
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    leadOptions.forEachIndexed { index, option ->
                                        val selected = leadMinutes == option.minutes
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { leadMinutes = option.minutes }
                                                .background(
                                                    if (selected) optionSelectedColor else Color.Transparent
                                                )
                                                .padding(horizontal = 12.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option.label,
                                                color = palette.textPrimary,
                                                fontSize = 17.sp,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (selected) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_sheet_check),
                                                    contentDescription = null,
                                                    tint = palette.accent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        if (index < leadOptions.lastIndex) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(
                                                        if (isDark) Color.White.copy(alpha = 0.10f)
                                                        else Color.Black.copy(alpha = 0.08f)
                                                    )
                                            )
                                        }
                                    }
                                    Text(
                                        text = "自定义... (0/5)",
                                        color = palette.accent,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 11.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }

        }
    }

    @Composable
    private fun DetailReminderWheelCard(
        label: String,
        values: List<Int>,
        selectedValue: Int,
        formatter: (Int) -> String,
        cardColor: Color,
        highlightColor: Color,
        selectedTextColor: Color,
        normalTextColor: Color,
        onSelected: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .graphicsLayer {
                        shape = RoundedCornerShape(18.dp)
                        clip = true
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .background(cardColor)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(highlightColor)
                )
                AndroidView(
                    factory = { context ->
                        DetailAnimatedNumberPicker(context).apply {
                            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                            wrapSelectorWheel = true
                            background = ColorDrawable(android.graphics.Color.TRANSPARENT)
                            setBackgroundColor(android.graphics.Color.TRANSPARENT)
                            isVerticalFadingEdgeEnabled = false
                            isHorizontalFadingEdgeEnabled = false
                            overScrollMode = android.view.View.OVER_SCROLL_NEVER
                            setFadingEdgeLength(0)
                            minValue = 0
                            maxValue = 0
                            displayedValues = arrayOf("")
                            setOnValueChangedListener { _, _, newVal ->
                                if (newVal in values.indices) {
                                    onSelected(values[newVal])
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(138.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    update = { picker ->
                        val displayValues = values.map(formatter).toTypedArray()
                        val maxValue = (values.size - 1).coerceAtLeast(0)
                        val shouldResetDisplayedValues =
                            picker.minValue != 0 ||
                                picker.maxValue != maxValue ||
                                picker.displayedValues == null ||
                                picker.displayedValues.size != displayValues.size ||
                                !picker.displayedValues.contentEquals(displayValues)
                        if (shouldResetDisplayedValues) {
                            picker.displayedValues = null
                            picker.minValue = 0
                            picker.maxValue = maxValue
                            picker.displayedValues = displayValues
                        }
                        picker.wrapSelectorWheel = values.size > 1
                        val targetIndex = values.indexOf(selectedValue).coerceAtLeast(0)
                        if (picker.value != targetIndex) {
                            picker.value = targetIndex
                        }
                        styleDetailNumberPicker(
                            picker = picker,
                            selectedTextColor = selectedTextColor,
                            normalTextColor = normalTextColor
                        )
                    }
                )
            }
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    color = rememberNoMemoPalette().textSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }

    private fun defaultDetailReminderTime(): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val minute = calendar.get(Calendar.MINUTE)
        val normalizedMinute = ((minute + 4) / 5) * 5
        if (normalizedMinute >= 60) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            calendar.set(Calendar.MINUTE, 0)
        } else {
            calendar.set(Calendar.MINUTE, normalizedMinute)
        }
        return calendar.timeInMillis
    }

    private fun buildReminderDateTime(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): Long {
        val safeDay = day.coerceAtMost(detailDaysInMonth(year, month))
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, safeDay)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun detailDaysInMonth(year: Int, month: Int): Int {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, (month - 1).coerceIn(0, 11))
            set(Calendar.DAY_OF_MONTH, 1)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun styleDetailNumberPicker(
        picker: NumberPicker,
        selectedTextColor: Color,
        normalTextColor: Color
    ) {
        val scaledDensity = picker.resources.displayMetrics.scaledDensity
        val candidateTextPx = 38f * scaledDensity
        val selectedTextPx = 44f * scaledDensity
        picker.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        runCatching {
            val setDividerHeight = NumberPicker::class.java.getMethod("setSelectionDividerHeight", Int::class.javaPrimitiveType)
            setDividerHeight.invoke(picker, 0)
        }
        runCatching {
            val selectionDividerField = NumberPicker::class.java.getDeclaredField("mSelectionDivider")
            selectionDividerField.isAccessible = true
            selectionDividerField.set(picker, ColorDrawable(android.graphics.Color.TRANSPARENT))
        }
        runCatching {
            val selectionDividerHeightField = NumberPicker::class.java.getDeclaredField("mSelectionDividerHeight")
            selectionDividerHeightField.isAccessible = true
            selectionDividerHeightField.setInt(picker, 0)
        }
        runCatching {
            val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
            selectorWheelPaintField.isAccessible = true
            val paint = selectorWheelPaintField.get(picker) as android.graphics.Paint
            paint.color = android.graphics.Color.TRANSPARENT
            paint.textSize = candidateTextPx
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.isFakeBoldText = true
        }
        runCatching {
            val textColorField = NumberPicker::class.java.getDeclaredField("mTextColor")
            textColorField.isAccessible = true
            textColorField.setInt(picker, normalTextColor.toArgb())
        }
        runCatching {
            val inputTextField = NumberPicker::class.java.getDeclaredField("mInputText")
            inputTextField.isAccessible = true
            val input = inputTextField.get(picker) as? EditText
            input?.apply {
                setTextColor(android.graphics.Color.TRANSPARENT)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTextPx)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isFocusable = false
                isFocusableInTouchMode = false
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                gravity = android.view.Gravity.CENTER
                includeFontPadding = false
                isCursorVisible = false
                alpha = 0f
                visibility = android.view.View.INVISIBLE
            }
        }
        for (index in 0 until picker.childCount) {
            val child = picker.getChildAt(index)
            if (child is EditText) {
                child.setTextColor(android.graphics.Color.TRANSPARENT)
                child.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectedTextPx)
                child.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                child.isFocusable = false
                child.isFocusableInTouchMode = false
                child.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                child.gravity = android.view.Gravity.CENTER
                child.includeFontPadding = false
                child.isCursorVisible = false
                child.alpha = 0f
                child.visibility = android.view.View.INVISIBLE
            }
        }
        if (picker is DetailAnimatedNumberPicker) {
            picker.updateTextStyle(
                normalColor = normalTextColor.toArgb(),
                selectedColor = selectedTextColor.toArgb(),
                normalTextPx = candidateTextPx,
                selectedTextPx = selectedTextPx
            )
        }
        picker.invalidate()
    }

    private class DetailAnimatedNumberPicker(
        context: Context
    ) : NumberPicker(context) {

        private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isFakeBoldText = true
        }

        private var normalColor: Int = android.graphics.Color.GRAY
        private var selectedColor: Int = android.graphics.Color.BLACK
        private var normalTextPx: Float = 0f
        private var selectedTextPx: Float = 0f

        fun updateTextStyle(
            normalColor: Int,
            selectedColor: Int,
            normalTextPx: Float,
            selectedTextPx: Float
        ) {
            this.normalColor = normalColor
            this.selectedColor = selectedColor
            this.normalTextPx = normalTextPx
            this.selectedTextPx = selectedTextPx
            invalidate()
        }

        override fun dispatchDraw(canvas: Canvas) {
            super.dispatchDraw(canvas)
            val selectorIndices = getSelectorIndices() ?: return
            val selectorElementHeight = getSelectorElementHeight()
            if (selectorElementHeight <= 0) {
                return
            }
            val currentScrollOffset = getCurrentScrollOffset().toFloat()
            val min = minValue
            val max = maxValue
            val displayed = displayedValues
            val middleIndex = selectorIndices.size / 2
            val selectedBaseline = currentScrollOffset + (middleIndex * selectorElementHeight)

            selectorIndices.forEachIndexed { index, rawSelectorValue ->
                val resolvedValue = resolveSelectorValue(rawSelectorValue, min, max, wrapSelectorWheel)
                val displayIndex = (resolvedValue - min).coerceIn(0, (max - min).coerceAtLeast(0))
                val label = displayed?.getOrNull(displayIndex) ?: resolvedValue.toString()

                val itemBaseline = currentScrollOffset + (index * selectorElementHeight)
                val distance = abs(selectedBaseline - itemBaseline)
                val focus = (1f - (distance / selectorElementHeight.toFloat())).coerceIn(0f, 1f)
                val easedFocus = focus * focus * (3f - 2f * focus)
                val textSize = lerpFloat(normalTextPx, selectedTextPx, easedFocus)

                drawPaint.textSize = textSize
                drawPaint.color = blendArgb(normalColor, selectedColor, easedFocus)
                canvas.drawText(label, width / 2f, itemBaseline, drawPaint)
            }
        }

        private fun getSelectorIndices(): IntArray? {
            return runCatching {
                val field = NumberPicker::class.java.getDeclaredField("mSelectorIndices")
                field.isAccessible = true
                field.get(this) as? IntArray
            }.getOrNull()
        }

        private fun getCurrentScrollOffset(): Int {
            return runCatching {
                val field = NumberPicker::class.java.getDeclaredField("mCurrentScrollOffset")
                field.isAccessible = true
                field.getInt(this)
            }.getOrDefault(0)
        }

        private fun getSelectorElementHeight(): Int {
            return runCatching {
                val field = NumberPicker::class.java.getDeclaredField("mSelectorElementHeight")
                field.isAccessible = true
                field.getInt(this)
            }.getOrDefault(0)
        }

        private fun resolveSelectorValue(
            value: Int,
            min: Int,
            max: Int,
            wrap: Boolean
        ): Int {
            if (value in min..max) {
                return value
            }
            if (!wrap || max <= min) {
                return value.coerceIn(min, max)
            }
            val range = max - min + 1
            var normalized = (value - min) % range
            if (normalized < 0) {
                normalized += range
            }
            return min + normalized
        }

        private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float {
            return start + (stop - start) * fraction
        }

        private fun blendArgb(start: Int, end: Int, fraction: Float): Int {
            val clamped = fraction.coerceIn(0f, 1f)
            val startA = android.graphics.Color.alpha(start)
            val startR = android.graphics.Color.red(start)
            val startG = android.graphics.Color.green(start)
            val startB = android.graphics.Color.blue(start)
            val endA = android.graphics.Color.alpha(end)
            val endR = android.graphics.Color.red(end)
            val endG = android.graphics.Color.green(end)
            val endB = android.graphics.Color.blue(end)
            return android.graphics.Color.argb(
                (startA + ((endA - startA) * clamped)).roundToInt(),
                (startR + ((endR - startR) * clamped)).roundToInt(),
                (startG + ((endG - startG) * clamped)).roundToInt(),
                (startB + ((endB - startB) * clamped)).roundToInt()
            )
        }
    }


    @Composable
    private fun DetailMoreMenuPanel(
        archived: Boolean,
        editing: Boolean,
        onEditToggle: () -> Unit,
        onArchiveToggle: () -> Unit,
        onAddToReminder: () -> Unit,
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
                    iconRes = R.drawable.ic_nm_reminder,
                    label = "添加到提醒事项",
                    onClick = onAddToReminder
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
