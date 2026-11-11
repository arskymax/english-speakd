package com.demi.englishspeakd

import android.content.Context

/**
 * Pro unlock — v1, simple and honest.
 * Free: Lessons 1–5, daily quiz, 5 drills, 30 words, voice bot (2 scenarios).
 * Pro (Rs 99 one-time, sold outside the app via WhatsApp / Gumroad / Instagram DM):
 * the buyer gets an unlock code from DEMi, enters it here, Pro unlocks locally.
 * Replace with Play Billing when the app goes on Google Play.
 * Demo unlock code for testing: SPEAKD99
 */
object ProStore {
    private const val PREFS = "englishspeakd"
    private const val KEY_PRO = "is_pro"

    fun isPro(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PRO, false)

    fun unlock(context: Context, code: String): Boolean {
        val ok = code.trim().uppercase() == "SPEAKD99"
        if (ok) context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_PRO, true).apply()
        return ok
    }

    fun markLessonDone(context: Context, lessonId: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean("done_$lessonId", true).apply()
    }

    fun isLessonDone(context: Context, lessonId: String): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean("done_$lessonId", false)

    fun doneCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.all.count { it.key.startsWith("done_") && it.value == true }
    }

    fun addStreak(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val today = java.time.LocalDate.now().toString()
        if (prefs.getString("last_day", "") == today) return
        val streak = prefs.getInt("streak", 0) + 1
        prefs.edit().putInt("streak", streak).putString("last_day", today).apply()
    }

    fun streak(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("streak", 0)

    fun saveQuizScore(context: Context, score: Int, total: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val best = prefs.getInt("best_quiz", -1)
        if (score > best) prefs.edit().putInt("best_quiz", score).putInt("best_quiz_total", total).apply()
    }

    fun bestQuiz(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("best_quiz", -1)

    // Optional free LLM upgrade: user pastes their own FREE Gemini API key in Settings.
    // Stored only on the phone, never in code, never sent anywhere except Google's API.
    fun geminiKey(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("gemini_key", "") ?: ""

    fun saveGeminiKey(context: Context, key: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("gemini_key", key.trim()).apply()
    }
}
