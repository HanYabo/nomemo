package com.han.nomemo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.continuities.G2Continuity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.tanh

private val DockShape = ContinuousCapsule(G2Continuity())
private val LocalDockTabScale = staticCompositionLocalOf { { 1f } }

private data class DockTabSpec(
    val tab: NoMemoDockTab,
    val iconRes: Int,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun LiquidGlassDock(
    selectedTab: NoMemoDockTab,
    onOpenMemory: () -> Unit,
    onOpenGroup: () -> Unit,
    onOpenReminder: () -> Unit,
    onAddClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    sharedBackdrop: Backdrop,
    dockOrderOverride: List<NoMemoDockTab>? = null,
    showAddButton: Boolean = true,
    startupPulseTab: NoMemoDockTab? = null,
    startupPulseDelayMs: Long = 0L
) {
    val palette = rememberNoMemoPalette()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val latestOpenMemory = rememberUpdatedState(onOpenMemory)
    val latestOpenGroup = rememberUpdatedState(onOpenGroup)
    val latestOpenReminder = rememberUpdatedState(onOpenReminder)
    val settingsStore = remember(appContext) { SettingsStore(appContext) }
    val prefs = remember(appContext) {
        appContext.getSharedPreferences(SettingsStore.PREF_NAME, Context.MODE_PRIVATE)
    }
    var dockOrderVersion by remember { mutableIntStateOf(0) }
    DisposableEffect(prefs) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "bottom_dock_order") {
                dockOrderVersion += 1
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
    val memoryLabel = stringResource(R.string.nav_memory)
    val groupLabel = stringResource(R.string.nav_group)
    val reminderLabel = stringResource(R.string.nav_reminder)
    val persistedDockOrder =
        remember(settingsStore, dockOrderVersion) { settingsStore.bottomDockOrder }
    val dockOrder = dockOrderOverride ?: persistedDockOrder
    val tabs = remember(
        dockOrder,
        memoryLabel,
        groupLabel,
        reminderLabel
    ) {
        dockOrder.map { tab ->
            when (tab) {
                NoMemoDockTab.MEMORY -> DockTabSpec(
                    tab = NoMemoDockTab.MEMORY,
                    iconRes = R.drawable.ic_nm_memory_dock,
                    label = memoryLabel,
                    onClick = { latestOpenMemory.value.invoke() }
                )

                NoMemoDockTab.GROUP -> DockTabSpec(
                    tab = NoMemoDockTab.GROUP,
                    iconRes = R.drawable.ic_nm_group_dock,
                    label = groupLabel,
                    onClick = { latestOpenGroup.value.invoke() }
                )

                NoMemoDockTab.REMINDER -> DockTabSpec(
                    tab = NoMemoDockTab.REMINDER,
                    iconRes = R.drawable.ic_nm_reminder_dock,
                    label = reminderLabel,
                    onClick = { latestOpenReminder.value.invoke() }
                )
            }
        }
    }
    val dockHeight = if (spec.isNarrow) 56.dp else 60.dp
    val buttonSize = if (spec.isNarrow) 54.dp else 58.dp
    val dockWidth = if (spec.isNarrow) 258.dp else 282.dp
    val horizontalInset = if (spec.isNarrow) 6.dp else 8.dp
    val backdrop = sharedBackdrop

    if (showAddButton) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(dockHeight)
                .padding(horizontal = horizontalInset),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LiquidGlassDockTabs(
                selectedTab = selectedTab,
                tabs = tabs,
                dockWidth = dockWidth,
                dockHeight = dockHeight,
                enabled = enabled,
                backdrop = backdrop,
                palette = palette,
                haptic = haptic,
                startupPulseTab = startupPulseTab,
                startupPulseDelayMs = startupPulseDelayMs
            )

            LiquidGlassAddButton(
                onAddClick = onAddClick,
                enabled = enabled,
                buttonSize = buttonSize,
                spec = spec,
                backdrop = backdrop,
                haptic = haptic
            )
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(dockHeight)
                .padding(horizontal = horizontalInset),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassDockTabs(
                selectedTab = selectedTab,
                tabs = tabs,
                dockWidth = dockWidth,
                dockHeight = dockHeight,
                enabled = enabled,
                backdrop = backdrop,
                palette = palette,
                haptic = haptic,
                startupPulseTab = startupPulseTab,
                startupPulseDelayMs = startupPulseDelayMs
            )
        }
    }
}

@Composable
private fun LiquidGlassDockTabs(
    selectedTab: NoMemoDockTab,
    tabs: List<DockTabSpec>,
    dockWidth: Dp,
    dockHeight: Dp,
    enabled: Boolean,
    backdrop: Backdrop,
    palette: NoMemoPalette,
    haptic: HapticFeedback,
    startupPulseTab: NoMemoDockTab? = null,
    startupPulseDelayMs: Long = 0L
) {
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor = if (isLightTheme) Color(0xFF0088FF) else Color(0xFF0091FF)
    val containerColor =
        if (isLightTheme) Color(0xFFFAFAFA).copy(alpha = 0.40f) else Color(0xFF121212).copy(alpha = 0.40f)
    val themedDockLensColor = androidx.compose.ui.graphics.lerp(
        palette.memoBgMid,
        palette.dockSurface,
        if (isLightTheme) 0.26f else 0.20f
    )
    val activeMaskColor =
        if (isLightTheme) {
            Color(0xFFDADBDE)
        } else {
            androidx.compose.ui.graphics.lerp(themedDockLensColor, Color.White, 0.12f).copy(alpha = 0.988f)
        }
    val baseColor = if (isLightTheme) Color.Black else Color.White
    val tabsBackdrop = rememberLayerBackdrop()

    Box(
        modifier = Modifier
            .width(dockWidth)
            .height(dockHeight),
        contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        val dockWidthPx = with(density) { dockWidth.toPx() }
        val tabsTrackWidth = with(density) { dockWidthPx - 8.dp.toPx() }
        val tabWidth = tabsTrackWidth / tabs.size
        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density, dockWidthPx) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / dockWidthPx).fastCoerceIn(-1f, 1f)
                with(density) {
                    4.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                }
            }
        }

        val selectedIndex = remember(selectedTab, tabs) {
            tabs.indexOfFirst { it.tab == selectedTab }.coerceAtLeast(0)
        }
        val tabOrderKey = tabs.map { it.tab }
        var currentIndex by remember(tabOrderKey) {
            mutableIntStateOf(selectedIndex)
        }
        var hasBoundInitialSelection by remember(tabOrderKey) {
            mutableStateOf(false)
        }
        var startupPulseConsumed by remember(startupPulseTab, selectedTab, tabOrderKey) {
            mutableStateOf(startupPulseTab == null || startupPulseTab != selectedTab)
        }
        val latestTabs by rememberUpdatedState(tabs)
        val latestSelectedIndex by rememberUpdatedState(selectedIndex)

        val dampedDragAnimation = remember(animationScope, tabs.size) {
            LiquidGlassDampedDragAnimation(
                animationScope = animationScope,
                initialValue = selectedIndex.toFloat(),
                valueRange = 0f..(tabs.lastIndex).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                onDragStarted = {},
                onDragStopped = {
                    val targetIndex = targetValue.fastRoundToInt().coerceIn(0, tabs.lastIndex)
                    currentIndex = targetIndex
                    animateToValue(targetIndex.toFloat())
                    animationScope.launch {
                        offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
                    }
                },
                onDrag = { _, dragAmount ->
                    updateValue(
                        (targetValue + dragAmount.x / tabWidth * if (isLtr) 1f else -1f)
                            .fastCoerceIn(0f, tabs.lastIndex.toFloat())
                    )
                    animationScope.launch {
                        offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                    }
                }
            )
        }
        LaunchedEffect(selectedIndex, dampedDragAnimation) {
            currentIndex = selectedIndex
            if (hasBoundInitialSelection) {
                dampedDragAnimation.animateToValue(selectedIndex.toFloat())
                offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
            } else {
                hasBoundInitialSelection = true
                offsetAnimation.snapTo(0f)
            }
        }

        LaunchedEffect(
            startupPulseConsumed,
            selectedIndex,
            startupPulseDelayMs,
            dampedDragAnimation
        ) {
            if (!startupPulseConsumed) {
                if (startupPulseDelayMs > 0) {
                    kotlinx.coroutines.delay(startupPulseDelayMs)
                }
                dampedDragAnimation.animateToValue(selectedIndex.toFloat())
                offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
                startupPulseConsumed = true
            }
        }

        LaunchedEffect(dampedDragAnimation, tabOrderKey) {
            snapshotFlow { currentIndex }
                .drop(1)
                .collectLatest { index ->
                    dampedDragAnimation.animateToValue(index.toFloat())
                    if (index != latestSelectedIndex) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        latestTabs[index].onClick()
                    }
                }
        }

        val interactiveHighlight = remember(animationScope) {
            LiquidGlassInteractiveHighlight(
                animationScope = animationScope,
                position = { size, _ ->
                    Offset(
                        if (isLtr) (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset
                        else size.width - (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset,
                        size.height / 2f
                    )
                }
            )
        }
        val activeTabTranslationX by remember(isLtr, tabWidth) {
            derivedStateOf {
                if (isLtr) {
                    dampedDragAnimation.value * tabWidth + panelOffset
                } else {
                    tabsTrackWidth - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
                }
            }
        }
        val activeTabScaleX by remember {
            derivedStateOf {
                val velocity = dampedDragAnimation.velocity / 10f
                dampedDragAnimation.scaleX /
                    (1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f))
            }
        }
        val activeTabScaleY by remember {
            derivedStateOf {
                val velocity = dampedDragAnimation.velocity / 10f
                dampedDragAnimation.scaleY *
                    (1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f))
            }
        }

        val tabsContent: @Composable RowScope.(Boolean) -> Unit = { tabEnabled ->
            tabs.forEachIndexed { index, tab ->
                LiquidGlassDockItem(
                    iconRes = tab.iconRes,
                    label = tab.label,
                    contentColor = baseColor,
                    enabled = tabEnabled && enabled,
                    onClick = { currentIndex = index }
                )
            }
        }

        Row(
            Modifier
                .graphicsLayer {
                    translationX = panelOffset
                }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { DockShape },
                    effects = {
                        vibrancy()
                        blur(8.dp.toPx())
                        lens(24.dp.toPx(), 24.dp.toPx())
                    },
                    layerBlock = {
                        val progress = dampedDragAnimation.pressProgress
                        val scale = lerp(1f, 1f + 16.dp.toPx() / size.width, progress)
                        scaleX = scale
                        scaleY = scale
                    },
                    onDrawSurface = { drawRect(containerColor) }
                )
                .then(interactiveHighlight.modifier)
                .height(dockHeight)
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = { tabsContent(true) }
        )

        CompositionLocalProvider(
            LocalDockTabScale provides { lerp(1f, 1.2f, dampedDragAnimation.pressProgress) }
        ) {
            Row(
                Modifier
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer {
                        translationX = panelOffset
                    }
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { DockShape },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            vibrancy()
                            blur(8.dp.toPx())
                            lens(24.dp.toPx() * progress, 24.dp.toPx() * progress)
                        },
                        highlight = {
                            Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress)
                        },
                        onDrawSurface = { drawRect(containerColor) }
                    )
                    .then(interactiveHighlight.modifier)
                    .height(dockHeight - 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                verticalAlignment = Alignment.CenterVertically,
                content = { tabsContent(false) }
            )
        }

        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    translationX = activeTabTranslationX
                    scaleX = activeTabScaleX
                    scaleY = activeTabScaleY
                }
                .height(dockHeight - 8.dp)
                .fillMaxWidth(1f / tabs.size)
                .clip(DockShape)
                .background(activeMaskColor)
        )

        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    translationX = activeTabTranslationX
                }
                .then(if (enabled) interactiveHighlight.gestureModifier else Modifier)
                .then(if (enabled) dampedDragAnimation.modifier else Modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                    shape = { DockShape },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        lens(
                            10.dp.toPx() * progress,
                            14.dp.toPx() * progress,
                            chromaticAberration = true
                        )
                    },
                    highlight = {
                        Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress)
                    },
                    shadow = {
                        Shadow(alpha = dampedDragAnimation.pressProgress)
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(
                            radius = 8.dp * progress,
                            alpha = progress
                        )
                    },
                    layerBlock = {
                        scaleX = activeTabScaleX
                        scaleY = activeTabScaleY
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(
                            if (isLightTheme) Color.Black.copy(alpha = 0.045f) else Color.White.copy(
                                alpha = 0.085f
                            ),
                            alpha = 1f - progress
                        )
                        drawRect(
                            if (isLightTheme) Color.Black.copy(alpha = 0.012f * progress) else Color.Black.copy(
                                alpha = 0.018f * progress
                            )
                        )
                    }
                )
                .height(dockHeight - 8.dp)
                .fillMaxWidth(1f / tabs.size)
        )
    }
}

@Composable
private fun RowScope.LiquidGlassDockItem(
    iconRes: Int,
    label: String,
    contentColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val scale = LocalDockTabScale.current
    val modifier = Modifier
        .clip(DockShape)
        .fillMaxHeight()
        .weight(1f)
        .graphicsLayer {
            val value = scale()
            scaleX = value
            scaleY = value
        }
        .let {
            if (enabled) {
                it.clickable(
                    interactionSource = null,
                    indication = null,
                    role = Role.Tab,
                    onClick = onClick
                )
            } else {
                it
            }
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        androidx.compose.material3.Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun LiquidGlassAddButton(
    onAddClick: () -> Unit,
    enabled: Boolean,
    buttonSize: Dp,
    spec: NoMemoAdaptiveSpec,
    backdrop: Backdrop,
    haptic: HapticFeedback
) {
    val isDark = isSystemInDarkTheme()
    val animationScope = rememberCoroutineScope()
    val interactiveHighlight = remember(animationScope) {
        LiquidGlassInteractiveHighlight(animationScope = animationScope)
    }
    val surfaceColor =
        if (isDark) Color(0xFF121212).copy(alpha = 0.40f) else Color(0xFFFAFAFA).copy(alpha = 0.40f)
    val iconTint =
        if (isDark) Color.White.copy(alpha = 0.96f) else Color(0xFF253244).copy(alpha = 0.92f)

    Box(
        modifier = Modifier
            .size(buttonSize)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { DockShape },
                effects = {
                    vibrancy()
                    blur(2.dp.toPx())
                    lens(12.dp.toPx(), 24.dp.toPx())
                },
                layerBlock = {
                    val width = size.width
                    val height = size.height
                    val progress = interactiveHighlight.pressProgress
                    val baseScale = lerp(1f, 1f + 4.dp.toPx() / size.height, progress)
                    val maxOffset = size.minDimension
                    val initialDerivative = 0.05f
                    val offset = interactiveHighlight.offset
                    translationX = maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                    translationY = maxOffset * tanh(initialDerivative * offset.y / maxOffset)
                    val maxDragScale = 4.dp.toPx() / size.height
                    val angle = atan2(offset.y, offset.x)
                    scaleX =
                        baseScale +
                                maxDragScale *
                                abs(cos(angle) * offset.x / size.maxDimension) *
                                (width / height).fastCoerceAtMost(1f)
                    scaleY =
                        baseScale +
                                maxDragScale *
                                abs(sin(angle) * offset.y / size.maxDimension) *
                                (height / width).fastCoerceAtMost(1f)
                },
                onDrawSurface = { drawRect(surfaceColor) }
            )
            .clip(DockShape)
            .clickable(
                enabled = enabled,
                interactionSource = null,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddClick()
                }
            )
            .then(interactiveHighlight.modifier)
            .then(if (enabled) interactiveHighlight.gestureModifier else Modifier),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            painter = painterResource(id = R.drawable.ic_nm_compose),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(if (spec.isNarrow) 20.dp else 22.dp)
        )
    }
}
