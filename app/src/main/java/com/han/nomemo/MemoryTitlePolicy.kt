package com.han.nomemo

import java.util.Locale

data class MemoryTitleNormalization(
    val title: String,
    val structuredFactsJson: String
)

object MemoryTextCompactor {
    @JvmStatic
    fun compact(text: String?, fallback: String?, maxLength: Int): String {
        val value = text?.takeIf { it.isNotBlank() } ?: fallback.orEmpty()
        val single = value.replace('\r', ' ').replace('\n', ' ').trim()
        return if (single.length <= maxLength) single else single.take(maxLength) + "..."
    }

    @JvmStatic
    fun compactSummary(text: String?, fallback: String?): String {
        return compact(text, fallback, 42)
    }
}

object MemoryTitlePolicy {
    const val CURRENT_VERSION = 1
    const val SOURCE_GENERATED = "generated"
    const val SOURCE_MANUAL_EDIT = "manual_edit"
    const val MAX_DISPLAY_UNITS = 28

    private const val PICKUP_SUFFIX = "取餐"
    private const val DELIVERY_SUFFIX = "包裹取件"
    private const val INVITATION_EMAIL_SUFFIX = "邀请邮件"
    private const val EMAIL_SUFFIX = "邮件"

    private val sentencePunctuation = Regex("""[。！？!?；;，,：:]""")
    private val quantitySuffix = Regex(
        """(?i)\s*(?:[x×*]\s*\d+|共\s*\d+\s*(?:份|件|个|杯)|\d+\s*(?:份|件|个|杯))\s*$"""
    )
    private val bracketSuffix = Regex("""\s*[\(（【\[].*?[\)）】\]]\s*$""")
    private val labelPrefix = Regex(
        """^(?:标题|主题|商品|商品名|餐品|菜品|门店|店铺|商家|快递公司|物流公司|内容|摘要)\s*[:：]\s*"""
    )
    private val administrativePrefix = Regex("""^(?:.*(?:省|市|县|区))(?=[^省市县区]{2,}(?:店|门店)$)""")
    private val courierSuffix = Regex("""(?:快递|速运|物流|快运|快递公司)$""")
    private val generatedSentencePrefix = Regex(
        """^(?:这是|这是一|我在|用户在|已在|已经|记录了|保存了|帮我|请帮我|关于|收到一)"""
    )
    private val legacyPickupTitle = Regex(""".*(?:取件码|取餐码)$""")
    private val placeholderText = setOf("AI 分析中", "AI分析中", "正在加载", "记忆")
    private val genericLines = setOf(
        "订单详情",
        "订单信息",
        "商品信息",
        "配送信息",
        "取餐信息"
    )

    @JvmStatic
    fun resolveGeneratedTitle(
        categoryCode: String?,
        candidateTitle: String?,
        fallbackText: String?,
        structuredFactsJson: String?
    ): String {
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson)
        val evidence = listOfNotNull(
            facts?.rawVisibleText,
            fallbackText,
            candidateTitle
        ).joinToString("\n")

        val categoryTitle = when (categoryCode) {
            CategoryCatalog.CODE_LIFE_PICKUP -> pickupTitle(facts)
                ?: semanticTitle(candidateTitle, fallbackText)
            CategoryCatalog.CODE_LIFE_DELIVERY -> deliveryTitle(facts)
                ?: semanticTitle(candidateTitle, fallbackText)
            CategoryCatalog.CODE_WORK_SCHEDULE -> semanticTitle(candidateTitle, fallbackText)
            CategoryCatalog.CODE_WORK_TODO -> semanticTitle(candidateTitle, fallbackText)
            CategoryCatalog.CODE_LIFE_CARD -> semanticTitle(candidateTitle, fallbackText)
            CategoryCatalog.CODE_LIFE_TICKET -> semanticTitle(candidateTitle, fallbackText)
            else -> informationalTitle(facts, evidence)
                ?: semanticTitle(candidateTitle, fallbackText)
        }
        return categoryTitle
            ?.takeIf { it.isNotBlank() }
            ?.let(::compactToDisplayWidth)
            ?.takeIf { it.isNotBlank() }
            ?: CategoryCatalog.getCategoryName(categoryCode).ifBlank { "小记" }
    }

    @JvmStatic
    fun compactFallbackTitle(text: String?, fallback: String?): String {
        return semanticTitle(text, fallback)
            ?.let(::compactToDisplayWidth)
            ?.takeIf { it.isNotBlank() }
            ?: fallback?.trim().orEmpty().ifBlank { "小记" }
    }

    @JvmStatic
    fun markGeneratedTitle(structuredFactsJson: String?, title: String?): String {
        val normalizedTitle = normalizeSingleLine(title)
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson) ?: MemoryStructuredFacts()
        return MemoryStructuredFactsJson.toJson(
            facts.copy(
                titlePolicyVersion = CURRENT_VERSION,
                generatedTitle = normalizedTitle.takeIf { it.isNotBlank() },
                titleSource = SOURCE_GENERATED
            )
        )
    }

    @JvmStatic
    fun markManualTitle(structuredFactsJson: String?): String {
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson) ?: MemoryStructuredFacts()
        return MemoryStructuredFactsJson.toJson(
            facts.copy(
                titlePolicyVersion = CURRENT_VERSION,
                generatedTitle = facts.generatedTitle,
                titleSource = SOURCE_MANUAL_EDIT
            )
        )
    }

    @JvmStatic
    fun isManualTitle(structuredFactsJson: String?): Boolean {
        return MemoryStructuredFactsJson.parse(structuredFactsJson)?.titleSource == SOURCE_MANUAL_EDIT
    }

    @JvmStatic
    fun preserveManualTitleMetadata(
        previousStructuredFactsJson: String?,
        nextStructuredFactsJson: String?
    ): String {
        return if (isManualTitle(previousStructuredFactsJson)) {
            markManualTitle(nextStructuredFactsJson)
        } else {
            nextStructuredFactsJson.orEmpty()
        }
    }

    @JvmStatic
    fun normalizeHistorical(
        record: MemoryRecord,
        categoryCode: String?,
        structuredFactsJson: String?,
        evidenceText: String?
    ): MemoryTitleNormalization {
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson)
        if (facts?.titleSource == SOURCE_MANUAL_EDIT) {
            return MemoryTitleNormalization(record.title.orEmpty(), structuredFactsJson.orEmpty())
        }
        val currentTitle = normalizeSingleLine(record.title)
        val categoryCorrectedLegacyTitle = categoryCode != record.categoryCode &&
            legacyPickupTitle.matches(currentTitle)
        if (!categoryCorrectedLegacyTitle && !shouldNormalizeHistorical(record, facts, currentTitle)) {
            return MemoryTitleNormalization(currentTitle, structuredFactsJson.orEmpty())
        }
        val title = resolveGeneratedTitle(
            categoryCode,
            currentTitle,
            evidenceText,
            structuredFactsJson
        )
        return MemoryTitleNormalization(
            title = title,
            structuredFactsJson = markGeneratedTitle(structuredFactsJson, title)
        )
    }

    @JvmStatic
    fun shouldNormalizeHistorical(record: MemoryRecord): Boolean {
        val facts = MemoryStructuredFactsJson.parse(record.structuredFactsJson)
        return shouldNormalizeHistorical(record, facts, normalizeSingleLine(record.title))
    }

    @JvmStatic
    fun displayWidth(value: String?): Int {
        val text = value.orEmpty()
        var width = 0
        var offset = 0
        while (offset < text.length) {
            val codePoint = text.codePointAt(offset)
            width += when {
                Character.getType(codePoint) == Character.NON_SPACING_MARK.toInt() -> 0
                codePoint <= 0x7f -> 1
                else -> 2
            }
            offset += Character.charCount(codePoint)
        }
        return width
    }

    private fun pickupTitle(facts: MemoryStructuredFacts?): String? {
        val item = cleanSubject(facts?.itemName)
        if (!item.isNullOrBlank()) {
            return appendSuffix(item, PICKUP_SUFFIX)
        }
        val merchant = cleanMerchant(facts?.merchantOrCompany)
        return merchant?.let { appendSuffix(it, PICKUP_SUFFIX) }
    }

    private fun deliveryTitle(facts: MemoryStructuredFacts?): String? {
        val courier = cleanMerchant(facts?.merchantOrCompany)
            ?.replace(courierSuffix, "")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
        if (!courier.isNullOrBlank()) {
            return appendSuffix(courier, DELIVERY_SUFFIX)
        }
        val station = cleanMerchant(facts?.location)
        return station?.let { appendSuffix(it, DELIVERY_SUFFIX) }
    }

    private fun informationalTitle(facts: MemoryStructuredFacts?, evidence: String): String? {
        val normalizedEvidence = normalizeSingleLine(evidence)
        val isEmail = normalizedEvidence.containsAny("邮件", "邮箱", "发件人", "收件人", "主题")
        val isInvitation = normalizedEvidence.containsAny("邀请", "受邀", "邀约")
        if (!isEmail && !isInvitation) return null
        val sender = cleanMerchant(facts?.merchantOrCompany)
        return when {
            !sender.isNullOrBlank() && isInvitation -> appendSuffix(sender, INVITATION_EMAIL_SUFFIX)
            !sender.isNullOrBlank() -> appendSuffix(sender, EMAIL_SUFFIX)
            isInvitation -> INVITATION_EMAIL_SUFFIX
            else -> EMAIL_SUFFIX
        }
    }

    private fun semanticTitle(candidateTitle: String?, fallbackText: String?): String? {
        val candidate = normalizeSingleLine(candidateTitle)
            .takeIf(::isUsefulCandidate)
        if (candidate != null) {
            return cleanSubject(candidate)
        }
        val source = listOfNotNull(fallbackText, candidateTitle)
            .asSequence()
            .flatMap { it.lineSequence() }
            .map(::normalizeSingleLine)
            .filter { it.isNotBlank() && it !in genericLines }
            .map { it.substringBefore('。').substringBefore('！').substringBefore('？') }
            .map { it.substringBefore('，').substringBefore(',').substringBefore('；').substringBefore(';') }
            .map { it.replace(generatedSentencePrefix, "").trim() }
            .mapNotNull(::cleanSubject)
            .firstOrNull { it.isNotBlank() && it !in genericLines }
        return source
    }

    private fun isUsefulCandidate(value: String): Boolean {
        if (value.isBlank() || value in placeholderText || value in genericLines) return false
        if (displayWidth(value) > MAX_DISPLAY_UNITS) return false
        if (sentencePunctuation.containsMatchIn(value)) return false
        if (generatedSentencePrefix.containsMatchIn(value)) return false
        return true
    }

    private fun cleanSubject(value: String?): String? {
        var result = normalizeSingleLine(value)
        if (result.isBlank()) return null
        result = result
            .replace(labelPrefix, "")
            .replace(quantitySuffix, "")
            .replace(bracketSuffix, "")
            .substringBefore("｜")
            .substringBefore("|")
            .trim(' ', '-', '_', '·', '。', '，', ',', '；', ';', ':', '：')
        return result.takeIf { it.isNotBlank() }
    }

    private fun cleanMerchant(value: String?): String? {
        var result = cleanSubject(value) ?: return null
        result = result.replace(administrativePrefix, "").trim()
        return result.takeIf { it.isNotBlank() }
    }

    private fun appendSuffix(subject: String, suffix: String): String {
        val clean = subject
            .removeSuffix("取餐码")
            .removeSuffix("取餐")
            .removeSuffix("取件码")
            .removeSuffix("取件")
            .trim()
        val available = (MAX_DISPLAY_UNITS - displayWidth(suffix)).coerceAtLeast(2)
        return compactToDisplayWidth(clean, available) + suffix
    }

    private fun compactToDisplayWidth(value: String, maxUnits: Int = MAX_DISPLAY_UNITS): String {
        val normalized = normalizeSingleLine(value)
        if (displayWidth(normalized) <= maxUnits) return normalized
        val result = StringBuilder()
        var width = 0
        var offset = 0
        while (offset < normalized.length) {
            val codePoint = normalized.codePointAt(offset)
            val codePointWidth = if (codePoint <= 0x7f) 1 else 2
            if (width + codePointWidth > maxUnits) break
            result.appendCodePoint(codePoint)
            width += codePointWidth
            offset += Character.charCount(codePoint)
        }
        return result.toString().trimEnd(' ', '-', '_', '·', '。', '，', ',', '；', ';', ':', '：')
    }

    private fun normalizeSingleLine(value: String?): String {
        return value.orEmpty()
            .replace('\r', ' ')
            .replace('\n', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun shouldNormalizeHistorical(
        record: MemoryRecord,
        facts: MemoryStructuredFacts?,
        currentTitle: String
    ): Boolean {
        if (currentTitle.isBlank()) return true
        if (facts?.titleSource == SOURCE_MANUAL_EDIT) return false
        if (facts?.titleSource == SOURCE_GENERATED &&
            facts.titlePolicyVersion < CURRENT_VERSION
        ) {
            return true
        }
        if (facts?.titleSource == SOURCE_GENERATED &&
            facts.titlePolicyVersion >= CURRENT_VERSION
        ) {
            return false
        }
        val source = normalizeSingleLine(record.sourceText)
        val legacyPrefix = currentTitle.removeSuffix("...")
        val matchesLegacyTruncation = currentTitle.endsWith("...") &&
            source.startsWith(legacyPrefix)
        val sentenceLike = sentencePunctuation.containsMatchIn(currentTitle) ||
            generatedSentencePrefix.containsMatchIn(currentTitle)
        val inconsistentPickupTitle = legacyPickupTitle.matches(currentTitle) &&
            record.categoryCode == CategoryCatalog.CODE_QUICK_NOTE
        val likelyGeneratedEngine = record.mode == MemoryRecord.MODE_AI ||
            record.engine.orEmpty().lowercase(Locale.ROOT) in setOf("cloud", "local", "assistant", "pending")
        return matchesLegacyTruncation ||
            inconsistentPickupTitle ||
            likelyGeneratedEngine && (
                sentenceLike ||
                    displayWidth(currentTitle) > MAX_DISPLAY_UNITS
                )
    }

    private fun String.containsAny(vararg values: String): Boolean {
        return values.any { contains(it, ignoreCase = true) }
    }
}
