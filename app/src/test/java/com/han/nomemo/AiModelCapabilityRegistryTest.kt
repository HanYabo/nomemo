package com.han.nomemo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiModelCapabilityRegistryTest {
    @Test
    fun `text flash model does not advertise image input`() {
        val capabilities = AiModelCapabilityRegistry.resolve(SettingsStore.MODEL_TEXT_FLASH)

        assertFalse(capabilities.supportsImageInput())
        assertTrue(capabilities.supportsResponseFormatJson())
        assertTrue(capabilities.supportsSystemRole())
    }

    @Test
    fun `runtime override can disable response format for a model`() {
        AiModelCapabilityRegistry.clearRuntimeOverridesForTest()
        AiModelCapabilityRegistry.markResponseFormatUnsupported(SettingsStore.MODEL_MULTIMODAL_FLASH)

        val capabilities = AiModelCapabilityRegistry.resolve(SettingsStore.MODEL_MULTIMODAL_FLASH)

        assertFalse(capabilities.supportsResponseFormatJson())
        assertTrue(capabilities.supportsImageInput())
        AiModelCapabilityRegistry.clearRuntimeOverridesForTest()
    }
}
