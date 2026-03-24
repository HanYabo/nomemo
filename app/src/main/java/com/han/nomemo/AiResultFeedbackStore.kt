package com.han.nomemo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class AiResultPreview(
    val recordId: String,
    val title: String,
    val summary: String,
    val memory: String,
    val analysis: String,
    val engine: String
)

class AiResultFeedbackStore(context: Context) {
    companion object {
        private const val PREF_NAME = "no_memo_ai_feedback"
        private const val KEY_QUEUE = "result_queue"
    }

    private val preferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun enqueue(record: MemoryRecord) {
        val queue = loadQueue()
        queue.put(
            JSONObject().apply {
                put("recordId", record.recordId)
                put("title", record.title.orEmpty())
                put("summary", record.summary.orEmpty())
                put("memory", record.memory.orEmpty())
                put("analysis", record.analysis.orEmpty())
                put("engine", record.engine.orEmpty())
            }
        )
        preferences.edit().putString(KEY_QUEUE, queue.toString()).apply()
    }

    fun consumeNext(): AiResultPreview? {
        val queue = loadQueue()
        if (queue.length() <= 0) {
            return null
        }
        val first = queue.optJSONObject(0) ?: return null
        val remaining = JSONArray()
        for (index in 1 until queue.length()) {
            remaining.put(queue.opt(index))
        }
        preferences.edit().putString(KEY_QUEUE, remaining.toString()).apply()
        return AiResultPreview(
            recordId = first.optString("recordId"),
            title = first.optString("title"),
            summary = first.optString("summary"),
            memory = first.optString("memory"),
            analysis = first.optString("analysis"),
            engine = first.optString("engine")
        )
    }

    private fun loadQueue(): JSONArray {
        val raw = preferences.getString(KEY_QUEUE, "[]").orEmpty()
        return try {
            JSONArray(raw)
        } catch (_: Exception) {
            JSONArray()
        }
    }
}

