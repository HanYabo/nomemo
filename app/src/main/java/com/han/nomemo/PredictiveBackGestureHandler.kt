package com.han.nomemo

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PredictiveBackGestureHandler(
    enabled: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (!enabled) {
        Box(modifier = modifier) { content() }
        return
    }

    val density = LocalDensity.current
    val progress = remember { Animatable(0f) }
    var swipeEdge by remember { mutableFloatStateOf(0f) }
    val cornerRadiusPx = with(density) { 24.dp.toPx() }
    val exitEasing = CubicBezierEasing(0.4f, 0f, 1f, 1f)

    val context = LocalContext.current
    val activity = context as? Activity

    PredictiveBackHandler(enabled = true) { backEvents ->
        val originalBackground = activity?.window?.decorView?.background
        activity?.window?.decorView?.setBackgroundColor(AndroidColor.TRANSPARENT)
        try {
            backEvents.collectLatest { event ->
                swipeEdge = if (event.swipeEdge == BackEventCompat.EDGE_LEFT) 1f else -1f
                progress.snapTo(event.progress)
            }
        } finally {
            val currentProgress = progress.value
            if (currentProgress > 0.4f) {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = exitEasing
                    )
                )
                onBack()
            } else {
                swipeEdge = 0f
                progress.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = 600f
                    )
                )
            }
            activity?.window?.decorView?.background = originalBackground
        }
    }

    val currentProgress = progress.value
    val scale = 1f - currentProgress * 0.06f
    val translationX = currentProgress * swipeEdge * 0.15f

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.translationX = translationX * size.width
                }
                .drawWithContent {
                    val radius = currentProgress * cornerRadiusPx
                    if (radius > 0f) {
                        val clipPath = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(0f, 0f, size.width, size.height),
                                    cornerRadius = CornerRadius(radius, radius)
                                )
                            )
                        }
                        drawContext.canvas.save()
                        drawContext.canvas.clipPath(clipPath)
                        drawContent()
                        drawContext.canvas.restore()
                    } else {
                        drawContent()
                    }
                }
        ) {
            content()
        }
    }
}
