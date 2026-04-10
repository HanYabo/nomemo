package com.han.nomemo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import com.kyant.backdrop.Backdrop
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

private val DockShape = ContinuousCapsule(G2Continuity())

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
    val dockHeight = if (spec.isNarrow) 64.dp else 68.dp
    val addButtonSize = dockHeight
    val dockWidth = if (spec.isNarrow) 266.dp else 292.dp

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
            backdrop = sharedBackdrop,
            palette = palette,
            haptic = haptic
        )

        LiquidGlassAddButton(
            onAddClick = onAddClick,
            buttonSize = addButtonSize,
            spec = spec,
            palette = palette,
            backdrop = sharedBackdrop,
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
    backdrop: Backdrop?,
    palette: NoMemoPalette,
    haptic: HapticFeedback
) {
    val isDark = isSystemInDarkTheme()
    val isLightTheme = !isDark
    val density = LocalDensity.current
    val tabBaseColor = if (isDark) {
        palette.textPrimary.copy(alpha = 0.64f)
    } else {
        Color(0xFF5B6A7D)
    }
    val accentColor = if (isDark) {
        Color(0xFFB8CAFF)
    } else {
        Color(0xFF7D93D8)
    }
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.18f)
    } else {
        Color.Black.copy(alpha = 0.10f)
    }
    val horizontalPadding = 4.dp
    val verticalPadding = 4.dp

    BoxWithConstraints(
        modifier = Modifier
            .width(dockWidth)
            .height(dockHeight)
    ) {
        // 计算实际内容区域宽度（减去左右 padding）
        val contentWidthPx = constraints.maxWidth - with(density) { horizontalPadding.toPx() * 2 }
        val tabWidth = contentWidthPx.toFloat() / tabs.size
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density, constraints.maxWidth) {
            derivedStateOf {
                val fraction = (offsetAnimation.value / constraints.maxWidth).fastCoerceIn(-1f, 1f)
                with(density) {
                    4.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                }
            }
        }
        val selectedIndex = remember(selectedTab, tabs) {
            tabs.indexOfFirst { it.tab == selectedTab }.coerceAtLeast(0)
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
                    animateToValue(targetIndex.toFloat())
                    animationScope.launch {
                        offsetAnimation.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 400f))
                    }
                    if (targetIndex != selectedIndex) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        tabs[targetIndex].onClick()
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
            dampedDragAnimation.animateToValue(selectedIndex.toFloat())
        }
        val focusedIndex by remember {
            derivedStateOf {
                dampedDragAnimation.value.fastRoundToInt().coerceIn(0, tabs.lastIndex)
            }
        }
        var pendingClickJob by remember { mutableStateOf<Job?>(null) }

        val interactiveHighlight = remember(animationScope) {
            LiquidGlassInteractiveHighlight(
                animationScope = animationScope,
                position = { size, _ ->
                    androidx.compose.ui.geometry.Offset(
                        if (isLtr) (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset
                        else size.width - (dampedDragAnimation.value + 0.5f) * tabWidth + panelOffset,
                        size.height / 2f
                    )
                }
            )
        }

        // 底层：整个 Dock 的背景 - 添加 Q 弹回缩动画
        val dockShrinkProgress by remember {
            derivedStateOf {
                dampedDragAnimation.pressProgress
            }
        }
        val dockScaleX by animateFloatAsState(
            targetValue = 1f - dockShrinkProgress * 0.04f,
            animationSpec = spring(dampingRatio = 0.4f, stiffness = 300f),
            label = "dockScaleX"
        )
        val dockScaleY by animateFloatAsState(
            targetValue = 1f - dockShrinkProgress * 0.1f,
            animationSpec = spring(dampingRatio = 0.32f, stiffness = 280f),
            label = "dockScaleY"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = dockScaleX
                    scaleY = dockScaleY
                }
                .drawBackdrop(
                    backdrop = backdrop ?: rememberLayerBackdrop { drawContent() },
                    shape = { DockShape },
                    effects = {
                        vibrancy()
                        blur(8.dp.toPx())
                        lens(24.dp.toPx(), 24.dp.toPx())
                    },
                    onDrawSurface = {}
                )
                .clip(DockShape)
                .border(1.dp, borderColor, DockShape)
        )

        // 第二层：滑动的选中高亮背景（slider）- 放在外层以允许透镜效果超出 dock 边界
        // 计算透镜边界限制
        val sliderPositionX = if (isLtr) {
            dampedDragAnimation.value * tabWidth + panelOffset
        } else {
            constraints.maxWidth - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
        }
        val sliderWidthPx = tabWidth
        val pressProgress = dampedDragAnimation.pressProgress
        // 透镜半径：基础值 + 按压增强，空背景下更明显
        val baseLensRadius = with(density) { 16.dp.toPx() }
        val pressLensBoost = with(density) { 8.dp.toPx() } * pressProgress
        val lensRadius = baseLensRadius + pressLensBoost
        // 限制透镜不超出屏幕边界
        val leftBound = with(density) { horizontalPadding.toPx() }
        val rightBound = constraints.maxWidth - with(density) { horizontalPadding.toPx() }
        val sliderLeft = sliderPositionX + leftBound
        val sliderRight = sliderPositionX + leftBound + sliderWidthPx
        val lensLeft = (lensRadius - sliderLeft).coerceAtLeast(0f)
        val lensRight = (lensRadius - (rightBound - sliderRight)).coerceAtLeast(0f)
        val effectiveLensX = (lensRadius - lensLeft - lensRight).coerceAtLeast(with(density) { 4.dp.toPx() })

        Box(
            modifier = Modifier
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = sliderPositionX
                    }
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(backdrop ?: rememberLayerBackdrop { drawContent() }, rememberLayerBackdrop()),
                        shape = { DockShape },
                        effects = {
                            vibrancy()
                            // 模糊：按压时减少，让透镜更清晰
                            blur(6.dp.toPx() * (1f - pressProgress * 0.5f))
                            lens(
                                // 水平透镜：基础 + 按压增强
                                with(density) { 12.dp.toPx() } + with(density) { 6.dp.toPx() } * pressProgress,
                                effectiveLensX,
                                chromaticAberration = true
                            )
                        },
                        highlight = {
                            // 高光：按压时增强，空背景下更明显
                            val baseAlpha = if (isLightTheme) 0.68f else 0.52f
                            val pressBoost = 0.15f * pressProgress
                            Highlight.Default.copy(alpha = baseAlpha + pressBoost)
                        },
                        shadow = {
                            Shadow(
                                radius = 6.dp + 2.dp * pressProgress,
                                color = if (isDark) Color.Black.copy(alpha = 0.18f + 0.08f * pressProgress) else Color.Black.copy(alpha = 0.08f + 0.04f * pressProgress)
                            )
                        },
                        innerShadow = {
                            InnerShadow(
                                radius = 6.dp + 4.dp * pressProgress,
                                alpha = if (isDark) 0.38f + 0.12f * pressProgress else 0.25f + 0.1f * pressProgress
                            )
                        },
                        layerBlock = {
                            scaleX = dampedDragAnimation.scaleX
                            scaleY = dampedDragAnimation.scaleY
                            val velocity = dampedDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        },
                        onDrawSurface = {}
                    )
                    .height(dockHeight - verticalPadding * 2)
                    .width(with(density) { tabWidth.toDp() })
            )
        }

        // 最上层：导航项（icon + 文字）+ 手势处理
        Row(
            modifier = Modifier
                .fillMaxSize()
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val selectionFraction by remember(index) {
                    derivedStateOf {
                        (1f - abs(dampedDragAnimation.value - index)).coerceIn(0f, 1f)
                    }
                }
                LiquidGlassDockItem(
                    iconRes = tab.iconRes,
                    label = tab.label,
                    baseColor = tabBaseColor,
                    selectedColor = accentColor,
                    selectionFraction = selectionFraction,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        pendingClickJob?.cancel()
                        if (index == selectedIndex) {
                            tab.onClick()
                        } else {
                            pendingClickJob = animationScope.launch {
                                dampedDragAnimation.animateToValue(index.toFloat())
                                delay(150)
                                tab.onClick()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.LiquidGlassDockItem(
    iconRes: Int,
    label: String,
    baseColor: Color,
    selectedColor: Color,
    selectionFraction: Float,
    onClick: () -> Unit
) {
    val contentColor by animateColorAsState(
        targetValue = lerp(baseColor, selectedColor, selectionFraction),
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "dockItemColor"
    )
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
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
    palette: NoMemoPalette,
    backdrop: Backdrop?,
    haptic: HapticFeedback
) {
    val isDark = isSystemInDarkTheme()
    val animationScope = rememberCoroutineScope()

    val interactiveHighlight = remember(animationScope) {
        LiquidGlassInteractiveHighlight(animationScope = animationScope)
    }

    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.18f)
    } else {
        Color.Black.copy(alpha = 0.10f)
    }
    val iconTint = if (isDark) {
        Color.White.copy(alpha = 0.96f)
    } else {
        Color(0xFF253244).copy(alpha = 0.92f)
    }

    Box(
        modifier = Modifier
            .size(buttonSize)
            .drawBackdrop(
                backdrop = backdrop ?: rememberLayerBackdrop { drawContent() },
                shape = { DockShape },
                effects = {
                    vibrancy()
                    blur(4.dp.toPx())
                    lens(12.dp.toPx(), 16.dp.toPx())
                },
                highlight = {
                    Highlight.Default.copy(alpha = if (isDark) 0.28f else 0.18f)
                },
                shadow = {
                    Shadow(
                        radius = 6.dp,
                        color = if (isDark) Color.Black.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.05f)
                    )
                },
                innerShadow = {
                    InnerShadow(
                        radius = 4.dp,
                        alpha = if (isDark) 0.18f else 0.12f
                    )
                },
                layerBlock = {
                    val progress = interactiveHighlight.pressProgress
                    // 基础缩放
                    val baseScale = 1f + 0.06f * progress
                    // 拖拽位移
                    val maxOffset = size.minDimension
                    val initialDerivative = 0.08f
                    val dragOffset = interactiveHighlight.offset
                    translationX = maxOffset * kotlin.math.tanh(initialDerivative * dragOffset.x / maxOffset)
                    translationY = maxOffset * kotlin.math.tanh(initialDerivative * dragOffset.y / maxOffset)

                    // 根据拖拽方向的形变
                    val maxDragScale = 0.04f
                    val offsetAngle = kotlin.math.atan2(dragOffset.y, dragOffset.x)
                    scaleX = baseScale + maxDragScale * kotlin.math.abs(kotlin.math.cos(offsetAngle)) * kotlin.math.abs(dragOffset.x) / size.width
                    scaleY = baseScale + maxDragScale * kotlin.math.abs(kotlin.math.sin(offsetAngle)) * kotlin.math.abs(dragOffset.y) / size.height
                },
                onDrawSurface = {}
            )
            .clip(DockShape)
            .border(1.dp, borderColor, DockShape)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddClick()
                }
            )
            .then(interactiveHighlight.modifier)
            .then(interactiveHighlight.gestureModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_nm_compose),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(if (spec.isNarrow) 22.dp else 24.dp)
        )
    }
}
