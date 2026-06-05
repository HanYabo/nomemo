package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiReanalysisSupportTest {
    @Test
    fun `failed reanalysis record preserves existing memory content`() {
        val factsJson = """{"domain":"pickup","pickupCode":"7904"}"""
        val baseRecord = MemoryRecord(
            "record-1",
            100L,
            MemoryRecord.MODE_AI,
            "Original title",
            "Original summary",
            "source",
            "note",
            "file:///image.jpg",
            "Original analysis",
            "Original memory",
            "cloud",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_PICKUP,
            "Pickup",
            123L,
            false,
            false,
            factsJson,
            "",
            "",
            MemoryRecord.LIVE_STATUS_ACTIVE
        )

        val failed = buildFailedReanalysisRecord(
            baseRecord = baseRecord,
            costMode = AiCostMode.ECONOMY,
            attemptCount = 3,
            attemptLimit = 3,
            failureStage = AiFailureStage.CLOUD_REQUEST,
            failureMessage = "Cloud AI request failed httpStatus=500"
        )
        val state = AiAnalysisStateJson.parse(failed.aiAnalysisStateJson)

        assertEquals(baseRecord.title, failed.title)
        assertEquals(baseRecord.summary, failed.summary)
        assertEquals(baseRecord.analysis, failed.analysis)
        assertEquals(baseRecord.memory, failed.memory)
        assertEquals(baseRecord.categoryCode, failed.categoryCode)
        assertEquals(baseRecord.structuredFactsJson, failed.structuredFactsJson)
        assertEquals(MemoryRecord.LIVE_STATUS_ACTIVE, failed.liveStatusState)
        assertEquals("", failed.aiVisualStateJson)
        assertTrue(state?.status == "failed")
        assertEquals(AiOperationKind.REANALYZE, state?.operationKind)
        assertEquals(3, state?.attemptCount)
        assertEquals(3, state?.attemptLimit)
        assertEquals(AiFailureStage.CLOUD_REQUEST, state?.failureStage)
        assertEquals("Cloud AI request failed httpStatus=500", state?.failureMessage)
    }

    @Test
    fun `failure descriptions distinguish non retryable image and model failures`() {
        assertEquals(
            "原图片无法读取，已保留原记忆",
            describeAiReanalysisFailure(AiFailureStage.IMAGE_INPUT, null)
        )
        assertEquals(
            "当前图片模型不支持图片输入，已保留原记忆",
            describeAiReanalysisFailure(AiFailureStage.MODEL_CAPABILITY, "image_input_unsupported")
        )
        assertEquals(
            "AI 返回格式异常，已保留原记忆",
            describeAiReanalysisFailure(AiFailureStage.JSON_REPAIR, "Repair output is still invalid")
        )
    }

    @Test
    fun `failure dialog explains retried server errors and non retryable client errors`() {
        val serverState = AiAnalysisState(
            status = "failed",
            operationKind = AiOperationKind.REANALYZE,
            costMode = AiCostMode.STANDARD,
            attemptCount = 3,
            attemptLimit = 3,
            failureStage = AiFailureStage.CLOUD_REQUEST,
            failureMessage = "Cloud AI request failed httpStatus=500"
        )
        val clientState = serverState.copy(
            attemptCount = 1,
            attemptLimit = 3,
            failureMessage = "Cloud AI request failed httpStatus=401"
        )

        val serverMessage = describeAiAnalysisFailureDialog(serverState)
        val clientMessage = describeAiAnalysisFailureDialog(clientState)

        assertTrue(serverMessage.contains("已尝试 3/3 次"))
        assertTrue(serverMessage.contains("属于可重试的临时错误"))
        assertTrue(clientMessage.contains("服务返回 401"))
        assertTrue(clientMessage.contains("盲目重试不会修复"))
    }
}
