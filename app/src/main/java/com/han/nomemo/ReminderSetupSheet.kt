package com.han.nomemo

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs


private data class ReminderLeadOption(
    val label: String,
    val minutes: Int
)

private data class ReminderRepeatOption(
    val label: String,
    val key: String
)

private enum class ReminderCustomLeadUnit(
    val label: String,
    val unitMinutes: Int,
    val valueCap: Int
) {
    MINUTE("分钟", 1, 60),
    HOUR("小时", 60, 24),
    DAY("天", 24 * 60, 31)
}

private enum class ReminderCustomRepeatUnit(
    val label: String
) {
    DAY("天"),
    WEEK("周"),
    MONTH("月"),
    YEAR("年")
}

private enum class ReminderRepeatEndCondition(
    val label: String
) {
    NEVER("永不结束"),
    DATE("指定日期结束"),
    COUNT("重复指定次数后结束")
}

@Composable
internal fun NoMemoReminderSetupSheet(
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

    var year by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.YEAR)) }
    var month by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.MONTH) + 1) }
    var day by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember(initialTime, visible) { mutableStateOf(initialCalendar.get(Calendar.MINUTE)) }
    var leadMinutes by remember(initialTime, visible) { mutableStateOf(defaultLeadMinutes) }
    var selectedLeadIsCustom by remember(initialTime, visible) { mutableStateOf(false) }
    var customLeadHours by remember(initialTime, visible) { mutableStateOf(0) }
    var customLeadPartMinutes by remember(initialTime, visible) { mutableStateOf(defaultLeadMinutes) }
    var customLeadMinutes by remember(initialTime, visible) { mutableStateOf<Int?>(null) }
    var repeatMode by remember(initialTime, visible) { mutableStateOf("none") }
    var customRepeatExpanded by remember(initialTime, visible) { mutableStateOf(false) }
    var customRepeatInterval by remember(initialTime, visible) { mutableStateOf(1) }
    var customRepeatUnit by remember(initialTime, visible) { mutableStateOf(ReminderCustomRepeatUnit.DAY) }
    var repeatEndCondition by remember(initialTime, visible) { mutableStateOf(ReminderRepeatEndCondition.NEVER) }
    var repeatEndDate by remember(initialTime, visible) { mutableStateOf(defaultRepeatEndDate(initialTime)) }
    var repeatEndDateConfirmed by remember(initialTime, visible) { mutableStateOf(false) }
    var repeatEndCount by remember(initialTime, visible) { mutableStateOf(1) }
    var showRepeatEndDateSheet by remember(initialTime, visible) { mutableStateOf(false) }
    var showCustomLeadSheet by remember(initialTime, visible) { mutableStateOf(false) }
    var customLeadPickerUnit by remember(initialTime, visible) { mutableStateOf(ReminderCustomLeadUnit.MINUTE) }
    var customLeadPickerValue by remember(initialTime, visible) { mutableStateOf(defaultLeadMinutes) }

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
            selectedLeadIsCustom = false
            customLeadHours = 0
            customLeadPartMinutes = defaultLeadMinutes
            customLeadMinutes = null
            repeatMode = "none"
            customRepeatExpanded = false
            customRepeatInterval = 1
            customRepeatUnit = ReminderCustomRepeatUnit.DAY
            repeatEndCondition = ReminderRepeatEndCondition.NEVER
            repeatEndDate = defaultRepeatEndDate(initialTime)
            repeatEndDateConfirmed = false
            repeatEndCount = 1
            showRepeatEndDateSheet = false
            showCustomLeadSheet = false
            customLeadPickerUnit = ReminderCustomLeadUnit.MINUTE
            customLeadPickerValue = defaultLeadMinutes
        }
    }

    val eventAt = remember(year, month, day, hour, minute) {
        buildReminderDateTime(year, month, day, hour, minute)
    }
    LaunchedEffect(eventAt) {
        if (repeatEndDate <= eventAt) {
            repeatEndDate = defaultRepeatEndDate(eventAt)
        }
    }
    val effectiveLeadCap = remember(eventAt) { maxValidLeadMinutesFor(eventAt) }
    val leadOptions = remember {
        listOf(
            ReminderLeadOption("5分钟前", 5),
            ReminderLeadOption("15分钟前", 15),
            ReminderLeadOption("30分钟前", 30),
            ReminderLeadOption("1小时前", 60),
            ReminderLeadOption("1天前", 24 * 60)
        )
    }
    val repeatOptions = remember {
        listOf(
            ReminderRepeatOption("不重复", "none"),
            ReminderRepeatOption("每天", "daily"),
            ReminderRepeatOption("每周", "weekly"),
            ReminderRepeatOption("每月", "monthly"),
            ReminderRepeatOption("每年", "yearly"),
            ReminderRepeatOption("自定义", "custom")
        )
    }
    val safeLeadMinutes = leadMinutes.coerceIn(0, effectiveLeadCap)
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
        val currentCustomLead = customLeadMinutes
        if (selectedLeadIsCustom && (currentCustomLead == null || currentCustomLead > effectiveLeadCap)) {
            selectedLeadIsCustom = false
        }
    }
    val sheetSurface = noMemoThemeSyncedSheetSurface(palette, isDark)
    val cardSurface = noMemoThemeSyncedContentSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.96f)),
        lightDefault = Color.White.copy(alpha = 0.995f),
        lightMix = 0.08f
    )
    val timeCardSurface = cardSurface
    val optionCardColor = cardSurface
    val dividerColor = if (isDark) {
        palette.glassStroke.copy(alpha = 0.22f)
    } else {
        Color.Black.copy(alpha = 0.055f)
    }
    val reminderAccent = Color(0xFF1677FF)
    val dragHandleColor = if (isDark) {
        Color(0xFF8E8E93).copy(alpha = 0.72f)
    } else {
        Color(0xFF8E8E93).copy(alpha = 0.68f)
    }
    val wheelHighlightColor = if (isDark) {
        reminderAccent.copy(alpha = 0.20f)
    } else {
        Color(0xFFEAF2FF)
    }
    val wheelHighlightSheenColor = Color.Transparent
    val wheelSelectedTextColor = if (isDark) Color.White.copy(alpha = 0.96f) else reminderAccent
    val wheelNormalTextColor = if (isDark) Color.White.copy(alpha = 0.70f) else palette.textPrimary.copy(alpha = 0.58f)
    val customRepeatSummary = "每${customRepeatInterval}${customRepeatUnit.label}"
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
    val sheetDrag = rememberNoMemoSheetDragController(
        onDismissRequest = {
            onDismiss()
            true
        }
    )
    BackHandler(enabled = visible && (showCustomLeadSheet || showRepeatEndDateSheet)) {
        when {
            showRepeatEndDateSheet -> showRepeatEndDateSheet = false
            showCustomLeadSheet -> showCustomLeadSheet = false
        }
    }

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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 2.dp, top = 2.dp, end = 2.dp, bottom = 14.dp)
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
                                    cardColor = timeCardSurface,
                                    highlightColor = wheelHighlightColor,
                                    highlightSheenColor = wheelHighlightSheenColor,
                                    selectedTextColor = wheelSelectedTextColor,
                                    normalTextColor = wheelNormalTextColor,
                                    onSelected = { year = it },
                                    modifier = Modifier.weight(1.2f),
                                    wheelHeight = reminderWheelHeight,
                                    highlightHeight = reminderWheelHighlightHeight,
                                    showContainer = true
                                )
                                DetailReminderWheelCard(
                                    label = "月",
                                    values = (1..12).toList(),
                                    selectedValue = month,
                                    formatter = { it.toString().padStart(2, '0') },
                                    cardColor = timeCardSurface,
                                    highlightColor = wheelHighlightColor,
                                    highlightSheenColor = wheelHighlightSheenColor,
                                    selectedTextColor = wheelSelectedTextColor,
                                    normalTextColor = wheelNormalTextColor,
                                    onSelected = { month = it },
                                    modifier = Modifier.weight(1f),
                                    wheelHeight = reminderWheelHeight,
                                    highlightHeight = reminderWheelHighlightHeight,
                                    showContainer = true
                                )
                                DetailReminderWheelCard(
                                    label = "日",
                                    values = (1..maxDay).toList(),
                                    selectedValue = day,
                                    formatter = { it.toString().padStart(2, '0') },
                                    cardColor = timeCardSurface,
                                    highlightColor = wheelHighlightColor,
                                    highlightSheenColor = wheelHighlightSheenColor,
                                    selectedTextColor = wheelSelectedTextColor,
                                    normalTextColor = wheelNormalTextColor,
                                    onSelected = { day = it },
                                    modifier = Modifier.weight(1f),
                                    wheelHeight = reminderWheelHeight,
                                    highlightHeight = reminderWheelHighlightHeight,
                                    showContainer = true
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
                                    label = "",
                                    values = (0..23).toList(),
                                    selectedValue = hour,
                                    formatter = { it.toString().padStart(2, '0') },
                                    cardColor = timeCardSurface,
                                    highlightColor = wheelHighlightColor,
                                    highlightSheenColor = wheelHighlightSheenColor,
                                    selectedTextColor = wheelSelectedTextColor,
                                    normalTextColor = wheelNormalTextColor,
                                    onSelected = { hour = it },
                                    modifier = Modifier.width(reminderTimeFieldWidth),
                                    wheelHeight = reminderWheelHeight,
                                    highlightHeight = reminderWheelHighlightHeight,
                                    showContainer = true
                                )
                                Text(
                                    text = ":",
                                    color = palette.textPrimary.copy(alpha = if (isDark) 0.76f else 0.78f),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                )
                                DetailReminderWheelCard(
                                    label = "",
                                    values = (0..59).toList(),
                                    selectedValue = minute,
                                    formatter = { it.toString().padStart(2, '0') },
                                    cardColor = timeCardSurface,
                                    highlightColor = wheelHighlightColor,
                                    highlightSheenColor = wheelHighlightSheenColor,
                                    selectedTextColor = wheelSelectedTextColor,
                                    normalTextColor = wheelNormalTextColor,
                                    onSelected = { minute = it },
                                    modifier = Modifier.width(reminderTimeFieldWidth),
                                    wheelHeight = reminderWheelHeight,
                                    highlightHeight = reminderWheelHighlightHeight,
                                    showContainer = true
                                )
                            }
                        }

                        DetailReminderSectionLabel(
                            text = "提前提醒",
                            color = palette.textSecondary.copy(alpha = if (isDark) 0.78f else 0.72f),
                            topPadding = 2.dp
                        )

                        DetailReminderOptionListCard(
                            surface = optionCardColor
                        ) {
                            leadOptions.forEachIndexed { index, option ->
                                val optionEnabled = option.minutes <= effectiveLeadCap
                                DetailReminderOptionRow(
                                    text = option.label,
                                    selected = !selectedLeadIsCustom && safeLeadMinutes == option.minutes,
                                    enabled = optionEnabled,
                                    accentColor = reminderAccent,
                                    textColor = palette.textPrimary,
                                    disabledTextColor = palette.textTertiary,
                                    showCheck = true
                                ) {
                                    selectedLeadIsCustom = false
                                    leadMinutes = option.minutes
                                }
                                DetailReminderOptionDivider(
                                    visible = true,
                                    color = dividerColor
                                )
                            }
                            DetailReminderOptionRow(
                                text = "自定义",
                                selected = selectedLeadIsCustom,
                                enabled = true,
                                accentColor = reminderAccent,
                                textColor = palette.textPrimary,
                                disabledTextColor = palette.textTertiary,
                                showCheck = true,
                                trailingText = customLeadMinutes?.let(::formatLeadDuration),
                                trailingTextColor = palette.textSecondary.copy(alpha = if (isDark) 0.84f else 0.78f)
                            ) {
                                val seedSourceMinutes = customLeadMinutes ?: (customLeadHours * 60 + customLeadPartMinutes)
                                val seedMinutes = seedSourceMinutes
                                    .coerceAtLeast(defaultLeadMinutes)
                                    .coerceIn(1, effectiveLeadCap.coerceAtLeast(1))
                                val seedUnit = customLeadUnitForMinutes(seedMinutes)
                                customLeadPickerUnit = seedUnit
                                customLeadPickerValue = customLeadValueForMinutes(seedMinutes, seedUnit)
                                showCustomLeadSheet = true
                            }
                        }

                        DetailReminderSectionLabel(
                            text = "重复",
                            color = palette.textSecondary.copy(alpha = if (isDark) 0.78f else 0.72f),
                            topPadding = 22.dp
                        )

                        DetailReminderOptionListCard(
                            surface = optionCardColor
                        ) {
                            repeatOptions.forEachIndexed { index, option ->
                                DetailReminderOptionRow(
                                    text = option.label,
                                    selected = repeatMode == option.key,
                                    enabled = true,
                                    accentColor = reminderAccent,
                                    textColor = palette.textPrimary,
                                    disabledTextColor = palette.textTertiary,
                                    showCheck = true,
                                    trailingText = if (option.key == "custom" && repeatMode == option.key) {
                                        customRepeatSummary
                                    } else {
                                        null
                                    },
                                    trailingTextColor = palette.textSecondary.copy(alpha = if (isDark) 0.84f else 0.78f)
                                ) {
                                    if (option.key == "custom") {
                                        if (repeatMode == "custom") {
                                            customRepeatExpanded = !customRepeatExpanded
                                        } else {
                                            repeatMode = option.key
                                            customRepeatExpanded = true
                                        }
                                    } else {
                                        repeatMode = option.key
                                        customRepeatExpanded = false
                                        showRepeatEndDateSheet = false
                                    }
                                }
                                DetailReminderOptionDivider(
                                    visible = index != repeatOptions.lastIndex,
                                    color = dividerColor
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            AnimatedVisibility(
                                visible = repeatMode == "custom" && customRepeatExpanded,
                                enter = fadeIn(tween(180)),
                                exit = fadeOut(tween(140))
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    DetailReminderCustomRepeatCard(
                                        interval = customRepeatInterval,
                                        unit = customRepeatUnit,
                                        cardSurface = optionCardColor,
                                        accentColor = reminderAccent,
                                        textColor = palette.textPrimary,
                                        secondaryTextColor = palette.textSecondary,
                                        onIntervalChange = { nextInterval ->
                                            customRepeatInterval = nextInterval.coerceIn(
                                                1,
                                                customRepeatIntervalCap(customRepeatUnit)
                                            )
                                        },
                                        onUnitChange = { nextUnit ->
                                            customRepeatUnit = nextUnit
                                            customRepeatInterval = customRepeatInterval.coerceIn(
                                                1,
                                                customRepeatIntervalCap(nextUnit)
                                            )
                                        }
                                    )

                                    DetailReminderSectionLabel(
                                        text = "结束条件",
                                        color = palette.textSecondary.copy(alpha = if (isDark) 0.78f else 0.72f),
                                        topPadding = 22.dp
                                    )

                                    DetailReminderOptionListCard(
                                        surface = optionCardColor
                                    ) {
                                        val endConditions = ReminderRepeatEndCondition.values()
                                        endConditions.forEachIndexed { index, condition ->
                                            DetailReminderOptionRow(
                                                text = condition.label,
                                                selected = repeatEndCondition == condition,
                                                enabled = true,
                                                accentColor = reminderAccent,
                                                textColor = palette.textPrimary,
                                                disabledTextColor = palette.textTertiary,
                                                showCheck = true,
                                                trailingText = when (condition) {
                                                    ReminderRepeatEndCondition.DATE -> {
                                                        if (repeatEndCondition == condition && repeatEndDateConfirmed) {
                                                            formatRepeatEndDate(repeatEndDate)
                                                        } else {
                                                            null
                                                        }
                                                    }
                                                    ReminderRepeatEndCondition.COUNT -> {
                                                        if (repeatEndCondition == condition) "${repeatEndCount}次" else null
                                                    }
                                                    ReminderRepeatEndCondition.NEVER -> null
                                                },
                                                trailingTextColor = palette.textSecondary.copy(alpha = if (isDark) 0.84f else 0.78f)
                                            ) {
                                                when (condition) {
                                                    ReminderRepeatEndCondition.DATE -> {
                                                        when {
                                                            repeatEndCondition == condition && repeatEndDateConfirmed -> {
                                                                showCustomLeadSheet = false
                                                                showRepeatEndDateSheet = true
                                                            }
                                                            repeatEndDateConfirmed -> {
                                                                repeatEndCondition = condition
                                                                showRepeatEndDateSheet = false
                                                            }
                                                            else -> {
                                                                showCustomLeadSheet = false
                                                                showRepeatEndDateSheet = true
                                                            }
                                                        }
                                                    }
                                                    else -> {
                                                        repeatEndCondition = condition
                                                        showRepeatEndDateSheet = false
                                                    }
                                                }
                                            }
                                            DetailReminderOptionDivider(
                                                visible = index != endConditions.lastIndex,
                                                color = dividerColor
                                            )
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = repeatEndCondition == ReminderRepeatEndCondition.COUNT,
                                        enter = fadeIn(tween(180)),
                                        exit = fadeOut(tween(140))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .animateContentSize()
                                        ) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            DetailReminderRepeatCountCard(
                                                count = repeatEndCount,
                                                cardSurface = optionCardColor,
                                                accentColor = reminderAccent,
                                                textColor = palette.textPrimary,
                                                secondaryTextColor = palette.textSecondary,
                                                onCountChange = { nextCount ->
                                                    repeatEndCount = nextCount.coerceIn(1, 999)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .height(24.dp)
                                .navigationBarsPadding()
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = visible && showCustomLeadSheet,
            enter = fadeIn(animationSpec = tween(durationMillis = 160)),
            exit = fadeOut(animationSpec = tween(durationMillis = 140)),
            modifier = Modifier.zIndex(36f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isDark) 0.44f else 0.36f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showCustomLeadSheet = false
                    }
            )
        }

        AnimatedVisibility(
            visible = visible && showCustomLeadSheet,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 240)
            ) + fadeIn(animationSpec = tween(durationMillis = 160)),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 200)
            ) + fadeOut(animationSpec = tween(durationMillis = 130)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(37f)
        ) {
            DetailReminderCustomLeadSheet(
                unit = customLeadPickerUnit,
                value = customLeadPickerValue,
                sheetSurface = sheetSurface,
                wheelSurface = timeCardSurface,
                accentColor = reminderAccent,
                wheelHighlightColor = wheelHighlightColor,
                wheelHighlightSheenColor = wheelHighlightSheenColor,
                wheelSelectedTextColor = wheelSelectedTextColor,
                wheelNormalTextColor = wheelNormalTextColor,
                onUnitChange = { nextUnit ->
                    customLeadPickerUnit = nextUnit
                    customLeadPickerValue = coerceCustomLeadValue(
                        value = customLeadPickerValue,
                        unit = nextUnit
                    )
                },
                onValueChange = { nextValue ->
                    customLeadPickerValue = nextValue
                },
                onCancel = {
                    showCustomLeadSheet = false
                },
                onConfirm = {
                    val customRuleMinutes = (customLeadPickerValue * customLeadPickerUnit.unitMinutes)
                        .coerceAtLeast(1)
                    customLeadMinutes = customRuleMinutes
                    customLeadHours = customRuleMinutes / 60
                    customLeadPartMinutes = customRuleMinutes % 60
                    selectedLeadIsCustom = true
                    leadMinutes = customRuleMinutes
                    showCustomLeadSheet = false
                }
            )
        }

        AnimatedVisibility(
            visible = visible && showRepeatEndDateSheet,
            enter = fadeIn(animationSpec = tween(durationMillis = 160)),
            exit = fadeOut(animationSpec = tween(durationMillis = 140)),
            modifier = Modifier.zIndex(38f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isDark) 0.44f else 0.36f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showRepeatEndDateSheet = false
                    }
            )
        }

        AnimatedVisibility(
            visible = visible && showRepeatEndDateSheet,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 240)
            ) + fadeIn(animationSpec = tween(durationMillis = 160)),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 200)
            ) + fadeOut(animationSpec = tween(durationMillis = 130)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(39f)
        ) {
            DetailReminderEndDateSheet(
                initialDate = repeatEndDate,
                minimumDate = defaultRepeatEndDate(eventAt),
                sheetSurface = sheetSurface,
                wheelSurface = timeCardSurface,
                accentColor = reminderAccent,
                wheelHighlightColor = wheelHighlightColor,
                wheelHighlightSheenColor = wheelHighlightSheenColor,
                wheelSelectedTextColor = wheelSelectedTextColor,
                wheelNormalTextColor = wheelNormalTextColor,
                onCancel = {
                    showRepeatEndDateSheet = false
                },
                onConfirm = { selectedDate ->
                    repeatEndDate = selectedDate
                    repeatEndDateConfirmed = true
                    repeatEndCondition = ReminderRepeatEndCondition.DATE
                    showRepeatEndDateSheet = false
                }
            )
        }

    }
}

@Composable
private fun DetailReminderSectionLabel(
    text: String,
    color: Color,
    topPadding: Dp
) {
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = topPadding, start = 2.dp, bottom = 10.dp)
        )
}

@Composable
private fun DetailReminderOptionListCard(
    surface: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = noMemoG2RoundedShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun DetailReminderOptionRow(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    accentColor: Color,
    textColor: Color,
    disabledTextColor: Color,
    showCheck: Boolean,
    trailingText: String? = null,
    trailingTextColor: Color = textColor.copy(alpha = 0.62f),
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val (backgroundColor, triggerHighlight) = rememberDetailReminderTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "detailReminderOptionRow_$text"
    )
    val rowModifier = Modifier
        .fillMaxWidth()
        .height(58.dp)
        .alpha(if (enabled) 1f else 0.42f)

    @Composable
    fun RowContent() {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) backgroundColor else Color.Transparent)
                .padding(start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (enabled) textColor else disabledTextColor,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (!trailingText.isNullOrBlank()) {
                Text(
                    text = trailingText,
                    color = if (enabled) trailingTextColor else disabledTextColor,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 12.dp, end = if (showCheck && selected) 8.dp else 0.dp)
                )
            }
            if (showCheck && selected) {
                Icon(
                    painter = painterResource(R.drawable.ic_sheet_check),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }

    if (enabled) {
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onClick()
            },
            modifier = rowModifier,
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            RowContent()
        }
    } else {
        Box(
            modifier = rowModifier
        ) {
            RowContent()
        }
    }
}

@Composable
private fun rememberDetailReminderTapHighlightColor(
    interactionSource: MutableInteractionSource,
    isDark: Boolean,
    label: String
): Pair<Color, () -> Unit> {
    val scope = rememberCoroutineScope()
    val pressed by interactionSource.collectIsPressedAsState()
    var holdHighlight by remember { mutableStateOf(false) }
    var holdJob by remember { mutableStateOf<Job?>(null) }

    val highlightActive = pressed || holdHighlight
    val highlightFactor by animateFloatAsState(
        targetValue = if (highlightActive) 1f else 0f,
        animationSpec = if (highlightActive) {
            tween(durationMillis = 75)
        } else {
            tween(durationMillis = 220, easing = FastOutSlowInEasing)
        },
        label = "${label}Factor"
    )

    val backgroundColor = if (isDark) {
        Color.White.copy(alpha = 0.055f * highlightFactor)
    } else {
        Color.Black.copy(alpha = 0.04f * highlightFactor)
    }

    val triggerHighlight = {
        holdJob?.cancel()
        holdHighlight = true
        holdJob = scope.launch {
            delay(150)
            holdHighlight = false
        }
    }

    return backgroundColor to triggerHighlight
}

@Composable
private fun DetailReminderCustomRepeatCard(
    interval: Int,
    unit: ReminderCustomRepeatUnit,
    cardSurface: Color,
    accentColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    onIntervalChange: (Int) -> Unit,
    onUnitChange: (ReminderCustomRepeatUnit) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val maxInterval = customRepeatIntervalCap(unit)
    val controlSurface = if (isDark) {
        Color.White.copy(alpha = 0.07f)
    } else {
        Color.White.copy(alpha = 0.995f)
    }

    DetailReminderNumberAdjustCard(
        title = "间隔",
        value = interval,
        valueCap = maxInterval,
        unitLabel = unit.label,
        cardSurface = cardSurface,
        accentColor = accentColor,
        textColor = textColor,
        secondaryTextColor = secondaryTextColor,
        onValueChange = onIntervalChange
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReminderCustomRepeatUnit.values().forEach { option ->
                DetailReminderCustomUnitButton(
                    text = option.label,
                    selected = option == unit,
                    accentColor = accentColor,
                    idleSurface = controlSurface,
                    textColor = textColor,
                    modifier = Modifier.weight(1f)
                ) {
                    onUnitChange(option)
                }
            }
        }
    }
}

@Composable
private fun DetailReminderRepeatCountCard(
    count: Int,
    cardSurface: Color,
    accentColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    onCountChange: (Int) -> Unit
) {
    DetailReminderNumberAdjustCard(
        title = "重复次数",
        value = count,
        valueCap = 999,
        unitLabel = "次",
        cardSurface = cardSurface,
        accentColor = accentColor,
        textColor = textColor,
        secondaryTextColor = secondaryTextColor,
        onValueChange = onCountChange
    )
}

@Composable
private fun DetailReminderNumberAdjustCard(
    title: String,
    value: Int,
    valueCap: Int,
    unitLabel: String,
    cardSurface: Color,
    accentColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    onValueChange: (Int) -> Unit,
    bottomContent: (@Composable () -> Unit)? = null
) {
    val safeValue = value.coerceIn(1, valueCap.coerceAtLeast(1))
    LaunchedEffect(value, safeValue) {
        if (value != safeValue) {
            onValueChange(safeValue)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = noMemoG2RoundedShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, top = 20.dp, end = 22.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = secondaryTextColor.copy(alpha = if (isSystemInDarkTheme()) 0.80f else 0.72f),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailReminderRepeatStepButton(
                    text = "-",
                    enabled = safeValue > 1,
                    emphasized = false,
                    accentColor = accentColor,
                    textColor = textColor,
                    secondaryTextColor = secondaryTextColor
                ) {
                    onValueChange(safeValue - 1)
                }

                Column(
                    modifier = Modifier.width(112.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = safeValue.toString(),
                        style = TextStyle(
                            color = textColor,
                            fontSize = 40.sp,
                            lineHeight = 42.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = unitLabel,
                        color = secondaryTextColor.copy(alpha = if (isSystemInDarkTheme()) 0.82f else 0.70f),
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                DetailReminderRepeatStepButton(
                    text = "+",
                    enabled = safeValue < valueCap,
                    emphasized = true,
                    accentColor = accentColor,
                    textColor = textColor,
                    secondaryTextColor = secondaryTextColor
                ) {
                    onValueChange(safeValue + 1)
                }
            }

            if (bottomContent != null) {
                Spacer(modifier = Modifier.height(20.dp))
                bottomContent()
            }
        }
    }
}

@Composable
private fun DetailReminderRepeatStepButton(
    text: String,
    enabled: Boolean,
    emphasized: Boolean,
    accentColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val (tapOverlay, triggerHighlight) = rememberDetailReminderTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "detailReminderStepButton_$text"
    )
    val backgroundColor = when {
        !enabled -> Color.Transparent
        emphasized -> accentColor.copy(alpha = if (isDark) 0.18f else 0.11f)
        else -> Color.Black.copy(alpha = if (isDark) 0.16f else 0.045f)
    }
    val contentColor = when {
        !enabled -> secondaryTextColor.copy(alpha = if (isDark) 0.36f else 0.30f)
        emphasized -> accentColor
        else -> textColor.copy(alpha = if (isDark) 0.82f else 0.54f)
    }

    Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .background(if (enabled) tapOverlay else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = {
                    triggerHighlight()
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = contentColor,
                fontSize = 26.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun DetailReminderOptionDivider(
    visible: Boolean,
    color: Color
) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(0.65.dp)
                .background(color)
        )
    }
}

@Composable
private fun DetailReminderEndDateSheet(
    initialDate: Long,
    minimumDate: Long,
    sheetSurface: Color,
    wheelSurface: Color,
    accentColor: Color,
    wheelHighlightColor: Color,
    wheelHighlightSheenColor: Color,
    wheelSelectedTextColor: Color,
    wheelNormalTextColor: Color,
    onCancel: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val minCalendar = remember(minimumDate) {
        Calendar.getInstance().apply { timeInMillis = minimumDate }
    }
    val safeInitialDate = remember(initialDate, minimumDate) {
        initialDate.coerceAtLeast(minimumDate)
    }
    val initialCalendar = remember(safeInitialDate) {
        Calendar.getInstance().apply { timeInMillis = safeInitialDate }
    }
    val minYear = minCalendar.get(Calendar.YEAR)
    val minMonth = minCalendar.get(Calendar.MONTH) + 1
    val minDay = minCalendar.get(Calendar.DAY_OF_MONTH)

    var year by remember(safeInitialDate) { mutableStateOf(initialCalendar.get(Calendar.YEAR).coerceAtLeast(minYear)) }
    var month by remember(safeInitialDate) { mutableStateOf(initialCalendar.get(Calendar.MONTH) + 1) }
    var day by remember(safeInitialDate) { mutableStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }

    val monthStart = if (year == minYear) minMonth else 1
    val monthValues = remember(year, monthStart) { (monthStart..12).toList() }
    val safeMonth = month.coerceIn(monthValues.first(), monthValues.last())
    val maxDay = remember(year, safeMonth) { detailDaysInMonth(year, safeMonth) }
    val dayStart = if (year == minYear && safeMonth == minMonth) minDay else 1
    val dayValues = remember(year, safeMonth, dayStart, maxDay) { (dayStart..maxDay).toList() }

    LaunchedEffect(year, monthStart) {
        if (month < monthStart) {
            month = monthStart
        }
    }
    LaunchedEffect(dayStart, maxDay) {
        val safeDay = day.coerceIn(dayStart, maxDay)
        if (day != safeDay) {
            day = safeDay
        }
    }

    val cancelSurface = if (isDark) Color.White else Color(0xFFE3E4E9)
    val cancelTextColor = if (isDark) Color.Black else palette.textPrimary
    val sheetShape = noMemoG2RoundedShape(topStart = 34.dp, topEnd = 34.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = sheetShape
            ),
        shape = sheetShape,
        colors = CardDefaults.cardColors(containerColor = sheetSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, top = 30.dp, end = 22.dp, bottom = 18.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "选择结束日期",
                color = palette.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailReminderWheelCard(
                    label = "年",
                    values = (minYear..(minYear + 10)).toList(),
                    selectedValue = year,
                    formatter = { it.toString() },
                    cardColor = wheelSurface,
                    highlightColor = wheelHighlightColor,
                    highlightSheenColor = wheelHighlightSheenColor,
                    selectedTextColor = wheelSelectedTextColor,
                    normalTextColor = wheelNormalTextColor,
                    onSelected = { year = it },
                    modifier = Modifier.weight(1.2f),
                    wheelHeight = 150.dp,
                    highlightHeight = 54.dp,
                    showContainer = true
                )
                DetailReminderWheelCard(
                    label = "月",
                    values = monthValues,
                    selectedValue = safeMonth,
                    formatter = { it.toString().padStart(2, '0') },
                    cardColor = wheelSurface,
                    highlightColor = wheelHighlightColor,
                    highlightSheenColor = wheelHighlightSheenColor,
                    selectedTextColor = wheelSelectedTextColor,
                    normalTextColor = wheelNormalTextColor,
                    onSelected = { month = it },
                    modifier = Modifier.weight(1f),
                    wheelHeight = 150.dp,
                    highlightHeight = 54.dp,
                    showContainer = true
                )
                DetailReminderWheelCard(
                    label = "日",
                    values = dayValues,
                    selectedValue = day.coerceIn(dayValues.first(), dayValues.last()),
                    formatter = { it.toString().padStart(2, '0') },
                    cardColor = wheelSurface,
                    highlightColor = wheelHighlightColor,
                    highlightSheenColor = wheelHighlightSheenColor,
                    selectedTextColor = wheelSelectedTextColor,
                    normalTextColor = wheelNormalTextColor,
                    onSelected = { day = it },
                    modifier = Modifier.weight(1f),
                    wheelHeight = 150.dp,
                    highlightHeight = 54.dp,
                    showContainer = true
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailReminderCustomActionButton(
                    text = "取消",
                    background = cancelSurface,
                    contentColor = cancelTextColor,
                    modifier = Modifier.weight(1f),
                    onClick = onCancel
                )
                DetailReminderCustomActionButton(
                    text = "确定",
                    background = accentColor,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onConfirm(buildRepeatEndDate(year, safeMonth, day).coerceAtLeast(minimumDate))
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailReminderCustomLeadSheet(
    unit: ReminderCustomLeadUnit,
    value: Int,
    sheetSurface: Color,
    wheelSurface: Color,
    accentColor: Color,
    wheelHighlightColor: Color,
    wheelHighlightSheenColor: Color,
    wheelSelectedTextColor: Color,
    wheelNormalTextColor: Color,
    onUnitChange: (ReminderCustomLeadUnit) -> Unit,
    onValueChange: (Int) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val values = remember(unit) {
        customLeadValuesForUnit(unit)
    }
    val safeValue = value.coerceIn(values.first(), values.last())
    LaunchedEffect(value, safeValue) {
        if (value != safeValue) {
            onValueChange(safeValue)
        }
    }
    val idleControlSurface = if (isDark) {
        Color.White.copy(alpha = 0.07f)
    } else {
        Color.White.copy(alpha = 0.995f)
    }
    val cancelSurface = if (isDark) Color.White else Color(0xFFE3E4E9)
    val cancelTextColor = if (isDark) Color.Black else palette.textPrimary
    val sheetShape = noMemoG2RoundedShape(topStart = 34.dp, topEnd = 34.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = sheetShape
            ),
        shape = sheetShape,
        colors = CardDefaults.cardColors(containerColor = sheetSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, top = 28.dp, end = 22.dp, bottom = 18.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "自定义提前时间",
                color = palette.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(26.dp))

            DetailReminderWheelCard(
                label = unit.label,
                values = values,
                selectedValue = safeValue,
                formatter = { it.toString().padStart(2, '0') },
                cardColor = wheelSurface,
                highlightColor = wheelHighlightColor,
                highlightSheenColor = wheelHighlightSheenColor,
                selectedTextColor = wheelSelectedTextColor,
                normalTextColor = wheelNormalTextColor,
                onSelected = onValueChange,
                modifier = Modifier.widthIn(min = 220.dp, max = 288.dp),
                wheelHeight = 150.dp,
                highlightHeight = 54.dp,
                showContainer = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReminderCustomLeadUnit.values().forEach { option ->
                    DetailReminderCustomUnitButton(
                        text = option.label,
                        selected = option == unit,
                        accentColor = accentColor,
                        idleSurface = idleControlSurface,
                        textColor = palette.textPrimary,
                        modifier = Modifier.weight(1f)
                    ) {
                        onUnitChange(option)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailReminderCustomActionButton(
                    text = "取消",
                    background = cancelSurface,
                    contentColor = cancelTextColor,
                    modifier = Modifier.weight(1f),
                    onClick = onCancel
                )
                DetailReminderCustomActionButton(
                    text = "确定",
                    background = accentColor,
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = onConfirm
                )
            }
        }
    }
}

@Composable
private fun DetailReminderCustomUnitButton(
    text: String,
    selected: Boolean,
    accentColor: Color,
    idleSurface: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val (tapOverlay, triggerHighlight) = rememberDetailReminderTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "detailReminderUnitButton_$text"
    )
    PressScaleBox(
        onClick = {
            triggerHighlight()
            onClick()
        },
        modifier = modifier
            .height(48.dp)
            .clip(noMemoG2RoundedShape(16.dp)),
        pressedScale = 1f,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(if (selected) accentColor else idleSurface)
                .background(tapOverlay),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else textColor.copy(alpha = 0.62f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DetailReminderCustomActionButton(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val (tapOverlay, triggerHighlight) = rememberDetailReminderTapHighlightColor(
        interactionSource = interactionSource,
        isDark = isDark,
        label = "detailReminderActionButton_$text"
    )
    PressScaleBox(
        onClick = {
            triggerHighlight()
            onClick()
        },
        modifier = modifier
            .height(48.dp)
            .clip(NoMemoG2CapsuleShape),
        pressedScale = 1f,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(background)
                .background(tapOverlay),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
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
                viewportHeight = wheelHeight,
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
    viewportHeight: Dp,
    modifier: Modifier = Modifier
) {
    if (values.isEmpty()) return

    key(values.size, values.first(), values.last()) {
        val itemHeight = 46.dp
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
            modifier = modifier.height(viewportHeight),
            flingBehavior = flingBehavior,
            userScrollEnabled = values.size > 1,
            contentPadding = PaddingValues(vertical = (viewportHeight - itemHeight) / 2),
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
            style = TextStyle(
                color = style.third,
                fontSize = style.first,
                lineHeight = style.first,
                fontWeight = style.second,
                textAlign = TextAlign.Center,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
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

private fun defaultRepeatEndDate(baseTime: Long): Long {
    val seed = if (baseTime > 0L) baseTime else System.currentTimeMillis()
    return Calendar.getInstance().apply {
        timeInMillis = seed
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun buildRepeatEndDate(
    year: Int,
    month: Int,
    day: Int
): Long {
    val safeDay = day.coerceAtMost(detailDaysInMonth(year, month))
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, safeDay)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
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

private fun formatRepeatEndDate(time: Long): String {
    if (time <= 0L) return ""
    val calendar = Calendar.getInstance().apply { timeInMillis = time }
    return String.format(
        "%04d.%02d.%02d",
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

private fun customLeadUnitForMinutes(totalMinutes: Int): ReminderCustomLeadUnit {
    val safeMinutes = totalMinutes.coerceAtLeast(0)
    return when {
        safeMinutes >= ReminderCustomLeadUnit.DAY.unitMinutes &&
            safeMinutes % ReminderCustomLeadUnit.DAY.unitMinutes == 0 -> ReminderCustomLeadUnit.DAY
        safeMinutes >= ReminderCustomLeadUnit.HOUR.unitMinutes &&
            safeMinutes % ReminderCustomLeadUnit.HOUR.unitMinutes == 0 -> ReminderCustomLeadUnit.HOUR
        else -> ReminderCustomLeadUnit.MINUTE
    }
}

private fun customLeadValueForMinutes(
    totalMinutes: Int,
    unit: ReminderCustomLeadUnit
): Int {
    val rawValue = if (unit.unitMinutes <= 0) 0 else totalMinutes.coerceAtLeast(0) / unit.unitMinutes
    return coerceCustomLeadValue(rawValue, unit)
}

private fun coerceCustomLeadValue(
    value: Int,
    unit: ReminderCustomLeadUnit
): Int {
    return value.coerceIn(1, unit.valueCap)
}

private fun customLeadValuesForUnit(unit: ReminderCustomLeadUnit): List<Int> {
    return (1..unit.valueCap).toList()
}

private fun customRepeatIntervalCap(unit: ReminderCustomRepeatUnit): Int {
    return when (unit) {
        ReminderCustomRepeatUnit.DAY -> 365
        ReminderCustomRepeatUnit.WEEK -> 52
        ReminderCustomRepeatUnit.MONTH -> 36
        ReminderCustomRepeatUnit.YEAR -> 99
    }
}

private fun formatLeadDuration(totalMinutes: Int): String {
    val safeMinutes = totalMinutes.coerceAtLeast(0)
    if (safeMinutes == 0) return "准时提醒"
    val days = safeMinutes / (24 * 60)
    val dayRemainder = safeMinutes % (24 * 60)
    val hours = safeMinutes / 60
    val minutes = safeMinutes % 60
    return when {
        days > 0 && dayRemainder == 0 -> "${days}天前"
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
