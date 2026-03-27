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
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay

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

    var aiMode by remember { mutableStateOf(true) }
    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageStatusText by remember { mutableStateOf("\u672A\u6DFB\u52A0\u56FE\u7247") }
    var selectedCategory by remember { mutableStateOf(CategoryCatalog.getQuickCategories().first()) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderAt by remember { mutableStateOf(0L) }
    var saving by remember { mutableStateOf(false) }
    var pendingAiInput by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var dismissCommitted by remember { mutableStateOf(false) }

    val allCategories = remember { CategoryCatalog.getAllCategories() }
    val panelSurface = palette.glassFill
    val panelBorder = palette.glassStroke
    val sheetSurface = if (isDark) Color(0xFF161A20) else Color(0xFFF6F6F7)
    val requestDismiss = remember(onDismiss, saving) {
        {
            if (!saving) {
                visible = false
            }
        }
    }

    DisposableEffect(activity) {
        val window = activity?.window
        val previousSoftInputMode = window?.attributes?.softInputMode
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        onDispose {
            if (window != null && previousSoftInputMode != null) {
                window.setSoftInputMode(previousSoftInputMode)
            }
        }
    }

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

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        selectedImageUri = uri
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {
        }
        imageStatusText = queryDisplayName(context, uri)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val input = pendingAiInput ?: return@rememberLauncherForActivityResult
        pendingAiInput = null
        if (!granted) {
            Toast.makeText(context, "\u5DF2\u521B\u5EFA\u8BB0\u5FC6\uFF0CAI \u5B8C\u6210\u540E\u5C06\u4EC5\u66F4\u65B0\u5217\u8868", Toast.LENGTH_SHORT).show()
        }
        saveRecord(
            context = context,
            memoryStore = memoryStore,
            aiMemoryService = aiMemoryService,
            input = input,
            imageUri = selectedImageUri,
            aiMode = true,
            category = CategoryCatalog.getQuickCategories().first(),
            reminderEnabled = reminderEnabled,
            reminderAt = reminderAt
        )
        requestDismiss()
    }

    BackHandler(enabled = true) {
        requestDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(12f)
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
                    .clickable(onClick = requestDismiss)
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = sheetSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .heightIn(min = if (adaptive.isNarrow) 560.dp else 620.dp, max = if (adaptive.isNarrow) 700.dp else 760.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 56.dp, height = 5.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isDark) Color.White.copy(alpha = 0.16f) else Color(0x24000000))
                    )
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconCircleButton(
                    iconRes = R.drawable.ic_sheet_close,
                    contentDescription = "\u5173\u95ED",
                    onClick = requestDismiss,
                    size = adaptive.topActionButtonSize
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(panelSurface)
                        .border(1.dp, panelBorder, RoundedCornerShape(999.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SheetModeChip("\u666E\u901A", !aiMode, Modifier.weight(1f)) {
                        aiMode = false
                    }
                    SheetModeChip("AI", aiMode, Modifier.weight(1f)) {
                        aiMode = true
                    }
                }
                GlassIconCircleButton(
                    iconRes = R.drawable.ic_sheet_check,
                    contentDescription = "\u786E\u8BA4",
                    onClick = {
                        val input = inputText.trim()
                        if (TextUtils.isEmpty(input) && selectedImageUri == null) {
                            Toast.makeText(context, "\u8BF7\u8F93\u5165\u6587\u5B57\u6216\u6DFB\u52A0\u56FE\u7247", Toast.LENGTH_SHORT).show()
                            return@GlassIconCircleButton
                        }
                        saving = true
                        if (aiMode && shouldRequestNotificationPermission(context)) {
                            pendingAiInput = input
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            saveRecord(
                                context = context,
                                memoryStore = memoryStore,
                                aiMemoryService = aiMemoryService,
                                input = input,
                                imageUri = selectedImageUri,
                                aiMode = aiMode,
                                category = if (aiMode) CategoryCatalog.getQuickCategories().first() else selectedCategory,
                                reminderEnabled = reminderEnabled,
                                reminderAt = reminderAt
                            )
                            requestDismiss()
                        }
                    },
                    size = adaptive.topActionButtonSize
                )
            }

            if (!aiMode) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = panelSurface),
                    border = BorderStroke(1.dp, panelBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryMenuExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "\u8BB0\u5FC6\u7C7B\u522B",
                                color = palette.textSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = selectedCategory.categoryName,
                                color = palette.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Icon(
                            painter = painterResource(R.drawable.ic_sheet_chevron_down),
                            contentDescription = null,
                            tint = palette.textSecondary
                        )
                        DropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false }
                        ) {
                            allCategories.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.categoryName) },
                                    onClick = {
                                        selectedCategory = item
                                        categoryMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = panelSurface),
                border = BorderStroke(1.dp, panelBorder)
            ) {
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = TextStyle(
                        color = palette.textPrimary,
                        fontSize = 17.sp,
                        lineHeight = 25.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                        .height(if (adaptive.isNarrow) 154.dp else 164.dp)
                ) { inner ->
                    Box(Modifier.fillMaxWidth()) {
                        if (inputText.isBlank()) {
                            Text(
                                text = if (aiMode) {
                                    "\u8F93\u5165\u6587\u5B57\uFF0C\u6216\u7ED3\u5408\u526A\u8D34\u677F\u3001\u622A\u56FE\u8FDB\u884C AI \u5206\u6790"
                                } else {
                                    "\u8F93\u5165\u4F60\u8981\u8BB0\u4E0B\u7684\u5185\u5BB9"
                                },
                                color = palette.textTertiary,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                        }
                        inner()
                    }
                }
            }

            if (aiMode) {
                SheetActionCard(
                    title = "\u7C98\u8D34\u526A\u8D34\u677F",
                    subtitle = "\u5C06\u526A\u8D34\u677F\u5185\u5BB9\u8FFD\u52A0\u5230\u8F93\u5165\u6846",
                    onClick = {
                        val clip = context.getSystemService(ClipboardManager::class.java)
                            ?.primaryClip
                            ?.takeIf { it.itemCount > 0 }
                            ?.getItemAt(0)
                            ?.coerceToText(context)
                            ?.toString()
                            ?.trim()
                            .orEmpty()
                        if (clip.isBlank()) {
                            Toast.makeText(context, "\u526A\u8D34\u677F\u6CA1\u6709\u53EF\u7528\u5185\u5BB9", Toast.LENGTH_SHORT).show()
                        } else {
                            inputText = if (inputText.isBlank()) clip else "$inputText\n$clip"
                        }
                    }
                )
                SheetActionCard(
                    title = "\u6DFB\u52A0\u622A\u56FE",
                    subtitle = if (selectedImageUri == null) "\u9009\u62E9\u4E00\u5F20\u622A\u56FE\u8F85\u52A9 AI \u5206\u6790" else imageStatusText,
                    onClick = { pickImageLauncher.launch(arrayOf("image/*")) }
                )
            } else {
                SheetActionCard(
                    title = "\u6DFB\u52A0\u56FE\u7247",
                    subtitle = if (selectedImageUri == null) "\u4E3A\u8FD9\u6761\u8BB0\u5FC6\u9009\u62E9\u4E00\u5F20\u56FE\u7247" else imageStatusText,
                    onClick = { pickImageLauncher.launch(arrayOf("image/*")) }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = panelSurface),
                border = BorderStroke(1.dp, panelBorder)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "\u63D0\u9192\u65F6\u95F4",
                                color = palette.textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (reminderEnabled) reminderLabel(reminderAt) else "\u672A\u8BBE\u7F6E",
                                color = palette.textSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { enabled ->
                                reminderEnabled = enabled
                                if (!enabled) reminderAt = 0L
                            }
                        )
                    }
                    if (reminderEnabled) {
                        SheetInlineButton(
                            text = if (reminderAt > 0L) "\u91CD\u65B0\u9009\u62E9\u65F6\u95F4" else "\u9009\u62E9\u65F6\u95F4",
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                openDateTimePicker(activity, reminderAt) { selected ->
                                    reminderAt = selected
                                }
                            }
                        )
                    }
                }
            }

            if (selectedImageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = panelSurface),
                    border = BorderStroke(1.dp, panelBorder)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            ImageView(ctx).apply {
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(adaptive.addPreviewHeight)
                            .clip(RoundedCornerShape(24.dp)),
                        update = { image ->
                            image.setImageURI(selectedImageUri)
                        }
                    )
                }
            }
        }
            }
        }
    }
}

@Composable
private fun SheetModeChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val selectedBackground = if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.Black.copy(alpha = 0.06f)
    }
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) {
                    selectedBackground
                } else {
                    Color.Transparent
                }
            )
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) palette.glassStroke else Color.Transparent,
                shape = RoundedCornerShape(999.dp)
            )
    ) {
        Text(
            text = text,
            color = if (selected) palette.textPrimary else palette.textSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SheetActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val actionSurface = if (isDark) {
        Color.White.copy(alpha = 0.05f)
    } else {
        Color.Black.copy(alpha = 0.03f)
    }
    val actionBorder = if (isDark) {
        Color.White.copy(alpha = 0.10f)
    } else {
        palette.glassStroke
    }
    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(actionSurface)
            .border(1.dp, actionBorder, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = palette.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = palette.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun SheetInlineButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(palette.glassFillSoft)
            .border(1.dp, palette.glassStroke, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = palette.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun saveRecord(
    context: Context,
    memoryStore: MemoryStore,
    aiMemoryService: AiMemoryService,
    input: String,
    imageUri: Uri?,
    aiMode: Boolean,
    category: CategoryCatalog.CategoryOption,
    reminderEnabled: Boolean,
    reminderAt: Long
) {
    val imageUriText = imageUri?.toString().orEmpty()
    val finalReminderAt = if (reminderEnabled) {
        if (reminderAt > 0L) reminderAt else System.currentTimeMillis() + 60L * 60L * 1000L
    } else {
        0L
    }

    if (!aiMode) {
        val memoryText = if (input.isBlank()) "\u5DF2\u4FDD\u5B58\u56FE\u7247\u8BB0\u5FC6" else input
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
                category.groupCode,
                category.categoryCode,
                category.categoryName,
                finalReminderAt,
                false,
                false
            )
        )
        Toast.makeText(context, "\u5DF2\u4FDD\u5B58", Toast.LENGTH_SHORT).show()
        return
    }

    val aiCategory = CategoryCatalog.getQuickCategories().first()
    val createdAt = System.currentTimeMillis()
    val placeholder = MemoryRecord(
        createdAt,
        MemoryRecord.MODE_AI,
        if (input.isBlank()) "AI \u5206\u6790\u4E2D" else compactTitle(input, aiCategory.categoryName),
        if (input.isBlank()) "\u56FE\u7247\u5DF2\u6DFB\u52A0\uFF0CAI \u6B63\u5728\u751F\u6210\u6458\u8981" else "\u5DF2\u521B\u5EFA\u6761\u76EE\uFF0CAI \u5B8C\u6210\u540E\u4F1A\u81EA\u52A8\u66F4\u65B0",
        input,
        input,
        imageUriText,
        "AI \u5206\u6790\u4E2D\u2026",
        if (input.isBlank()) "\u5DF2\u4FDD\u5B58\u622A\u56FE\u8BB0\u5FC6" else input,
        "pending",
        aiCategory.groupCode,
        aiCategory.categoryCode,
        aiCategory.categoryName,
        finalReminderAt,
        false,
        false
    )
    memoryStore.prependRecord(placeholder)
    Toast.makeText(context, "\u5DF2\u521B\u5EFA\u8BB0\u5FC6\uFF0CAI \u5206\u6790\u5B8C\u6210\u540E\u4F1A\u81EA\u52A8\u66F4\u65B0", Toast.LENGTH_SHORT).show()

    Thread {
        val resolved = try {
            val result = aiMemoryService.generateMemory(input, imageUri)
            val resolvedCategory = CategoryCatalog.getAllCategories().firstOrNull {
                it.categoryCode == result.suggestedCategoryCode
            } ?: aiCategory
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
            val fallbackMemory = if (input.isBlank()) "\u5DF2\u4FDD\u5B58\u56FE\u7247\u8BB0\u5FC6" else input
            MemoryRecord(
                placeholder.recordId,
                createdAt,
                MemoryRecord.MODE_AI,
                compactTitle(input, aiCategory.categoryName),
                compactSummary(input, fallbackMemory),
                input,
                input,
                imageUriText,
                context.getString(R.string.memory_fallback_analysis),
                fallbackMemory,
                "local",
                aiCategory.groupCode,
                aiCategory.categoryCode,
                aiCategory.categoryName,
                finalReminderAt,
                false,
                false
            )
        }
        if (!memoryStore.updateRecord(resolved)) {
            memoryStore.prependRecord(resolved)
        }
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

private fun reminderLabel(time: Long): String {
    if (time <= 0L) return "\u672A\u8BBE\u7F6E"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return dateFormat.format(time)
}

private fun openDateTimePicker(activity: Activity?, initial: Long, onSelected: (Long) -> Unit) {
    activity ?: return
    val calendar = Calendar.getInstance()
    if (initial > 0L) {
        calendar.timeInMillis = initial
    } else {
        calendar.add(Calendar.HOUR_OF_DAY, 1)
    }
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
