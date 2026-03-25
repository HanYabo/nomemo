package com.han.nomemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class SettingsActivity : BaseComposeActivity() {
    private lateinit var settingsStore: SettingsStore
    private lateinit var memoryStore: MemoryStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsStore = SettingsStore(this)
        memoryStore = MemoryStore(this)
        setContent {
            SettingsContent(
                onBack = { finish() },
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
                onVisualIntensityChange = { value ->
                    settingsStore.visualIntensity = value
                    setResult(RESULT_OK)
                },
                onClearData = {
                    memoryStore.clearAll()
                    setResult(RESULT_OK)
                    Toast.makeText(this, R.string.settings_data_cleared, Toast.LENGTH_SHORT).show()
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
            onResult(false, "请先填写完整的 Base URL、API Key 和 Model。")
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
                        onResult(false, error.message ?: "请求失败，请检查网络或配置。")
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
                throw IllegalStateException("连接失败（HTTP $code）。${extractErrorMessage(responseBody)}")
            }

            val responseJson = JSONObject(responseBody)
            val choices = responseJson.optJSONArray("choices")
            if (choices == null || choices.length() == 0) {
                throw IllegalStateException("接口已连通，但返回内容不符合预期。")
            }

            "连接成功，当前 API 配置可以正常访问。"
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
            return "请检查 Base URL、API Key、Model 或网络连接。"
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

    @Composable
    private fun SettingsContent(
        onBack: () -> Unit,
        onBaseUrlChange: (String) -> Unit,
        onApiKeyChange: (String) -> Unit,
        onModelChange: (String) -> Unit,
        onThemeModeChange: (String) -> Unit,
        onVisualIntensityChange: (String) -> Unit,
        onClearData: () -> Unit,
        onTestApi: (String, String, String, (Boolean, String) -> Unit) -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val isDark = isSystemInDarkTheme()
        val sectionSurface = if (isDark) Color(0xFF181B20).copy(alpha = 0.98f) else Color.White.copy(alpha = 0.97f)
        val fieldSurface = if (isDark) Color(0xFF111418) else Color(0xFFF9FAFC)
        val subtleBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0x12000000)
        val dangerSurface = if (isDark) Color(0xFF231618) else Color.White.copy(alpha = 0.96f)
        val dangerText = if (isDark) Color(0xFFFF8A80) else Color(0xFFB42318)
        var baseUrl by remember { mutableStateOf(settingsStore.apiBaseUrl.ifBlank { BuildConfig.OPENAI_BASE_URL }) }
        var apiKey by remember { mutableStateOf(settingsStore.apiKey) }
        var model by remember { mutableStateOf(settingsStore.apiModel.ifBlank { BuildConfig.OPENAI_MODEL }) }
        var themeMode by remember { mutableStateOf(settingsStore.themeMode) }
        var visualIntensity by remember { mutableStateOf(settingsStore.visualIntensity) }
        var showClearConfirm by remember { mutableStateOf(false) }
        var testingApi by remember { mutableStateOf(false) }
        var showTestResult by remember { mutableStateOf(false) }
        var testResultSuccess by remember { mutableStateOf(false) }
        var testResultMessage by remember { mutableStateOf("") }

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = spec.pageTopPadding,
                            end = spec.pageHorizontalPadding,
                            bottom = 18.dp
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_close,
                                contentDescription = getString(R.string.back),
                                onClick = onBack
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 14.dp)
                            ) {
                                Text(
                                    text = getString(R.string.settings_title),
                                    color = palette.textPrimary,
                                    fontSize = if (spec.isNarrow) 24.sp else 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = getString(R.string.settings_theme_section),
                                    color = palette.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        SettingsSection(
                            title = getString(R.string.settings_api_section),
                            modifier = Modifier.padding(top = 18.dp),
                            sectionSurface = sectionSurface,
                            borderColor = subtleBorder
                        ) {
                            SettingField(
                                label = getString(R.string.settings_base_url),
                                value = baseUrl,
                                onValueChange = {
                                    baseUrl = it
                                    onBaseUrlChange(it)
                                },
                                placeholder = BuildConfig.OPENAI_BASE_URL,
                                fieldSurface = fieldSurface,
                                borderColor = subtleBorder
                            )
                            SettingField(
                                label = getString(R.string.settings_api_key),
                                value = apiKey,
                                onValueChange = {
                                    apiKey = it
                                    onApiKeyChange(it)
                                },
                                placeholder = "sk-...",
                                fieldSurface = fieldSurface,
                                borderColor = subtleBorder
                            )
                            SettingField(
                                label = getString(R.string.settings_model),
                                value = model,
                                onValueChange = {
                                    model = it
                                    onModelChange(it)
                                },
                                placeholder = BuildConfig.OPENAI_MODEL,
                                fieldSurface = fieldSurface,
                                borderColor = subtleBorder
                            )
                            PressScaleBox(
                                onClick = {
                                    if (testingApi) return@PressScaleBox
                                    testingApi = true
                                    onTestApi(baseUrl, apiKey, model) { success, message ->
                                        testingApi = false
                                        testResultSuccess = success
                                        testResultMessage = message
                                        showTestResult = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                                    colors = CardDefaults.cardColors(containerColor = fieldSurface),
                                    border = BorderStroke(1.dp, subtleBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                                    ) {
                                        Text(
                                            text = if (testingApi) "测试连接中..." else "测试 API 连接",
                                            color = palette.textPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (testingApi) {
                                                "正在验证当前 Base URL、API Key 和 Model 是否可用。"
                                            } else {
                                                "不需要先保存，直接测试当前输入的 AI 配置。"
                                            },
                                            color = palette.textSecondary,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        SettingsSection(
                            title = getString(R.string.settings_theme_section),
                            modifier = Modifier.padding(top = 16.dp),
                            sectionSurface = sectionSurface,
                            borderColor = subtleBorder
                        ) {
                            Text(
                                text = getString(R.string.settings_theme),
                                color = palette.textSecondary,
                                fontSize = 12.sp
                            )
                            TwoLineChoiceGroup(
                                modifier = Modifier.padding(top = 10.dp),
                                firstRow = listOf(
                                    ChoiceItem(getString(R.string.settings_theme_system), themeMode == SettingsStore.THEME_SYSTEM) {
                                        themeMode = SettingsStore.THEME_SYSTEM
                                        onThemeModeChange(SettingsStore.THEME_SYSTEM)
                                    },
                                    ChoiceItem(getString(R.string.settings_theme_light), themeMode == SettingsStore.THEME_LIGHT) {
                                        themeMode = SettingsStore.THEME_LIGHT
                                        onThemeModeChange(SettingsStore.THEME_LIGHT)
                                    }
                                ),
                                secondRow = listOf(
                                    ChoiceItem(getString(R.string.settings_theme_dark), themeMode == SettingsStore.THEME_DARK) {
                                        themeMode = SettingsStore.THEME_DARK
                                        onThemeModeChange(SettingsStore.THEME_DARK)
                                    }
                                )
                            )

                            Text(
                                text = getString(R.string.settings_visual_intensity),
                                color = palette.textSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            TwoLineChoiceGroup(
                                modifier = Modifier.padding(top = 10.dp),
                                firstRow = listOf(
                                    ChoiceItem(getString(R.string.settings_visual_soft), visualIntensity == SettingsStore.VISUAL_SOFT) {
                                        visualIntensity = SettingsStore.VISUAL_SOFT
                                        onVisualIntensityChange(SettingsStore.VISUAL_SOFT)
                                    },
                                    ChoiceItem(getString(R.string.settings_visual_normal), visualIntensity == SettingsStore.VISUAL_NORMAL) {
                                        visualIntensity = SettingsStore.VISUAL_NORMAL
                                        onVisualIntensityChange(SettingsStore.VISUAL_NORMAL)
                                    }
                                ),
                                secondRow = listOf(
                                    ChoiceItem(getString(R.string.settings_visual_strong), visualIntensity == SettingsStore.VISUAL_STRONG) {
                                        visualIntensity = SettingsStore.VISUAL_STRONG
                                        onVisualIntensityChange(SettingsStore.VISUAL_STRONG)
                                    }
                                )
                            )
                        }

                        SettingsSection(
                            title = getString(R.string.settings_data_section),
                            modifier = Modifier.padding(top = 16.dp),
                            sectionSurface = sectionSurface,
                            borderColor = subtleBorder
                        ) {
                            GlassPanelText(
                                text = getString(R.string.settings_clear_data_desc),
                                modifier = Modifier.padding(top = 4.dp),
                                color = palette.textSecondary
                            )
                            PressScaleBox(
                                onClick = { showClearConfirm = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = dangerSurface),
                                    border = BorderStroke(1.dp, subtleBorder)
                                ) {
                                    Text(
                                        text = getString(R.string.settings_clear_data),
                                        color = dangerText,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showClearConfirm) {
            AlertDialog(
                onDismissRequest = { showClearConfirm = false },
                title = { Text(getString(R.string.settings_clear_confirm_title)) },
                text = { Text(getString(R.string.settings_clear_confirm_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearConfirm = false
                            onClearData()
                        }
                    ) {
                        Text(getString(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirm = false }) {
                        Text(getString(R.string.cancel))
                    }
                }
            )
        }

        if (showTestResult) {
            AlertDialog(
                onDismissRequest = { showTestResult = false },
                title = {
                    Text(if (testResultSuccess) "API 测试成功" else "API 测试失败")
                },
                text = {
                    Text(testResultMessage)
                },
                confirmButton = {
                    TextButton(onClick = { showTestResult = false }) {
                        Text("知道了")
                    }
                }
            )
        }
    }

    @Composable
    private fun SettingsSection(
        title: String,
        modifier: Modifier = Modifier,
        sectionSurface: Color,
        borderColor: Color,
        content: @Composable () -> Unit
    ) {
        val palette = rememberNoMemoPalette()
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = sectionSurface),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
                Text(
                    text = title,
                    color = palette.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun SettingField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String,
        fieldSurface: Color,
        borderColor: Color
    ) {
        val palette = rememberNoMemoPalette()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Text(
                text = label,
                color = palette.textSecondary,
                fontSize = 12.sp
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = fieldSurface),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        color = palette.textPrimary,
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
                                color = palette.textTertiary,
                                fontSize = 15.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        }
    }

    @Composable
    private fun ChoiceChip(text: String, selected: Boolean, onClick: () -> Unit) {
        GlassChip(
            text = text,
            selected = selected,
            onClick = onClick,
            modifier = Modifier.widthIn(min = 72.dp)
        )
    }

    @Composable
    private fun TwoLineChoiceGroup(
        modifier: Modifier = Modifier,
        firstRow: List<ChoiceItem>,
        secondRow: List<ChoiceItem>
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChoiceRow(firstRow)
            if (secondRow.isNotEmpty()) {
                ChoiceRow(secondRow)
            }
        }
    }

    @Composable
    private fun ChoiceRow(items: List<ChoiceItem>) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                ChoiceChip(
                    text = item.text,
                    selected = item.selected,
                    onClick = item.onClick
                )
            }
        }
    }

    private data class ChoiceItem(
        val text: String,
        val selected: Boolean,
        val onClick: () -> Unit
    )
}
