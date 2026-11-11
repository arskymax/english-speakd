package com.demi.englishspeakd

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class DrillActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        findViewById<TextView>(R.id.tvLessonTitle).text = "Speaking Drills — Zor se bolo!"
        findViewById<TextView>(R.id.tvLessonIntro).text = "Har drill pe 30 second zor se English me bolo. Akele bhi chalega — awaaz me bolna hi practice hai."
        findViewById<TextView>(R.id.tvLessonTip).text = "Tip: Ruko mat. Galti ho to bhi continue karo — fluency rukne se nahi, bolne se aati hai."
        val container = findViewById<LinearLayout>(R.id.phraseList)
        val drills = DataStore.drills(this)
        val freeLimit = if (ProStore.isPro(this)) drills.size else 5
        drills.take(freeLimit).forEach { d ->
            val card = layoutInflater.inflate(R.layout.item_phrase, container, false)
            card.findViewById<TextView>(R.id.tvHinglish).text = d.topic
            card.findViewById<TextView>(R.id.tvEnglish).text = d.prompt + "\nHints: " + d.hints.joinToString(" • ")
            card.findViewById<TextView>(R.id.tvPronounce).text = "30 seconds — speak aloud, in English."
            card.findViewById<Button>(R.id.btnReveal).text = "Done — I spoke it ✅"
            card.findViewById<Button>(R.id.btnReveal).setOnClickListener {
                ProStore.addStreak(this)
                Toast.makeText(this, "Well done! Agli drill try karo.", Toast.LENGTH_SHORT).show()
            }
            container.addView(card)
        }
        if (!ProStore.isPro(this)) {
            val note = TextView(this)
            note.text = "Baaki ${drills.size - freeLimit} drills Pro me hain — ₹99 one-time."
            note.setPadding(0, 16, 0, 0)
            container.addView(note)
        }
        findViewById<Button>(R.id.btnDone).text = "Back to Home"
        findViewById<Button>(R.id.btnDone).setOnClickListener { finish() }
    }
}
