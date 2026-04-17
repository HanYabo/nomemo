package com.han.nomemo

import android.Manifest
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    onDismiss: () -> Unit,
    onSaved: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val memoryStore = remember(context) { MemoryStore(context) }
    val settingsStore = remember(context) { SettingsStore(context) }
    val aiMemoryService = remember(context) { AiMemoryService(context) }
    val adaptive = rememberNoMemoAdaptiveSpec()
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()

    val aiEnabled = remember { settingsStore.isAiAvailable() }
    var aiMode by remember(aiEnabled) { mutableStateOf(aiEnabled) }
    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
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
    var showExitConfirm by remember { mutableStateOf(false) }

    val defaultCategory = remember { CategoryCatalog.getQuickCategories().first() }
    val allCategories = remember { CategoryCatalog.getAllCategories() }
    val panelSurface = addSheetPanelSurface(isDark, palette)
    val sheetSurface = addSheetBaseSurface(isDark, palette)
    val inputSurface = addSheetInputSurface(isDark, palette)
    val actionSurface = if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.96f))
    } else {
        Color.White.copy(alpha = 0.985f)
    }
    val inputTextColor = palette.textPrimary
    val inputHintColor = palette.textTertiary
    val sheetBodyHeight = rememberNoMemoSheetHeight(
        compactPreferredHeight = 650.dp,
        regularPreferredHeight = 720.dp,
        compactScreenFraction = 0.86f,
        regularScreenFraction = 0.82f,
        minimumHeight = 360.dp
    )
    val dragHandleColor = if (isDark) {
        Color(0xFF8E8E93).copy(alpha = 0.72f)
    } else {
        Color(0xFF8E8E93).copy(alpha = 0.68f)
    }
    val hasDraftChanges =
        inputText.isNotBlank() ||
            selectedImageUri != null ||
            selectedCategory.categoryCode != defaultCategory.categoryCode ||
            (reminderEnabled && reminderAt > 0L)
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
    val requestDismiss = remember(saving, hasDraftChanges) {
        {
            if (saving) {
                Unit
            } else if (hasDraftChanges) {
                showExitConfirm = true
            } else {
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
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {
        }
        // 复制图片到应用缓存并保存拷贝 URI（file://...）
        val copiedUriString = try {
            ImageUtils.copyUriToCache(context, uri)
        } catch (_: Exception) {
            null
        }
        selectedImageUri = if (copiedUriString != null) Uri.parse(copiedUriString) else uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val input = pendingAiInput ?: return@rememberLauncherForActivityResult
        pendingAiInput = null
        if (!granted) {
            Toast.makeText(context, "已创建记忆，AI 完成后将仅更新列表", Toast.LENGTH_SHORT).show()
        }
        saveRecord(
            context = context,
            memoryStore = memoryStore,
            aiMemoryService = aiMemoryService,
            input = input,
            imageUri = selectedImageUri,
            aiMode = true,
            category = defaultCategory,
            reminderEnabled = reminderEnabled && reminderAt > 0L,
            reminderAt = reminderAt
        )
        onSaved?.invoke()
        dismissAfterSave()
    }

    val performSaveAndDismiss = save@{
        val input = inputText.trim()
        if (TextUtils.isEmpty(input) && selectedImageUri == null) {
            Toast.makeText(context, "请输入文字或添加图片", Toast.LENGTH_SHORT).show()
            return@save
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
                category = if (aiMode) defaultCategory else selectedCategory,
                reminderEnabled = reminderEnabled && reminderAt > 0L,
                reminderAt = reminderAt
            )
            onSaved?.invoke()
            dismissAfterSave()
        }
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = requestDismiss
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
                        shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp)
                    ),
                shape = noMemoG2RoundedShape(topStart = 36.dp, topEnd = 36.dp),
                colors = CardDefaults.cardColors(containerColor = sheetSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = sheetBodyHeight)
                        .padding(start = 14.dp, top = 10.dp, end = 14.dp, bottom = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 56.dp, height = 5.dp)
                            .clip(NoMemoG2CapsuleShape)
                            .background(dragHandleColor)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SheetHeaderButton(
                            iconRes = R.drawable.ic_sheet_close,
                            contentDescription = "关闭",
                            onClick = requestDismiss,
                            size = adaptive.topActionButtonSize
                        )
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            SheetModeSwitch(
                                aiMode = aiMode,
                                aiEnabled = aiEnabled,
                                modifier = Modifier.fillMaxWidth(if (adaptive.isNarrow) 0.52f else 0.46f),
                                onSelectNormal = { aiMode = false },
                                onSelectAi = { if (aiEnabled) aiMode = true }
                            )
                        }
                        SheetHeaderButton(
                            iconRes = R.drawable.ic_sheet_check,
                            contentDescription = "确认",
                            onClick = performSaveAndDismiss,
                            size = adaptive.topActionButtonSize
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(top = 16.dp)
                    ) {
                        if (!(aiMode && aiEnabled)) {
                            SheetCategorySection(
                                categories = allCategories,
                                selectedCategory = selectedCategory,
                                expanded = categoryMenuExpanded,
                                modifier = Modifier,
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
                                .padding(
                                    top = if (aiMode) 0.dp else 18.dp
                                ),
                            shape = noMemoG2RoundedShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = inputSurface)
                        ) {
                            BasicTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                textStyle = TextStyle(
                                    color = inputTextColor,
                                    fontSize = 17.sp,
                                    lineHeight = 25.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 18.dp)
                                    .height(adaptive.addInputHeight)
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
                                                    "输入文字，或结合剪贴板、截图进行 AI 分析"
                                                } else {
                                                    "输入你要记下的内容"
                                                },
                                                color = inputHintColor,
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp
                                            )
                                        }
                                        inner()
                                    }
                                    if (inputText.isNotBlank()) {
                                        SheetMiniIconButton(
                                            iconRes = R.drawable.ic_sheet_close,
                                            contentDescription = "清空文字",
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
                                title = "粘贴剪贴板",
                                surfaceColor = actionSurface,
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
                                        Toast.makeText(context, "剪贴板没有可用内容", Toast.LENGTH_SHORT).show()
                                    } else {
                                        inputText = if (inputText.isBlank()) clip else "$inputText\n$clip"
                                    }
                                }
                            )
                            val selectedImage = selectedImageUri
                            if (selectedImage != null) {
                                SheetImagePreviewCard(
                                    imageUri = selectedImage,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 18.dp),
                                    previewHeight = adaptive.addPreviewHeight,
                                    surfaceColor = panelSurface,
                                    onRemove = { selectedImageUri = null }
                                )
                            } else {
                                SheetActionCard(
                                    title = "添加图片",
                                    surfaceColor = actionSurface,
                                    onClick = { pickImageLauncher.launch(arrayOf("image/*")) }
                                )
                            }
                        } else {
                            val selectedImage = selectedImageUri
                            if (selectedImage != null) {
                                SheetImagePreviewCard(
                                    imageUri = selectedImage,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 18.dp),
                                    previewHeight = adaptive.addPreviewHeight,
                                    surfaceColor = panelSurface,
                                    onRemove = { selectedImageUri = null }
                                )
                            } else {
                                SheetActionCard(
                                    title = "添加图片",
                                    surfaceColor = actionSurface,
                                    onClick = { pickImageLauncher.launch(arrayOf("image/*")) }
                                )
                            }
                        }

                        SheetInlineButton(
                            text = if (reminderEnabled && reminderAt > 0L) {
                                "提醒时间  ${reminderLabel(reminderAt)}"
                            } else {
                                "提醒时间"
                            },
                            surfaceColor = actionSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 18.dp),
                            onClick = { openReminderPicker(reminderAt) }
                        )
                        Spacer(modifier = Modifier.height(if (adaptive.isNarrow) 28.dp else 36.dp))
                    }
                }
            }
        }
        if (showExitConfirm) {
            NoMemoTernaryConfirmDialog(
                title = "退出编辑？",
                message = "当前有未保存内容，是否保存后退出？",
                confirmText = "保存并退出",
                dismissText = "取消",
                tertiaryText = "不保存",
                destructiveTertiary = true,
                onConfirm = {
                    showExitConfirm = false
                    performSaveAndDismiss()
                },
                onDismiss = { showExitConfirm = false },
                onTertiary = {
                    showExitConfirm = false
                    visible = false
                }
            )
        }
        if (showReminderPicker) {
            ReminderPickerDialog(
                initialMillis = reminderPickerAt,
                onDismiss = { showReminderPicker = false },
                onClear = {
                    reminderAt = 0L
                    reminderEnabled = false
                    showReminderPicker = false
                },
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
    size: androidx.compose.ui.unit.Dp
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
    aiEnabled: Boolean,
    modifier: Modifier = Modifier,
    onSelectNormal: () -> Unit,
    onSelectAi: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val shape = noMemoG2RoundedShape(32.dp)
    val switchSurface = addSheetInputSurface(isDark, palette)
    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.08f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(switchSurface)
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SheetModeChip(
                text = "普通",
                selected = !aiMode,
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = onSelectNormal
            )
            SheetModeChip(
                text = "AI",
                selected = aiMode,
                enabled = aiEnabled,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                onClick = onSelectAi
            )
        }
    }
}

@Composable
private fun SheetModeChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val shape = noMemoG2RoundedShape(28.dp)
    val selectedSurface = if (isDark) {
        palette.accent.copy(alpha = 0.22f)
    } else {
        palette.accent.copy(alpha = 0.12f)
    }
    val selectedTextColor = palette.textPrimary
    PressScaleBox(
        onClick = { if (enabled) onClick() },
        modifier = modifier
            .clip(shape)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(selectedSurface)
            )
        }
        Text(
            text = text,
            color = if (!enabled) palette.textTertiary else if (selected) selectedTextColor else palette.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(if (enabled) 1f else 0.52f)
        )
    }
}

@Composable
fun SheetCategorySection(
    categories: List<CategoryCatalog.CategoryOption>,
    selectedCategory: CategoryCatalog.CategoryOption,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    detailStyle: Boolean = false,
    onToggleExpanded: () -> Unit,
    onSelectCategory: (CategoryCatalog.CategoryOption) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val selectorShape = noMemoG2RoundedShape(if (detailStyle) 22.dp else 24.dp)
    val selectorSurface = if (detailStyle) noMemoCardSurfaceColor(isDark) else addSheetCategorySelectorSurface(isDark, palette)
    val menuSurface = if (detailStyle) noMemoCardSurfaceColor(isDark) else addSheetCategoryMenuSurface(isDark, palette)
    val primaryTextColor = palette.textPrimary
    val chevronColor = if (expanded) palette.textPrimary else palette.textSecondary
    val selectedSummary = selectedCategory.categoryName
    val selectorHeight = if (detailStyle) 54.dp else 56.dp
    val selectorTextSize = if (detailStyle) 16.sp else 17.sp
    val selectorTextWeight = if (detailStyle) FontWeight.Medium else FontWeight.SemiBold
    val selectorDotStart = if (detailStyle) 18.dp else 16.dp
    val selectorTextStart = if (detailStyle) 34.dp else 32.dp
    val selectorChevronEnd = if (detailStyle) 18.dp else 16.dp
    val selectorChevronSize = if (detailStyle) 18.dp else 20.dp
    val menuTopPadding = if (detailStyle) 10.dp else 12.dp
    val menuContentHorizontalPadding = if (detailStyle) 16.dp else 14.dp
    val menuContentVerticalPadding = if (detailStyle) 14.dp else 12.dp
    val groupedByParent = remember(categories) { categories.groupBy { it.groupCode } }
    val orderedParents = remember(groupedByParent) {
        listOf(CategoryCatalog.GROUP_LIFE, CategoryCatalog.GROUP_WORK, CategoryCatalog.GROUP_QUICK)
            .filter { groupedByParent[it]?.isNotEmpty() == true }
    }
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
                .height(selectorHeight)
                .clip(selectorShape)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(selectorShape)
                    .background(selectorSurface)
            )
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = selectorDotStart)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(addSheetCategoryDotColor(selectedCategory.categoryCode))
                )
                Text(
                    text = "分类  $selectedSummary",
                    color = primaryTextColor,
                    fontSize = selectorTextSize,
                    fontWeight = selectorTextWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .padding(start = selectorTextStart, end = 46.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = chevronColor,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = selectorChevronEnd)
                        .size(selectorChevronSize)
                        .rotate(chevronRotation)
                )
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(140))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = menuTopPadding),
                shape = noMemoG2RoundedShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = menuSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = menuContentHorizontalPadding,
                            vertical = menuContentVerticalPadding
                        )
                ) {
                    orderedParents.forEachIndexed { parentIndex, parentCode ->
                        if (parentIndex > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f)
                                    )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        Text(
                            text = CategoryCatalog.getGroupName(parentCode),
                            color = palette.textSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )
                        val children = groupedByParent[parentCode].orEmpty()
                        children.forEachIndexed { childIndex, item ->
                            SheetCategoryListItem(
                                item = item,
                                selected = item.categoryCode == selectedCategory.categoryCode,
                                showDivider = childIndex != children.lastIndex,
                                compact = detailStyle,
                                onClick = { onSelectCategory(item) }
                            )
                        }
                    }                    
                }
            }            
        }
    }
}

@Composable
private fun SheetCategoryListItem(
    item: CategoryCatalog.CategoryOption,
    selected: Boolean,
    showDivider: Boolean,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val rowShape = noMemoG2RoundedShape(12.dp)
    val textColor = palette.textPrimary
    val rowHeight = if (compact) 46.dp else 48.dp
    val textSize = if (compact) 16.sp else 17.sp
    val rowHorizontalPadding = if (compact) 12.dp else 10.dp
    val textStartPadding = if (compact) 10.dp else 12.dp
    val checkSize = if (compact) 17.dp else 18.dp
    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .clip(rowShape)
                    .padding(horizontal = rowHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(addSheetCategoryDotColor(item.categoryCode))
                )
                Text(
                    text = item.categoryName,
                    color = textColor,
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = textStartPadding)
                )
                AnimatedVisibility(visible = selected) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sheet_check),
                        contentDescription = null,
                        tint = palette.accent,
                        modifier = Modifier.size(checkSize)
                    )
                }
            }
            if (showDivider) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp)
                        .height(1.dp)
                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f))
                )
            }
        }
    }
}

@Composable
private fun SheetActionCard(
    title: String,
    surfaceColor: Color,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val titleColor = if (isDark) Color(0xFF2E8BFF) else Color(0xFF1677FF)
    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        pressedScale = 1f
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(noMemoG2RoundedShape(20.dp))
                .background(surfaceColor)
                .padding(horizontal = 20.dp, vertical = 17.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SheetInlineButton(
    text: String,
    surfaceColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val titleColor = if (isDark) Color(0xFF2E8BFF) else Color(0xFF1677FF)
    PressScaleBox(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        pressedScale = 1f
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(noMemoG2RoundedShape(20.dp))
                .background(surfaceColor)
                .padding(horizontal = 20.dp, vertical = 17.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = titleColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SheetImagePreviewCard(
    imageUri: Uri,
    modifier: Modifier = Modifier,
    previewHeight: androidx.compose.ui.unit.Dp,
    surfaceColor: Color,
    onRemove: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = noMemoG2RoundedShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
        ) {
            AndroidView(
                factory = { ctx ->
                    ImageView(ctx).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(noMemoG2RoundedShape(24.dp)),
                update = { image ->
                    image.setImageURI(imageUri)
                }
            )
            SheetMiniIconButton(
                iconRes = R.drawable.ic_sheet_close,
                contentDescription = "删除图片",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp),
                onClick = onRemove
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
                .background(addSheetSubtleSurface(isDark, palette))
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
    onClear: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val canClear = initialMillis > 0L
    var selectedAt by remember(initialMillis) { mutableStateOf(if (initialMillis > 0L) initialMillis else defaultReminderPickerTime()) }
    val quickOptions = listOf(
        "今天" to 0,
        "明天" to 1,
        "3天后" to 3,
        "7天后" to 7
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
                    shape = noMemoG2RoundedShape(24.dp),
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.32f) else Color.Black.copy(alpha = 0.12f),
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.32f) else Color.Black.copy(alpha = 0.12f)
                ),
            shape = noMemoG2RoundedShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = addSheetPanelSurface(isDark, palette)
            ),
            
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
                        text = "选择提醒时间",
                        color = palette.textPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    SheetMiniIconButton(
                        iconRes = R.drawable.ic_sheet_close,
                        contentDescription = "关闭提醒时间",
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
                    title = "日期",
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
                        title = "小时",
                        value = formatReminderHour(selectedAt),
                        modifier = Modifier.weight(1f),
                        onDecrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.HOUR_OF_DAY, -1) },
                        onIncrease = { selectedAt = adjustReminderTime(selectedAt, Calendar.HOUR_OF_DAY, 1) }
                    )
                    ReminderAdjustCard(
                        title = "分钟",
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
                    ReminderFooterActionButton(
                        text = "取消",
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    )
                    if (canClear) {
                        ReminderFooterActionButton(
                            text = "清除",
                            modifier = Modifier.weight(1f),
                            onClick = onClear
                        )
                    }
                    ReminderFooterActionButton(
                        text = "确定",
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
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = noMemoG2RoundedShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = addSheetInputSurface(isDark, palette)),
            border = BorderStroke(
                1.dp,
                if (isDark) Color.White.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.10f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = palette.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
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
                .background(addSheetInputSurface(isDark, palette))
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
            .clip(noMemoG2RoundedShape(18.dp))
            .background(addSheetSubtleSurface(isDark, palette))
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
        shape = noMemoG2RoundedShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = addSheetSubtleSurface(isDark, palette)
        )
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
private fun ReminderFooterActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = noMemoG2RoundedShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = addSheetInputSurface(isDark, palette)),
            border = BorderStroke(
                1.dp,
                if (isDark) Color.White.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.10f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = palette.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun addSheetPanelSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.94f))
    } else {
        Color.White.copy(alpha = 0.995f)
    }
}

private fun addSheetBaseSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) Color(0xFF121316) else Color(0xFFF5F6F8)
}

private fun addSheetInputSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.96f))
    } else {
        Color.White
    }
}

private fun addSheetCategorySelectorSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.96f))
    } else {
        Color.White
    }
}

private fun addSheetCategoryMenuSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.96f))
    } else {
        Color.White
    }
}

private fun addSheetCategoryDotColor(categoryCode: String): Color {
    return when (categoryCode) {
        CategoryCatalog.CODE_LIFE_PICKUP -> Color(0xFFFFA157)
        CategoryCatalog.CODE_LIFE_DELIVERY -> Color(0xFF69A7FF)
        CategoryCatalog.CODE_LIFE_CARD -> Color(0xFFD2B37C)
        CategoryCatalog.CODE_LIFE_TICKET -> Color(0xFF9C7CFF)
        CategoryCatalog.CODE_WORK_TODO -> Color(0xFF58D89A)
        CategoryCatalog.CODE_WORK_SCHEDULE -> Color(0xFF4F8CFF)
        else -> Color(0xFFF3C243)
    }
}

private fun addSheetBorderColor(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) Color.White.copy(alpha = 0.12f) else palette.glassStroke
}

private fun addSheetSubtleSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    return if (isDark) {
        palette.glassFillSoft.copy(alpha = 0.68f)
    } else {
        Color.Black.copy(alpha = 0.035f)
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
    val aiEnabled = SettingsStore(context).isAiAvailable()
    val quickNoteCategory = CategoryCatalog.getQuickCategories().first()
    val finalReminderAt = if (reminderEnabled) {
        if (reminderAt > 0L) reminderAt else System.currentTimeMillis() + 60L * 60L * 1000L
    } else {
        0L
    }

    if (!(aiMode && aiEnabled)) {
        val finalCategory = if (aiEnabled) category else quickNoteCategory
        val memoryText = if (input.isBlank()) "已保存图片记忆" else input
        memoryStore.prependRecord(
            MemoryRecord(
                System.currentTimeMillis(),
                MemoryRecord.MODE_NORMAL,
                compactTitle(input, finalCategory.categoryName),
                compactSummary(input, memoryText),
                input,
                input,
                imageUriText,
                "",
                memoryText,
                "manual",
                finalCategory.groupCode,
                finalCategory.categoryCode,
                finalCategory.categoryName,
                finalReminderAt,
                false,
                false
            )
        )
        Toast.makeText(context, "已保存", Toast.LENGTH_SHORT).show()
        return
    }

    val aiCategory = quickNoteCategory
    val createdAt = System.currentTimeMillis()
    val placeholder = MemoryRecord(
        createdAt,
        MemoryRecord.MODE_AI,
        if (input.isBlank()) "AI 分析中" else compactTitle(input, aiCategory.categoryName),
        if (input.isBlank()) "图片已添加，AI 正在生成摘要" else "已创建条目，AI 完成后会自动更新",
        input,
        input,
        imageUriText,
        "AI 分析中...",
        if (input.isBlank()) "已保存图片记忆" else input,
        "pending",
        aiCategory.groupCode,
        aiCategory.categoryCode,
        aiCategory.categoryName,
        finalReminderAt,
        false,
        false
    )
    memoryStore.prependRecord(placeholder)
    Toast.makeText(context, "已创建记忆，AI 分析完成后会自动更新", Toast.LENGTH_SHORT).show()

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
            val fallbackMemory = if (input.isBlank()) "已保存图片记忆" else input
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
    if (time <= 0L) return "未设置"
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

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


