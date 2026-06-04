package com.han.nomemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.lifecycle.ViewModelProvider
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiAssistantActivity : BaseComposeActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AiAssistantActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            AiAssistantViewModel.Factory(application)
        )[AiAssistantViewModel::class.java]
        setContent {
            AiAssistantContent(
                viewModel = viewModel,
                onClose = { finish() },
                onOpenMemory = { recordId ->
                    startActivity(MemoryDetailActivity.createIntent(this, recordId))
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiAssistantContent(
    viewModel: AiAssistantViewModel,
    onClose: () -> Unit,
    onOpenMemory: (String) -> Unit
) {
    val context = LocalContext.current
    val palette = rememberNoMemoPalette()
    val isDark = isSystemInDarkTheme()
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()

    val quickActions = listOf(
        "帮我归档过期的记忆",
        "帮我找一下购物记录",
        "查看我的快递",
        "查看所有购物相关"
    )

    val accent = Color(0xFF202435)
    val assistantTextPrimary = Color(0xFF171A2C)
    val assistantTextSecondary = Color(0xFF77849A)
    val assistantIconTint = Color(0xFF4E5B70)
    val contentSurface = Color.White.copy(alpha = 0.86f)
    val insetSurface = Color(0xFFF7F8FC).copy(alpha = 0.96f)
    val subtleStroke = Color.White.copy(alpha = 0.68f)
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
        selectedImageUri = copiedUriString?.let(Uri::parse) ?: uri
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AiAssistantUiEvent.OpenMemory -> onOpenMemory(event.recordId)
            }
        }
    }

    LaunchedEffect(uiState.messages.size, uiState.isSending) {
        val itemCount = uiState.messages.size + if (uiState.isSending) 1 else 0
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }

    NoMemoBackground {
        ResponsiveContentFrame { spec ->
            AssistantScreenBackground {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(
                            start = if (spec.isNarrow) 28.dp else 34.dp,
                            top = 28.dp,
                            end = if (spec.isNarrow) 28.dp else 34.dp
                        )
                ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                    ) {
                        AssistantTopCircleButton(
                            iconRes = R.drawable.ic_sheet_back,
                            contentDescription = "返回",
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.CenterStart),
                            tint = assistantIconTint
                        )
                        Text(
                            text = "AI 助手",
                            color = assistantTextPrimary,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        AssistantTopCircleButton(
                            iconRes = R.drawable.ic_nm_delete,
                            contentDescription = "清空输入",
                            onClick = {
                                inputText = ""
                                selectedImageUri = null
                            },
                            modifier = Modifier.align(Alignment.CenterEnd),
                            tint = assistantIconTint
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (uiState.messages.isEmpty() && !uiState.isSending) {
                            AssistantWelcome(
                                quickActions = quickActions,
                                textPrimary = assistantTextPrimary,
                                textSecondary = assistantTextSecondary,
                                iconTint = assistantTextPrimary,
                                isNarrow = spec.isNarrow,
                                onQuickAction = { inputText = it }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listState,
                                contentPadding = PaddingValues(top = 18.dp, bottom = 156.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(uiState.messages, key = { it.id }) { message ->
                                    AssistantMessageItem(
                                        message = message,
                                        palette = palette,
                                        isDark = isDark,
                                        assistantSurface = contentSurface,
                                        userSurface = accent,
                                        subtleStroke = subtleStroke,
                                        onOpenMemory = { viewModel.openMemory(it) },
                                        onConfirm = { viewModel.confirmOperation(it) },
                                        onCancel = { viewModel.cancelOperation(it) }
                                    )
                                }
                                if (uiState.isSending) {
                                    item(key = "assistant_loading") {
                                        AssistantTypingBubble(
                                            palette = palette,
                                            surfaceColor = contentSurface,
                                            subtleStroke = subtleStroke
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            start = if (spec.isNarrow) 10.dp else 18.dp,
                            end = if (spec.isNarrow) 10.dp else 18.dp,
                            bottom = 26.dp
                        )
                        .graphicsLayer {
                            translationY = -keyboardLiftPx
                        },
                    shape = NoMemoG2CapsuleShape,
                    colors = CardDefaults.cardColors(containerColor = contentSurface),
                    border = BorderStroke(1.dp, subtleStroke),
                    elevation = CardDefaults.cardElevation(defaultElevation = 18.dp)
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
                                .heightIn(min = 74.dp)
                                .padding(start = 18.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .clickable(enabled = !uiState.isSending) {
                                    imagePickerLauncher.launch(arrayOf("image/*"))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_image),
                                contentDescription = "添加图片",
                                tint = assistantTextSecondary,
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                color = assistantTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 22.sp
                            ),
                            cursorBrush = SolidColor(accent),
                            maxLines = 4,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 46.dp, max = 112.dp)
                                .padding(horizontal = 12.dp)
                        ) { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (inputText.isBlank()) {
                                    Text(
                                        text = "输入消息...",
                                        color = Color(0xFFA9B3C2),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                innerTextField()
                            }
                        }

                        val sendEnabled = !uiState.isSending &&
                            (inputText.isNotBlank() || selectedImageUri != null)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(
                                    elevation = 14.dp,
                                    shape = CircleShape,
                                    clip = false,
                                    ambientColor = Color(0x22000000),
                                    spotColor = Color(0x22000000)
                                )
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.98f))
                                .clickable(enabled = sendEnabled) {
                                    viewModel.sendMessage(inputText, selectedImageUri)
                                    inputText = ""
                                    selectedImageUri = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_nm_send),
                                contentDescription = "发送",
                                tint = if (sendEnabled) accent else Color(0xFFA4AEC0),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun AssistantScreenBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEAF3FF).copy(alpha = 0.78f),
                            Color.Transparent
                        ),
                        center = Offset(0f, 240f),
                        radius = 860f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFEFF7).copy(alpha = 0.74f),
                            Color.Transparent
                        ),
                        center = Offset(900f, 250f),
                        radius = 920f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.40f),
                            Color(0xFFF8F7FF).copy(alpha = 0.70f)
                        )
                    )
                )
        )
        content()
    }
}

@Composable
private fun AssistantTopCircleButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier
) {
    PressScaleBox(
        onClick = onClick,
        pressedScale = 0.965f,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color(0x10000000),
                    spotColor = Color(0x10000000)
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.76f))
                .border(1.dp, Color.White.copy(alpha = 0.64f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssistantWelcome(
    quickActions: List<String>,
    textPrimary: Color,
    textSecondary: Color,
    iconTint: Color,
    isNarrow: Boolean,
    onQuickAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 8.dp,
                top = if (isNarrow) 104.dp else 118.dp,
                end = 8.dp,
                bottom = 154.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(if (isNarrow) 112.dp else 122.dp)
                .shadow(
                    elevation = 30.dp,
                    shape = CircleShape,
                    clip = false,
                    ambientColor = Color(0x14000000),
                    spotColor = Color(0x14000000)
                )
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.82f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nm_ai_assistant),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(if (isNarrow) 42.dp else 46.dp)
            )
        }
        Spacer(modifier = Modifier.height(if (isNarrow) 42.dp else 48.dp))
        Text(
            text = "你好，我是 AI 助手",
            color = textPrimary,
            fontSize = if (isNarrow) 27.sp else 29.sp,
            lineHeight = if (isNarrow) 33.sp else 35.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "可以帮你查找、整理、归档记忆",
            color = textSecondary,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        FlowRow(
            modifier = Modifier.widthIn(max = if (isNarrow) 356.dp else 390.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            quickActions.forEach { action ->
                QuickActionChip(
                    text = action,
                    onClick = { onQuickAction(action) }
                )
            }
        }
    }
}

@Composable
private fun AssistantMessageItem(
    message: AiAssistantMessage,
    palette: NoMemoPalette,
    isDark: Boolean,
    assistantSurface: Color,
    userSurface: Color,
    subtleStroke: Color,
    onOpenMemory: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onCancel: (String) -> Unit
) {
    val isUser = message.role == AiAssistantRole.USER
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        message.imageUri?.let { rawUri ->
            AssistantMessageImagePreview(
                imageUri = Uri.parse(rawUri),
                isUser = isUser,
                isDark = isDark
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (message.text.isNotBlank()) {
            AssistantMessageBubble(
                text = message.text,
                isUser = isUser,
                isError = message.isError,
                palette = palette,
                assistantSurface = assistantSurface,
                userSurface = userSurface,
                subtleStroke = subtleStroke
            )
        }
        if (message.memoryCards.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                message.memoryCards.forEach { card ->
                    AssistantMemoryResultCard(
                        card = card,
                        palette = palette,
                        surfaceColor = assistantSurface,
                        subtleStroke = subtleStroke,
                        onClick = { onOpenMemory(card.recordId) }
                    )
                }
            }
        }
        message.confirmation?.let { confirmation ->
            Spacer(modifier = Modifier.height(8.dp))
            AssistantConfirmationCard(
                confirmation = confirmation,
                palette = palette,
                surfaceColor = assistantSurface,
                subtleStroke = subtleStroke,
                onConfirm = { onConfirm(confirmation.confirmationId) },
                onCancel = { onCancel(confirmation.confirmationId) }
            )
        }
    }
}

@Composable
private fun AssistantMessageBubble(
    text: String,
    isUser: Boolean,
    isError: Boolean,
    palette: NoMemoPalette,
    assistantSurface: Color,
    userSurface: Color,
    subtleStroke: Color
) {
    val background = when {
        isError -> Color(0xFFFFE7E7)
        isUser -> userSurface
        else -> assistantSurface
    }
    val textColor = when {
        isError -> Color(0xFF9B1C1C)
        isUser -> palette.onAccent
        else -> palette.textPrimary
    }
    Box(
        modifier = Modifier
            .widthIn(max = 430.dp)
            .clip(noMemoG2RoundedShape(20.dp))
            .background(background)
            .then(if (isUser) Modifier else Modifier)
            .padding(horizontal = 15.dp, vertical = 11.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium
        )
    }
    if (!isUser && !isError) {
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@Composable
private fun AssistantMessageImagePreview(
    imageUri: Uri,
    isUser: Boolean,
    isDark: Boolean
) {
    Box(
        modifier = Modifier
            .size(width = 150.dp, height = 112.dp)
            .clip(noMemoG2RoundedShape(20.dp))
            .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.86f))
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = if (isUser) "用户图片" else "图片",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun AssistantMemoryResultCard(
    card: AiAssistantMemoryCard,
    palette: NoMemoPalette,
    surfaceColor: Color,
    subtleStroke: Color,
    onClick: () -> Unit
) {
    val accent = assistantCategoryAccent(card.categoryCode)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = noMemoG2RoundedShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, subtleStroke)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(NoMemoG2CapsuleShape)
                        .background(accent.copy(alpha = 0.16f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = card.categoryName,
                        color = accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatAssistantTime(card.createdAt),
                    color = palette.textTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(9.dp))
            Text(
                text = card.title,
                color = palette.textPrimary,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (card.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = card.summary,
                    color = palette.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AssistantConfirmationCard(
    confirmation: AiAssistantConfirmation,
    palette: NoMemoPalette,
    surfaceColor: Color,
    subtleStroke: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 540.dp),
        shape = noMemoG2RoundedShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, subtleStroke)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Text(
                text = confirmation.title,
                color = palette.textPrimary,
                fontSize = 16.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = confirmation.description,
                color = palette.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                confirmation.records.take(5).forEach { card ->
                    AssistantConfirmationRecordRow(card = card, palette = palette)
                }
            }
            if (confirmation.records.size > 5) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "还有 ${confirmation.records.size - 5} 条将一并处理",
                    color = palette.textTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            when (confirmation.status) {
                AiAssistantConfirmationStatus.PENDING -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ConfirmationActionButton(
                            text = "取消",
                            background = Color.Transparent,
                            textColor = palette.textSecondary,
                            border = subtleStroke,
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        )
                        ConfirmationActionButton(
                            text = when (confirmation.action) {
                                AiAssistantDangerousAction.ARCHIVE -> "确认归档"
                                AiAssistantDangerousAction.DELETE -> "确认删除"
                            },
                            background = palette.accent,
                            textColor = palette.onAccent,
                            border = Color.Transparent,
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                AiAssistantConfirmationStatus.CONFIRMED -> Text(
                    text = "已确认并执行",
                    color = palette.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                AiAssistantConfirmationStatus.CANCELLED -> Text(
                    text = "已取消，未修改记忆",
                    color = palette.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AssistantConfirmationRecordRow(
    card: AiAssistantMemoryCard,
    palette: NoMemoPalette
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(assistantCategoryAccent(card.categoryCode))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = card.title,
                color = palette.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${card.categoryName} · ${formatAssistantTime(card.createdAt)}",
                color = palette.textTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ConfirmationActionButton(
    text: String,
    background: Color,
    textColor: Color,
    border: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(NoMemoG2CapsuleShape)
            .background(background)
            .then(
                if (border == Color.Transparent) {
                    Modifier
                } else {
                    Modifier.background(Color.Transparent)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AssistantTypingBubble(
    palette: NoMemoPalette,
    surfaceColor: Color,
    subtleStroke: Color
) {
    Box(
        modifier = Modifier
            .widthIn(max = 240.dp)
            .clip(noMemoG2RoundedShape(20.dp))
            .background(surfaceColor)
            .padding(horizontal = 15.dp, vertical = 11.dp)
    ) {
        Text(
            text = "正在整理记忆...",
            color = palette.textSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
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
    onClick: () -> Unit
) {
    PressScaleBox(
        onClick = onClick,
        pressedScale = 0.985f
    ) {
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 18.dp,
                    shape = NoMemoG2CapsuleShape,
                    clip = false,
                    ambientColor = Color(0x10000000),
                    spotColor = Color(0x10000000)
                )
                .clip(NoMemoG2CapsuleShape)
                .background(Color.White.copy(alpha = 0.78f))
                .border(1.dp, Color.White.copy(alpha = 0.62f), NoMemoG2CapsuleShape)
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Text(
                text = text,
                color = Color(0xFF4F5B70),
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun assistantCategoryAccent(categoryCode: String): Color {
    return Color(CategoryCatalog.getCategoryAccentColor(categoryCode).toLong() and 0xFFFFFFFF)
}

private fun formatAssistantTime(timestamp: Long): String {
    return SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA).format(Date(timestamp))
}
