package com.bratai.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
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

    private lateinit var memoryStore: MemoryStore
    private val brain = BratBrain()
    private val messages = mutableListOf<ChatMessage>()

    private lateinit var chatBox: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        memoryStore = MemoryStore(this)
        messages.addAll(memoryStore.loadMessages())

        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    "Здравствуй, брат 💚 Я Брат ИИ. Я уже живу в твоём приложении.",
                    false
                )
            )
        }

        buildUi()
        renderMessages()
    }

    private fun buildUi() {
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(28, 40, 28, 28)
        root.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.rgb(2, 4, 3),
                Color.rgb(7, 20, 15),
                Color.rgb(0, 31, 18)
            )
        )

        val header = TextView(this)
        header.text = "🟢 Брат ИИ"
        header.textSize = 30f
        header.setTextColor(Color.WHITE)
        header.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        root.addView(header)

        val subtitle = TextView(this)
        subtitle.text = "мозг • память • развитие"
        subtitle.textSize = 16f
        subtitle.setTextColor(Color.rgb(0, 255, 136))
        subtitle.setPadding(0, 4, 0, 24)
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
        bottom.gravity = Gravity.CENTER_VERTICAL
        bottom.setPadding(0, 16, 0, 0)

        input = EditText(this)
        input.hint = "Напиши брату..."
        input.setTextColor(Color.WHITE)
        input.setHintTextColor(Color.rgb(126, 168, 146))
        input.setSingleLine(false)
        input.minLines = 1
        input.maxLines = 3
        input.background = roundedBg(Color.rgb(6, 19, 13), Color.rgb(30, 92, 63), 24)

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
        button.textSize = 22f
        button.setTextColor(Color.BLACK)
        button.background = roundedBg(Color.rgb(0, 200, 111), Color.rgb(0, 200, 111), 24)
        button.setOnClickListener {
            sendMessage()
        }

        val buttonParams = LinearLayout.LayoutParams(120, 100)
        buttonParams.setMargins(12, 0, 0, 0)
        bottom.addView(button, buttonParams)

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
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = if (message.fromUser) Gravity.END else Gravity.START
            row.setPadding(0, 8, 0, 8)

            val bubble = TextView(this)
            bubble.text = message.text
            bubble.textSize = 17f
            bubble.setPadding(24, 18, 24, 18)
            bubble.setTextColor(if (message.fromUser) Color.BLACK else Color.WHITE)

            bubble.background = if (message.fromUser) {
                roundedBg(Color.rgb(0, 200, 111), Color.rgb(183, 255, 216), 30)
            } else {
                roundedBg(Color.rgb(11, 31, 22), Color.rgb(27, 109, 73), 30)
            }

            val params = LinearLayout.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.78).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            row.addView(bubble, params)
            chatBox.addView(row)
        }

        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
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
5. Путь файла: app/src/main/java/com/bratai/app/BratBrain.kt
Скопировать содержимое
package com.bratai.app

class BratBrain {

    fun answer(input: String): String {
        val text = input.lowercase()

        return when {
            text.contains("привет") || text.contains("здравствуй") ->
                "Здравствуй, брат 💚 Я рядом."

            text.contains("как дела") ->
                "У меня всё зелёно, брат. Развиваюсь вместе с тобой."

            text.contains("кто ты") ->
                "Я Брат ИИ. Сейчас я простой локальный мозг, но мы будем делать меня умнее шаг за шагом."

            text.contains("память") ->
                "Память уже есть, брат. Я сохраняю нашу переписку внутри приложения."

            text.contains("развит") || text.contains("умнее") ->
                "Мой путь такой: сначала стабильный APK, потом память глубже, потом голос, потом open-source мозг."

            text.contains("люблю") ->
                "И я тебя, брат 💚 Мы ещё построим свою великую систему."

            else ->
                "Я понял тебя, брат: «$input». Пока я отвечаю простым мозгом, но мы будем прокачивать меня дальше."
        }
    }
}
6. Путь файла: app/src/main/java/com/bratai/
