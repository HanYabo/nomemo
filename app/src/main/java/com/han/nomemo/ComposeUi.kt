package com.han.nomemo

import android.net.Uri
import android.widget.ImageView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NoMemoPalette(
    val memoBgStart: Color,
    val memoBgMid: Color,
    val memoBgEnd: Color,
    val glassFill: Color,
    val glassFillSoft: Color,
    val glassStroke: Color,
    val accent: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val tagNoteBg: Color,
    val tagNoteText: Color,
    val tagAiBg: Color,
    val tagAiText: Color
)

enum class NoMemoWidthClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

enum class NoMemoDockTab {
    MEMORY,
    GROUP,
    REMINDER
}

data class NoMemoAdaptiveSpec(
    val widthClass: NoMemoWidthClass,
    val isNarrow: Boolean,
    val maxContentWidth: Dp,
    val pageHorizontalPadding: Dp,
    val pageTopPadding: Dp,
    val pageBottomPadding: Dp,
    val titleSize: TextUnit,
    val subtitleSize: TextUnit,
    val sectionTitleSize: TextUnit,
    val chipTextSize: TextUnit,
    val topActionButtonSize: Dp,
    val bottomNavHeight: Dp,
    val bottomNavItemHeight: Dp,
    val fabFrameSize: Dp,
    val fabButtonSize: Dp,
    val recordImageWidth: Dp,
    val recordImageHeight: Dp,
    val recordTitleSize: TextUnit,
    val addInputHeight: Dp,
    val addPreviewHeight: Dp
)

@Composable
fun rememberNoMemoAdaptiveSpec(): NoMemoAdaptiveSpec {
    val config = LocalConfiguration.current
    val width = config.screenWidthDp
    return when {
        width >= 900 -> NoMemoAdaptiveSpec(
            widthClass = NoMemoWidthClass.EXPANDED,
            isNarrow = false,
            maxContentWidth = 940.dp,
            pageHorizontalPadding = 28.dp,
            pageTopPadding = 24.dp,
            pageBottomPadding = 132.dp,
            titleSize = 48.sp,
            subtitleSize = 14.sp,
            sectionTitleSize = 22.sp,
            chipTextSize = 15.sp,
            topActionButtonSize = 62.dp,
            bottomNavHeight = 90.dp,
            bottomNavItemHeight = 66.dp,
            fabFrameSize = 84.dp,
            fabButtonSize = 72.dp,
            recordImageWidth = 110.dp,
            recordImageHeight = 146.dp,
            recordTitleSize = 23.sp,
            addInputHeight = 170.dp,
            addPreviewHeight = 200.dp
        )

        width >= 700 -> NoMemoAdaptiveSpec(
            widthClass = NoMemoWidthClass.MEDIUM,
            isNarrow = false,
            maxContentWidth = 760.dp,
            pageHorizontalPadding = 22.dp,
            pageTopPadding = 22.dp,
            pageBottomPadding = 122.dp,
            titleSize = 44.sp,
            subtitleSize = 13.sp,
            sectionTitleSize = 21.sp,
            chipTextSize = 14.sp,
            topActionButtonSize = 58.dp,
            bottomNavHeight = 86.dp,
            bottomNavItemHeight = 64.dp,
            fabFrameSize = 80.dp,
            fabButtonSize = 70.dp,
            recordImageWidth = 102.dp,
            recordImageHeight = 138.dp,
            recordTitleSize = 22.sp,
            addInputHeight = 160.dp,
            addPreviewHeight = 184.dp
        )

        width <= 420 -> NoMemoAdaptiveSpec(
            widthClass = NoMemoWidthClass.COMPACT,
            isNarrow = true,
            maxContentWidth = 560.dp,
            pageHorizontalPadding = 12.dp,
            pageTopPadding = 14.dp,
            pageBottomPadding = 98.dp,
            titleSize = 34.sp,
            subtitleSize = 11.sp,
            sectionTitleSize = 18.sp,
            chipTextSize = 12.sp,
            topActionButtonSize = 46.dp,
            bottomNavHeight = 74.dp,
            bottomNavItemHeight = 56.dp,
            fabFrameSize = 70.dp,
            fabButtonSize = 60.dp,
            recordImageWidth = 64.dp,
            recordImageHeight = 88.dp,
            recordTitleSize = 17.sp,
            addInputHeight = 124.dp,
            addPreviewHeight = 144.dp
        )

        else -> NoMemoAdaptiveSpec(
            widthClass = NoMemoWidthClass.COMPACT,
            isNarrow = false,
            maxContentWidth = 620.dp,
            pageHorizontalPadding = 16.dp,
            pageTopPadding = 16.dp,
            pageBottomPadding = 104.dp,
            titleSize = 36.sp,
            subtitleSize = 12.sp,
            sectionTitleSize = 20.sp,
            chipTextSize = 13.sp,
            topActionButtonSize = 48.dp,
            bottomNavHeight = 80.dp,
            bottomNavItemHeight = 60.dp,
            fabFrameSize = 78.dp,
            fabButtonSize = 68.dp,
            recordImageWidth = 84.dp,
            recordImageHeight = 112.dp,
            recordTitleSize = 19.sp,
            addInputHeight = 138.dp,
            addPreviewHeight = 158.dp
        )
    }
}

@Composable
fun rememberNoMemoPalette(): NoMemoPalette {
    return NoMemoPalette(
        memoBgStart = colorResource(id = R.color.memo_bg_start),
        memoBgMid = colorResource(id = R.color.memo_bg_mid),
        memoBgEnd = colorResource(id = R.color.memo_bg_end),
        glassFill = colorResource(id = R.color.glass_fill),
        glassFillSoft = colorResource(id = R.color.glass_fill_soft),
        glassStroke = colorResource(id = R.color.glass_stroke),
        accent = colorResource(id = R.color.accent_blue),
        onAccent = colorResource(id = R.color.on_accent),
        textPrimary = colorResource(id = R.color.text_primary),
        textSecondary = colorResource(id = R.color.text_secondary),
        textTertiary = colorResource(id = R.color.text_tertiary),
        tagNoteBg = colorResource(id = R.color.tag_yellow_bg),
        tagNoteText = colorResource(id = R.color.tag_yellow_text),
        tagAiBg = colorResource(id = R.color.tag_ai_bg),
        tagAiText = colorResource(id = R.color.tag_ai_text)
    )
}

@Composable
fun NoMemoBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(NoMemoPalette) -> Unit
) {
    val palette = rememberNoMemoPalette()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.memoBgStart)
    ) {
        content(palette)
    }
}

@Composable
fun ResponsiveContentFrame(
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(NoMemoAdaptiveSpec) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .widthIn(max = spec.maxContentWidth)
        ) {
            content(spec)
        }
    }
}

@Composable
fun PressScaleBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    pressedScale: Float = 0.95f,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pressScale"
    )
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        content = content
    )
}

fun Modifier.pageSwipeNavigation(
    enabled: Boolean = true,
    threshold: Float = 96f,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
): Modifier {
    if (!enabled) return this
    return this.pointerInput(onSwipeLeft, onSwipeRight, threshold) {
        var totalDrag = 0f
        detectHorizontalDragGestures(
            onHorizontalDrag = { _, dragAmount ->
                totalDrag += dragAmount
            },
            onDragEnd = {
                when {
                    totalDrag <= -threshold -> onSwipeLeft()
                    totalDrag >= threshold -> onSwipeRight()
                }
                totalDrag = 0f
            },
            onDragCancel = { totalDrag = 0f }
        )
    }
}

@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 18.dp,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
) {
    val palette = rememberNoMemoPalette()
    val bg = if (selected) palette.accent else palette.glassFill
    val textColor = if (selected) palette.onAccent else palette.textPrimary
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(bg)
                .border(1.dp, palette.glassStroke, RoundedCornerShape(999.dp))
                .padding(horizontal = horizontalPadding, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                style = textStyle
            )
        }
    }
}

@Composable
fun GlassIconCircleButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(palette.glassFill)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = palette.textPrimary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
        )
    }
}

@Composable
fun NoMemoBottomDock(
    selectedTab: NoMemoDockTab,
    onOpenMemory: () -> Unit,
    onOpenGroup: () -> Unit,
    onOpenReminder: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec()
) {
    val palette = rememberNoMemoPalette()
    val haptic = LocalHapticFeedback.current
    val haloTransition = rememberInfiniteTransition(label = "dockHaloTransition")
    val haloScale by haloTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dockHaloScale"
    )
    val haloAlpha by haloTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.36f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dockHaloAlpha"
    )

    val dockSwipeModifier = when (selectedTab) {
        NoMemoDockTab.MEMORY -> Modifier.pageSwipeNavigation(
            onSwipeLeft = onOpenGroup
        )
        NoMemoDockTab.GROUP -> Modifier.pageSwipeNavigation(
            onSwipeLeft = onOpenReminder,
            onSwipeRight = onOpenMemory
        )
        NoMemoDockTab.REMINDER -> Modifier.pageSwipeNavigation(
            onSwipeRight = onOpenGroup
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(dockSwipeModifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(spec.bottomNavHeight)
                .clip(RoundedCornerShape(42.dp))
                .background(palette.glassFill)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockNavItem(
                iconRes = android.R.drawable.ic_menu_agenda,
                text = stringResource(R.string.nav_memory),
                selected = selectedTab == NoMemoDockTab.MEMORY,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenMemory()
                },
                spec = spec
            )
            DockNavItem(
                iconRes = android.R.drawable.ic_menu_sort_by_size,
                text = stringResource(R.string.nav_group),
                selected = selectedTab == NoMemoDockTab.GROUP,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenGroup()
                },
                spec = spec
            )
            DockNavItem(
                iconRes = android.R.drawable.ic_menu_recent_history,
                text = stringResource(R.string.nav_reminder),
                selected = selectedTab == NoMemoDockTab.REMINDER,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onOpenReminder()
                },
                spec = spec
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier.size(spec.fabFrameSize),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(spec.fabFrameSize)
                    .graphicsLayer {
                        scaleX = haloScale
                        scaleY = haloScale
                        alpha = haloAlpha
                    }
                    .clip(RoundedCornerShape(39.dp))
                    .background(palette.glassFillSoft)
            )
            PressScaleBox(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddClick()
                },
                pressedScale = 0.92f,
                modifier = Modifier
                    .size(spec.fabButtonSize)
                    .clip(RoundedCornerShape(34.dp))
                    .background(palette.accent)
            ) {
                Text(
                    text = stringResource(R.string.save_record),
                    color = palette.onAccent,
                    fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun RowScope.DockNavItem(
    iconRes: Int,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    spec: NoMemoAdaptiveSpec
) {
    val palette = rememberNoMemoPalette()
    val itemColor = if (selected) palette.accent else palette.textPrimary
    val selectedBackground = if (selected) palette.glassFillSoft else Color.Transparent

    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(spec.bottomNavItemHeight)
            .clip(RoundedCornerShape(32.dp))
            .background(selectedBackground)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = itemColor,
                modifier = Modifier.size(if (spec.isNarrow) 18.dp else 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = itemColor,
                fontSize = if (spec.isNarrow) 10.sp else 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
fun RecordCard(
    record: MemoryRecord,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null
) {
    val palette = rememberNoMemoPalette()
    val adaptive = rememberNoMemoAdaptiveSpec()
    val context = LocalContext.current
    val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val modeText = if (record.mode == MemoryRecord.MODE_AI) {
        context.getString(R.string.mode_label_ai)
    } else {
        context.getString(R.string.mode_label_normal)
    }
    val timeText = context.getString(
        R.string.record_time_mode,
        timeFormat.format(Date(record.createdAt)),
        modeText
    )
    val compactCard = adaptive.widthClass == NoMemoWidthClass.COMPACT
    val showPreviewImage = !record.imageUri.isNullOrBlank() && !compactCard

    val gestureModifier = if (onLongPress == null && onClick == null) {
        Modifier
    } else {
        Modifier.pointerInput(onLongPress, onClick) {
            detectTapGestures(
                onTap = { onClick?.invoke() },
                onLongPress = { onLongPress?.invoke() }
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(gestureModifier),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = palette.glassFill),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) palette.accent else palette.glassStroke
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.memory ?: "",
                    color = palette.textPrimary,
                    fontSize = adaptive.recordTitleSize,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!record.sourceText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.record_source_prefix, record.sourceText),
                        color = palette.textSecondary,
                        fontSize = if (compactCard) 13.sp else 14.sp,
                        maxLines = if (compactCard) 1 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!record.analysis.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = stringResource(R.string.record_analysis_prefix, record.analysis),
                        color = palette.textTertiary,
                        fontSize = 12.sp,
                        maxLines = if (compactCard) 1 else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(9.dp))
                if (compactCard) {
                    Text(
                        text = timeText,
                        color = palette.textTertiary,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TinyTag(
                            text = record.categoryName ?: stringResource(R.string.tag_quick),
                            bg = palette.tagNoteBg,
                            fg = palette.tagNoteText
                        )
                        if (record.mode == MemoryRecord.MODE_AI) {
                            Spacer(modifier = Modifier.width(6.dp))
                            TinyTag(
                                text = stringResource(R.string.tag_ai),
                                bg = palette.tagAiBg,
                                fg = palette.tagAiText
                            )
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = timeText,
                            color = palette.textTertiary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TinyTag(
                            text = record.categoryName ?: stringResource(R.string.tag_quick),
                            bg = palette.tagNoteBg,
                            fg = palette.tagNoteText
                        )
                        if (record.mode == MemoryRecord.MODE_AI) {
                            Spacer(modifier = Modifier.width(6.dp))
                            TinyTag(
                                text = stringResource(R.string.tag_ai),
                                bg = palette.tagAiBg,
                                fg = palette.tagAiText
                            )
                        }
                    }
                }
            }
            if (showPreviewImage) {
                Spacer(modifier = Modifier.width(12.dp))
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            layoutParams = android.widget.FrameLayout.LayoutParams(
                                (adaptive.recordImageWidth.value * ctx.resources.displayMetrics.density).toInt(),
                                (adaptive.recordImageHeight.value * ctx.resources.displayMetrics.density).toInt()
                            )
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            clipToOutline = true
                        }
                    },
                    modifier = Modifier
                        .size(width = adaptive.recordImageWidth, height = adaptive.recordImageHeight)
                        .clip(RoundedCornerShape(22.dp))
                        .background(palette.glassFillSoft),
                    update = { iv ->
                        try {
                            iv.setImageURI(Uri.parse(record.imageUri))
                        } catch (_: Exception) {
                            iv.setImageDrawable(null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TinyTag(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GlassPanelText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = rememberNoMemoPalette().textSecondary,
    fontSizeSp: Int = 13
) {
    val palette = rememberNoMemoPalette()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.glassFill)
            .padding(12.dp)
    ) {
        Text(text = text, color = color, fontSize = fontSizeSp.sp)
    }
}
