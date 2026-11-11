package com.demi.englishspeakd

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    companion object {
        var questions: List<QuizQuestion> = emptyList()
        var answers: Map<Int, Int> = emptyMap()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 0)
        findViewById<TextView>(R.id.tvResultTitle).text = intent.getStringExtra("title") ?: "Result"
        val pct = if (total > 0) score * 100 / total else 0
        findViewById<TextView>(R.id.tvScore).text = "Score: $score / $total  ($pct%)"
        findViewById<TextView>(R.id.tvMsg).text = when {
            pct >= 80 -> "Excellent! 🔥 Tumhari English strong ho rahi hai."
            pct >= 60 -> "Good! Roz 1 lesson + quiz karo, aur behtar hoga."
            pct >= 40 -> "Theek hai — galat wale sentences ko dobara bolo."
            else -> "Koi baat nahi — shuruaat hai. Roz practice karo, score badhega."
        }
        findViewById<TextView>(R.id.tvWeak).text = "Review neeche hai — har galat jawab ko zor se 2 baar bolo."
        val review = questions.mapIndexed { idx, q ->
            val mine = answers[idx]
            val correct = mine == q.answerIndex
            val myAns = if (mine == null) "— skipped" else q.options[mine]
            "Q${idx + 1}. ${q.question}\nYour answer: $myAns ${if (correct) "✅" else "❌"}\nCorrect: ${q.options[q.answerIndex]}\n${q.explanation}\n"
        }.joinToString("\n")
        findViewById<TextView>(R.id.tvReview).text = review
        findViewById<Button>(R.id.btnHome).setOnClickListener { finish() }
    }
}
