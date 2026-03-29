package com.han.nomemo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StructuredPickupInfo(
    val sectionTitle: String,
    val code: String,
    val company: String?,
    val locationTitle: String?,
    val addressDetail: String?,
    val secondaryCodeLabel: String?,
    val secondaryCodeValue: String?,
    val status: String?
) {
    val navigationQuery: String
        get() = (addressDetail ?: locationTitle).orEmpty()
}

@Composable
fun NoMemoDetailReanalyzeButton(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = {
            if (enabled) {
                onClick()
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (enabled) palette.accent else palette.glassFill
            ),
            border = BorderStroke(
                1.dp,
                if (enabled) palette.accent else palette.glassStroke
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (enabled) palette.onAccent else palette.textSecondary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1A1A1D) else Color.White
        )
    ) {
        if (editing) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = palette.textPrimary,
                    fontSize = 16.sp,
                    lineHeight = 25.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .heightIn(min = 120.dp)
            ) { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isBlank()) {
                        Text(
                            text = "输入摘要",
                            color = palette.textTertiary,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        } else {
            Text(
                text = value,
                color = palette.textPrimary,
                fontSize = 16.sp,
                lineHeight = 25.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1A1A1D) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = info.code,
                color = palette.textPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            info.company?.let {
                Text(
                    text = "${if (info.sectionTitle == "取件码") "快递公司" else "商家"}：$it",
                    color = palette.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            if (!info.secondaryCodeLabel.isNullOrBlank() && !info.secondaryCodeValue.isNullOrBlank()) {
                Text(
                    text = "${info.secondaryCodeLabel}：${info.secondaryCodeValue}",
                    color = palette.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            info.status?.let {
                Text(
                    text = "状态：$it",
                    color = palette.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            (info.addressDetail ?: info.locationTitle)?.let {
                Text(
                    text = "${if (info.sectionTitle == "取件码") "取件地址" else "取餐地点"}：$it",
                    color = palette.textSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun NoMemoPickupLocationCard(
    info: StructuredPickupInfo,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    val palette = rememberNoMemoPalette()
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1A1A1D) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = info.locationTitle ?: info.addressDetail.orEmpty(),
                    color = palette.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (info.navigationQuery.isNotBlank()) {
                    Text(
                        text = "导航",
                        color = palette.accent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate(info.navigationQuery) }
                    )
                }
            }
            info.addressDetail
                ?.takeIf { it.isNotBlank() && it != info.locationTitle }
                ?.let {
                    Text(
                        text = "地址：$it",
                        color = palette.textSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
        }
    }
}

@Composable
fun NoMemoDetailActionButton(
    text: String,
    primary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = rememberNoMemoPalette()
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (primary) palette.accent else palette.glassFill
            ),
            border = BorderStroke(1.dp, if (primary) palette.accent else palette.glassStroke)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = if (primary) palette.onAccent else palette.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
