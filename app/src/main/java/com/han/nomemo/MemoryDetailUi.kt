package com.han.nomemo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
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

private val memoryDetailPanelShape = RoundedCornerShape(24.dp)

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
            shape = memoryDetailPanelShape,
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
        shape = memoryDetailPanelShape,
        colors = CardDefaults.cardColors(
            containerColor = noMemoCardSurfaceColor(isDark)
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
            containerColor = noMemoCardSurfaceColor(isDark)
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
    val supportingLocationText = buildPickupLocationSupportingText(info)
    val hasSupportingText = !supportingLocationText.isNullOrBlank()
    val hasNavigation = info.navigationQuery.isNotBlank()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = noMemoCardSurfaceColor(isDark)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                val textAreaModifier = Modifier
                    .fillMaxWidth()
                    .padding(end = if (hasNavigation) 98.dp else 0.dp)
                if (hasSupportingText) {
                    Column(
                        modifier = textAreaModifier,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = info.locationTitle ?: info.addressDetail.orEmpty(),
                            color = palette.textPrimary,
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        supportingLocationText?.let {
                            Text(
                                text = it,
                                color = palette.textSecondary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = textAreaModifier.heightIn(min = 40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = info.locationTitle ?: info.addressDetail.orEmpty(),
                            color = palette.textPrimary,
                            fontSize = 17.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                if (hasNavigation) {
                    NoMemoLocationNavigateButton(
                        text = "瀵艰埅",
                        onClick = { onNavigate(info.navigationQuery) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}

private fun buildPickupLocationSupportingText(info: StructuredPickupInfo): String? {
    val detail = info.addressDetail
        ?.trim()
        ?.takeIf { it.isNotBlank() && it != info.locationTitle }
        ?: return null

    val title = info.locationTitle?.trim().orEmpty()
    if (title.isNotBlank() && detail.startsWith(title)) {
        return detail
            .removePrefix(title)
            .trimStart('：', ':', '，', ',', '-', ' ')
            .removeSurrounding("：", "：")
            .removeSurrounding("(", ")")
            .trim()
            .takeIf { it.isNotBlank() }
    }

    return detail
}

@Composable
private fun NoMemoLocationNavigateButton(
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
            colors = CardDefaults.cardColors(containerColor = palette.accent),
            border = BorderStroke(1.dp, palette.accent)
        ) {
            Text(
                text = text,
                color = palette.onAccent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp)
            )
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
    val containerColor = when {
        primary -> palette.accent
        showBorder -> palette.glassFill
        else -> noMemoCardSurfaceColor(isDark, Color.White.copy(alpha = 0.995f))
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
            shape = RoundedCornerShape(18.dp),
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

