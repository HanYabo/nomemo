package com.han.nomemo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsStoreTest {
    @Test
    fun `resolved default base url and key can satisfy ai configuration`() {
        assertTrue(
            SettingsStore.hasAiConfiguredValues(
                resolvedBaseUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions",
                resolvedApiKey = "default-key",
                resolvedImageModel = "GLM-4.6V-Flash",
                resolvedTextModel = "GLM-4.6-Flash",
                resolvedMultimodalModel = "GLM-4.6V-Flash"
            )
        )
    }

    @Test
    fun `missing resolved base url or api key is not configured`() {
        assertFalse(
            SettingsStore.hasAiConfiguredValues(
                resolvedBaseUrl = "",
                resolvedApiKey = "default-key",
                resolvedImageModel = "GLM-4.6V-Flash",
                resolvedTextModel = "GLM-4.6-Flash",
                resolvedMultimodalModel = "GLM-4.6V-Flash"
            )
        )
        assertFalse(
            SettingsStore.hasAiConfiguredValues(
                resolvedBaseUrl = BuildConfig.OPENAI_BASE_URL,
                resolvedApiKey = "",
                resolvedImageModel = "GLM-4.6V-Flash",
                resolvedTextModel = "GLM-4.6-Flash",
                resolvedMultimodalModel = "GLM-4.6V-Flash"
            )
        )
    }
}
