package com.han.nomemo

import android.Manifest
import android.app.Activity
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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
    var showReminderPicker by remember { mutableStateOf(false) }
    var reminderPickerAt by remember { mutableStateOf(defaultReminderPickerTime()) }
    var saving by remember { mutableStateOf(false) }
    var pendingAiInput by remember { mutableStateOf<String?>(null) }
    var visible by remember { mutableStateOf(false) }
    var dismissCommitted by remember { mutableStateOf(false) }

    val allCategories = remember { CategoryCatalog.getAllCategories() }
    val panelSurface = addSheetPanelSurface(isDark)
    val panelBorder = addSheetBorderColor(isDark, palette)
    val sheetSurface = addSheetBaseSurface(isDark, palette)
    val accentMono = palette.accent
    val accentSoft = palette.accent.copy(alpha = if (isDark) 0.22f else 0.06f)
    val accentStroke = palette.accent.copy(alpha = if (isDark) 0.30f else 0.12f)
    val sheetBodyHeight = if (adaptive.isNarrow) 650.dp else 720.dp
    val openReminderPicker = remember {
        { initial: Long ->
            reminderPickerAt = if (initial > 0L) initial else defaultReminderPickerTime()
            showReminderPicker = true
        }
    }
    val dismissAfterSave = remember {
        {
            visible = false
        }
    }
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

    LaunchedEffect(aiMode) {
        if (aiMode) {
            categoryMenuExpanded = false
        }
    }

    LaunchedEffect(reminderEnabled) {
        if (!reminderEnabled) {
            showReminderPicker = false
        }
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
        dismissAfterSave()
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
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (adaptive.isNarrow) 18.dp else 24.dp,
                        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                    ),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = sheetSurface),
                border = BorderStroke(1.dp, panelBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(sheetBodyHeight)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SheetHeaderButton(
                            iconRes = R.drawable.ic_sheet_close,
                            contentDescription = "关闭",
                            onClick = requestDismiss,
                            size = adaptive.topActionButtonSize
                        )
                        SheetModeSwitch(
                            aiMode = aiMode,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                            onSelectNormal = { aiMode = false },
                            onSelectAi = { aiMode = true }
                        )
                        SheetHeaderButton(
                            iconRes = R.drawable.ic_sheet_check,
                            contentDescription = "确认",
                            onClick = {
                                val input = inputText.trim()
                                if (TextUtils.isEmpty(input) && selectedImageUri == null) {
                                    Toast.makeText(context, "请输入文字或添加图片", Toast.LENGTH_SHORT).show()
                                    return@SheetHeaderButton
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
                                    dismissAfterSave()
                                }
                            },
                            size = adaptive.topActionButtonSize,
                            primary = true
                        )
                    }

                    if (!aiMode) {
                        SheetCategorySection(
                            categories = allCategories,
                            selectedCategory = selectedCategory,
                            expanded = categoryMenuExpanded,
                            accentColor = accentMono,
                            accentSoft = accentSoft,
                            accentStroke = accentStroke,
                            modifier = Modifier.padding(top = 14.dp),
                            onToggleExpanded = { categoryMenuExpanded = !categoryMenuExpanded },
                            onSelectCategory = { item ->
                                selectedCategory = item
                                categoryMenuExpanded = false
                            }
                        )
                    }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                shape = RoundedCornerShape(24.dp),
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = if (inputText.isBlank()) 0.dp else 42.dp)
                        ) {
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
                        if (inputText.isNotBlank()) {
                            SheetMiniIconButton(
                                iconRes = R.drawable.ic_sheet_close,
                                contentDescription = "\u6E05\u7A7A\u6587\u5B57",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 2.dp),
                                onClick = { inputText = "" }
                            )
                        }
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
                            enabled = !saving,
                            onCheckedChange = { enabled ->
                                reminderEnabled = enabled
                                if (!enabled) {
                                    reminderAt = 0L
                                } else if (reminderAt <= 0L) {
                                    openReminderPicker(reminderAt)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = palette.onAccent,
                                checkedTrackColor = accentMono,
                                uncheckedThumbColor = if (isDark) Color.White.copy(alpha = 0.88f) else Color.White,
                                uncheckedTrackColor = addSheetSubtleSurface(isDark),
                                uncheckedBorderColor = addSheetBorderColor(isDark, palette)
                            )
                        )
                    }
                    if (reminderEnabled) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SheetInlineButton(
                                text = if (reminderAt > 0L) "\u91CD\u65B0\u9009\u62E9\u65F6\u95F4" else "\u9009\u62E9\u65F6\u95F4",
                                modifier = Modifier.weight(1f),
                                onClick = { openReminderPicker(reminderAt) }
                            )
                            if (reminderAt > 0L) {
                                SheetInlineButton(
                                    text = "\u6E05\u9664",
                                    modifier = Modifier,
                                    onClick = { reminderAt = 0L }
                                )
                            }
                        }
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
        if (showReminderPicker) {
            ReminderPickerDialog(
                initialMillis = reminderPickerAt,
                onDismiss = { showReminderPicker = false },
                onConfirm = { selected ->
                    reminderAt = selected
                    reminderEnabled = true
                    showReminderPicker = false
                }
            )
        }
    }
}

@Composable
private fun SheetHeaderButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    primary: Boolean = false
) {
    GlassIconCircleButton(
        iconRes = iconRes,
        contentDescription = contentDescription,
        onClick = onClick,
        size = size
    )
}

@Composable
private fun SheetModeSwitch(
    aiMode: Boolean,
    modifier: Modifier = Modifier,
    onSelectNormal: () -> Unit,
    onSelectAi: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(32.dp)
    val borderColor = addSheetBorderColor(isDark, palette)
    val surfaceBrush = addSheetRaisedBrush(isDark, palette)
    Box(
        modifier = modifier
            .height(58.dp)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.08f)
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(surfaceBrush)
                .border(1.dp, borderColor, shape)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(1.dp)
                .clip(RoundedCornerShape(31.dp))
                    .border(
                        0.75.dp,
                        addSheetInnerHighlightColor(isDark),
                        RoundedCornerShape(31.dp)
                    )
        )
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SheetModeChip(
                text = "普通",
                selected = !aiMode,
                modifier = Modifier.weight(1f),
                onClick = onSelectNormal
            )
            SheetModeChip(
                text = "AI",
                selected = aiMode,
                modifier = Modifier.weight(1f),
                onClick = onSelectAi
            )
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
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(28.dp)
    val selectedBrush = Brush.verticalGradient(
        listOf(
            palette.accent.copy(alpha = if (isDark) 0.22f else 0.08f),
            palette.accent.copy(alpha = if (isDark) 0.14f else 0.04f)
        )
    )
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .clip(shape)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(selectedBrush)
                    .border(
                        1.dp,
                        if (isDark) palette.accent.copy(alpha = 0.30f) else palette.accent.copy(alpha = 0.10f),
                        shape
                    )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                addSheetInnerHighlightColor(isDark),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Text(
            text = text,
            color = if (selected) palette.accent else palette.textSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun SheetCategorySection(
    categories: List<CategoryCatalog.CategoryOption>,
    selectedCategory: CategoryCatalog.CategoryOption,
    expanded: Boolean,
    accentColor: Color,
    accentSoft: Color,
    accentStroke: Color,
    modifier: Modifier = Modifier,
    onToggleExpanded: () -> Unit,
    onSelectCategory: (CategoryCatalog.CategoryOption) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val borderSoft = addSheetBorderColor(isDark, palette)
    val inputSurface = addSheetPanelSurface(isDark)
    val selectorShape = RoundedCornerShape(24.dp)
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(240),
        label = "sheetCategoryChevron"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        PressScaleBox(
            onClick = onToggleExpanded,
            modifier = Modifier
                .fillMaxWidth()
                .clip(selectorShape)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                inputSurface,
                                addSheetBaseSurface(isDark, palette)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        if (expanded) accentStroke else borderSoft,
                        selectorShape
                    )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(selectorShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                addSheetInnerHighlightColor(isDark),
                                Color.Transparent
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Text(
                    text = selectedCategory.categoryName,
                    color = palette.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = if (expanded) accentColor else palette.textSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(chevronRotation)
                )
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(140))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            SheetCategoryChip(
                                text = item.categoryName,
                                selected = item.categoryCode == selectedCategory.categoryCode,
                                accentColor = accentColor,
                                accentSoft = accentSoft,
                                accentStroke = accentStroke,
                                modifier = Modifier.weight(1f),
                                onClick = { onSelectCategory(item) }
                            )
                        }
                        repeat(2 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetCategoryChip(
    text: String,
    selected: Boolean,
    accentColor: Color,
    accentSoft: Color,
    accentStroke: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val borderSoft = addSheetBorderColor(isDark, palette)
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    if (selected) {
                        Brush.verticalGradient(listOf(accentSoft, accentSoft.copy(alpha = 0.88f)))
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                addSheetPanelSurface(isDark),
                                addSheetPanelSurface(isDark)
                            )
                        )
                    }
                )
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                addSheetInnerHighlightColor(isDark),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Text(
            text = text,
            color = if (selected) accentColor else palette.textSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun SheetActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(24.dp)
    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.08f)
            )
            .clip(shape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    addSheetRaisedBrush(isDark, palette)
                )
                .border(1.dp, addSheetBorderColor(isDark, palette), shape)
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(1.dp)
                .clip(RoundedCornerShape(23.dp))
                .border(
                    0.75.dp,
                    addSheetInnerHighlightColor(isDark),
                    RoundedCornerShape(23.dp)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = palette.accent,
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(addSheetSubtleSurface(isDark))
                    .border(
                        1.dp,
                        addSheetBorderColor(isDark, palette),
                        CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(18.dp)
                        .rotate(-90f)
                )
            }
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
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(addSheetSubtleBrush(isDark))
                .border(
                    1.dp,
                    addSheetBorderColor(isDark, palette),
                    RoundedCornerShape(18.dp)
                )
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
}

@Composable
private fun SheetMiniIconButton(
    iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(addSheetSubtleSurface(isDark))
                .border(
                    1.dp,
                    addSheetBorderColor(isDark, palette),
                    CircleShape
                )
        )
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = palette.textSecondary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(14.dp)
        )
    }
}

@Composable
private fun ReminderPickerDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    var selectedAt by remember(initialMillis) { mutableStateOf(if (initialMillis > 0L) initialMillis else defaultReminderPickerTime()) }
    val quickOptions = listOf(
        "\u4ECA\u5929" to 0,
        "\u660E\u5929" to 1,
        "3\u5929\u540E" to 3,
        "7\u5929\u540E" to 7
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(30f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if (isDark) 0.58f else 0.30f))
                .clickable(onClick = onDismiss)
        )
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .shadow(
                    elevation = 22.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.32f) else Color.Black.copy(alpha = 0.12f),
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.32f) else Color.Black.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = addSheetPanelSurface(isDark)
            ),
            border = BorderStroke(1.dp, addSheetBorderColor(isDark, palette))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u9009\u62E9\u63D0\u9192\u65F6\u95F4",
                        color = palette.textPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SheetMiniIconButton(
                        iconRes = R.drawable.ic_sheet_close,
                        contentDescription = "\u5173\u95ED\u63D0\u9192\u65F6\u95F4",
                        onClick = onDismiss
                    )
                }
                Text(
                    text = reminderLabel(selectedAt),
                    color = palette.textSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickOptions.forEach { (label, days) ->
                        ReminderPresetChip(
                            text = label,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedAt = setReminderDay(selectedAt, days) }
                        )
                    }
                }
                ReminderAdjustRow(
                    title = "\u65E5\u671F",
                    value = formatReminderDate(selectedAt),
                    modifier = Modifier.padding(top = 14.dp),
                    onDecrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.DAY_OF_YEAR, -1) },
                    onIncrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.DAY_OF_YEAR, 1) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReminderAdjustCard(
                        title = "\u5C0F\u65F6",
                        value = formatReminderHour(selectedAt),
                        modifier = Modifier.weight(1f),
                        onDecrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.HOUR_OF_DAY, -1) },
                        onIncrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.HOUR_OF_DAY, 1) }
                    )
                    ReminderAdjustCard(
                        title = "\u5206\u949F",
                        value = formatReminderMinute(selectedAt),
                        modifier = Modifier.weight(1f),
                        onDecrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.MINUTE, -5) },
                        onIncrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.MINUTE, 5) }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SheetInlineButton(
                        text = "\u53D6\u6D88",
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    )
                    ReminderConfirmButton(
                        text = "\u786E\u5B9A",
                        modifier = Modifier.weight(1f),
                        onClick = { onConfirm(normalizeReminderTime(selectedAt)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderPresetChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(addSheetSubtleBrush(isDark))
                .border(
                    1.dp,
                    addSheetBorderColor(isDark, palette),
                    RoundedCornerShape(18.dp)
                )
        )
        Text(
            text = text,
            color = palette.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ReminderAdjustRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(addSheetSubtleSurface(isDark))
            .border(
                1.dp,
                addSheetBorderColor(isDark, palette),
                RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = palette.textSecondary,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = palette.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )
        ReminderAdjustIconButton(
            text = "-",
            onClick = onDecrease
        )
        ReminderAdjustIconButton(
            text = "+",
            modifier = Modifier.padding(start = 8.dp),
            onClick = onIncrease
        )
    }
}

@Composable
private fun ReminderAdjustCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = addSheetSubtleSurface(isDark)
        ),
        border = BorderStroke(1.dp, addSheetBorderColor(isDark, palette))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = palette.textSecondary,
                fontSize = 13.sp
            )
            Text(
                text = value,
                color = palette.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReminderAdjustIconButton(text = "-", onClick = onDecrease)
                ReminderAdjustIconButton(text = "+", onClick = onIncrease)
            }
        }
    }
}

@Composable
private fun ReminderAdjustIconButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(addSheetSubtleSurface(isDark))
                .border(
                    1.dp,
                    addSheetBorderColor(isDark, palette),
                    CircleShape
                )
        )
        Text(
            text = text,
            color = palette.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ReminderConfirmButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            palette.accent,
                            palette.accent.copy(alpha = 0.90f)
                        )
                    )
                )
                .border(
                    1.dp,
                    if (isDark) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.08f),
                    RoundedCornerShape(18.dp)
                )
        )
        Text(
            text = text,
            color = palette.onAccent,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun addSheetPanelSurface(isDark: Boolean): Color {
    return noMemoCardSurfaceColor(isDark, Color.White.copy(alpha = 0.995f))
}

private fun addSheetBaseSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return noMemoCardSurfaceColor(isDark, palette.memoBgStart)
}

private fun addSheetBorderColor(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) Color.White.copy(alpha = 0.12f) else palette.glassStroke
}

private fun addSheetInnerHighlightColor(isDark: Boolean): Color {
    return if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.78f)
}

private fun addSheetRaisedBrush(isDark: Boolean, palette: NoMemoPalette): Brush {
    return Brush.verticalGradient(
        listOf(
            addSheetPanelSurface(isDark),
            addSheetBaseSurface(isDark, palette)
        )
    )
}

private fun addSheetSubtleSurface(isDark: Boolean): Color {
    return if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.035f)
}

private fun addSheetSubtleBrush(isDark: Boolean): Brush {
    val top = if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.72f)
    return Brush.verticalGradient(
        listOf(
            top,
            addSheetSubtleSurface(isDark)
        )
    )
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

private fun defaultReminderPickerTime(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.HOUR_OF_DAY, 1)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val minute = calendar.get(Calendar.MINUTE)
    val roundedMinute = ((minute + 4) / 5) * 5
    if (roundedMinute >= 60) {
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 0)
    } else {
        calendar.set(Calendar.MINUTE, roundedMinute)
    }
    return calendar.timeInMillis
}

private fun normalizeReminderTime(time: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun adjustReminderTime(base: Long, field: Int, amount: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = if (base > 0L) base else defaultReminderPickerTime()
    calendar.add(field, amount)
    return normalizeReminderTime(calendar.timeInMillis)
}

private fun setReminderDay(base: Long, daysFromToday: Int): Long {
    val source = Calendar.getInstance().apply {
        timeInMillis = if (base > 0L) base else defaultReminderPickerTime()
    }
    val target = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, daysFromToday)
        set(Calendar.HOUR_OF_DAY, source.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, source.get(Calendar.MINUTE))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return target.timeInMillis
}

private fun formatReminderDate(time: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd E", Locale.getDefault())
    return dateFormat.format(time)
}

private fun formatReminderHour(time: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = time }
    return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.HOUR_OF_DAY))
}

private fun formatReminderMinute(time: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = time }
    return String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MINUTE))
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
