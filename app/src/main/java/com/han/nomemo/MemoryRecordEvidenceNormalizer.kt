package com.han.nomemo

object MemoryRecordEvidenceNormalizer {
    private val generatedPickupTitle = Regex(""".*(取件码|取餐码)$""")
    private val senderPattern = Regex("""(?:发件人|来自)\s*[:：]?\s*([^\n，,。;；]{2,32})""")
    private val subjectPattern = Regex("""(?:邮件标题|主题|标题)\s*[:：]\s*([^\n]{2,48})""")

    @JvmStatic
    fun normalize(record: MemoryRecord): MemoryRecord {
        val originalFacts = MemoryStructuredFactsJson.parse(record.structuredFactsJson)
        val cleanedSource = MemoryFactReconciler.cleanLegacyStructuredTemplateText(record.sourceText)
        val cleanedNote = MemoryFactReconciler.cleanLegacyStructuredTemplateText(record.note)
        val hadLegacyTemplate = cleanedSource != record.sourceText.orEmpty() ||
            cleanedNote != record.note.orEmpty()
        val shouldNormalizeFacts = originalFacts != null && (
            !originalFacts.pickupCode.isNullOrBlank() ||
                !originalFacts.location.isNullOrBlank() ||
                originalFacts.domain == "pickup" ||
                originalFacts.domain == "delivery" ||
                record.categoryCode == CategoryCatalog.CODE_LIFE_PICKUP ||
                record.categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY ||
                hadLegacyTemplate
            )
        if (!shouldNormalizeFacts && !hadLegacyTemplate) {
            return record
        }

        val evidence = listOf(
            originalFacts?.rawVisibleText,
            cleanedSource,
            cleanedNote
        ).filterNotNull().filter { it.isNotBlank() }.distinct().joinToString("\n")
        val normalizedFactsJson = if (shouldNormalizeFacts) {
            MemoryFactReconciler.sanitizeFactsAgainstEvidence(
                evidence,
                record.structuredFactsJson,
                record.categoryCode
            )
        } else {
            record.structuredFactsJson.orEmpty()
        }
        val normalizedFacts = MemoryStructuredFactsJson.parse(normalizedFactsJson)
        val removedCode = !originalFacts?.pickupCode.isNullOrBlank() &&
            originalFacts?.pickupCode != normalizedFacts?.pickupCode &&
            originalFacts?.pickupCodeEvidence != "manual_edit"
        val removedLocation = !originalFacts?.location.isNullOrBlank() &&
            originalFacts?.location != normalizedFacts?.location &&
            originalFacts?.locationEvidence != "manual_edit"
        val invalidStructuredFactsRemoved = removedCode || removedLocation
        val structuredFactsChanged = normalizedFactsJson != record.structuredFactsJson.orEmpty()
        val hasValidatedTransactionFacts = (
            normalizedFacts?.domain == "pickup" ||
                normalizedFacts?.domain == "delivery"
            ) &&
            !normalizedFacts.pickupCode.isNullOrBlank() &&
            normalizedFacts.pickupCodeConfidence >= 0.55

        val normalizedCategory = if (
            invalidStructuredFactsRemoved ||
            structuredFactsChanged ||
            hasValidatedTransactionFacts ||
            looksInformational(evidence)
        ) {
            MemoryFactReconciler.normalizeCategoryCode(
                record.categoryCode,
                normalizedFactsJson,
                evidence
            )
        } else {
            record.categoryCode ?: CategoryCatalog.CODE_QUICK_NOTE
        }
        val normalizedTitle = if (
            invalidStructuredFactsRemoved &&
            generatedPickupTitle.matches(record.title?.trim().orEmpty())
        ) {
            deriveInformationalTitle(record, originalFacts, evidence)
        } else {
            record.title
        }
        val normalizedSummary = if (
            removedCode &&
            looksGeneratedPickupSummary(record.summary, originalFacts?.pickupCode)
        ) {
            deriveSafeSummary(record, evidence)
        } else {
            record.summary
        }
        val normalizedGroup = CategoryCatalog.getGroupByCategoryCode(normalizedCategory)
        val normalizedCategoryName = CategoryCatalog.getCategoryName(normalizedCategory)

        if (
            cleanedSource == record.sourceText.orEmpty() &&
            cleanedNote == record.note.orEmpty() &&
            normalizedFactsJson == record.structuredFactsJson.orEmpty() &&
            normalizedCategory == record.categoryCode &&
            normalizedTitle == record.title &&
            normalizedSummary == record.summary &&
            normalizedGroup == record.categoryGroupCode &&
            normalizedCategoryName == record.categoryName
        ) {
            return record
        }

        return MemoryRecord(
            record.recordId,
            record.createdAt,
            record.mode,
            normalizedTitle,
            normalizedSummary,
            cleanedSource,
            cleanedNote,
            record.imageUri,
            record.analysis,
            record.memory,
            record.engine,
            normalizedGroup,
            normalizedCategory,
            normalizedCategoryName,
            record.reminderAt,
            record.isReminderDone,
            record.isArchived,
            normalizedFactsJson,
            record.aiAnalysisStateJson,
            record.aiVisualStateJson,
            record.liveStatusState
        )
    }

    private fun looksInformational(evidence: String): Boolean {
        return evidence.containsAny(
            "邮件", "邮箱", "发件人", "收件人", "主题", "邀请", "公告", "通知",
            "计划背景", "权益说明", "使用说明", "申请方式", "规则说明"
        )
    }

    private fun deriveInformationalTitle(
        record: MemoryRecord,
        facts: MemoryStructuredFacts?,
        evidence: String
    ): String {
        if (looksInformational(evidence)) {
            val sender = firstNonBlank(
                facts?.merchantOrCompany,
                senderPattern.find(evidence)?.groupValues?.getOrNull(1)
            )?.trim()?.take(24)
            if (!sender.isNullOrBlank()) {
                return "${sender}邀请邮件"
            }
            val subject = subjectPattern.find(evidence)?.groupValues?.getOrNull(1)
                ?.trim()
                ?.take(24)
            if (!subject.isNullOrBlank()) {
                return if (subject.contains("邀请")) subject else "${subject}邀请邮件"
            }
            return "活动邀请邮件"
        }
        return record.memory
            ?.lineSequence()
            ?.map { it.trim() }
            ?.firstOrNull { it.isNotBlank() }
            ?.take(24)
            ?: CategoryCatalog.getCategoryName(CategoryCatalog.CODE_QUICK_NOTE)
    }

    private fun looksGeneratedPickupSummary(summary: String?, oldCode: String?): Boolean {
        val value = summary?.trim().orEmpty()
        val code = oldCode?.trim().orEmpty()
        if (value.isBlank() || code.isBlank()) return false
        return value.startsWith("取件码 $code") ||
            value.startsWith("取餐码 $code") ||
            value.startsWith("取件码：$code") ||
            value.startsWith("取餐码：$code")
    }

    private fun deriveSafeSummary(record: MemoryRecord, evidence: String): String {
        val candidate = firstNonBlank(record.analysis, record.memory, evidence).orEmpty()
            .replace(Regex("""\s+"""), " ")
            .trim()
        if (candidate.isBlank()) return ""
        val sentence = Regex("""^.*?[。！？!?](?=\s|$|[\p{L}\d])""")
            .find(candidate)
            ?.value
            ?.trim()
            ?: candidate
        return sentence.take(80).trim()
    }
}

private fun String.containsAny(vararg keywords: String): Boolean {
    return keywords.any { contains(it, ignoreCase = true) }
}

private fun firstNonBlank(vararg values: String?): String? {
    return values.firstNotNullOfOrNull { value ->
        value?.trim()?.takeIf { it.isNotEmpty() }
    }
}
