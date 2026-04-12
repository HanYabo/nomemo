package com.han.nomemo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
    modifier: Modifier = Modifier,
    spec: NoMemoAdaptiveSpec = rememberNoMemoAdaptiveSpec(),
    sharedBackdrop: Backdrop? = null
) {
    val palette = rememberNoMemoPalette()
    val haptic = LocalHapticFeedback.current
    val tabs = remember(onOpenMemory, onOpenGroup, onOpenReminder) {
        listOf(
            DockTabSpec(NoMemoDockTab.MEMORY, R.drawable.ic_nm_memory, "", onOpenMemory),
            DockTabSpec(NoMemoDockTab.GROUP, R.drawable.ic_nm_group, "", onOpenGroup),
            DockTabSpec(NoMemoDockTab.REMINDER, R.drawable.ic_nm_reminder, "", onOpenReminder)
        )
    }
    val dockHeight = if (spec.isNarrow) 60.dp else 64.dp
    val buttonSize = dockHeight
    val dockWidth = if (spec.isNarrow) 266.dp else 292.dp
    val backdrop = sharedBackdrop ?: rememberLayerBackdrop { drawContent() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dockHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LiquidGlassDockTabs(
            selectedTab = selectedTab,
            tabs = tabs.mapIndexed { index, item ->
                item.copy(
                    label = when (index) {
                        0 -> stringResource(R.string.nav_memory)
                        1 -> stringResource(R.string.nav_group)
                        else -> stringResource(R.string.nav_reminder)
                    }
                )
            },
            dockWidth = dockWidth,
            dockHeight = dockHeight,
            backdrop = backdrop,
            palette = palette,
            haptic = haptic
        )

        LiquidGlassAddButton(
            onAddClick = onAddClick,
            buttonSize = buttonSize,
            spec = spec,
            backdrop = backdrop,
            haptic = haptic
        )
    }
}

@Composable
private fun LiquidGlassDockTabs(
    selectedTab: NoMemoDockTab,
    tabs: List<DockTabSpec>,
    dockWidth: Dp,
    dockHeight: Dp,
    backdrop: Backdrop,
    palette: NoMemoPalette,
    haptic: HapticFeedback
) {
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor = if (isLightTheme) Color(0xFF0088FF) else Color(0xFF0091FF)
    val containerColor = if (isLightTheme) Color(0xFFFAFAFA).copy(alpha = 0.40f) else Color(0xFF121212).copy(alpha = 0.40f)
    val baseColor = if (isLightTheme) Color(0xFF5B6A7D) else palette.textPrimary.copy(alpha = 0.64f)
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
        val tabWidth = with(density) {
            (dockWidthPx - 8.dp.toPx()) / tabs.size
        }

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
        var currentIndex by remember(selectedTab, tabs) {
            mutableIntStateOf(selectedIndex)
        }

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
        LaunchedEffect(selectedIndex) {
            currentIndex = selectedIndex
        }

        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { currentIndex }
                .drop(1)
                .collectLatest { index ->
                    dampedDragAnimation.animateToValue(index.toFloat())
                    if (index != selectedIndex) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        tabs[index].onClick()
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                LiquidGlassDockItem(
                    iconRes = tab.iconRes,
                    label = tab.label,
                    contentColor = baseColor,
                    onClick = { currentIndex = index }
                )
            }
        }

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
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    LiquidGlassDockItem(
                        iconRes = tab.iconRes,
                        label = tab.label,
                        contentColor = accentColor,
                        enabled = false,
                        onClick = {}
                    )
                }
            }
        }

        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    translationX = if (isLtr) {
                        dampedDragAnimation.value * tabWidth + panelOffset
                    } else {
                        size.width - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
                    }
                }
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
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
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 10f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(
                            if (isLightTheme) Color.Black.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.10f),
                            alpha = 1f - progress
                        )
                        drawRect(
                            if (isLightTheme) Color.Black.copy(alpha = 0.03f * progress) else Color.Black.copy(alpha = 0.05f * progress)
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
            modifier = Modifier.size(19.dp)
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
    val surfaceColor = if (isDark) Color(0xFF121212).copy(alpha = 0.40f) else Color(0xFFFAFAFA).copy(alpha = 0.40f)
    val iconTint = if (isDark) Color.White.copy(alpha = 0.96f) else Color(0xFF253244).copy(alpha = 0.92f)

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
                interactionSource = null,
                indication = null,
                role = Role.Button,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddClick()
                }
            )
            .then(interactiveHighlight.modifier)
            .then(interactiveHighlight.gestureModifier),
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
