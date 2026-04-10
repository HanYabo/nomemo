package com.han.nomemo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PathMeasure
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.util.LruCache
import android.util.Size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.draw.shadow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private val DockEaseOut = androidx.compose.animation.core.CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

object AiProcessingStateRegistry {
    private val processingState = mutableStateMapOf<String, Boolean>()

    fun markProcessing(recordId: String) {
        if (recordId.isNotBlank()) {
            processingState[recordId] = true
        }
    }

    fun clearProcessing(recordId: String) {
        if (recordId.isNotBlank()) {
            processingState.remove(recordId)
        }
    }

    fun isProcessing(recordId: String): Boolean {
        return processingState[recordId] == true
    }
}

private object MemoryThumbnailCache {
    private val exactCache = object : LruCache<String, Bitmap>(24 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }
    private val uriCache = object : LruCache<String, Bitmap>(24 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    fun getExact(key: String): Bitmap? = exactCache.get(key)

    fun getByUri(uri: String): Bitmap? = uriCache.get(uri)

    fun put(key: String, uri: String, bitmap: Bitmap) {
        exactCache.put(key, bitmap)
        if (uri.isNotBlank()) {
            uriCache.put(uri, bitmap)
        }
    }
}

private fun memoryThumbnailCacheKey(
    uriString: String,
    widthPx: Int,
    heightPx: Int
): String {
    return "$uriString@${widthPx}x${heightPx}"
}

private const val MemoryThumbnailPrewarmWidthPx = 240
private const val MemoryThumbnailPrewarmHeightPx = 320
private const val MemoryThumbnailPrewarmLimit = 8

internal suspend fun prewarmMemoryThumbnailCache(
    context: android.content.Context,
    records: List<MemoryRecord>,
    widthPx: Int = MemoryThumbnailPrewarmWidthPx,
    heightPx: Int = MemoryThumbnailPrewarmHeightPx,
    limit: Int = MemoryThumbnailPrewarmLimit
) {
    if (records.isEmpty() || limit <= 0) {
        return
    }
    val appContext = context.applicationContext
    records.asSequence()
        .mapNotNull { it.imageUri?.trim()?.takeIf(String::isNotEmpty) }
        .distinct()
        .take(limit)
        .forEach { uriString ->
            loadMemoryThumbnail(appContext, uriString, widthPx, heightPx)
        }
}

data class NoMemoPalette(
    val memoBgStart: Color,
    val memoBgMid: Color,
    val memoBgEnd: Color,
    val glassFill: Color,
    val glassFillSoft: Color,
    val glassStroke: Color,
    val dockSurface: Color,
    val dockStroke: Color,
    val dockIndicator: Color,
    val dockGlow: Color,
    val dockFabSurface: Color,
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

fun noMemoCardSurfaceColor(isDark: Boolean, lightColor: Color = Color.White): Color {
    return if (isDark) Color(0xFF1A1A1C) else lightColor
}

fun noMemoSelectedCardGradient(isDark: Boolean): List<Color> {
    return if (isDark) {
        listOf(
            Color(0xFF253754),
            Color(0xFF1C2D46)
        )
    } else {
        listOf(
            Color(0xFFE7F0FF),
            Color(0xFFDDE9FF)
        )
    }
}

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
    val context = LocalContext.current.applicationContext
    val prefs = remember(context) {
        context.getSharedPreferences(SettingsStore.PREF_NAME, Context.MODE_PRIVATE)
    }
    var settingsVersion by remember { mutableStateOf(0) }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            settingsVersion += 1
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    val settingsStore = remember(context) { SettingsStore(context) }
    val isDark = isSystemInDarkTheme()
    settingsVersion

    val basePalette = NoMemoPalette(
        memoBgStart = colorResource(id = R.color.memo_bg_start),
        memoBgMid = colorResource(id = R.color.memo_bg_mid),
        memoBgEnd = colorResource(id = R.color.memo_bg_end),
        glassFill = colorResource(id = R.color.glass_fill),
        glassFillSoft = colorResource(id = R.color.glass_fill_soft),
        glassStroke = colorResource(id = R.color.glass_stroke),
        dockSurface = colorResource(id = R.color.dock_surface),
        dockStroke = colorResource(id = R.color.dock_stroke),
        dockIndicator = colorResource(id = R.color.dock_indicator),
        dockGlow = colorResource(id = R.color.dock_glow),
        dockFabSurface = colorResource(id = R.color.dock_fab_surface),
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
    return applyNoMemoThemeOverrides(
        base = basePalette,
        isDark = isDark,
        themeGlobalEnabled = settingsStore.themeGlobalEnabled,
        themeAccentKey = settingsStore.themeAccent,
        showDividers = settingsStore.showDividers
    )
}

private fun applyNoMemoThemeOverrides(
    base: NoMemoPalette,
    isDark: Boolean,
    themeGlobalEnabled: Boolean,
    themeAccentKey: String,
    showDividers: Boolean
): NoMemoPalette {
    val themeBackgroundSeed = if (themeGlobalEnabled) {
        resolveNoMemoThemeAccent(themeAccentKey, isDark, base.dockIndicator)
    } else {
        base.dockIndicator
    }
    val liftedBackgroundSeed = if (themeGlobalEnabled) {
        if (isDark) lerp(themeBackgroundSeed, Color.White, 0.42f) else lerp(themeBackgroundSeed, Color.White, 0.62f)
    } else {
        themeBackgroundSeed
    }
    val themedBgStart = if (themeGlobalEnabled) {
        lerp(base.memoBgStart, liftedBackgroundSeed, if (isDark) 0.080f else 0.140f)
    } else {
        base.memoBgStart
    }
    val themedBgMid = if (themeGlobalEnabled) {
        lerp(base.memoBgMid, liftedBackgroundSeed, if (isDark) 0.110f else 0.180f)
    } else {
        base.memoBgMid
    }
    val themedBgEnd = if (themeGlobalEnabled) {
        lerp(base.memoBgEnd, liftedBackgroundSeed, if (isDark) 0.140f else 0.220f)
    } else {
        base.memoBgEnd
    }
    val effectiveStroke = if (showDividers) base.glassStroke else Color.Transparent
    val effectiveDockStroke = if (showDividers) base.dockStroke else Color.Transparent

    return base.copy(
        memoBgStart = themedBgStart,
        memoBgMid = themedBgMid,
        memoBgEnd = themedBgEnd,
        glassStroke = effectiveStroke,
        dockStroke = effectiveDockStroke,
        glassFill = base.glassFill,
        glassFillSoft = base.glassFillSoft,
        dockSurface = base.dockSurface,
        dockIndicator = base.dockIndicator,
        dockGlow = base.dockGlow,
        dockFabSurface = base.dockFabSurface,
        accent = base.accent,
        onAccent = base.onAccent,
        tagAiBg = base.tagAiBg,
        tagAiText = base.tagAiText
    )
}

private fun resolveNoMemoThemeAccent(key: String, isDark: Boolean, fallback: Color): Color {
    return when (key) {
        SettingsStore.THEME_ACCENT_WARM_GRAY -> if (isDark) Color(0xFFD4CCBE) else Color(0xFFB8AF9D)
        SettingsStore.THEME_ACCENT_NOTE_YELLOW -> if (isDark) Color(0xFFFFD54F) else Color(0xFFFFC83D)
        SettingsStore.THEME_ACCENT_SAKURA_PINK -> if (isDark) Color(0xFFFF7FA6) else Color(0xFFFF6B96)
        SettingsStore.THEME_ACCENT_SKY_BLUE -> if (isDark) Color(0xFF64B6FF) else Color(0xFF4AA3FF)
        SettingsStore.THEME_ACCENT_MINT_GREEN -> if (isDark) Color(0xFF58E59A) else Color(0xFF39D98A)
        SettingsStore.THEME_ACCENT_PEACH_ORANGE -> if (isDark) Color(0xFFFFB35A) else Color(0xFFFF9A3D)
        SettingsStore.THEME_ACCENT_LAVENDER_PURPLE -> if (isDark) Color(0xFFC39CFF) else Color(0xFFB587FF)
        else -> fallback
    }
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
    interactionSource: MutableInteractionSource? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
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
    verticalPadding: Dp = 10.dp,
    showBorder: Boolean = true,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val bg = if (selected) palette.accent else if (isDark) palette.glassFill else Color.White
    val textColor = if (selected) palette.onAccent else palette.textPrimary
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(bg)
                .border(
                    width = if (showBorder) 1.dp else 0.dp,
                    color = if (showBorder) palette.glassStroke else Color.Transparent,
                    shape = RoundedCornerShape(999.dp)
                )
        ) {
            Text(
                text = text,
                color = textColor,
                style = textStyle,
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding)
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
    size: Dp = 56.dp,
    onBoundsChanged: ((IntRect) -> Unit)? = null
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInWindow()
                onBoundsChanged?.invoke(
                    IntRect(
                        left = bounds.left.roundToInt(),
                        top = bounds.top.roundToInt(),
                        right = bounds.right.roundToInt(),
                        bottom = bounds.bottom.roundToInt()
                    )
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(if (isDark) palette.glassFill else Color.White)
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
    onMoreClick: () -> Unit,
    onMoreButtonBoundsChanged: ((IntRect) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(6f),
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
            size = spec.topActionButtonSize,
            onBoundsChanged = onMoreButtonBoundsChanged
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
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = noMemoCardSurfaceColor(isDark, Color.White.copy(alpha = 0.995f))),
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
fun NoMemoConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    destructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    NoMemoDialogShell(
        title = title,
        message = message,
        onDismissRequest = onDismiss,
        actions = listOf(
            NoMemoDialogActionSpec(
                text = dismissText,
                primary = false,
                onClick = onDismiss
            ),
            NoMemoDialogActionSpec(
                text = confirmText,
                primary = true,
                destructive = destructive,
                onClick = onConfirm
            )
        )
    )
}

@Composable
fun NoMemoDeleteConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    NoMemoConfirmDialog(
        title = title,
        message = message,
        confirmText = "删除",
        dismissText = "取消",
        destructive = true,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun NoMemoMessageDialog(
    title: String,
    message: String,
    confirmText: String = "知道了",
    onDismiss: () -> Unit
) {
    NoMemoDialogShell(
        title = title,
        message = message,
        onDismissRequest = onDismiss,
        actions = listOf(
            NoMemoDialogActionSpec(
                text = confirmText,
                primary = true,
                onClick = onDismiss
            )
        )
    )
}

@Composable
fun NoMemoTernaryConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    tertiaryText: String,
    destructiveTertiary: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onTertiary: () -> Unit
) {
    NoMemoDialogShell(
        title = title,
        message = message,
        onDismissRequest = onDismiss,
        actions = listOf(
            NoMemoDialogActionSpec(
                text = dismissText,
                primary = false,
                onClick = onDismiss
            ),
            NoMemoDialogActionSpec(
                text = tertiaryText,
                primary = false,
                destructive = destructiveTertiary,
                onClick = onTertiary
            ),
            NoMemoDialogActionSpec(
                text = confirmText,
                primary = true,
                onClick = onConfirm
            )
        )
    )
}

private data class NoMemoDialogActionSpec(
    val text: String,
    val primary: Boolean,
    val destructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
private fun NoMemoDialogShell(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    actions: List<NoMemoDialogActionSpec>
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val panelShape = RoundedCornerShape(30.dp)
    val panelSurface = if (isDark) {
        noMemoCardSurfaceColor(true, Color(0xFF171A20))
    } else {
        noMemoCardSurfaceColor(false, Color.White.copy(alpha = 0.998f))
    }
    val panelStroke = if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.Black.copy(alpha = 0.06f)
    }
    val scrimColor = Color.Black.copy(alpha = if (isDark) 0.46f else 0.28f)
    val outsideInteraction = remember { MutableInteractionSource() }
    val panelInteraction = remember { MutableInteractionSource() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = outsideInteraction,
                        indication = null,
                        onClick = onDismissRequest
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .widthIn(max = 368.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = panelShape,
                        ambientColor = Color.Black.copy(alpha = if (isDark) 0.34f else 0.12f),
                        spotColor = Color.Black.copy(alpha = if (isDark) 0.34f else 0.12f)
                    )
                    .clip(panelShape)
                    .background(panelSurface)
                    .border(1.dp, panelStroke, panelShape)
                    .clickable(
                        interactionSource = panelInteraction,
                        indication = null,
                        onClick = {}
                    )
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = title,
                        color = palette.textPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = message,
                        color = palette.textSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        actions.forEachIndexed { index, action ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            NoMemoDialogActionButton(
                                text = action.text,
                                primary = action.primary,
                                destructive = action.destructive,
                                onClick = action.onClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoMemoDialogActionButton(
    text: String,
    primary: Boolean,
    destructive: Boolean = false,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val destructiveBase = Color(0xFFFF5A52)
    val backgroundColor = when {
        primary && destructive -> destructiveBase
        primary -> palette.accent
        destructive -> if (isDark) Color(0xFF2D1C1B) else Color(0xFFFFF1F0)
        else -> if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.035f)
    }
    val borderColor = when {
        primary && destructive -> destructiveBase
        primary -> palette.accent
        destructive -> destructiveBase.copy(alpha = if (isDark) 0.42f else 0.32f)
        else -> if (isDark) palette.glassStroke.copy(alpha = 0.34f) else Color.Black.copy(alpha = 0.06f)
    }
    val textColor = when {
        primary -> palette.onAccent
        destructive -> destructiveBase
        else -> palette.textPrimary
    }
    PressScaleBox(
        onClick = onClick,
        modifier = Modifier
            .widthIn(min = 84.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private class NoMemoAnchoredMenuPositionProvider(
    private val anchorBounds: IntRect,
    private val verticalGapPx: Int,
    private val horizontalInsetPx: Int,
    private val topInsetPx: Int,
    private val bottomInsetPx: Int
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val maxX = (windowSize.width - horizontalInsetPx - popupContentSize.width)
            .coerceAtLeast(horizontalInsetPx)
        val x = (this.anchorBounds.right - popupContentSize.width)
            .coerceIn(horizontalInsetPx, maxX)

        val preferredY = this.anchorBounds.bottom + verticalGapPx
        val maxY = (windowSize.height - bottomInsetPx - popupContentSize.height)
            .coerceAtLeast(topInsetPx)
        val y = if (preferredY <= maxY) preferredY else maxY

        return IntOffset(x, y)
    }
}

@Composable
private fun NoMemoAnchoredMenuAnimatedLayer(
    progress: Float,
    menuWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .width(menuWidth)
            .zIndex(20f)
            .graphicsLayer {
                alpha = progress
                scaleX = 0.94f + (0.06f * progress)
                scaleY = 0.94f + (0.06f * progress)
                translationY = with(density) { (-6).dp.toPx() * (1f - progress) }
                transformOrigin = TransformOrigin(1f, 0f)
                clip = false
            }
    ) {
        content()
    }
}

@Composable
fun NoMemoMenuList(
    actions: List<NoMemoMenuActionItem>,
    modifier: Modifier = Modifier
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val panelShape = RoundedCornerShape(24.dp)
    val panelBase = if (isDark) {
        noMemoCardSurfaceColor(true, Color(0xFF171A20))
    } else {
        Color.White
    }
    val panelStroke = if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color(0x14000000)
    }
    val panelShadow = if (isDark) {
        Color.Black.copy(alpha = 0.18f)
    } else {
        Color(0xFF2A3442).copy(alpha = 0.045f)
    }
    val topSheen = if (isDark) {
        Color.White.copy(alpha = 0.035f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }
    val contentInset = 6.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 10.dp else 12.dp,
                shape = panelShape,
                ambientColor = panelShadow,
                spotColor = panelShadow
            )
            .clip(panelShape)
            .background(panelBase)
            .border(1.dp, panelStroke, panelShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(topSheen)
                .alpha(0.28f)
        ) {
        }
        Column(
            modifier = Modifier.padding(horizontal = contentInset, vertical = contentInset),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            actions.forEach { item ->
                NoMemoAnchoredMenuRow(
                    iconRes = item.iconRes,
                    label = item.label,
                    destructive = item.destructive,
                    onClick = item.onClick
                )
            }
        }
    }
}

@Composable
fun NoMemoAnchoredMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchorBounds: IntRect?,
    actions: List<NoMemoMenuActionItem>,
    menuWidth: Dp = 194.dp,
    verticalGap: Dp = 8.dp
) {
    if (actions.isEmpty()) return

    var keepMounted by remember { mutableStateOf(expanded) }
    val density = LocalDensity.current
    val verticalGapPx = with(density) { verticalGap.roundToPx() }
    val horizontalInsetPx = with(density) { 12.dp.roundToPx() }
    val topInsetPx = with(density) { 12.dp.roundToPx() }
    val bottomInsetPx = with(density) { 12.dp.roundToPx() }
    val popupProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (expanded) 170 else 120,
            easing = FastOutSlowInEasing
        ),
        label = "anchoredMenuProgress"
    )
    LaunchedEffect(expanded) {
        if (expanded) {
            keepMounted = true
        } else {
            delay(120L)
            keepMounted = false
        }
    }
    val positionProvider = remember(
        anchorBounds,
        verticalGapPx,
        horizontalInsetPx,
        topInsetPx,
        bottomInsetPx
    ) {
        anchorBounds?.let {
            NoMemoAnchoredMenuPositionProvider(
                anchorBounds = it,
                verticalGapPx = verticalGapPx,
                horizontalInsetPx = horizontalInsetPx,
                topInsetPx = topInsetPx,
                bottomInsetPx = bottomInsetPx
            )
        }
    }

    if (keepMounted && positionProvider != null) {
        Popup(
            popupPositionProvider = positionProvider,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                clippingEnabled = false
            )
        ) {
            NoMemoAnchoredMenuAnimatedLayer(
                progress = popupProgress,
                menuWidth = menuWidth
            ) {
                NoMemoMenuList(
                    actions = actions,
                    modifier = Modifier.width(menuWidth)
                )
            }
        }
    }
}

@Composable
private fun NoMemoAnchoredMenuRow(
    iconRes: Int,
    label: String,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val destructiveBase = Color(0xFFFF5A52)
    val contentColor = if (destructive) {
        destructiveBase
    } else {
        palette.textPrimary
    }
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressedBackground = if (destructive) {
        destructiveBase.copy(alpha = if (isDark) 0.22f else 0.14f)
    } else if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.Black.copy(alpha = 0.045f)
    }
    val rowShape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(rowShape)
                .background(if (pressed) pressedBackground else Color.Transparent)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
        }
    }
}

data class NoMemoMenuActionItem(
    val iconRes: Int,
    val label: String,
    val onClick: () -> Unit,
    val destructive: Boolean = false
)

@Composable
fun NoMemoPillTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(999.dp),
            colors = CardDefaults.cardColors(containerColor = palette.glassFill),
            border = BorderStroke(1.dp, palette.glassStroke)
        ) {
            Text(
                text = text,
                color = palette.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
            )
        }
    }
}

@Composable
fun NoMemoWideActionButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color
) {
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(26.dp)),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
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
    val isDark = isSystemInDarkTheme()
    val palette = rememberNoMemoPalette()
    val dockShape = RoundedCornerShape(999.dp)
    val dockHeight = if (spec.isNarrow) 64.dp else 68.dp
    val dockBlurTint = if (isDark) {
        palette.dockSurface.copy(alpha = 0.72f)
    } else {
        Color.White.copy(alpha = 0.24f)
    }
    val dockContainerBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                palette.dockSurface.copy(alpha = 0.96f),
                Color(0xFF101520).copy(alpha = 0.98f),
                Color(0xFF0D121A).copy(alpha = 0.96f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.88f),
                palette.dockSurface.copy(alpha = 0.84f),
                Color(0xFFEAF0F8).copy(alpha = 0.80f)
            )
        )
    }
    val dockStroke = if (isDark) {
        if (showEnhancedOutline) {
            palette.dockStroke.copy(alpha = 0.86f)
        } else {
            palette.dockStroke.copy(alpha = 0.34f)
        }
    } else {
        palette.dockStroke.copy(alpha = 0.58f)
    }
    val dockInnerStroke = if (isDark) {
        if (showEnhancedOutline) {
            Color.White.copy(alpha = 0.06f)
        } else {
            Color.White.copy(alpha = 0.028f)
        }
    } else {
        Color.White.copy(alpha = 0.92f)
    }
    val dockShadow = if (isDark) {
        Color.Black.copy(alpha = 0.28f)
    } else {
        Color(0xFF263242).copy(alpha = 0.10f)
    }
    val dockFrostMistBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.02f),
                Color.Black.copy(alpha = 0.10f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.10f),
                Color(0xFFB9C7DA).copy(alpha = 0.04f)
            )
        )
    }
    val lightGlassRefractionBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.22f),
            Color(0xFFDCE6F5).copy(alpha = 0.10f),
            Color.Transparent
        ),
        start = Offset(0f, 0f),
        end = Offset(1200f, 800f)
    )
    val lightGlassTopSheenBrush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.34f),
            Color.White.copy(alpha = 0.12f),
            Color.Transparent
        )
    )
    val lightGlassDepthBrush = Brush.verticalGradient(
        listOf(
            Color.Transparent,
            Color.Transparent,
            Color(0xFFB2C0D4).copy(alpha = 0.06f)
        )
    )
    val lightGlassEdgeVignette = Brush.horizontalGradient(
        listOf(
            Color(0xFFD0D9E8).copy(alpha = 0.07f),
            Color.Transparent,
            Color(0xFFD0D9E8).copy(alpha = 0.07f)
        )
    )
    val overlayBackdropTint = if (isDark) {
        Color(0xFF0A0E15).copy(alpha = 0.20f)
    } else {
        Color(0xFFE6EDF7).copy(alpha = 0.10f)
    }
    val enhancedOutlineColor = if (showEnhancedOutline) {
        if (isDark) {
            Color.White.copy(alpha = 0.10f)
        } else {
            Color(0xFFAEBED6).copy(alpha = 0.24f)
        }
    } else {
        Color.Transparent
    }
    val selectedGlowTarget = when (selectedTab) {
        NoMemoDockTab.MEMORY -> 0.17f
        NoMemoDockTab.GROUP -> 0.50f
        NoMemoDockTab.REMINDER -> 0.83f
    }
    val selectedGlowX by animateFloatAsState(
        targetValue = selectedGlowTarget,
        animationSpec = tween(durationMillis = 360, easing = DockEaseOut),
        label = "dockGlowX"
    )
    val addButtonShape = CircleShape
    val addButtonSize = dockHeight - 2.dp
    val addButtonBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1F2530),
                palette.dockFabSurface,
                Color(0xFF131821)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.99f),
                palette.dockFabSurface.copy(alpha = 0.98f),
                Color(0xFFE2EAF6).copy(alpha = 0.96f)
            )
        )
    }
    val addButtonIconTint = if (isDark) {
        Color.White.copy(alpha = 0.96f)
    } else {
        Color(0xFF253244).copy(alpha = 0.94f)
    }
    val addButtonShadow = if (isDark) {
        if (showEnhancedOutline) {
            Color.Black.copy(alpha = 0.24f)
        } else {
            Color.Black.copy(alpha = 0.18f)
        }
    } else {
        Color(0xFF223147).copy(alpha = 0.10f)
    }
    val addButtonStroke = if (isDark) {
        if (showEnhancedOutline) {
            palette.dockStroke.copy(alpha = 0.56f)
        } else {
            palette.dockStroke.copy(alpha = 0.24f)
        }
    } else {
        palette.dockStroke.copy(alpha = 0.48f)
    }
    val haptic = LocalHapticFeedback.current
    val navTrackWidthFraction = if (spec.isNarrow) 0.90f else 0.86f
    val navTrackHorizontalPadding = if (spec.isNarrow) 4.dp else 6.dp
    val bottomIndicatorWidth = if (spec.isNarrow) 24.dp else 28.dp
    val selectedDockIndex = when (selectedTab) {
        NoMemoDockTab.MEMORY -> 0
        NoMemoDockTab.GROUP -> 1
        NoMemoDockTab.REMINDER -> 2
    }
    val selectedDockIndexAnimated by animateFloatAsState(
        targetValue = selectedDockIndex.toFloat(),
        animationSpec = tween(durationMillis = 220, easing = DockEaseOut),
        label = "dockBottomIndicatorIndex"
    )

    val dockSwipeModifier = when (selectedTab) {
        NoMemoDockTab.MEMORY -> Modifier.pageSwipeNavigation(
            onSwipeRight = onOpenGroup
        )
        NoMemoDockTab.GROUP -> Modifier.pageSwipeNavigation(
            onSwipeLeft = onOpenMemory,
            onSwipeRight = onOpenReminder
        )
        NoMemoDockTab.REMINDER -> Modifier.pageSwipeNavigation(
            onSwipeLeft = onOpenGroup
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(dockSwipeModifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (spec.isNarrow) 0.73f else 0.75f)
                .height(dockHeight)
                .shadow(
                    elevation = if (spec.isNarrow) 8.dp else 10.dp,
                    shape = dockShape,
                    ambientColor = dockShadow,
                    spotColor = dockShadow
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            when {
                                isDark && showEnhancedOutline -> 30.dp
                                isDark -> 24.dp
                                showEnhancedOutline -> 30.dp
                                else -> 20.dp
                            }
                        } else {
                            when {
                                isDark && showEnhancedOutline -> 20.dp
                                isDark -> 14.dp
                                showEnhancedOutline -> 22.dp
                                else -> 16.dp
                            }
                        },
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .clip(dockShape)
                    .background(dockBlurTint)
            )
            if (showEnhancedOutline) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(
                            radius = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 24.dp else 16.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                        .clip(dockShape)
                        .background(overlayBackdropTint)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(dockShape)
                    .background(dockContainerBrush)
                    .border(1.dp, dockStroke, dockShape)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .border(0.8.dp, dockInnerStroke, RoundedCornerShape(999.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(dockShape)
                    .background(dockFrostMistBrush)
                    .blur(
                        radius = if (showEnhancedOutline) 22.dp else 18.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .clip(dockShape)
            )
            if (!isDark) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .background(lightGlassRefractionBrush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .background(lightGlassTopSheenBrush)
                        .drawBehind {
                            drawLine(
                                color = Color.White.copy(alpha = 0.62f),
                                start = Offset(18.dp.toPx(), 1.dp.toPx()),
                                end = Offset(size.width - 18.dp.toPx(), 1.dp.toPx()),
                                strokeWidth = 1.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .background(lightGlassDepthBrush)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .background(lightGlassEdgeVignette)
                )
            }
            DockGlowLayer(
                animatedX = selectedGlowX,
                isDark = isDark,
                dockShape = dockShape,
                glowColor = palette.dockGlow
            )
            if (showEnhancedOutline) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(dockShape)
                        .border(1.dp, enhancedOutlineColor, dockShape)
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .fillMaxWidth(navTrackWidthFraction)
                    .padding(
                        horizontal = navTrackHorizontalPadding,
                        vertical = if (spec.isNarrow) 5.dp else 6.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockNavItem(
                    iconRes = R.drawable.ic_nm_memory,
                    text = stringResource(R.string.nav_memory),
                    selected = selectedTab == NoMemoDockTab.MEMORY,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenMemory()
                    },
                    spec = spec,
                    isDark = isDark,
                    isPrimaryCenter = false
                )
                DockNavItem(
                    iconRes = R.drawable.ic_nm_group,
                    text = stringResource(R.string.nav_group),
                    selected = selectedTab == NoMemoDockTab.GROUP,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenGroup()
                    },
                    spec = spec,
                    isDark = isDark,
                    isPrimaryCenter = true
                )
                DockNavItem(
                    iconRes = R.drawable.ic_nm_reminder,
                    text = stringResource(R.string.nav_reminder),
                    selected = selectedTab == NoMemoDockTab.REMINDER,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenReminder()
                    },
                    spec = spec,
                    isDark = isDark,
                    isPrimaryCenter = false
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        val navWidth = size.width * navTrackWidthFraction
                        val navLeft = (size.width - navWidth) / 2f + navTrackHorizontalPadding.toPx()
                        val navInnerWidth = navWidth - navTrackHorizontalPadding.toPx() * 2f
                        val slotWidth = navInnerWidth / 3f
                        val indicatorWidthPx = bottomIndicatorWidth.toPx()
                        val startX = navLeft + selectedDockIndexAnimated * slotWidth + (slotWidth - indicatorWidthPx) / 2f
                        val endX = startX + indicatorWidthPx
                        val y = size.height - 1.dp.toPx()
                        drawLine(
                            color = palette.dockIndicator,
                            start = Offset(startX, y),
                            end = Offset(endX, y),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
            )
        }

        PressScaleBox(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onAddClick()
            },
            pressedScale = 0.95f,
            modifier = Modifier
                .size(addButtonSize)
                .shadow(
                    elevation = if (spec.isNarrow) 5.dp else 7.dp,
                    shape = addButtonShape,
                    ambientColor = addButtonShadow,
                    spotColor = addButtonShadow
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(addButtonShape)
                        .background(addButtonBrush)
                        .border(
                            width = 1.dp,
                            color = addButtonStroke,
                            shape = addButtonShape
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(addButtonShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = if (isDark) 0.26f else 0.18f),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            )
                        )
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_nm_compose),
                    contentDescription = stringResource(R.string.save_record_desc),
                    tint = addButtonIconTint,
                    modifier = Modifier
                        .size(if (spec.isNarrow) 20.dp else 22.dp)
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
    spec: NoMemoAdaptiveSpec,
    isDark: Boolean,
    isPrimaryCenter: Boolean
) {
    val selectedIconColor = if (isDark) Color(0xFFF5F8FF) else Color(0xFF243142)
    val selectedTextColor = if (isDark) Color(0xFFE8EEFF) else Color(0xFF243142)
    val baseColor = if (isDark) Color(0xFFE6ECF8) else Color(0xFF536275)
    val baseAlpha = if (isDark) {
        if (isPrimaryCenter) 0.70f else 0.56f
    } else {
        if (isPrimaryCenter) 0.82f else 0.66f
    }
    val alpha by animateFloatAsState(
        targetValue = if (selected) 1f else baseAlpha,
        animationSpec = tween(durationMillis = 230, easing = DockEaseOut),
        label = "dockItemAlpha"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = tween(durationMillis = 230, easing = DockEaseOut),
        label = "dockIconScale"
    )
    val lift by animateDpAsState(
        targetValue = if (selected) (-1.5).dp else 0.dp,
        animationSpec = tween(durationMillis = 230, easing = DockEaseOut),
        label = "dockLift"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) {
            selectedIconColor.copy(alpha = alpha)
        } else {
            baseColor.copy(alpha = alpha)
        },
        animationSpec = tween(durationMillis = 200, easing = DockEaseOut),
        label = "dockIconTint"
    )
    val textTint by animateColorAsState(
        targetValue = if (selected) {
            selectedTextColor.copy(alpha = alpha)
        } else {
            baseColor.copy(alpha = alpha)
        },
        animationSpec = tween(durationMillis = 200, easing = DockEaseOut),
        label = "dockTextTint"
    )

    PressScaleBox(
        onClick = onClick,
        pressedScale = 0.98f,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = lift)
                .padding(vertical = if (spec.isNarrow) 4.dp else 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier
                    .size(if (spec.isNarrow) 20.dp else 22.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = text,
                color = textTint,
                fontSize = if (spec.isNarrow) 12.sp else 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DockGlowLayer(
    animatedX: Float,
    isDark: Boolean,
    dockShape: RoundedCornerShape,
    glowColor: Color
) {
    val whiteGlow = if (isDark) Color.White.copy(alpha = 0.13f) else Color.White.copy(alpha = 0.24f)
    val coolGlow = if (isDark) glowColor.copy(alpha = 0.16f) else glowColor.copy(alpha = 0.14f)
    val edgeShade = if (isDark) Color.Black.copy(alpha = 0.26f) else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(dockShape)
            .drawBehind {
                val centerX = size.width * animatedX
                val centerY = size.height * 0.52f
                val radius = size.minDimension * if (isDark) 0.56f else 0.50f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            whiteGlow,
                            Color.Transparent
                        ),
                        center = Offset(centerX, centerY),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(coolGlow, Color.Transparent),
                        center = Offset(centerX, centerY + size.minDimension * 0.02f),
                        radius = size.minDimension * if (isDark) 0.64f else 0.58f
                    ),
                    radius = size.minDimension * if (isDark) 0.64f else 0.58f,
                    center = Offset(centerX, centerY + size.minDimension * 0.02f)
                )
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            edgeShade,
                            Color.Transparent,
                            edgeShade
                        ),
                        startX = 0f,
                        endX = size.width
                    )
                )
            }
            .blur(if (isDark) 22.dp else 18.dp)
    )
}

private fun loadSampledBitmap(context: android.content.Context, uri: Uri, widthPx: Int, heightPx: Int): Bitmap? {
    return runCatching {
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
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    }.getOrNull()
}

private fun loadSampledBitmapFromFile(filePath: String, widthPx: Int, heightPx: Int): Bitmap? {
    return runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(filePath, bounds)

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
        BitmapFactory.decodeFile(filePath, options)
    }.getOrNull()
}

private fun loadMemoryThumbnail(
    context: android.content.Context,
    uriString: String,
    widthPx: Int,
    heightPx: Int
): Bitmap? {
    if (uriString.isBlank()) return null
    val key = memoryThumbnailCacheKey(uriString, widthPx, heightPx)
    MemoryThumbnailCache.getExact(key)?.let { return it }
    MemoryThumbnailCache.getByUri(uriString)?.let { return it }

    // Try multiple strategies to load the thumbnail:
    // 1) Treat as content/file URI and use ContentResolver (loadThumbnail / openInputStream)
    // 2) Treat as absolute file path and decode directly
    // 3) If it's a file:// URI, decode by path
    var bitmap: Bitmap? = null
    // Strategy A: parse as URI and try ContentResolver
    val parsedUri = runCatching { Uri.parse(uriString) }.getOrNull()
    if (parsedUri != null) {
        bitmap = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(parsedUri, Size(widthPx.coerceAtLeast(1), heightPx.coerceAtLeast(1)), null)
            } else {
                loadSampledBitmap(context, parsedUri, widthPx, heightPx)
            }
        }.getOrNull()
        if (bitmap == null) {
            bitmap = loadSampledBitmap(context, parsedUri, widthPx, heightPx)
        }
    }

    // Strategy B: treat uriString as a raw file path
    if (bitmap == null) {
        try {
            val file = java.io.File(uriString)
            if (file.exists()) {
                bitmap = loadSampledBitmapFromFile(file.absolutePath, widthPx, heightPx)
            }
        } catch (_: Exception) {
        }
    }

    // Strategy C: if parsedUri is file://, try decode by its path
    if (bitmap == null && parsedUri != null && parsedUri.scheme == "file") {
        val path = parsedUri.path
        if (!path.isNullOrBlank()) {
            bitmap = loadSampledBitmapFromFile(path, widthPx, heightPx)
        }
    }

    if (bitmap != null) {
        MemoryThumbnailCache.put(key, uriString, bitmap)
    }
    return bitmap
}

@Composable
fun MemoryThumbnail(
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
    val cacheKey = remember(uriString, widthPx, heightPx) {
        memoryThumbnailCacheKey(uriString, widthPx, heightPx)
    }
    var bitmap by remember(uriString) {
        mutableStateOf<Bitmap?>(MemoryThumbnailCache.getByUri(uriString))
    }

    LaunchedEffect(uriString, widthPx, heightPx, cacheKey) {
        if (uriString.isBlank()) {
            bitmap = null
            return@LaunchedEffect
        }
        val cached = MemoryThumbnailCache.getExact(cacheKey) ?: MemoryThumbnailCache.getByUri(uriString)
        if (cached != null) {
            if (bitmap !== cached) {
                bitmap = cached
            }
            return@LaunchedEffect
        }
        val loaded = withContext(Dispatchers.IO) {
            loadMemoryThumbnail(context.applicationContext, uriString, widthPx, heightPx)
        }
        if (loaded != null) {
            bitmap = loaded
        }
    }
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        val shownBitmap = bitmap
        if (shownBitmap != null) {
            Image(
                bitmap = shownBitmap.asImageBitmap(),
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
    allowImageLoading: Boolean = true,
    showShadow: Boolean = true,
    darkCardBackgroundOverride: Color? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()
    val transientAiProcessing = AiProcessingStateRegistry.isProcessing(record.recordId)
    val aiProcessing = transientAiProcessing || remember(
        record.recordId,
        record.mode,
        record.engine,
        record.analysis,
        record.title,
        record.summary
    ) {
        isAiProcessingRecord(record)
    }
    val timeFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val titleText = remember(record.recordId, record.title, record.memory) {
        record.title?.takeIf { it.isNotBlank() } ?: record.memory.orEmpty()
    }
    val summaryText = remember(record.recordId, record.analysis, record.summary, record.memory, record.sourceText) {
        when {
            !record.analysis.isNullOrBlank() -> record.analysis
            !record.memory.isNullOrBlank() -> record.memory
            !record.summary.isNullOrBlank() -> record.summary
            !record.sourceText.isNullOrBlank() -> record.sourceText
            else -> ""
        }
    }
    val categoryText = remember(record.categoryName, context) {
        record.categoryName ?: context.getString(R.string.tag_quick)
    }
    val timeText = remember(record.createdAt) {
        timeFormat.format(Date(record.createdAt))
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
    val cardCornerRadius = if (adaptive.isNarrow) 28.dp else 30.dp
    val cardShape = RoundedCornerShape(cardCornerRadius)
    val darkCardColor = darkCardBackgroundOverride ?: noMemoCardSurfaceColor(true)
    val lightCardTop = Color.White.copy(alpha = 0.995f)
    val lightCardBottom = Color(0xFFFCFCFD).copy(alpha = 0.995f)
    val cardGradient = if (selected) {
        noMemoSelectedCardGradient(isDark)
    } else if (isDark) {
        listOf(
            darkCardColor,
            darkCardColor
        )
    } else {
        listOf(
            lightCardTop,
            lightCardBottom
        )
    }
    val cardShadow = if (showShadow) {
        if (isDark) 0.dp else if (selected) 5.dp else 4.dp
    } else {
        0.dp
    }
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
    val thumbnailBackground = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F4F8)
    val thumbnailBorder = if (isDark) Color.Transparent else Color.Transparent
    val gestureModifier = if (onLongPress == null && onClick == null) {
        Modifier
    } else {
        Modifier.pointerInput(onLongPress, onClick) {
            detectTapGestures(
                onTap = {
                    onClick?.invoke()
                },
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress?.invoke()
                }
            )
        }
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(gestureModifier),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = cardShadow)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(cardGradient))
                    .padding(horizontal = 18.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (aiProcessing) {
                        AiProcessingStatusChip(isDark = isDark)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
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
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    RecordMetaLine(
                        timeText = timeText,
                        categoryCode = record.categoryCode,
                        categoryText = categoryText,
                        metaColor = metaColor
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
        if (aiProcessing) {
            AiProcessingBorderOverlay(
                modifier = Modifier.matchParentSize(),
                cornerRadius = cardCornerRadius,
                isDark = isDark
            )
        }
    }
}

private fun isAiProcessingRecord(record: MemoryRecord): Boolean {
    if (record.mode != MemoryRecord.MODE_AI) {
        return false
    }
    val analysisPending = isAiProcessingPlaceholderText(record.analysis)
    val titlePending = isAiProcessingPlaceholderText(record.title)
    val summaryPending = isAiProcessingPlaceholderText(record.summary)
    return analysisPending || titlePending || summaryPending
}

private fun isAiProcessingPlaceholderText(value: String?): Boolean {
    val normalized = value?.trim().orEmpty()
    if (normalized.isEmpty()) return false
    return normalized == "AI 分析中"
        || normalized == "AI分析中"
        || normalized == "AI 分析中..."
        || normalized == "AI分析中..."
        || normalized == "分析中"
        || normalized == "分析中..."
}

@Composable
private fun AiProcessingStatusChip(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val chipBackground = if (isDark) {
        Color.Black.copy(alpha = 0.78f)
    } else {
        Color(0xFF111111).copy(alpha = 0.92f)
    }
    val chipBorder = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.18f)
    }
    val dotBrush = Brush.linearGradient(
        listOf(
            Color(0xFFFFBC79),
            Color(0xFFFFA0B3),
            Color(0xFFFFE3EC)
        )
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(chipBackground)
            .border(
                width = 1.dp,
                color = chipBorder,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(dotBrush, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "正在分析",
            color = Color.White.copy(alpha = 0.98f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AiProcessingBorderOverlay(
    cornerRadius: Dp,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "aiProcessingBorder")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "aiProcessingBorderProgress"
    )
    val baseBorder = if (isDark) {
        Color.White.copy(alpha = 0.22f)
    } else {
        Color(0xFFE5ECF8)
    }
    val activeBorder = if (isDark) {
        Color(0xFFFFE5D1).copy(alpha = 0.28f)
    } else {
        Color(0xFFF5DDE6).copy(alpha = 0.98f)
    }
    val warm = if (isDark) {
        Color(0xFFFFBC79).copy(alpha = 0.84f)
    } else {
        Color(0xFFFFBE86).copy(alpha = 0.96f)
    }
    val softPink = if (isDark) {
        Color(0xFFFFA3B8).copy(alpha = 0.80f)
    } else {
        Color(0xFFFFADC1).copy(alpha = 0.90f)
    }
    val coolPink = if (isDark) {
        Color(0xFFFFE1EB).copy(alpha = 0.62f)
    } else {
        Color(0xFFFFE4ED).copy(alpha = 0.80f)
    }
    Box(
        modifier = modifier.drawWithCache {
            val strokeWidth = 1.55.dp.toPx()
            val inset = strokeWidth / 2f
            val radiusPx = cornerRadius.toPx()
            val drawSize = androidx.compose.ui.geometry.Size(
                width = size.width - strokeWidth,
                height = size.height - strokeWidth
            )
            val topLeft = Offset(inset, inset)
            val stroke = Stroke(width = strokeWidth)

            val androidPath = android.graphics.Path().apply {
                addRoundRect(
                    RectF(
                        topLeft.x,
                        topLeft.y,
                        topLeft.x + drawSize.width,
                        topLeft.y + drawSize.height
                    ),
                    radiusPx,
                    radiusPx,
                    android.graphics.Path.Direction.CW
                )
            }
            val pathMeasure = PathMeasure(androidPath, true)
            val pathLength = pathMeasure.length
            val tailPath = android.graphics.Path()
            val midPath = android.graphics.Path()
            val headPath = android.graphics.Path()

            fun normalizeDistance(distance: Float): Float {
                if (pathLength <= 0f) return 0f
                var normalized = distance % pathLength
                if (normalized < 0f) {
                    normalized += pathLength
                }
                return normalized
            }

            fun buildSegmentPath(
                reusablePath: android.graphics.Path,
                rawStart: Float,
                rawEnd: Float
            ) {
                reusablePath.rewind()
                if (pathLength <= 0f) return
                var remaining = rawEnd - rawStart
                if (remaining <= 0f) return
                var cursor = rawStart
                while (remaining > 0.5f) {
                    val segmentStart = normalizeDistance(cursor)
                    val available = pathLength - segmentStart
                    if (available <= 0.5f) {
                        cursor += 0.5f
                        remaining -= 0.5f
                        continue
                    }
                    val segmentLength = minOf(remaining, available)
                    pathMeasure.getSegment(
                        segmentStart,
                        segmentStart + segmentLength,
                        reusablePath,
                        true
                    )
                    cursor += segmentLength
                    remaining -= segmentLength
                }
            }

            onDrawBehind {
                drawRoundRect(
                    color = baseBorder,
                    topLeft = topLeft,
                    size = drawSize,
                    cornerRadius = CornerRadius(radiusPx, radiusPx),
                    style = stroke
                )
                drawRoundRect(
                    color = activeBorder,
                    topLeft = topLeft,
                    size = drawSize,
                    cornerRadius = CornerRadius(radiusPx, radiusPx),
                    style = Stroke(width = strokeWidth * 0.92f)
                )

                if (pathLength <= 0f) return@onDrawBehind

                val tailLength = pathLength * 0.20f
                val midLength = pathLength * 0.13f
                val headLength = pathLength * 0.07f
                val headEnd = progress * pathLength

                buildSegmentPath(
                    reusablePath = tailPath,
                    rawStart = headEnd - headLength - midLength - tailLength,
                    rawEnd = headEnd - headLength - midLength
                )
                buildSegmentPath(
                    reusablePath = midPath,
                    rawStart = headEnd - headLength - midLength,
                    rawEnd = headEnd - headLength
                )
                buildSegmentPath(
                    reusablePath = headPath,
                    rawStart = headEnd - headLength,
                    rawEnd = headEnd
                )

                drawPath(
                    path = tailPath.asComposePath(),
                    color = warm,
                    style = Stroke(
                        width = strokeWidth * 0.98f,
                        cap = StrokeCap.Round
                    )
                )
                drawPath(
                    path = midPath.asComposePath(),
                    color = softPink,
                    style = Stroke(
                        width = strokeWidth * 1.02f,
                        cap = StrokeCap.Round
                    )
                )
                drawPath(
                    path = headPath.asComposePath(),
                    color = coolPink,
                    style = Stroke(
                        width = strokeWidth * 1.08f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    )
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
fun RecordMetaLine(
    timeText: String,
    categoryCode: String,
    categoryText: String,
    metaColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = timeText,
            color = metaColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        MetaDividerDot(color = metaColor)
        Icon(
            imageVector = recordCategoryMetaIcon(categoryCode),
            contentDescription = null,
            tint = metaColor.copy(alpha = 0.86f),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = categoryText,
            color = metaColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun recordCategoryMetaIcon(categoryCode: String): ImageVector {
    return when (categoryCode) {
        CategoryCatalog.CODE_LIFE_PICKUP -> Icons.Outlined.Restaurant
        CategoryCatalog.CODE_LIFE_DELIVERY -> Icons.Outlined.LocalShipping
        CategoryCatalog.CODE_LIFE_CARD -> Icons.Outlined.Badge
        CategoryCatalog.CODE_LIFE_TICKET -> Icons.Outlined.ConfirmationNumber
        CategoryCatalog.CODE_WORK_TODO -> Icons.Outlined.AssignmentTurnedIn
        CategoryCatalog.CODE_WORK_SCHEDULE -> Icons.Outlined.CalendarMonth
        else -> Icons.Outlined.Edit
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
