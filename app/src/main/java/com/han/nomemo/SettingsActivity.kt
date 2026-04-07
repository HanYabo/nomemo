package com.han.nomemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
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
                onBaseUrlChange = { value ->
                    settingsStore.apiBaseUrl = value
                    setResult(RESULT_OK)
                },
                onApiKeyChange = { value ->
                    settingsStore.apiKey = value
                    setResult(RESULT_OK)
                },
                onModelChange = { value ->
                    settingsStore.apiModel = value
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
                onTestApi = { baseUrl, apiKey, model, onResult ->
                    testApiConnection(baseUrl, apiKey, model, onResult)
                }
            )
        }
    }

    private fun testApiConnection(
        baseUrl: String,
        apiKey: String,
        model: String,
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
                    onSuccess = { onResult(true, it) },
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

            "连接成功，当前 API 配置可正常访问。"
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
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onModelChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onClearData: () -> Unit,
        onTestApi: (String, String, String, (Boolean, String) -> Unit) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val pageBackground = palette.memoBgStart
        val cardSurface = noMemoCardSurfaceColor(isDark)
        val softSurface = if (isDark) noMemoCardSurfaceColor(true) else Color(0xFFF7F7FA)
        val titleColor = if (isDark) Color(0xFFF7F8FA) else Color(0xFF111111)
        val subtitleColor = if (isDark) Color.White.copy(alpha = 0.58f) else Color(0xFF8E8E93)
        val sectionLabelColor = if (isDark) Color.White.copy(alpha = 0.42f) else Color(0xFF9A9AA1)
        val dividerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE8E8EE)
        val borderColor = if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF1F1F5)
        var baseUrl by remember { mutableStateOf(settingsStore.apiBaseUrl.ifBlank { BuildConfig.OPENAI_BASE_URL }) }
        var apiKey by remember { mutableStateOf(settingsStore.apiKey) }
        var model by remember { mutableStateOf(settingsStore.apiModel.ifBlank { BuildConfig.OPENAI_MODEL }) }
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
                            SettingsRoute.AiConfig -> "AI 功能设置"
                            SettingsRoute.AiGuide -> "AI 功能说明"
                            SettingsRoute.Appearance -> "显示与主题"
                            SettingsRoute.Data -> "数据管理"
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
                        baseUrl = baseUrl,
                        apiKey = apiKey,
                        model = model,
                        themeMode = themeMode,
                        testingApi = testingApi,
                        onNavigate = onNavigate,
                        onBaseUrlChange = {
                            baseUrl = it
                            onBaseUrlChange(it)
                        },
                        onApiKeyChange = {
                            apiKey = it
                            onApiKeyChange(it)
                        },
                        onModelChange = {
                            model = it
                            onModelChange(it)
                        },
                        onThemeModeChange = {
                            themeMode = it
                            onThemeModeChange(it)
                        },
                        onShowClearConfirm = { showClearConfirm = true },
                        onRunApiTest = {
                            if (!testingApi) {
                                testingApi = true
                                onTestApi(baseUrl, apiKey, model) { success, message ->
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
        baseUrl: String,
        apiKey: String,
        model: String,
        themeMode: String,
        testingApi: Boolean,
        onNavigate: (SettingsRoute) -> Unit,
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onModelChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onShowClearConfirm: () -> Unit,
        onRunApiTest: () -> Unit
    ) {
        when (currentRoute) {
            SettingsRoute.Home -> {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingsHomePage(
                        sectionLabelColor = sectionLabelColor,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        cardSurface = cardSurface,
                        dividerColor = dividerColor,
                        borderColor = borderColor,
                        onNavigate = onNavigate
                    )
                }
            }

            SettingsRoute.AiConfig -> {
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    SettingsSectionLabel("AI 功能", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SettingsField(
                                label = "Base URL",
                                value = baseUrl,
                                onValueChange = onBaseUrlChange,
                                placeholder = BuildConfig.OPENAI_BASE_URL,
                                fieldSurface = softSurface,
                                borderColor = borderColor,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                accentColor = palette.accent
                            )
                            SettingsField(
                                label = "API Key",
                                value = apiKey,
                                onValueChange = onApiKeyChange,
                                placeholder = "sk-...",
                                isSecret = true,
                                fieldSurface = softSurface,
                                borderColor = borderColor,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                accentColor = palette.accent
                            )
                            SettingsField(
                                label = "Model",
                                value = model,
                                onValueChange = onModelChange,
                                placeholder = BuildConfig.OPENAI_MODEL,
                                fieldSurface = softSurface,
                                borderColor = borderColor,
                                titleColor = titleColor,
                                subtitleColor = subtitleColor,
                                accentColor = palette.accent
                            )
                        }
                    }

                    SettingsPrimaryAction(
                        title = if (testingApi) "正在测试..." else "测试 API 连接",
                        subtitle = if (testingApi) {
                            "正在校验当前 Base URL、API Key 与 Model。"
                        } else {
                            "直接使用当前 AI 设置验证连接是否正常。"
                        },
                        modifier = Modifier.padding(top = 14.dp),
                        accentColor = palette.accent,
                        borderColor = borderColor,
                        onClick = onRunApiTest
                    )
                }
            }

            SettingsRoute.AiGuide -> {
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    SettingsSectionLabel("说明", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    Spacer(modifier = Modifier.height(18.dp))
                    SettingsSectionLabel("个性化", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Text(
                                text = "显示模式",
                                color = titleColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "选中后立即生效，不需要额外保存。",
                                color = subtitleColor,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 18.dp),
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
                    Spacer(modifier = Modifier.height(18.dp))
                    SettingsSectionLabel("数据管理", sectionLabelColor)
                    SettingsSurfaceCard(
                        surface = cardSurface,
                        borderColor = borderColor,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "本地数据管理",
                                color = titleColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
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
                        subtitle = "删除当前设备上的所有本地记录",
                        modifier = Modifier.padding(top = 14.dp),
                        borderColor = borderColor,
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
        subtitleColor: Color,
        cardSurface: Color,
        dividerColor: Color,
        borderColor: Color,
        onNavigate: (SettingsRoute) -> Unit
    ) {
        SettingsSectionLabel("AI 功能", sectionLabelColor)
        SettingsMenuGroup(
            surface = cardSurface,
            borderColor = borderColor,
            dividerColor = dividerColor,
            modifier = Modifier.padding(top = 12.dp),
            items = listOf(
                SettingsMenuItem("AI 功能设置", "配置 API 地址、密钥和模型", SettingsRoute.AiConfig),
                SettingsMenuItem("AI 功能说明", "了解文本与图片分析的工作方式", SettingsRoute.AiGuide)
            ),
            titleColor = titleColor,
            subtitleColor = subtitleColor,
            onClick = onNavigate
        )

        Spacer(modifier = Modifier.height(22.dp))
        SettingsSectionLabel("个性化", sectionLabelColor)
        SettingsMenuGroup(
            surface = cardSurface,
            borderColor = borderColor,
            dividerColor = dividerColor,
            modifier = Modifier.padding(top = 12.dp),
            items = listOf(
                SettingsMenuItem("显示与主题", "显示模式、主题切换", SettingsRoute.Appearance)
            ),
            titleColor = titleColor,
            subtitleColor = subtitleColor,
            onClick = onNavigate
        )

        Spacer(modifier = Modifier.height(22.dp))
        SettingsSectionLabel("数据管理", sectionLabelColor)
        SettingsMenuGroup(
            surface = cardSurface,
            borderColor = borderColor,
            dividerColor = dividerColor,
            modifier = Modifier.padding(top = 12.dp),
            items = listOf(
                SettingsMenuItem("本地数据管理", "清空当前设备上的本地数据", SettingsRoute.Data)
            ),
            titleColor = titleColor,
            subtitleColor = subtitleColor,
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
                fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun SettingsCircleBackButton(
        onClick: () -> Unit,
        iconTint: Color,
        modifier: Modifier = Modifier,
        size: androidx.compose.ui.unit.Dp = 56.dp
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        PressScaleBox(
            onClick = onClick,
            modifier = modifier.size(size),
            pressedScale = 0.96f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (isDark) palette.glassFill else Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sheet_chevron_down),
                    contentDescription = "返回",
                    tint = iconTint,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                        .graphicsLayer { rotationZ = 90f }
                )
            }
        }
    }

    @Composable
    private fun SettingsSectionLabel(text: String, color: Color) {
        Text(
            text = text,
            color = color,
            fontSize = 16.sp,
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
            shape = RoundedCornerShape(30.dp),
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
        subtitleColor: Color,
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
                        subtitleColor = subtitleColor,
                        onClick = { onClick(item.route) }
                    )
                    if (index != items.lastIndex) {
                        SettingsInfoDivider(dividerColor, modifier = Modifier.padding(horizontal = 24.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingsMenuRow(
        item: SettingsMenuItem,
        titleColor: Color,
        subtitleColor: Color,
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
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = titleColor,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.subtitle.isNotBlank()) {
                        Text(
                            text = item.subtitle,
                            color = subtitleColor,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_sheet_chevron_down),
                    contentDescription = null,
                    tint = subtitleColor,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(20.dp)
                        .graphicsLayer { rotationZ = -90f }
                )
            }
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
                shape = RoundedCornerShape(20.dp),
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
        subtitle: String,
        accentColor: Color,
        borderColor: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        Column(modifier = modifier.fillMaxWidth()) {
            NoMemoWideActionButton(
                text = title,
                iconRes = R.drawable.ic_sheet_check,
                onClick = onClick,
                containerColor = accentColor,
                contentColor = palette.onAccent,
                borderColor = accentColor
            )
            Text(
                text = subtitle,
                color = palette.textSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 6.dp, top = 8.dp)
            )
        }
    }

    @Composable
    private fun SettingsDangerAction(
        title: String,
        subtitle: String,
        borderColor: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val dangerColor = Color(0xFFB42318)
        Column(modifier = modifier.fillMaxWidth()) {
            NoMemoWideActionButton(
                text = title,
                iconRes = R.drawable.ic_nm_delete,
                onClick = onClick,
                containerColor = dangerColor,
                contentColor = Color.White,
                borderColor = dangerColor
            )
            Text(
                text = subtitle,
                color = dangerColor.copy(alpha = 0.78f),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 6.dp, top = 8.dp)
            )
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
        val subtitle: String,
        val route: SettingsRoute
    )
}



