package com.han.nomemo

import java.util.Locale

data class GroupAutoMatchResult(
    val record: MemoryRecord,
    val score: Int,
    val reasons: List<String>
)

object GroupAutoClassifier {
    private data class QueryProfile(
        val normalizedName: String,
        val phrases: List<String>,
        val terms: List<String>
    )

    private data class SearchField(
        val label: String,
        val text: String,
        val phraseWeight: Int,
        val termWeight: Int
    )

    private val splitRegex = Regex("""[\s,，。；;、｜|:：/\\\-\n\r\t()（）\[\]【】<>《》"'“”‘’]+""")
    private val latinTokenRegex = Regex("""[a-z0-9]{2,20}""")
    private val hanTokenRegex = Regex("""[\u4e00-\u9fff]{2,20}""")
    private val boilerplateTerms = listOf(
        "自动归类",
        "自动",
        "归类",
        "分组",
        "记忆",
        "内容",
        "事项",
        "描述",
        "匹配",
        "相关",
        "有关",
        "关于",
        "用于",
        "进行",
        "包含",
        "包括",
        "例如",
        "比如",
        "全部",
        "所有",
        "一起",
        "按照",
        "这个",
        "那个"
    )

    @JvmStatic
    fun classify(
        albumName: String,
        albumDescription: String,
        records: List<MemoryRecord>,
        existingRecordIds: Set<String>
    ): List<GroupAutoMatchResult> {
        val description = albumDescription.trim()
        if (description.isBlank()) {
            return emptyList()
        }
        val queryProfile = buildQueryProfile(albumName, description)
        if (queryProfile.phrases.isEmpty() && queryProfile.terms.isEmpty()) {
            return emptyList()
        }
        return records.asSequence()
            .filterNot { existingRecordIds.contains(it.recordId) }
            .mapNotNull { scoreRecord(it, queryProfile) }
            .sortedWith(
                compareByDescending<GroupAutoMatchResult> { it.score }
                    .thenByDescending { it.record.createdAt }
            )
            .toList()
    }

    @JvmStatic
    fun searchableText(record: MemoryRecord): String {
        return buildSearchFields(record).joinToString("\n") { it.text }
    }

    private fun scoreRecord(
        record: MemoryRecord,
        profile: QueryProfile
    ): GroupAutoMatchResult? {
        val fields = buildSearchFields(record)
        if (fields.isEmpty()) {
            return null
        }
        val reasons = linkedSetOf<String>()
        val matchedFields = linkedSetOf<String>()
        var score = 0
        var phraseHits = 0
        var termHits = 0

        fields.forEach { field ->
            profile.phrases.forEach { phrase ->
                if (field.text.contains(phrase)) {
                    score += field.phraseWeight
                    phraseHits += 1
                    matchedFields += field.label
                    if (reasons.size < 6) {
                        reasons += "${field.label}:$phrase"
                    }
                }
            }
            profile.terms.forEach { term ->
                if (field.text.contains(term)) {
                    score += field.termWeight
                    termHits += 1
                    matchedFields += field.label
                    if (reasons.size < 6) {
                        reasons += "${field.label}:$term"
                    }
                }
            }
        }

        val normalizedName = profile.normalizedName
        if (normalizedName.isNotBlank()) {
            val titleField = fields.firstOrNull { it.label == "title" }
            val structuredField = fields.firstOrNull { it.label == "structured" }
            if (titleField?.text?.contains(normalizedName) == true) {
                score += 12
                matchedFields += "title"
            }
            if (structuredField?.text?.contains(normalizedName) == true) {
                score += 10
                matchedFields += "structured"
            }
        }

        if (matchedFields.size >= 2) {
            score += 8
        }
        if (matchedFields.contains("title") && matchedFields.contains("structured")) {
            score += 10
        }
        if (matchedFields.contains("title") && matchedFields.contains("category")) {
            score += 8
        }
        if (phraseHits == 0 && termHits < 2) {
            return null
        }
        if (score < 28) {
            return null
        }
        return GroupAutoMatchResult(
            record = record,
            score = score,
            reasons = reasons.toList()
        )
    }

    private fun buildQueryProfile(albumName: String, albumDescription: String): QueryProfile {
        val normalizedName = normalizeText(albumName)
        val phraseCandidates = linkedSetOf<String>()
        listOf(albumName, albumDescription)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .forEach { raw ->
                val normalized = normalizeText(raw)
                if (normalized.length in 2..32) {
                    phraseCandidates += normalized
                }
                splitRegex.split(normalized)
                    .map { cleanConceptFragment(it) }
                    .filter { it.length >= 2 }
                    .forEach { fragment ->
                        phraseCandidates += fragment
                    }
            }

        val terms = linkedSetOf<String>()
        phraseCandidates.forEach { phrase ->
            extractTerms(phrase).forEach { terms += it }
        }

        return QueryProfile(
            normalizedName = normalizedName,
            phrases = phraseCandidates
                .filter { it.length >= 2 }
                .sortedByDescending { it.length },
            terms = terms
                .filter { it.length >= 2 }
                .sortedByDescending { it.length }
        )
    }

    private fun buildSearchFields(record: MemoryRecord): List<SearchField> {
        val facts = MemoryStructuredFactsJson.parse(record.structuredFactsJson)
        val structuredText = listOf(
            facts?.merchantOrCompany,
            facts?.itemName,
            facts?.location,
            facts?.pickupCode,
            facts?.orderNumber
        ).joinToString("\n") { normalizeText(it) }.trim()

        val fields = listOfNotNull(
            searchField("title", record.title, phraseWeight = 24, termWeight = 12),
            searchField("category", record.categoryName, phraseWeight = 18, termWeight = 9),
            searchField("structured", structuredText, phraseWeight = 18, termWeight = 10),
            searchField("summary", record.summary, phraseWeight = 12, termWeight = 6),
            searchField("memory", record.memory, phraseWeight = 12, termWeight = 6),
            searchField("analysis", record.analysis, phraseWeight = 12, termWeight = 6),
            searchField("source", record.sourceText, phraseWeight = 8, termWeight = 4)
        )
        return fields.filter { it.text.isNotBlank() }
    }

    private fun searchField(
        label: String,
        raw: String?,
        phraseWeight: Int,
        termWeight: Int
    ): SearchField? {
        val text = normalizeText(raw)
        if (text.isBlank()) {
            return null
        }
        return SearchField(label, text, phraseWeight, termWeight)
    }

    private fun extractTerms(phrase: String): List<String> {
        val scrubbed = boilerplateTerms.fold(phrase) { acc, token ->
            acc.replace(token, " ")
        }
        val terms = linkedSetOf<String>()
        latinTokenRegex.findAll(scrubbed).forEach { match ->
            val value = match.value.trim()
            if (value.length >= 2) {
                terms += value
            }
        }
        hanTokenRegex.findAll(scrubbed).forEach { match ->
            val value = match.value.trim()
            if (value.length < 2) {
                return@forEach
            }
            terms += value
            if (value.length in 4..12) {
                for (size in 2..4) {
                    for (index in 0..value.length - size) {
                        val gram = value.substring(index, index + size)
                        if (!boilerplateTerms.contains(gram)) {
                            terms += gram
                        }
                    }
                }
            }
        }
        return terms.toList()
    }

    private fun cleanConceptFragment(raw: String): String {
        var value = raw.trim()
        if (value.isEmpty()) {
            return ""
        }
        boilerplateTerms.forEach { token ->
            value = value.replace(token, "")
        }
        return normalizeText(value)
    }

    private fun normalizeText(raw: String?): String {
        if (raw.isNullOrBlank()) {
            return ""
        }
        val builder = StringBuilder(raw.length)
        raw.lowercase(Locale.ROOT).forEach { ch ->
            builder.append(ch.toHalfWidth())
        }
        return builder.toString()
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun Char.toHalfWidth(): Char {
        return when (this) {
            '\u3000' -> ' '
            in '\uFF01'..'\uFF5E' -> (code - 0xFEE0).toChar()
            else -> this
        }
    }
}
