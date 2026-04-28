package com.han.nomemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import java.io.InputStream
import java.util.LinkedHashSet
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class MemoryImageOcrResult(
    val fullText: String,
    val topRoiText: String,
    val mergedText: String
)

object MemoryImageOcrService {
    private const val TAG = "MemoryImageOcrService"
    private const val OCR_TIMEOUT_SECONDS = 8L
    private const val OCR_MAX_EDGE = 2200
    private const val TOP_ROI_RATIO = 0.34f
    private const val TOP_ROI_MIN_HEIGHT = 420
    private const val MAX_MERGED_TEXT_LENGTH = 1800

    @JvmStatic
    fun extractVisibleText(context: Context, imageUri: Uri): MemoryImageOcrResult? {
        val sourceBitmap = readBitmap(context, imageUri) ?: return null
        val scaledBitmap = scaleForOcr(sourceBitmap)
        val topBitmap = cropTopRegion(scaledBitmap)
        val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        try {
            val fullText = recognize(recognizer, scaledBitmap)
            val topText = if (topBitmap === scaledBitmap) fullText else recognize(recognizer, topBitmap)
            val mergedText = mergePrioritizedText(topText, fullText)
            if (mergedText.isBlank()) {
                return null
            }
            return MemoryImageOcrResult(
                fullText = fullText,
                topRoiText = topText,
                mergedText = mergedText
            )
        } catch (error: Exception) {
            Log.w(TAG, "Local OCR failed", error)
            return null
        } finally {
            recognizer.close()
            if (topBitmap !== scaledBitmap) {
                topBitmap.recycle()
            }
            if (scaledBitmap !== sourceBitmap) {
                scaledBitmap.recycle()
            }
            sourceBitmap.recycle()
        }
    }

    private fun readBitmap(context: Context, imageUri: Uri): Bitmap? {
        val resolver = context.contentResolver
        val inputStream: InputStream = resolver.openInputStream(imageUri) ?: return null
        return inputStream.use { BitmapFactory.decodeStream(it) }
    }

    private fun scaleForOcr(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val longest = maxOf(width, height)
        if (longest <= OCR_MAX_EDGE) {
            return source
        }
        val ratio = OCR_MAX_EDGE.toFloat() / longest.toFloat()
        val scaledWidth = maxOf(1, (width * ratio).roundToInt())
        val scaledHeight = maxOf(1, (height * ratio).roundToInt())
        return Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)
    }

    private fun cropTopRegion(bitmap: Bitmap): Bitmap {
        val roiHeight = maxOf(TOP_ROI_MIN_HEIGHT, (bitmap.height * TOP_ROI_RATIO).roundToInt())
            .coerceAtMost(bitmap.height)
        if (roiHeight >= bitmap.height) {
            return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, roiHeight)
    }

    private fun recognize(
        recognizer: com.google.mlkit.vision.text.TextRecognizer,
        bitmap: Bitmap
    ): String {
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = Tasks.await(
            recognizer.process(image),
            OCR_TIMEOUT_SECONDS,
            TimeUnit.SECONDS
        )
        return normalizeRecognizedText(result.text)
    }

    private fun mergePrioritizedText(topText: String, fullText: String): String {
        val lines = LinkedHashSet<String>()
        appendLines(lines, topText)
        appendLines(lines, fullText)
        if (lines.isEmpty()) {
            return ""
        }
        val builder = StringBuilder()
        for (line in lines) {
            if (builder.isNotEmpty()) {
                builder.append('\n')
            }
            builder.append(line)
            if (builder.length >= MAX_MERGED_TEXT_LENGTH) {
                return builder.substring(0, MAX_MERGED_TEXT_LENGTH).trim()
            }
        }
        return builder.toString().trim()
    }

    private fun appendLines(target: LinkedHashSet<String>, rawText: String) {
        rawText.lines()
            .map { normalizeRecognizedLine(it) }
            .filter { it.isNotBlank() }
            .forEach(target::add)
    }

    private fun normalizeRecognizedText(value: String): String {
        return value.lines()
            .map { normalizeRecognizedLine(it) }
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }

    private fun normalizeRecognizedLine(value: String): String {
        val normalized = value
            .replace('\u3000', ' ')
            .replace(Regex("""[ \t]+"""), " ")
            .trim()
        return collapseSpacedShortCodes(normalized)
    }

    private fun collapseSpacedShortCodes(value: String): String {
        return value.replace(
            Regex("""(?<![A-Za-z0-9])([A-Za-z]?\s*(?:\d\s*){4,6})(?![A-Za-z0-9])""")
        ) { match ->
            match.groupValues[1].replace(Regex("""\s+"""), "")
        }
    }
}
