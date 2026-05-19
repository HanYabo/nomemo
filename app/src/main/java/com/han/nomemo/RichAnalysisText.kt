package com.han.nomemo

private val richAnalysisHeadingRegex = Regex("^[\\p{So}\\u2600-\\u27BF]\\s*\\S.{0,18}$")

data class RichAnalysisBlock(
    val heading: String,
    val body: String
)

data class RichAnalysisContent(
    val overview: String,
    val blocks: List<RichAnalysisBlock>
)

fun parseRichAnalysisContent(text: String): RichAnalysisContent? {
    val normalized = text
        .replace("\r\n", "\n")
        .replace('\r', '\n')
        .trim()
    if (normalized.isBlank()) return null

    val sections = normalized
        .split(Regex("\n\\s*\n"))
        .map { it.trim() }
        .filter { it.isNotBlank() }

    if (sections.size < 3) return null

    val overview = sections.first()
    val blocks = sections.drop(1).mapNotNull { section ->
        val lines = section
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
        if (lines.size < 2) return@mapNotNull null
        val heading = lines.first()
        if (!richAnalysisHeadingRegex.matches(heading)) return@mapNotNull null
        val body = lines.drop(1).joinToString("\n").trim()
        if (body.isBlank()) return@mapNotNull null
        RichAnalysisBlock(heading = heading, body = body)
    }

    if (blocks.size < 2) return null
    return RichAnalysisContent(
        overview = overview,
        blocks = blocks.take(4)
    )
}
