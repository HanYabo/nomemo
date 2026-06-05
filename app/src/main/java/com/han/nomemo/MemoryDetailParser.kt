package com.han.nomemo

object MemoryDetailParser {
    @JvmStatic
    fun parseStructuredPickupInfo(record: MemoryRecord): StructuredPickupInfo? {
        return MemoryUnderstandingPipeline.presentationFor(record)
    }
}
