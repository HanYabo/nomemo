package com.han.nomemo

import org.json.JSONObject
import java.util.Locale

private const val DOMAIN_PICKUP = "pickup"
private const val DOMAIN_DELIVERY = "delivery"
private const val DOMAIN_TICKET = "ticket"
private const val DOMAIN_SCHEDULE = "schedule"
private const val DOMAIN_TODO = "todo"
private const val DOMAIN_CARD = "card"
private const val DOMAIN_NOTE = "note"

data class MemoryStructuredFacts(
    val domain: String = DOMAIN_NOTE,
    val pickupCode: String? = null,
    val pickupCodeType: String? = null,
    val pickupCodeConfidence: Double = 0.0,
    val pickupCodeEvidence: String? = null,
    val location: String? = null,
    val locationConfidence: Double = 0.0,
    val locationEvidence: String? = null,
    val merchantOrCompany: String? = null,
    val itemName: String? = null,
    val orderNumber: String? = null,
    val trackingNumber: String? = null,
    val amount: String? = null,
    val timeWindow: String? = null,
    val rawVisibleText: String? = null
)

object MemoryStructuredFactsJson {
    private val allowedDomains = setOf(
        DOMAIN_PICKUP,
        DOMAIN_DELIVERY,
        DOMAIN_TICKET,
        DOMAIN_SCHEDULE,
        DOMAIN_TODO,
        DOMAIN_CARD,
        DOMAIN_NOTE
    )

    @JvmStatic
    fun parse(raw: String?): MemoryStructuredFacts? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return runCatching {
            val root = JSONObject(text)
            val json = root.optJSONObject("structuredFacts") ?: root
            MemoryStructuredFacts(
                domain = normalizeDomain(json.optCleanString("domain")),
                pickupCode = sanitizeCodeValue(json.optCleanString("pickupCode")),
                pickupCodeType = json.optCleanString("pickupCodeType"),
                pickupCodeConfidence = json.optConfidence("pickupCodeConfidence"),
                pickupCodeEvidence = json.optCleanString("pickupCodeEvidence"),
                location = sanitizeFactValue(json.optCleanString("location")),
                locationConfidence = json.optConfidence("locationConfidence"),
                locationEvidence = json.optCleanString("locationEvidence"),
                merchantOrCompany = sanitizeFactValue(json.optCleanString("merchantOrCompany")),
                itemName = sanitizeFactValue(json.optCleanString("itemName")),
                orderNumber = sanitizeCodeValue(json.optCleanString("orderNumber")),
                trackingNumber = sanitizeCodeValue(json.optCleanString("trackingNumber")),
                amount = sanitizeFactValue(json.optCleanString("amount")),
                timeWindow = sanitizeFactValue(json.optCleanString("timeWindow")),
                rawVisibleText = json.optCleanString("rawVisibleText")
            )
        }.getOrNull()
    }

    @JvmStatic
    fun toJson(facts: MemoryStructuredFacts?): String {
        facts ?: return ""
        val json = JSONObject()
        json.put("domain", normalizeDomain(facts.domain))
        json.putNullable("pickupCode", sanitizeCodeValue(facts.pickupCode))
        json.putNullable("pickupCodeType", facts.pickupCodeType)
        json.put("pickupCodeConfidence", facts.pickupCodeConfidence.coerceIn(0.0, 1.0))
        json.putNullable("pickupCodeEvidence", facts.pickupCodeEvidence)
        json.putNullable("location", sanitizeFactValue(facts.location))
        json.put("locationConfidence", facts.locationConfidence.coerceIn(0.0, 1.0))
        json.putNullable("locationEvidence", facts.locationEvidence)
        json.putNullable("merchantOrCompany", sanitizeFactValue(facts.merchantOrCompany))
        json.putNullable("itemName", sanitizeFactValue(facts.itemName))
        json.putNullable("orderNumber", sanitizeCodeValue(facts.orderNumber))
        json.putNullable("trackingNumber", sanitizeCodeValue(facts.trackingNumber))
        json.putNullable("amount", sanitizeFactValue(facts.amount))
        json.putNullable("timeWindow", sanitizeFactValue(facts.timeWindow))
        json.putNullable("rawVisibleText", facts.rawVisibleText)
        return json.toString()
    }

    private fun normalizeDomain(value: String?): String {
        val normalized = value?.trim()?.lowercase(Locale.ROOT).orEmpty()
        return if (normalized in allowedDomains) normalized else DOMAIN_NOTE
    }
}

object MemoryFactExtractor {
    private data class Candidate(
        val value: String,
        val type: String,
        val score: Int,
        val evidence: String
    )

    private val codeLabelRegex = Regex(
        """(?i)(取件码|取餐码|提货码|取货码|自提码|核销码|领取码|收货码|凭码|柜号|格口号|货架号|架位号|尾号|验证码)\s*[:：]?\s*([A-Za-z0-9][A-Za-z0-9\-_]{0,14})"""
    )
    private val stationCodeRegex = Regex("""(?<![A-Za-z0-9])([A-Za-z]?\d{1,2}-\d{1,2}-\d{2,5})(?![A-Za-z0-9])""")
    private val genericShortCodeRegex = Regex("""(?<![A-Za-z0-9])([A-Za-z]\d{2,5}|\d{2,6})(?![A-Za-z0-9])""")
    private val orderRegex = Regex("""(?:订单号|订单编号|订单|单号)\s*[:：]?\s*([A-Za-z0-9\-_]{8,32})""")
    private val trackingRegex = Regex("""(?:运单号|快递单号|物流单号|物流编号)\s*[:：]?\s*([A-Za-z0-9\-_]{8,32})""")
    private val amountRegex = Regex("""(?:应付|实付|合计|总计|金额|付款|支付)\s*[:：]?\s*(¥?\s*\d+(?:\.\d{1,2})?\s*元?)""")
    private val timeRegex = Regex("""((?:20\d{2}[年\-/\.]\d{1,2}[月\-/\.]\d{1,2}日?)?(?:\s*)\d{1,2}[:：]\d{2}(?:\s*[-~至到]\s*\d{1,2}[:：]\d{2})?)""")

    private val courierNames = listOf(
        "顺丰", "中通", "圆通", "申通", "韵达", "极兔", "京东", "邮政", "EMS", "德邦",
        "菜鸟", "丰巢"
    )
    private val merchantNames = listOf(
        "瑞幸", "星巴克", "幸运咖", "蜜雪冰城", "奈雪", "喜茶", "库迪", "肯德基",
        "麦当劳", "美团", "饿了么"
    )
    private val locationTokens = listOf(
        "省", "市", "区", "县", "镇", "乡", "村", "路", "街", "道", "号", "楼",
        "栋", "单元", "室", "门", "店", "校区", "园区", "广场", "中心", "小区",
        "公寓", "大学", "学校", "医院", "驿站", "快递柜", "自提点", "取件点",
        "取餐点", "食堂", "窗口", "前台", "菜鸟", "丰巢"
    )
    private val locationStopLabels = listOf(
        "取件码", "取餐码", "提货码", "取货码", "自提码", "核销码", "验证码",
        "订单号", "订单编号", "运单号", "快递单号", "物流单号", "金额", "应付",
        "实付", "状态", "商品", "餐品", "物品", "电话", "手机号"
    )
    private val statusWords = listOf(
        "待取件", "待取餐", "待领取", "待自取", "已送达", "已到店", "已完成",
        "配送中", "制作中", "门店已接单", "请及时", "请尽快", "请于", "请在",
        "前往", "出示"
    )
    private val pickupDomainKeywords = listOf(
        "取餐", "外卖", "奶茶", "咖啡", "饮品", "饮料", "餐品", "菜品", "门店", "到店", "喜茶", "瑞幸"
    )
    private val deliveryDomainKeywords = listOf(
        "取件", "快递", "包裹", "驿站", "菜鸟", "丰巢", "自提点", "快递柜", "物流", "运单"
    )

    @JvmStatic
    fun extractLocalFacts(
        userText: String?,
        aiRawVisibleText: String?,
        memory: String?,
        analysis: String?,
        summary: String?,
        title: String?,
        categoryCode: String?
    ): MemoryStructuredFacts {
        val sources = listOf(userText, aiRawVisibleText, memory, analysis, summary, title)
            .mapNotNull { normalizeVisibleText(it).takeIf { value -> value.isNotBlank() } }
            .distinct()
        val source = sources.joinToString("\n")
        val lines = source.lines().map { it.trim() }.filter { it.isNotBlank() }
        val code = extractPickupCode(lines, source, categoryCode)
        val location = extractLocation(lines)
        val domain = inferDomain(source, categoryCode, code?.evidence)
        val merchant = extractMerchantOrCompany(lines, source, domain)
        val item = extractItem(lines)
        val orderNumber = extractFirst(orderRegex, source)
        val trackingNumber = extractFirst(trackingRegex, source)
        val amount = extractFirst(amountRegex, source)
        val timeWindow = extractFirst(timeRegex, source)

        return MemoryStructuredFacts(
            domain = domain,
            pickupCode = code?.value,
            pickupCodeType = code?.type,
            pickupCodeConfidence = code?.score?.toConfidence() ?: 0.0,
            pickupCodeEvidence = code?.evidence,
            location = location?.value,
            locationConfidence = location?.score?.toConfidence() ?: 0.0,
            locationEvidence = location?.evidence,
            merchantOrCompany = merchant,
            itemName = item,
            orderNumber = orderNumber,
            trackingNumber = trackingNumber,
            amount = amount,
            timeWindow = timeWindow,
            rawVisibleText = aiRawVisibleText?.trim()?.takeIf { it.isNotEmpty() }
        )
    }

    private fun extractPickupCode(lines: List<String>, source: String, categoryCode: String?): Candidate? {
        val candidates = mutableListOf<Candidate>()
        lines.forEachIndexed { index, line ->
            codeLabelRegex.findAll(line).forEach { match ->
                val label = match.groupValues[1]
                val raw = match.groupValues[2]
                val code = sanitizeCodeValue(raw) ?: return@forEach
                val type = codeTypeForLabel(label, categoryCode)
                if (!isValidPickupCode(code, line, hasExplicitPickupLabel = label.isStrongCodeLabel())) return@forEach
                var score = when {
                    label.contains("取件") || label.contains("取餐") || label.contains("提货") ||
                        label.contains("取货") || label.contains("自提") -> 92
                    label.contains("核销") || label.contains("领取") || label.contains("收货") -> 78
                    label.contains("柜") || label.contains("货架") || label.contains("架位") -> 72
                    label.contains("验证码") -> 58
                    else -> 52
                }
                if (line.length <= 24) score += 4
                if (index <= 4) score += 2
                candidates += Candidate(code, type, score, line)
            }

            stationCodeRegex.findAll(line).forEach { match ->
                val code = sanitizeCodeValue(match.groupValues[1]) ?: return@forEach
                if (!isValidPickupCode(code, line, hasExplicitPickupLabel = false)) return@forEach
                var score = 74
                if (line.containsAny("取件", "快递", "包裹", "驿站", "菜鸟", "丰巢", "自提")) score += 14
                if (categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY) score += 6
                candidates += Candidate(code, "shelf", score, line)
            }

            genericShortCodeRegex.findAll(line).forEach { match ->
                if (isEmbeddedInHyphenatedCode(line, match.range)) return@forEach
                val code = sanitizeCodeValue(match.groupValues[1]) ?: return@forEach
                if (!isValidPickupCode(code, line, hasExplicitPickupLabel = false)) return@forEach
                val window = contextualWindow(lines, index)
                var score = 35
                if (line.containsAny("取件", "取餐", "提货", "取货", "自提", "核销", "领取")) score += 38
                if (line.containsAny("快递", "包裹", "驿站", "菜鸟", "丰巢", "外卖", "门店", "餐")) score += 16
                if (line.containsAny("订单", "运单", "单号", "电话", "手机号", "金额", "应付", "实付")) score -= 55
                if (line.length <= 18) score += 6
                if (index <= 4) score += 4
                if (code.length in 4..6) score += 6
                if (line.matches(Regex("""[A-Za-z]?\d{3,6}"""))) score += 10
                score += contextualPickupCodeBoost(window, source, categoryCode)
                if (window.containsAny("订单号", "运单号", "物流单号", "快递单号", "手机号")) score -= 16
                if (score >= 55) {
                    candidates += Candidate(code, codeTypeForContext(window, categoryCode), score, window)
                }
            }
        }
        return candidates
            .sortedWith(compareByDescending<Candidate> { it.score }.thenBy { it.value.length })
            .firstOrNull()
    }

    private fun extractLocation(lines: List<String>): Candidate? {
        val labeled = extractLabeledLineValue(
            lines,
            "取件地址", "取餐地址", "取货地址", "收货地址", "门店地址", "地址", "地点",
            "驿站", "自提点", "取件点", "取餐点"
        )?.let { raw ->
            sanitizeLocationValue(raw)?.let { Candidate(it, "location", 82, raw) }
        }
        if (labeled != null) return labeled

        return lines.asSequence()
            .mapNotNull { line ->
                val value = sanitizeLocationValue(line) ?: return@mapNotNull null
                if (!looksLikeLocation(value)) return@mapNotNull null
                var score = 48
                if (value.containsAny("驿站", "快递柜", "自提点", "菜鸟", "丰巢")) score += 22
                if (value.containsAny("路", "街", "号", "楼", "小区", "大学", "校区", "门店")) score += 12
                Candidate(value, "location", score, line)
            }
            .sortedByDescending { it.score }
            .firstOrNull()
    }

    private fun extractMerchantOrCompany(lines: List<String>, source: String, domain: String): String? {
        val labels = if (domain == DOMAIN_DELIVERY) {
            arrayOf("快递公司", "物流公司", "承运公司", "配送公司")
        } else {
            arrayOf("商家", "门店", "店铺", "餐厅")
        }
        extractLabeledLineValue(lines, *labels)
            ?.let(::sanitizeShortText)
            ?.takeIf { isValidName(it) }
            ?.let { return it }
        val known = if (domain == DOMAIN_DELIVERY) courierNames else merchantNames
        return known.firstOrNull { source.contains(it, ignoreCase = true) }
    }

    private fun extractItem(lines: List<String>): String? {
        return extractLabeledLineValue(lines, "商品", "商品名", "餐品", "菜品", "套餐", "饮品", "饮料", "物品")
            ?.let(::sanitizeShortText)
            ?.takeIf { it.length in 2..48 && !looksLikeLocation(it) && !isStatusLike(it) }
    }

    private fun extractLabeledLineValue(lines: List<String>, vararg labels: String): String? {
        val labelPattern = labels.joinToString("|") { Regex.escape(it) }
        val regex = Regex("""(?:^|[\s,，;；|])(?:$labelPattern)\s*[:：]\s*([^\n;；|]+)""")
        lines.forEach { line ->
            regex.find(line)?.groupValues?.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() }?.let {
                return it
            }
        }
        return null
    }

    private fun extractFirst(regex: Regex, source: String): String? {
        return regex.find(source)?.groupValues?.getOrNull(1)?.trim()?.takeIf { it.isNotEmpty() }
    }

    private fun inferDomain(source: String, categoryCode: String?, codeEvidence: String?): String {
        val evidence = listOf(source, codeEvidence.orEmpty()).joinToString(" ")
        val pickupScore = pickupDomainScore(evidence, categoryCode)
        val deliveryScore = deliveryDomainScore(evidence, categoryCode)
        if (pickupScore >= 34 || deliveryScore >= 34) {
            if (pickupScore >= deliveryScore + 6) return DOMAIN_PICKUP
            if (deliveryScore >= pickupScore + 6) return DOMAIN_DELIVERY
        }
        return when {
            evidence.containsAny("票", "券", "车次", "航班", "座位") -> DOMAIN_TICKET
            evidence.containsAny("会议", "日程", "预约", "时间") -> DOMAIN_SCHEDULE
            evidence.containsAny("待办", "完成", "处理", "跟进", "提交") -> DOMAIN_TODO
            evidence.containsAny("身份证", "银行卡", "会员卡", "门禁卡", "证件") -> DOMAIN_CARD
            pickupScore >= deliveryScore && pickupScore >= 24 -> DOMAIN_PICKUP
            deliveryScore > pickupScore && deliveryScore >= 24 -> DOMAIN_DELIVERY
            else -> DOMAIN_NOTE
        }
    }

    private fun contextualWindow(lines: List<String>, index: Int): String {
        val start = (index - 2).coerceAtLeast(0)
        val end = (index + 2).coerceAtMost(lines.lastIndex)
        return (start..end).joinToString(" ") { lines[it] }
    }

    private fun contextualPickupCodeBoost(window: String, source: String, categoryCode: String?): Int {
        var score = 0
        if (window.containsAny(*pickupDomainKeywords.toTypedArray())) score += 18
        if (window.containsAny(*deliveryDomainKeywords.toTypedArray())) score += 18
        if (merchantNames.any { window.contains(it) }) score += 16
        if (courierNames.any { window.contains(it) }) score += 16
        if (source.containsAny("订单详情", "感谢光顾", "门店已接单", "待取餐", "待取件")) score += 6
        if (categoryCode == CategoryCatalog.CODE_LIFE_PICKUP) score += 4
        if (categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY) score += 4
        return score
    }

    private fun pickupDomainScore(evidence: String, categoryCode: String?): Int {
        var score = 0
        if (evidence.containsAny("取餐", "待取餐", "餐品", "菜品", "奶茶", "咖啡", "饮品", "饮料", "门店已接单")) score += 28
        if (evidence.containsAny("外卖", "门店", "到店", "订单详情")) score += 12
        if (merchantNames.any { evidence.contains(it) }) score += 18
        if (evidence.containsAny("热", "冷", "少甜", "去冰", "拿铁", "果茶", "奶盖", "碎银子")) score += 10
        if (categoryCode == CategoryCatalog.CODE_LIFE_PICKUP) score += 6
        return score
    }

    private fun deliveryDomainScore(evidence: String, categoryCode: String?): Int {
        var score = 0
        if (evidence.containsAny("取件", "待取件", "快递", "包裹", "驿站", "菜鸟", "丰巢", "自提点", "快递柜")) score += 28
        if (evidence.containsAny("物流", "运单", "派送", "签收", "快递站")) score += 14
        if (courierNames.any { evidence.contains(it) }) score += 18
        if (categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY) score += 6
        return score
    }

    private fun codeTypeForLabel(label: String, categoryCode: String?): String {
        return when {
            label.contains("取餐") -> "meal"
            label.contains("取件") || label.contains("提货") || label.contains("取货") ||
                label.contains("自提") || label.contains("收货") -> "package"
            label.contains("核销") -> "redeem"
            label.contains("柜") || label.contains("货架") || label.contains("架位") -> "shelf"
            label.contains("尾号") -> "tail"
            label.contains("验证") -> "verification"
            categoryCode == CategoryCatalog.CODE_LIFE_PICKUP -> "meal"
            categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY -> "package"
            else -> "pickup"
        }
    }

    private fun codeTypeForContext(line: String, categoryCode: String?): String {
        return when {
            line.containsAny("取餐", "外卖", "餐", "门店", "奶茶", "咖啡", "饮品", "饮料") -> "meal"
            merchantNames.any { line.contains(it) } -> "meal"
            line.containsAny("取件", "快递", "包裹", "驿站", "丰巢", "菜鸟") -> "package"
            courierNames.any { line.contains(it) } -> "package"
            categoryCode == CategoryCatalog.CODE_LIFE_PICKUP -> "meal"
            categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY -> "package"
            else -> "pickup"
        }
    }

    private fun isEmbeddedInHyphenatedCode(line: String, range: IntRange): Boolean {
        val before = line.getOrNull(range.first - 1)
        val after = line.getOrNull(range.last + 1)
        return before == '-' || after == '-'
    }

    private fun String.isStrongCodeLabel(): Boolean {
        return contains("取件") || contains("取餐") || contains("提货") || contains("取货") ||
            contains("自提") || contains("核销") || contains("领取") || contains("收货")
    }

    private fun isValidPickupCode(code: String, evidence: String, hasExplicitPickupLabel: Boolean): Boolean {
        val normalized = code.trim()
        if (normalized.length !in 2..14) return false
        val digitsOnly = normalized.filter { it.isDigit() }
        if (digitsOnly.length == 11 && digitsOnly.startsWith("1")) return false
        if (normalized.matches(Regex("""20\d{2}[-/.]?\d{1,2}[-/.]?\d{1,2}"""))) return false
        if (normalized.matches(Regex("""\d{8,}"""))) return false
        if (!hasExplicitPickupLabel && evidence.containsAny("订单", "运单", "快递单号", "物流单号", "手机号", "电话")) {
            return false
        }
        return true
    }

    private fun sanitizeLocationValue(value: String): String? {
        var result = sanitizeShortText(value) ?: return null
        locationStopLabels.forEach { label ->
            val index = result.indexOf(label)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        statusWords.forEach { word ->
            val index = result.indexOf(word)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        result = result.trim('，', ',', '。', '.', '；', ';', '：', ':', ' ', '|')
        if (result.length !in 2..80) return null
        if (isStatusLike(result)) return null
        if (result.matches(Regex("""[A-Za-z0-9\-_]{2,14}"""))) return null
        return result
    }

    private fun sanitizeShortText(value: String?): String? {
        var result = normalizeVisibleText(value)
            .substringBefore('\n')
            .trim('，', ',', '。', '.', '；', ';', '：', ':', ' ', '|')
        val hardStops = listOf("订单号", "运单号", "快递单号", "物流单号", "应付", "实付", "金额", "状态")
        hardStops.forEach { stop ->
            val index = result.indexOf(stop)
            if (index > 0) {
                result = result.substring(0, index).trim()
            }
        }
        return result.takeIf { it.isNotBlank() }
    }

    private fun looksLikeLocation(value: String): Boolean {
        if (value.length < 2 || isStatusLike(value)) return false
        return locationTokens.any { value.contains(it, ignoreCase = true) }
    }

    private fun isStatusLike(value: String): Boolean {
        return statusWords.any { value.contains(it, ignoreCase = true) }
    }

    private fun isValidName(value: String): Boolean {
        if (value.length !in 2..40) return false
        if (isStatusLike(value) || looksLikeLocation(value) && !value.containsAny("店", "咖啡", "茶")) return false
        if (value.containsAny("订单", "单号", "金额", "地址", "地点", "电话")) return false
        return true
    }
}

object MemoryFactReconciler {
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
        val aiFacts = MemoryStructuredFactsJson.parse(aiStructuredFactsJson)
        val allowGeneratedTextFallback = userText.isNullOrBlank() && aiFacts?.rawVisibleText.isNullOrBlank()
        val localFacts = MemoryFactExtractor.extractLocalFacts(
            userText = userText,
            aiRawVisibleText = aiFacts?.rawVisibleText,
            memory = if (allowGeneratedTextFallback) memory else null,
            analysis = if (allowGeneratedTextFallback) analysis else null,
            summary = if (allowGeneratedTextFallback) summary else null,
            title = if (allowGeneratedTextFallback) title else null,
            categoryCode = categoryCode
        )
        return MemoryStructuredFactsJson.toJson(
            reconcile(
                aiFacts = aiFacts,
                localFacts = localFacts,
                supportText = listOf(
                    userText,
                    aiFacts?.rawVisibleText,
                    if (userText.isNullOrBlank()) memory else null,
                    if (userText.isNullOrBlank()) analysis else null,
                    if (userText.isNullOrBlank()) summary else null,
                    if (userText.isNullOrBlank()) title else null
                ).filterNotNull().joinToString("\n"),
                categoryCode = categoryCode
            )
        )
    }

    @JvmStatic
    fun stableSummary(categoryCode: String?, fallbackSummary: String?, structuredFactsJson: String?): String {
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson) ?: return fallbackSummary.orEmpty()
        val displayDomain = displayDomain(categoryCode, facts.domain)
        val code = facts.pickupCode?.trim().orEmpty()
        if (code.isBlank() || facts.pickupCodeConfidence < 0.55) {
            return fallbackSummary.orEmpty()
        }
        return when (displayDomain) {
            DOMAIN_DELIVERY -> {
                val target = firstNonBlank(facts.merchantOrCompany, facts.location)
                if (target == null) "取件码 $code" else "取件码 $code｜$target"
            }
            DOMAIN_PICKUP -> {
                val target = firstNonBlank(facts.merchantOrCompany, facts.itemName, facts.location)
                if (target == null) "取餐码 $code" else "取餐码 $code｜$target"
            }
            else -> fallbackSummary.orEmpty()
        }
    }

    @JvmStatic
    fun normalizeCategoryCode(categoryCode: String?, structuredFactsJson: String?): String {
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson)
        val domain = facts?.domain
        val hasReliableCode = (facts?.pickupCodeConfidence ?: 0.0) >= 0.55 && !facts?.pickupCode.isNullOrBlank()
        val hasReliablePickupContext = !facts?.merchantOrCompany.isNullOrBlank() || !facts?.itemName.isNullOrBlank()
        val hasReliableDeliveryContext = (facts?.locationConfidence ?: 0.0) >= 0.48 || !facts?.location.isNullOrBlank()
        return when {
            domain == DOMAIN_PICKUP && (hasReliableCode || hasReliablePickupContext) ->
                CategoryCatalog.CODE_LIFE_PICKUP
            domain == DOMAIN_DELIVERY && (hasReliableCode || hasReliableDeliveryContext) ->
                CategoryCatalog.CODE_LIFE_DELIVERY
            else -> categoryCode ?: CategoryCatalog.CODE_QUICK_NOTE
        }
    }

    fun structuredPickupInfo(categoryCode: String?, structuredFactsJson: String?): StructuredPickupInfo? {
        val facts = MemoryStructuredFactsJson.parse(structuredFactsJson) ?: return null
        val displayDomain = displayDomain(categoryCode, facts.domain)
        if (displayDomain != DOMAIN_PICKUP && displayDomain != DOMAIN_DELIVERY) return null
        val code = facts.pickupCode?.trim()?.takeIf {
            it.isNotEmpty() && facts.pickupCodeConfidence >= 0.55
        } ?: return null
        return if (displayDomain == DOMAIN_DELIVERY) {
            StructuredPickupInfo(
                sectionTitle = "取件码",
                code = code,
                primaryLabel = "快递公司",
                primaryValue = fallbackStructuredValue(facts.merchantOrCompany),
                secondaryLabel = "取件地址",
                secondaryValue = fallbackStructuredValue(facts.location),
                locationText = facts.location?.trim()?.takeIf { it.isNotEmpty() }
            )
        } else {
            StructuredPickupInfo(
                sectionTitle = "取餐码",
                code = code,
                primaryLabel = "店铺",
                primaryValue = fallbackStructuredValue(facts.merchantOrCompany),
                secondaryLabel = "商品",
                secondaryValue = fallbackStructuredValue(facts.itemName),
                locationText = facts.location?.trim()?.takeIf { it.isNotEmpty() }
            )
        }
    }

    private fun reconcile(
        aiFacts: MemoryStructuredFacts?,
        localFacts: MemoryStructuredFacts,
        supportText: String,
        categoryCode: String?
    ): MemoryStructuredFacts {
        val domain = displayDomain(
            categoryCode,
            aiFacts?.domain?.takeUnless { it == DOMAIN_NOTE } ?: localFacts.domain
        )
        val pickupCode = chooseCode(aiFacts, localFacts, supportText)
        val location = chooseValue(
            aiValue = aiFacts?.location,
            aiConfidence = aiFacts?.locationConfidence ?: 0.0,
            localValue = localFacts.location,
            localConfidence = localFacts.locationConfidence,
            supportText = supportText,
            minAiConfidence = 0.62,
            minLocalConfidence = 0.48
        )
        return MemoryStructuredFacts(
            domain = domain,
            pickupCode = pickupCode?.first,
            pickupCodeType = pickupCode?.second ?: localFacts.pickupCodeType ?: aiFacts?.pickupCodeType,
            pickupCodeConfidence = pickupCode?.third ?: 0.0,
            pickupCodeEvidence = if (pickupCode?.first == aiFacts?.pickupCode) aiFacts?.pickupCodeEvidence else localFacts.pickupCodeEvidence,
            location = location?.first,
            locationConfidence = location?.second ?: 0.0,
            locationEvidence = if (location?.first == aiFacts?.location) aiFacts?.locationEvidence else localFacts.locationEvidence,
            merchantOrCompany = choosePlainFact(aiFacts?.merchantOrCompany, localFacts.merchantOrCompany, supportText),
            itemName = choosePlainFact(aiFacts?.itemName, localFacts.itemName, supportText),
            orderNumber = choosePlainFact(aiFacts?.orderNumber, localFacts.orderNumber, supportText),
            trackingNumber = choosePlainFact(aiFacts?.trackingNumber, localFacts.trackingNumber, supportText),
            amount = choosePlainFact(aiFacts?.amount, localFacts.amount, supportText),
            timeWindow = choosePlainFact(aiFacts?.timeWindow, localFacts.timeWindow, supportText),
            rawVisibleText = firstNonBlank(aiFacts?.rawVisibleText, localFacts.rawVisibleText)
        )
    }

    private fun chooseCode(
        aiFacts: MemoryStructuredFacts?,
        localFacts: MemoryStructuredFacts,
        supportText: String
    ): Triple<String, String?, Double>? {
        val aiCode = sanitizeCodeValue(aiFacts?.pickupCode)
        val localCode = sanitizeCodeValue(localFacts.pickupCode)
        val aiConfidence = aiFacts?.pickupCodeConfidence ?: 0.0
        if (
            aiCode != null &&
            aiConfidence >= 0.72 &&
            isSupportedBySource(aiCode, supportText)
        ) {
            return Triple(aiCode, aiFacts?.pickupCodeType, aiConfidence.coerceIn(0.0, 1.0))
        }
        if (localCode != null && localFacts.pickupCodeConfidence >= 0.55) {
            return Triple(localCode, localFacts.pickupCodeType, localFacts.pickupCodeConfidence.coerceIn(0.0, 1.0))
        }
        return null
    }

    private fun chooseValue(
        aiValue: String?,
        aiConfidence: Double,
        localValue: String?,
        localConfidence: Double,
        supportText: String,
        minAiConfidence: Double,
        minLocalConfidence: Double
    ): Pair<String, Double>? {
        val cleanedAi = sanitizeFactValue(aiValue)
        if (
            cleanedAi != null &&
            aiConfidence >= minAiConfidence &&
            isSupportedBySource(cleanedAi, supportText)
        ) {
            return cleanedAi to aiConfidence.coerceIn(0.0, 1.0)
        }
        val cleanedLocal = sanitizeFactValue(localValue)
        if (cleanedLocal != null && localConfidence >= minLocalConfidence) {
            return cleanedLocal to localConfidence.coerceIn(0.0, 1.0)
        }
        return null
    }

    private fun choosePlainFact(aiValue: String?, localValue: String?, supportText: String): String? {
        val cleanedAi = sanitizeFactValue(aiValue) ?: sanitizeCodeValue(aiValue)
        if (cleanedAi != null && isSupportedBySource(cleanedAi, supportText)) {
            return cleanedAi
        }
        return sanitizeFactValue(localValue) ?: sanitizeCodeValue(localValue)
    }

    private fun displayDomain(categoryCode: String?, domain: String?): String {
        return when {
            domain == DOMAIN_PICKUP || domain == DOMAIN_DELIVERY -> domain
            categoryCode == CategoryCatalog.CODE_LIFE_PICKUP -> DOMAIN_PICKUP
            categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY -> DOMAIN_DELIVERY
            else -> DOMAIN_NOTE
        }
    }

    private fun isSupportedBySource(value: String, supportText: String): Boolean {
        val normalizedValue = normalizeForSupport(value)
        if (normalizedValue.isBlank()) return false
        val normalizedSource = normalizeForSupport(supportText)
        if (normalizedSource.isBlank()) return false
        return normalizedSource.contains(normalizedValue)
    }

    private fun fallbackStructuredValue(value: String?): String {
        return value?.trim()?.takeIf { it.isNotEmpty() } ?: "未识别"
    }
}

private fun JSONObject.optCleanString(name: String): String? {
    val value = opt(name) ?: return null
    if (value == JSONObject.NULL) return null
    return value.toString()
        .trim()
        .takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
}

private fun JSONObject.optConfidence(name: String): Double {
    return when (val value = opt(name)) {
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }.coerceIn(0.0, 1.0)
}

private fun JSONObject.putNullable(name: String, value: String?) {
    put(name, value?.takeIf { it.isNotBlank() } ?: JSONObject.NULL)
}

private fun Int.toConfidence(): Double {
    return (this / 100.0).coerceIn(0.0, 1.0)
}

private fun normalizeVisibleText(value: String?): String {
    if (value.isNullOrBlank()) return ""
    val builder = StringBuilder(value.length)
    value.forEach { char ->
        val code = char.code
        val normalized = when {
            code == 0x3000 -> ' '
            code in 0xFF01..0xFF5E -> (code - 0xFEE0).toChar()
            char == '：' -> ':'
            char == '，' -> ','
            char == '；' -> ';'
            char == '（' -> '('
            char == '）' -> ')'
            else -> char
        }
        builder.append(normalized)
    }
    return builder.toString()
        .replace(Regex("""[ \t]+"""), " ")
        .trim()
}

private fun sanitizeFactValue(value: String?): String? {
    val cleaned = normalizeVisibleText(value)
        .trim(' ', ',', '，', '.', '。', ';', '；', ':', '：', '|')
    if (cleaned.isBlank()) return null
    if (cleaned.equals("未识别", ignoreCase = true) || cleaned.equals("无", ignoreCase = true)) return null
    return cleaned
}

private fun sanitizeCodeValue(value: String?): String? {
    val cleaned = normalizeVisibleText(value)
        .uppercase(Locale.ROOT)
        .trim(' ', ',', '，', '.', '。', ';', '；', ':', '：', '|')
    if (cleaned.isBlank()) return null
    if (!cleaned.matches(Regex("""[A-Z0-9\-_]{2,32}"""))) return null
    return cleaned
}

private fun normalizeForSupport(value: String?): String {
    return normalizeVisibleText(value)
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
