package com.han.nomemo

import android.text.Selection
import android.text.Spannable
import android.util.TypedValue
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

data class StructuredPickupInfo(
    val sectionTitle: String,
    val code: String,
    val primaryLabel: String,
    val primaryValue: String,
    val secondaryLabel: String,
    val secondaryValue: String,
    val locationText: String? = null,
    val navigationLatitude: Double? = null,
    val navigationLongitude: Double? = null
) {
    val navigationQuery: String
        get() = locationText.orEmpty()

    val hasNavigableLocation: Boolean
        get() = locationText.orEmpty().isNotBlank() ||
            (navigationLatitude != null && navigationLongitude != null)
}

private val memoryDetailPanelShape = noMemoG2RoundedShape(24.dp)
private val memoryDetailContentPanelShape = noMemoG2RoundedShape(22.dp)
private val memoryDetailContentHorizontalPadding = 18.dp
private val memoryDetailContentVerticalPadding = 17.dp
private val memoryDetailBodyFontSize = 16.sp
private val memoryDetailBodyLineHeight = 25.sp
private val memoryDetailLocationFontSize = 18.sp
private val memoryDetailLocationLineHeight = 28.sp

private fun memoryDetailActionSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    if (memoryDetailUsesThemeTint(palette, isDark)) {
        return memoryDetailContentSurface(isDark, palette)
    }
    return if (isDark) {
        noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.96f))
    } else {
        Color.White.copy(alpha = 0.985f)
    }
}

private fun memoryDetailUsesThemeTint(palette: NoMemoPalette, isDark: Boolean): Boolean {
    val defaultMid = if (isDark) Color.Black else Color(0xFFF5F5F5)
    return palette.memoBgMid != defaultMid
}

private fun memoryDetailContentSurface(isDark: Boolean, palette: NoMemoPalette): Color {
    if (!memoryDetailUsesThemeTint(palette, isDark)) {
        return noMemoCardSurfaceColor(isDark)
    }
    val themeBase = lerp(palette.memoBgMid, palette.memoBgEnd, 0.45f)
    return if (isDark) {
        lerp(themeBase, Color.White, 0.085f).copy(alpha = 0.98f)
    } else {
        lerp(Color.White, themeBase, 0.52f).copy(alpha = 0.995f)
    }
}

private fun memoryDetailActionColor(isDark: Boolean): Color {
    return if (isDark) Color(0xFF2E8BFF) else Color(0xFF1677FF)
}

@Composable
fun NoMemoDetailReanalyzeButton(
    text: String,
    processing: Boolean,
    cancelable: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCancelClick: (() -> Unit)? = null
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val actionSurface = memoryDetailActionSurface(isDark, palette)
    val actionColor = memoryDetailActionColor(isDark)
    val cancelTextColor = Color(0xFFFF5A52)
    val processingLabel = rememberDetailProcessingLabel(text, processing)
    val containerShape = noMemoG2RoundedShape(20.dp)
    if (processing && cancelable && onCancelClick != null) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(containerShape)
                    .background(actionSurface)
                    .padding(horizontal = 20.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowingProcessingText(
                    text = processingLabel,
                    color = actionColor,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .clickable(onClick = onCancelClick)
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "取消",
                        color = cancelTextColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        return
    }
    PressScaleBox(
        onClick = {
            if (!processing) {
                onClick()
            }
        },
        modifier = modifier.fillMaxWidth(),
        pressedScale = 1f
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(containerShape)
                    .background(actionSurface)
                    .padding(horizontal = 20.dp, vertical = 17.dp),
                contentAlignment = Alignment.Center
            ) {
                if (processing) {
                    FlowingProcessingText(
                        text = processingLabel,
                        color = actionColor,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = text,
                        color = actionColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberDetailProcessingLabel(text: String, processing: Boolean): String {
    if (!processing) {
        return text
    }
    return remember(text) {
        val normalized = text.replace("...", "").trim()
        when {
            normalized.contains("重试") -> "AI重试中..."
            else -> "AI分析中..."
        }
    }
}

@Composable
private fun FlowingProcessingText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign
) {
    val transition = rememberInfiniteTransition(label = "detailProcessingTextFlow")
    val flowOffset by transition.animateFloat(
        initialValue = -220f,
        targetValue = 420f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1650, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "detailProcessingTextFlowOffset"
    )
    val brush = remember(color, flowOffset) {
        Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = 0.72f),
                color.copy(alpha = 0.96f),
                Color.White.copy(alpha = 0.96f),
                color.copy(alpha = 0.96f),
                color.copy(alpha = 0.72f)
            ),
            start = Offset(flowOffset - 220f, 0f),
            end = Offset(flowOffset + 220f, 0f)
        )
    }
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            brush = brush,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = textAlign
        ),
        textAlign = textAlign
    )
}


@Composable
fun NoMemoDetailSummaryBox(
    value: String,
    editing: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = memoryDetailContentPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = memoryDetailContentSurface(isDark, palette)
        )
    ) {
        if (editing) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                maxLines = Int.MAX_VALUE,
                textStyle = TextStyle(
                    color = palette.textPrimary,
                    fontSize = memoryDetailBodyFontSize,
                    lineHeight = memoryDetailBodyLineHeight,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = memoryDetailContentHorizontalPadding,
                        vertical = memoryDetailContentVerticalPadding
                    )
            ) { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isBlank()) {
                        Text(
                            text = "输入摘要",
                            color = palette.textTertiary,
                            fontSize = memoryDetailBodyFontSize,
                            lineHeight = memoryDetailBodyLineHeight,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    innerTextField()
                }
            }
        } else {
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        setTextIsSelectable(true)
                        setTextColor(palette.textPrimary.toArgb())
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, memoryDetailBodyFontSize.value)
                        setLineSpacing(
                            (memoryDetailBodyLineHeight.value - memoryDetailBodyFontSize.value).coerceAtLeast(0f),
                            1f
                        )
                        typeface = android.graphics.Typeface.DEFAULT
                        isFocusable = false
                        isFocusableInTouchMode = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = memoryDetailContentHorizontalPadding,
                        vertical = memoryDetailContentVerticalPadding
                    ),
                update = { textView ->
                    textView.text = value
                    textView.setTextColor(palette.textPrimary.toArgb())
                    textView.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
                        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean = true
                        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
                        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                            if (item?.itemId == android.R.id.copy) {
                                textView.post {
                                    clearSelectableTextSelection(textView)
                                    mode?.finish()
                                }
                            }
                            return false
                        }

                        override fun onDestroyActionMode(mode: ActionMode?) {
                            clearSelectableTextSelection(textView)
                        }
                    })
                }
            )
        }
    }
}

private fun clearSelectableTextSelection(textView: TextView) {
    (textView.text as? Spannable)?.let { spannable ->
        Selection.removeSelection(spannable)
    }
    textView.clearFocus()
}
@Composable
fun NoMemoPickupCodeCard(
    info: StructuredPickupInfo,
    modifier: Modifier = Modifier
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = memoryDetailContentPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = memoryDetailContentSurface(isDark, palette)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = memoryDetailContentHorizontalPadding,
                    vertical = memoryDetailContentVerticalPadding
                )
        ) {
            Text(
                text = info.code,
                color = palette.textPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${info.primaryLabel}：${info.primaryValue}",
                color = palette.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${info.secondaryLabel}：${info.secondaryValue}",
                color = palette.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun NoMemoDetailTitleEditor(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = memoryDetailContentPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = memoryDetailContentSurface(isDark, palette)
        )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = palette.textPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = memoryDetailContentHorizontalPadding,
                    vertical = memoryDetailContentVerticalPadding
                )
        ) { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isBlank()) {
                    Text(
                        text = "输入标题",
                        color = palette.textTertiary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp
                    )
                }
                innerTextField()
            }
        }
    }
}

@Composable
fun NoMemoEditablePickupCodeCard(
    code: String,
    primaryLabel: String,
    primaryValue: String,
    secondaryLabel: String,
    secondaryValue: String,
    modifier: Modifier = Modifier,
    onCodeChange: (String) -> Unit,
    onPrimaryValueChange: (String) -> Unit,
    onSecondaryValueChange: (String) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = memoryDetailContentPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = memoryDetailContentSurface(isDark, palette)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = memoryDetailContentHorizontalPadding,
                    vertical = memoryDetailContentVerticalPadding
                )
        ) {
            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = palette.textPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            ) { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (code.isBlank()) {
                        Text(
                            text = "输入编码",
                            color = palette.textTertiary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            }
            Spacer(modifier = Modifier.heightIn(min = 8.dp))
            NoMemoDetailFieldEditor(
                label = primaryLabel,
                value = primaryValue,
                placeholder = "输入$primaryLabel",
                onValueChange = onPrimaryValueChange
            )
            Spacer(modifier = Modifier.heightIn(min = 8.dp))
            NoMemoDetailFieldEditor(
                label = secondaryLabel,
                value = secondaryValue,
                placeholder = "输入$secondaryLabel",
                onValueChange = onSecondaryValueChange
            )
        }
    }
}

@Composable
fun NoMemoPickupLocationCard(
    info: StructuredPickupInfo,
    modifier: Modifier = Modifier,
    onNavigate: (StructuredPickupInfo) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val locationText = remember(info.locationText, info.navigationLatitude, info.navigationLongitude) {
        val explicit = info.locationText?.trim().orEmpty()
        if (explicit.isNotBlank()) {
            explicit
        } else if (info.navigationLatitude != null && info.navigationLongitude != null) {
            String.format(
                java.util.Locale.getDefault(),
                "图片定位 %.6f, %.6f",
                info.navigationLatitude,
                info.navigationLongitude
            )
        } else {
            ""
        }
    }
    if (locationText.isBlank()) return
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = memoryDetailContentPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = memoryDetailContentSurface(isDark, palette)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = memoryDetailContentHorizontalPadding,
                    vertical = memoryDetailContentVerticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = locationText,
                color = palette.textPrimary,
                fontSize = memoryDetailLocationFontSize,
                lineHeight = memoryDetailLocationLineHeight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (info.hasNavigableLocation) {
                Spacer(modifier = Modifier.size(12.dp))
                NoMemoLocationNavigateButton(
                    text = "导航",
                    onClick = { onNavigate(info) }
                )
            }
        }
    }
}

@Composable
fun NoMemoEditablePickupLocationCard(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = memoryDetailContentPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = memoryDetailContentSurface(isDark, palette)
        )
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            minLines = 1,
            maxLines = Int.MAX_VALUE,
            textStyle = TextStyle(
                color = palette.textPrimary,
                fontSize = memoryDetailLocationFontSize,
                lineHeight = memoryDetailLocationLineHeight,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = memoryDetailContentHorizontalPadding,
                    vertical = memoryDetailContentVerticalPadding
                )
        ) { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isBlank()) {
                    Text(
                        text = "输入地点",
                        color = palette.textTertiary,
                        fontSize = memoryDetailLocationFontSize,
                        lineHeight = memoryDetailLocationLineHeight,
                        fontWeight = FontWeight.Bold
                    )
                }
                innerTextField()
            }
        }
    }
}

@Composable
private fun NoMemoLocationNavigateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val actionColor = memoryDetailActionColor(isDark)
    PressScaleBox(
        onClick = onClick,
        modifier = modifier,
        pressedScale = 1f
    ) {
        Text(
            text = text,
            color = actionColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun NoMemoDetailFieldEditor(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    val palette = rememberNoMemoPalette()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = palette.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.heightIn(min = 6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            minLines = 1,
            maxLines = Int.MAX_VALUE,
            textStyle = TextStyle(
                color = palette.textPrimary,
                fontSize = memoryDetailBodyFontSize,
                lineHeight = memoryDetailBodyLineHeight,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth()
        ) { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        color = palette.textTertiary,
                        fontSize = memoryDetailBodyFontSize,
                        lineHeight = memoryDetailBodyLineHeight,
                        fontWeight = FontWeight.Normal
                    )
                }
                innerTextField()
            }
        }
    }
}

@Composable
fun NoMemoDetailActionButton(
    text: String,
    primary: Boolean,
    showBorder: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val themedSurface = memoryDetailContentSurface(isDark, palette)
    val usesThemeTint = memoryDetailUsesThemeTint(palette, isDark)
    val containerColor = when {
        primary -> palette.accent
        showBorder -> if (usesThemeTint) themedSurface else palette.glassFill
        else -> if (usesThemeTint) themedSurface else noMemoCardSurfaceColor(isDark, Color.White.copy(alpha = 0.995f))
    }
    val contentColor = when {
        primary -> palette.onAccent
        else -> palette.textPrimary
    }
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Card(
            shape = noMemoG2RoundedShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            ),
            border = if (showBorder) BorderStroke(1.dp, if (primary) palette.accent else palette.glassStroke) else null
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
