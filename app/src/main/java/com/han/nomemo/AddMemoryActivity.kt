package com.han.nomemo

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddMemoryActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private lateinit var aiMemoryService: AiMemoryService

    private var inputText by mutableStateOf("")
    private var selectedImageUri by mutableStateOf<Uri?>(null)
    private var imageStatusText by mutableStateOf("")
    private var aiMode by mutableStateOf(false)
    private var selectedGroupCode by mutableStateOf(CategoryCatalog.GROUP_QUICK)
    private var selectedCategoryOption by mutableStateOf(CategoryCatalog.getQuickCategories().first())
    private var reminderEnabled by mutableStateOf(false)
    private var selectedReminderAt by mutableStateOf(0L)
    private var saving by mutableStateOf(false)
    private var pendingAiInputAfterPermission: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                return@registerForActivityResult
            }
            selectedImageUri = uri
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {
                // Provider may not support persistable permissions.
            }
            imageStatusText = getString(R.string.image_selected, queryDisplayName(uri))
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val pendingInput = pendingAiInputAfterPermission ?: return@registerForActivityResult
            pendingAiInputAfterPermission = null
            if (!granted) {
                Toast.makeText(this, "通知权限未开启，AI 完成后将只更新列表", Toast.LENGTH_SHORT).show()
            }
            if (!saving) {
                saving = true
                createAiRecord(pendingInput)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        aiMemoryService = AiMemoryService(this)
        imageStatusText = getString(R.string.image_not_selected)

        setContent {
            AddMemoryContent(
                onBack = { finishWithTransition() },
                onPasteClipboard = { pasteClipboardText() },
                onPickImage = { pickImageLauncher.launch(arrayOf("image/*")) },
                onPickReminderTime = { openDateTimePicker() },
                onSave = { saveRecord() }
            )
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit)
    }

    private fun finishWithTransition() {
        finish()
    }

    private fun updateGroup(groupCode: String) {
        selectedGroupCode = groupCode
        val options = CategoryCatalog.getCategoriesByGroup(groupCode)
        selectedCategoryOption = options.first()
        if (groupCode != CategoryCatalog.GROUP_WORK) {
            reminderEnabled = false
            selectedReminderAt = 0L
        }
    }

    private fun updateCategoryByIndex(index: Int) {
        val options = CategoryCatalog.getCategoriesByGroup(selectedGroupCode)
        if (index in options.indices) {
            selectedCategoryOption = options[index]
        }
    }

    private fun openDateTimePicker() {
        val calendar = Calendar.getInstance()
        if (selectedReminderAt > 0L) {
            calendar.timeInMillis = selectedReminderAt
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        selectedReminderAt = calendar.timeInMillis
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun reminderText(): String {
        if (selectedReminderAt <= 0L) {
            return getString(R.string.reminder_not_set)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return getString(R.string.reminder_set_time, dateFormat.format(selectedReminderAt))
    }

    private fun pasteClipboardText() {
        val clipboardManager = getSystemService(ClipboardManager::class.java)
        val clipText = clipboardManager?.primaryClip
            ?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)
            ?.coerceToText(this)
            ?.toString()
            ?.trim()
            .orEmpty()

        if (clipText.isBlank()) {
            Toast.makeText(this, "剪贴板里没有可粘贴的文字", Toast.LENGTH_SHORT).show()
            return
        }

        inputText = if (inputText.isBlank()) clipText else "$inputText\n$clipText"
        Toast.makeText(this, "已粘贴剪贴板内容", Toast.LENGTH_SHORT).show()
    }

    private fun saveRecord() {
        val input = inputText.trim()
        if (TextUtils.isEmpty(input) && selectedImageUri == null) {
            Toast.makeText(this, R.string.input_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (saving) {
            return
        }
        if (aiMode && shouldRequestNotificationPermission()) {
            pendingAiInputAfterPermission = input
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        saving = true
        if (aiMode) {
            createAiRecord(input)
        } else {
            createNormalRecord(input)
        }
    }

    private fun shouldRequestNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    }

    private fun resolveReminderAt(
        groupCode: String,
        reminderEnabled: Boolean,
        reminderAt: Long
    ): Long {
        if (groupCode != CategoryCatalog.GROUP_WORK || !reminderEnabled) {
            return 0L
        }
        if (reminderAt > 0L) {
            return reminderAt
        }
        return System.currentTimeMillis() + 60L * 60L * 1000L
    }

    private fun buildRecordTitle(text: String, fallback: String): String {
        val raw = if (text.isBlank()) fallback else text
        val singleLine = raw.replace('\n', ' ').trim()
        return if (singleLine.length <= 18) singleLine else singleLine.substring(0, 18) + "..."
    }

    private fun buildRecordSummary(text: String, fallback: String): String {
        val raw = if (text.isBlank()) fallback else text
        val singleLine = raw.replace('\n', ' ').trim()
        return if (singleLine.length <= 42) singleLine else singleLine.substring(0, 42) + "..."
    }

    private fun resolveCategoryOption(categoryCode: String?): CategoryCatalog.CategoryOption {
        if (categoryCode.isNullOrBlank()) {
            return selectedCategoryOption
        }
        return CategoryCatalog.getAllCategories().firstOrNull { it.categoryCode == categoryCode }
            ?: selectedCategoryOption
    }

    private fun createNormalRecord(input: String) {
        val imageUriText = selectedImageUri?.toString() ?: ""
        val reminderAt = resolveReminderAt(selectedGroupCode, reminderEnabled, selectedReminderAt)
        val memoryText = if (input.isBlank()) getString(R.string.memory_saved_screenshot) else input
        val record = MemoryRecord(
            System.currentTimeMillis(),
            MemoryRecord.MODE_NORMAL,
            buildRecordTitle(input, selectedCategoryOption.categoryName),
            buildRecordSummary(input, memoryText),
            input,
            input,
            imageUriText,
            "",
            memoryText,
            "manual",
            selectedGroupCode,
            selectedCategoryOption.categoryCode,
            selectedCategoryOption.categoryName,
            reminderAt,
            false,
            false
        )
        onRecordSaved(record, false)
    }

    private fun createAiRecord(input: String) {
        val localUri = selectedImageUri
        val imageUriText = localUri?.toString() ?: ""
        val groupCode = selectedGroupCode
        val category = selectedCategoryOption
        val reminderAt = resolveReminderAt(groupCode, reminderEnabled, selectedReminderAt)
        val createdAt = System.currentTimeMillis()
        val placeholderMemory = if (input.isBlank()) getString(R.string.memory_saved_screenshot) else input
        val placeholderTitle = if (input.isBlank()) "AI 分析中" else buildRecordTitle(input, category.categoryName)
        val placeholderSummary = if (input.isBlank()) {
            "已创建图片记忆，等待模型提取内容"
        } else {
            "已提交到模型分析，完成后自动更新"
        }
        val placeholderRecord = MemoryRecord(
            createdAt,
            MemoryRecord.MODE_AI,
            placeholderTitle,
            placeholderSummary,
            input,
            input,
            imageUriText,
            "AI 分析中…完成后会自动更新这个条目",
            placeholderMemory,
            "pending",
            groupCode,
            category.categoryCode,
            category.categoryName,
            reminderAt,
            false,
            false
        )

        memoryStore.prependRecord(placeholderRecord)
        setResult(RESULT_OK)
        Toast.makeText(this, "已创建记忆，AI 分析完成后会自动更新", Toast.LENGTH_SHORT).show()
        finishWithTransition()

        Thread {
            try {
                val result = aiMemoryService.generateMemory(input, localUri)
                val resolvedCategory = if (
                    category.categoryCode == CategoryCatalog.CODE_QUICK_NOTE &&
                    groupCode == CategoryCatalog.GROUP_QUICK
                ) {
                    resolveCategoryOption(result.suggestedCategoryCode)
                } else {
                    category
                }
                val record = MemoryRecord(
                    placeholderRecord.recordId,
                    createdAt,
                    MemoryRecord.MODE_AI,
                    result.title,
                    result.summary,
                    input,
                    input,
                    imageUriText,
                    result.analysis,
                    result.memory,
                    result.engine,
                    resolvedCategory.groupCode,
                    resolvedCategory.categoryCode,
                    resolvedCategory.categoryName,
                    reminderAt,
                    false,
                    false
                )
                persistResolvedAiRecord(record)
            } catch (_: Exception) {
                val fallbackMemory = if (input.isBlank()) getString(R.string.memory_saved_screenshot) else input
                val fallbackRecord = MemoryRecord(
                    placeholderRecord.recordId,
                    createdAt,
                    MemoryRecord.MODE_AI,
                    buildRecordTitle(input, category.categoryName),
                    buildRecordSummary(input, fallbackMemory),
                    input,
                    input,
                    imageUriText,
                    getString(R.string.memory_fallback_analysis),
                    fallbackMemory,
                    "local",
                    groupCode,
                    category.categoryCode,
                    category.categoryName,
                    reminderAt,
                    false,
                    false
                )
                persistResolvedAiRecord(fallbackRecord)
            }
        }.start()
    }

    private fun persistResolvedAiRecord(record: MemoryRecord) {
        if (!memoryStore.updateRecord(record)) {
            memoryStore.prependRecord(record)
        }
        AiSummaryNotifier.notifyAnalysisReady(applicationContext, record)
    }

    private fun onRecordSaved(record: MemoryRecord, usedFallback: Boolean) {
        memoryStore.prependRecord(record)
        setResult(RESULT_OK)
        saving = false
        if (usedFallback) {
            Toast.makeText(this, R.string.ai_error_fallback, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        }
        finishWithTransition()
    }

    private fun queryDisplayName(uri: Uri): String {
        var cursor: Cursor? = null
        return try {
            cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) cursor.getString(index) else uri.lastPathSegment ?: "image"
            } else {
                uri.lastPathSegment ?: "image"
            }
        } catch (_: Exception) {
            uri.lastPathSegment ?: "image"
        } finally {
            cursor?.close()
        }
    }

    @Composable
    private fun AddMemoryContent(
        onBack: () -> Unit,
        onPasteClipboard: () -> Unit,
        onPickImage: () -> Unit,
        onPickReminderTime: () -> Unit,
        onSave: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val canReminder = selectedGroupCode == CategoryCatalog.GROUP_WORK
        val groups = listOf(
            GroupOption(CategoryCatalog.GROUP_QUICK, stringResource(R.string.group_quick)),
            GroupOption(CategoryCatalog.GROUP_LIFE, stringResource(R.string.group_life)),
            GroupOption(CategoryCatalog.GROUP_WORK, stringResource(R.string.group_work))
        )
        val categories = remember(selectedGroupCode) {
            CategoryCatalog.getCategoriesByGroup(selectedGroupCode)
        }
        val groupLabel = groups.firstOrNull { it.code == selectedGroupCode }?.name.orEmpty()
        val reminderStateTitle = when {
            !canReminder -> "仅工作分组可提醒"
            reminderEnabled -> "提醒已开启"
            else -> "开启提醒"
        }
        val reminderStateSubtitle = if (canReminder) {
            if (reminderEnabled) "点击可关闭提醒" else "点击开启提醒"
        } else {
            "切换到工作分组后可设置提醒"
        }
        val reminderTimeTitle = if (selectedReminderAt > 0L) {
            SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(selectedReminderAt)
        } else {
            "提醒时间"
        }
        val reminderTimeSubtitle = if (canReminder) {
            if (reminderEnabled) "点击调整提醒时间" else "先开启提醒再选择时间"
        } else {
            "工作类记忆支持时间提醒"
        }
        val sheetSurface = if (isDark) Color(0xFF12161D) else Color(0xFFF7F6FB)
        val sectionSurface = if (isDark) Color(0xFF1A1F27).copy(alpha = 0.985f) else Color.White.copy(alpha = 0.985f)
        val borderSoft = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000)
        val accentBlue = if (isDark) Color(0xFF8FBCFF) else Color(0xFF1D78F2)
        val accentBlueSoft = if (isDark) Color(0xFF21334D) else Color(0xFFE7F1FF)
        val accentBlueStroke = if (isDark) Color(0xFF38557C) else Color(0xFFC6DDFF)
        val scrollState = rememberScrollState()

        LaunchedEffect(selectedGroupCode) {
            if (!categories.any { it.categoryCode == selectedCategoryOption.categoryCode }) {
                selectedCategoryOption = categories.first()
            }
        }

        var entered by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { entered = true }
        val scrimAlpha by animateFloatAsState(
            targetValue = if (entered) 1f else 0.8f,
            animationSpec = tween(420),
            label = "scrimAlpha"
        )
        val panelAlpha by animateFloatAsState(
            targetValue = if (entered) 1f else 0.96f,
            animationSpec = tween(460),
            label = "panelAlpha"
        )
        val panelOffsetY by animateFloatAsState(
            targetValue = if (entered) 0f else 56f,
            animationSpec = tween(460),
            label = "panelOffset"
        )
        val panelScale by animateFloatAsState(
            targetValue = if (entered) 1f else 0.988f,
            animationSpec = tween(460),
            label = "panelScale"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background((if (isDark) Color.Black else Color(0xFF0E1420)).copy(alpha = 0.74f * scrimAlpha))
        ) {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = if (spec.isNarrow) 12.dp else 20.dp,
                            end = spec.pageHorizontalPadding,
                            bottom = if (spec.isNarrow) 12.dp else 20.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = if (spec.isNarrow) 560.dp else 700.dp)
                            .fillMaxHeight(if (spec.isNarrow) 0.94f else 0.88f)
                            .graphicsLayer {
                                alpha = panelAlpha
                                translationY = panelOffsetY
                                scaleX = panelScale
                                scaleY = panelScale
                            }
                            .shadow(
                                elevation = if (spec.isNarrow) 28.dp else 38.dp,
                                shape = RoundedCornerShape(if (spec.isNarrow) 34.dp else 40.dp)
                            )
                            .clip(RoundedCornerShape(if (spec.isNarrow) 34.dp else 40.dp))
                            .background(sheetSurface)
                            .border(
                                1.dp,
                                borderSoft,
                                RoundedCornerShape(if (spec.isNarrow) 34.dp else 40.dp)
                            )
                            .verticalScroll(scrollState)
                            .padding(
                                start = if (spec.isNarrow) 16.dp else 22.dp,
                                top = 14.dp,
                                end = if (spec.isNarrow) 16.dp else 22.dp,
                                bottom = 22.dp
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(62.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.18f) else Color(0x24000000))
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HeaderCircleButton(
                                iconRes = R.drawable.ic_sheet_close,
                                contentDescription = stringResource(R.string.back),
                                enabled = !saving,
                                surfaceColor = sectionSurface,
                                onClick = onBack
                            )

                            ModeSwitch(
                                aiMode = aiMode,
                                enabled = !saving,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 14.dp)
                                    .widthIn(max = 250.dp),
                                onSelectAi = { aiMode = true },
                                onSelectLocal = { aiMode = false }
                            )

                            HeaderCircleButton(
                                iconRes = R.drawable.ic_sheet_check,
                                contentDescription = stringResource(R.string.save_record_desc),
                                enabled = !saving,
                                surfaceColor = sectionSurface,
                                onClick = onSave
                            )
                        }

                        Text(
                            text = if (aiMode) "AI Prompt" else "New Memory",
                            color = accentBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 18.dp)
                        )

                        Text(
                            text = if (aiMode) "添加一条可分析的记忆" else "添加一条即时记忆",
                            color = palette.textPrimary,
                            fontSize = if (spec.isNarrow) 24.sp else 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        Text(
                            text = if (aiMode) {
                                "像一个弹出 prompt 那样，先输入内容，再让 AI 帮你整理标题、摘要和分类。"
                            } else {
                                "像快速弹窗一样记录这一刻，保存后就从主页面里消失，不再像一层页面套着一层页面。"
                            },
                            color = palette.textSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "当前分类：$groupLabel / ${selectedCategoryOption.categoryName}",
                            color = palette.textTertiary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        InputSection(
                            value = inputText,
                            enabled = !saving,
                            placeholder = stringResource(R.string.hint_text_input),
                            minHeight = spec.addInputHeight + if (spec.isNarrow) 42.dp else 54.dp,
                            modifier = Modifier.padding(top = 18.dp),
                            onValueChange = { inputText = it }
                        )

                        if (aiMode) {
                            ActionCard(
                                title = "粘贴剪贴板内容",
                                subtitle = "把刚复制的文字快速带进 AI 解析，减少来回复制补录的步骤。",
                                enabled = !saving,
                                modifier = Modifier.padding(top = 14.dp),
                                onClick = onPasteClipboard
                            )
                        }

                        ActionCard(
                            title = stringResource(R.string.pick_screenshot),
                            subtitle = imageStatusText,
                            enabled = !saving,
                            modifier = Modifier.padding(top = 14.dp),
                            onClick = onPickImage
                        )

                        if (selectedImageUri != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = sectionSurface),
                                border = BorderStroke(1.dp, borderSoft)
                            ) {
                                AndroidView(
                                    factory = { ctx ->
                                        ImageView(ctx).apply {
                                            scaleType = ImageView.ScaleType.CENTER_CROP
                                            clipToOutline = true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(spec.addPreviewHeight + 12.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(sectionSurface),
                                    update = { imageView ->
                                        try {
                                            imageView.setImageURI(selectedImageUri)
                                        } catch (_: Exception) {
                                            imageView.setImageDrawable(null)
                                        }
                                    }
                                )
                            }
                        }

                        CategorySection(
                            groups = groups,
                            categories = categories,
                            selectedGroupCode = selectedGroupCode,
                            selectedCategoryName = selectedCategoryOption.categoryName,
                            enabled = !saving,
                            modifier = Modifier.padding(top = 16.dp),
                            onGroupSelect = { code -> updateGroup(code) },
                            onCategorySelect = { index -> updateCategoryByIndex(index) }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OptionTile(
                                title = reminderStateTitle,
                                subtitle = reminderStateSubtitle,
                                iconRes = R.drawable.ic_sheet_flag,
                                selected = reminderEnabled && canReminder,
                                enabled = !saving && canReminder,
                                modifier = Modifier.weight(1f),
                                surfaceColor = sectionSurface,
                                accentBlue = accentBlue,
                                accentBlueSoft = accentBlueSoft,
                                accentBlueStroke = accentBlueStroke,
                                onClick = {
                                    if (!saving && canReminder) {
                                        reminderEnabled = !reminderEnabled
                                        if (!reminderEnabled) {
                                            selectedReminderAt = 0L
                                        } else if (selectedReminderAt <= 0L) {
                                            onPickReminderTime()
                                        }
                                    }
                                }
                            )
                            OptionTile(
                                title = reminderTimeTitle,
                                subtitle = reminderTimeSubtitle,
                                iconRes = R.drawable.ic_sheet_calendar,
                                selected = selectedReminderAt > 0L && reminderEnabled && canReminder,
                                enabled = !saving && canReminder && reminderEnabled,
                                modifier = Modifier.weight(1f),
                                surfaceColor = sectionSurface,
                                accentBlue = accentBlue,
                                accentBlueSoft = accentBlueSoft,
                                accentBlueStroke = accentBlueStroke,
                                onClick = onPickReminderTime
                            )
                        }

                        if (canReminder && reminderEnabled) {
                            Text(
                                text = reminderText(),
                                color = palette.textSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                            )
                        }

                        Text(
                            text = if (aiMode) {
                                stringResource(R.string.ai_hint)
                            } else {
                                "保存后会直接回到记忆列表，这个弹出 prompt 不会在界面里留下第二层页面感。"
                            },
                            color = palette.textTertiary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 16.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HeaderCircleButton(
        iconRes: Int,
        contentDescription: String,
        enabled: Boolean,
        surfaceColor: Color,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        PressScaleBox(
            onClick = { if (enabled) onClick() },
            modifier = modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(if (enabled) surfaceColor else palette.glassFill)
                .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000), CircleShape)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                tint = if (enabled) palette.textPrimary else palette.textTertiary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(28.dp)
            )
        }
    }

    @Composable
    private fun ModeSwitch(
        aiMode: Boolean,
        enabled: Boolean,
        onSelectAi: () -> Unit,
        onSelectLocal: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val isDark = isSystemInDarkTheme()
        Row(
            modifier = modifier
                .height(58.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (isDark) Color(0xFF1A1F27).copy(alpha = 0.98f) else Color.White.copy(alpha = 0.98f))
                .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000), RoundedCornerShape(999.dp))
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeSwitchChip(
                text = stringResource(R.string.mode_ai),
                selected = aiMode,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = onSelectAi
            )
            ModeSwitchChip(
                text = stringResource(R.string.mode_normal),
                selected = !aiMode,
                enabled = enabled,
                modifier = Modifier.weight(1f),
                onClick = onSelectLocal
            )
        }
    }

    @Composable
    private fun ModeSwitchChip(
        text: String,
        selected: Boolean,
        enabled: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val accentBlue = if (isDark) Color(0xFF8FBCFF) else Color(0xFF1D78F2)
        val accentBlueSoft = if (isDark) Color(0xFF233752) else Color(0xFFDCEBFF)
        PressScaleBox(
            onClick = { if (enabled) onClick() },
            modifier = modifier
                .height(48.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (selected) accentBlueSoft
                    else Color.Transparent
                )
        ) {
            Text(
                text = text,
                color = when {
                    !enabled -> palette.textTertiary
                    selected -> accentBlue
                    else -> palette.textSecondary
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun CategorySection(
        groups: List<GroupOption>,
        categories: List<CategoryCatalog.CategoryOption>,
        selectedGroupCode: String,
        selectedCategoryName: String,
        enabled: Boolean,
        onGroupSelect: (String) -> Unit,
        onCategorySelect: (Int) -> Unit,
        modifier: Modifier = Modifier,
        surfaceColor: Color = Color.Unspecified,
        accentBlue: Color = Color.Unspecified,
        accentBlueSoft: Color = Color.Unspecified,
        accentBlueStroke: Color = Color.Unspecified
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val borderSoft = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000)
        val inputSurface = if (isDark) Color(0xFF11161D) else Color(0xFFF9FAFC)
        val resolvedSurface = if (surfaceColor == Color.Unspecified) {
            if (isDark) Color(0xFF1A1F27).copy(alpha = 0.98f) else Color.White.copy(alpha = 0.98f)
        } else {
            surfaceColor
        }
        val resolvedAccentBlue = if (accentBlue == Color.Unspecified) {
            if (isDark) Color(0xFF8FBCFF) else Color(0xFF1D78F2)
        } else {
            accentBlue
        }
        val resolvedAccentBlueSoft = if (accentBlueSoft == Color.Unspecified) {
            if (isDark) Color(0xFF233752) else Color(0xFFDCEBFF)
        } else {
            accentBlueSoft
        }
        val resolvedAccentBlueStroke = if (accentBlueStroke == Color.Unspecified) {
            if (isDark) Color(0xFF38557C) else Color(0xFFB6D6FF)
        } else {
            accentBlueStroke
        }
        var expanded by remember { mutableStateOf(false) }

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = resolvedSurface),
            border = BorderStroke(1.dp, borderSoft)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.category_sub),
                    color = palette.textSecondary,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(inputSurface)
                            .border(1.dp, borderSoft, RoundedCornerShape(22.dp))
                            .clickable(enabled = enabled) { expanded = true }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(resolvedAccentBlue)
                        )
                        Text(
                            text = selectedCategoryName,
                            color = if (enabled) palette.textPrimary else palette.textTertiary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_sheet_chevron_down),
                            contentDescription = null,
                            tint = palette.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(palette.memoBgStart)
                    ) {
                        categories.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item.categoryName,
                                        color = palette.textPrimary,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    onCategorySelect(index)
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groups.forEach { group ->
                        PressScaleBox(
                            onClick = { if (enabled) onGroupSelect(group.code) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (group.code == selectedGroupCode) resolvedAccentBlueSoft
                                    else inputSurface
                                )
                                .border(
                                    1.dp,
                                    if (group.code == selectedGroupCode) resolvedAccentBlueStroke
                                    else borderSoft,
                                    RoundedCornerShape(18.dp)
                                )
                        ) {
                            Text(
                                text = group.name,
                                color = if (group.code == selectedGroupCode) resolvedAccentBlue else palette.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InputSection(
        value: String,
        enabled: Boolean,
        placeholder: String,
        minHeight: androidx.compose.ui.unit.Dp,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF1A1F27).copy(alpha = 0.98f) else Color.White.copy(alpha = 0.98f)
            ),
            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000))
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = TextStyle(
                    color = palette.textPrimary,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(minHeight)
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                color = palette.textTertiary,
                                fontSize = 18.sp,
                                lineHeight = 26.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }

    @Composable
    private fun ActionCard(
        title: String,
        subtitle: String,
        enabled: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        PressScaleBox(
            onClick = { if (enabled) onClick() },
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (enabled) {
                        if (isDark) Color(0xFF1A1F27).copy(alpha = 0.98f) else Color.White.copy(alpha = 0.98f)
                    } else {
                        if (isDark) Color(0xFF13171D) else Color(0xFFF4F4F6)
                    }
                )
                .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000), RoundedCornerShape(28.dp))
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = title,
                    color = if (enabled) {
                        if (isDark) Color(0xFF8FBCFF) else Color(0xFF1D78F2)
                    } else palette.textTertiary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = palette.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }

    @Composable
    private fun OptionTile(
        title: String,
        subtitle: String,
        iconRes: Int,
        selected: Boolean,
        enabled: Boolean,
        surfaceColor: Color,
        accentBlue: Color,
        accentBlueSoft: Color,
        accentBlueStroke: Color,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        val borderSoft = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.12f) else Color(0x12000000)
        PressScaleBox(
            onClick = { if (enabled) onClick() },
            modifier = modifier
                .height(132.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    when {
                        !enabled -> surfaceColor.copy(alpha = 0.62f)
                        selected -> accentBlueSoft
                        else -> surfaceColor
                    }
                )
                .border(
                    1.dp,
                    if (selected) accentBlueStroke else borderSoft,
                    RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    tint = when {
                        !enabled -> palette.textTertiary.copy(alpha = 0.45f)
                        selected -> accentBlue
                        else -> palette.textSecondary
                    },
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = title,
                    color = if (enabled) palette.textPrimary else palette.textTertiary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 14.dp)
                )
                Text(
                    text = subtitle,
                    color = if (selected && enabled) accentBlue else palette.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }

    data class GroupOption(val code: String, val name: String)
}
