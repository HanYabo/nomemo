package com.han.nomemo

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ThemePaletteOverrideTest {
    private val basePalette = NoMemoPalette(
        memoBgStart = Color(0xFFF5F5F5),
        memoBgMid = Color(0xFFF0F0F0),
        memoBgEnd = Color(0xFFEAEAEA),
        glassFill = Color(0x0D000000),
        glassFillSoft = Color(0x08000000),
        glassStroke = Color(0x1F000000),
        dockSurface = Color(0xFFF4F7FD),
        dockStroke = Color(0x9ED0D9E8),
        dockIndicator = Color(0xFF7B9BFF),
        dockGlow = Color(0xFFBFD4FF),
        dockFabSurface = Color(0xFFECF1F8),
        accent = Color(0xFF1A1A1A),
        onAccent = Color(0xFFFFFFFF),
        textPrimary = Color(0xFF101010),
        textSecondary = Color(0xFF5F5F5F),
        textTertiary = Color(0xFF8A8A8A),
        tagNoteBg = Color(0x12000000),
        tagNoteText = Color(0xFF222222),
        tagAiBg = Color(0x18000000),
        tagAiText = Color(0xFF222222)
    )

    @Test
    fun themeGlobalDisabled_keepsDefaultBackgrounds() {
        val result = applyNoMemoThemeOverrides(
            base = basePalette,
            isDark = false,
            themeState = NoMemoThemeState(
                themeMode = SettingsStore.THEME_SYSTEM,
                themeAccent = SettingsStore.THEME_ACCENT_SAKURA_PINK,
                themeGlobalEnabled = false,
                showDividers = true
            )
        )

        assertEquals(basePalette.memoBgStart, result.memoBgStart)
        assertEquals(basePalette.memoBgMid, result.memoBgMid)
        assertEquals(basePalette.memoBgEnd, result.memoBgEnd)
        assertEquals(basePalette.accent, result.accent)
        assertEquals(basePalette.glassFill, result.glassFill)
        assertEquals(basePalette.dockSurface, result.dockSurface)
        assertEquals(basePalette.tagAiBg, result.tagAiBg)
    }

    @Test
    fun nonDefaultAccent_onlyBackgroundTokensChange() {
        val result = applyNoMemoThemeOverrides(
            base = basePalette,
            isDark = false,
            themeState = NoMemoThemeState(
                themeMode = SettingsStore.THEME_LIGHT,
                themeAccent = SettingsStore.THEME_ACCENT_SKY_BLUE,
                themeGlobalEnabled = true,
                showDividers = true
            )
        )

        assertNotEquals(basePalette.memoBgStart, result.memoBgStart)
        assertNotEquals(basePalette.memoBgMid, result.memoBgMid)
        assertNotEquals(basePalette.memoBgEnd, result.memoBgEnd)
        assertEquals(basePalette.accent, result.accent)
        assertEquals(basePalette.glassFill, result.glassFill)
        assertEquals(basePalette.dockSurface, result.dockSurface)
        assertEquals(basePalette.tagAiBg, result.tagAiBg)
        assertEquals(basePalette.tagAiText, result.tagAiText)
    }

    @Test
    fun showDividersFalse_onlyDividerTokensBecomeTransparent() {
        val result = applyNoMemoThemeOverrides(
            base = basePalette,
            isDark = true,
            themeState = NoMemoThemeState(
                themeMode = SettingsStore.THEME_DARK,
                themeAccent = SettingsStore.THEME_ACCENT_MINT_GREEN,
                themeGlobalEnabled = true,
                showDividers = false
            )
        )

        assertEquals(Color.Transparent, result.glassStroke)
        assertEquals(Color.Transparent, result.dockStroke)
        assertEquals(basePalette.glassFill, result.glassFill)
        assertEquals(basePalette.dockSurface, result.dockSurface)
    }

    @Test
    fun defaultTheme_keepsDefaultBackgroundTokens() {
        val result = applyNoMemoThemeOverrides(
            base = basePalette,
            isDark = false,
            themeState = NoMemoThemeState(
                themeMode = SettingsStore.THEME_LIGHT,
                themeAccent = SettingsStore.THEME_ACCENT_DEFAULT,
                themeGlobalEnabled = true,
                showDividers = true
            )
        )

        assertEquals(basePalette.memoBgStart, result.memoBgStart)
        assertEquals(basePalette.memoBgMid, result.memoBgMid)
        assertEquals(basePalette.memoBgEnd, result.memoBgEnd)
    }
}
