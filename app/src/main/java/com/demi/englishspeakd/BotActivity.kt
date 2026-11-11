package com.demi.englishspeakd

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BotActivity : AppCompatActivity() {
    private lateinit var voice: VoiceBot
    private var conv: Conversation? = null
    private var stepIndex = 0
    private val chat = mutableListOf<ChatMsg>()
    private lateinit var chatBox: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot)
        chatBox = findViewById(R.id.tvChat)
        voice = VoiceBot(this)
        voice.onHeard = { heard -> runOnUiThread { onUserSaid(heard) } }
        voice.onError = { err -> runOnUiThread { addMsg(false, "Bot: $err"); Toast.makeText(this, err, Toast.LENGTH_LONG).show() } }

        val convs = DataStore.conversations(this)
        val spinner = findViewById<Spinner>(R.id.spScenario)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            convs.map { "${it.title} • ${it.level}" + if (convs.indexOf(it) >= 2 && !ProStore.isPro(this)) " 🔒 Pro" else "" })
        findViewById<Button>(R.id.btnStartBot).setOnClickListener {
            val pos = spinner.selectedItemPosition
            if (pos >= 2 && !ProStore.isPro(this)) {
                Toast.makeText(this, "Ye scenario Pro me hai — ₹99 me unlock karo.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            startConversation(convs[pos])
        }
        findViewById<Button>(R.id.btnSpeak).setOnClickListener { askMicThenListen() }
        findViewById<Button>(R.id.btnSendText).setOnClickListener {
            val input = findViewById<EditText>(R.id.etTyped)
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) { input.setText(""); onUserSaid(text) }
        }
        findViewById<Button>(R.id.btnHearAgain).setOnClickListener {
            val lastBot = chat.lastOrNull { it.fromBot }?.text
            if (lastBot != null) voice.speak(lastBot) else Toast.makeText(this, "Pehle conversation start karo.", Toast.LENGTH_SHORT).show()
        }
        // Default: first scenario, so the screen is never empty.
        startConversation(convs[0])
    }

    private fun startConversation(c: Conversation) {
        conv = c; stepIndex = 0; chat.clear()
        addMsg(true, "Bot: ${c.botOpening}")
        voice.speak(c.botOpening)
        findViewById<TextView>(R.id.tvBotHint).text = "Hint: ${c.steps[0].hint}"
    }

    private fun askMicThenListen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
            Toast.makeText(this, "Mic permission do, phir 🎤 dabao.", Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, "Sun raha hoon… bolo!", Toast.LENGTH_SHORT).show()
        voice.listen(this)
    }

    private fun onUserSaid(text: String) {
        addMsg(false, "You: $text")
        val c = conv ?: run {
            val reply = BotEngine.openReply(text)
            addMsg(true, "Bot: $reply"); voice.speak(reply); return
        }
        val reply = BotEngine.replyFor(text, c, stepIndex)
        addMsg(true, "Bot: ${reply.text}")
        reply.tip?.let { addMsg(true, "💡 Tip: $it") }
        voice.speak(reply.text)
        if (reply.advance) {
            stepIndex++
            if (stepIndex < c.steps.size) {
                findViewById<TextView>(R.id.tvBotHint).text = "Hint: ${c.steps[stepIndex].hint}"
            } else {
                ProStore.addStreak(this)
                findViewById<TextView>(R.id.tvBotHint).text = "🎉 Conversation complete! Streak badh gaya."
            }
        }
    }

    private fun addMsg(fromBot: Boolean, text: String) {
        chat.add(ChatMsg(fromBot, text))
        chatBox.text = chat.joinToString("\n\n") { it.text }
    }

    override fun onDestroy() { super.onDestroy(); voice.stop() }
}
