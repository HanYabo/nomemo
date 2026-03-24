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
                onSave = { baseUrl, apiKey, model, themeMode, visualIntensity ->
                    settingsStore.apiBaseUrl = baseUrl
                    settingsStore.apiKey = apiKey
                    settingsStore.apiModel = model
                    settingsStore.themeMode = themeMode
                    settingsStore.visualIntensity = visualIntensity
                    settingsStore.applyThemeMode()
                    setResult(RESULT_OK)
                    Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
                    recreate()
                },
                onClearData = {
                    memoryStore.clearAll()
                    setResult(RESULT_OK)
                    Toast.makeText(this, R.string.settings_data_cleared, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    @Composable
    private fun SettingsContent(
        onBack: () -> Unit,
        onSave: (String, String, String, String, String) -> Unit,
        onClearData: () -> Unit
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
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_check,
                                contentDescription = getString(R.string.save_record_desc),
                                onClick = { onSave(baseUrl, apiKey, model, themeMode, visualIntensity) }
                            )
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
                                onValueChange = { baseUrl = it },
                                placeholder = BuildConfig.OPENAI_BASE_URL,
                                fieldSurface = fieldSurface,
                                borderColor = subtleBorder
                            )
                            SettingField(
                                label = getString(R.string.settings_api_key),
                                value = apiKey,
                                onValueChange = { apiKey = it },
                                placeholder = "sk-...",
                                fieldSurface = fieldSurface,
                                borderColor = subtleBorder
                            )
                            SettingField(
                                label = getString(R.string.settings_model),
                                value = model,
                                onValueChange = { model = it },
                                placeholder = BuildConfig.OPENAI_MODEL,
                                fieldSurface = fieldSurface,
                                borderColor = subtleBorder
                            )
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
                                    },
                                    ChoiceItem(getString(R.string.settings_theme_light), themeMode == SettingsStore.THEME_LIGHT) {
                                        themeMode = SettingsStore.THEME_LIGHT
                                    }
                                ),
                                secondRow = listOf(
                                    ChoiceItem(getString(R.string.settings_theme_dark), themeMode == SettingsStore.THEME_DARK) {
                                        themeMode = SettingsStore.THEME_DARK
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
                                    },
                                    ChoiceItem(getString(R.string.settings_visual_normal), visualIntensity == SettingsStore.VISUAL_NORMAL) {
                                        visualIntensity = SettingsStore.VISUAL_NORMAL
                                    }
                                ),
                                secondRow = listOf(
                                    ChoiceItem(getString(R.string.settings_visual_strong), visualIntensity == SettingsStore.VISUAL_STRONG) {
                                        visualIntensity = SettingsStore.VISUAL_STRONG
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
