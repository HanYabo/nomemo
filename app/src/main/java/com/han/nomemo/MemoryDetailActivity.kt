package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private var record by mutableStateOf<MemoryRecord?>(null)
    private var memoryChangeRegistered = false

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadRecordOrFinish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        loadRecordOrFinish()
        setContent {
            DetailContent(
                record = record,
                onBack = { finish() }
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.page_back_enter, R.anim.page_back_exit)
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
        val loaded = memoryStore.findRecordById(recordId)
        record = loaded
        if (loaded == null) {
            Toast.makeText(this, "这条记忆可能已经被删除了", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @Composable
    private fun DetailContent(
        record: MemoryRecord?,
        onBack: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val currentRecord = record

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
                            bottom = 18.dp
                        )
                ) {
                    if (currentRecord == null) {
                        GlassPanelText(
                            text = "正在读取记忆详情…",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        val createdAtText = rememberTime(currentRecord.createdAt)
                        val titleText = currentRecord.title?.takeIf { it.isNotBlank() }
                            ?: currentRecord.memory?.takeIf { it.isNotBlank() }
                            ?: "未命名记忆"

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GlassIconCircleButton(
                                    iconRes = R.drawable.ic_sheet_close,
                                    contentDescription = getString(R.string.back),
                                    onClick = onBack
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 14.dp)
                                ) {
                                    Text(
                                        text = "记忆详情",
                                        color = palette.textPrimary,
                                        fontSize = if (spec.isNarrow) 24.sp else 28.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = createdAtText,
                                        color = palette.textSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            DetailSection(modifier = Modifier.padding(top = 18.dp)) {
                                Text(
                                    text = titleText,
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 26.sp else 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = if (spec.isNarrow) 34.sp else 38.sp
                                )
                                if (!currentRecord.summary.isNullOrBlank() &&
                                    currentRecord.summary != titleText &&
                                    currentRecord.summary != currentRecord.memory
                                ) {
                                    Text(
                                        text = currentRecord.summary,
                                        color = palette.textSecondary,
                                        fontSize = 15.sp,
                                        lineHeight = 23.sp,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.padding(top = 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DetailTag(
                                        text = currentRecord.categoryName ?: "小记",
                                        background = palette.tagNoteBg,
                                        contentColor = palette.tagNoteText
                                    )
                                    DetailTag(
                                        text = if (currentRecord.mode == MemoryRecord.MODE_AI) "AI 记忆" else "普通记录",
                                        background = if (currentRecord.mode == MemoryRecord.MODE_AI) palette.tagAiBg else palette.glassFillSoft,
                                        contentColor = if (currentRecord.mode == MemoryRecord.MODE_AI) palette.tagAiText else palette.textSecondary
                                    )
                                    if (currentRecord.isArchived) {
                                        DetailTag(
                                            text = "已归档",
                                            background = palette.glassFillSoft,
                                            contentColor = palette.textSecondary
                                        )
                                    }
                                }
                            }

                            if (!currentRecord.imageUri.isNullOrBlank()) {
                                DetailSection(modifier = Modifier.padding(top = 14.dp)) {
                                    Text(
                                        text = "附件",
                                        color = palette.textPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    AndroidView(
                                        factory = { ctx ->
                                            ImageView(ctx).apply {
                                                adjustViewBounds = true
                                                scaleType = ImageView.ScaleType.FIT_CENTER
                                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                            .clip(RoundedCornerShape(24.dp)),
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

                            DetailKeyValueSection(
                                modifier = Modifier.padding(top = 14.dp),
                                title = "基本信息",
                                items = listOf(
                                    "创建时间" to createdAtText,
                                    "模式" to if (currentRecord.mode == MemoryRecord.MODE_AI) "AI 生成" else "普通记录",
                                    "分类" to (currentRecord.categoryName ?: "未分类"),
                                    "提醒" to buildReminderText(currentRecord),
                                    "引擎" to currentRecord.engine.orEmpty().ifBlank { "manual" }
                                )
                            )

                            if (!currentRecord.memory.isNullOrBlank()) {
                                DetailTextSection(
                                    modifier = Modifier.padding(top = 14.dp),
                                    title = "记忆内容",
                                    text = currentRecord.memory
                                )
                            }

                            if (!currentRecord.sourceText.isNullOrBlank() &&
                                currentRecord.sourceText != currentRecord.memory
                            ) {
                                DetailTextSection(
                                    modifier = Modifier.padding(top = 14.dp),
                                    title = "原始输入",
                                    text = currentRecord.sourceText
                                )
                            }

                            if (!currentRecord.note.isNullOrBlank() &&
                                currentRecord.note != currentRecord.sourceText
                            ) {
                                DetailTextSection(
                                    modifier = Modifier.padding(top = 14.dp),
                                    title = "备注",
                                    text = currentRecord.note
                                )
                            }

                            if (!currentRecord.analysis.isNullOrBlank()) {
                                DetailTextSection(
                                    modifier = Modifier.padding(top = 14.dp),
                                    title = "AI 分析",
                                    text = currentRecord.analysis
                                )
                            }

                            Spacer(modifier = Modifier.height(26.dp))
                        }
                    }
                }
            }
        }
    }

    private fun rememberTime(timeMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timeMillis))
    }

    private fun buildReminderText(record: MemoryRecord): String {
        if (record.reminderAt <= 0L) {
            return "未设置"
        }
        val timeText = rememberTime(record.reminderAt)
        return if (record.isReminderDone) "$timeText · 已完成" else timeText
    }

    @Composable
    private fun DetailSection(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = palette.glassFill),
            border = BorderStroke(1.dp, palette.glassStroke)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                content()
            }
        }
    }

    @Composable
    private fun DetailTextSection(
        modifier: Modifier = Modifier,
        title: String,
        text: String
    ) {
        val palette = rememberNoMemoPalette()
        DetailSection(modifier = modifier) {
            Text(
                text = title,
                color = palette.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                color = palette.textSecondary,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }

    @Composable
    private fun DetailKeyValueSection(
        modifier: Modifier = Modifier,
        title: String,
        items: List<Pair<String, String>>
    ) {
        val palette = rememberNoMemoPalette()
        DetailSection(modifier = modifier) {
            Text(
                text = title,
                color = palette.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            items.filter { it.second.isNotBlank() }.forEachIndexed { index, (label, value) ->
                if (index == 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = label,
                        color = palette.textTertiary,
                        fontSize = 13.sp,
                        modifier = Modifier.widthIn(min = 74.dp)
                    )
                    Text(
                        text = value,
                        color = palette.textSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    private fun DetailTag(
        text: String,
        background: Color,
        contentColor: Color
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(background)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
