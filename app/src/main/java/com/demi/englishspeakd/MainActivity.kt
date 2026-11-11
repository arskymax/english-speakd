package com.demi.englishspeakd

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lessons = DataStore.lessons(this)
        val totalPhrases = lessons.sumOf { it.phrases.size }
        findViewById<TextView>(R.id.tvSubtitle).text =
            "Hi, ${AuthStore.displayName(this)} 👋\n${lessons.size} lessons • $totalPhrases sentences • ${DataStore.words(this).size} words • Voice Bot • Offline"
        updateStats()

        findViewById<Button>(R.id.btnBot).setOnClickListener {
            startActivity(Intent(this, BotActivity::class.java))
        }
        findViewById<Button>(R.id.btnDaily).setOnClickListener {
            LessonActivity.lesson = DataStore.dailyLesson(this, LocalDate.now().toEpochDay())
            startActivity(Intent(this, LessonActivity::class.java))
        }
        findViewById<Button>(R.id.btnWords).setOnClickListener {
            startActivity(Intent(this, WordsActivity::class.java))
        }
        findViewById<Button>(R.id.btnQuiz).setOnClickListener {
            QuizActivity.questions = DataStore.quiz(this).shuffled().take(10)
            startActivity(Intent(this, QuizActivity::class.java))
        }
        findViewById<Button>(R.id.btnDrills).setOnClickListener {
            startActivity(Intent(this, DrillActivity::class.java))
        }
        findViewById<Button>(R.id.btnPro).setOnClickListener { showUnlockDialog() }
        findViewById<Button>(R.id.btnSignIn).setOnClickListener { showSignInDialog() }

        val list = findViewById<ListView>(R.id.lvTests)
        val labels = lessons.map { l ->
            val done = if (ProStore.isLessonDone(this, l.id)) " ✅ Done" else ""
            val lock = if (l.isPro && !ProStore.isPro(this)) " 🔒 Pro" else ""
            "${l.title}$lock$done\n${l.category} • ${l.level} • ${l.phrases.size} sentences"
        }
        list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labels)
        list.setOnItemClickListener { _, _, pos, _ ->
            val lesson = lessons[pos]
            if (lesson.isPro && !ProStore.isPro(this)) {
                Toast.makeText(this, "Ye Pro lesson hai — ₹99 me unlock karo.", Toast.LENGTH_LONG).show()
                showUnlockDialog()
                return@setOnItemClickListener
            }
            LessonActivity.lesson = lesson
            startActivity(Intent(this, LessonActivity::class.java))
        }
    }

    override fun onResume() { super.onResume(); updateStats() }

    private fun updateStats() {
        findViewById<TextView>(R.id.tvPro).text =
            if (ProStore.isPro(this)) "PRO UNLOCKED ✅ — all lessons + all voice scenarios open"
            else "Free: Lessons 1–5 + 2 voice scenarios • Pro ₹99 one-time"
        val best = ProStore.bestQuiz(this)
        findViewById<TextView>(R.id.tvStats).text =
            "Lessons done: ${ProStore.doneCount(this)}/20 • Streak: ${ProStore.streak(this)} day(s)" +
            if (best >= 0) " • Best quiz: $best/10" else ""
        findViewById<Button>(R.id.btnSignIn).text =
            if (AuthStore.isSignedIn(this)) "👤 ${AuthStore.displayName(this)} — tap to sign out"
            else "🔐 Sign in with Google"
    }

    private fun showSignInDialog() {
        if (AuthStore.isSignedIn(this)) {
            AlertDialog.Builder(this)
                .setTitle("Sign out?")
                .setMessage("Signed in as ${AuthStore.displayName(this)}.")
                .setPositiveButton("Sign out") { _, _ ->
                    AuthStore.signOut(this, webClientId())
                    Toast.makeText(this, "Signed out. Guest mode on.", Toast.LENGTH_SHORT).show()
                    recreate()
                }
                .setNegativeButton("Cancel", null).show()
            return
        }
        val webId = webClientId()
        AlertDialog.Builder(this)
            .setTitle("Sign in with Google")
            .setMessage(
                if (webId == null) "Google Sign-In is not configured yet (free setup: Google Cloud Console → OAuth Client ID → paste it in res/values/google_signin.xml). Tab tak Guest mode me continue karo — app poora kaam karega."
                else "Google account se sign in karo — tumhara naam aur progress save rahega."
            )
            .setPositiveButton("Sign in with Google") { _, _ ->
                val intent = AuthStore.signInIntent(this, webId)
                if (intent == null) {
                    Toast.makeText(this, "Google Sign-In abhi configure nahi hai. Guest mode use karo.", Toast.LENGTH_LONG).show()
                } else startActivityForResult(intent, AuthStore.RC_SIGN_IN)
            }
            .setNeutralButton("Continue as Guest") { _, _ ->
                AuthStore.signInAsGuest(this)
                Toast.makeText(this, "Welcome, Guest Learner! 👋", Toast.LENGTH_SHORT).show()
                recreate()
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun webClientId(): String? {
        return try {
            val id = getString(R.string.google_web_client_id)
            if (id.isBlank() || id.startsWith("PASTE_")) null else id
        } catch (_: Exception) { null }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AuthStore.RC_SIGN_IN) {
            val account = AuthStore.handleResult(this, data)
            if (account != null) {
                Toast.makeText(this, "Welcome, ${account.displayName}! 👋", Toast.LENGTH_LONG).show()
                recreate()
            } else {
                Toast.makeText(this, "Sign-in failed. Try again or continue as Guest.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showUnlockDialog() {
        val input = EditText(this)
        input.hint = "Unlock code likho — jaise SPEAKD99"
        AlertDialog.Builder(this)
            .setTitle("Unlock Pro — ₹99 one-time")
            .setMessage("Pro kharidne ke liye @im666demi pe DM karo. Code milne ke baad yahan dalo — saare 20 lessons, 160 sentences, 100 words, 8 voice scenarios unlock ho jayenge.")
            .setView(input)
            .setPositiveButton("Unlock") { _, _ ->
                if (ProStore.unlock(this, input.text.toString())) {
                    Toast.makeText(this, "Pro unlocked! 🎉 Ab bolo, confidently!", Toast.LENGTH_LONG).show()
                    recreate()
                } else {
                    Toast.makeText(this, "Galat code. Sahi code ke liye @im666demi pe message karo.", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }
}
