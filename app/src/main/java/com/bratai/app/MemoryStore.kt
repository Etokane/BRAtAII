package com.bratai.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class MemoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("brat_ai_memory", Context.MODE_PRIVATE)

    fun saveMessages(messages: List<ChatMessage>) {
        val array = JSONArray()

        messages.takeLast(100).forEach { message ->
            val obj = JSONObject()
            obj.put("text", message.text)
            obj.put("fromUser", message.fromUser)
            array.put(obj)
        }

        prefs.edit().putString("messages", array.toString()).apply()
    }

    fun loadMessages(): List<ChatMessage> {
        val raw = prefs.getString("messages", null) ?: return emptyList()

        return try {
            val array = JSONArray(raw)
            val result = mutableListOf<ChatMessage>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    ChatMessage(
                        text = obj.getString("text"),
                        fromUser = obj.getBoolean("fromUser")
                    )
                )
            }

            result
        } catch (_: Exception) {
            emptyList()
        }
    }
}
