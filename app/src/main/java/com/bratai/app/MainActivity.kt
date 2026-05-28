package com.bratai.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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
            messages.add(ChatMessage("Здравствуй, брат 💚 Я Брат ИИ. Теперь у меня есть память фактов. Напиши: запомни: ...", false))
        }

        buildUi()
        renderMessages()
    }

    private fun buildUi() {
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(28, 42, 28, 28)
        root.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.rgb(2, 4, 3),
                Color.rgb(5, 18, 12),
                Color.rgb(0, 32, 18)
            )
        )

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL

        val logo = TextView(this)
        logo.text = "Б"
        logo.textSize = 28f
        logo.gravity = Gravity.CENTER
        logo.setTextColor(Color.BLACK)
        logo.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        logo.background = roundedBg(Color.rgb(0, 255, 136), Color.WHITE, 60)

        header.addView(logo, LinearLayout.LayoutParams(82, 82))

        val titleBox = LinearLayout(this)
        titleBox.orientation = LinearLayout.VERTICAL
        titleBox.setPadding(18, 0, 0, 0)

        val title = TextView(this)
        title.text = "Брат ИИ"
        title.textSize = 30f
        title.setTextColor(Color.WHITE)
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        titleBox.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "мозг • память фактов • развитие"
        subtitle.textSize = 15f
        subtitle.setTextColor(Color.rgb(0, 255, 136))
        titleBox.addView(subtitle)

        header.addView(titleBox)
        root.addView(header)

        val status = TextView(this)
        status.text = "● локальный режим активен • v1.3"
        status.textSize = 14f
        status.setTextColor(Color.rgb(130, 210, 170))
        status.setPadding(0, 16, 0, 16)
        root.addView(status)

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
        bottom.gravity = Gravity.CENTER_VERTICAL
        bottom.setPadding(0, 14, 0, 0)

        input = EditText(this)
        input.hint = "Напиши брату..."
        input.setTextColor(Color.WHITE)
        input.setHintTextColor(Color.rgb(130, 165, 145))
        input.textSize = 16f
        input.minLines = 1
        input.maxLines = 3
        input.background = roundedBg(Color.rgb(6, 19, 13), Color.rgb(30, 92, 63), 24)
        input.setPadding(18, 10, 18, 10)

        bottom.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val sendButton = Button(this)
        sendButton.text = "➤"
        sendButton.textSize = 20f
        sendButton.setTextColor(Color.BLACK)
        sendButton.background = roundedBg(Color.rgb(0, 220, 120), Color.rgb(0, 255, 136), 24)
        sendButton.setOnClickListener {
            sendMessage()
        }

        val sendParams = LinearLayout.LayoutParams(110, 92)
        sendParams.setMargins(10, 0, 0, 0)
        bottom.addView(sendButton, sendParams)

        root.addView(bottom)

        val tools = LinearLayout(this)
        tools.orientation = LinearLayout.HORIZONTAL
        tools.gravity = Gravity.CENTER

        val clearChatButton = Button(this)
        clearChatButton.text = "Очистить чат"
        clearChatButton.textSize = 12f
        clearChatButton.setTextColor(Color.WHITE)
        clearChatButton.background = roundedBg(Color.rgb(20, 45, 32), Color.rgb(35, 110, 75), 22)
        clearChatButton.setOnClickListener {
            messages.clear()
            messages.add(ChatMessage("Чат очищен, брат. Память фактов я оставил 💚", false))
            memoryStore.saveMessages(messages)
            renderMessages()
        }

        val clearFactsButton = Button(this)
        clearFactsButton.text = "Очистить факты"
        clearFactsButton.textSize = 12f
        clearFactsButton.setTextColor(Color.WHITE)
        clearFactsButton.background = roundedBg(Color.rgb(55, 28, 28), Color.rgb(140, 60, 60), 22)
        clearFactsButton.setOnClickListener {
            memoryStore.clearFacts()
            messages.add(ChatMessage("Память фактов очищена, брат.", false))
            memoryStore.saveMessages(messages)
            renderMessages()
        }

        val toolParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            76
        )
        toolParams.setMargins(4, 10, 4, 0)

        tools.addView(clearChatButton, toolParams)
        tools.addView(clearFactsButton, toolParams)

        root.addView(tools)

        setContentView(root)
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return

        messages.add(ChatMessage(text, true))

        val reply = handleMemoryCommand(text)
            ?: brain.answer(text, memoryStore.loadFacts())

        messages.add(ChatMessage(reply, false))

        input.setText("")
        memoryStore.saveMessages(messages)
        renderMessages()
    }

    private fun handleMemoryCommand(text: String): String? {
        val lower = text.lowercase().trim()

        if (lower.startsWith("запомни:") || lower.startsWith("запомни ")) {
            val fact = text
                .replaceFirst("запомни:", "", ignoreCase = true)
                .replaceFirst("запомни", "", ignoreCase = true)
                .trim()

            if (fact.isBlank()) {
                return "Брат, напиши так: запомни: меня зовут Николай"
            }

            memoryStore.addFact(fact)
            return "Запомнил, брат 💚\nФакт: $fact"
        }

        if (
            lower.contains("что ты помнишь") ||
            lower.contains("что помнишь") ||
            lower.contains("покажи память") ||
            lower.contains("память фактов")
        ) {
            val facts = memoryStore.loadFacts()

            return if (facts.isEmpty()) {
                "Пока в памяти фактов пусто, брат. Напиши: запомни: ..."
            } else {
                "Вот что я помню, брат:\n\n" + facts.mapIndexed { index, fact ->
                    "${index + 1}. $fact"
                }.joinToString("\n")
            }
        }

        if (lower.contains("очисти память фактов") || lower.contains("забудь факты")) {
            memoryStore.clearFacts()
            return "Готово, брат. Память фактов очищена."
        }

        return null
    }

    private fun renderMessages() {
        chatBox.removeAllViews()

        for (message in messages.takeLast(80)) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = if (message.fromUser) Gravity.END else Gravity.START
            row.setPadding(0, 8, 0, 8)

            val bubble = TextView(this)
            bubble.text = message.text
            bubble.textSize = 16f
            bubble.setPadding(22, 16, 22, 16)
            bubble.setTextColor(if (message.fromUser) Color.BLACK else Color.WHITE)
            bubble.background = if (message.fromUser) {
                roundedBg(Color.rgb(0, 210, 115), Color.rgb(170, 255, 210), 28)
            } else {
                roundedBg(Color.rgb(10, 36, 24), Color.rgb(28, 112, 74), 28)
            }

            val params = LinearLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.78).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            row.addView(bubble, params)
            chatBox.addView(row)
        }

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun roundedBg(fill: Int, stroke: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fill)
            cornerRadius = radius.toFloat()
            setStroke(2, stroke)
        }
    }
}
