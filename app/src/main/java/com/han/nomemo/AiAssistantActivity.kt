package com.han.nomemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

class AiAssistantActivity : BaseComposeActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AiAssistantActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiAssistantContent(onClose = { finish() })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiAssistantContent(onClose: () -> Unit) {
    val context = LocalContext.current
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val scrollState = rememberScrollState()

    val quickActions = listOf(
        "帮我归档过期的记忆",
        "帮我找一下购物记录",
        "查看我的快递",
        "查看所有购物相关"
    )

    val accent = palette.accent
    val contentSurface = noMemoThemeSyncedContentSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = palette.glassFill.copy(alpha = 0.96f),
        lightDefault = Color.White,
        darkLift = 0.095f,
        lightMix = 0.24f,
        darkAlpha = 0.96f,
        lightAlpha = 1f
    )
    val insetSurface = noMemoThemeSyncedInsetSurface(
        palette = palette,
        isDark = isDark,
        darkDefault = Color.White.copy(alpha = 0.08f),
        lightDefault = Color(0xFFE9EBF0),
        darkLift = 0.15f,
        lightMix = 0.62f,
        darkAlpha = 0.72f,
        lightAlpha = 0.96f
    )
    val subtleStroke = if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        palette.glassStroke
    }
    val density = LocalDensity.current
    val keyboardLiftTargetPx = (
        WindowInsets.ime.getBottom(density) - WindowInsets.navigationBars.getBottom(density)
    ).coerceAtLeast(0).toFloat()
    val keyboardLiftPx = keyboardLiftTargetPx
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {
        }
        val copiedUriString = try {
            ImageUtils.copyUriToCache(context, uri)
        } catch (_: Exception) {
            null
        }
        selectedImageUri = if (copiedUriString != null) {
            Uri.parse(copiedUriString)
        } else {
            uri
        }
    }

    NoMemoBackground {
        ResponsiveContentFrame { spec ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        start = spec.pageHorizontalPadding,
                        top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                        end = spec.pageHorizontalPadding
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spec.topActionButtonSize)
                    ) {
                        GlassIconCircleButton(
                            iconRes = R.drawable.ic_sheet_back,
                            contentDescription = "返回",
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.CenterStart),
                            size = spec.topActionButtonSize
                        )
                        Text(
                            text = "AI 助手",
                            color = palette.textPrimary,
                            fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        GlassIconCircleButton(
                            iconRes = R.drawable.ic_nm_delete,
                            contentDescription = "清空",
                            onClick = {
                                inputText = ""
                                selectedImageUri = null
                            },
                            modifier = Modifier.align(Alignment.CenterEnd),
                            size = spec.topActionButtonSize
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(if (spec.isNarrow) 44.dp else 58.dp))

                        Box(
                            modifier = Modifier
                                .size(if (spec.isNarrow) 78.dp else 88.dp)
                                .clip(CircleShape)
                                .background(contentSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_ai_assistant),
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(if (spec.isNarrow) 40.dp else 44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = "你好，我是 AI 助手",
                            color = palette.textPrimary,
                            fontSize = if (spec.isNarrow) 22.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "可以帮你查找、整理、归纳记忆",
                            color = palette.textSecondary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        FlowRow(
                            modifier = Modifier
                                .widthIn(max = 420.dp)
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            quickActions.forEach { action ->
                                QuickActionChip(
                                    text = action,
                                    palette = palette,
                                    onClick = { inputText = action }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(124.dp))
                    }
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp)
                        .graphicsLayer {
                            translationY = -keyboardLiftPx
                        },
                    shape = noMemoG2RoundedShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = contentSurface),
                    border = BorderStroke(1.dp, subtleStroke)
                ) {
                    selectedImageUri?.let { imageUri ->
                        AssistantImageAttachmentPreview(
                            imageUri = imageUri,
                            palette = palette,
                            isDark = isDark,
                            surfaceColor = insetSurface,
                            onRemove = { selectedImageUri = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, top = 12.dp, end = 12.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(insetSurface)
                                .clickable {
                                    imagePickerLauncher.launch(arrayOf("image/*"))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_image),
                                contentDescription = "添加图片",
                                tint = palette.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                color = palette.textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 22.sp
                            ),
                            cursorBrush = SolidColor(accent),
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 38.dp, max = 112.dp)
                                .padding(horizontal = 12.dp)
                        ) { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (inputText.isBlank()) {
                                    Text(
                                        text = "输入消息...",
                                        color = palette.textTertiary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                innerTextField()
                            }
                        }

                        val sendEnabled = inputText.isNotBlank() || selectedImageUri != null
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (sendEnabled) accent else insetSurface)
                                .clickable(enabled = sendEnabled) {
                                    Toast.makeText(context, "AI 助手功能正在接入中", Toast.LENGTH_SHORT).show()
                                    inputText = ""
                                    selectedImageUri = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_send),
                                contentDescription = "发送",
                                tint = if (sendEnabled) palette.onAccent else palette.textTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssistantImageAttachmentPreview(
    imageUri: Uri,
    palette: NoMemoPalette,
    isDark: Boolean,
    surfaceColor: Color,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 82.dp, height = 62.dp)
                .clip(noMemoG2RoundedShape(18.dp))
                .background(surfaceColor)
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "已选择图片",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.Black.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.92f))
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sheet_close),
                    contentDescription = "移除图片",
                    tint = if (isDark) Color.White.copy(alpha = 0.94f) else Color.Black.copy(alpha = 0.68f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "图片已添加",
                color = palette.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "将随消息一起发送",
                color = palette.textTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickActionChip(
    text: String,
    palette: NoMemoPalette,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    PressScaleBox(
        onClick = onClick,
        pressedScale = 0.985f
    ) {
        Box(
            modifier = Modifier
                .clip(NoMemoG2CapsuleShape)
                .background(noMemoThemeSyncedChipBackground(palette, isDark, selected = false))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = text,
                color = noMemoThemeSyncedChipTextColor(palette, selected = false),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
