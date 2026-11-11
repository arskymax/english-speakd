package com.demi.englishspeakd

import android.content.Context
import org.json.JSONArray

object DataStore {
    private var lessons: List<Lesson>? = null
    private var quiz: List<QuizQuestion>? = null
    private var drills: List<Drill>? = null
    private var words: List<Word>? = null
    private var sentences: List<Sentence>? = null
    private var convs: List<Conversation>? = null

    private fun read(context: Context, file: String): String =
        context.assets.open(file).bufferedReader().use { it.readText() }

    fun lessons(context: Context): List<Lesson> {
        lessons?.let { return it }
        val arr = JSONArray(read(context, "lessons.json"))
        val list = mutableListOf<Lesson>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val phrases = mutableListOf<Phrase>()
            val pa = o.getJSONArray("phrases")
            for (j in 0 until pa.length()) {
                val p = pa.getJSONObject(j)
                phrases.add(Phrase(p.getString("hinglish"), p.getString("english"), p.getString("pronounce")))
            }
            list.add(Lesson(o.getString("id"), o.getString("title"), o.getString("category"),
                o.getString("level"), o.getString("intro"), o.getString("tip"),
                o.getBoolean("isPro"), phrases))
        }
        lessons = list; return list
    }

    fun quiz(context: Context): List<QuizQuestion> {
        quiz?.let { return it }
        val arr = JSONArray(read(context, "quiz.json"))
        val list = mutableListOf<QuizQuestion>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val opts = mutableListOf<String>()
            val oa = o.getJSONArray("options")
            for (j in 0 until oa.length()) opts.add(oa.getString(j))
            list.add(QuizQuestion(o.getString("id"), o.getString("question"), opts, o.getInt("answer"), o.getString("explanation")))
        }
        quiz = list; return list
    }

    fun drills(context: Context): List<Drill> {
        drills?.let { return it }
        val arr = JSONArray(read(context, "drills.json"))
        val list = mutableListOf<Drill>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val hints = mutableListOf<String>()
            val ha = o.getJSONArray("hints")
            for (j in 0 until ha.length()) hints.add(ha.getString(j))
            list.add(Drill(o.getString("id"), o.getString("topic"), o.getString("prompt"), hints))
        }
        drills = list; return list
    }

    fun words(context: Context): List<Word> {
        words?.let { return it }
        val arr = JSONArray(read(context, "words.json"))
        val list = mutableListOf<Word>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(Word(o.getString("id"), o.getString("word"), o.getString("meaning"),
                o.getString("pronounce"), o.getString("example"), o.getString("example_hinglish")))
        }
        words = list; return list
    }

    fun sentences(context: Context): List<Sentence> {
        sentences?.let { return it }
        val arr = JSONArray(read(context, "sentences.json"))
        val list = mutableListOf<Sentence>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(Sentence(o.getString("id"), o.getString("hinglish"), o.getString("english")))
        }
        sentences = list; return list
    }

    fun conversations(context: Context): List<Conversation> {
        convs?.let { return it }
        val arr = JSONArray(read(context, "conversations.json"))
        val list = mutableListOf<Conversation>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val steps = mutableListOf<ConvStep>()
            val sa = o.getJSONArray("steps")
            for (j in 0 until sa.length()) {
                val s = sa.getJSONObject(j)
                val kw = mutableListOf<String>()
                val ka = s.getJSONArray("keywords")
                for (k in 0 until ka.length()) kw.add(ka.getString(k))
                steps.add(ConvStep(s.getString("bot"), s.getString("hint"), kw))
            }
            list.add(Conversation(o.getString("id"), o.getString("title"), o.getString("level"), o.getString("bot_opening"), steps))
        }
        convs = list; return list
    }

    fun dailyLesson(context: Context, daySeed: Long): Lesson {
        val all = lessons(context)
        return all[(daySeed % all.size).toInt()]
    }

    fun dailyWord(context: Context, daySeed: Long): Word {
        val all = words(context)
        return all[(daySeed % all.size).toInt()]
    }
}
