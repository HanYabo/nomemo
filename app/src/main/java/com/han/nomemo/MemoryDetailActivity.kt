package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        setContent {
            DetailContent(
                record = record,
                reanalyzing = reanalyzing,
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
            add("褰撳墠鍒嗙被: ${record.categoryName ?: "灏忚"}")
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
        onBack: () -> Unit,
        onToggleArchive: (MemoryRecord) -> Unit,
        onDelete: (MemoryRecord) -> Unit,
        onReanalyze: (MemoryRecord) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showImagePreview by remember { mutableStateOf(false) }
        var editing by remember(record?.recordId) { mutableStateOf(false) }

        BackHandler(enabled = moreMenuExpanded || showDeleteConfirm || showImagePreview || editing) {
            when {
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
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                onDispose {
                    insetsController.show(WindowInsetsCompat.Type.statusBars())
                    WindowStyleManager.apply(this@MemoryDetailActivity, provideWindowStyleConfig())
                }
            }
        }

        NoMemoBackground {
            Box(modifier = Modifier.fillMaxSize()) {
                val configuration = LocalConfiguration.current
                val screenAspectRatio = remember(configuration.screenWidthDp, configuration.screenHeightDp) {
                    val width = configuration.screenWidthDp.toFloat().coerceAtLeast(1f)
                    val height = configuration.screenHeightDp.toFloat().coerceAtLeast(1f)
                    width / height
                }
                ResponsiveContentFrame(spec = adaptive) { spec ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                end = spec.pageHorizontalPadding,
                                bottom = 20.dp
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
                            draftImageUri = uri.toString()
                        }
                        val displayImageUri = if (editing) draftImageUri else currentRecord.imageUri.orEmpty()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopStart)
                                .padding(top = 0.dp)
                                .offset(y = (-4).dp)
                                .zIndex(2f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_close,
                                contentDescription = getString(R.string.back),
                                onClick = onBack,
                                size = spec.topActionButtonSize
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_nm_more,
                                contentDescription = getString(R.string.action_more),
                                onClick = { moreMenuExpanded = true },
                                size = spec.topActionButtonSize
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spec.topActionButtonSize + 14.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                        if (displayImageUri.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.64f)
                                    .align(Alignment.CenterHorizontally)
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
                                        text = "鏇存崲鍥剧墖",
                                        primary = false,
                                        onClick = { imagePickerLauncher.launch(arrayOf("image/*")) }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    NoMemoDetailActionButton(
                                        text = "鍒犻櫎鍥剧墖",
                                        primary = false,
                                        onClick = { draftImageUri = "" }
                                    )
                                }
                            }
                        } else if (editing) {
                            NoMemoDetailActionButton(
                                text = "娣诲姞鍥剧墖",
                                primary = false,
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
                                text = currentRecord.categoryName ?: "灏忚",
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
                                modifier = Modifier.padding(start = detailTextStartPadding, top = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                NoMemoDetailActionButton(
                                    text = "取消",
                                    primary = false,
                                    onClick = {
                                        editing = false
                                        draftTitle = titleText
                                        draftSummary = summaryText
                                        draftImageUri = currentRecord.imageUri.orEmpty()
                                    }
                                )
                                NoMemoDetailActionButton(
                                    text = "保存修改",
                                    primary = true,
                                    onClick = {
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
                                )
                            }
                        } else NoMemoDetailReanalyzeButton(
                            text = if (reanalyzing) "正在重新分析..." else "重新分析",
                            enabled = !reanalyzing,
                            modifier = Modifier.padding(
                                start = detailTextStartPadding,
                                end = detailTextStartPadding,
                                top = 28.dp
                            ),
                            onClick = { onReanalyze(currentRecord) }
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                        if (moreMenuExpanded) {
                            val dismissInteraction = remember { MutableInteractionSource() }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(4f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable(
                                            interactionSource = dismissInteraction,
                                            indication = null,
                                            onClick = { moreMenuExpanded = false }
                                        )
                                )
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
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = spec.topActionButtonSize + 10.dp)
                                )
                            }
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

                val previewRecord = record?.takeIf { !it.imageUri.isNullOrBlank() }
                AnimatedVisibility(
                    visible = showImagePreview && previewRecord != null,
                    enter = fadeIn(animationSpec = tween(220)),
                    exit = fadeOut(animationSpec = tween(180)),
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(20f)
                ) {
                    val previewDismissInteraction = remember { MutableInteractionSource() }
                    var previewFillScreen by remember(previewRecord?.imageUri) { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    ) {
                        val previewVerticalInset = if (adaptive.isNarrow) 92.dp else 104.dp
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    interactionSource = previewDismissInteraction,
                                    indication = null,
                                    onClick = { showImagePreview = false }
                                )
                        )
                        AndroidView(
                                factory = { ctx ->
                                    ImageView(ctx).apply {
                                        adjustViewBounds = false
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        setBackgroundColor(android.graphics.Color.BLACK)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize()
                                    .padding(
                                        top = if (previewFillScreen) 0.dp else previewVerticalInset,
                                        bottom = if (previewFillScreen) 0.dp else previewVerticalInset
                                    ),
                                update = { imageView ->
                                    try {
                                        imageView.setImageURI(Uri.parse(previewRecord!!.imageUri))
                                        val drawable = imageView.drawable
                                        val imageAspectRatio = if (drawable != null && drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
                                            drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                                        } else {
                                            0f
                                        }
                                        val shouldFillScreen = imageAspectRatio > 0f &&
                                            isAspectRatioCloseToScreen(imageAspectRatio, screenAspectRatio)
                                        previewFillScreen = shouldFillScreen
                                        imageView.scaleType = if (shouldFillScreen) {
                                            ImageView.ScaleType.CENTER_CROP
                                        } else {
                                            ImageView.ScaleType.FIT_CENTER
                                        }
                                    } catch (_: Exception) {
                                        imageView.setImageDrawable(null)
                                        previewFillScreen = false
                                    }
                                }
                            )
                        MemoryDetailPreviewCloseButton(
                            iconRes = R.drawable.ic_sheet_close,
                            contentDescription = getString(R.string.cancel),
                            onClick = { showImagePreview = false },
                            size = if (adaptive.isNarrow) 68.dp else 76.dp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
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
        PressScaleBox(
            onClick = onClick,
            modifier = modifier.size(size)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
                    .border(1.dp, Color.White.copy(alpha = 0.10f), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = contentDescription,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
        }
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
                    R.drawable.ic_nm_more,
                    if (editing) "结束编辑" else "编辑",
                    onEditToggle
                ),
                NoMemoMenuActionItem(
                    R.drawable.ic_sheet_calendar,
                    if (archived) getString(R.string.action_unarchive) else getString(R.string.action_archive),
                    onArchiveToggle
                ),
                NoMemoMenuActionItem(
                    R.drawable.ic_nm_delete,
                    getString(R.string.action_delete),
                    onDelete
                )
            ),
            modifier = modifier
        )
    }
}
