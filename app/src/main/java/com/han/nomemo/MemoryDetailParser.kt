package com.han.nomemo

import java.util.Locale

object MemoryDetailParser {
    private enum class StructuredFieldKind {
        COMPANY,
        ITEM,
        LOCATION
    }

    private val stationNamePattern = Regex(
        """([\u4E00-\u9FA5A-Za-z0-9]{2,50}(?:驿站|自提点|快递柜|快递超市|门店|食堂|窗口|前台|代收点|服务点|取件点|取餐点|菜鸟|丰巢))"""
    )
    private val genericStationTexts = setOf(
        "联系驿站", "查看驿站", "驿站", "快递柜", "自提点",
        "菜鸟", "丰巢", "代收点", "服务点"
    )
    private val locationStopTokens = listOf(
        "状态",
        "取件码", "取件琅", "取件玛", "取件吗",
        "取餐码", "取餐玛", "取餐吗",
        "提货码", "提货吗",
        "取货码",
        "验证码",
        "核销码",
        "领取码",
        "尾号",
        "格口号", "柜口号", "柜号",
        "货架号", "架位号", "货位号",
        "快递单号", "物流单号", "运单号", "单号",
        "凭码", "号码"
    )
    private val codeKeywordVariants = listOf(
        "取件码", "取件琅", "取件玛", "取件吗",
        "取餐码", "取餐玛", "取餐吗",
        "提货码", "提货吗",
        "取货码", "取货吗",
        "自提码",
        "验证码",
        "核销码",
        "领取码",
        "收货码"
    )
    private val structuredFieldLabels = listOf(
        "商品", "商品名", "餐品", "菜品", "套餐", "饮品", "饮料", "物品",
        "商家", "门店", "店铺",
        "快递公司", "配送公司", "承运公司", "物流公司",
        "取餐地址", "门店地址", "取餐点",
        "取件地址", "取货地址", "收货地址",
        "地址", "地点", "驿站",
        "取餐码", "取件码", "提货码", "取货码", "收货码",
        "状态", "订单号", "单号", "运单号", "快递单号", "物流单号",
        "应付", "实付", "合计", "总计", "金额", "配送费", "优惠"
    )
    private val structuredTailStopKeywords = listOf(
        "请于", "请在", "前往", "出示",
        "门店已接单", "门店已制作", "门店已备餐",
        "请及时", "请尽快", "及时到店", "当日", "当天", "今日", "今天",
        "状态", "待取件", "待取餐", "待领取", "待自取",
        "制作中", "已接单", "已出餐", "配送中", "已送达", "已到店", "已完成",
        "订单号", "单号", "运单号", "快递单号", "物流单号",
        "应付", "实付", "合计", "总计", "金额", "配送费", "优惠"
    )
    private val companyInvalidKeywords = listOf(
        "地址", "地点", "取餐地址", "门店地址", "取件地址",
        "订单", "单号", "状态",
        "应付", "实付", "合计", "总计", "金额",
        "请于", "请在", "商品", "餐品", "菜品", "位于"
    )
    private val pickupItemInvalidKeywords = listOf(
        "地址", "地点", "取餐地址", "门店地址", "取件地址",
        "门店", "店铺", "商家",
        "快递", "驿站", "物流",
        "订单", "单号", "状态",
        "应付", "实付", "合计", "总计", "金额",
        "请于", "请在", "前往", "出示", "位于"
    )

    fun parseStructuredPickupInfo(record: MemoryRecord): StructuredPickupInfo? {
        MemoryFactReconciler.structuredPickupInfo(
            record.categoryCode,
            record.structuredFactsJson
        )?.let { return it }

        val isDelivery = record.categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY
        val isPickup = record.categoryCode == CategoryCatalog.CODE_LIFE_PICKUP
        if (!isDelivery && !isPickup) {
            return null
        }
        val sourceParts = prioritizedSourceParts(record)
        val source = sourceParts.joinToString("\n")
        if (source.isBlank()) {
            return null
        }

        val lines = source.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val code = extractPickupCode(source, lines) ?: return null
        val trackingNumber = extractTrackingNumber(source)
        val company = extractCompanyName(sourceParts, source, isDelivery, trackingNumber)

        return if (isDelivery) {
            StructuredPickupInfo(
                sectionTitle = "取件码",
                code = code,
                primaryLabel = "快递公司",
                primaryValue = fallbackStructuredValue(company),
                secondaryLabel = "取件地址",
                secondaryValue = fallbackStructuredValue(extractDeliveryAddress(sourceParts, source, lines)),
                locationText = extractDeliveryAddress(sourceParts, source, lines)
            )
        } else {
            StructuredPickupInfo(
                sectionTitle = "取餐码",
                code = code,
                primaryLabel = "店铺",
                primaryValue = fallbackStructuredValue(company),
                secondaryLabel = "商品",
                secondaryValue = fallbackStructuredValue(extractPickupItem(sourceParts, source, lines, company)),
                locationText = extractPickupLocation(sourceParts, source, lines, company)
            )
        }
    }

    private fun containsAnyKeyword(source: String, vararg keywords: String): Boolean {
        return keywords.any { source.contains(it, ignoreCase = true) }
    }

    private fun containsAnyKeyword(source: String, keywords: Collection<String>): Boolean {
        return keywords.any { source.contains(it, ignoreCase = true) }
    }

    private fun prioritizedSourceParts(record: MemoryRecord): List<String> {
        return listOfNotNull(
            record.note,
            record.sourceText,
            record.memory,
            record.summary,
            record.analysis,
            record.title
        ).map { it.trim() }.filter { it.isNotBlank() }
    }

    private fun extractFirstLabeledValue(
        sourceParts: List<String>,
        labels: List<String>,
        kind: StructuredFieldKind
    ): String? {
        sourceParts.forEach { source ->
            source.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { rawLine ->
                    labels.forEach { label ->
                        val value = extractLabeledValueFromLine(rawLine, label, kind) ?: return@forEach
                        val normalized = normalizeStructuredFieldValue(value, kind) ?: return@forEach
                        return normalized
                    }
                }
        }
        return null
    }

    private fun extractLabeledCandidatesFromSources(
        sourceParts: List<String>,
        labels: List<String>,
        kind: StructuredFieldKind
    ): List<String> {
        val results = mutableListOf<String>()
        sourceParts.forEach { source ->
            source.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { rawLine ->
                    labels.forEach { label ->
                        val value = extractLabeledValueFromLine(rawLine, label, kind) ?: return@forEach
                        val normalized = normalizeStructuredFieldValue(value, kind) ?: return@forEach
                        results += normalized
                    }
                }
        }
        return results.distinct()
    }

    private fun normalizeStructuredFieldValue(
        value: String,
        kind: StructuredFieldKind
    ): String? {
        return when (kind) {
            StructuredFieldKind.COMPANY -> normalizeCompanyValue(value)
            StructuredFieldKind.ITEM -> normalizePickupItemValue(value)
            StructuredFieldKind.LOCATION -> normalizeAddressValue(value)
        }
    }

    private fun truncateStructuredValue(value: String, kind: StructuredFieldKind): String {
        var result = value
            .replace('\n', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
        val stopIndexes = mutableListOf<Int>()

        structuredTailStopKeywords.forEach { token ->
            val index = result.indexOf(token)
            if (index > 0) {
                stopIndexes += index
            }
        }

        structuredFieldLabels.forEach { label ->
            Regex("""[，,；;。|｜]\s*${Regex.escape(label)}\s*[：:]?""")
                .find(result)
                ?.let { stopIndexes += it.range.first }
            Regex("""\s+${Regex.escape(label)}\s*[：:]""")
                .find(result)
                ?.let { stopIndexes += it.range.first }
        }

        listOf("；", ";", "。", "|", "｜").forEach { token ->
            val index = result.indexOf(token)
            if (index > 0) {
                stopIndexes += index
            }
        }
        if (kind != StructuredFieldKind.LOCATION) {
            listOf("，", ",").forEach { token ->
                val index = result.indexOf(token)
                if (index > 0) {
                    stopIndexes += index
                }
            }
        }

        val stopIndex = stopIndexes.minOrNull()
        if (stopIndex != null && stopIndex > 0) {
            result = result.substring(0, stopIndex).trim()
        }
        return result.trim('：', ':', '，', ',', '。', '；', ';', '|', '｜', ' ')
    }

    private fun extractPickupCode(source: String, lines: List<String>): String? {
        val explicitPatterns = listOf(
            Regex("""(?:${codeKeywordVariants.joinToString("|")})\s*[：:；;]?\s*([A-Za-z0-9-]{2,12})"""),
            Regex("""(?:凭码|号码|尾号)\s*[：:；;]?\s*([A-Za-z0-9-]{4,12})"""),
            Regex("""([A-Za-z]?\d{1,2}-\d{1,2}-\d{2,4})""")
        )
        explicitPatterns.forEach { pattern ->
            pattern.find(source)?.groupValues?.getOrNull(1)?.trim()?.let { raw ->
                val code = raw.uppercase(Locale.getDefault())
                if (isValidPickupCode(code)) {
                    return code
                }
            }
        }

        val candidates = mutableListOf<Pair<String, Int>>()
        val genericCodePattern = Regex("""(?<!\d)([A-Za-z]?\d{4,6})(?!\d)""")
        lines.forEachIndexed { index, line ->
            genericCodePattern.findAll(line).forEach { match ->
                val rawCode = match.groupValues[1]
                val code = normalizeOcrCodeCandidate(rawCode)
                if (!isValidPickupCode(code)) return@forEach
                var score = 0
                if (containsAnyKeyword(line, "取件", "取餐", "提货", "核销", "验证码", "领取")) score += 6
                val colonIndex = line.indexOfAny(charArrayOf('：', ':'))
                if (colonIndex >= 0 && match.range.first > colonIndex) score += 3
                if (line.length <= 18) score += 2
                if (index <= 3) score += 1
                if (code.length in 4..6) score += 2
                candidates += code to score
            }
        }
        return candidates
            .sortedByDescending { it.second }
            .map { it.first }
            .firstOrNull()
    }

    private fun isValidPickupCode(code: String): Boolean {
        val normalized = code.trim()
        if (normalized.length < 2 || normalized.length > 12) return false
        if (normalized.matches(Regex("""20\d{6,}"""))) return false
        if (normalized.matches(Regex("""\d{8,}"""))) return false
        return true
    }

    private fun normalizeOcrCodeCandidate(raw: String): String {
        val chars = raw.trim().uppercase(Locale.getDefault()).toCharArray()
        for (index in chars.indices) {
            chars[index] = when (chars[index]) {
                'O', 'Q' -> '0'
                'I', 'L', '|' -> '1'
                'Z' -> '2'
                'S' -> '5'
                'B' -> '8'
                'G' -> '6'
                else -> chars[index]
            }
        }
        return String(chars)
    }

    private fun extractTrackingNumber(source: String): String? {
        val explicit = Regex("""(?:快递单号|物流单号|运单号|单号)\s*[：:；;]?\s*([A-Za-z0-9]{8,24})""")
            .find(source)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
        if (!explicit.isNullOrBlank()) {
            return explicit
        }
        return Regex("""([A-Z]{1,4}\d{8,20})""")
            .find(source.uppercase(Locale.getDefault()))
            ?.groupValues
            ?.getOrNull(1)
    }

    private fun extractCompanyName(
        sourceParts: List<String>,
        source: String,
        isDelivery: Boolean,
        trackingNumber: String?
    ): String? {
        val labeled = extractFirstLabeledValue(
            sourceParts,
            if (isDelivery) listOf("快递公司", "配送公司", "承运公司", "物流公司") else listOf("商家", "门店", "店铺"),
            StructuredFieldKind.COMPANY
        )
        if (!labeled.isNullOrBlank()) {
            return labeled
        }
        val knownName = detectKnownName(
            source,
            if (isDelivery) {
                listOf("圆通快递", "中通快递", "申通快递", "韵达快递", "顺丰速运", "京东快递", "极兔速递", "中国邮政", "菜鸟速递", "丰巢")
            } else {
                listOf("瑞幸咖啡", "奈雪的茶", "喜茶", "蜜雪冰城", "库迪咖啡", "幸运咖", "肯德基", "麦当劳")
            }
        )
        if (!knownName.isNullOrBlank()) {
            return knownName
        }
        return if (isDelivery) inferCourierFromTrackingNumber(trackingNumber) else null
    }

    private fun fallbackStructuredValue(value: String?): String {
        return value?.trim()?.takeIf { it.isNotEmpty() } ?: "未识别"
    }

    private fun extractDeliveryAddress(sourceParts: List<String>, source: String, lines: List<String>): String? {
        val stationLike = extractStationLikeName(source)
        val labeledCandidates = extractLabeledLocationCandidatesFromSources(
            sourceParts,
            listOf("取件地址", "取货地址", "收货地址", "地址", "地点", "驿站")
        )
            .sortedByDescending { scoreAddressCandidate(it, stationLike) }
        val labeled = labeledCandidates.firstOrNull()

        val addressLike = lines.asSequence()
            .map { truncateStructuredValue(it, StructuredFieldKind.LOCATION) }
            .firstOrNull { candidate ->
                candidate.isNotBlank() &&
                    candidate != stationLike &&
                    !isStatusLikeText(candidate) &&
                    !isLikelyCodeOrLogisticsText(candidate) &&
                    looksLikeLocationText(candidate)
            }
            ?.let(::normalizeAddressValue)

        return when {
            !stationLike.isNullOrBlank() && !labeled.isNullOrBlank() && labeled != stationLike -> mergeStationAndDetail(stationLike, labeled)
            !stationLike.isNullOrBlank() && !addressLike.isNullOrBlank() && addressLike != stationLike -> mergeStationAndDetail(stationLike, addressLike)
            !stationLike.isNullOrBlank() -> normalizeAddressValue(stationLike)
            !labeled.isNullOrBlank() -> labeled
            else -> addressLike
        }
    }

    private fun mergeStationAndDetail(stationLike: String, detail: String): String {
        val normalizedStation = normalizeAddressValue(stationLike).orEmpty()
        val normalizedDetail = normalizeAddressValue(detail).orEmpty()
        if (normalizedStation.isBlank()) return normalizedDetail
        if (normalizedDetail.isBlank()) return normalizedStation
        if (normalizedDetail.contains(normalizedStation)) {
            return normalizedDetail
        }
        if (!looksLikeLocationText(normalizedDetail)) {
            return normalizedStation
        }
        return "$normalizedStation $normalizedDetail".trim()
    }

    private fun extractPickupItem(sourceParts: List<String>, source: String, lines: List<String>, storeName: String?): String? {
        extractFirstLabeledValue(
            sourceParts,
            listOf("商品", "商品名", "餐品", "菜品", "套餐", "饮品", "饮料", "物品"),
            StructuredFieldKind.ITEM
        )?.let { return it }

        return lines.asSequence()
            .map(::normalizePickupItemValue)
            .firstOrNull { candidate ->
                !candidate.isNullOrBlank() &&
                    candidate != storeName &&
                    !candidate.contains("外卖", ignoreCase = true) &&
                    !candidate.contains("美团", ignoreCase = true) &&
                    !candidate.contains("饿了么", ignoreCase = true)
            }
    }

    private fun extractPickupLocation(sourceParts: List<String>, source: String, lines: List<String>, storeName: String?): String? {
        val stationLike = extractStationLikeName(source)
        val explicitLocationCandidates = extractLabeledLocationCandidatesFromSources(
            sourceParts,
            listOf("取餐地址", "门店地址", "地址", "地点", "取餐点")
        )
            .sortedByDescending { scoreAddressCandidate(it, stationLike ?: storeName) }
        val labeled = explicitLocationCandidates.firstOrNull()
        val storeLocationCandidates = extractLabeledLocationCandidatesFromSources(
            sourceParts,
            listOf("门店", "窗口", "食堂")
        )
            .sortedByDescending { scoreAddressCandidate(it, stationLike ?: storeName) }
        val storeLocation = storeLocationCandidates.firstOrNull()

        val addressLike = lines.asSequence()
            .map { truncateStructuredValue(it, StructuredFieldKind.LOCATION) }
            .firstOrNull { candidate ->
                candidate.isNotBlank() &&
                    !isStatusLikeText(candidate) &&
                    !isLikelyCodeOrLogisticsText(candidate) &&
                    looksLikeLocationText(candidate) &&
                    !containsAnyKeyword(candidate, "取餐码", "取件码", "外卖", "商品", "餐品")
            }
            ?.let(::normalizeAddressValue)

        val storeAsLocation = storeName
            ?.takeIf { looksLikeLocationText(it) || containsAnyKeyword(it, "门店", "窗口", "食堂", "校区", "店") }
            ?.let(::normalizeAddressValue)

        return when {
            !stationLike.isNullOrBlank() && !labeled.isNullOrBlank() && labeled != stationLike -> mergeStationAndDetail(stationLike, labeled)
            !stationLike.isNullOrBlank() && !addressLike.isNullOrBlank() && addressLike != stationLike -> mergeStationAndDetail(stationLike, addressLike)
            !labeled.isNullOrBlank() -> labeled
            !addressLike.isNullOrBlank() -> addressLike
            !stationLike.isNullOrBlank() -> normalizeAddressValue(stationLike)
            !storeLocation.isNullOrBlank() -> storeLocation
            else -> storeAsLocation
        }
    }

    private fun extractLabeledLocationCandidatesFromSources(sourceParts: List<String>, labels: List<String>): List<String> {
        return extractLabeledCandidatesFromSources(sourceParts, labels, StructuredFieldKind.LOCATION)
    }

    private fun normalizeAddressValue(value: String?): String? {
        if (value.isNullOrBlank()) {
            return null
        }
        val cleaned = cleanLocationCandidate(
            simplifyAddressText(
                trimAfterStopTokens(
                    truncateStructuredValue(value, StructuredFieldKind.LOCATION),
                    locationStopTokens
                )
            )
        )
        val normalized = stripStructuredLeadingWords(cleaned, listOf("位于"))
        return normalized.takeIf {
            it.isNotBlank() &&
                !isStatusLikeText(it) &&
                !isLikelyCodeOrLogisticsText(it) &&
                (stationNamePattern.containsMatchIn(it) || looksLikeLocationText(it))
        }
    }

    private fun normalizeCompanyValue(value: String): String? {
        val cleaned = cleanStructuredFieldValue(
            truncateStructuredValue(value, StructuredFieldKind.COMPANY)
        )
            .replace(Regex("""\s+"""), " ")
            .trim()
        return cleaned.takeIf(::isValidCompanyCandidate)
    }

    private fun isValidCompanyCandidate(text: String): Boolean {
        val candidate = text.trim()
        if (candidate.length !in 2..32) {
            return false
        }
        if (candidate.startsWith("地址") || candidate.startsWith("地点") || candidate.startsWith("位于")) {
            return false
        }
        if (isStatusLikeText(candidate) || isLikelyCodeOrLogisticsText(candidate)) {
            return false
        }
        return !containsAnyKeyword(candidate, companyInvalidKeywords)
    }

    private fun normalizePickupItemValue(value: String): String? {
        val cleaned = stripStructuredLeadingWords(
            cleanStructuredFieldValue(
                truncateStructuredValue(value, StructuredFieldKind.ITEM)
            ),
            listOf("为")
        )
            .replace(Regex("""\s+"""), " ")
            .trim()
        return cleaned.takeIf(::isValidPickupItemCandidate)
    }

    private fun stripStructuredLeadingWords(value: String, prefixes: List<String>): String {
        var result = value.trim()
        prefixes.forEach { prefix ->
            if (result.startsWith("$prefix：")) {
                result = result.removePrefix("$prefix：").trim()
            } else if (result.startsWith("$prefix:")) {
                result = result.removePrefix("$prefix:").trim()
            } else if (result.startsWith(prefix)) {
                result = result.removePrefix(prefix).trim()
            }
        }
        return result
    }

    private fun isValidPickupItemCandidate(text: String): Boolean {
        val candidate = text.trim()
        if (candidate.isBlank() || candidate.length <= 1) {
            return false
        }
        if (isGenericPickupItemText(candidate)) {
            return false
        }
        if (isStatusLikeText(candidate) || isLikelyCodeOrLogisticsText(candidate) || looksLikeLocationText(candidate)) {
            return false
        }
        return !containsAnyKeyword(candidate, pickupItemInvalidKeywords)
    }

    private fun simplifyAddressText(value: String): String {
        val instructionTokens = listOf("请于", "请在", "前往", "出示", "凭取件码", "凭码", "后前往", "后到")
        var result = value
            .replace('\n', ' ')
            .replace(Regex("""\s+"""), " ")
            .trim()
        instructionTokens.forEach { token ->
            val index = result.indexOf(token)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        listOf("。", "；", ";").forEach { token ->
            val index = result.indexOf(token)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        result = trimAfterBusinessNoise(result)
        return result.trim('，', '。', '；', ';', ' ')
    }

    private fun scoreAddressCandidate(candidate: String, stationLike: String?): Int {
        var score = 0
        if (!stationLike.isNullOrBlank() && candidate.contains(stationLike)) score += 10
        if (stationNamePattern.containsMatchIn(candidate)) score += 8
        if (containsAnyKeyword(candidate, "校区", "驿站", "快递柜", "自提点", "门店", "超市", "商场", "小区")) score += 6
        if (looksLikeLocationText(candidate)) score += 4
        if (containsAnyKeyword(candidate, "请于", "请在", "前往", "出示", "凭码", "今日", "今天")) score -= 8
        if (containsAnyKeyword(candidate, "收件人", "联系人", "商品", "物品", "订单号", "单号", "应付", "实付", "门店已接单")) score -= 12
        if (candidate.length > 28) score -= 3
        return score
    }

    private fun cleanStructuredFieldValue(value: String): String {
        return value
            .substringBefore('\n')
            .trim()
            .trim('：', ':', '，', ',', '。', '；', ';', '|', '｜', ' ')
    }

    private fun inferCourierFromTrackingNumber(trackingNumber: String?): String? {
        if (trackingNumber.isNullOrBlank()) {
            return null
        }
        val upper = trackingNumber.uppercase(Locale.getDefault())
        return when {
            upper.startsWith("SF") -> "顺丰速运"
            upper.startsWith("ZTO") -> "中通快递"
            upper.startsWith("STO") -> "申通快递"
            upper.startsWith("YT") -> "圆通快递"
            upper.startsWith("YD") -> "韵达快递"
            upper.startsWith("JT") || upper.startsWith("JTSD") -> "极兔速递"
            upper.startsWith("JD") || upper.startsWith("JDX") -> "京东快递"
            upper.startsWith("EMS") || upper.startsWith("E") -> "中国邮政"
            else -> null
        }
    }

    private fun extractLabeledValueFromLine(
        line: String,
        label: String,
        kind: StructuredFieldKind
    ): String? {
        val match = Regex("""(^|[，,；;。|｜])\s*${Regex.escape(label)}\s*[：:]?\s*""")
            .find(line)
            ?: return null
        val value = line.substring(match.range.last + 1).trim()
        if (value.isBlank()) {
            return null
        }
        return truncateStructuredValue(value, kind)
    }

    private fun detectKnownName(source: String, candidates: List<String>): String? {
        return candidates.firstOrNull { source.contains(it, ignoreCase = true) }
    }

    private fun extractStationLikeName(source: String): String? {
        return stationNamePattern.findAll(source)
            .mapNotNull { it.groupValues.getOrNull(1) }
            .map(::cleanLocationCandidate)
            .filter { candidate ->
                candidate.isNotBlank() &&
                    candidate !in genericStationTexts &&
                    !candidate.startsWith("联系") &&
                    !candidate.startsWith("查看")
            }
            .maxByOrNull { it.length }
    }

    private fun cleanLocationCandidate(text: String): String {
        val withoutStatus = text
            .replace(Regex("""状态\s*[：:；;]?\s*[^\n，。]*"""), "")
            .replace(Regex("""[（(]\s*(今天|今日|现在)?\s*\d{1,2}[：:]\d{2}\s*[)）]"""), "")
        return trimAfterBusinessNoise(sanitizeLocationText(withoutStatus))
            .trim('，', '。', ' ', '\n')
    }

    private fun trimAfterBusinessNoise(value: String): String {
        var result = value
        val stopTokens = listOf(
            "收件人", "联系人", "联系电话", "手机号", "电话",
            "商品名", "商品", "物品", "餐品",
            "订单号", "单号", "运单号", "快递单号", "配送员",
            "状态", "应付", "实付", "合计", "门店已接单"
        )
        stopTokens.forEach { token ->
            val index = result.indexOf(token)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        result = result.removeSuffix("，").removeSuffix(",").trim()
        return result
    }

    private fun isLikelyCodeOrLogisticsText(text: String): Boolean {
        val candidate = text.trim()
        if (candidate.isBlank()) {
            return false
        }
        if (candidate.any { it in '\u4E00'..'\u9FA5' }) {
            return containsAnyKeyword(
                candidate,
                "取件码", "取件琅", "取件玛", "取件吗",
                "取餐码", "取餐玛", "取餐吗",
                "提货码", "提货吗", "取货码", "取货吗",
                "验证码", "核销码", "领取码", "收货码",
                "尾号", "格口号", "柜口号", "柜号",
                "货架号", "架位号", "货位号",
                "快递单号", "物流单号", "运单号", "单号",
                "凭码", "号码"
            )
        }
        return candidate.matches(Regex("""[A-Za-z]?\d{4,6}""")) ||
            candidate.matches(Regex("""[A-Za-z]?\d{1,2}-\d{1,2}-\d{2,4}"""))
    }

    private fun looksLikeLocationText(text: String): Boolean {
        if (text.isBlank() || isLikelyCodeOrLogisticsText(text)) {
            return false
        }
        if (stationNamePattern.containsMatchIn(text)) {
            return true
        }
        return containsAnyKeyword(
            text,
            "省", "市", "区", "县", "镇", "乡", "村",
            "楼", "栋", "层", "室", "路", "街", "道", "号",
            "校区", "园区", "大厦", "广场", "中心", "前台",
            "门口", "东门", "西门", "南门", "北门", "附近", "旁",
            "店", "商场", "超市", "小区", "公寓", "大学", "学校",
            "医院", "馆", "站", "座", "排", "幢", "底商",
            "服务点", "取餐点"
        )
    }

    private fun isStatusLikeText(text: String): Boolean {
        return containsAnyKeyword(
            text,
            "待取件", "待取餐", "待领取", "待自取",
            "已签收", "配送中", "已送达", "已到店", "待处理"
        )
    }

    private fun isGenericPickupItemText(text: String): Boolean {
        val candidate = text.trim()
        if (candidate.isBlank()) {
            return true
        }
        if (candidate.length <= 1) {
            return true
        }
        return containsAnyKeyword(
            candidate,
            "明细", "详情", "商品明细", "餐品明细", "菜品明细",
            "物品明细", "订单明细", "查看明细", "查看详情",
            "更多详情", "商品详情"
        )
    }

    private fun trimAfterStopTokens(value: String, stopTokens: List<String>): String {
        var result = value
        stopTokens.forEach { token ->
            val index = result.indexOf(token)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        return result.trim('，', '。', ' ', '\n')
    }

    private fun sanitizeLocationText(text: String): String {
        return text
            .replace("地址：", "")
            .replace("取件地址：", "")
            .replace("取餐地址：", "")
            .replace("地点：", "")
            .replace("门店：", "")
            .replace("驿站：", "")
            .trim()
    }

}
