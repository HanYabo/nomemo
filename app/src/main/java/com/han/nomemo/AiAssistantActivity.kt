package com.han.nomemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Restaurant
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
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
            val settingsStore = remember { SettingsStore(this) }
            PredictiveBackGestureHandler(
                enabled = settingsStore.predictiveBackGesture,
                onBack = { finish() }
            ) {
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
    var inputBarHeightPx by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    val quickActions = listOf(
        "查看我的快递",
        "帮我找一下购物记录",
        "帮我归档过期的记忆",
        "总结最近一周的记忆"
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
    val inputBarBottomClearance = if (inputBarHeightPx > 0) {
        with(density) { inputBarHeightPx.toDp() } + 18.dp
    } else {
        112.dp
    }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                Column(modifier = Modifier.fillMaxSize().statusBarsPadding()
                    .padding(
                        start = spec.pageHorizontalPadding,
                        end = spec.pageHorizontalPadding,
                        top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                        bottom = inputBarBottomClearance
                    )) {
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
                            iconRes = R.drawable.ic_nm_list_view,
                            contentDescription = "会话历史",
                            onClick = { viewModel.toggleHistoryPanel() },
                            modifier = Modifier.align(Alignment.CenterEnd),
                            size = spec.topActionButtonSize
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = inputBarBottomClearance)
                    ) {
                        if (uiState.messages.isEmpty() && !uiState.isSending) {
                            AssistantWelcome(
                                quickActions = quickActions,
                                palette = palette,
                                accent = accent,
                                contentSurface = contentSurface,
                                isNarrow = spec.isNarrow,
                                onQuickAction = { inputText = it }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                state = listState,
                                contentPadding = PaddingValues(top = 18.dp, bottom = 20.dp),
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
                                    item(key = "assistant_loading") { AssistantTypingBubble(
                                            palette = palette,
                                            surfaceColor = contentSurface,
                                            subtleStroke = subtleStroke,
                                            isDark = isDark
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
                        .onSizeChanged { inputBarHeightPx = it.height }
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp),
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
                                .clickable(enabled = !uiState.isSending) {
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

                        val sendEnabled = !uiState.isSending &&
                            (inputText.isNotBlank() || selectedImageUri != null)
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (sendEnabled) accent else insetSurface)
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
                                tint = if (sendEnabled) palette.onAccent else palette.textTertiary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // History panel overlay
            BackHandler(enabled = uiState.showHistoryPanel) {
                viewModel.closeHistoryPanel()
            }

            AnimatedVisibility(
                visible = uiState.showHistoryPanel,
                enter = fadeIn(tween(durationMillis = 250, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(durationMillis = 200, easing = FastOutSlowInEasing)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { viewModel.closeHistoryPanel() }
                        )
                )
            }

            AnimatedVisibility(
                visible = uiState.showHistoryPanel,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                AssistantHistoryPanel(
                    sessions = uiState.sessions,
                    currentSessionId = uiState.currentSessionId,
                    palette = palette,
                    contentSurface = contentSurface,
                    accent = accent,
                    isDark = isDark,
                    onSelectSession = { viewModel.loadSession(it) },
                    onDeleteSession = { viewModel.deleteSession(it) },
                    onNewSession = { viewModel.startNewSession() },
                    onClose = { viewModel.closeHistoryPanel() }
                )
            }
        }
    }
}

@Composable
private fun AssistantHistoryPanel(
    sessions: List<AiAssistantSession>,
    currentSessionId: String?,
    palette: NoMemoPalette,
    contentSurface: Color,
    accent: Color,
    isDark: Boolean,
    onSelectSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onNewSession: () -> Unit,
    onClose: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
    val panelShape = noMemoG2RoundedShape(topStart = 20.dp, bottomStart = 20.dp)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .shadow(12.dp, panelShape, ambientColor = Color.Black.copy(alpha = 0.15f), spotColor = Color.Black.copy(alpha = 0.20f))
            .clip(panelShape)
            .background(contentSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )

            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "会话历史",
                    color = palette.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.glassFill)
                            .clickable { onNewSession() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "新建会话",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(palette.glassFill)
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "关闭",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(palette.textTertiary.copy(alpha = 0.12f))
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = palette.textTertiary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "暂无会话记录",
                            color = palette.textTertiary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "开始新对话后记录将出现在这里",
                            color = palette.textTertiary.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(sessions, key = { it.sessionId }) { session ->
                        val isCurrent = session.sessionId == currentSessionId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(noMemoG2RoundedShape(14.dp))
                                .background(if (isCurrent) accent.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable { onSelectSession(session.sessionId) }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = session.title.ifBlank { "新会话" },
                                    color = if (isCurrent) accent else palette.textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateFormat.format(Date(session.createdAt)) + "  ·  ${session.messages.size} 条",
                                    color = palette.textTertiary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssistantWelcome(
    quickActions: List<String>,
    palette: NoMemoPalette,
    accent: Color,
    contentSurface: Color,
    isNarrow: Boolean,
    onQuickAction: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isNarrow) 78.dp else 88.dp)
                .clip(CircleShape)
                .background(contentSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nm_ai_assistant),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(if (isNarrow) 40.dp else 44.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "你好，我是NoMemoAI助手",
            color = palette.textPrimary,
            fontSize = if (isNarrow) 22.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "可以帮你查找、整理和归档记忆",
            color = palette.textSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        FlowRow(
            modifier = Modifier.widthIn(max = 420.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            quickActions.forEach { action ->
                QuickActionChip(
                    text = action,
                    palette = palette,
                    onClick = { onQuickAction(action) }
                )
            }
        }
        Spacer(modifier = Modifier.height(96.dp))
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                message.memoryCards.forEach { card ->
                    AssistantMemoryResultCard(
                        card = card,
                        palette = palette,
                        isDark = isDark,
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
    isDark: Boolean,
    onClick: () -> Unit
) {
    val cardColor = if (isDark) Color(0xFF1A1A1C) else Color.White
    val summaryColor = if (isDark) {
        palette.textSecondary.copy(alpha = 0.88f)
    } else {
        Color(0xFF697281)
    }
    val metaColor = if (isDark) {
        Color.White.copy(alpha = 0.46f)
    } else {
        Color(0xFF98A1AE)
    }
    val showPreviewImage = !card.imageUri.isNullOrBlank()
    val thumbnailBackground = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF1F4F8)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = noMemoG2RoundedShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.title,
                    color = palette.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (card.summary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = card.summary,
                        color = summaryColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatAssistantTime(card.createdAt),
                        color = metaColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(metaColor.copy(alpha = 0.72f))
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Icon(
                        imageVector = assistantCategoryMetaIcon(card.categoryCode),
                        contentDescription = null,
                        tint = metaColor.copy(alpha = 0.86f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = card.categoryName,
                        color = metaColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (showPreviewImage) {
                Spacer(modifier = Modifier.width(14.dp))
                MemoryThumbnail(
                    uriString = card.imageUri.orEmpty(),
                    width = 82.dp,
                    height = 108.dp,
                    backgroundColor = thumbnailBackground,
                    cornerRadius = 17.dp,
                    modifier = Modifier
                        .border(
                            border = noMemoMemoryImageBorder(palette),
                            shape = noMemoG2RoundedShape(17.dp)
                        )
                )
            }
        }
    }
}

private fun assistantCategoryMetaIcon(categoryCode: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (categoryCode) {
        CategoryCatalog.CODE_LIFE_PICKUP -> Icons.Outlined.Restaurant
        CategoryCatalog.CODE_LIFE_DELIVERY -> Icons.Outlined.LocalShipping
        CategoryCatalog.CODE_LIFE_CARD -> Icons.Outlined.Badge
        CategoryCatalog.CODE_LIFE_TICKET -> Icons.Outlined.ConfirmationNumber
        CategoryCatalog.CODE_WORK_TODO -> Icons.Outlined.AssignmentTurnedIn
        CategoryCatalog.CODE_WORK_SCHEDULE -> Icons.Outlined.CalendarMonth
        else -> Icons.Outlined.Edit
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(NoMemoG2CapsuleShape)
            .background(background)
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
    subtleStroke: Color,
    isDark: Boolean
) {
    val flowColor = if (isDark) Color(0xFF2E8BFF) else Color(0xFF1677FF)
    Box(
        modifier = Modifier
            .widthIn(max = 240.dp)
            .clip(noMemoG2RoundedShape(20.dp))
            .background(surfaceColor)
            .padding(horizontal = 15.dp, vertical = 11.dp)
    ) {
        NoMemoFlowingText(
            text = "思考中...",
            color = flowColor,
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

private fun assistantCategoryAccent(categoryCode: String): Color {
    return Color(CategoryCatalog.getCategoryAccentColor(categoryCode).toLong() and 0xFFFFFFFF)
}

private fun formatAssistantTime(timestamp: Long): String {
    return SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA).format(Date(timestamp))
}
