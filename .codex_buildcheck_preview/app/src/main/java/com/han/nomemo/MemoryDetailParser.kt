package com.han.nomemo

import java.util.Locale

object MemoryDetailParser {
    private val stationNamePattern = Regex("""([\u4E00-\u9FA5A-Za-z0-9]{2,50}(?:驿站|自提点|快递柜|门店|食堂|窗口|前台))""")
    private val genericStationTexts = setOf("联系驿站", "查看驿站", "驿站", "快递柜", "自提点")
    private val locationStopTokens = listOf(
        "状态",
        "取件码",
        "取餐码",
        "提货码",
        "取货码",
        "验证码",
        "核销码",
        "领取码",
        "尾号",
        "格口号",
        "柜口号",
        "柜号",
        "货架号",
        "架位号",
        "货位号",
        "快递单号",
        "物流单号",
        "运单号",
        "单号",
        "凭码",
        "号码"
    )

    fun parseStructuredPickupInfo(record: MemoryRecord): StructuredPickupInfo? {
        val isDelivery = record.categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY
        val isPickup = record.categoryCode == CategoryCatalog.CODE_LIFE_PICKUP
        if (!isDelivery && !isPickup) {
            return null
        }
        val sourceParts = listOfNotNull(
            record.note,
            record.memory,
            record.sourceText,
            record.summary,
            record.title,
            record.analysis
        ).map { it.trim() }.filter { it.isNotBlank() }
        val source = sourceParts.joinToString("\n")
        if (source.isBlank()) {
            return null
        }

        val lines = source.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val code = extractPickupCode(source, lines) ?: return null
        val trackingNumber = extractTrackingNumber(source)
        val company = extractCompanyName(source, isDelivery, trackingNumber)

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

    private fun extractPickupCode(source: String, lines: List<String>): String? {
        val explicitPatterns = listOf(
            Regex("""(?:取件码|取餐码|提货码|取货码|自提码|验证码|核销码|领取码|收货码)\s*[:：]?\s*([A-Za-z0-9-]{3,12})"""),
            Regex("""(?:凭码|号码|尾号)\s*[:：]?\s*([A-Za-z0-9-]{4,12})"""),
            Regex("""([A-Za-z]?\d{1,2}-\d{1,2}-\d{2,4})""")
        )
        explicitPatterns.forEach { pattern ->
            pattern.find(source)?.groupValues?.getOrNull(1)?.trim()?.let(::normalizeOcrCodeCandidate)?.takeIf { isValidPickupCode(it) }?.let {
                return it
            }
        }

        val candidates = mutableListOf<Pair<String, Int>>()
        val genericCodePattern = Regex("""(?<!\d)([A-Za-z]?\d{4,6})(?!\d)""")
        lines.forEachIndexed { index, line ->
            genericCodePattern.findAll(line).forEach { match ->
                val code = normalizeOcrCodeCandidate(match.groupValues[1])
                if (!isValidPickupCode(code)) return@forEach
                var score = 0
                if (containsAnyKeyword(line, "取件", "取餐", "提货", "核销", "验证码", "领取")) score += 6
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

    private fun extractSecondaryCode(source: String, lines: List<String>): Pair<String, String>? {
        val explicitPatterns = listOf(
            "格口号" to Regex("""(?:格口号|柜口号|柜号|货架号|架位号|货位号)\s*[:：]?\s*([A-Za-z0-9-]{2,16})"""),
            "尾号" to Regex("""(?:尾号)\s*[:：]?\s*([A-Za-z0-9-]{2,16})"""),
            "单号" to Regex("""(?:单号)\s*[:：]?\s*([A-Za-z0-9-]{5,20})""")
        )
        explicitPatterns.forEach { (label, pattern) ->
            pattern.find(source)?.groupValues?.getOrNull(1)?.trim()?.let(::normalizeOcrCodeCandidate)?.takeIf { it.isNotBlank() }?.let {
                return label to it
            }
        }

        lines.forEach { line ->
            Regex("""([A-Za-z]?\d{1,2}-\d{1,2}-\d{2,4})""")
                .find(line)
                ?.groupValues
                ?.getOrNull(1)
                ?.let(::normalizeOcrCodeCandidate)
                ?.takeIf { it.isNotBlank() }
                ?.let { return "格口号" to it }
        }
        return null
    }

    private fun isValidPickupCode(code: String): Boolean {
        val normalized = code.trim()
        if (normalized.length < 4 || normalized.length > 12) return false
        if (normalized.matches(Regex("""20\d{6,}"""))) return false
        if (normalized.matches(Regex("""\d{8,}"""))) return false
        return true
    }

    private fun normalizeOcrCodeCandidate(raw: String): String {
        val chars = raw.trim().uppercase(Locale.getDefault()).toCharArray()
        for (index in chars.indices) {
            chars[index] = when (chars[index]) {
                'O', 'Q', 'D' -> '0'
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
        val explicit = Regex("""(?:快递单号|物流单号|运单号|单号)\s*[:：]?\s*([A-Za-z0-9]{8,24})""")
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

    private fun extractCompanyName(source: String, isDelivery: Boolean, trackingNumber: String?): String? {
        val labeled = extractLabeledValue(
            source,
            if (isDelivery) listOf("快递公司", "配送公司", "承运公司", "物流公司") else listOf("商家", "门店", "店铺")
        )
        if (!labeled.isNullOrBlank()) {
            return labeled
        }
        val knownName = detectKnownName(
            source,
            if (isDelivery) {
                listOf("圆通快递", "中通快递", "申通快递", "韵达快递", "顺丰速运", "京东快递", "极兔速递", "中国邮政", "菜鸟速递", "丰巢")
            } else {
                listOf("瑞幸咖啡", "奈雪的茶", "喜茶", "蜜雪冰城", "库迪咖啡", "肯德基", "麦当劳")
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
            .mapNotNull(::normalizeAddressValue)
            .sortedByDescending { scoreAddressCandidate(it, stationLike) }
        val labeled = labeledCandidates.firstOrNull()

        val addressLike = lines.asSequence()
            .map(::cleanLocationCandidate)
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
        extractLabeledValue(
            source,
            listOf("商品", "商品名", "餐品", "菜品", "套餐", "饮品", "饮料", "物品")
        )?.let { return cleanStructuredFieldValue(it) }

        sourceParts.forEach { part ->
            val partSource = part.trim()
            if (partSource.isBlank()) return@forEach
            extractLabeledValue(
                partSource,
                listOf("商品", "商品名", "餐品", "菜品", "套餐", "饮品", "饮料", "物品")
            )?.let { return cleanStructuredFieldValue(it) }
        }

        return lines.asSequence()
            .map(::cleanStructuredFieldValue)
            .firstOrNull { candidate ->
                candidate.isNotBlank() &&
                    candidate != storeName &&
                    !isStatusLikeText(candidate) &&
                    !isLikelyCodeOrLogisticsText(candidate) &&
                    !looksLikeLocationText(candidate) &&
                    !containsAnyKeyword(candidate, "取餐码", "取件码", "外卖", "美团", "饿了么", "快递", "驿站", "地址", "地点", "门店地址")
            }
    }

    private fun extractPickupLocation(sourceParts: List<String>, source: String, lines: List<String>, storeName: String?): String? {
        val stationLike = extractStationLikeName(source)
        val labeledCandidates = extractLabeledLocationCandidatesFromSources(
            sourceParts,
            listOf("取餐地址", "门店地址", "地址", "地点", "取餐点", "门店", "窗口", "食堂")
        )
            .mapNotNull(::normalizeAddressValue)
            .sortedByDescending { scoreAddressCandidate(it, stationLike ?: storeName) }
        val labeled = labeledCandidates.firstOrNull()

        val addressLike = lines.asSequence()
            .map(::cleanLocationCandidate)
            .firstOrNull { candidate ->
                candidate.isNotBlank() &&
                    !isStatusLikeText(candidate) &&
                    !isLikelyCodeOrLogisticsText(candidate) &&
                    looksLikeLocationText(candidate) &&
                    !containsAnyKeyword(candidate, "取餐码", "外卖", "商品", "餐品")
            }
            ?.let(::normalizeAddressValue)

        val storeAsLocation = storeName
            ?.takeIf { looksLikeLocationText(it) || containsAnyKeyword(it, "门店", "窗口", "食堂", "校区") }
            ?.let(::normalizeAddressValue)

        return when {
            !stationLike.isNullOrBlank() && !labeled.isNullOrBlank() && labeled != stationLike -> mergeStationAndDetail(stationLike, labeled)
            !stationLike.isNullOrBlank() && !addressLike.isNullOrBlank() && addressLike != stationLike -> mergeStationAndDetail(stationLike, addressLike)
            !labeled.isNullOrBlank() -> labeled
            !addressLike.isNullOrBlank() -> addressLike
            !stationLike.isNullOrBlank() -> normalizeAddressValue(stationLike)
            else -> storeAsLocation
        }
    }

    private fun extractLabeledLocationValueFromSources(sourceParts: List<String>, labels: List<String>): String? {
        sourceParts.forEach { source ->
            val lines = source.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
            extractLabeledLocationValue(lines, labels)?.let { return it }
        }
        return null
    }

    private fun extractLabeledLocationCandidatesFromSources(sourceParts: List<String>, labels: List<String>): List<String> {
        val results = mutableListOf<String>()
        sourceParts.forEach { source ->
            val lines = source.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
            labels.forEach { label ->
                lines.forEach { rawLine ->
                    extractLocationValueFromLine(rawLine, label)?.let { value ->
                        val cleaned = cleanLocationCandidate(trimAfterStopTokens(value, locationStopTokens))
                        if (cleaned.isNotBlank() && !isLikelyCodeOrLogisticsText(cleaned)) {
                            results += cleaned
                        }
                    }
                }
            }
        }
        return results.distinct()
    }

    private fun normalizeAddressValue(value: String?): String? {
        if (value.isNullOrBlank()) {
            return null
        }
        val cleaned = cleanLocationCandidate(
            simplifyAddressText(trimAfterStopTokens(value, locationStopTokens))
        )
        return cleaned.takeIf {
            it.isNotBlank() &&
                !isStatusLikeText(it) &&
                !isLikelyCodeOrLogisticsText(it) &&
                (stationNamePattern.containsMatchIn(it) || looksLikeLocationText(it))
        }
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
        if (containsAnyKeyword(candidate, "校区", "驿站", "快递柜", "自提点", "门店")) score += 6
        if (looksLikeLocationText(candidate)) score += 4
        if (containsAnyKeyword(candidate, "请于", "请在", "前往", "出示", "凭码", "今日", "今天")) score -= 8
        if (containsAnyKeyword(candidate, "收件人", "联系人", "商品", "物品", "订单号", "单号")) score -= 12
        if (candidate.length > 28) score -= 3
        return score
    }

    private fun cleanStructuredFieldValue(value: String): String {
        return value
            .substringBefore('\n')
            .trim()
            .trim('：', ':', '，', '。', ' ')
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

    private fun extractStatus(source: String): String? {
        return Regex("""(?:状态)\s*[:：]?\s*([^\n，。]{2,24})""")
            .find(source)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
    }

    private fun extractLocationBlock(source: String, lines: List<String>, isDelivery: Boolean): String? {
        val labeled = extractLabeledLocationValue(
            lines,
            if (isDelivery) {
                listOf("取件地址", "取货地址", "收货地址", "地址", "地点", "驿站")
            } else {
                listOf("取餐地址", "门店地址", "地址", "地点", "门店", "取餐点")
            }
        )
        if (!labeled.isNullOrBlank()) {
            return labeled
        }

        val stationLike = extractStationLikeName(source)
        val detailLike = lines.asSequence()
            .map(::cleanLocationCandidate)
            .firstOrNull { candidate ->
                candidate.isNotBlank() &&
                    candidate != stationLike &&
                    !isStatusLikeText(candidate) &&
                    !isLikelyCodeOrLogisticsText(candidate) &&
                    looksLikeLocationText(candidate)
            }
        val fallbackDetail = lines.asSequence()
            .map(::cleanLocationCandidate)
            .firstOrNull { candidate ->
                candidate.isNotBlank() &&
                    candidate != stationLike &&
                    !isStatusLikeText(candidate) &&
                    !isLikelyCodeOrLogisticsText(candidate)
            }

        return when {
            !stationLike.isNullOrBlank() && !detailLike.isNullOrBlank() && detailLike != stationLike ->
                stationLike + "\n" + detailLike
            !stationLike.isNullOrBlank() -> stationLike
            !detailLike.isNullOrBlank() -> detailLike
            !fallbackDetail.isNullOrBlank() -> fallbackDetail
            else -> null
        }
    }

    private fun extractLabeledLocationValue(lines: List<String>, labels: List<String>): String? {
        labels.forEach { label ->
            lines.forEach { rawLine ->
                val line = rawLine.trim()
                extractLocationValueFromLine(line, label)?.let { value ->
                    val cleaned = cleanLocationCandidate(trimAfterStopTokens(value, locationStopTokens))
                    if (cleaned.isNotBlank() && !isLikelyCodeOrLogisticsText(cleaned)) {
                        return cleaned
                    }
                }
            }
        }
        return null
    }

    private fun extractLocationValueFromLine(line: String, label: String): String? {
        listOf("$label：", "$label:").forEach { token ->
            val index = line.indexOf(token)
            if (index >= 0) {
                return line.substring(index + token.length).trim()
            }
        }
        val plainIndex = line.indexOf(label)
        if (plainIndex >= 0) {
            val value = line.substring(plainIndex + label.length)
                .trimStart('：', ':', '，', ',', ' ', '　')
                .trim()
            if (value.isNotBlank()) {
                return value
            }
        }
        if (line.startsWith(label)) {
            return line.removePrefix(label).trimStart('：', ':', ' ', '　')
        }
        return null
    }

    private fun extractLabeledValue(source: String, labels: List<String>): String? {
        labels.forEach { label ->
            Regex("""(?:$label)\s*[:：]?\s*([^\n]{2,80})""")
                .find(source)
                ?.groupValues
                ?.getOrNull(1)
                ?.trim()
                ?.trim('：', ':', '，', '。', ' ')
                ?.takeIf { it.isNotBlank() }
                ?.let { return cleanLocationCandidate(it) }
        }
        return null
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

    private fun normalizeLocationTitle(rawLocation: String?): String? {
        if (rawLocation.isNullOrBlank()) {
            return null
        }
        val cleaned = cleanLocationCandidate(rawLocation)
        if (cleaned.isBlank() || isStatusLikeText(cleaned)) {
            return null
        }
        val stationMatch = stationNamePattern.find(cleaned)
            ?.groupValues
            ?.getOrNull(1)
        if (!stationMatch.isNullOrBlank()) {
            return stationMatch
        }
        return cleaned.lineSequence()
            .map { it.trim() }
            .firstOrNull {
                it.isNotBlank() &&
                    !isStatusLikeText(it) &&
                    !isLikelyCodeOrLogisticsText(it)
            }
            ?.ifBlank { cleaned }
            ?: cleaned.takeIf { !isLikelyCodeOrLogisticsText(it) }
    }

    private fun extractAddressDetail(rawLocation: String?, locationTitle: String?): String? {
        if (rawLocation.isNullOrBlank()) {
            return null
        }
        val cleaned = cleanLocationCandidate(rawLocation)
        val lines = cleaned.lines().map { it.trim() }.filter { it.isNotBlank() }
        val locationLike = lines.firstOrNull {
            it != locationTitle &&
                !isStatusLikeText(it) &&
                !isLikelyCodeOrLogisticsText(it) &&
                looksLikeLocationText(it)
        }
        if (!locationLike.isNullOrBlank()) {
            return locationLike
        }
        val firstNonTitle = lines.firstOrNull {
            it != locationTitle &&
                !isStatusLikeText(it) &&
                !isLikelyCodeOrLogisticsText(it)
        }
        if (!firstNonTitle.isNullOrBlank()) {
            return firstNonTitle
        }
        if (!locationTitle.isNullOrBlank()) {
            val removed = cleanLocationCandidate(cleaned.replace(locationTitle, ""))
            if (removed.isNotBlank() && !isStatusLikeText(removed) && !isLikelyCodeOrLogisticsText(removed)) {
                return removed
            }
        }
        return null
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

    private fun cleanLocationCandidate(text: String): String {
        val withoutStatus = text
            .replace(Regex("""状态\s*[:：]?\s*[^\n，。]*"""), "")
            .replace(Regex("""[（(]\s*(今天|今日|现在)?\s*\d{1,2}[:：]\d{2}\s*[)）]"""), "")
        return trimAfterBusinessNoise(sanitizeLocationText(withoutStatus))
            .trim('，', '。', ' ', '\n')
    }

    private fun trimAfterBusinessNoise(value: String): String {
        var result = value
        val stopTokens = listOf(
            "收件人",
            "联系人",
            "联系电话",
            "手机号",
            "电话",
            "商品名",
            "商品",
            "物品",
            "餐品",
            "订单号",
            "单号",
            "运单号",
            "快递单号",
            "配送员"
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
        if (
            containsAnyKeyword(
                candidate,
                "取件码",
                "取餐码",
                "提货码",
                "取货码",
                "验证码",
                "核销码",
                "领取码",
                "收货码",
                "尾号",
                "格口号",
                "柜口号",
                "柜号",
                "货架号",
                "架位号",
                "货位号",
                "快递单号",
                "物流单号",
                "运单号",
                "单号",
                "凭码",
                "号码"
            )
        ) {
            return true
        }
        return candidate.matches(Regex("""[A-Za-z]?\d{4,6}""")) ||
            candidate.matches(Regex("""[A-Za-z]?\d{1,2}-\d{1,2}-\d{2,4}""")) ||
            candidate.matches(Regex("""[A-Za-z0-9-]{4,20}"""))
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
            "楼",
            "栋",
            "层",
            "室",
            "路",
            "街",
            "道",
            "号",
            "校区",
            "园区",
            "大厦",
            "广场",
            "中心",
            "前台",
            "门口",
            "东门",
            "西门",
            "南门",
            "北门",
            "附近",
            "旁"
        )
    }

    private fun isStatusLikeText(text: String): Boolean {
        return containsAnyKeyword(
            text,
            "待取件",
            "待取餐",
            "待领取",
            "待自取",
            "已签收",
            "配送中",
            "已送达",
            "已到店",
            "待处理"
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
}
