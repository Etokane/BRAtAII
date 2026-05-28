package com.bratai.app

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)

class MainActivity : Activity() {

    private val brain = BratBrain()
    private lateinit var memoryStore: MemoryStore
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var chatBox: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        memoryStore = MemoryStore(this)
        messages.addAll(memoryStore.loadMessages())

        if (messages.isEmpty()) {
            messages.add(ChatMessage("Здравствуй, брат 💚 Я Брат ИИ.", false))
        }

        buildUi()
        renderMessages()
    }

    private fun buildUi() {
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(30, 50, 30, 30)
        root.setBackgroundColor(Color.rgb(2, 20, 12))

        val title = TextView(this)
        title.text = "🟢 Брат ИИ"
        title.textSize = 30f
        title.setTextColor(Color.WHITE)
        root.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "мозг • память • развитие"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.rgb(0, 255, 136))
        root.addView(subtitle)

        scrollView = ScrollView(this)
        chatBox = LinearLayout(this)
        chatBox.orientation = LinearLayout.VERTICAL
        scrollView.addView(chatBox)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val bottom = LinearLayout(this)
        bottom.orientation = LinearLayout.HORIZONTAL

        input = EditText(this)
        input.hint = "Напиши брату..."
        input.setTextColor(Color.WHITE)
        input.setHintTextColor(Color.GRAY)

        bottom.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val button = Button(this)
        button.text = "➤"
        button.setOnClickListener {
            sendMessage()
        }

        bottom.addView(button)
        root.addView(bottom)

        setContentView(root)
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return

        messages.add(ChatMessage(text, true))
        messages.add(ChatMessage(brain.answer(text), false))

        input.setText("")
        memoryStore.saveMessages(messages)
        renderMessages()
    }

    private fun renderMessages() {
        chatBox.removeAllViews()

        for (message in messages) {
            val bubble = TextView(this)
            bubble.text = message.text
            bubble.textSize = 17f
            bubble.setPadding(18, 14, 18, 14)
            bubble.setTextColor(if (message.fromUser) Color.BLACK else Color.WHITE)
            bubble.setBackgroundColor(
                if (message.fromUser) Color.rgb(0, 200, 111)
                else Color.rgb(10, 50, 30)
            )

            val row = LinearLayout(this)
            row.gravity = if (message.fromUser) Gravity.END else Gravity.START
            row.setPadding(0, 10, 0, 10)
            row.addView(bubble)

            chatBox.addView(row)
        }

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}
