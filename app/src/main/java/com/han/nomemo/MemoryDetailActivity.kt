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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
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
                    val result = aiMemoryService.generateMemory(input, imageUri)
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

        BackHandler(enabled = moreMenuExpanded || showDeleteConfirm || showImagePreview) {
            when {
                showImagePreview -> showImagePreview = false
                showDeleteConfirm -> showDeleteConfirm = false
                moreMenuExpanded -> moreMenuExpanded = false
            }
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = spec.pageTopPadding,
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
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
                            .padding(top = spec.topActionButtonSize + 18.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (!currentRecord.imageUri.isNullOrBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.72f)
                                    .align(Alignment.CenterHorizontally)
                                    .clickable { showImagePreview = true },
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = palette.glassFill),
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
                                            imageView.setImageURI(Uri.parse(currentRecord.imageUri))
                                        } catch (_: Exception) {
                                            imageView.setImageDrawable(null)
                                        }
                                    }
                                )
                            }
                        }

                        Text(
                            text = titleText,
                            color = palette.textPrimary,
                            fontSize = if (spec.isNarrow) 27.sp else 31.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = if (spec.isNarrow) 35.sp else 39.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 20.dp)
                        )

                        Row(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = createdAtText,
                                color = palette.textSecondary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = " · ",
                                color = palette.textTertiary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            DetailMetaPill(text = currentRecord.categoryName ?: "小记")
                        }

                        Text(
                            text = "摘要",
                            color = palette.textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 24.dp)
                        )

                        Text(
                            text = summaryText,
                            color = palette.textSecondary,
                            fontSize = 16.sp,
                            lineHeight = 25.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        ReanalyzeButton(
                            text = if (reanalyzing) "正在重新分析..." else "重新分析",
                            enabled = !reanalyzing,
                            modifier = Modifier.padding(top = 28.dp),
                            onClick = { onReanalyze(currentRecord) }
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    if (moreMenuExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(4f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { moreMenuExpanded = false }
                            )
                            DetailMoreMenuPanel(
                                archived = currentRecord.isArchived,
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

                    if (showImagePreview && !currentRecord.imageUri.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(palette.memoBgStart)
                                .zIndex(6f)
                                .clickable { showImagePreview = false }
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    ImageView(ctx).apply {
                                        adjustViewBounds = true
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 18.dp, vertical = 24.dp),
                                update = { imageView ->
                                    try {
                                        imageView.setImageURI(Uri.parse(currentRecord.imageUri))
                                    } catch (_: Exception) {
                                        imageView.setImageDrawable(null)
                                    }
                                }
                            )
                        }
                    }

                    if (showDeleteConfirm) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm = false },
                            title = { Text("确认删除") },
                            text = { Text("确定删除这条记忆吗？") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteConfirm = false
                                        onDelete(currentRecord)
                                    }
                                ) {
                                    Text(text = getString(R.string.action_delete))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text(text = getString(R.string.cancel))
                                }
                            }
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

    @Composable
    private fun DetailMetaPill(text: String) {
        val palette = rememberNoMemoPalette()
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(palette.glassFill)
                .border(1.dp, palette.glassStroke, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = text,
                color = palette.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    @Composable
    private fun ReanalyzeButton(
        text: String,
        enabled: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        PressScaleBox(
            onClick = {
                if (enabled) {
                    onClick()
                }
            },
            modifier = modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (enabled) palette.accent else palette.glassFill
                ),
                border = BorderStroke(
                    1.dp,
                    if (enabled) palette.accent else palette.glassStroke
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (enabled) palette.onAccent else palette.textSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    @Composable
    private fun DetailMoreMenuPanel(
        archived: Boolean,
        onArchiveToggle: () -> Unit,
        onDelete: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val menuSurface = Color(0xFFFBFBFC).copy(alpha = 0.94f)
        Card(
            modifier = modifier
                .width(176.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = menuSurface),
            border = BorderStroke(1.dp, palette.glassStroke)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                DetailMoreMenuRow(
                    iconRes = R.drawable.ic_sheet_calendar,
                    label = if (archived) getString(R.string.action_unarchive) else getString(R.string.action_archive),
                    onClick = onArchiveToggle
                )
                DetailMoreMenuRow(
                    iconRes = R.drawable.ic_nm_delete,
                    label = getString(R.string.action_delete),
                    onClick = onDelete,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }

    @Composable
    private fun DetailMoreMenuRow(
        iconRes: Int,
        label: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        PressScaleBox(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(palette.glassFillSoft)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(palette.glassFill)
                        .border(1.dp, palette.glassStroke, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = palette.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = label,
                    color = palette.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}
