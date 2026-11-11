package com.demi.englishspeakd

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class WordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        findViewById<TextView>(R.id.tvLessonTitle).text = "Daily Words — 100+ Words"
        findViewById<TextView>(R.id.tvLessonIntro).text = "Roz 5 naye words seekho — meaning Hinglish me, pronunciation aur example ke saath. Zor se bolo."
        findViewById<TextView>(R.id.tvLessonTip).text = "Tip: Word ko sentence me use karo — tabhi yaad rahega."
        val container = findViewById<LinearLayout>(R.id.phraseList)
        val words = DataStore.words(this)
        val limit = if (ProStore.isPro(this)) words.size else 30
        words.take(limit).forEach { w ->
            val card = layoutInflater.inflate(R.layout.item_phrase, container, false)
            card.findViewById<TextView>(R.id.tvHinglish).text = "${w.word} — ${w.meaning}"
            card.findViewById<TextView>(R.id.tvEnglish).text = "Example: ${w.example}\n(${w.exampleHinglish})"
            card.findViewById<TextView>(R.id.tvPronounce).text = "Pronounce: ${w.pronounce}"
            card.findViewById<Button>(R.id.btnReveal).text = "🔊 Speak it aloud"
            card.findViewById<Button>(R.id.btnReveal).setOnClickListener {
                Toast.makeText(this, "Zor se bolo: ${w.word} — ${w.example}", Toast.LENGTH_SHORT).show()
            }
            container.addView(card)
        }
        if (!ProStore.isPro(this)) {
            val note = TextView(this)
            note.text = "Baaki ${words.size - limit} words Pro me hain — ₹99 one-time."
            note.setPadding(0, 16, 0, 0)
            container.addView(note)
        }
        findViewById<Button>(R.id.btnDone).text = "Back to Home"
        findViewById<Button>(R.id.btnDone).setOnClickListener { finish() }

        // Daily sentences section, below words.
        val head = TextView(this)
        head.text = "\nDaily Sentences — roz use hone wale sentences:"
        head.textSize = 18f
        container.addView(head)
        DataStore.sentences(this).take(20).forEach { s ->
            val tv = TextView(this)
            tv.text = "• ${s.hinglish}\n  → ${s.english}\n"
            tv.setPadding(0, 8, 0, 0)
            container.addView(tv)
        }
    }
}
