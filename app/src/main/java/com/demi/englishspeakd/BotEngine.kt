package com.demi.englishspeakd

/**
 * English Speakd — local conversation bot.
 *
 * How it works (free, offline, no API key needed):
 * - Each conversation is a guided scenario (interview, shop, restaurant…).
 * - The bot speaks its line (TextToSpeech), listens (SpeechRecognizer),
 *   then checks your reply for the expected keywords.
 * - If your reply matches, it moves to the next step and praises you.
 * - If not, it gently repeats the hint in Hinglish and lets you try again.
 * - It also gives a small grammar tip when it spots a common mistake
 *   (e.g. "I am go" → "I am going").
 *
 * Optional upgrade (in Settings / GeminiHelper): if the user adds their
 * own FREE Google Gemini API key, the bot can do open conversation
 * beyond the scripted scenarios. Without a key, the scripted bot works fully.
 */
object BotEngine {

    data class BotReply(val text: String, val advance: Boolean, val tip: String?)

    private val grammarTips = listOf(
        "i am go" to "Say 'I am going', not 'I am go'.",
        "he go" to "Say 'He goes', not 'He go'.",
        "she go" to "Say 'She goes', not 'She go'.",
        "i is" to "Say 'I am', not 'I is'.",
        "he is go" to "Say 'He is going', not 'He is go'.",
        "i have went" to "Say 'I went' or 'I have gone'.",
        "more better" to "Say 'better', not 'more better'.",
        "i am agree" to "Say 'I agree', not 'I am agree'."
    )

    fun grammarTip(userText: String): String? {
        val lower = userText.lowercase()
        return grammarTips.firstOrNull { lower.contains(it.first) }?.second
    }

    /** Score 0..100 for how well the reply matched the expected step. */
    fun scoreReply(userText: String, step: ConvStep): Int {
        val lower = userText.lowercase()
        if (userText.isBlank()) return 0
        val hits = step.keywords.count { lower.contains(it.lowercase()) }
        return when {
            hits >= 2 -> 100
            hits == 1 -> 80
            lower.length > 8 -> 50  // tried in English, even if keywords missed
            else -> 30
        }
    }

    fun replyFor(userText: String, conv: Conversation, stepIndex: Int): BotReply {
        val step = conv.steps.getOrNull(stepIndex)
            ?: return BotReply("Great conversation! Let's try another topic.", true, null)
        val score = scoreReply(userText, step)
        val tip = grammarTip(userText)
        return if (score >= 50) {
            val next = conv.steps.getOrNull(stepIndex + 1)
            if (next != null) {
                BotReply(next.bot, true, tip)
            } else {
                BotReply("Excellent! You finished this conversation. 🎉 Try another topic to practise more.", true, tip)
            }
        } else {
            BotReply(
                "Good try! Try again — ${step.hint}",
                false,
                tip ?: "Hint: ${step.hint}"
            )
        }
    }

    /** Open practice (no scenario): respond to common greetings / questions. */
    fun openReply(userText: String): String {
        val t = userText.lowercase()
        return when {
            "hello" in t || "hi" in t -> "Hello! How are you today?"
            "how are you" in t -> "I am good, thank you! How about you?"
            "your name" in t -> "My name is Speakd, your English practice bot. What is your name?"
            "thank" in t -> "You are welcome! Keep practising."
            "bye" in t -> "Goodbye! See you tomorrow. Keep speaking English."
            "weather" in t -> "The weather is nice today. Do you like rain or sunshine?"
            "cricket" in t -> "Cricket is very popular in India! Who is your favourite player?"
            t.isBlank() -> "I did not hear you. Please speak again, slowly."
            else -> "Nice! Can you tell me more about that in English? Try 2–3 sentences."
        }
    }
}
