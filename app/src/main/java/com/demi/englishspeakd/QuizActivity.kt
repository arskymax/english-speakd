package com.demi.englishspeakd

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    companion object { var questions: List<QuizQuestion> = emptyList() }
    private var index = 0
    private val answers = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        findViewById<TextView>(R.id.tvQuizTitle).text = "English Quiz — 10 Questions"
        findViewById<TextView>(R.id.tvTimer).text = "No timer — soch ke jawab do"
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            saveAnswer()
            if (index < questions.size - 1) { index++; showQuestion() } else finishQuiz()
        }
        findViewById<Button>(R.id.btnPrev).setOnClickListener { if (index > 0) { index--; showQuestion() } }
        showQuestion()
    }

    private fun saveAnswer() {
        val group = findViewById<RadioGroup>(R.id.rgOptions)
        val checked = group.checkedRadioButtonId
        if (checked != -1) answers[index] = group.indexOfChild(findViewById(checked))
    }

    private fun showQuestion() {
        val q = questions[index]
        findViewById<TextView>(R.id.tvQNo).text = "Q ${index + 1} / ${questions.size}"
        findViewById<TextView>(R.id.tvQuestion).text = q.question
        val group = findViewById<RadioGroup>(R.id.rgOptions)
        group.removeAllViews(); group.clearCheck()
        q.options.forEachIndexed { i, opt ->
            val rb = RadioButton(this)
            rb.text = "${('A' + i)}. $opt"; rb.textSize = 16f
            group.addView(rb)
            if (answers[index] == i) rb.isChecked = true
        }
        findViewById<Button>(R.id.btnNext).text = if (index == questions.size - 1) "Submit ✅" else "Next →"
    }

    private fun finishQuiz() {
        saveAnswer()
        val score = questions.indices.count { answers[it] == questions[it].answerIndex }
        ProStore.saveQuizScore(this, score, questions.size)
        ProStore.addStreak(this)
        ResultActivity.questions = questions
        ResultActivity.answers = answers.toMap()
        val i = Intent(this, ResultActivity::class.java)
        i.putExtra("score", score); i.putExtra("total", questions.size)
        i.putExtra("title", "English Quiz Result")
        startActivity(i); finish()
    }
}
