package com.han.nomemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
                onCustomModelChange = { value ->
                    settingsStore.apiModel = value
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
                onClearData = {
                    memoryStore.clearAll()
                    setResult(RESULT_OK)
                    Toast.makeText(this, "本地数据已清空", Toast.LENGTH_SHORT).show()
                },
                onTestApi = { baseUrl, apiKey, model, title, onResult ->
                    testApiConnection(baseUrl, apiKey, model, title, onResult)
                }
            )
        }
    }

    private fun testApiConnection(
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
                performApiConnectionTest(resolvedBaseUrl, resolvedApiKey, resolvedModel)
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
            val payload = JSONObject().apply {
                put("model", model)
                put("temperature", 0)
                put(
                    "messages",
                    JSONArray().apply {
                        put(
                            JSONObject().apply {
                                put("role", "user")
                                put("content", "Reply with OK only.")
                            }
                        )
                    }
                )
                put("max_tokens", 8)
            }

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
    @Composable
    private fun SettingsContent(
        currentRoute: SettingsRoute,
        onClose: () -> Unit,
        onNavigate: (SettingsRoute) -> Unit,
        onAiEnabledChange: (Boolean) -> Unit,
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onCustomModelChange: (String) -> Unit,
        onImageModelPresetChange: (String) -> Unit,
        onTextModelPresetChange: (String) -> Unit,
        onMultimodalModelPresetChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onClearData: () -> Unit,
        onTestApi: (String, String, String, String, (Boolean, String) -> Unit) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val pageBackground = palette.memoBgStart
        val cardSurface = if (isDark) noMemoCardSurfaceColor(true, Color(0xFF151717)) else noMemoCardSurfaceColor(false, Color.White)
        val softSurface = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF6F7FB)
        val titleColor = if (isDark) Color(0xFFF7F8FA) else Color(0xFF111111)
        val subtitleColor = if (isDark) Color.White.copy(alpha = 0.58f) else Color(0xFF8E8E93)
        val sectionLabelColor = if (isDark) Color.White.copy(alpha = 0.48f) else Color(0xFF8F939B)
        val dividerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8E8EE)
        val borderColor = if (isDark) Color.White.copy(alpha = 0.03f) else Color(0x14000000)
        var aiEnabled by remember { mutableStateOf(settingsStore.aiEnabled) }
        var baseUrl by remember { mutableStateOf(settingsStore.apiBaseUrl.ifBlank { BuildConfig.OPENAI_BASE_URL }) }
        var apiKey by remember { mutableStateOf(settingsStore.apiKey) }
        var customModel by remember { mutableStateOf(settingsStore.apiModel.ifBlank { BuildConfig.OPENAI_MODEL }) }
        var imageModelPreset by remember { mutableStateOf(settingsStore.imageModelPreset) }
        var textModelPreset by remember { mutableStateOf(settingsStore.textModelPreset) }
        var multimodalModelPreset by remember { mutableStateOf(settingsStore.multimodalModelPreset) }
        var themeMode by remember { mutableStateOf(settingsStore.themeMode) }
        var showClearConfirm by remember { mutableStateOf(false) }
        var testingApi by remember { mutableStateOf(false) }
        var showTestResult by remember { mutableStateOf(false) }
        var testResultSuccess by remember { mutableStateOf(false) }
        var testResultMessage by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground)
        ) {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = 10.dp,
                            end = spec.pageHorizontalPadding,
                            bottom = 24.dp
                        )
                        .verticalScroll(rememberScrollState())
                ) {
                    SettingsHeader(
                        spec = spec,
                        title = when (currentRoute) {
                            SettingsRoute.Home -> "设置"
                            SettingsRoute.AiConfig -> "AI 功能"
                            SettingsRoute.AiModel -> "模型配置"
                            SettingsRoute.AiGuide -> "AI 功能说明"
                            SettingsRoute.Appearance -> "显示与主题"
                            SettingsRoute.Data -> "本地数据管理"
                        },
                        onBack = onClose,
                        titleColor = titleColor,
                    )
                    SettingsRouteBody(
                        currentRoute = currentRoute,
                        sectionLabelColor = sectionLabelColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        cardSurface = cardSurface,
                        softSurface = softSurface,
                        dividerColor = dividerColor,
                        borderColor = borderColor,
                        palette = palette,
                        aiEnabled = aiEnabled,
                        baseUrl = baseUrl,
                        apiKey = apiKey,
                        customModel = customModel,
                        imageModelPreset = imageModelPreset,
                        textModelPreset = textModelPreset,
                        multimodalModelPreset = multimodalModelPreset,
                        themeMode = themeMode,
                        testingApi = testingApi,
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
                        onCustomModelChange = {
                            customModel = it
                            onCustomModelChange(it)
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
                        onShowClearConfirm = { showClearConfirm = true },
                        onRunApiTest = { modelName, title ->
                            if (!testingApi) {
                                testingApi = true
                                onTestApi(baseUrl, apiKey, modelName, title) { success, message ->
                                    testingApi = false
                                    testResultSuccess = success
                                    testResultMessage = message
                                    showTestResult = true
                                }
                            }
                        }
                    )
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
        dividerColor: Color,
        borderColor: Color,
        palette: NoMemoPalette,
        aiEnabled: Boolean,
        baseUrl: String,
        apiKey: String,
        customModel: String,
        imageModelPreset: String,
        textModelPreset: String,
        multimodalModelPreset: String,
        themeMode: String,
        testingApi: Boolean,
        onNavigate: (SettingsRoute) -> Unit,
        onAiEnabledChange: (Boolean) -> Unit,
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onCustomModelChange: (String) -> Unit,
        onImageModelPresetChange: (String) -> Unit,
        onTextModelPresetChange: (String) -> Unit,
        onMultimodalModelPresetChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onShowClearConfirm: () -> Unit,
        onRunApiTest: (String, String) -> Unit
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
                                accentColor = palette.accent
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
                                accentColor = palette.accent
                            )
                        }
                    }

                    SettingsHelpLinkRow(
                        text = "如何配置 AI 服务？",
                        color = palette.accent,
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
                        selectedPreset = imageModelPreset,
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
                                subtitle = "当前自定义模型：${customModel.ifBlank { BuildConfig.OPENAI_MODEL }}"
                            )
                        ),
                        onSelect = onImageModelPresetChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("文本处理模型", sectionLabelColor)
                    SettingsModelPresetCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        dividerColor = dividerColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        selectedPreset = textModelPreset,
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
                                subtitle = "当前自定义模型：${customModel.ifBlank { BuildConfig.OPENAI_MODEL }}"
                            )
                        ),
                        onSelect = onTextModelPresetChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    SettingsSectionLabel("多模态处理模型", sectionLabelColor)
                    SettingsModelPresetCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        dividerColor = dividerColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        selectedPreset = multimodalModelPreset,
                        options = listOf(
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_MULTIMODAL_FLASH,
                                title = "GLM-4.6V-Flash",
                                subtitle = "等价时间更短，更均衡的速度"
                            ),
                            SettingsModelOption(
                                preset = SettingsStore.MODEL_PRESET_CUSTOM,
                                title = "自定义模型",
                                subtitle = "当前自定义模型：${customModel.ifBlank { BuildConfig.OPENAI_MODEL }}"
                            )
                        ),
                        onSelect = onMultimodalModelPresetChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionLabel("自定义模型", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
                            SettingsInlineConfigField(
                                label = "模型名称",
                                value = customModel,
                                onValueChange = onCustomModelChange,
                                placeholder = BuildConfig.OPENAI_MODEL,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor
                            )
                        }
                    }

                    SettingsPrimaryAction(
                        title = if (testingApi) "正在测试..." else "测试图像 API",
                        modifier = Modifier.padding(top = 18.dp),
                        accentColor = palette.accent,
                        onClick = {
                            onRunApiTest(
                                resolveModelPresetValue(
                                    imageModelPreset,
                                    customModel,
                                    SettingsStore.MODEL_IMAGE_DEFAULT
                                ),
                                "图像 API"
                            )
                        }
                    )
                    SettingsPrimaryAction(
                        title = if (testingApi) "正在测试..." else "测试文本 API",
                        modifier = Modifier.padding(top = 10.dp),
                        accentColor = palette.accent,
                        onClick = {
                            onRunApiTest(
                                resolveModelPresetValue(
                                    textModelPreset,
                                    customModel,
                                    SettingsStore.MODEL_TEXT_DEFAULT
                                ),
                                "文本 API"
                            )
                        }
                    )
                    SettingsPrimaryAction(
                        title = if (testingApi) "正在测试..." else "测试多模态 API",
                        modifier = Modifier.padding(top = 10.dp),
                        accentColor = palette.accent,
                        onClick = {
                            onRunApiTest(
                                resolveModelPresetValue(
                                    multimodalModelPreset,
                                    customModel,
                                    SettingsStore.MODEL_MULTIMODAL_DEFAULT
                                ),
                                "多模态 API"
                            )
                        }
                    )

                    Column(
                        modifier = Modifier.padding(top = 16.dp, start = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SettingsBulletNote("图像模型：用于分析图片内容", subtitleColor)
                        SettingsBulletNote("文本模型：用于分析文本内容", subtitleColor)
                        SettingsBulletNote("多模态模型：用于同时分析图片和文字", subtitleColor)
                        SettingsBulletNote("在使用 AI 功能之前，请先完成 API 测试", subtitleColor)
                    }
                }
            }

            SettingsRoute.AiGuide -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    SettingsSectionLabel("说明", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            SettingsInfoBlock(
                                title = "分析方式",
                                text = "新增记忆时可以提交文本或图片，应用会读取当前 AI 配置完成内容提取与摘要生成。",
                                titleColor = titleColor,
                                textColor = subtitleColor
                            )
                            SettingsInfoDivider(dividerColor)
                            SettingsInfoBlock(
                                title = "保存逻辑",
                                text = "点击确认后会先生成对应条目，分析完成后再热更新摘要、记忆与分析结果。",
                                titleColor = titleColor,
                                textColor = subtitleColor
                            )
                            SettingsInfoDivider(dividerColor)
                            SettingsInfoBlock(
                                title = "兜底策略",
                                text = "当网络或模型调用失败时，应用会使用本地 fallback，避免普通录入流程被阻塞。",
                                titleColor = titleColor,
                                textColor = subtitleColor
                            )
                        }
                    }
                }
            }

            SettingsRoute.Appearance -> {
                Column {
                    Spacer(modifier = Modifier.height(22.dp))
                    SettingsSectionLabel("显示模式", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ThemeChoicePill(
                                    text = "跟随系统",
                                    selected = themeMode == SettingsStore.THEME_SYSTEM,
                                    onClick = { onThemeModeChange(SettingsStore.THEME_SYSTEM) },
                                    selectedColor = palette.accent,
                                    selectedTextColor = palette.onAccent,
                                    surface = softSurface,
                                    textColor = titleColor
                                )
                                ThemeChoicePill(
                                    text = "浅色",
                                    selected = themeMode == SettingsStore.THEME_LIGHT,
                                    onClick = { onThemeModeChange(SettingsStore.THEME_LIGHT) },
                                    selectedColor = palette.accent,
                                    selectedTextColor = palette.onAccent,
                                    surface = softSurface,
                                    textColor = titleColor
                                )
                                ThemeChoicePill(
                                    text = "深色",
                                    selected = themeMode == SettingsStore.THEME_DARK,
                                    onClick = { onThemeModeChange(SettingsStore.THEME_DARK) },
                                    selectedColor = palette.accent,
                                    selectedTextColor = palette.onAccent,
                                    surface = softSurface,
                                    textColor = titleColor
                                )
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
                SettingsMenuItem("AI 功能说明", route = SettingsRoute.AiGuide)
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
                .padding(top = 10.dp)
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
            border = BorderStroke(1.dp, borderColor)
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
        PressScaleBox(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 0.985f
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
    private fun SettingsFeatureStatusRow(
        title: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        titleColor: Color,
        accentColor: Color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.18f),
                    disabledCheckedThumbColor = Color.White,
                    disabledCheckedTrackColor = accentColor.copy(alpha = 0.92f),
                    disabledUncheckedThumbColor = Color.White.copy(alpha = 0.72f),
                    disabledUncheckedTrackColor = Color.White.copy(alpha = 0.12f)
                )
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
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
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
                                fontSize = 14.sp,
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
            pressedScale = 0.985f
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
        PressScaleBox(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            pressedScale = 0.985f
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
    private fun SettingsModelPresetCard(
        surface: Color,
        borderColor: Color,
        dividerColor: Color,
        titleColor: Color,
        subtitleColor: Color,
        selectedPreset: String,
        options: List<SettingsModelOption>,
        onSelect: (String) -> Unit
    ) {
        SettingsSurfaceCard(
            surface = surface,
            borderColor = borderColor,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Column {
                options.forEachIndexed { index, option ->
                    PressScaleBox(
                        onClick = { onSelect(option.preset) },
                        modifier = Modifier.fillMaxWidth(),
                        pressedScale = 0.985f
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    color = titleColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = option.subtitle,
                                    color = subtitleColor,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (selectedPreset == option.preset) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_sheet_check),
                                    contentDescription = null,
                                    tint = Color(0xFF2E8BFF),
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                    if (index != options.lastIndex) {
                        SettingsInfoDivider(dividerColor, modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingsBulletNote(text: String, color: Color) {
        Text(
            text = "• $text",
            color = color,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
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
            pressedScale = 0.97f
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
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        NoMemoWideActionButton(
            text = title,
            iconRes = R.drawable.ic_sheet_check,
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            containerColor = accentColor,
            contentColor = palette.onAccent,
            borderColor = accentColor
        )
    }

    @Composable
    private fun SettingsDangerAction(
        title: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val dangerColor = Color(0xFFB42318)
        NoMemoWideActionButton(
            text = title,
            iconRes = R.drawable.ic_nm_delete,
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            containerColor = dangerColor,
            contentColor = Color.White,
            borderColor = dangerColor
        )
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
    private fun SettingsInfoDivider(color: Color, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
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
}
