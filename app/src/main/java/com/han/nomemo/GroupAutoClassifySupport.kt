package com.han.nomemo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun GroupAutoClassifyToggleRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val accent = if (isDark) Color(0xFF2E8BFF) else Color(0xFF1677FF)
    val uncheckedBorderColor = palette.textTertiary.copy(alpha = 0.36f)
    val ringColor by animateColorAsState(
        targetValue = if (checked) accent else uncheckedBorderColor,
        animationSpec = tween(durationMillis = 180),
        label = "groupAutoClassifyRing"
    )
    val fillColor by animateColorAsState(
        targetValue = if (checked) accent else Color.Transparent,
        animationSpec = tween(durationMillis = 180),
        label = "groupAutoClassifyFill"
    )
    val rowBackground by animateColorAsState(
        targetValue = if (pressed) {
            if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.035f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 140),
        label = "groupAutoClassifyRowBg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(noMemoG2RoundedShape(18.dp))
            .background(rowBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(horizontal = 2.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(fillColor, CircleShape)
                .border(
                    width = if (checked) 1.8.dp else 2.2.dp,
                    color = ringColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sheet_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "整理历史记忆",
            color = palette.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
