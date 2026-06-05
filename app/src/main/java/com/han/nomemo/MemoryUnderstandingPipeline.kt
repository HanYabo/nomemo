package com.han.nomemo

import java.util.Locale

enum class MemoryFactType(val jsonName: String) {
    PICKUP_CODE("pickupCode"),
    LOCATION("location"),
    MERCHANT_OR_COMPANY("merchantOrCompany"),
    ITEM_NAME("itemName"),
    ORDER_NUMBER("orderNumber"),
    TRACKING_NUMBER("trackingNumber"),
    AMOUNT("amount"),
    TIME_WINDOW("timeWindow")
}

enum class MemoryEvidenceSource(val wireName: String, val priority: Int) {
    MANUAL_EDIT("manual_edit", 100),
    USER_INPUT("user_input", 90),
    OCR("ocr", 85),
    LOCAL_RULE("local_rule", 75),
    AI_MODEL("ai_model", 55),
    GENERATED_TEXT("generated_text", 35),
    HISTORICAL("historical", 20)
}

data class MemoryFactEvidence(
    val field: String,
    val value: String,
    val source: String,
    val excerpt: String,
    val start: Int = -1,
    val end: Int = -1,
    val confidence: Double = 0.0
)

data class MemoryEvidenceDocument(
    val source: MemoryEvidenceSource,
    val text: String
)

private data class MemoryFactCandidate(
    val type: MemoryFactType,
    val value: String,
    val source: MemoryEvidenceSource,
    val excerpt: String,
    val start: Int,
    val end: Int,
    val confidence: Double,
    val codeType: String? = null,
    val explicitBoundary: Boolean = false
) {
    val rank: Double
        get() = resolutionPriority() * 1000.0 +
            confidence * 10.0 +
            candidateSpecificity(value)

    private fun resolutionPriority(): Int {
        return if (explicitBoundary) {
            source.priority.coerceAtLeast(80)
        } else {
            source.priority
        }
    }
}

data class MemoryUnderstandingResult(
    val facts: MemoryStructuredFacts,
    val categoryCode: String,
    val evidence: List<MemoryFactEvidence>
)

object MemoryUnderstandingPipeline {
    const val CURRENT_PARSER_VERSION = 2

    @JvmStatic
    fun reconcileToJson(
        userText: String?,
        aiStructuredFactsJson: String?,
        title: String?,
        summary: String?,
        analysis: String?,
        memory: String?,
        categoryCode: String?
    ): String {
        return MemoryStructuredFactsJson.toJson(
            understand(
                userText = userText,
                aiStructuredFactsJson = aiStructuredFactsJson,
                title = title,
                summary = summary,
                analysis = analysis,
                memory = memory,
                categoryCode = categoryCode
            ).facts
        )
    }

    fun understand(
        userText: String?,
        aiStructuredFactsJson: String?,
        title: String?,
        summary: String?,
        analysis: String?,
        memory: String?,
        categoryCode: String?
    ): MemoryUnderstandingResult {
        val aiFacts = MemoryStructuredFactsJson.parse(aiStructuredFactsJson)
        val cleanedUserText = MemoryFactReconciler.cleanLegacyStructuredTemplateText(userText)
        val primaryDocuments = buildList {
            cleanedUserText.trim().takeIf { it.isNotEmpty() }?.let {
                add(MemoryEvidenceDocument(MemoryEvidenceSource.USER_INPUT, it))
            }
            aiFacts?.rawVisibleText?.trim()?.takeIf { it.isNotEmpty() && it != cleanedUserText.trim() }?.let {
                add(MemoryEvidenceDocument(MemoryEvidenceSource.OCR, it))
            }
        }
        val generatedDocuments = if (primaryDocuments.isEmpty()) {
            listOfNotNull(memory, analysis, summary, title)
                .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
                .distinct()
                .map { MemoryEvidenceDocument(MemoryEvidenceSource.GENERATED_TEXT, it) }
        } else {
            emptyList()
        }
        val documents = primaryDocuments + generatedDocuments
        val supportText = documents.joinToString("\n") { it.text }

        val candidates = buildList {
            documents.forEach { document ->
                addAll(MemoryFieldSegmenter.extract(document))
            }
            addAll(MemoryLexicalCandidateExtractor.extract(documents))
            addAll(candidatesFromExistingFacts(aiFacts, documents))
            addAll(
                candidatesFromLocalRules(
                    MemoryFactExtractor.extractLocalFacts(
                        userText = cleanedUserText,
                        aiRawVisibleText = aiFacts?.rawVisibleText,
                        memory = if (primaryDocuments.isEmpty()) memory else null,
                        analysis = if (primaryDocuments.isEmpty()) analysis else null,
                        summary = if (primaryDocuments.isEmpty()) summary else null,
                        title = if (primaryDocuments.isEmpty()) title else null,
                        categoryCode = categoryCode
                    ),
                    documents
                )
            )
        }
            .filter { MemoryFactCandidateValidator.isValid(it, supportText) }
            .distinctBy { Triple(it.type, comparisonKey(it.value), it.source) }

        val winners = MemoryFactCandidateResolver.resolve(candidates)
        val candidateFacts = buildCandidateFacts(aiFacts, winners)
        val legacyFacts = MemoryFactReconciler.reconcileLegacy(
            userText = cleanedUserText,
            aiStructuredFactsJson = MemoryStructuredFactsJson.toJson(candidateFacts),
            title = title,
            summary = summary,
            analysis = analysis,
            memory = memory,
            categoryCode = categoryCode
        )
        val resolvedFacts = applyWinners(legacyFacts, winners).copy(
            parserVersion = CURRENT_PARSER_VERSION,
            evidence = winners.values
                .sortedBy { it.type.ordinal }
                .map { it.toEvidence() },
            rawVisibleText = firstNonBlank(aiFacts?.rawVisibleText, legacyFacts.rawVisibleText)
        )
        val resolvedCategory = MemoryFactReconciler.normalizeCategoryCode(
            categoryCode,
            MemoryStructuredFactsJson.toJson(resolvedFacts),
            supportText
        )
        return MemoryUnderstandingResult(
            facts = resolvedFacts,
            categoryCode = resolvedCategory,
            evidence = resolvedFacts.evidence
        )
    }

    @JvmStatic
    fun mergeManualEdits(
        structuredFactsJson: String?,
        categoryCode: String?,
        code: String?,
        primaryValue: String?,
        secondaryValue: String?,
        locationText: String?
    ): String {
        val existing = MemoryStructuredFactsJson.parse(structuredFactsJson) ?: MemoryStructuredFacts()
        val domain = when (categoryCode) {
            CategoryCatalog.CODE_LIFE_DELIVERY -> "delivery"
            CategoryCatalog.CODE_LIFE_PICKUP -> "pickup"
            else -> existing.domain
        }
        val normalizedCode = MemoryFieldValueNormalizer.normalizeCode(code)
        val normalizedLocation = MemoryFieldValueNormalizer.normalizeLocation(
            if (domain == "delivery") locationText ?: secondaryValue else locationText
        )
        val normalizedPrimary = MemoryFieldValueNormalizer.normalizePlain(primaryValue)
        val normalizedSecondary = MemoryFieldValueNormalizer.normalizePlain(secondaryValue)
        val manualEvidence = buildList {
            normalizedCode?.let {
                add(manualEvidence(MemoryFactType.PICKUP_CODE, it))
            }
            normalizedLocation?.let {
                add(manualEvidence(MemoryFactType.LOCATION, it))
            }
            normalizedPrimary?.let {
                add(manualEvidence(MemoryFactType.MERCHANT_OR_COMPANY, it))
            }
            if (domain == "pickup") {
                normalizedSecondary?.let {
                    add(manualEvidence(MemoryFactType.ITEM_NAME, it))
                }
            }
        }
        val retainedEvidence = existing.evidence.filterNot { evidence ->
            manualEvidence.any { it.field == evidence.field }
        }
        val updated = if (domain == "delivery") {
            existing.copy(
                domain = domain,
                pickupCode = normalizedCode,
                pickupCodeType = normalizedCode?.let { existing.pickupCodeType ?: "package" },
                pickupCodeConfidence = if (normalizedCode == null) 0.0 else 1.0,
                pickupCodeEvidence = normalizedCode?.let { MemoryEvidenceSource.MANUAL_EDIT.wireName },
                merchantOrCompany = normalizedPrimary,
                location = normalizedLocation,
                locationConfidence = if (normalizedLocation == null) 0.0 else 1.0,
                locationEvidence = normalizedLocation?.let { MemoryEvidenceSource.MANUAL_EDIT.wireName },
                parserVersion = CURRENT_PARSER_VERSION,
                evidence = retainedEvidence + manualEvidence
            )
        } else {
            existing.copy(
                domain = domain,
                pickupCode = normalizedCode,
                pickupCodeType = normalizedCode?.let { existing.pickupCodeType ?: "meal" },
                pickupCodeConfidence = if (normalizedCode == null) 0.0 else 1.0,
                pickupCodeEvidence = normalizedCode?.let { MemoryEvidenceSource.MANUAL_EDIT.wireName },
                merchantOrCompany = normalizedPrimary,
                itemName = normalizedSecondary,
                location = MemoryFieldValueNormalizer.normalizeLocation(locationText),
                locationConfidence = if (locationText.isNullOrBlank()) 0.0 else 1.0,
                locationEvidence = locationText?.takeIf { it.isNotBlank() }?.let {
                    MemoryEvidenceSource.MANUAL_EDIT.wireName
                },
                parserVersion = CURRENT_PARSER_VERSION,
                evidence = retainedEvidence + manualEvidence
            )
        }
        return MemoryStructuredFactsJson.toJson(updated)
    }

    fun presentationFor(record: MemoryRecord): StructuredPickupInfo? {
        val stored = MemoryStructuredFactsJson.parse(record.structuredFactsJson)
        val facts = if (
            stored != null &&
            stored.parserVersion >= CURRENT_PARSER_VERSION &&
            !stored.pickupCode.isNullOrBlank()
        ) {
            stored
        } else {
            understand(
                userText = listOfNotNull(record.note, record.sourceText)
                    .filter { it.isNotBlank() }
                    .distinct()
                    .joinToString("\n"),
                aiStructuredFactsJson = record.structuredFactsJson,
                title = record.title,
                summary = record.summary,
                analysis = record.analysis,
                memory = record.memory,
                categoryCode = record.categoryCode
            ).facts
        }
        return MemoryStructuredPresentationMapper.pickupInfo(record.categoryCode, facts)
    }

    private fun buildCandidateFacts(
        existing: MemoryStructuredFacts?,
        winners: Map<MemoryFactType, MemoryFactCandidate>
    ): MemoryStructuredFacts {
        val code = winners[MemoryFactType.PICKUP_CODE]
        val location = winners[MemoryFactType.LOCATION]
        return (existing ?: MemoryStructuredFacts()).copy(
            pickupCode = code?.value ?: existing?.pickupCode,
            pickupCodeType = code?.codeType ?: existing?.pickupCodeType,
            pickupCodeConfidence = code?.confidence ?: existing?.pickupCodeConfidence ?: 0.0,
            pickupCodeEvidence = code?.excerpt ?: existing?.pickupCodeEvidence,
            location = location?.value ?: existing?.location,
            locationConfidence = location?.confidence ?: existing?.locationConfidence ?: 0.0,
            locationEvidence = location?.excerpt ?: existing?.locationEvidence,
            merchantOrCompany = winners[MemoryFactType.MERCHANT_OR_COMPANY]?.value
                ?: existing?.merchantOrCompany,
            itemName = winners[MemoryFactType.ITEM_NAME]?.value ?: existing?.itemName,
            orderNumber = winners[MemoryFactType.ORDER_NUMBER]?.value ?: existing?.orderNumber,
            trackingNumber = winners[MemoryFactType.TRACKING_NUMBER]?.value ?: existing?.trackingNumber,
            amount = winners[MemoryFactType.AMOUNT]?.value ?: existing?.amount,
            timeWindow = winners[MemoryFactType.TIME_WINDOW]?.value ?: existing?.timeWindow
        )
    }

    private fun applyWinners(
        legacy: MemoryStructuredFacts,
        winners: Map<MemoryFactType, MemoryFactCandidate>
    ): MemoryStructuredFacts {
        val code = winners[MemoryFactType.PICKUP_CODE]
        val location = winners[MemoryFactType.LOCATION]
        return legacy.copy(
            pickupCode = code?.value,
            pickupCodeType = code?.codeType,
            pickupCodeConfidence = code?.confidence ?: 0.0,
            pickupCodeEvidence = code?.excerpt,
            location = location?.value,
            locationConfidence = location?.confidence ?: 0.0,
            locationEvidence = location?.excerpt,
            merchantOrCompany = winners[MemoryFactType.MERCHANT_OR_COMPANY]?.value
                ?: legacy.merchantOrCompany,
            itemName = winners[MemoryFactType.ITEM_NAME]?.value ?: legacy.itemName,
            orderNumber = winners[MemoryFactType.ORDER_NUMBER]?.value ?: legacy.orderNumber,
            trackingNumber = winners[MemoryFactType.TRACKING_NUMBER]?.value ?: legacy.trackingNumber,
            amount = winners[MemoryFactType.AMOUNT]?.value ?: legacy.amount,
            timeWindow = winners[MemoryFactType.TIME_WINDOW]?.value ?: legacy.timeWindow
        )
    }

    private fun candidatesFromExistingFacts(
        facts: MemoryStructuredFacts?,
        documents: List<MemoryEvidenceDocument>
    ): List<MemoryFactCandidate> {
        facts ?: return emptyList()
        val manualCode = facts.pickupCodeEvidence == MemoryEvidenceSource.MANUAL_EDIT.wireName
        val manualLocation = facts.locationEvidence == MemoryEvidenceSource.MANUAL_EDIT.wireName
        return listOfNotNull(
            candidateFromExisting(
                MemoryFactType.PICKUP_CODE,
                facts.pickupCode,
                if (manualCode) MemoryEvidenceSource.MANUAL_EDIT else MemoryEvidenceSource.AI_MODEL,
                facts.pickupCodeConfidence,
                facts.pickupCodeEvidence,
                documents,
                facts.pickupCodeType
            ),
            candidateFromExisting(
                MemoryFactType.LOCATION,
                facts.location,
                if (manualLocation) MemoryEvidenceSource.MANUAL_EDIT else MemoryEvidenceSource.AI_MODEL,
                facts.locationConfidence,
                facts.locationEvidence,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.MERCHANT_OR_COMPANY,
                facts.merchantOrCompany,
                MemoryEvidenceSource.AI_MODEL,
                0.68,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.ITEM_NAME,
                facts.itemName,
                MemoryEvidenceSource.AI_MODEL,
                0.68,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.ORDER_NUMBER,
                facts.orderNumber,
                MemoryEvidenceSource.AI_MODEL,
                0.72,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.TRACKING_NUMBER,
                facts.trackingNumber,
                MemoryEvidenceSource.AI_MODEL,
                0.72,
                null,
                documents
            )
        )
    }

    private fun candidatesFromLocalRules(
        facts: MemoryStructuredFacts,
        documents: List<MemoryEvidenceDocument>
    ): List<MemoryFactCandidate> {
        return listOfNotNull(
            candidateFromExisting(
                MemoryFactType.PICKUP_CODE,
                facts.pickupCode,
                MemoryEvidenceSource.LOCAL_RULE,
                facts.pickupCodeConfidence,
                facts.pickupCodeEvidence,
                documents,
                facts.pickupCodeType
            ),
            candidateFromExisting(
                MemoryFactType.LOCATION,
                facts.location,
                MemoryEvidenceSource.LOCAL_RULE,
                facts.locationConfidence,
                facts.locationEvidence,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.MERCHANT_OR_COMPANY,
                facts.merchantOrCompany,
                MemoryEvidenceSource.LOCAL_RULE,
                0.7,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.ITEM_NAME,
                facts.itemName,
                MemoryEvidenceSource.LOCAL_RULE,
                0.7,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.ORDER_NUMBER,
                facts.orderNumber,
                MemoryEvidenceSource.LOCAL_RULE,
                0.78,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.TRACKING_NUMBER,
                facts.trackingNumber,
                MemoryEvidenceSource.LOCAL_RULE,
                0.78,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.AMOUNT,
                facts.amount,
                MemoryEvidenceSource.LOCAL_RULE,
                0.72,
                null,
                documents
            ),
            candidateFromExisting(
                MemoryFactType.TIME_WINDOW,
                facts.timeWindow,
                MemoryEvidenceSource.LOCAL_RULE,
                0.72,
                null,
                documents
            )
        )
    }

    private fun candidateFromExisting(
        type: MemoryFactType,
        rawValue: String?,
        source: MemoryEvidenceSource,
        confidence: Double,
        evidence: String?,
        documents: List<MemoryEvidenceDocument>,
        codeType: String? = null
    ): MemoryFactCandidate? {
        val value = MemoryFieldValueNormalizer.normalize(type, rawValue) ?: return null
        if (source == MemoryEvidenceSource.MANUAL_EDIT) {
            return MemoryFactCandidate(
                type,
                value,
                source,
                evidence ?: value,
                -1,
                -1,
                confidence.coerceAtLeast(1.0),
                codeType
            )
        }
        val located = locateEvidence(type, value, evidence, documents) ?: return null
        return MemoryFactCandidate(
            type,
            value,
            source,
            located.excerpt,
            located.start,
            located.end,
            confidence.coerceIn(0.0, 1.0),
            codeType
        )
    }

    private fun locateEvidence(
        type: MemoryFactType,
        value: String,
        evidence: String?,
        documents: List<MemoryEvidenceDocument>
    ): MemoryFactEvidence? {
        if (
            type == MemoryFactType.PICKUP_CODE ||
            type == MemoryFactType.ORDER_NUMBER ||
            type == MemoryFactType.TRACKING_NUMBER
        ) {
            val codePattern = buildCodeEvidencePattern(value)
            documents.forEach { document ->
                val match = codePattern.find(document.text) ?: return@forEach
                return MemoryFactEvidence(
                    field = "",
                    value = value,
                    source = document.source.wireName,
                    excerpt = lineExcerpt(document.text, match.range),
                    start = match.range.first,
                    end = match.range.last + 1,
                    confidence = 0.0
                )
            }
            return null
        }
        val needles = listOfNotNull(value.trim(), evidence?.trim())
            .filter { it.isNotEmpty() }
            .distinct()
        documents.forEach { document ->
            needles.forEach { needle ->
                val direct = document.text.indexOf(needle, ignoreCase = true)
                if (direct >= 0) {
                    return MemoryFactEvidence(
                        field = "",
                        value = value,
                        source = document.source.wireName,
                        excerpt = needle,
                        start = direct,
                        end = direct + needle.length,
                        confidence = 0.0
                    )
                }
            }
            val compactValue = comparisonKey(value)
            if (compactValue.isNotBlank() && comparisonKey(document.text).contains(compactValue)) {
                return MemoryFactEvidence(
                    field = "",
                    value = value,
                    source = document.source.wireName,
                    excerpt = value,
                    confidence = 0.0
                )
            }
        }
        return null
    }

    private fun buildCodeEvidencePattern(value: String): Regex {
        val pattern = buildString {
            append("(?<![A-Za-z0-9])")
            value.forEachIndexed { index, char ->
                append(Regex.escape(char.toString()))
                if (index < value.lastIndex) append("[ \\t]*")
            }
            append("(?![A-Za-z0-9])")
        }
        return Regex(pattern, RegexOption.IGNORE_CASE)
    }

    private fun lineExcerpt(text: String, range: IntRange): String {
        val start = text.lastIndexOf('\n', (range.first - 1).coerceAtLeast(0))
            .let { if (it < 0) 0 else it + 1 }
        val end = text.indexOf('\n', range.last + 1)
            .let { if (it < 0) text.length else it }
        return text.substring(start, end).trim()
    }

    private fun manualEvidence(type: MemoryFactType, value: String): MemoryFactEvidence {
        return MemoryFactEvidence(
            field = type.jsonName,
            value = value,
            source = MemoryEvidenceSource.MANUAL_EDIT.wireName,
            excerpt = value,
            confidence = 1.0
        )
    }
}

private object MemoryFieldSegmenter {
    private val labelTypes = linkedMapOf(
        "取件地址" to MemoryFactType.LOCATION,
        "取餐地址" to MemoryFactType.LOCATION,
        "取货地址" to MemoryFactType.LOCATION,
        "收货地址" to MemoryFactType.LOCATION,
        "门店地址" to MemoryFactType.LOCATION,
        "快递公司" to MemoryFactType.MERCHANT_OR_COMPANY,
        "物流公司" to MemoryFactType.MERCHANT_OR_COMPANY,
        "承运公司" to MemoryFactType.MERCHANT_OR_COMPANY,
        "配送公司" to MemoryFactType.MERCHANT_OR_COMPANY,
        "取件码" to MemoryFactType.PICKUP_CODE,
        "取件号" to MemoryFactType.PICKUP_CODE,
        "取件琅" to MemoryFactType.PICKUP_CODE,
        "取件玛" to MemoryFactType.PICKUP_CODE,
        "取件吗" to MemoryFactType.PICKUP_CODE,
        "取餐码" to MemoryFactType.PICKUP_CODE,
        "取餐号" to MemoryFactType.PICKUP_CODE,
        "取餐玛" to MemoryFactType.PICKUP_CODE,
        "取餐吗" to MemoryFactType.PICKUP_CODE,
        "提货码" to MemoryFactType.PICKUP_CODE,
        "提货号" to MemoryFactType.PICKUP_CODE,
        "提货吗" to MemoryFactType.PICKUP_CODE,
        "取货码" to MemoryFactType.PICKUP_CODE,
        "取货号" to MemoryFactType.PICKUP_CODE,
        "取货吗" to MemoryFactType.PICKUP_CODE,
        "自提码" to MemoryFactType.PICKUP_CODE,
        "自提号" to MemoryFactType.PICKUP_CODE,
        "核销码" to MemoryFactType.PICKUP_CODE,
        "柜号" to MemoryFactType.PICKUP_CODE,
        "货架号" to MemoryFactType.PICKUP_CODE,
        "架位号" to MemoryFactType.PICKUP_CODE,
        "订单号" to MemoryFactType.ORDER_NUMBER,
        "订单编号" to MemoryFactType.ORDER_NUMBER,
        "运单号" to MemoryFactType.TRACKING_NUMBER,
        "快递单号" to MemoryFactType.TRACKING_NUMBER,
        "物流单号" to MemoryFactType.TRACKING_NUMBER,
        "商品名" to MemoryFactType.ITEM_NAME,
        "商品" to MemoryFactType.ITEM_NAME,
        "餐品" to MemoryFactType.ITEM_NAME,
        "菜品" to MemoryFactType.ITEM_NAME,
        "套餐" to MemoryFactType.ITEM_NAME,
        "门店" to MemoryFactType.MERCHANT_OR_COMPANY,
        "店铺" to MemoryFactType.MERCHANT_OR_COMPANY,
        "商家" to MemoryFactType.MERCHANT_OR_COMPANY,
        "地址" to MemoryFactType.LOCATION,
        "地点" to MemoryFactType.LOCATION,
        "金额" to MemoryFactType.AMOUNT,
        "实付" to MemoryFactType.AMOUNT,
        "应付" to MemoryFactType.AMOUNT,
        "时间" to MemoryFactType.TIME_WINDOW
    )
    private val labelRegex = Regex(
        """(?i)(${labelTypes.keys.joinToString("|") { Regex.escape(it) }})\s*[:：]\s*"""
    )

    fun extract(document: MemoryEvidenceDocument): List<MemoryFactCandidate> {
        val matches = labelRegex.findAll(document.text).toList()
        if (matches.isEmpty()) return emptyList()
        return buildList {
            matches.forEachIndexed { index, match ->
                val label = match.groupValues[1]
                val type = labelTypes[label] ?: return@forEachIndexed
                val valueStart = match.range.last + 1
                val nextLabelStart = matches.getOrNull(index + 1)?.range?.first ?: document.text.length
                val hardEnd = findHardBoundary(document.text, valueStart, nextLabelStart, type)
                val raw = document.text.substring(valueStart, hardEnd).trim()
                val value = MemoryFieldValueNormalizer.normalize(type, raw) ?: return@forEachIndexed
                add(
                    MemoryFactCandidate(
                        type = type,
                        value = value,
                        source = document.source,
                        excerpt = document.text.substring(match.range.first, hardEnd).trim(),
                        start = valueStart,
                        end = hardEnd,
                        confidence = confidenceFor(type, label),
                        codeType = codeType(label),
                        explicitBoundary = true
                    )
                )
            }
        }
    }

    private fun findHardBoundary(
        text: String,
        valueStart: Int,
        nextLabelStart: Int,
        type: MemoryFactType
    ): Int {
        var end = nextLabelStart
        listOf('\n', '\r', ';', '；', '|').forEach { delimiter ->
            val index = text.indexOf(delimiter, valueStart)
            if (index in valueStart until end) end = index
        }
        if (type != MemoryFactType.LOCATION) {
            listOf(',', '，').forEach { delimiter ->
                val index = text.indexOf(delimiter, valueStart)
                if (index in valueStart until end) end = index
            }
        }
        return end
    }

    private fun confidenceFor(type: MemoryFactType, label: String): Double {
        return when (type) {
            MemoryFactType.PICKUP_CODE -> if (label.contains("取件") || label.contains("取餐")) 0.98 else 0.9
            MemoryFactType.LOCATION -> 0.95
            MemoryFactType.ORDER_NUMBER, MemoryFactType.TRACKING_NUMBER -> 0.96
            else -> 0.9
        }
    }

    private fun codeType(label: String): String? {
        return when {
            label.contains("取餐") || label.contains("核销") -> "meal"
            label.contains("柜") || label.contains("货架") || label.contains("架位") -> "shelf"
            label.contains("取件") || label.contains("提货") || label.contains("取货") -> "package"
            else -> null
        }
    }
}

object MemoryFieldValueNormalizer {
    private val nextFieldLabelPattern = Regex(
        """(?i)(?:取件地址|取餐地址|取货地址|收货地址|门店地址|地址|地点|快递公司|物流公司|承运公司|配送公司|取件码|取餐码|提货码|取货码|自提码|核销码|订单号|订单编号|运单号|快递单号|物流单号|商品名|商品|餐品|菜品|套餐|门店|店铺|商家|金额|实付|应付|时间)\s*[:：]\s*"""
    )
    private val locationSemantics = Regex(
        """(?:校区|园区|广场|中心|小区|公寓|大学|学校|学院|医院|驿站|快递柜|自提点|取件点|取餐点|食堂|窗口|前台|办公室|会议室|教室|宿舍|大厅|车站|机场|商场|菜鸟|丰巢|北门|南门|东门|西门|正门|门店|教学楼|办公楼|宿舍楼|实验楼|综合楼|[\p{L}\d]{2,}(?:省|县|镇|乡|村|路|街|大道|巷|弄)|[\p{L}\d]{2,}市(?!场)|[\p{L}\d]{2,}区(?!块|别|域|分)|\d+\s*(?:号楼|号|楼|栋|单元|室))""",
        RegexOption.IGNORE_CASE
    )
    private val nonLocationSuffix = Regex(
        """(?i)[\s,，]*(?:[A-Za-z]{2,10}\s+)?(?:(?:顺丰|中通|圆通|申通|韵达|极兔|京东|邮政|EMS|德邦|菜鸟)\s*)?(?:快递|速递|速运|物流)?\s*(?:包裹|快件|已到|已送达|待取件|请取件).*$"""
    )
    private val instructionSuffix = Regex(
        """[\s,，]*(?:请于|请在|请前往|前往|出示|凭取件码|凭码|请及时|请尽快|及时到店).*$"""
    )
    private val isolatedLatinNoise = Regex("""(?<=[\u4E00-\u9FFF）)])\s+[A-Za-z]{2,10}(?=\s|$)""")

    fun normalize(type: MemoryFactType, value: String?): String? {
        return when (type) {
            MemoryFactType.PICKUP_CODE,
            MemoryFactType.ORDER_NUMBER,
            MemoryFactType.TRACKING_NUMBER -> normalizeCode(value)
            MemoryFactType.LOCATION -> normalizeLocation(value)
            MemoryFactType.ITEM_NAME -> normalizeItem(value)
            else -> normalizePlain(value)
        }
    }

    fun normalizePlain(value: String?): String? {
        val cleaned = normalizeText(value)
            .substringBefore('\n')
            .trim(' ', ',', '，', '.', '。', ';', '；', ':', '：', '|', '(', '（')
        if (cleaned.isBlank() || cleaned.equals("未识别", true) || cleaned.equals("无", true)) return null
        return cleaned
    }

    fun normalizeCode(value: String?): String? {
        val cleaned = normalizeText(value)
            .uppercase(Locale.ROOT)
            .trim(' ', ',', '，', '.', '。', ';', '；', ':', '：', '|')
        if (!cleaned.matches(Regex("""[A-Z0-9\-_]{2,32}"""))) return null
        return cleaned
    }

    private fun normalizeItem(value: String?): String? {
        return normalizePlain(value)
            ?.removePrefix("为")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    fun normalizeLocation(value: String?): String? {
        var normalized = normalizeText(value)
            .replace('\n', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
        if (normalized.isBlank()) return null
        normalized = nextFieldLabelPattern.split(normalized)
            .asSequence()
            .map(::cleanLocationSegment)
            .filter { it.length in 2..80 && locationSemantics.containsMatchIn(it) }
            .distinct()
            .sortedWith(
                compareByDescending<String> { locationScore(it) }
                    .thenBy { it.length }
            )
            .firstOrNull()
            ?: cleanLocationSegment(normalized)
        return normalized.takeIf {
            it.length in 2..80 &&
                locationSemantics.containsMatchIn(it) &&
                !it.matches(Regex("""[A-Za-z0-9\-_]{2,80}"""))
        }
    }

    private fun cleanLocationSegment(value: String): String {
        var result = value
            .trim(' ', ',', '，', '.', '。', ';', '；', ':', '：', '|')
            .removePrefix("位于")
            .trim()
        result = isolatedLatinNoise.replace(result, " ")
        result = nonLocationSuffix.replace(result, "")
        result = instructionSuffix.replace(result, "")
        result = result
            .replace(Regex("""\s+"""), " ")
            .trimEnd(' ', ',', '，', '.', '。', ';', '；', ':', '：', '|', '(', '（', '[', '【')
            .trim()
        return result
    }

    private fun locationScore(value: String): Int {
        var score = value.length.coerceAtMost(40)
        if (value.containsAny("驿站", "快递柜", "自提点", "取件点", "取餐点", "菜鸟", "丰巢")) score += 20
        if (value.containsAny("校区", "园区", "小区", "公寓", "大学", "学校", "学院", "医院")) score += 12
        if (value.containsAny("省", "市", "区", "县", "镇", "乡", "村", "路", "街", "号", "楼", "栋", "室")) score += 8
        if (Regex("""[A-Za-z]{2,}""").containsMatchIn(value)) score -= 10
        return score
    }

    private fun normalizeText(value: String?): String {
        if (value.isNullOrBlank()) return ""
        return buildString(value.length) {
            value.forEach { char ->
                append(
                    when {
                        char.code == 0x3000 -> ' '
                        char == '（' || char == '）' -> char
                        char.code in 0xFF01..0xFF5E -> (char.code - 0xFEE0).toChar()
                        else -> char
                    }
                )
            }
        }.replace(Regex("""[ \t]+"""), " ").trim()
    }
}

private object MemoryFactCandidateValidator {
    private val dateTimePatterns = listOf(
        Regex("""(?<!\d)(?:20\d{2}[年./-])?\d{1,2}[月./-]\d{1,2}日?\s+\d{1,2}[:：]\d{2}(?!\d)"""),
        Regex("""(?<!\d)\d{1,2}[:：]\d{2}(?!\d)""")
    )
    private val phonePattern = Regex("""(?<!\d)1\d{10}(?!\d)""")
    private val amountPattern = Regex("""(?:[¥￥$]\s*\d+(?:\.\d{1,2})?|\d+(?:\.\d{1,2})?\s*元)""")

    fun isValid(candidate: MemoryFactCandidate, supportText: String): Boolean {
        if (candidate.source == MemoryEvidenceSource.MANUAL_EDIT) return true
        return when (candidate.type) {
            MemoryFactType.PICKUP_CODE -> validCode(candidate, supportText)
            MemoryFactType.LOCATION -> validLocation(candidate, supportText)
            MemoryFactType.MERCHANT_OR_COMPANY -> {
                candidate.value.length in 2..40 &&
                    !candidate.value.startsWith("地址") &&
                    !candidate.value.startsWith("地点") &&
                    !candidate.value.startsWith("位于")
            }
            MemoryFactType.ITEM_NAME -> candidate.value.length in 2..80
            MemoryFactType.ORDER_NUMBER,
            MemoryFactType.TRACKING_NUMBER -> candidate.value.length in 6..32
            MemoryFactType.AMOUNT,
            MemoryFactType.TIME_WINDOW -> candidate.value.length in 1..40
        }
    }

    private fun validCode(candidate: MemoryFactCandidate, supportText: String): Boolean {
        val code = candidate.value
        if (code.length !in 2..14) return false
        if (phonePattern.containsMatchIn(code)) return false
        if (dateTimePatterns.any { it.containsMatchIn(candidate.excerpt) }) {
            val explicit = candidate.excerpt.containsAny(
                "取件码", "取件号", "取餐码", "取餐号", "提货码", "提货号",
                "取货码", "取货号", "自提码", "自提号", "核销码", "货架号", "架位号"
            )
            if (!explicit) return false
        }
        val explicit = candidate.excerpt.containsAny(
            "取件码", "取件号", "取餐码", "取餐号", "提货码", "提货号",
            "取货码", "取货号", "自提码", "自提号", "核销码", "货架号", "架位号"
        )
        if (!explicit && amountPattern.containsMatchIn(candidate.excerpt)) return false
        if (!explicit && phonePattern.containsMatchIn(candidate.excerpt)) return false
        if (!explicit && candidate.excerpt.containsAny("订单号", "订单编号", "运单号", "快递单号", "物流单号")) {
            return false
        }
        if (!supportText.containsAny(
                "取件", "取餐", "快递", "包裹", "驿站", "外卖", "门店", "自提", "核销",
                "订单已完成", "感谢光顾", "商品总价", "喜茶", "瑞幸", "星巴克", "库迪",
                "肯德基", "麦当劳", "奶茶", "咖啡", "饮品", "餐品"
            )
        ) {
            return false
        }
        return true
    }

    private fun validLocation(candidate: MemoryFactCandidate, supportText: String): Boolean {
        val value = candidate.value
        if (value.length !in 2..80) return false
        if (MemoryFieldValueNormalizer.normalizeLocation(value) == null) return false
        return comparisonKey(supportText).contains(comparisonKey(value))
    }
}

private object MemoryLexicalCandidateExtractor {
    private val knownNames = listOf(
        "顺丰速运", "中通快递", "圆通快递", "申通快递", "韵达快递", "极兔速递",
        "京东快递", "中国邮政", "菜鸟速递", "丰巢",
        "瑞幸咖啡", "奈雪的茶", "蜜雪冰城", "库迪咖啡", "星巴克", "肯德基",
        "麦当劳", "幸运咖", "喜茶"
    ).sortedByDescending(String::length)

    fun extract(documents: List<MemoryEvidenceDocument>): List<MemoryFactCandidate> {
        return buildList {
            documents.forEach { document ->
                knownNames.forEach { name ->
                    val index = document.text.indexOf(name, ignoreCase = true)
                    if (index < 0) return@forEach
                    val value = extendBranchName(document.text, index, name)
                    add(
                        MemoryFactCandidate(
                            type = MemoryFactType.MERCHANT_OR_COMPANY,
                            value = value,
                            source = MemoryEvidenceSource.LOCAL_RULE,
                            excerpt = value,
                            start = index,
                            end = index + value.length,
                            confidence = 0.88
                        )
                    )
                }
            }
        }
    }

    private fun extendBranchName(text: String, start: Int, name: String): String {
        if (name.endsWith("咖啡") || name.endsWith("茶")) return name
        val suffix = text.substring((start + name.length).coerceAtMost(text.length))
        val branch = Regex("""^[（(][^（）()\n]{2,24}(?:店|校区店|门店)[）)]""")
            .find(suffix)
            ?.value
        return if (branch == null) name else name + branch
    }
}

private object MemoryFactCandidateResolver {
    fun resolve(candidates: List<MemoryFactCandidate>): Map<MemoryFactType, MemoryFactCandidate> {
        return candidates
            .groupBy { it.type }
            .mapValues { (_, values) ->
                values.maxWithOrNull(
                    compareBy<MemoryFactCandidate> { it.rank }
                        .thenByDescending { it.value.length }
                )!!
            }
    }
}

object MemoryStructuredPresentationMapper {
    fun pickupInfo(categoryCode: String?, facts: MemoryStructuredFacts?): StructuredPickupInfo? {
        facts ?: return null
        val code = facts.pickupCode?.trim()?.takeIf {
            it.isNotEmpty() && facts.pickupCodeConfidence >= 0.55
        } ?: return null
        val displayDomain = when {
            facts.domain == "pickup" || facts.domain == "delivery" -> facts.domain
            categoryCode == CategoryCatalog.CODE_LIFE_PICKUP -> "pickup"
            categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY -> "delivery"
            else -> "note"
        }
        val location = MemoryFieldValueNormalizer.normalizeLocation(facts.location)
        return when (displayDomain) {
            "delivery" -> StructuredPickupInfo(
                sectionTitle = "取件码",
                code = code,
                primaryLabel = "快递公司",
                primaryValue = facts.merchantOrCompany?.takeIf { it.isNotBlank() } ?: "未识别",
                secondaryLabel = "取件地址",
                secondaryValue = location ?: "未识别",
                locationText = location
            )
            "pickup" -> StructuredPickupInfo(
                sectionTitle = "取餐码",
                code = code,
                primaryLabel = "店铺",
                primaryValue = facts.merchantOrCompany?.takeIf { it.isNotBlank() } ?: location ?: "未识别",
                secondaryLabel = "商品",
                secondaryValue = facts.itemName?.takeIf { it.isNotBlank() } ?: "未识别",
                locationText = location
            )
            else -> null
        }
    }
}

private fun MemoryFactCandidate.toEvidence(): MemoryFactEvidence {
    return MemoryFactEvidence(
        field = type.jsonName,
        value = value,
        source = source.wireName,
        excerpt = excerpt,
        start = start,
        end = end,
        confidence = confidence.coerceIn(0.0, 1.0)
    )
}

private fun candidateSpecificity(value: String): Int {
    return value.length.coerceAtMost(40)
}

private fun comparisonKey(value: String?): String {
    return value.orEmpty()
        .lowercase(Locale.ROOT)
        .replace(Regex("""[\s,，.。;；:：|()（）\[\]【】{}"'`·_-]+"""), "")
}

private fun String.containsAny(vararg keywords: String): Boolean {
    return keywords.any { contains(it, ignoreCase = true) }
}

private fun firstNonBlank(vararg values: String?): String? {
    return values.firstNotNullOfOrNull { value ->
        value?.trim()?.takeIf { it.isNotEmpty() }
    }
}
