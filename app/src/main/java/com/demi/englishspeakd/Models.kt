package com.demi.englishspeakd

data class Phrase(val hinglish: String, val english: String, val pronounce: String)
data class Lesson(
    val id: String, val title: String, val category: String, val level: String,
    val intro: String, val tip: String, val isPro: Boolean, val phrases: List<Phrase>
)
data class QuizQuestion(val id: String, val question: String, val options: List<String>, val answerIndex: Int, val explanation: String)
data class Drill(val id: String, val topic: String, val prompt: String, val hints: List<String>)
data class Word(val id: String, val word: String, val meaning: String, val pronounce: String, val example: String, val exampleHinglish: String)
data class Sentence(val id: String, val hinglish: String, val english: String)
data class ConvStep(val bot: String, val hint: String, val keywords: List<String>)
data class Conversation(val id: String, val title: String, val level: String, val botOpening: String, val steps: List<ConvStep>)
data class ChatMsg(val fromBot: Boolean, val text: String)
