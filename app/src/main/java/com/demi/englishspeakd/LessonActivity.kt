package com.demi.englishspeakd

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LessonActivity : AppCompatActivity() {
    companion object { var lesson: Lesson? = null }
    private var shown = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        val l = lesson ?: run { finish(); return }
        findViewById<TextView>(R.id.tvLessonTitle).text = l.title
        findViewById<TextView>(R.id.tvLessonIntro).text = l.intro
        findViewById<TextView>(R.id.tvLessonTip).text = l.tip

        val container = findViewById<LinearLayout>(R.id.phraseList)
        l.phrases.forEach { ph ->
            val card = layoutInflater.inflate(R.layout.item_phrase, container, false)
            card.findViewById<TextView>(R.id.tvHinglish).text = ph.hinglish
            card.findViewById<TextView>(R.id.tvEnglish).text = ph.english
            card.findViewById<TextView>(R.id.tvPronounce).text = "Pronounce: ${ph.pronounce}"
            // Tap to practise aloud: hide English first, tap to reveal.
            val eng = card.findViewById<TextView>(R.id.tvEnglish)
            val pron = card.findViewById<TextView>(R.id.tvPronounce)
            eng.visibility = android.view.View.GONE
            pron.visibility = android.view.View.GONE
            card.findViewById<Button>(R.id.btnReveal).setOnClickListener {
                eng.visibility = android.view.View.VISIBLE
                pron.visibility = android.view.View.VISIBLE
                shown++
                if (shown >= l.phrases.size) {
                    ProStore.markLessonDone(this, l.id)
                    ProStore.addStreak(this)
                    Toast.makeText(this, "Lesson complete! 🎉 Streak badh gaya.", Toast.LENGTH_LONG).show()
                }
            }
            container.addView(card)
        }
        findViewById<Button>(R.id.btnDone).setOnClickListener {
            ProStore.markLessonDone(this, l.id)
            ProStore.addStreak(this)
            Toast.makeText(this, "Lesson done! Ab zor se 2 baar bolo.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
