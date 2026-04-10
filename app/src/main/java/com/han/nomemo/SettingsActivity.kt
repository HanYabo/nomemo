package com.han.nomemo

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat

private enum class AiTestTarget {
    IMAGE,
    TEXT,
    MULTIMODAL
}

private const val API_TEST_IMAGE_DATA_URI =
    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9sX6lz0AAAAASUVORK5CYII="

class SettingsActivity : BaseComposeActivity() {
    companion object {
        private const val EXTRA_ROUTE_KEY = "extra_route_key"
        fun createIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
    private lateinit var settingsStore: SettingsStore
    private lateinit var memoryStore: MemoryStore
    private val initialRoute by lazy {
        SettingsRoute.fromKey(intent.getStringExtra(EXTRA_ROUTE_KEY).orEmpty())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsStore = SettingsStore(this)
        memoryStore = MemoryStore(this)
        setContent {
            SettingsContent(
                currentRoute = initialRoute,
                onClose = { finish() },
                onNavigate = { route -> openRoutePage(route) },
                onAiEnabledChange = { value ->
                    settingsStore.aiEnabled = value
                    setResult(RESULT_OK)
                },
                onBaseUrlChange = { value ->
                    settingsStore.apiBaseUrl = value
                    setResult(RESULT_OK)
                },
                onApiKeyChange = { value ->
                    settingsStore.apiKey = value
                    setResult(RESULT_OK)
                },
                onImageCustomModelChange = { value ->
                    settingsStore.imageCustomModel = value
                    setResult(RESULT_OK)
                },
                onTextCustomModelChange = { value ->
                    settingsStore.textCustomModel = value
                    setResult(RESULT_OK)
                },
                onMultimodalCustomModelChange = { value ->
                    settingsStore.multimodalCustomModel = value
                    setResult(RESULT_OK)
                },
                onImageModelPresetChange = { value ->
                    settingsStore.imageModelPreset = value
                    setResult(RESULT_OK)
                },
                onTextModelPresetChange = { value ->
                    settingsStore.textModelPreset = value
                    setResult(RESULT_OK)
                },
                onMultimodalModelPresetChange = { value ->
                    settingsStore.multimodalModelPreset = value
                    setResult(RESULT_OK)
                },
                onThemeModeChange = { value ->
                    settingsStore.themeMode = value
                    settingsStore.applyThemeMode()
                    setResult(RESULT_OK)
                },
                onThemeGlobalEnabledChange = { value ->
                    settingsStore.themeGlobalEnabled = value
                    setResult(RESULT_OK)
                },
                onShowDividersChange = { value ->
                    settingsStore.showDividers = value
                    setResult(RESULT_OK)
                },
                onThemeAccentChange = { value ->
                    settingsStore.themeAccent = value
                    setResult(RESULT_OK)
                },
                onClearData = {
                    memoryStore.clearAll()
                    setResult(RESULT_OK)
                    Toast.makeText(this, "本地数据已清空", Toast.LENGTH_SHORT).show()
                },
                onTestApi = { target, baseUrl, apiKey, model, title, onResult ->
                    testApiConnection(target, baseUrl, apiKey, model, title, onResult)
                }
            )
        }
    }

    private fun testApiConnection(
        target: AiTestTarget,
        baseUrl: String,
        apiKey: String,
        model: String,
        title: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val resolvedBaseUrl = baseUrl.trim().ifBlank { BuildConfig.OPENAI_BASE_URL }
        val resolvedApiKey = apiKey.trim().ifBlank { BuildConfig.OPENAI_API_KEY }
        val resolvedModel = model.trim().ifBlank { BuildConfig.OPENAI_MODEL }

        if (resolvedBaseUrl.isBlank() || resolvedApiKey.isBlank() || resolvedModel.isBlank()) {
            onResult(false, "请先补全 Base URL、API Key 与 Model。")
            return
        }

        Thread {
            val result = runCatching {
                performApiConnectionTest(target, resolvedBaseUrl, resolvedApiKey, resolvedModel)
            }
            runOnUiThread {
                result.fold(
                    onSuccess = { onResult(true, "${title}连接成功，当前配置可正常访问。") },
                    onFailure = { error ->
                        onResult(false, error.message ?: "API 连接测试失败，请检查当前配置。")
                    }
                )
            }
        }.start()
    }

    private fun performApiConnectionTest(
        target: AiTestTarget,
        baseUrl: String,
        apiKey: String,
        model: String
    ): String {
        val url = URL(resolveChatCompletionsUrl(baseUrl))
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 20_000
            doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json")
        }

        return try {
            val payload = buildApiTestPayload(target, model)

            val body = payload.toString().toByteArray(StandardCharsets.UTF_8)
            connection.outputStream.use { output ->
                output.write(body)
                output.flush()
            }

            val code = connection.responseCode
            val responseBody = readResponse(connection)
            if (code !in 200..299) {
                throw IllegalStateException("HTTP $code：${extractErrorMessage(responseBody)}")
            }

            val responseJson = JSONObject(responseBody)
            val choices = responseJson.optJSONArray("choices")
            if (choices == null || choices.length() == 0) {
                throw IllegalStateException("接口已响应，但没有返回有效内容。")
            }

            "OK"
        } finally {
            connection.disconnect()
        }
    }

    private fun buildApiTestPayload(
        target: AiTestTarget,
        model: String
    ): JSONObject {
        return JSONObject().apply {
            put("model", model)
            put("temperature", 0)
            put("max_tokens", 8)
            put(
                "messages",
                JSONArray().apply {
                    put(
                        when (target) {
                            AiTestTarget.TEXT -> JSONObject().apply {
                                put("role", "user")
                                put("content", "Reply with OK only.")
                            }
                            AiTestTarget.IMAGE -> JSONObject().apply {
                                put("role", "user")
                                put(
                                    "content",
                                    JSONArray().apply {
                                        put(
                                            JSONObject().apply {
                                                put("type", "text")
                                                put("text", "Inspect the image and reply with OK only.")
                                            }
                                        )
                                        put(
                                            JSONObject().apply {
                                                put("type", "image_url")
                                                put(
                                                    "image_url",
                                                    JSONObject().put("url", API_TEST_IMAGE_DATA_URI)
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                            AiTestTarget.MULTIMODAL -> JSONObject().apply {
                                put("role", "user")
                                put(
                                    "content",
                                    JSONArray().apply {
                                        put(
                                            JSONObject().apply {
                                                put("type", "text")
                                                put("text", "Text: hello. Read the image and reply with OK only.")
                                            }
                                        )
                                        put(
                                            JSONObject().apply {
                                                put("type", "image_url")
                                                put(
                                                    "image_url",
                                                    JSONObject().put("url", API_TEST_IMAGE_DATA_URI)
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            )
        }
    }

    private fun resolveChatCompletionsUrl(baseUrl: String): String {
        var normalized = baseUrl.trim()
        while (normalized.endsWith("/")) {
            normalized = normalized.dropLast(1)
        }
        return when {
            normalized.endsWith("/chat/completions") -> normalized
            normalized.endsWith("/responses") -> normalized.removeSuffix("/responses") + "/chat/completions"
            else -> normalized + "/chat/completions"
        }
    }

    private fun readResponse(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }
        return stream?.use(InputStream::readBytes)?.toString(StandardCharsets.UTF_8) ?: ""
    }

    private fun extractErrorMessage(responseBody: String): String {
        if (responseBody.isBlank()) {
            return "未收到服务端返回，请检查 Base URL、API Key 与 Model。"
        }
        return runCatching {
            val json = JSONObject(responseBody)
            val error = json.optJSONObject("error")
            when {
                error != null && error.optString("message").isNotBlank() -> error.optString("message")
                json.optString("message").isNotBlank() -> json.optString("message")
                else -> responseBody.take(160)
            }
        }.getOrElse {
            responseBody.take(160)
        }
    }

    private fun openRoutePage(route: SettingsRoute) {
        if (route == SettingsRoute.Home || route == initialRoute) {
            return
        }
        startActivity(
            Intent(this, SettingsActivity::class.java).putExtra(EXTRA_ROUTE_KEY, route.key)
        )
    }

    private fun openExternalUrl(url: String) {
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }.onFailure {
            Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    private fun SettingsContent(
        currentRoute: SettingsRoute,
        onClose: () -> Unit,
        onNavigate: (SettingsRoute) -> Unit,
        onAiEnabledChange: (Boolean) -> Unit,
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onImageCustomModelChange: (String) -> Unit,
        onTextCustomModelChange: (String) -> Unit,
        onMultimodalCustomModelChange: (String) -> Unit,
        onImageModelPresetChange: (String) -> Unit,
        onTextModelPresetChange: (String) -> Unit,
        onMultimodalModelPresetChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onThemeGlobalEnabledChange: (Boolean) -> Unit,
        onShowDividersChange: (Boolean) -> Unit,
        onThemeAccentChange: (String) -> Unit,
        onClearData: () -> Unit,
        onTestApi: (AiTestTarget, String, String, String, String, (Boolean, String) -> Unit) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val pageBackgroundBrush = Brush.verticalGradient(
            listOf(
                palette.memoBgStart,
                palette.memoBgMid,
                palette.memoBgEnd
            )
        )
        val cardSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFill.copy(alpha = 0.92f))
        } else {
            Color.White.copy(alpha = 0.985f)
        }
        val actionSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.96f))
        } else {
            Color.White.copy(alpha = 0.985f)
        }
        val softSurface = if (isDark) {
            noMemoCardSurfaceColor(true, palette.glassFillSoft.copy(alpha = 0.92f))
        } else {
            Color(0xFFF1F3F6)
        }
        val titleColor = palette.textPrimary
        val subtitleColor = palette.textSecondary
        val sectionLabelColor = palette.textTertiary
        val borderColor = Color.Transparent
        val aiToggleColor = if (isDark) Color(0xFF3BD166) else Color(0xFF30C85A)
        val aiActionColor = if (isDark) Color(0xFF2E8BFF) else Color(0xFF1677FF)
        var aiEnabled by remember { mutableStateOf(settingsStore.aiEnabled) }
        var baseUrl by remember { mutableStateOf(settingsStore.apiBaseUrl.ifBlank { BuildConfig.OPENAI_BASE_URL }) }
        var apiKey by remember { mutableStateOf(settingsStore.apiKey) }
        var imageCustomModel by remember {
            mutableStateOf(settingsStore.imageCustomModel.ifBlank { settingsStore.resolvedApiModel() })
        }
        var textCustomModel by remember {
            mutableStateOf(settingsStore.textCustomModel.ifBlank { settingsStore.resolvedApiModel() })
        }
        var multimodalCustomModel by remember {
            mutableStateOf(settingsStore.multimodalCustomModel.ifBlank { settingsStore.resolvedApiModel() })
        }
        var imageModelPreset by remember { mutableStateOf(settingsStore.imageModelPreset) }
        var textModelPreset by remember { mutableStateOf(settingsStore.textModelPreset) }
        var multimodalModelPreset by remember { mutableStateOf(settingsStore.multimodalModelPreset) }
        var themeMode by remember { mutableStateOf(settingsStore.themeMode) }
        var themeGlobalEnabled by remember { mutableStateOf(settingsStore.themeGlobalEnabled) }
        var showDividers by remember { mutableStateOf(settingsStore.showDividers) }
        var themeAccent by remember { mutableStateOf(settingsStore.themeAccent) }
        val dividerColor = if (showDividers) {
            if (isDark) palette.glassStroke.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.055f)
        } else {
            Color.Transparent
        }
        var showClearConfirm by remember { mutableStateOf(false) }
        var testingTarget by remember { mutableStateOf<AiTestTarget?>(null) }
        var showTestResult by remember { mutableStateOf(false) }
        var testResultSuccess by remember { mutableStateOf(false) }
        var testResultMessage by remember { mutableStateOf("") }
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundBrush)
        ) {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = (spec.pageTopPadding - 4.dp).coerceAtLeast(0.dp),
                            end = spec.pageHorizontalPadding,
                            bottom = 0.dp
                        )
                ) {
                    SettingsHeader(
                        spec = spec,
                        title = when (currentRoute) {
                            SettingsRoute.Home -> "设置"
                            SettingsRoute.AiConfig -> "AI 功能"
                            SettingsRoute.AiModel -> "模型配置"
                            SettingsRoute.AiGuide -> "AI功能帮助"
                            SettingsRoute.Appearance -> "显示与主题"
                            SettingsRoute.Data -> "本地数据管理"
                        },
                        onBack = onClose,
                        titleColor = titleColor,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 14.dp)
                            .verticalScroll(scrollState)
                            .padding(bottom = 24.dp)
                    ) {
                        SettingsRouteBody(
                            currentRoute = currentRoute,
                            sectionLabelColor = sectionLabelColor,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            cardSurface = cardSurface,
                            softSurface = softSurface,
                            actionSurface = actionSurface,
                            dividerColor = dividerColor,
                            borderColor = borderColor,
                            aiToggleColor = aiToggleColor,
                            aiActionColor = aiActionColor,
                            palette = palette,
                            aiEnabled = aiEnabled,
                            baseUrl = baseUrl,
                            apiKey = apiKey,
                            imageCustomModel = imageCustomModel,
                            textCustomModel = textCustomModel,
                            multimodalCustomModel = multimodalCustomModel,
                            imageModelPreset = imageModelPreset,
                            textModelPreset = textModelPreset,
                            multimodalModelPreset = multimodalModelPreset,
                            themeMode = themeMode,
                            themeGlobalEnabled = themeGlobalEnabled,
                            showDividers = showDividers,
                            themeAccent = themeAccent,
                            testingTarget = testingTarget,
                            onNavigate = onNavigate,
                            onAiEnabledChange = {
                                aiEnabled = it
                                onAiEnabledChange(it)
                            },
                            onBaseUrlChange = {
                                baseUrl = it
                                onBaseUrlChange(it)
                            },
                            onApiKeyChange = {
                                apiKey = it
                                onApiKeyChange(it)
                            },
                            onImageCustomModelChange = {
                                imageCustomModel = it
                                onImageCustomModelChange(it)
                            },
                            onTextCustomModelChange = {
                                textCustomModel = it
                                onTextCustomModelChange(it)
                            },
                            onMultimodalCustomModelChange = {
                                multimodalCustomModel = it
                                onMultimodalCustomModelChange(it)
                            },
                            onImageModelPresetChange = {
                                imageModelPreset = it
                                onImageModelPresetChange(it)
                            },
                            onTextModelPresetChange = {
                                textModelPreset = it
                                onTextModelPresetChange(it)
                            },
                            onMultimodalModelPresetChange = {
                                multimodalModelPreset = it
                                onMultimodalModelPresetChange(it)
                            },
                            onThemeModeChange = {
                                themeMode = it
                                onThemeModeChange(it)
                            },
                            onThemeGlobalEnabledChange = {
                                themeGlobalEnabled = it
                                onThemeGlobalEnabledChange(it)
                            },
                            onShowDividersChange = {
                                showDividers = it
                                onShowDividersChange(it)
                            },
                            onThemeAccentChange = {
                                themeAccent = it
                                onThemeAccentChange(it)
                            },
                            onShowClearConfirm = { showClearConfirm = true },
                            onRunApiTest = { target, modelName, title ->
                                if (testingTarget != null) {
                                    return@SettingsRouteBody
                                }
                                testingTarget = target
                                onTestApi(target, baseUrl, apiKey, modelName, title) { success, message ->
                                    testingTarget = null
                                    testResultSuccess = success
                                    testResultMessage = message
                                    showTestResult = true
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showClearConfirm) {
            NoMemoConfirmDialog(
                title = "确认清空数据",
                message = "该操作会删除当前设备上的全部本地数据，且无法恢复。",
                confirmText = "确认",
                dismissText = "取消",
                destructive = true,
                onConfirm = {
                    showClearConfirm = false
                    onClearData()
                },
                onDismiss = { showClearConfirm = false }
            )
        }

        if (showTestResult) {
            NoMemoMessageDialog(
                title = if (testResultSuccess) "API 测试成功" else "API 测试失败",
                message = testResultMessage,
                confirmText = "知道了",
                onDismiss = { showTestResult = false }
            )
        }
    }

    @Composable
    private fun SettingsRouteBody(
        currentRoute: SettingsRoute,
        sectionLabelColor: Color,
        titleColor: Color,
        subtitleColor: Color,
        cardSurface: Color,
        softSurface: Color,
        actionSurface: Color,
        dividerColor: Color,
        borderColor: Color,
        aiToggleColor: Color,
        aiActionColor: Color,
        palette: NoMemoPalette,
        aiEnabled: Boolean,
        baseUrl: String,
        apiKey: String,
        imageCustomModel: String,
        textCustomModel: String,
        multimodalCustomModel: String,
        imageModelPreset: String,
        textModelPreset: String,
        multimodalModelPreset: String,
        themeMode: String,
        themeGlobalEnabled: Boolean,
        showDividers: Boolean,
        themeAccent: String,
        testingTarget: AiTestTarget?,
        onNavigate: (SettingsRoute) -> Unit,
        onAiEnabledChange: (Boolean) -> Unit,
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onImageCustomModelChange: (String) -> Unit,
        onTextCustomModelChange: (String) -> Unit,
        onMultimodalCustomModelChange: (String) -> Unit,
        onImageModelPresetChange: (String) -> Unit,
        onTextModelPresetChange: (String) -> Unit,
        onMultimodalModelPresetChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onThemeGlobalEnabledChange: (Boolean) -> Unit,
        onShowDividersChange: (Boolean) -> Unit,
        onThemeAccentChange: (String) -> Unit,
        onShowClearConfirm: () -> Unit,
        onRunApiTest: (AiTestTarget, String, String) -> Unit
    ) {
        when (currentRoute) {
            SettingsRoute.Home -> {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingsHomePage(
                        sectionLabelColor = sectionLabelColor,
                        titleColor = titleColor,
                        cardSurface = cardSurface,
                        dividerColor = dividerColor,
                        borderColor = borderColor,
                        onNavigate = onNavigate
                    )
                }
            }

            SettingsRoute.AiConfig -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    SettingsSectionLabel("AI 配置", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
                        ) {
                            SettingsFeatureStatusRow(
                                title = "启用 AI 功能",
                                checked = aiEnabled,
                                onCheckedChange = onAiEnabledChange,
                                enabled = true,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                accentColor = aiToggleColor,
                                surface = softSurface
                            )
                            SettingsInfoDivider(dividerColor, modifier = Modifier.padding(vertical = 18.dp))
                            SettingsInlineConfigField(
                                label = "API 地址",
                                value = baseUrl,
                                onValueChange = onBaseUrlChange,
                                placeholder = BuildConfig.OPENAI_BASE_URL,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor
                            )
                            SettingsInfoDivider(dividerColor, modifier = Modifier.padding(vertical = 18.dp))
                            SettingsInlineConfigField(
                                label = "API 密钥",
                                value = apiKey,
                                onValueChange = onApiKeyChange,
                                placeholder = "sk-...",
                                isSecret = true,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                accentColor = aiActionColor
                            )
                        }
                    }

                    SettingsHelpLinkRow(
                        text = "如何配置 AI 服务？",
                        color = aiActionColor,
                        modifier = Modifier.padding(top = 14.dp),
                        onClick = { onNavigate(SettingsRoute.AiGuide) }
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                    SettingsSectionLabel("模型配置", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        SettingsDisclosureRow(
                            title = "模型选择与测试",
                            titleColor = titleColor,
                            expanded = false,
                            onClick = { onNavigate(SettingsRoute.AiModel) }
                        )
                    }
                }
            }

            SettingsRoute.AiModel -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    SettingsSectionLabel("图像处理模型", sectionLabelColor)
                    SettingsModelPresetCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        dividerColor = dividerColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        accentColor = aiActionColor,
                        selectedPreset = imageModelPreset,
                        customModel = imageCustomModel,
                        options = listOf(
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_IMAGE_FLASH,
                                title = "GLM-4.6V-Flash",
                                subtitle = "支持图片理解，更均衡的速度"
                            ),
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_IMAGE_THINKING,
                                title = "GLM-4.1V-Thinking-Flash",
                                subtitle = "内置深度思考，视觉推理能力更强"
                            ),
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_PRESET_CUSTOM,
                                title = "自定义模型",
                                subtitle = "当前自定义模型：${imageCustomModel.ifBlank { settingsStore.resolvedApiModel() }}"
                            )
                        ),
                        onSelect = onImageModelPresetChange,
                        onCustomModelChange = onImageCustomModelChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("文本处理模型", sectionLabelColor)
                    SettingsModelPresetCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        dividerColor = dividerColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        accentColor = aiActionColor,
                        selectedPreset = textModelPreset,
                        customModel = textCustomModel,
                        options = listOf(
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_TEXT_FLASH,
                                title = "GLM-4.6-Flash",
                                subtitle = "等价时间更短，更均衡的速度"
                            ),
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_TEXT_FLASH_47,
                                title = "GLM-4.7-Flash",
                                subtitle = "多步推理更强，但处理更充分"
                            ),
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_PRESET_CUSTOM,
                                title = "自定义模型",
                                subtitle = "当前自定义模型：${textCustomModel.ifBlank { settingsStore.resolvedApiModel() }}"
                            )
                        ),
                        onSelect = onTextModelPresetChange,
                        onCustomModelChange = onTextCustomModelChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("多模态处理模型", sectionLabelColor)
                    SettingsModelPresetCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        dividerColor = dividerColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        accentColor = aiActionColor,
                        selectedPreset = multimodalModelPreset,
                        customModel = multimodalCustomModel,
                        options = listOf(
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_MULTIMODAL_FLASH,
                                title = "GLM-4.6V-Flash",
                                subtitle = "等价时间更短，更均衡的速度"
                            ),
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_PRESET_CUSTOM,
                                title = "自定义模型",
                                subtitle = "当前自定义模型：${multimodalCustomModel.ifBlank { settingsStore.resolvedApiModel() }}"
                            )
                        ),
                        onSelect = onMultimodalModelPresetChange,
                        onCustomModelChange = onMultimodalCustomModelChange
                    )

                    SettingsPrimaryAction(
                        title = if (testingTarget == AiTestTarget.IMAGE) "正在测试..." else "测试图像 API",
                        modifier = Modifier.padding(top = 24.dp),
                        accentColor = aiActionColor,
                        surface = actionSurface,
                        onClick = {
                            onRunApiTest(
                                AiTestTarget.IMAGE,
                                resolveModelPresetValue(
                                    imageModelPreset,
                                    imageCustomModel,
                                    SettingsStore.MODEL_IMAGE_DEFAULT
                                ),
                                "图像 API"
                            )
                        }
                    )
                    SettingsPrimaryAction(
                        title = if (testingTarget == AiTestTarget.TEXT) "正在测试..." else "测试文本 API",
                        modifier = Modifier.padding(top = 14.dp),
                        accentColor = aiActionColor,
                        surface = actionSurface,
                        onClick = {
                            onRunApiTest(
                                AiTestTarget.TEXT,
                                resolveModelPresetValue(
                                    textModelPreset,
                                    textCustomModel,
                                    SettingsStore.MODEL_TEXT_DEFAULT
                                ),
                                "文本 API"
                            )
                        }
                    )
                    SettingsPrimaryAction(
                        title = if (testingTarget == AiTestTarget.MULTIMODAL) "正在测试..." else "测试多模态 API",
                        modifier = Modifier.padding(top = 14.dp),
                        accentColor = aiActionColor,
                        surface = actionSurface,
                        onClick = {
                            onRunApiTest(
                                AiTestTarget.MULTIMODAL,
                                resolveModelPresetValue(
                                    multimodalModelPreset,
                                    multimodalCustomModel,
                                    SettingsStore.MODEL_MULTIMODAL_DEFAULT
                                ),
                                "多模态 API"
                            )
                        }
                    )

                    Column(
                        modifier = Modifier.padding(top = 20.dp, start = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SettingsBulletNote("图像模型：用于分析图片内容", subtitleColor, 13.sp, 20.sp)
                        SettingsBulletNote("文本模型：用于分析文本内容", subtitleColor, 13.sp, 20.sp)
                        SettingsBulletNote("多模态模型：用于同时分析图片和文字", subtitleColor, 13.sp, 20.sp)
                        SettingsBulletNote("在使用 AI 功能之前，请先完成 API 测试", subtitleColor, 13.sp, 20.sp)
                    }
                }
            }

            SettingsRoute.AiGuide -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    SettingsSectionLabel("简介", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Text(
                            text = "AI功能需要配置API地址与API Key方可正常访问和使用。配置后，即可使用AI根据截图、文本内容，自动提取并创建记忆。",
                            color = titleColor,
                            fontSize = 15.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("申请 API Key", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        SettingsGuideLinkRow(
                            title = "智谱AI开放平台",
                            subtitle = "兼容 OpenAI 接口，推荐配置 API Key",
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            onClick = { openExternalUrl("https://bigmodel.cn/login") }
                        )
                    }
                    Text(
                        text = "页面会展示账户下所有 API Key，请妥善保管。如无信息，可先完成实名认证或创建应用。",
                        color = subtitleColor,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 12.dp, end = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("配置步骤", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column {
                            SettingsGuideStepRow(
                                index = "1",
                                title = "添加新密钥",
                                subtitle = "点击“添加新的 API Key”按钮",
                                titleColor = titleColor,
                                subtitleColor = subtitleColor
                            )
                            SettingsInfoDivider(
                                color = dividerColor,
                                startInset = 20.dp,
                                endInset = 20.dp
                            )
                            SettingsGuideStepRow(
                                index = "2",
                                title = "创建密钥",
                                subtitle = "填写名称后点击“确定”",
                                titleColor = titleColor,
                                subtitleColor = subtitleColor
                            )
                            SettingsInfoDivider(
                                color = dividerColor,
                                startInset = 20.dp,
                                endInset = 20.dp
                            )
                            SettingsGuideStepRow(
                                index = "3",
                                title = "配置应用",
                                subtitle = "复制 API Key 并粘贴到应用设置",
                                titleColor = titleColor,
                                subtitleColor = subtitleColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("说明", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "本应用支持通过AI分析截图和文本，自动识别并提取取件码、票券、日程、预约等关键信息。",
                                color = titleColor,
                                fontSize = 15.sp,
                                lineHeight = 23.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "您可以选择：",
                                color = titleColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            SettingsBulletNote("智谱AI：使用预设模型，配置简单，也可以通过自定义模型接入其他兼容模型。", subtitleColor)
                            SettingsBulletNote("自定义API：支持 OpenAI 兼容接口，配置 BaseURL 与 API Key 后即可使用。", subtitleColor)
                            SettingsInfoDivider(color = dividerColor)
                            Text(
                                text = "申请并配置 API Key 后，在“AI 功能”页完成地址、密钥和模型设置即可启用。免费模型可能存在波动，建议在稳定场景下优先使用可靠服务。",
                                color = subtitleColor,
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            SettingsRoute.Appearance -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    val appearanceSelectionColor =
                        if (isSystemInDarkTheme()) Color(0xFF2E8BFF) else Color(0xFF1677FF)
                    SettingsSectionLabel("显示", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column {
                            SettingsSelectRow(
                                title = "跟随系统",
                                selected = themeMode == SettingsStore.THEME_SYSTEM,
                                titleColor = titleColor,
                                accentColor = appearanceSelectionColor,
                                onClick = { onThemeModeChange(SettingsStore.THEME_SYSTEM) }
                            )
                            SettingsInfoDivider(dividerColor, modifier = Modifier.padding(horizontal = 20.dp))
                            SettingsSelectRow(
                                title = "浅色模式",
                                selected = themeMode == SettingsStore.THEME_LIGHT,
                                titleColor = titleColor,
                                accentColor = appearanceSelectionColor,
                                onClick = { onThemeModeChange(SettingsStore.THEME_LIGHT) }
                            )
                            SettingsInfoDivider(dividerColor, modifier = Modifier.padding(horizontal = 20.dp))
                            SettingsSelectRow(
                                title = "深色模式",
                                selected = themeMode == SettingsStore.THEME_DARK,
                                titleColor = titleColor,
                                accentColor = appearanceSelectionColor,
                                onClick = { onThemeModeChange(SettingsStore.THEME_DARK) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("主题设置", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        SettingsToggleRow(
                            title = "全局生效",
                            checked = themeGlobalEnabled,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            accentColor = aiToggleColor,
                            surface = softSurface,
                            verticalPadding = 15.dp,
                            onCheckedChange = onThemeGlobalEnabledChange
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("视觉效果", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        SettingsToggleRow(
                            title = "显示分割线",
                            checked = showDividers,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            accentColor = aiToggleColor,
                            surface = softSurface,
                            verticalPadding = 15.dp,
                            onCheckedChange = onShowDividersChange
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("主题", sectionLabelColor)
                    val defaultThemeSwatch = rememberLightThemeTokenColor(R.color.dock_indicator)
                    val themeOptions = appearanceThemeOptions(defaultThemeSwatch)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column {
                            themeOptions.forEachIndexed { index, option ->
                                SettingsThemeAccentRow(
                                    title = option.title,
                                    accentColor = option.swatchColor,
                                    selected = themeAccent == option.key,
                                    titleColor = titleColor,
                                    selectedColor = appearanceSelectionColor,
                                    onClick = { onThemeAccentChange(option.key) }
                                )
                                if (index != themeOptions.lastIndex) {
                                    SettingsInfoDivider(dividerColor, modifier = Modifier.padding(horizontal = 20.dp))
                                }
                            }
                        }
                    }
                }
            }

            SettingsRoute.Data -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    SettingsSectionLabel("本地数据", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "本地数据管理",
                                color = titleColor,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "清空后会同步删除本地记忆、分组统计与提醒数据，此操作不可恢复。",
                                color = subtitleColor,
                                fontSize = 14.sp
                            )
                        }
                    }

                    SettingsDangerAction(
                        title = "清空本地数据",
                        modifier = Modifier.padding(top = 14.dp),
                        onClick = onShowClearConfirm
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingsHomePage(
        sectionLabelColor: Color,
        titleColor: Color,
        cardSurface: Color,
        dividerColor: Color,
        borderColor: Color,
        onNavigate: (SettingsRoute) -> Unit
    ) {
        SettingsSectionLabel("偏好配置", sectionLabelColor)
        SettingsMenuGroup(
            surface = cardSurface,
            borderColor = borderColor,
            dividerColor = dividerColor,
            modifier = Modifier.padding(top = 10.dp),
            items = listOf(
                SettingsMenuItem("AI 功能", route = SettingsRoute.AiConfig),
                SettingsMenuItem("AI功能帮助", route = SettingsRoute.AiGuide)
            ),
            titleColor = titleColor,
            onClick = onNavigate
        )

        Spacer(modifier = Modifier.height(22.dp))
        SettingsSectionLabel("个性化", sectionLabelColor)
        SettingsMenuGroup(
            surface = cardSurface,
            borderColor = borderColor,
            dividerColor = dividerColor,
            modifier = Modifier.padding(top = 10.dp),
            items = listOf(
                SettingsMenuItem("显示与主题", route = SettingsRoute.Appearance)
            ),
            titleColor = titleColor,
            onClick = onNavigate
        )

        Spacer(modifier = Modifier.height(22.dp))
        SettingsSectionLabel("数据管理", sectionLabelColor)
        SettingsMenuGroup(
            surface = cardSurface,
            borderColor = borderColor,
            dividerColor = dividerColor,
            modifier = Modifier.padding(top = 10.dp),
            items = listOf(
                SettingsMenuItem("本地数据管理", route = SettingsRoute.Data)
            ),
            titleColor = titleColor,
            onClick = onNavigate
        )
    }

    @Composable
    private fun SettingsHeader(
        spec: NoMemoAdaptiveSpec,
        title: String,
        onBack: () -> Unit,
        titleColor: Color
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(spec.topActionButtonSize)
        ) {
            GlassIconCircleButton(
                iconRes = R.drawable.ic_sheet_back,
                contentDescription = "返回",
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
                size = spec.topActionButtonSize
            )
            Text(
                text = title,
                color = titleColor,
                fontSize = if (spec.isNarrow) 20.sp else 21.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun SettingsSectionLabel(text: String, color: Color) {
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 2.dp)
        )
    }

    @Composable
    private fun SettingsSurfaceCard(
        surface: Color,
        borderColor: Color,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = surface),
            shape = RoundedCornerShape(26.dp),
            border = null
        ) {
            content()
        }
    }

    @Composable
    private fun SettingsMenuGroup(
        surface: Color,
        borderColor: Color,
        dividerColor: Color,
        modifier: Modifier = Modifier,
        items: List<SettingsMenuItem>,
        titleColor: Color,
        onClick: (SettingsRoute) -> Unit
    ) {
        SettingsSurfaceCard(
            surface = surface,
            borderColor = borderColor,
            modifier = modifier
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsMenuRow(
                        item = item,
                        titleColor = titleColor,
                        onClick = { onClick(item.route) }
                    )
                    if (index != items.lastIndex) {
                        SettingsInfoDivider(dividerColor, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingsMenuRow(
        item: SettingsMenuItem,
        titleColor: Color,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val interactionSource = remember { MutableInteractionSource() }
        val (backgroundColor, triggerHighlight) = rememberSettingsTapHighlightColor(
            interactionSource = interactionSource,
            isDark = isDark,
            label = "settingsMenuRow"
        )
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onClick()
            },
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .height(60.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = titleColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = titleColor.copy(alpha = 0.46f),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(18.dp)
                        .graphicsLayer { rotationZ = -90f }
                )
            }
        }
    }

    @Composable
    private fun SettingsSelectRow(
        title: String,
        selected: Boolean,
        titleColor: Color,
        accentColor: Color,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val interactionSource = remember { MutableInteractionSource() }
        val (backgroundColor, triggerHighlight) = rememberSettingsTapHighlightColor(
            interactionSource = interactionSource,
            isDark = isDark,
            label = "settingsSelectRow_$title"
        )
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onClick()
            },
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (selected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sheet_check),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingsToggleRow(
        title: String,
        checked: Boolean,
        titleColor: Color,
        subtitleColor: Color,
        accentColor: Color,
        surface: Color,
        subtitle: String? = null,
        verticalPadding: androidx.compose.ui.unit.Dp = 18.dp,
        onCheckedChange: (Boolean) -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val interactionSource = remember { MutableInteractionSource() }
        val (backgroundColor, triggerHighlight) = rememberSettingsTapHighlightColor(
            interactionSource = interactionSource,
            isDark = isDark,
            label = "settingsToggleRow_$title"
        )
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onCheckedChange(!checked)
            },
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp, vertical = verticalPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            color = subtitleColor,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                SettingsToggleSwitch(
                    checked = checked,
                    enabled = true,
                    accentColor = accentColor,
                    surface = surface,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }

    @Composable
    private fun SettingsThemeAccentRow(
        title: String,
        accentColor: Color,
        selected: Boolean,
        titleColor: Color,
        selectedColor: Color,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val interactionSource = remember { MutableInteractionSource() }
        val (backgroundColor, triggerHighlight) = rememberSettingsTapHighlightColor(
            interactionSource = interactionSource,
            isDark = isDark,
            label = "settingsThemeAccentRow_$title"
        )
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onClick()
            },
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 18.dp, height = 18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor)
                )
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .weight(1f)
                )
                if (selected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sheet_check),
                        contentDescription = null,
                        tint = selectedColor,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(18.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingsFeatureStatusRow(
        title: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        titleColor: Color,
        subtitleColor: Color,
        accentColor: Color,
        surface: Color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (checked) "已启用，支持创建与分析流程" else "已关闭，页面内 AI 入口将隐藏",
                    color = subtitleColor,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            SettingsToggleSwitch(
                checked = checked,
                enabled = enabled,
                accentColor = accentColor,
                surface = surface,
                onCheckedChange = onCheckedChange
            )
        }
    }

    @Composable
    private fun SettingsInlineConfigField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        titleColor: Color,
        subtitleColor: Color,
        accentColor: Color = Color.Unspecified,
        isSecret: Boolean = false
    ) {
        var secretVisible by remember(isSecret) { mutableStateOf(!isSecret) }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = titleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = if (isSecret && !secretVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                textStyle = TextStyle(
                    color = titleColor,
                    fontSize = 16.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                color = subtitleColor,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                    if (isSecret) {
                        Icon(
                            imageVector = if (secretVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (secretVisible) "隐藏 API 密钥" else "显示 API 密钥",
                            tint = if (accentColor != Color.Unspecified) accentColor.copy(alpha = 0.74f) else subtitleColor,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(22.dp)
                                .clickable { secretVisible = !secretVisible }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingsHelpLinkRow(
        text: String,
        color: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        PressScaleBox(
            onClick = onClick,
            modifier = modifier,
            pressedScale = 1f
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = null,
                    tint = color.copy(alpha = 0.9f),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = text,
                    color = color,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    @Composable
    private fun SettingsDisclosureRow(
        title: String,
        titleColor: Color,
        expanded: Boolean,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val interactionSource = remember { MutableInteractionSource() }
        val (backgroundColor, triggerHighlight) = rememberSettingsTapHighlightColor(
            interactionSource = interactionSource,
            isDark = isDark,
            label = "settingsDisclosureRow"
        )
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onClick()
            },
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .height(60.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = titleColor.copy(alpha = 0.46f),
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer { rotationZ = if (expanded) 0f else -90f }
                )
            }
        }
    }

    @Composable
    private fun SettingsGuideLinkRow(
        title: String,
        subtitle: String,
        titleColor: Color,
        subtitleColor: Color,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val interactionSource = remember { MutableInteractionSource() }
        val (backgroundColor, triggerHighlight) = rememberSettingsTapHighlightColor(
            interactionSource = interactionSource,
            isDark = isDark,
            label = "settingsGuideLinkRow"
        )
        PressScaleBox(
            onClick = {
                triggerHighlight()
                onClick()
            },
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 1f,
            interactionSource = interactionSource
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = subtitleColor,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = titleColor.copy(alpha = 0.46f),
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(18.dp)
                        .graphicsLayer { rotationZ = -90f }
                )
            }
        }
    }

    @Composable
    private fun SettingsGuideStepRow(
        index: String,
        title: String,
        subtitle: String,
        titleColor: Color,
        subtitleColor: Color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = index,
                color = titleColor.copy(alpha = 0.92f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 1.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    @Composable
    private fun SettingsModelPresetCard(
        surface: Color,
        borderColor: Color,
        dividerColor: Color,
        titleColor: Color,
        subtitleColor: Color,
        accentColor: Color,
        selectedPreset: String,
        customModel: String,
        options: List<SettingsModelOption>,
        onSelect: (String) -> Unit,
        onCustomModelChange: (String) -> Unit
    ) {
        var customEditorExpanded by remember { mutableStateOf(false) }
        SettingsSurfaceCard(
            surface = surface,
            borderColor = borderColor,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Column {
                options.forEachIndexed { index, option ->
                    val isDark = isSystemInDarkTheme()
                    val customExpanded =
                        option.preset == SettingsStore.MODEL_PRESET_CUSTOM &&
                            selectedPreset == SettingsStore.MODEL_PRESET_CUSTOM &&
                            customEditorExpanded
                    val selected = selectedPreset == option.preset
                    val interactionSource = remember(option.preset) { MutableInteractionSource() }
                    val (optionBackground, triggerHighlight) = rememberSettingsTapHighlightColor(
                        interactionSource = interactionSource,
                        isDark = isDark,
                        label = "settingsModelOption_${option.preset}"
                    )
                    PressScaleBox(
                        onClick = {
                            triggerHighlight()
                            if (option.preset == SettingsStore.MODEL_PRESET_CUSTOM) {
                                if (selectedPreset == SettingsStore.MODEL_PRESET_CUSTOM) {
                                    customEditorExpanded = !customEditorExpanded
                                } else {
                                    customEditorExpanded = false
                                    onSelect(option.preset)
                                }
                            } else {
                                customEditorExpanded = false
                                onSelect(option.preset)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        pressedScale = 1f,
                        interactionSource = interactionSource
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(optionBackground)
                                .padding(
                                    start = 20.dp,
                                    top = 18.dp,
                                    end = 20.dp,
                                    bottom = if (customExpanded) 10.dp else 18.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.title,
                                        color = titleColor,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            Text(
                                text = option.subtitle,
                                color = subtitleColor,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                if (selected) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_sheet_check),
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    if (index != options.lastIndex) {
                        SettingsInfoDivider(
                            color = dividerColor,
                            startInset = 20.dp,
                            endInset = 20.dp,
                            thickness = 0.65.dp
                        )
                    }
                }
                AnimatedVisibility(
                    visible = selectedPreset == SettingsStore.MODEL_PRESET_CUSTOM && customEditorExpanded,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    ) + expandVertically(
                        animationSpec = tween(durationMillis = 170, easing = FastOutSlowInEasing),
                        expandFrom = Alignment.Top
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 110)
                    ) + shrinkVertically(
                        animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing),
                        shrinkTowards = Alignment.Top
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 18.dp)
                    ) {
                        SettingsInlineModelInputBar(
                            value = customModel,
                            onValueChange = onCustomModelChange,
                            placeholder = BuildConfig.OPENAI_MODEL,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            accentColor = accentColor,
                            onDone = { customEditorExpanded = false }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingsBulletNote(
        text: String,
        color: Color,
        fontSize: androidx.compose.ui.unit.TextUnit = 12.sp,
        lineHeight: androidx.compose.ui.unit.TextUnit = 18.sp
    ) {
        Text(
            text = "• $text",
            color = color,
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }

    @Composable
    private fun rememberSettingsTapHighlightColor(
        interactionSource: MutableInteractionSource,
        isDark: Boolean,
        label: String
    ): Pair<Color, () -> Unit> {
        val scope = rememberCoroutineScope()
        val pressed by interactionSource.collectIsPressedAsState()
        var holdHighlight by remember { mutableStateOf(false) }
        var holdJob by remember { mutableStateOf<Job?>(null) }

        val highlightActive = pressed || holdHighlight
        val highlightFactor by animateFloatAsState(
            targetValue = if (highlightActive) 1f else 0f,
            animationSpec = if (highlightActive) {
                tween(durationMillis = 75)
            } else {
                tween(durationMillis = 220, easing = FastOutSlowInEasing)
            },
            label = "${label}Factor"
        )

        val backgroundColor = if (isDark) {
            Color.White.copy(alpha = 0.055f * highlightFactor)
        } else {
            Color.Black.copy(alpha = 0.04f * highlightFactor)
        }

        val triggerHighlight = {
            holdJob?.cancel()
            holdHighlight = true
            holdJob = scope.launch {
                delay(150)
                holdHighlight = false
            }
        }
        return backgroundColor to triggerHighlight
    }

    private fun resolveModelPresetValue(
        preset: String,
        customModel: String,
        fallback: String
    ): String {
        return when (preset) {
            SettingsStore.MODEL_PRESET_CUSTOM -> customModel.ifBlank { BuildConfig.OPENAI_MODEL }
            SettingsStore.MODEL_IMAGE_FLASH,
            SettingsStore.MODEL_IMAGE_THINKING,
            SettingsStore.MODEL_TEXT_FLASH,
            SettingsStore.MODEL_TEXT_FLASH_47,
            SettingsStore.MODEL_MULTIMODAL_FLASH -> preset
            else -> fallback
        }
    }

    private fun appearanceThemeOptions(defaultSwatch: Color): List<SettingsThemeOption> {
        return listOf(
            SettingsThemeOption("默认", SettingsStore.THEME_ACCENT_DEFAULT, defaultSwatch),
            SettingsThemeOption("米灰", SettingsStore.THEME_ACCENT_WARM_GRAY, Color(0xFFB8AF9D)),
            SettingsThemeOption("便签黄", SettingsStore.THEME_ACCENT_NOTE_YELLOW, Color(0xFFFFC83D)),
            SettingsThemeOption("樱花粉", SettingsStore.THEME_ACCENT_SAKURA_PINK, Color(0xFFFF6B96)),
            SettingsThemeOption("天空蓝", SettingsStore.THEME_ACCENT_SKY_BLUE, Color(0xFF4AA3FF)),
            SettingsThemeOption("薄荷绿", SettingsStore.THEME_ACCENT_MINT_GREEN, Color(0xFF39D98A)),
            SettingsThemeOption("蜜桃橙", SettingsStore.THEME_ACCENT_PEACH_ORANGE, Color(0xFFFF9A3D)),
            SettingsThemeOption("薰衣草紫", SettingsStore.THEME_ACCENT_LAVENDER_PURPLE, Color(0xFFB587FF))
        )
    }

    @Composable
    private fun rememberLightThemeTokenColor(colorRes: Int): Color {
        val context = LocalContext.current
        return remember(context, colorRes) {
            val configuration = Configuration(context.resources.configuration).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
            }
            val lightContext = context.createConfigurationContext(configuration)
            Color(ContextCompat.getColor(lightContext, colorRes))
        }
    }

    @Composable
    private fun SettingsField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        fieldSurface: Color,
        borderColor: Color,
        titleColor: Color,
        subtitleColor: Color,
        accentColor: Color,
        isSecret: Boolean = false
    ) {
        var secretVisible by remember(isSecret) { mutableStateOf(!isSecret) }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = fieldSurface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    visualTransformation = if (isSecret && !secretVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    textStyle = TextStyle(
                        color = titleColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp)
                ) { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                color = subtitleColor,
                                fontSize = 15.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                innerTextField()
                            }
                            if (isSecret) {
                                Text(
                                    text = if (secretVisible) "隐藏" else "显示",
                                    color = accentColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .clickable { secretVisible = !secretVisible }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.ThemeChoicePill(
        text: String,
        selected: Boolean,
        onClick: () -> Unit,
        selectedColor: Color,
        selectedTextColor: Color,
        surface: Color,
        textColor: Color
    ) {
        PressScaleBox(
            onClick = onClick,
            modifier = Modifier.weight(1f),
            pressedScale = 1f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (selected) selectedColor else surface,
                        shape = RoundedCornerShape(999.dp)
                    )
            ) {
                Text(
                    text = text,
                    color = if (selected) selectedTextColor else textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 13.dp)
                )
            }
        }
    }

    @Composable
    private fun SettingsPrimaryAction(
        title: String,
        accentColor: Color,
        surface: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        PressScaleBox(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            pressedScale = 1f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(surface)
                    .padding(horizontal = 20.dp, vertical = 17.dp)
            ) {
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    @Composable
    private fun SettingsInlineModelInputBar(
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        titleColor: Color,
        subtitleColor: Color,
        accentColor: Color,
        onDone: () -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.035f))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = titleColor,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                color = subtitleColor,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            PressScaleBox(
                onClick = {
                    focusManager.clearFocus(force = true)
                    onDone()
                },
                pressedScale = 1f
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(accentColor)
                        .padding(horizontal = 22.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "完成",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingsDangerAction(
        title: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val isDark = isSystemInDarkTheme()
        val dangerColor = Color(0xFFFF5A52)
        val surface = if (isDark) {
            dangerColor.copy(alpha = 0.18f)
        } else {
            dangerColor.copy(alpha = 0.12f)
        }
        PressScaleBox(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            pressedScale = 1f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(surface)
                    .padding(horizontal = 20.dp, vertical = 17.dp)
            ) {
                Text(
                    text = title,
                    color = dangerColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    @Composable
    private fun SettingsToggleSwitch(
        checked: Boolean,
        enabled: Boolean,
        accentColor: Color,
        surface: Color,
        onCheckedChange: (Boolean) -> Unit
    ) {
        val trackColor by animateColorAsState(
            targetValue = if (checked) accentColor else surface,
            animationSpec = tween(durationMillis = 180),
            label = "settingsToggleTrack"
        )
        val thumbOffsetProgress by animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
            label = "settingsToggleThumb"
        )
        val travelPx = 24.dp.value * androidx.compose.ui.platform.LocalDensity.current.density

        PressScaleBox(
            onClick = {
                if (enabled) {
                    onCheckedChange(!checked)
                }
            },
            modifier = Modifier.alpha(if (enabled) 1f else 0.52f),
            pressedScale = 1f
        ) {
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 36.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(trackColor)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { translationX = thumbOffsetProgress * travelPx }
                        .clip(CircleShape)
                        .background(Color.White)
                        .align(Alignment.CenterStart)
                )
            }
        }
    }

    @Composable
    private fun SettingsInfoBlock(
        title: String,
        text: String,
        titleColor: Color,
        textColor: Color
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }

    @Composable
    private fun SettingsInfoPair(
        label: String,
        value: String,
        titleColor: Color,
        textColor: Color
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = label,
                color = titleColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                color = textColor,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }

    @Composable
    private fun SettingsInfoDivider(
        color: Color,
        modifier: Modifier = Modifier,
        startInset: androidx.compose.ui.unit.Dp = 0.dp,
        endInset: androidx.compose.ui.unit.Dp = 0.dp,
        thickness: androidx.compose.ui.unit.Dp = 0.65.dp
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = startInset, end = endInset)
                .height(thickness)
                .background(color)
        )
    }

    private fun maskedApiKey(value: String): String {
        if (value.isBlank()) {
            return "未填写"
        }
        if (value.length <= 8) {
            return "已填写"
        }
        return value.take(4) + " •••• " + value.takeLast(4)
    }

    private sealed interface SettingsRoute {
        val key: String

        data object Home : SettingsRoute {
            override val key: String = "home"
        }

        data object AiConfig : SettingsRoute {
            override val key: String = "ai_config"
        }

        data object AiModel : SettingsRoute {
            override val key: String = "ai_model"
        }

        data object AiGuide : SettingsRoute {
            override val key: String = "ai_guide"
        }

        data object Appearance : SettingsRoute {
            override val key: String = "appearance"
        }

        data object Data : SettingsRoute {
            override val key: String = "data"
        }

        companion object {
            fun fromKey(key: String): SettingsRoute {
                return when (key) {
                    AiConfig.key -> AiConfig
                    AiModel.key -> AiModel
                    AiGuide.key -> AiGuide
                    Appearance.key -> Appearance
                    Data.key -> Data
                    else -> Home
                }
            }
        }
    }

    private data class SettingsMenuItem(
        val title: String,
        val route: SettingsRoute
    )

    private data class SettingsModelOption(
        val preset: String,
        val title: String,
        val subtitle: String
    )

    private data class SettingsThemeOption(
        val title: String,
        val key: String,
        val swatchColor: Color
    )
}
