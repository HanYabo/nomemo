package com.han.nomemo

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemorySheet(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val memoryStore = remember(context) { MemoryStore(context) }
    val aiMemoryService = remember(context) { AiMemoryService(context) }
    val adaptive = rememberNoMemoAdaptiveSpec()
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageStatusText by remember { mutableStateOf(context.getString(R.string.image_not_selected)) }
    var aiMode by remember { mutableStateOf(false) }
    var selectedGroupCode by remember { mutableStateOf(CategoryCatalog.GROUP_QUICK) }
    var selectedCategory by remember { mutableStateOf(CategoryCatalog.getQuickCategories().first()) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderAt by remember { mutableStateOf(0L) }
    var saving by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var pendingAiInput by remember { mutableStateOf<String?>(null) }

    val categories = remember(selectedGroupCode) { CategoryCatalog.getCategoriesByGroup(selectedGroupCode) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        selectedImageUri = uri
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {
        }
        imageStatusText = context.getString(R.string.image_selected, queryDisplayName(context, uri))
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val input = pendingAiInput ?: return@rememberLauncherForActivityResult
        pendingAiInput = null
        if (!granted) {
            Toast.makeText(context, "通知权限未开启，AI 完成后将只更新列表", Toast.LENGTH_SHORT).show()
        }
        saveRecord(
            context, memoryStore, aiMemoryService, input, selectedImageUri, aiMode,
            selectedGroupCode, selectedCategory, reminderEnabled, reminderAt
        )
        onDismiss()
    }

    BackHandler(enabled = true) {
        if (!saving) onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = { if (!saving) onDismiss() },
        sheetState = sheetState,
        containerColor = if (isDark) Color(0xFF12161D) else Color(0xFFF7F6FB),
        scrimColor = Color.Black.copy(alpha = if (isDark) 0.56f else 0.28f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = 56.dp, height = 5.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.18f) else Color(0x24000000))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GlassIconCircleButton(
                    iconRes = R.drawable.ic_sheet_close,
                    contentDescription = stringResource(R.string.back),
                    onClick = onDismiss,
                    size = adaptive.topActionButtonSize
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isDark) Color(0xFF1A1F27) else Color.White)
                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000), RoundedCornerShape(999.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SheetModeChip("AI", aiMode, Modifier.weight(1f)) { aiMode = true }
                    SheetModeChip("普通", !aiMode, Modifier.weight(1f)) { aiMode = false }
                }
                GlassIconCircleButton(
                    iconRes = R.drawable.ic_sheet_check,
                    contentDescription = stringResource(R.string.save_record_desc),
                    onClick = {
                        val input = inputText.trim()
                        if (TextUtils.isEmpty(input) && selectedImageUri == null) {
                            Toast.makeText(context, R.string.input_required, Toast.LENGTH_SHORT).show()
                            return@GlassIconCircleButton
                        }
                        saving = true
                        if (aiMode && shouldRequestNotificationPermission(context)) {
                            pendingAiInput = input
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            saveRecord(
                                context, memoryStore, aiMemoryService, input, selectedImageUri, aiMode,
                                selectedGroupCode, selectedCategory, reminderEnabled, reminderAt
                            )
                            onDismiss()
                        }
                    },
                    size = adaptive.topActionButtonSize
                )
            }

            Text(
                text = if (aiMode) "让模型补全这条记忆" else "记录刚刚这一刻",
                color = palette.textPrimary,
                fontSize = if (adaptive.isNarrow) 24.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
            Text(
                text = if (aiMode) "确认后先创建条目，AI 再回填摘要与分析。" else "这是一个原地弹出的输入层，不再跳转新页面。",
                color = palette.textSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 6.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1A1F27) else Color.White),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000))
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = TextStyle(color = palette.textPrimary, fontSize = 18.sp, lineHeight = 26.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                        .height(adaptive.addInputHeight + 26.dp)
                ) { inner ->
                    Box(Modifier.fillMaxWidth()) {
                        if (inputText.isBlank()) {
                            Text(stringResource(R.string.hint_text_input), color = palette.textTertiary, fontSize = 18.sp)
                        }
                        inner()
                    }
                }
            }

            if (aiMode) {
                SheetActionCard("粘贴剪贴板内容", "快速带入 AI 分析") {
                    val clip = context.getSystemService(ClipboardManager::class.java)
                        ?.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(context)?.toString()?.trim().orEmpty()
                    if (clip.isBlank()) {
                        Toast.makeText(context, "剪贴板里没有可粘贴的文字", Toast.LENGTH_SHORT).show()
                    } else {
                        inputText = if (inputText.isBlank()) clip else "$inputText\n$clip"
                    }
                }
            }

            SheetActionCard(stringResource(R.string.pick_screenshot), imageStatusText) {
                pickImageLauncher.launch(arrayOf("image/*"))
            }

            if (selectedImageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1A1F27) else Color.White),
                    border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000))
                ) {
                    AndroidView(
                        factory = { ctx -> ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(adaptive.addPreviewHeight)
                            .clip(RoundedCornerShape(24.dp)),
                        update = { image -> image.setImageURI(selectedImageUri) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    CategoryCatalog.GROUP_QUICK to stringResource(R.string.group_quick),
                    CategoryCatalog.GROUP_LIFE to stringResource(R.string.group_life),
                    CategoryCatalog.GROUP_WORK to stringResource(R.string.group_work)
                ).forEach { (code, label) ->
                    GlassChip(text = label, selected = selectedGroupCode == code, onClick = {
                        selectedGroupCode = code
                        selectedCategory = CategoryCatalog.getCategoriesByGroup(code).first()
                        if (code != CategoryCatalog.GROUP_WORK) {
                            reminderEnabled = false
                            reminderAt = 0L
                        }
                    })
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1A1F27) else Color.White),
                border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoryMenuExpanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("分类：${selectedCategory.categoryName}", color = palette.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Icon(painterResource(R.drawable.ic_sheet_chevron_down), contentDescription = null, tint = palette.textSecondary)
                    DropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                        categories.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.categoryName) },
                                onClick = {
                                    categoryMenuExpanded = false
                                    selectedCategory = item
                                }
                            )
                        }
                    }
                }
            }

            if (selectedGroupCode == CategoryCatalog.GROUP_WORK) {
                SheetActionCard(
                    if (reminderEnabled) reminderLabel(context, reminderAt) else "开启提醒",
                    if (reminderEnabled) "点击调整时间" else "工作分组支持提醒"
                ) {
                    if (!reminderEnabled) reminderEnabled = true
                    openDateTimePicker(activity, reminderAt) { reminderAt = it }
                }
            }
        }
    }
}

@Composable
private fun SheetModeChip(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) if (isDark) Color(0xFF233752) else Color(0xFFDCEBFF) else Color.Transparent)
    ) {
        Text(text, color = if (selected) palette.accent else palette.textSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun SheetActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDark) Color(0xFF1A1F27) else Color.White)
            .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Text(title, color = palette.textPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = palette.textSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

private fun saveRecord(
    context: Context,
    memoryStore: MemoryStore,
    aiMemoryService: AiMemoryService,
    input: String,
    imageUri: Uri?,
    aiMode: Boolean,
    groupCode: String,
    category: CategoryCatalog.CategoryOption,
    reminderEnabled: Boolean,
    reminderAt: Long
) {
    val imageUriText = imageUri?.toString().orEmpty()
    val finalReminderAt = if (groupCode == CategoryCatalog.GROUP_WORK && reminderEnabled) {
        if (reminderAt > 0L) reminderAt else System.currentTimeMillis() + 60L * 60L * 1000L
    } else {
        0L
    }
    if (!aiMode) {
        val memoryText = if (input.isBlank()) context.getString(R.string.memory_saved_screenshot) else input
        memoryStore.prependRecord(
            MemoryRecord(
                System.currentTimeMillis(),
                MemoryRecord.MODE_NORMAL,
                compactTitle(input, category.categoryName),
                compactSummary(input, memoryText),
                input,
                input,
                imageUriText,
                "",
                memoryText,
                "manual",
                groupCode,
                category.categoryCode,
                category.categoryName,
                finalReminderAt,
                false,
                false
            )
        )
        Toast.makeText(context, R.string.save_success, Toast.LENGTH_SHORT).show()
        return
    }

    val createdAt = System.currentTimeMillis()
    val placeholder = MemoryRecord(
        createdAt,
        MemoryRecord.MODE_AI,
        if (input.isBlank()) "AI 分析中" else compactTitle(input, category.categoryName),
        if (input.isBlank()) "已创建图片记忆，等待模型提取内容" else "已提交到模型分析，完成后自动更新",
        input,
        input,
        imageUriText,
        "AI 分析中…完成后会自动更新这个条目",
        if (input.isBlank()) context.getString(R.string.memory_saved_screenshot) else input,
        "pending",
        groupCode,
        category.categoryCode,
        category.categoryName,
        finalReminderAt,
        false,
        false
    )
    memoryStore.prependRecord(placeholder)
    Toast.makeText(context, "已创建记忆，AI 分析完成后会自动更新", Toast.LENGTH_SHORT).show()
    Thread {
        val resolved = try {
            val result = aiMemoryService.generateMemory(input, imageUri)
            val resolvedCategory = CategoryCatalog.getAllCategories().firstOrNull { it.categoryCode == result.suggestedCategoryCode } ?: category
            MemoryRecord(
                placeholder.recordId,
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
                finalReminderAt,
                false,
                false
            )
        } catch (_: Exception) {
            val fallbackMemory = if (input.isBlank()) context.getString(R.string.memory_saved_screenshot) else input
            MemoryRecord(
                placeholder.recordId,
                createdAt,
                MemoryRecord.MODE_AI,
                compactTitle(input, category.categoryName),
                compactSummary(input, fallbackMemory),
                input,
                input,
                imageUriText,
                context.getString(R.string.memory_fallback_analysis),
                fallbackMemory,
                "local",
                groupCode,
                category.categoryCode,
                category.categoryName,
                finalReminderAt,
                false,
                false
            )
        }
        if (!memoryStore.updateRecord(resolved)) memoryStore.prependRecord(resolved)
        AiSummaryNotifier.notifyAnalysisReady(context.applicationContext, resolved)
    }.start()
}

private fun shouldRequestNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

private fun compactTitle(text: String, fallback: String): String {
    val value = if (text.isBlank()) fallback else text
    val single = value.replace('\n', ' ').trim()
    return if (single.length <= 18) single else single.substring(0, 18) + "..."
}

private fun compactSummary(text: String, fallback: String): String {
    val value = if (text.isBlank()) fallback else text
    val single = value.replace('\n', ' ').trim()
    return if (single.length <= 42) single else single.substring(0, 42) + "..."
}

private fun reminderLabel(context: Context, time: Long): String {
    if (time <= 0L) return "开启提醒"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return context.getString(R.string.reminder_set_time, dateFormat.format(time))
}

private fun openDateTimePicker(activity: Activity?, initial: Long, onSelected: (Long) -> Unit) {
    activity ?: return
    val calendar = Calendar.getInstance()
    if (initial > 0L) calendar.timeInMillis = initial else calendar.add(Calendar.HOUR_OF_DAY, 1)
    DatePickerDialog(
        activity,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            TimePickerDialog(
                activity,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    onSelected(calendar.timeInMillis)
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

private fun queryDisplayName(context: Context, uri: Uri): String {
    var cursor: Cursor? = null
    return try {
        cursor = context.contentResolver.query(uri, null, null, null, null)
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

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
