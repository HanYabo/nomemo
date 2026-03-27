package com.han.nomemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.draw.shadow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private object MemoryThumbnailCache {
    private val cache = object : LruCache<String, Bitmap>(24 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    fun get(key: String): Bitmap? = cache.get(key)

    fun put(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }
}

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

private val LocalNoMemoPalette = staticCompositionLocalOf<NoMemoPalette?> { null }
private val LocalNoMemoAdaptiveSpec = staticCompositionLocalOf<NoMemoAdaptiveSpec?> { null }

@Composable
fun rememberNoMemoAdaptiveSpec(): NoMemoAdaptiveSpec {
    LocalNoMemoAdaptiveSpec.current?.let { return it }
    return rememberNoMemoAdaptiveSpecValue()
}

@Composable
private fun rememberNoMemoAdaptiveSpecValue(): NoMemoAdaptiveSpec {
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
    LocalNoMemoPalette.current?.let { return it }
    return rememberNoMemoPaletteValue()
}

@Composable
private fun rememberNoMemoPaletteValue(): NoMemoPalette {
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
    val palette = rememberNoMemoPaletteValue()
    CompositionLocalProvider(LocalNoMemoPalette provides palette) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(palette.memoBgStart)
        ) {
            content(palette)
        }
    }
}

@Composable
fun ResponsiveContentFrame(
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(NoMemoAdaptiveSpec) -> Unit
) {
    CompositionLocalProvider(LocalNoMemoAdaptiveSpec provides spec) {
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
        ) {
            Text(
                text = text,
                color = textColor,
                style = textStyle,
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 10.dp)
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
}

@Composable
fun NoMemoTopActionButtons(
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 0.dp)
            .offset(y = (-4).dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconCircleButton(
            iconRes = R.drawable.ic_nm_search,
            contentDescription = stringResource(R.string.action_search),
            onClick = onSearchClick,
            modifier = Modifier.padding(end = 10.dp),
            size = spec.topActionButtonSize
        )
        GlassIconCircleButton(
            iconRes = R.drawable.ic_nm_more,
            contentDescription = stringResource(R.string.action_more),
            onClick = onMoreClick,
            size = spec.topActionButtonSize
        )
    }
}

@Composable
fun NoMemoSearchBarCard(
    value: String,
    onValueChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = rememberNoMemoPalette()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = palette.glassFill),
        border = BorderStroke(1.dp, palette.glassStroke)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nm_search),
                contentDescription = stringResource(R.string.action_search),
                tint = palette.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = palette.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(min = 0.dp)
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            color = palette.textTertiary,
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            }
            TextButton(onClick = onClose) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    }
}

@Composable
fun NoMemoMoreMenuPanel(
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null,
    onSelectAll: (() -> Unit)? = null
) {
    val palette = rememberNoMemoPalette()
    val menuSurface = if (isSystemInDarkTheme()) {
        Color(0xFF171B22).copy(alpha = 0.95f)
    } else {
        Color(0xFFFBFBFC).copy(alpha = 0.94f)
    }
    Card(
        modifier = modifier
            .width(176.dp)
            .shadow(14.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = menuSurface),
        border = BorderStroke(1.dp, palette.glassStroke)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            if (onSelectAll != null) {
                NoMemoMoreMenuActionRow(
                    iconRes = R.drawable.ic_sheet_check,
                    label = stringResource(R.string.action_select_all),
                    onClick = onSelectAll
                )
            }
            if (onOpenSettings != null) {
                NoMemoMoreMenuActionRow(
                    iconRes = R.drawable.ic_nm_settings,
                    label = stringResource(R.string.action_settings),
                    onClick = onOpenSettings,
                    modifier = Modifier.padding(top = if (onSelectAll != null) 6.dp else 0.dp)
                )
            }
        }
    }
}

@Composable
private fun NoMemoMoreMenuActionRow(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
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

@Composable
fun NoMemoBottomDock(
    selectedTab: NoMemoDockTab,
    onOpenMemory: () -> Unit,
    onOpenGroup: () -> Unit,
    onOpenReminder: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    animateFabHalo: Boolean = true,
    showEnhancedOutline: Boolean = false
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val dockShape = RoundedCornerShape(42.dp)
    val dockContainerBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF171B22).copy(alpha = 0.94f),
                Color(0xFF1C2129).copy(alpha = 0.90f),
                Color(0xFF11151B).copy(alpha = 0.92f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.90f),
                Color(0xFFFAFBFD).copy(alpha = 0.87f),
                Color(0xFFF2F5F8).copy(alpha = 0.88f)
            )
        )
    }
    val dockBlurTint = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.48f)
    }
    val dockHighlight = if (isDark) {
        Color.White.copy(alpha = 0.24f)
    } else {
        Color.White.copy(alpha = 0.74f)
    }
    val dockTopHighlightBrush = Brush.verticalGradient(
        listOf(
            if (isDark) Color.White.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.56f),
            if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.20f),
            Color.Transparent
        )
    )
    val dockShadow = if (isDark) {
        Color.Black.copy(alpha = 0.34f)
    } else {
        Color.Black.copy(alpha = 0.10f)
    }
    val selectedItemBackground = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF2A3442).copy(alpha = 0.96f),
                Color(0xFF222C39).copy(alpha = 0.94f),
                Color(0xFF1C2430).copy(alpha = 0.92f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFE7EEF8).copy(alpha = 0.98f),
                Color(0xFFDCE7F4).copy(alpha = 0.97f),
                Color(0xFFD1DEEF).copy(alpha = 0.96f)
            )
        )
    }
    val selectedItemStroke = if (isDark) {
        Color(0xFF8EA4C0).copy(alpha = 0.32f)
    } else {
        Color(0xFF90A3BB).copy(alpha = 0.42f)
    }
    val selectedItemTopHighlight = if (isDark) {
        Color.White.copy(alpha = 0.16f)
    } else {
        Color.White.copy(alpha = 0.42f)
    }
    val selectedItemContentColor = if (isDark) {
        Color(0xFFF2F6FB)
    } else {
        Color(0xFF2D4258)
    }
    val enhancedOutlineColor = if (showEnhancedOutline) {
        if (isDark) {
            Color.White.copy(alpha = 0.18f)
        } else {
            Color(0xFF7E90A7).copy(alpha = 0.28f)
        }
    } else {
        Color.Transparent
    }
    val enhancedInnerOutlineColor = if (showEnhancedOutline) {
        if (isDark) {
            Color.White.copy(alpha = 0.08f)
        } else {
            Color.White.copy(alpha = 0.52f)
        }
    } else {
        Color.Transparent
    }
    val enhancedTopRimBrush = if (showEnhancedOutline) {
        Brush.verticalGradient(
            listOf(
                if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.72f),
                if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.24f),
                Color.Transparent
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(Color.Transparent, Color.Transparent)
        )
    }
    val fabFrameShape = RoundedCornerShape(39.dp)
    val fabFrameBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.06f),
                Color.White.copy(alpha = 0.08f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.60f),
                Color.White.copy(alpha = 0.42f),
                Color.White.copy(alpha = 0.52f)
            )
        )
    }
    val addButtonSize = spec.fabButtonSize + 4.dp
    val addButtonShape = RoundedCornerShape(34.dp)
    val addButtonBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF7F9FC),
                Color(0xFFE7EBF1),
                Color(0xFFD7DDE6)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF33404F),
                Color(0xFF242F3B),
                Color(0xFF171E27)
            )
        )
    }
    val addButtonStroke = if (isDark) {
        Color.White.copy(alpha = 0.34f)
    } else {
        Color.White.copy(alpha = 0.16f)
    }
    val addButtonTopHighlightBrush = Brush.verticalGradient(
        listOf(
            if (isDark) Color.White.copy(alpha = 0.34f) else Color.White.copy(alpha = 0.18f),
            if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.06f),
            Color.Transparent
        )
    )
    val addButtonInnerStroke = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.08f)
    }
    val addButtonShadow = if (isDark) {
        Color.Black.copy(alpha = 0.30f)
    } else {
        Color(0xFF0E1620).copy(alpha = 0.26f)
    }
    val addButtonIconTint = if (isDark) {
        Color(0xFF121821)
    } else {
        Color.White
    }
    val haptic = LocalHapticFeedback.current
    val haloScale: Float
    val haloAlpha: Float
    if (animateFabHalo) {
        val haloTransition = rememberInfiniteTransition(label = "dockHaloTransition")
        haloScale = haloTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dockHaloScale"
        ).value
        haloAlpha = haloTransition.animateFloat(
            initialValue = 0.18f,
            targetValue = 0.36f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dockHaloAlpha"
        ).value
    } else {
        haloScale = 1f
        haloAlpha = 0.18f
    }

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
        Box(
            modifier = Modifier
                .weight(1f)
                .height(spec.bottomNavHeight)
                .shadow(
                    elevation = if (spec.isNarrow) 10.dp else 14.dp,
                    shape = dockShape,
                    ambientColor = dockShadow,
                    spotColor = dockShadow
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 28.dp else 18.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .clip(dockShape)
                    .background(dockBlurTint)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(dockShape)
                    .background(dockContainerBrush)
                    .border(1.dp, dockHighlight, dockShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(dockShape)
                    .background(dockTopHighlightBrush)
            )
            if (showEnhancedOutline) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .border(1.dp, enhancedOutlineColor, dockShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .clip(RoundedCornerShape(41.dp))
                        .border(0.75.dp, enhancedInnerOutlineColor, RoundedCornerShape(41.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .background(enhancedTopRimBrush)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockNavItem(
                    iconRes = R.drawable.ic_nm_memory,
                    text = stringResource(R.string.nav_memory),
                    selected = selectedTab == NoMemoDockTab.MEMORY,
                    selectedBackground = selectedItemBackground,
                    selectedStroke = selectedItemStroke,
                    selectedTopHighlight = selectedItemTopHighlight,
                    selectedContentColor = selectedItemContentColor,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenMemory()
                    },
                    spec = spec
                )
                DockNavItem(
                    iconRes = R.drawable.ic_nm_group,
                    text = stringResource(R.string.nav_group),
                    selected = selectedTab == NoMemoDockTab.GROUP,
                    selectedBackground = selectedItemBackground,
                    selectedStroke = selectedItemStroke,
                    selectedTopHighlight = selectedItemTopHighlight,
                    selectedContentColor = selectedItemContentColor,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenGroup()
                    },
                    spec = spec
                )
                DockNavItem(
                    iconRes = R.drawable.ic_nm_reminder,
                    text = stringResource(R.string.nav_reminder),
                    selected = selectedTab == NoMemoDockTab.REMINDER,
                    selectedBackground = selectedItemBackground,
                    selectedStroke = selectedItemStroke,
                    selectedTopHighlight = selectedItemTopHighlight,
                    selectedContentColor = selectedItemContentColor,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenReminder()
                    },
                    spec = spec
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(spec.fabFrameSize)
                .shadow(
                    elevation = if (spec.isNarrow) 10.dp else 12.dp,
                    shape = fabFrameShape,
                    ambientColor = dockShadow,
                    spotColor = dockShadow
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(spec.fabFrameSize)
                    .blur(
                        radius = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 24.dp else 16.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .clip(fabFrameShape)
                    .background(dockBlurTint)
            )
            Box(
                modifier = Modifier
                    .size(spec.fabFrameSize)
                    .graphicsLayer {
                        scaleX = haloScale
                        scaleY = haloScale
                        alpha = haloAlpha
                    }
                    .clip(fabFrameShape)
                    .background(fabFrameBrush)
                    .border(1.dp, dockHighlight, fabFrameShape)
            )
            PressScaleBox(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddClick()
                },
                pressedScale = 0.92f,
                modifier = Modifier
                    .size(addButtonSize)
                    .shadow(
                        elevation = if (spec.isNarrow) 8.dp else 10.dp,
                        shape = addButtonShape,
                        ambientColor = addButtonShadow,
                        spotColor = addButtonShadow
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(addButtonShape)
                        .background(addButtonBrush)
                        .border(1.dp, addButtonStroke, addButtonShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .clip(RoundedCornerShape(33.dp))
                        .border(0.75.dp, addButtonInnerStroke, RoundedCornerShape(33.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(addButtonShape)
                        .background(addButtonTopHighlightBrush)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_nm_add),
                    contentDescription = stringResource(R.string.save_record_desc),
                    tint = addButtonIconTint,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(if (spec.isNarrow) 22.dp else 24.dp)
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
    selectedBackground: Brush,
    selectedStroke: Color,
    selectedTopHighlight: Color,
    selectedContentColor: Color,
    onClick: () -> Unit,
    spec: NoMemoAdaptiveSpec
) {
    val palette = rememberNoMemoPalette()
    val itemShape = RoundedCornerShape(32.dp)
    val itemColor = if (selected) selectedContentColor else palette.textPrimary

    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .height(spec.bottomNavItemHeight)
            .clip(itemShape)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(selectedBackground)
                    .border(1.dp, selectedStroke, itemShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(itemShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                selectedTopHighlight,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = itemColor,
                modifier = Modifier.size(if (spec.isNarrow) 20.dp else 22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = itemColor,
                fontSize = if (spec.isNarrow) 11.sp else 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

private fun loadSampledBitmap(context: android.content.Context, uri: Uri, widthPx: Int, heightPx: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    } ?: return null

    val targetWidth = widthPx.coerceAtLeast(1)
    val targetHeight = heightPx.coerceAtLeast(1)
    var sampleSize = 1
    var sourceWidth = bounds.outWidth
    var sourceHeight = bounds.outHeight
    while (sourceWidth / 2 >= targetWidth && sourceHeight / 2 >= targetHeight) {
        sourceWidth /= 2
        sourceHeight /= 2
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    }
}

private fun loadMemoryThumbnail(
    context: android.content.Context,
    uriString: String,
    widthPx: Int,
    heightPx: Int
): Bitmap? {
    if (uriString.isBlank()) return null
    val key = "$uriString@$widthPx x $heightPx"
    MemoryThumbnailCache.get(key)?.let { return it }
    val uri = Uri.parse(uriString)
    val bitmap = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.loadThumbnail(uri, Size(widthPx.coerceAtLeast(1), heightPx.coerceAtLeast(1)), null)
        } else {
            loadSampledBitmap(context, uri, widthPx, heightPx)
        }
    } catch (_: Exception) {
        loadSampledBitmap(context, uri, widthPx, heightPx)
    }
    if (bitmap != null) {
        MemoryThumbnailCache.put(key, bitmap)
    }
    return bitmap
}

@Composable
private fun MemoryThumbnail(
    uriString: String,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    cornerRadius: Dp
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val widthPx = with(density) { width.roundToPx().coerceAtLeast(1) }
    val heightPx = with(density) { height.roundToPx().coerceAtLeast(1) }
    val thumbnailState = produceState<Bitmap?>(initialValue = null, uriString, widthPx, heightPx) {
        if (uriString.isBlank()) {
            value = null
            return@produceState
        }
        val cacheKey = "$uriString@$widthPx x $heightPx"
        val cached = MemoryThumbnailCache.get(cacheKey)
        if (cached != null) {
            value = cached
            return@produceState
        }
        value = withContext(Dispatchers.IO) {
            loadMemoryThumbnail(context.applicationContext, uriString, widthPx, heightPx)
        }
    }
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        val bitmap = thumbnailState.value
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
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
    onLongPress: (() -> Unit)? = null,
    palette: NoMemoPalette = rememberNoMemoPalette(),
    adaptive: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    allowImageLoading: Boolean = true
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()
    val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val titleText = remember(record.recordId, record.title, record.memory) {
        record.title?.takeIf { it.isNotBlank() } ?: record.memory.orEmpty()
    }
    val summaryText = remember(record.recordId, record.summary, record.memory, record.sourceText, titleText) {
        when {
            !record.summary.isNullOrBlank() -> record.summary
            !record.memory.isNullOrBlank() && record.memory != titleText -> record.memory
            !record.sourceText.isNullOrBlank() -> record.sourceText
            else -> ""
        }
    }
    val modeText = remember(record.mode, context) {
        if (record.mode == MemoryRecord.MODE_AI) {
            context.getString(R.string.mode_label_ai)
        } else {
            context.getString(R.string.mode_label_normal)
        }
    }
    val categoryText = remember(record.categoryName, context) {
        record.categoryName ?: context.getString(R.string.tag_quick)
    }
    val timeText = remember(record.createdAt, modeText, context) {
        context.getString(
            R.string.record_time_mode,
            timeFormat.format(Date(record.createdAt)),
            modeText
        )
    }
    val compactCard = adaptive.widthClass == NoMemoWidthClass.COMPACT
    val showPreviewImage = !record.imageUri.isNullOrBlank()
    val previewWidth = when (adaptive.widthClass) {
        NoMemoWidthClass.EXPANDED -> 102.dp
        NoMemoWidthClass.MEDIUM -> 94.dp
        NoMemoWidthClass.COMPACT -> if (adaptive.isNarrow) 72.dp else 82.dp
    }
    val previewHeight = when (adaptive.widthClass) {
        NoMemoWidthClass.EXPANDED -> 132.dp
        NoMemoWidthClass.MEDIUM -> 122.dp
        NoMemoWidthClass.COMPACT -> if (adaptive.isNarrow) 94.dp else 108.dp
    }
    val previewCornerRadius = if (adaptive.isNarrow) 15.dp else 17.dp
    val cardShape = RoundedCornerShape(if (adaptive.isNarrow) 28.dp else 30.dp)
    val cardGradient = if (isDark) {
        listOf(
            Color(0xFF171A20).copy(alpha = 0.98f),
            Color(0xFF14171C).copy(alpha = 0.98f)
        )
    } else {
        listOf(
            Color.White.copy(alpha = 0.995f),
            Color(0xFFFCFCFD).copy(alpha = 0.995f)
        )
    }
    val cardBorderColor = if (selected) {
        if (isDark) Color.White.copy(alpha = 0.14f) else Color(0x26111111)
    } else {
        Color.Transparent
    }
    val cardShadow = if (isDark) 0.dp else if (selected) 7.dp else 4.dp
    val summaryColor = if (isDark) {
        palette.textSecondary.copy(alpha = 0.88f)
    } else {
        Color(0xFF697281)
    }
    val metaColor = if (isDark) {
        Color.White.copy(alpha = 0.46f)
    } else {
        Color(0xFF98A1AE)
    }
    val aiMetaColor = if (isDark) {
        Color.White.copy(alpha = 0.72f)
    } else {
        Color(0xFF6E7B92)
    }
    val thumbnailBackground = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F4F8)
    val thumbnailBorder = if (isDark) Color.Transparent else Color.Transparent

    val gestureModifier = if (onLongPress == null && onClick == null) {
        Modifier
    } else {
        Modifier.pointerInput(onLongPress, onClick) {
            detectTapGestures(
                onTap = { onClick?.invoke() },
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress?.invoke()
                }
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(gestureModifier),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = cardShadow),
        border = if (selected) BorderStroke(1.dp, cardBorderColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(cardGradient))
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleText,
                    color = palette.textPrimary,
                    fontSize = adaptive.recordTitleSize,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = if (compactCard) 2 else 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (!summaryText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = summaryText,
                        color = summaryColor,
                        fontSize = if (compactCard) 13.sp else 14.sp,
                        maxLines = if (compactCard) 2 else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                RecordMetaLine(
                    timeText = timeText,
                    categoryText = categoryText,
                    showAi = record.mode == MemoryRecord.MODE_AI,
                    metaColor = metaColor,
                    aiColor = aiMetaColor
                )
            }

            if (showPreviewImage) {
                Spacer(modifier = Modifier.width(14.dp))
                if (allowImageLoading) {
                    MemoryThumbnail(
                        uriString = record.imageUri.orEmpty(),
                        width = previewWidth,
                        height = previewHeight,
                        backgroundColor = thumbnailBackground,
                        cornerRadius = previewCornerRadius,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = thumbnailBorder,
                                shape = RoundedCornerShape(previewCornerRadius)
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(width = previewWidth, height = previewHeight)
                            .clip(RoundedCornerShape(previewCornerRadius))
                            .background(thumbnailBackground)
                            .border(
                                width = 1.dp,
                                color = thumbnailBorder,
                                shape = RoundedCornerShape(previewCornerRadius)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun rememberDockHasUnderContent(
    listState: LazyListState,
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    extraThreshold: Dp = 18.dp
): Boolean {
    val density = LocalDensity.current
    val overlapThresholdPx = with(density) { (spec.bottomNavHeight + extraThreshold).roundToPx() }
    return remember(listState, overlapThresholdPx) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) {
                false
            } else {
                val cutoff = layoutInfo.viewportEndOffset - overlapThresholdPx
                visibleItems.any { visibleItem ->
                    visibleItem.offset + visibleItem.size > cutoff
                }
            }
        }
    }.value
}

@Composable
private fun RecordMetaLine(
    timeText: String,
    categoryText: String,
    showAi: Boolean,
    metaColor: Color,
    aiColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = timeText,
            color = metaColor,
            fontSize = 12.sp
        )
        MetaDividerDot(color = metaColor)
        Text(
            text = categoryText,
            color = metaColor,
            fontSize = 12.sp
        )
        if (showAi) {
            MetaDividerDot(color = metaColor)
            Text(
                text = "AI",
                color = aiColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MetaDividerDot(color: Color) {
    Spacer(modifier = Modifier.width(7.dp))
    Box(
        modifier = Modifier
            .size(3.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.72f))
    )
    Spacer(modifier = Modifier.width(7.dp))
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

@Composable
fun NoMemoEmptyState(
    iconRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val palette = rememberNoMemoPalette()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = palette.textTertiary.copy(alpha = 0.62f),
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            color = palette.textSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = palette.textTertiary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )
        }
    }
}
