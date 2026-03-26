package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
    private var record by mutableStateOf<MemoryRecord?>(null)
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

    @Composable
    private fun DetailContent(
        record: MemoryRecord?,
        onBack: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()

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
                    val currentRecord = record
                    if (currentRecord == null) {
                        GlassPanelText(
                            text = "\u6B63\u5728\u52A0\u8F7D...",
                            modifier = Modifier.align(Alignment.Center)
                        )
                        return@Box
                    }

                    val createdAtText = rememberTime(currentRecord.createdAt)
                    val titleText = currentRecord.title?.takeIf { it.isNotBlank() }
                        ?: currentRecord.memory?.takeIf { it.isNotBlank() }
                        ?: stringResourceSafe(R.string.page_title)

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
                                    text = "\u8BB0\u5FC6\u8BE6\u60C5",
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
                                    text = currentRecord.categoryName ?: "\u5C0F\u8BB0",
                                    background = palette.tagNoteBg,
                                    contentColor = palette.tagNoteText
                                )
                                DetailTag(
                                    text = if (currentRecord.mode == MemoryRecord.MODE_AI) {
                                        getString(R.string.tag_ai)
                                    } else {
                                        getString(R.string.mode_label_normal)
                                    },
                                    background = if (currentRecord.mode == MemoryRecord.MODE_AI) {
                                        palette.tagAiBg
                                    } else {
                                        palette.glassFillSoft
                                    },
                                    contentColor = if (currentRecord.mode == MemoryRecord.MODE_AI) {
                                        palette.tagAiText
                                    } else {
                                        palette.textSecondary
                                    }
                                )
                                if (currentRecord.isArchived) {
                                    DetailTag(
                                        text = "\u5DF2\u5F52\u6863",
                                        background = palette.glassFillSoft,
                                        contentColor = palette.textSecondary
                                    )
                                }
                            }
                        }

                        if (!currentRecord.imageUri.isNullOrBlank()) {
                            DetailSection(modifier = Modifier.padding(top = 14.dp)) {
                                Text(
                                    text = "\u56FE\u7247",
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
                            title = "\u57FA\u672C\u4FE1\u606F",
                            items = listOf(
                                "\u521B\u5EFA\u65F6\u95F4" to createdAtText,
                                "\u6A21\u5F0F" to if (currentRecord.mode == MemoryRecord.MODE_AI) {
                                    getString(R.string.mode_label_ai)
                                } else {
                                    getString(R.string.mode_label_normal)
                                },
                                "\u5206\u7C7B" to (currentRecord.categoryName ?: "\u5C0F\u8BB0"),
                                "\u63D0\u9192" to buildReminderText(currentRecord),
                                "\u5F15\u64CE" to currentRecord.engine.orEmpty().ifBlank { "manual" }
                            )
                        )

                        if (!currentRecord.memory.isNullOrBlank()) {
                            DetailTextSection(
                                modifier = Modifier.padding(top = 14.dp),
                                title = "\u8BB0\u5FC6\u5185\u5BB9",
                                text = currentRecord.memory
                            )
                        }

                        if (!currentRecord.sourceText.isNullOrBlank() &&
                            currentRecord.sourceText != currentRecord.memory
                        ) {
                            DetailTextSection(
                                modifier = Modifier.padding(top = 14.dp),
                                title = "\u6765\u6E90\u6587\u672C",
                                text = currentRecord.sourceText
                            )
                        }

                        if (!currentRecord.note.isNullOrBlank() &&
                            currentRecord.note != currentRecord.sourceText
                        ) {
                            DetailTextSection(
                                modifier = Modifier.padding(top = 14.dp),
                                title = "\u5907\u6CE8",
                                text = currentRecord.note
                            )
                        }

                        if (!currentRecord.analysis.isNullOrBlank()) {
                            DetailTextSection(
                                modifier = Modifier.padding(top = 14.dp),
                                title = "AI \u5206\u6790",
                                text = currentRecord.analysis
                            )
                        }

                        Spacer(modifier = Modifier.height(26.dp))
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
            return "\u672A\u8BBE\u7F6E"
        }
        val timeText = rememberTime(record.reminderAt)
        return if (record.isReminderDone) {
            "$timeText | \u5DF2\u5B8C\u6210"
        } else {
            timeText
        }
    }

    private fun stringResourceSafe(resId: Int): String = try {
        getString(resId)
    } catch (_: Exception) {
        "\u8BB0\u5FC6"
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
