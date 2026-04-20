package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.SystemClock
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Transition
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import androidx.lifecycle.lifecycleScope
import me.saket.telephoto.zoomable.DoubleClickToZoomListener
import me.saket.telephoto.zoomable.EnabledZoomGestures
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.roundToInt

private object MemoryDetailReanalyzeScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
}

private const val MinReanalyzeFeedbackMs = 650L
private const val ReanalyzeStateRevealDelayMs = 120L

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
    private lateinit var settingsStore: SettingsStore
    private lateinit var aiMemoryService: AiMemoryService
    private var aiEnabled by mutableStateOf(false)
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
        settingsStore = SettingsStore(this)
        aiMemoryService = AiMemoryService(this)
        aiEnabled = settingsStore.isAiAvailable()
        loadRecordOrFinish()
        val statusBarResourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeightPx = if (statusBarResourceId > 0) resources.getDimensionPixelSize(statusBarResourceId) else 0
        setContent {
            DetailContent(
                record = record,
                aiEnabled = aiEnabled,
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
        aiEnabled = settingsStore.isAiAvailable()
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

    private fun openMemoryPage() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        switchPrimaryPage(intent)
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
        if (nextArchived) {
            openMemoryPage()
        }
    }

    private fun deleteRecord(currentRecord: MemoryRecord) {
        val deleted = memoryStore.deleteRecord(currentRecord.recordId)
        if (deleted) {
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private data class EditableStructuredFields(
        val code: String = "",
        val primaryValue: String = "",
        val secondaryValue: String = "",
        val locationText: String = ""
    )

    private data class StructuredCategoryPresentation(
        val sectionTitle: String,
        val primaryLabel: String,
        val secondaryLabel: String
    )

    private fun structuredPresentation(categoryCode: String): StructuredCategoryPresentation? {
        return when (categoryCode) {
            CategoryCatalog.CODE_LIFE_DELIVERY -> StructuredCategoryPresentation(
                sectionTitle = "取件码",
                primaryLabel = "快递公司",
                secondaryLabel = "取件地址"
            )

            CategoryCatalog.CODE_LIFE_PICKUP -> StructuredCategoryPresentation(
                sectionTitle = "取餐码",
                primaryLabel = "店铺",
                secondaryLabel = "商品"
            )

            else -> null
        }
    }

    private fun resolveCategoryOption(record: MemoryRecord): CategoryCatalog.CategoryOption {
        return CategoryCatalog.getAllCategories().firstOrNull { it.categoryCode == record.categoryCode }
            ?: CategoryCatalog.CategoryOption(
                CategoryCatalog.getGroupByCategoryCode(record.categoryCode),
                record.categoryCode,
                record.categoryName ?: CategoryCatalog.getCategoryName(record.categoryCode)
            )
    }

    private fun sanitizeStructuredDraftValue(value: String?): String {
        val normalized = value?.trim().orEmpty()
        return if (normalized == "未识别") "" else normalized
    }

    private fun buildEditableStructuredFields(
        categoryCode: String,
        info: StructuredPickupInfo?
    ): EditableStructuredFields {
        if (structuredPresentation(categoryCode) == null) {
            return EditableStructuredFields()
        }
        return EditableStructuredFields(
            code = sanitizeStructuredDraftValue(info?.code),
            primaryValue = sanitizeStructuredDraftValue(info?.primaryValue),
            secondaryValue = sanitizeStructuredDraftValue(info?.secondaryValue),
            locationText = sanitizeStructuredDraftValue(info?.locationText)
        )
    }

    private fun buildStructuredOverrideText(
        category: CategoryCatalog.CategoryOption,
        currentRecord: MemoryRecord,
        draft: EditableStructuredFields?
    ): String? {
        val presentation = structuredPresentation(category.categoryCode) ?: return null
        val existing = buildEditableStructuredFields(
            category.categoryCode,
            MemoryDetailParser.parseStructuredPickupInfo(currentRecord)
        )
        val effective = draft ?: existing
        val code = effective.code.trim().ifBlank { existing.code }
        val primaryValue = effective.primaryValue.trim()
        val secondaryValue = effective.secondaryValue.trim()
        val locationText = effective.locationText.trim()
        return buildList {
            add("${presentation.sectionTitle}：$code")
            add("${presentation.primaryLabel}：$primaryValue")
            add("${presentation.secondaryLabel}：$secondaryValue")
            add("地点：$locationText")
        }.joinToString("\n")
    }

    private fun saveEditedRecord(
        currentRecord: MemoryRecord,
        title: String,
        summary: String,
        imageUri: String?,
        category: CategoryCatalog.CategoryOption,
        structuredFields: EditableStructuredFields?
    ): Boolean {
        val trimmedDetail = summary.trim()
        val structuredOverride = buildStructuredOverrideText(category, currentRecord, structuredFields)
        val updated = MemoryRecord(
            currentRecord.recordId,
            currentRecord.createdAt,
            currentRecord.mode,
            title.trim().ifBlank { deriveTitle(currentRecord) },
            if (currentRecord.mode == MemoryRecord.MODE_AI) currentRecord.summary else trimmedDetail,
            structuredOverride ?: currentRecord.sourceText,
            structuredOverride ?: currentRecord.note,
            imageUri.orEmpty(),
            if (currentRecord.mode == MemoryRecord.MODE_AI) trimmedDetail else currentRecord.analysis,
            currentRecord.memory,
            currentRecord.engine,
            category.groupCode,
            category.categoryCode,
            category.categoryName,
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
        val recordId = currentRecord.recordId
        if (!aiEnabled || reanalyzing || AiProcessingStateRegistry.isProcessing(recordId)) {
            return
        }
        val previousRecord = normalizeStableAiRecord(currentRecord)
        val appContext = applicationContext
        AiProcessingStateRegistry.markProcessing(recordId, attempt = 1)
        reanalyzing = true
        MemoryDetailReanalyzeScope.scope.launch {
            val startedAt = SystemClock.elapsedRealtime()
            delay(ReanalyzeStateRevealDelayMs)
            try {
                val updated = withContext(Dispatchers.IO) {
                    try {
                        val service = AiMemoryService(appContext)
                        val input = buildAiInput(previousRecord)
                        val imageUri = previousRecord.imageUri
                            ?.takeIf { it.isNotBlank() }
                            ?.let(Uri::parse)
                        val result = service.generateEnhancedMemoryStrict(
                            input,
                            imageUri,
                            buildEnhancedAiContext(previousRecord)
                        ) { attempt, _ ->
                            runOnUiThread {
                                AiProcessingStateRegistry.markProcessing(recordId, attempt)
                            }
                        }
                        val resolvedTitle = result.title
                            .trim()
                            .takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
                            ?: deriveTitle(previousRecord)
                        val resolvedSummary = result.summary
                            .trim()
                            .takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
                            ?: deriveSummary(previousRecord)
                        val resolvedAnalysis = result.analysis
                            .trim()
                            .takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
                            ?: previousRecord.analysis
                                ?.trim()
                                ?.takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
                                ?: deriveSummary(previousRecord)
                        val resolvedEngine = result.engine
                            .trim()
                            .takeIf { it.isNotEmpty() && !it.equals("pending", ignoreCase = true) }
                            ?: "local"
                        MemoryRecord(
                            previousRecord.recordId,
                            previousRecord.createdAt,
                            previousRecord.mode,
                            resolvedTitle,
                            resolvedSummary,
                            previousRecord.sourceText,
                            previousRecord.note,
                            previousRecord.imageUri,
                            resolvedAnalysis,
                            result.memory.takeIf { it.isNotBlank() } ?: previousRecord.memory,
                            resolvedEngine,
                            previousRecord.categoryGroupCode,
                            previousRecord.categoryCode,
                            previousRecord.categoryName,
                            previousRecord.reminderAt,
                            previousRecord.isReminderDone,
                            previousRecord.isArchived
                        )
                    } catch (_: Exception) {
                        null
                    }
                }
                if (updated == null) {
                    waitForMinimumReanalyzeFeedback(startedAt)
                    withContext(Dispatchers.Main) {
                        reanalyzing = false
                        Toast.makeText(appContext, "重新分析失败", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val updateSuccess = withContext(Dispatchers.IO) {
                    memoryStore.updateRecord(updated)
                }
                if (!updateSuccess) {
                    waitForMinimumReanalyzeFeedback(startedAt)
                    withContext(Dispatchers.Main) {
                        reanalyzing = false
                        Toast.makeText(appContext, "重新分析失败", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                waitForMinimumReanalyzeFeedback(startedAt)
                withContext(Dispatchers.Main) {
                    reanalyzing = false
                    Toast.makeText(appContext, "已重新分析", Toast.LENGTH_SHORT).show()
                    if (record?.recordId == recordId) {
                        loadRecordOrFinish()
                    }
                }
            } finally {
                withContext(Dispatchers.Main.immediate) {
                    reanalyzing = false
                    AiProcessingStateRegistry.clearProcessing(recordId)
                }
            }
        }
    }

    private suspend fun waitForMinimumReanalyzeFeedback(startedAt: Long) {
        val elapsed = SystemClock.elapsedRealtime() - startedAt
        val remaining = MinReanalyzeFeedbackMs - elapsed
        if (remaining > 0L) {
            delay(remaining)
        }
    }

    private fun normalizeStableAiRecord(record: MemoryRecord): MemoryRecord {
        if (record.mode != MemoryRecord.MODE_AI) {
            return record
        }
        val normalizedTitle = record.title
            ?.trim()
            ?.takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
            ?: deriveTitle(record)
        val normalizedSummary = record.summary
            ?.trim()
            ?.takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
            ?: deriveSummary(record)
        val normalizedAnalysis = record.analysis
            ?.trim()
            ?.takeIf { it.isNotEmpty() && !isAiPlaceholderText(it) }
            ?: deriveSummary(record)
        val normalizedEngine = record.engine
            ?.trim()
            ?.takeIf { it.isNotEmpty() && !it.equals("pending", ignoreCase = true) }
            ?: "local"
        if (
            normalizedTitle == record.title
            && normalizedSummary == record.summary
            && normalizedAnalysis == record.analysis
            && normalizedEngine == record.engine
        ) {
            return record
        }
        return MemoryRecord(
            record.recordId,
            record.createdAt,
            record.mode,
            normalizedTitle,
            normalizedSummary,
            record.sourceText,
            record.note,
            record.imageUri,
            normalizedAnalysis,
            record.memory,
            normalizedEngine,
            record.categoryGroupCode,
            record.categoryCode,
            record.categoryName,
            record.reminderAt,
            record.isReminderDone,
            record.isArchived
        )
    }

    private fun isAiPlaceholderText(value: String?): Boolean {
        val normalized = value?.trim().orEmpty()
        if (normalized.isEmpty()) return false
        return normalized == "AI 分析中"
            || normalized == "AI分析中"
            || normalized == "AI 分析中..."
            || normalized == "AI分析中..."
            || normalized == "分析中"
            || normalized == "分析中..."
    }

    private fun buildAiInput(record: MemoryRecord): String {
        val economyMode = settingsStore.economyMode
        val parts = buildList {
            record.sourceText
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.let { add(compactAiField(it, if (economyMode) 240 else 1200)) }
            record.note
                ?.trim()
                ?.takeIf { it.isNotEmpty() && it != record.sourceText }
                ?.let { add(compactAiField(it, if (economyMode) 120 else 600)) }
            record.memory
                ?.trim()
                ?.takeIf { it.isNotEmpty() && it != record.sourceText }
                ?.let { add(compactAiField(it, if (economyMode) 120 else 600)) }
        }
        return parts.joinToString("\n")
            .ifBlank { deriveTitle(record) }
            .let { compactAiField(it, if (economyMode) 320 else 1800) }
    }

    private fun buildEnhancedAiContext(record: MemoryRecord): String {
        val economyMode = settingsStore.economyMode
        val parts = buildList {
            add("当前分类: ${record.categoryName ?: "小记"}")
            record.title?.trim()?.takeIf { it.isNotEmpty() }?.let {
                add("现有标题: ${compactAiField(it, if (economyMode) 24 else 80)}")
            }
            record.summary?.trim()?.takeIf { it.isNotEmpty() }?.let {
                add("现有摘要: ${compactAiField(it, if (economyMode) 42 else 140)}")
            }
            if (economyMode) {
                record.analysis?.trim()?.takeIf { it.isNotEmpty() }?.let {
                    add("分析要点: ${compactAiField(it, 60)}")
                }
                record.memory?.trim()?.takeIf { it.isNotEmpty() }?.let {
                    add("正文片段: ${compactAiField(it, 80)}")
                }
            } else {
                record.analysis?.trim()?.takeIf { it.isNotEmpty() }?.let { add("现有分析: $it") }
                record.memory?.trim()?.takeIf { it.isNotEmpty() }?.let { add("现有记忆正文: $it") }
            }
        }
        return parts.joinToString("\n")
            .let { compactAiField(it, if (economyMode) 260 else 2200) }
    }

    private fun compactAiField(value: String, maxLength: Int): String {
        val normalized = value
            .replace("\r", " ")
            .replace("\n", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        return if (normalized.length <= maxLength) {
            normalized
        } else {
            normalized.take(maxLength).trim()
        }
    }

    @Composable
    private fun DetailContent(
        record: MemoryRecord?,
        aiEnabled: Boolean,
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
        val view = LocalView.current
        val configuration = LocalConfiguration.current
        val statusBarHeightDp = with(density) { statusBarHeightPx.toDp() }
        val previewRequestWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
        val previewRequestHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
        var moreMenuExpanded by remember { mutableStateOf(false) }
        var moreMenuAnchorBounds by remember { mutableStateOf<IntRect?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }
        var showEditExitConfirm by remember { mutableStateOf(false) }
        var deleteTargetTitle by remember { mutableStateOf("") }
        var resetEditDraftsRef by remember { mutableStateOf<(() -> Unit)?>(null) }
        var hasPendingEditChanges by remember(record?.recordId) { mutableStateOf(false) }
        var showReminderSetupSheet by remember { mutableStateOf(false) }
        var showImagePreview by remember { mutableStateOf(false) }
        var showPreviewActionMenu by remember { mutableStateOf(false) }
        var previewActionMenuAnchorBounds by remember { mutableStateOf<androidx.compose.ui.unit.IntRect?>(null) }
        var editing by remember(record?.recordId) { mutableStateOf(false) }
        var imageCardBounds by remember(record?.recordId) { mutableStateOf(Rect.Zero) }
        val previewRecord = record?.takeIf { !it.imageUri.isNullOrBlank() }
        val previewRequest = remember(
            previewRecord?.recordId,
            previewRecord?.imageUri,
            previewRequestWidthPx,
            previewRequestHeightPx
        ) {
            previewRecord?.imageUri?.let { imageUri ->
                ImageRequest.Builder(this@MemoryDetailActivity)
                    .data(imageUri)
                    .size(previewRequestWidthPx, previewRequestHeightPx)
                    .build()
            }
        }
        var previewImageAspectRatio by remember(previewRecord?.recordId, previewRecord?.imageUri) { mutableStateOf<Float?>(null) }
        var previewImageMetadataResolved by remember(previewRecord?.recordId, previewRecord?.imageUri) {
            mutableStateOf(previewRecord == null)
        }
        var previewImagePrepared by remember(
            previewRecord?.recordId,
            previewRecord?.imageUri,
            previewRequestWidthPx,
            previewRequestHeightPx
        ) {
            mutableStateOf(previewRequest == null)
        }
        var previewOpenPending by remember(previewRecord?.recordId, previewRecord?.imageUri) {
            mutableStateOf(false)
        }
        val previewTransition = updateTransition(
            targetState = showImagePreview && previewRecord != null,
            label = "memoryDetailPreview"
        )
        val previewOverlayVisible = previewTransition.currentState || previewTransition.targetState
        val previewSourceCardHidden = previewOverlayVisible
        val previewImageReady = previewImagePrepared && previewImageMetadataResolved

        LaunchedEffect(previewRecord?.recordId, previewRecord?.imageUri) {
            if (previewRecord == null) {
                previewImageAspectRatio = null
                previewImageMetadataResolved = true
                return@LaunchedEffect
            }
            previewImageMetadataResolved = false
            previewImageAspectRatio = withContext(Dispatchers.IO) {
                resolveImageAspectRatio(previewRecord.imageUri)
            }
            previewImageMetadataResolved = true
        }

        LaunchedEffect(previewRequest) {
            if (previewRequest == null) {
                previewImagePrepared = true
                return@LaunchedEffect
            }
            previewImagePrepared = false
            runCatching {
                SingletonImageLoader.get(applicationContext).execute(previewRequest)
            }
            previewImagePrepared = true
        }

        LaunchedEffect(previewImageReady, previewOpenPending, previewRecord?.recordId) {
            if (previewImageReady && previewOpenPending && previewRecord != null) {
                previewOpenPending = false
                showImagePreview = true
            }
        }

        LaunchedEffect(showImagePreview) {
            if (!showImagePreview) {
                showPreviewActionMenu = false
            }
        }

        BackHandler(enabled = moreMenuExpanded || showDeleteConfirm || showEditExitConfirm || showReminderSetupSheet || showImagePreview || editing || showPreviewActionMenu) {
            when {
                showPreviewActionMenu -> showPreviewActionMenu = false
                showImagePreview -> showImagePreview = false
                showDeleteConfirm -> showDeleteConfirm = false
                showEditExitConfirm -> showEditExitConfirm = false
                showReminderSetupSheet -> showReminderSetupSheet = false
                moreMenuExpanded -> moreMenuExpanded = false
                editing -> if (hasPendingEditChanges) showEditExitConfirm = true else editing = false
            }
        }

        if (previewOverlayVisible) {
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
                ResponsiveContentFrame(spec = adaptive) { spec ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = statusBarHeightDp)
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
                        val sectionTitleStartPadding = detailTextStartPadding + 4.dp
                        val sectionTitleTopSpacing = 22.dp
                        val sectionCardTopSpacing = 8.dp
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
                        val allCategories = remember { CategoryCatalog.getAllCategories() }
                        val initialCategory = remember(
                            currentRecord.recordId,
                            currentRecord.categoryCode,
                            currentRecord.categoryName
                        ) {
                            resolveCategoryOption(currentRecord)
                        }
                        var draftCategory by remember(
                            currentRecord.recordId,
                            currentRecord.categoryCode,
                            currentRecord.categoryName
                        ) {
                            mutableStateOf(initialCategory)
                        }
                        var categoryMenuExpanded by remember(currentRecord.recordId) {
                            mutableStateOf(false)
                        }
                        val initialStructuredFields = remember(
                            currentRecord.recordId,
                            currentRecord.categoryCode,
                            pickupInfo?.code,
                            pickupInfo?.primaryValue,
                            pickupInfo?.secondaryValue,
                            pickupInfo?.locationText
                        ) {
                            buildEditableStructuredFields(currentRecord.categoryCode, pickupInfo)
                        }
                        var draftStructuredFields by remember(
                            currentRecord.recordId,
                            currentRecord.categoryCode,
                            pickupInfo?.code,
                            pickupInfo?.primaryValue,
                            pickupInfo?.secondaryValue,
                            pickupInfo?.locationText
                        ) {
                            mutableStateOf(initialStructuredFields)
                        }
                        val activeStructuredPresentation = remember(draftCategory.categoryCode) {
                            structuredPresentation(draftCategory.categoryCode)
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
                        var detailImageAspectRatio by remember(currentRecord.recordId, displayImageUri) {
                            mutableStateOf<Float?>(null)
                        }
                        LaunchedEffect(displayImageUri) {
                            detailImageAspectRatio = withContext(Dispatchers.IO) {
                                resolveImageAspectRatio(displayImageUri)
                            }
                        }
                        val detailImageWidthFraction = remember(detailImageAspectRatio) {
                            detailImageCardWidthFraction(detailImageAspectRatio)
                        }
                        val detailImageDisplayAspectRatio = detailImageAspectRatio ?: 1f
                        val collapsedTitleText = if (editing) draftTitle.ifBlank { titleText } else titleText
                        val localDeleteTargetTitle = titleText
                            .replace("\\s+".toRegex(), " ")
                            .trim()
                            .ifBlank { "未命名记忆" }
                        deleteTargetTitle = localDeleteTargetTitle
                        val hasEditDraftChanges by remember(
                            draftTitle,
                            draftSummary,
                            draftImageUri,
                            draftCategory,
                            draftStructuredFields,
                            titleText,
                            summaryText,
                            currentRecord.imageUri,
                            initialCategory,
                            initialStructuredFields,
                            activeStructuredPresentation
                        ) {
                            derivedStateOf {
                                normalizeDetailDraftText(draftTitle) != normalizeDetailDraftText(titleText) ||
                                    normalizeDetailDraftText(draftSummary) != normalizeDetailDraftText(summaryText) ||
                                    normalizeDetailDraftText(draftImageUri) != normalizeDetailDraftText(currentRecord.imageUri.orEmpty()) ||
                                    draftCategory.groupCode != initialCategory.groupCode ||
                                    draftCategory.categoryCode != initialCategory.categoryCode ||
                                    draftCategory.categoryName != initialCategory.categoryName ||
                                    (activeStructuredPresentation != null && hasStructuredDraftChanges(draftStructuredFields, initialStructuredFields))
                            }
                        }
                        LaunchedEffect(hasEditDraftChanges, editing) {
                            hasPendingEditChanges = editing && hasEditDraftChanges
                        }
                        val resetEditDrafts = {
                            draftTitle = titleText
                            draftSummary = summaryText
                            draftImageUri = currentRecord.imageUri.orEmpty()
                            draftCategory = initialCategory
                            draftStructuredFields = initialStructuredFields
                            categoryMenuExpanded = false
                            showEditExitConfirm = false
                        }
                        resetEditDraftsRef = resetEditDrafts
                        val requestExitEditing = {
                            categoryMenuExpanded = false
                            if (hasEditDraftChanges) {
                                showEditExitConfirm = true
                            } else {
                                resetEditDrafts()
                                editing = false
                            }
                        }
                        val commitEdits = {
                            val saved = saveEditedRecord(
                                currentRecord,
                                draftTitle,
                                draftSummary,
                                draftImageUri,
                                draftCategory,
                                draftStructuredFields.takeIf { activeStructuredPresentation != null }
                            )
                            if (saved) {
                                editing = false
                                categoryMenuExpanded = false
                                showEditExitConfirm = false
                                Toast.makeText(this@MemoryDetailActivity, "已保存修改", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MemoryDetailActivity, "保存失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = spec.pageHorizontalPadding,
                                    top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                                    end = spec.pageHorizontalPadding
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(spec.topActionButtonSize)
                                    .align(Alignment.TopStart)
                                    .zIndex(6f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_back,
                                    contentDescription = getString(R.string.back),
                                    onClick = {
                                        if (editing) {
                                            requestExitEditing()
                                        } else {
                                            onBack()
                                        }
                                    },
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
                                    size = spec.topActionButtonSize,
                                    onBoundsChanged = { moreMenuAnchorBounds = it }
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
                                    .fillMaxWidth(detailImageWidthFraction)
                                    .align(Alignment.CenterHorizontally)
                                    .onGloballyPositioned { coordinates ->
                                        imageCardBounds = coordinates.boundsInRoot()
                                    }
                                    .alpha(if (previewSourceCardHidden) 0f else 1f)
                                    .clickable {
                                        if (editing) {
                                            imagePickerLauncher.launch(arrayOf("image/*"))
                                        } else {
                                            if (previewImageReady) {
                                                showImagePreview = true
                                            } else {
                                                previewOpenPending = true
                                            }
                                        }
                                    },
                                shape = noMemoG2RoundedShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = noMemoCardSurfaceColor(isSystemInDarkTheme(), palette.glassFill)),
                                border = BorderStroke(1.dp, palette.glassStroke)
                            ) {
                                AsyncImage(
                                    model = remember(displayImageUri) {
                                        ImageRequest.Builder(this@MemoryDetailActivity)
                                            .data(displayImageUri)
                                            .build()
                                    },
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(detailImageDisplayAspectRatio)
                                        .clip(noMemoG2RoundedShape(28.dp))
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
                            NoMemoDetailTitleEditor(
                                value = draftTitle,
                                modifier = Modifier.padding(
                                    start = detailTextStartPadding,
                                    end = detailTextStartPadding,
                                    top = 20.dp
                                ),
                                onValueChange = { draftTitle = it }
                            )
                        } else {
                            Text(
                                text = titleText,
                                color = palette.textPrimary,
                                fontSize = if (spec.isNarrow) 26.sp else 30.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = if (spec.isNarrow) 34.sp else 38.sp,
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
                                categoryCode = if (editing) draftCategory.categoryCode else currentRecord.categoryCode,
                                categoryText = if (editing) draftCategory.categoryName else currentRecord.categoryName ?: "小记",
                                metaColor = metaColor
                            )
                        }

                        Text(
                            text = "摘要",
                            color = palette.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = sectionTitleStartPadding, top = sectionTitleTopSpacing)
                        )

                        NoMemoDetailSummaryBox(
                            value = if (editing) draftSummary else summaryText,
                            editing = editing,
                            modifier = Modifier.padding(
                                start = detailTextStartPadding,
                                end = detailTextStartPadding,
                                top = sectionCardTopSpacing
                            ),
                            onValueChange = { draftSummary = it }
                        )

                        if (editing && activeStructuredPresentation != null) {
                                Text(
                                    text = activeStructuredPresentation.sectionTitle,
                                    color = palette.textPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = sectionTitleStartPadding, top = sectionTitleTopSpacing)
                                )
                            NoMemoEditablePickupCodeCard(
                                code = draftStructuredFields.code,
                                primaryLabel = activeStructuredPresentation.primaryLabel,
                                primaryValue = draftStructuredFields.primaryValue,
                                secondaryLabel = activeStructuredPresentation.secondaryLabel,
                                secondaryValue = draftStructuredFields.secondaryValue,
                                    modifier = Modifier.padding(
                                        start = detailTextStartPadding,
                                        end = detailTextStartPadding,
                                        top = sectionCardTopSpacing
                                    ),
                                onCodeChange = {
                                    draftStructuredFields = draftStructuredFields.copy(code = it)
                                },
                                onPrimaryValueChange = {
                                    draftStructuredFields = draftStructuredFields.copy(primaryValue = it)
                                },
                                onSecondaryValueChange = {
                                    draftStructuredFields = draftStructuredFields.copy(secondaryValue = it)
                                }
                            )
                            Text(
                                text = "地点",
                                color = palette.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = sectionTitleStartPadding, top = sectionTitleTopSpacing)
                            )
                            NoMemoEditablePickupLocationCard(
                                value = draftStructuredFields.locationText,
                                modifier = Modifier.padding(
                                    start = detailTextStartPadding,
                                    end = detailTextStartPadding,
                                    top = sectionCardTopSpacing
                                ),
                                onValueChange = {
                                    draftStructuredFields = draftStructuredFields.copy(locationText = it)
                                }
                            )
                        } else if (!editing) {
                            pickupInfo?.let { info ->
                                Text(
                                    text = info.sectionTitle,
                                    color = palette.textPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = sectionTitleStartPadding, top = sectionTitleTopSpacing)
                                )
                                NoMemoPickupCodeCard(
                                    info = info,
                                    modifier = Modifier.padding(
                                        start = detailTextStartPadding,
                                        end = detailTextStartPadding,
                                        top = sectionCardTopSpacing
                                    )
                                )
                                if (!info.locationText.isNullOrBlank()) {
                                    Text(
                                        text = "地点",
                                        color = palette.textPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = sectionTitleStartPadding, top = sectionTitleTopSpacing)
                                    )
                                    NoMemoPickupLocationCard(
                                        info = info,
                                        modifier = Modifier.padding(
                                            start = detailTextStartPadding,
                                            end = detailTextStartPadding,
                                            top = sectionCardTopSpacing
                                        ),
                                        onNavigate = { query -> openNavigation(query) }
                                    )
                                }
                            }
                        }

                        if (editing) {
                            Text(
                                text = "分类",
                                color = palette.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = sectionTitleStartPadding, top = sectionTitleTopSpacing)
                            )
                            SheetCategorySection(
                                categories = allCategories,
                                selectedCategory = draftCategory,
                                expanded = categoryMenuExpanded,
                                detailStyle = true,
                                modifier = Modifier.padding(
                                    start = detailTextStartPadding,
                                    end = detailTextStartPadding,
                                    top = sectionCardTopSpacing
                                ),
                                onToggleExpanded = { categoryMenuExpanded = !categoryMenuExpanded },
                                onSelectCategory = {
                                    draftCategory = it
                                    categoryMenuExpanded = false
                                }
                            )
                        } else if (aiEnabled) {
                            val buttonProcessing = reanalyzing || AiProcessingStateRegistry.isProcessing(currentRecord.recordId)
                            NoMemoDetailReanalyzeButton(
                                text = when {
                                    buttonProcessing && currentRecord.mode == MemoryRecord.MODE_AI -> "正在重新分析"
                                    buttonProcessing -> "正在AI分析"
                                    currentRecord.mode == MemoryRecord.MODE_AI -> "重新分析"
                                    else -> "AI分析"
                                },
                                processing = buttonProcessing,
                                modifier = Modifier.padding(
                                    start = detailTextStartPadding,
                                    end = detailTextStartPadding,
                                    top = 28.dp,
                                ),
                                onClick = { onReanalyze(currentRecord) }
                            )
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                        }

                        NoMemoAnchoredMenu(
                            expanded = moreMenuExpanded,
                            onDismissRequest = { moreMenuExpanded = false },
                            anchorBounds = moreMenuAnchorBounds,
                            actions = listOf(
                                NoMemoMenuActionItem(
                                    iconRes = R.drawable.ic_nm_edit,
                                    label = if (editing) "结束编辑" else "编辑内容",
                                    onClick = {
                                        moreMenuExpanded = false
                                        if (editing) {
                                            requestExitEditing()
                                        } else {
                                            editing = true
                                        }
                                    }
                                ),
                                NoMemoMenuActionItem(
                                    iconRes = R.drawable.ic_sheet_calendar,
                                    label = if (currentRecord.isArchived) getString(R.string.action_unarchive) else getString(R.string.action_archive),
                                    onClick = {
                                        moreMenuExpanded = false
                                        onToggleArchive(currentRecord)
                                    }
                                ),
                                NoMemoMenuActionItem(
                                    iconRes = R.drawable.ic_nm_reminder,
                                    label = "添加到提醒事项",
                                    onClick = {
                                        moreMenuExpanded = false
                                        showReminderSetupSheet = true
                                    }
                                ),
                                NoMemoMenuActionItem(
                                    iconRes = R.drawable.ic_nm_delete,
                                    label = getString(R.string.action_delete),
                                    destructive = true,
                                    onClick = {
                                        moreMenuExpanded = false
                                        showDeleteConfirm = true
                                    }
                                )
                            )
                        )
                    }
                }

                if (previewRecord != null && previewOverlayVisible) {
                    MemoryDetailImagePreviewOverlay(
                        transition = previewTransition,
                        previewRecord = previewRecord,
                        previewRequest = previewRequest,
                        previewImageAspectRatio = previewImageAspectRatio,
                        imageCardBounds = imageCardBounds,
                        adaptive = adaptive,
                        statusBarHeightDp = statusBarHeightDp,
                        showPreviewActionMenu = showPreviewActionMenu,
                        previewActionMenuAnchorBounds = previewActionMenuAnchorBounds,
                        onTogglePreviewActionMenu = {
                            showPreviewActionMenu = !showPreviewActionMenu
                        },
                        onPreviewActionMenuDismiss = { showPreviewActionMenu = false },
                        onPreviewActionMenuAnchorBoundsChanged = { previewActionMenuAnchorBounds = it },
                        onDismiss = { showImagePreview = false },
                        onSavePreviewImage = {
                            showPreviewActionMenu = false
                            savePreviewImage(previewRecord.imageUri.orEmpty())
                        },
                        onSharePreviewImage = {
                            showPreviewActionMenu = false
                            sharePreviewImage(previewRecord.imageUri.orEmpty())
                        }
                    )
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

            if (showDeleteConfirm && record != null) {
                NoMemoDeleteConfirmDialog(
                    title = "确认删除",
                    message = "确定删除\u201C${deleteTargetTitle}\u201D这条记忆吗？",
                    onConfirm = {
                        showDeleteConfirm = false
                        onDelete(record)
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }

            if (showEditExitConfirm) {
                NoMemoConfirmDialog(
                    title = "放弃修改？",
                    message = "当前编辑内容尚未保存，离开后这些修改会丢失。",
                    confirmText = "放弃修改",
                    dismissText = "继续编辑",
                    destructive = true,
                    onConfirm = {
                        resetEditDraftsRef?.invoke()
                        editing = false
                    },
                    onDismiss = { showEditExitConfirm = false }
                )
            }
        }
        }
    }

    @Composable
    private fun MemoryDetailImagePreviewOverlay(
        transition: Transition<Boolean>,
        previewRecord: MemoryRecord,
        previewRequest: ImageRequest?,
        previewImageAspectRatio: Float?,
        imageCardBounds: Rect,
        adaptive: NoMemoAdaptiveSpec,
        statusBarHeightDp: Dp,
        showPreviewActionMenu: Boolean,
        previewActionMenuAnchorBounds: IntRect?,
        onTogglePreviewActionMenu: () -> Unit,
        onPreviewActionMenuDismiss: () -> Unit,
        onPreviewActionMenuAnchorBoundsChanged: (IntRect) -> Unit,
        onDismiss: () -> Unit,
        onSavePreviewImage: () -> Unit,
        onSharePreviewImage: () -> Unit
    ) {
        val density = LocalDensity.current
        val configuration = LocalConfiguration.current
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
        val screenAspectRatio = remember(configuration.screenWidthDp, configuration.screenHeightDp) {
            val width = configuration.screenWidthDp.toFloat().coerceAtLeast(1f)
            val height = configuration.screenHeightDp.toFloat().coerceAtLeast(1f)
            width / height
        }
        val sourceCornerRadiusDp = 28.dp
        val sourceRect = remember(imageCardBounds, screenWidthPx, screenHeightPx) {
            if (imageCardBounds.isEmpty) {
                Rect(
                    left = screenWidthPx * 0.18f,
                    top = screenHeightPx * 0.28f,
                    right = screenWidthPx * 0.82f,
                    bottom = screenHeightPx * 0.52f
                )
            } else {
                imageCardBounds
            }
        }
        val previewFillScreen = previewImageAspectRatio != null &&
            isAspectRatioCloseToScreen(previewImageAspectRatio, screenAspectRatio)
        val animationTargetRect = remember(
            screenWidthPx,
            screenHeightPx,
            previewImageAspectRatio,
            previewFillScreen
        ) {
            buildPreviewDisplayedImageBounds(
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                imageAspectRatio = previewImageAspectRatio,
                fillScreen = previewFillScreen
            )
        }
        val previewAlpha by transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = 280, easing = FastOutSlowInEasing)
            },
            label = "previewAlpha"
        ) { expanded -> if (expanded) 1f else 0f }
        val previewProgress by transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = 280, easing = FastOutSlowInEasing)
            },
            label = "previewProgress"
        ) { expanded -> if (expanded) 1f else 0f }
        val buttonAlpha by transition.animateFloat(
            transitionSpec = {
                tween(durationMillis = 180, delayMillis = if (targetState) 90 else 0)
            },
            label = "previewButtonAlpha"
        ) { expanded -> if (expanded) 1f else 0f }
        val animatedLeft = lerp(sourceRect.left, animationTargetRect.left, previewProgress)
        val animatedTop = lerp(sourceRect.top, animationTargetRect.top, previewProgress)
        val animatedWidth = lerp(sourceRect.width, animationTargetRect.width, previewProgress)
        val animatedHeight = lerp(sourceRect.height, animationTargetRect.height, previewProgress)
        val animatedCornerRadius = with(density) {
            ((1f - previewProgress) * sourceCornerRadiusDp.value).dp
        }
        val previewInteractionSource = remember(previewRecord.recordId, previewRecord.imageUri) {
            MutableInteractionSource()
        }
        val previewButtonSize = adaptive.topActionButtonSize
        val previewCloseButtonSize = if (adaptive.isNarrow) 68.dp else 76.dp
        val previewZoomableImageState = key(
            previewRecord.recordId,
            previewRecord.imageUri
        ) {
            rememberZoomableImageState(
                rememberZoomableState(
                    zoomSpec = ZoomSpec(maxZoomFactor = 3.5f)
                )
            )
        }
        val previewDoubleClickZoom = remember {
            DoubleClickToZoomListener.cycle(maxZoomFactor = 2.2f)
        }
        val previewZoomableVisible = transition.currentState && transition.targetState
        val previewZoomGestures = EnabledZoomGestures.ZoomAndPan
        val previewBaseAlpha = if (previewZoomableVisible) 0f else 1f
        val previewZoomableAlpha = if (previewZoomableVisible) 1f else 0f
        val animatedRect = Rect(
            left = animatedLeft,
            top = animatedTop,
            right = animatedLeft + animatedWidth,
            bottom = animatedTop + animatedHeight
        )
        val renderedRect = animatedRect
        val renderedCornerRadiusDp = animatedCornerRadius
        val renderedBackdropAlpha = previewAlpha.coerceIn(0f, 1f)
        val renderedButtonAlpha = buttonAlpha.coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(20f)
                .background(Color.Black.copy(alpha = renderedBackdropAlpha))
                .clickable(
                    interactionSource = previewInteractionSource,
                    indication = null
                ) { onDismiss() }
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = renderedRect.left.roundToInt(),
                            y = renderedRect.top.roundToInt()
                        )
                    }
                    .size(
                        width = with(density) { renderedRect.width.toDp() },
                        height = with(density) { renderedRect.height.toDp() }
                    )
                    .graphicsLayer(alpha = previewBaseAlpha, compositingStrategy = CompositingStrategy.Offscreen)
                    .clip(noMemoG2RoundedShape(renderedCornerRadiusDp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = previewRequest,
                        contentDescription = "预览图片",
                        contentScale = if (previewFillScreen) ContentScale.Crop else ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(previewZoomableAlpha)
            ) {
                ZoomableAsyncImage(
                    model = previewRequest,
                    state = previewZoomableImageState,
                    gestures = if (previewZoomableAlpha > 0.5f) {
                        previewZoomGestures
                    } else {
                        EnabledZoomGestures.None
                    },
                    contentDescription = "预览图片",
                    contentScale = if (previewFillScreen) ContentScale.Crop else ContentScale.Fit,
                    onDoubleClick = previewDoubleClickZoom,
                    modifier = Modifier.fillMaxSize()
                )
            }

            MemoryDetailPreviewIconButton(
                iconRes = R.drawable.ic_nm_more,
                contentDescription = "更多操作",
                onClick = onTogglePreviewActionMenu,
                size = previewButtonSize,
                onBoundsChanged = onPreviewActionMenuAnchorBoundsChanged,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = statusBarHeightDp)
                    .alpha(renderedButtonAlpha)
                    .padding(
                        top = (adaptive.pageTopPadding - 2.dp).coerceAtLeast(0.dp),
                        end = adaptive.pageHorizontalPadding
                    )
            )
            NoMemoAnchoredMenu(
                expanded = showPreviewActionMenu,
                onDismissRequest = onPreviewActionMenuDismiss,
                anchorBounds = previewActionMenuAnchorBounds,
                actions = listOf(
                    NoMemoMenuActionItem(
                        iconRes = R.drawable.ic_nm_download,
                        label = "保存图片",
                        onClick = onSavePreviewImage
                    ),
                    NoMemoMenuActionItem(
                        iconRes = R.drawable.ic_nm_share,
                        label = "分享图片",
                        onClick = onSharePreviewImage
                    )
                )
            )
            MemoryDetailPreviewCloseButton(
                iconRes = R.drawable.ic_sheet_close,
                contentDescription = getString(R.string.cancel),
                onClick = onDismiss,
                size = previewCloseButtonSize,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .alpha(renderedButtonAlpha)
                    .padding(bottom = 18.dp)
            )
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
        return Rect(0f, 0f, screenWidthPx, screenHeightPx)
    }

    private fun buildPreviewDisplayedImageBounds(
        screenWidthPx: Float,
        screenHeightPx: Float,
        imageAspectRatio: Float?,
        fillScreen: Boolean
    ): Rect {
        val contentSize = calculatePreviewContentSize(
            containerWidthPx = screenWidthPx,
            containerHeightPx = screenHeightPx,
            imageAspectRatio = imageAspectRatio,
            fillScreen = fillScreen
        )
        val left = (screenWidthPx - contentSize.first) / 2f
        val top = (screenHeightPx - contentSize.second) / 2f
        return Rect(
            left = left,
            top = top,
            right = left + contentSize.first,
            bottom = top + contentSize.second
        )
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

    private fun detailImageCardWidthFraction(imageAspectRatio: Float?): Float {
        if (imageAspectRatio == null) return 0.64f
        return when {
            imageAspectRatio <= 0.50f -> 0.46f
            imageAspectRatio <= 0.68f -> 0.52f
            imageAspectRatio <= 0.82f -> 0.58f
            else -> 0.64f
        }
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

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
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
        val palette = rememberNoMemoPalette()
        val buttonSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f))
        } else {
            Color.White.copy(alpha = 0.94f)
        }
        val borderColor = if (isDark) {
            Color.White.copy(alpha = 0.16f)
        } else {
            Color.Black.copy(alpha = 0.08f)
        }
        val iconTint = if (isDark) {
            Color.White.copy(alpha = 0.96f)
        } else {
            palette.textPrimary
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
                        .size(if (size <= 48.dp) 22.dp else 24.dp)
                )
            }
        }
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
        val defaultLeadMinutes = 5
        val customLeadCapMinutes = 23 * 60 + 59

        var year by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.YEAR)) }
        var month by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.MONTH) + 1) }
        var day by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }
        var hour by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
        var minute by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.MINUTE)) }
        var leadMinutes by remember(initialTime, visible) { mutableStateOf(defaultLeadMinutes) }
        var customLeadHours by remember(initialTime, visible) { mutableStateOf(0) }
        var customLeadPartMinutes by remember(initialTime, visible) { mutableStateOf(defaultLeadMinutes) }

        val maxDay = remember(year, month) { detailDaysInMonth(year, month) }
        LaunchedEffect(maxDay) {
            if (day > maxDay) {
                day = maxDay
            }
        }

        LaunchedEffect(visible, initialTime) {
            if (!visible) {
                val resetCalendar = Calendar.getInstance().apply { timeInMillis = initialTime }
                year = resetCalendar.get(Calendar.YEAR)
                month = resetCalendar.get(Calendar.MONTH) + 1
                day = resetCalendar.get(Calendar.DAY_OF_MONTH)
                hour = resetCalendar.get(Calendar.HOUR_OF_DAY)
                minute = resetCalendar.get(Calendar.MINUTE)
                leadMinutes = defaultLeadMinutes
                customLeadHours = 0
                customLeadPartMinutes = defaultLeadMinutes
            }
        }

        val eventAt = remember(year, month, day, hour, minute) {
            buildReminderDateTime(year, month, day, hour, minute)
        }
        val effectiveLeadCap = remember(eventAt) { maxValidLeadMinutesFor(eventAt) }
        val leadOptions = remember {
            listOf(
                ReminderLeadOption("准时提醒", 0),
                ReminderLeadOption("5分钟前", 5),
                ReminderLeadOption("15分钟前", 15),
                ReminderLeadOption("30分钟前", 30),
                ReminderLeadOption("1小时前", 60),
                ReminderLeadOption("1天前", 24 * 60)
            )
        }
        val safeLeadMinutes = leadMinutes.coerceIn(0, effectiveLeadCap)
        val usingCustomLead = leadOptions.none { it.minutes == safeLeadMinutes }
        LaunchedEffect(effectiveLeadCap) {
            val clampedLeadMinutes = leadMinutes.coerceIn(0, effectiveLeadCap)
            val clampedLeadHours = clampedLeadMinutes / 60
            val clampedLeadPartMinutes = clampedLeadMinutes % 60
            if (
                clampedLeadMinutes != leadMinutes ||
                clampedLeadHours != customLeadHours ||
                clampedLeadPartMinutes != customLeadPartMinutes
            ) {
                leadMinutes = clampedLeadMinutes
                customLeadHours = clampedLeadHours
                customLeadPartMinutes = clampedLeadPartMinutes
            }
        }
        val finalReminderAt = remember(eventAt, safeLeadMinutes) {
            (eventAt - safeLeadMinutes * 60L * 1000L).coerceAtLeast(0L)
        }

        val sheetSurface = if (isDark) {
            Color(0xFF121316)
        } else {
            Color(0xFFF5F6F8)
        }
        val cardSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f))
        } else {
            Color.White
        }
        val timeCardSurface = cardSurface
        val optionCardColor = cardSurface
        val optionSelectedColor = if (isDark) {
            palette.accent.copy(alpha = 0.24f)
        } else {
            palette.accent.copy(alpha = 0.12f)
        }
        val optionSelectedStroke = if (isDark) {
            palette.accent.copy(alpha = 0.52f)
        } else {
            palette.accent.copy(alpha = 0.28f)
        }
        val dragHandleColor = if (isDark) {
            Color(0xFF8E8E93).copy(alpha = 0.72f)
        } else {
            Color(0xFF8E8E93).copy(alpha = 0.68f)
        }
        val wheelHighlightColor = if (isDark) {
            palette.accent.copy(alpha = 0.18f)
        } else {
            palette.accent.copy(alpha = 0.10f)
        }
        val wheelHighlightSheenColor = if (isDark) {
            Color.White.copy(alpha = 0.08f)
        } else {
            Color.White.copy(alpha = 0.72f)
        }
        val wheelSelectedTextColor = if (isDark) palette.textPrimary else palette.accent
        val wheelNormalTextColor = if (isDark) Color.White.copy(alpha = 0.70f) else palette.textPrimary.copy(alpha = 0.58f)
        val infoCardSurface = cardSurface
        val timePanelBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent
            )
        )
        val infoCardBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent
            )
        )
        val sheetBodyHeight = rememberNoMemoSheetHeight(
            compactPreferredHeight = 664.dp,
            regularPreferredHeight = 720.dp,
            compactScreenFraction = 0.88f,
            regularScreenFraction = 0.84f,
            minimumHeight = 360.dp
        )
        val reminderWheelHeight = if (adaptive.isNarrow) 124.dp else 138.dp
        val reminderWheelHighlightHeight = if (adaptive.isNarrow) 44.dp else 48.dp
        val reminderTimeFieldWidth = if (adaptive.isNarrow) 88.dp else 100.dp
        val eventTimeLabel = remember(eventAt) { formatReminderSheetDateTime(eventAt) }
        val finalReminderLabel = remember(finalReminderAt) { formatReminderSheetDateTime(finalReminderAt) }
        val sheetDrag = rememberNoMemoSheetDragController(
            onDismissRequest = {
                onDismiss()
                true
            }
        )

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
                        .background(
                            Color.Black.copy(
                                alpha = (if (isDark) 0.56f else 0.28f) * sheetDrag.scrimAlphaFraction
                            )
                        )
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
                        .noMemoSheetDragOffset(sheetDrag)
                        .fillMaxWidth()
                        .shadow(
                            elevation = if (adaptive.isNarrow) 18.dp else 24.dp,
                            shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp)
                        ),
                    shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp),
                    colors = CardDefaults.cardColors(containerColor = sheetSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = sheetBodyHeight)
                            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 0.dp)
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
                                onClick = {
                                    val confirmedLeadMinutes = leadMinutes.coerceIn(0, maxValidLeadMinutesFor(eventAt))
                                    onConfirm((eventAt - confirmedLeadMinutes * 60L * 1000L).coerceAtLeast(0L))
                                },
                                size = adaptive.topActionButtonSize
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "提醒时间",
                                color = palette.textSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 2.dp, bottom = 10.dp)
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = noMemoG2RoundedShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = timeCardSurface)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(timePanelBrush)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 14.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            DetailReminderWheelCard(
                                                label = "年",
                                                values = ((now.get(Calendar.YEAR) - 1)..(now.get(Calendar.YEAR) + 5)).toList(),
                                                selectedValue = year,
                                                formatter = { it.toString() },
                                                cardColor = Color.Transparent,
                                                highlightColor = wheelHighlightColor,
                                                highlightSheenColor = wheelHighlightSheenColor,
                                                selectedTextColor = wheelSelectedTextColor,
                                                normalTextColor = wheelNormalTextColor,
                                                onSelected = { year = it },
                                                modifier = Modifier.weight(1.2f),
                                                wheelHeight = reminderWheelHeight,
                                                highlightHeight = reminderWheelHighlightHeight,
                                                showContainer = false
                                            )
                                            DetailReminderWheelCard(
                                                label = "月",
                                                values = (1..12).toList(),
                                                selectedValue = month,
                                                formatter = { it.toString().padStart(2, '0') },
                                                cardColor = Color.Transparent,
                                                highlightColor = wheelHighlightColor,
                                                highlightSheenColor = wheelHighlightSheenColor,
                                                selectedTextColor = wheelSelectedTextColor,
                                                normalTextColor = wheelNormalTextColor,
                                                onSelected = { month = it },
                                                modifier = Modifier.weight(1f),
                                                wheelHeight = reminderWheelHeight,
                                                highlightHeight = reminderWheelHighlightHeight,
                                                showContainer = false
                                            )
                                            DetailReminderWheelCard(
                                                label = "日",
                                                values = (1..maxDay).toList(),
                                                selectedValue = day,
                                                formatter = { it.toString().padStart(2, '0') },
                                                cardColor = Color.Transparent,
                                                highlightColor = wheelHighlightColor,
                                                highlightSheenColor = wheelHighlightSheenColor,
                                                selectedTextColor = wheelSelectedTextColor,
                                                normalTextColor = wheelNormalTextColor,
                                                onSelected = { day = it },
                                                modifier = Modifier.weight(1f),
                                                wheelHeight = reminderWheelHeight,
                                                highlightHeight = reminderWheelHighlightHeight,
                                                showContainer = false
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            DetailReminderWheelCard(
                                                label = "时",
                                                values = (0..23).toList(),
                                                selectedValue = hour,
                                                formatter = { it.toString().padStart(2, '0') },
                                                cardColor = Color.Transparent,
                                                highlightColor = wheelHighlightColor,
                                                highlightSheenColor = wheelHighlightSheenColor,
                                                selectedTextColor = wheelSelectedTextColor,
                                                normalTextColor = wheelNormalTextColor,
                                                onSelected = { hour = it },
                                                modifier = Modifier.width(reminderTimeFieldWidth),
                                                wheelHeight = reminderWheelHeight,
                                                highlightHeight = reminderWheelHighlightHeight,
                                                showContainer = false
                                            )
                                            Text(
                                                text = ":",
                                                color = palette.textSecondary.copy(alpha = 0.58f),
                                                fontSize = 21.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
                                            )
                                            DetailReminderWheelCard(
                                                label = "分",
                                                values = (0..59).toList(),
                                                selectedValue = minute,
                                                formatter = { it.toString().padStart(2, '0') },
                                                cardColor = Color.Transparent,
                                                highlightColor = wheelHighlightColor,
                                                highlightSheenColor = wheelHighlightSheenColor,
                                                selectedTextColor = wheelSelectedTextColor,
                                                normalTextColor = wheelNormalTextColor,
                                                onSelected = { minute = it },
                                                modifier = Modifier.width(reminderTimeFieldWidth),
                                                wheelHeight = reminderWheelHeight,
                                                highlightHeight = reminderWheelHighlightHeight,
                                                showContainer = false
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DetailReminderInfoCard(
                                    title = "事件时间",
                                    value = eventTimeLabel,
                                    surface = infoCardSurface,
                                    brush = infoCardBrush,
                                    modifier = Modifier.weight(1f)
                                )
                                DetailReminderInfoCard(
                                    title = "实际提醒",
                                    value = finalReminderLabel,
                                    surface = infoCardSurface,
                                    brush = infoCardBrush,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Text(
                                text = "提前提醒",
                                color = palette.textSecondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 16.dp, start = 2.dp, bottom = 10.dp)
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = noMemoG2RoundedShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = optionCardColor)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    (leadOptions + ReminderLeadOption("自定义", -1)).chunked(3).forEach { rowOptions ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            rowOptions.forEach { option ->
                                                val isCustomOption = option.minutes < 0
                                                val optionEnabled = isCustomOption || option.minutes <= effectiveLeadCap
                                                val selected = if (isCustomOption) usingCustomLead else safeLeadMinutes == option.minutes
                                                DetailReminderLeadChip(
                                                    text = option.label,
                                                    selected = selected,
                                                    enabled = optionEnabled,
                                                    selectedColor = optionSelectedColor,
                                                    selectedStroke = optionSelectedStroke,
                                                    idleColor = timeCardSurface,
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    if (isCustomOption) {
                                                        if (!usingCustomLead) {
                                                            val seedMinutes = safeLeadMinutes.coerceIn(0, effectiveLeadCap)
                                                            customLeadHours = seedMinutes / 60
                                                            customLeadPartMinutes = seedMinutes % 60
                                                        }
                                                        leadMinutes = buildCustomLeadMinutes(
                                                            customLeadHours,
                                                            customLeadPartMinutes,
                                                            minOf(customLeadCapMinutes, effectiveLeadCap)
                                                        )
                                                    } else {
                                                        leadMinutes = option.minutes
                                                    }
                                                }
                                            }
                                            repeat(3 - rowOptions.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                    if (usingCustomLead) {
                                        DetailReminderCustomLeadEditor(
                                            hours = customLeadHours,
                                            minutes = customLeadPartMinutes,
                                            maxMinutes = effectiveLeadCap,
                                            surface = timeCardSurface,
                                            onHoursChange = { nextHours ->
                                                val nextLeadMinutes = buildCustomLeadMinutes(
                                                    nextHours,
                                                    customLeadPartMinutes,
                                                    minOf(customLeadCapMinutes, effectiveLeadCap)
                                                )
                                                customLeadHours = nextLeadMinutes / 60
                                                customLeadPartMinutes = nextLeadMinutes % 60
                                                leadMinutes = nextLeadMinutes
                                            },
                                            onMinutesChange = { nextMinutes ->
                                                val nextLeadMinutes = buildCustomLeadMinutes(
                                                    customLeadHours,
                                                    nextMinutes,
                                                    minOf(customLeadCapMinutes, effectiveLeadCap)
                                                )
                                                customLeadHours = nextLeadMinutes / 60
                                                customLeadPartMinutes = nextLeadMinutes % 60
                                                leadMinutes = nextLeadMinutes
                                            }
                                        )
                                    }
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
        highlightSheenColor: Color,
        selectedTextColor: Color,
        normalTextColor: Color,
        onSelected: (Int) -> Unit,
        modifier: Modifier = Modifier,
        wheelHeight: Dp = 138.dp,
        highlightHeight: Dp = 48.dp,
        showContainer: Boolean = true
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(wheelHeight)
                    .graphicsLayer {
                        shape = noMemoG2RoundedShape(18.dp)
                        clip = true
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .then(
                        if (showContainer) Modifier.background(cardColor) else Modifier
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(highlightHeight)
                        .padding(horizontal = 8.dp)
                        .clip(noMemoG2RoundedShape(15.dp))
                        .background(highlightColor)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(highlightHeight)
                        .padding(horizontal = 8.dp)
                        .clip(noMemoG2RoundedShape(15.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    highlightSheenColor,
                                    Color.Transparent
                                )
                            )
                        )
                )
                DetailReminderWheel(
                    values = values,
                    selectedValue = selectedValue,
                    formatter = formatter,
                    selectedTextColor = selectedTextColor,
                    normalTextColor = normalTextColor,
                    onSelected = onSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(wheelHeight)
                        .clip(noMemoG2RoundedShape(18.dp))
                )
            }
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    color = rememberNoMemoPalette().textSecondary.copy(alpha = 0.82f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    @Composable
    private fun DetailReminderWheel(
        values: List<Int>,
        selectedValue: Int,
        formatter: (Int) -> String,
        selectedTextColor: Color,
        normalTextColor: Color,
        onSelected: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        if (values.isEmpty()) return

        val wheelVisibleCount = 3
        val itemHeight = 46.dp
        val wheelHeight = itemHeight * wheelVisibleCount
        val repetitionCount = maxOf(values.size * 240, 2400)
        val baseIndex = remember(values.size) {
            val middle = repetitionCount / 2
            middle - positiveMod(middle, values.size)
        }
        val selectedValueIndex = values.indexOf(selectedValue).coerceAtLeast(0)
        val initialIndex = remember(baseIndex, selectedValueIndex) { baseIndex + selectedValueIndex }
        val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
        val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        val scope = rememberCoroutineScope()
        var centeredVirtualIndex by remember(values.size) { mutableStateOf(initialIndex) }

        LaunchedEffect(listState, values) {
            snapshotFlow { centeredItemIndex(listState) to listState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { (centeredIndex, isScrolling) ->
                    centeredIndex ?: return@collect
                    centeredVirtualIndex = centeredIndex
                    if (!isScrolling) {
                        val resolvedIndex = positiveMod(centeredIndex, values.size)
                        val resolvedValue = values[resolvedIndex]
                        if (resolvedValue != selectedValue) {
                            onSelected(resolvedValue)
                        }
                    }
                }
        }

        LaunchedEffect(selectedValue, values) {
            val targetActualIndex = values.indexOf(selectedValue).coerceAtLeast(0)
            val currentAnchor = centeredItemIndex(listState) ?: centeredVirtualIndex
            val targetVirtualIndex = nearestVirtualIndexForActual(
                anchorIndex = currentAnchor,
                actualIndex = targetActualIndex,
                size = values.size,
                minIndex = 0,
                maxIndex = repetitionCount - 1
            )
            centeredVirtualIndex = targetVirtualIndex
            if (listState.firstVisibleItemIndex != targetVirtualIndex || listState.firstVisibleItemScrollOffset != 0) {
                listState.scrollToItem(targetVirtualIndex)
            }
        }

        LazyColumn(
            state = listState,
            modifier = modifier.height(wheelHeight),
            flingBehavior = flingBehavior,
            userScrollEnabled = values.size > 1,
            contentPadding = PaddingValues(vertical = (wheelHeight - itemHeight) / 2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = repetitionCount) { virtualIndex ->
                val actualIndex = positiveMod(virtualIndex, values.size)
                val itemValue = values[actualIndex]
                val distance = abs(virtualIndex - centeredVirtualIndex)
                DetailReminderWheelItem(
                    text = formatter(itemValue),
                    distanceFromCenter = distance,
                    selectedTextColor = selectedTextColor,
                    normalTextColor = normalTextColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            scope.launch {
                                listState.animateScrollToItem(virtualIndex)
                            }
                        }
                )
            }
        }
    }

    @Composable
    private fun DetailReminderWheelItem(
        text: String,
        distanceFromCenter: Int,
        selectedTextColor: Color,
        normalTextColor: Color,
        modifier: Modifier = Modifier
    ) {
        val style = when {
            distanceFromCenter == 0 -> Triple(28.sp, FontWeight.Bold, selectedTextColor)
            distanceFromCenter == 1 -> Triple(19.sp, FontWeight.SemiBold, normalTextColor.copy(alpha = 0.74f))
            else -> Triple(15.sp, FontWeight.Medium, normalTextColor.copy(alpha = 0.38f))
        }
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = style.third,
                fontSize = style.first,
                lineHeight = style.first,
                fontWeight = style.second,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Composable
    private fun DetailReminderInfoCard(
        title: String,
        value: String,
        surface: Color,
        brush: Brush,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        Card(
            modifier = modifier,
            shape = noMemoG2RoundedShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 13.dp)
                ) {
                    Text(
                        text = title,
                        color = palette.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = value,
                        color = palette.textPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun DetailReminderLeadChip(
        text: String,
        selected: Boolean,
        enabled: Boolean,
        selectedColor: Color,
        selectedStroke: Color,
        idleColor: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        PressScaleBox(
            onClick = if (enabled) onClick else ({ }),
            modifier = modifier
                .height(46.dp)
                .clip(noMemoG2RoundedShape(16.dp))
                .alpha(if (enabled) 1f else 0.44f)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(if (selected) selectedColor else idleColor)
                    .border(
                        width = 1.dp,
                        color = if (selected) selectedStroke else Color.Transparent,
                        shape = noMemoG2RoundedShape(16.dp)
                    )
            )
            Text(
                text = text,
                color = if (enabled) palette.textPrimary else palette.textTertiary,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun DetailReminderCustomLeadEditor(
        hours: Int,
        minutes: Int,
        maxMinutes: Int,
        surface: Color,
        onHoursChange: (Int) -> Unit,
        onMinutesChange: (Int) -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = noMemoG2RoundedShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "自定义提前时间",
                    color = palette.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatLeadDuration((hours * 60 + minutes).coerceIn(0, maxMinutes)),
                    color = palette.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailReminderStepperField(
                        label = "小时",
                        value = hours,
                        range = 0..23,
                        maxMinutes = maxMinutes,
                        stepUnitMinutes = 60,
                        pairedValue = minutes,
                        onValueChange = onHoursChange,
                        modifier = Modifier.weight(1f)
                    )
                    DetailReminderStepperField(
                        label = "分钟",
                        value = minutes,
                        range = 0..59,
                        maxMinutes = maxMinutes,
                        stepUnitMinutes = 1,
                        pairedValue = hours,
                        onValueChange = onMinutesChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    private fun DetailReminderStepperField(
        label: String,
        value: Int,
        range: IntRange,
        maxMinutes: Int,
        stepUnitMinutes: Int,
        pairedValue: Int = 0,
        onValueChange: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        Column(modifier = modifier) {
            Text(
                text = label,
                color = palette.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailReminderStepButton(
                    text = "−",
                    enabled = value > range.first,
                    modifier = Modifier.weight(1f)
                ) {
                    onValueChange((value - 1).coerceAtLeast(range.first))
                }
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(42.dp)
                        .clip(noMemoG2RoundedShape(14.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.88f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value.toString().padStart(2, '0'),
                        color = palette.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                DetailReminderStepButton(
                    text = "+",
                    enabled = when (stepUnitMinutes) {
                        60 -> ((value + 1) * 60 + pairedValue) <= maxMinutes && value < range.last
                        else -> (pairedValue * 60 + (value + 1)) <= maxMinutes && value < range.last
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    onValueChange((value + 1).coerceAtMost(range.last))
                }
            }
        }
    }

    @Composable
    private fun DetailReminderStepButton(
        text: String,
        enabled: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val palette = rememberNoMemoPalette()
        PressScaleBox(
            onClick = if (enabled) onClick else ({ }),
            modifier = modifier
                .height(42.dp)
                .clip(noMemoG2RoundedShape(14.dp))
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.84f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (enabled) palette.textPrimary else palette.textTertiary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    private fun defaultDetailReminderTime(): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
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

    private fun formatReminderSheetDateTime(time: Long): String {
        if (time <= 0L) return "未设置"
        val calendar = Calendar.getInstance().apply { timeInMillis = time }
        return String.format(
            "%04d.%02d.%02d %02d:%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }

    private fun normalizeDetailDraftText(value: String?): String {
        return value
            ?.replace("\r\n", "\n")
            ?.trim()
            .orEmpty()
    }

    private fun hasStructuredDraftChanges(
        draft: EditableStructuredFields,
        initial: EditableStructuredFields
    ): Boolean {
        return normalizeDetailDraftText(draft.code) != normalizeDetailDraftText(initial.code) ||
            normalizeDetailDraftText(draft.primaryValue) != normalizeDetailDraftText(initial.primaryValue) ||
            normalizeDetailDraftText(draft.secondaryValue) != normalizeDetailDraftText(initial.secondaryValue) ||
            normalizeDetailDraftText(draft.locationText) != normalizeDetailDraftText(initial.locationText)
    }

    private fun buildCustomLeadMinutes(
        hours: Int,
        minutes: Int,
        maxMinutes: Int
    ): Int {
        val total = hours.coerceAtLeast(0) * 60 + minutes.coerceIn(0, 59)
        return total.coerceIn(0, maxMinutes.coerceAtLeast(0))
    }

    private fun formatLeadDuration(totalMinutes: Int): String {
        val safeMinutes = totalMinutes.coerceAtLeast(0)
        if (safeMinutes == 0) return "准时提醒"
        val hours = safeMinutes / 60
        val minutes = safeMinutes % 60
        return when {
            hours > 0 && minutes > 0 -> "${hours}小时${minutes}分钟前"
            hours > 0 -> "${hours}小时前"
            else -> "${minutes}分钟前"
        }
    }

    private fun maxValidLeadMinutesFor(eventAt: Long, nowMillis: Long = System.currentTimeMillis()): Int {
        if (eventAt <= nowMillis) return 0
        return ((eventAt - nowMillis) / 60_000L).toInt().coerceAtLeast(0)
    }

    private fun centeredItemIndex(listState: LazyListState): Int? {
        val layoutInfo = listState.layoutInfo
        val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
        return layoutInfo.visibleItemsInfo.minByOrNull { item ->
            abs((item.offset + item.size / 2f) - center)
        }?.index
    }

    private fun nearestVirtualIndexForActual(
        anchorIndex: Int,
        actualIndex: Int,
        size: Int,
        minIndex: Int,
        maxIndex: Int
    ): Int {
        if (size <= 0) return 0
        val lower = anchorIndex - positiveMod(anchorIndex - actualIndex, size)
        val upper = lower + size
        val lowerClamped = lower.coerceIn(minIndex, maxIndex)
        val upperClamped = upper.coerceIn(minIndex, maxIndex)
        return if (abs(lowerClamped - anchorIndex) <= abs(upperClamped - anchorIndex)) {
            lowerClamped
        } else {
            upperClamped
        }
    }

    private fun positiveMod(value: Int, mod: Int): Int {
        if (mod <= 0) return 0
        val remainder = value % mod
        return if (remainder < 0) remainder + mod else remainder
    }
}
