# English Speakd 🗣️🤖 — AI Voice Bot se English Practice

**Roz 5 minute. Bot se bolo. Dar kam, English zyada.**

A clean, professional Spoken English app for Hinglish / Assamese speakers in
India who understand English but can't speak it — for jobs, interviews,
college, and confidence.

> Students know English grammar from books, but they freeze when they have
> to *speak*. Coaching costs ₹2,000–₹5,000, and most apps are English-only.
> English Speakd teaches in Hinglish, gives you an AI voice bot to talk to,
> and makes you speak aloud — 5 minutes a day, offline, free to start.

Built by [DEMi](https://www.instagram.com/im666demi) from Assam, India.
If this helps you, please ⭐ star this repo.

---

## What is inside — everything, in one app

### 🤖 AI Voice Bot — practice conversation like a human
- **8 guided scenarios:** Greetings, Interview, At the Shop, Restaurant,
  Travelling, Talk About Your Day, Small Talk, On the Phone
- Bot **speaks** (free Android TextToSpeech), **listens** (free Android
  SpeechRecognizer), checks your reply, praises you, and moves ahead —
  or gently repeats the hint in Hinglish so you can try again
- **Grammar tips** for common mistakes — e.g. "I am go" → "I am going"
- **Type mode** if mic / speech recognition is not available — nothing breaks
- **Offline bot:** scripted scenarios work with no internet, no API key, no cost.
  Optional upgrade: paste your own **free Gemini API key** in ProStore
  (`gemini_key`, stored only on the phone) for open conversation beyond the scripts.

### 📚 Learn English — 20 lessons, 5 min each
Greetings, Introducing Yourself, Daily Routine, Family, Shop, College,
Friends, Directions, Phone, Restaurant, Travel, Work, Interview (×2),
Meeting, Small Talk, Apologising, Making Plans, Shopping Online, Confidence.

### 📖 Daily words & sentences — everything you use daily
- **100+ daily-used words** — meaning in Hinglish, pronunciation, example sentence
- **100+ daily-used sentences** — Hinglish → English
- **160 lesson sentences** — Hinglish → English, with pronunciation

### 🎤 Speaking drills — 20 topics
30-second topics to speak aloud — introduce yourself, your day, your dream job…

### 📝 Quiz — 50 questions
Choose the correct English, with instant review + explanation. Best score saved.

### 🔐 Sign in with Google
- **Google Sign-In** (free, Play Services Auth — no Firebase needed)
- Your name is shown on Home, progress stays linked to you
- **Guest mode works fully** — no account needed to learn

### 📈 Progress
Lessons done, streak, best quiz score — saved on your phone, no account needed.

## Content counts

| | Count |
|---|---|
| Lessons | 20 |
| Lesson sentences | 160 |
| Daily words | 100+ |
| Daily sentences | 100+ |
| Quiz questions | 50 |
| Speaking drills | 20 |
| Voice-bot scenarios | 8 |

Content lives in `app/src/main/assets/` — `lessons.json`, `words.json`,
`sentences.json`, `quiz.json`, `drills.json`, `conversations.json`.
Regenerate with:
```bash
python3 tools/generate_data.py        # EnglishBol base (lessons/quiz/drills)
python3 tools/generate_speakd_data.py # words / sentences / conversations
```

## Free vs Pro

| | Free | Pro — ₹99 one-time |
|---|---|---|
| Lessons 1–5, daily lesson | ✅ | ✅ |
| Voice Bot — first 2 scenarios | ✅ | ✅ |
| Words (first 30), Quiz, Drills (first 5) | ✅ | ✅ |
| Lessons 6–20 + Interview pack | 🔒 | ✅ |
| All 8 voice scenarios, all words/sentences/drills | 🔒 | ✅ |

Pro is sold **outside the app** (WhatsApp / Gumroad / Instagram DM to @im666demi).
Buyer gets an unlock code → enters it in the app → Pro unlocks on the phone.

> Demo unlock code for testing: `SPEAKD99`
> Replace it in `ProStore.kt` before selling — and switch to Google Play Billing when the app goes on Play.

## Google Sign-In setup (free, ~10 min, once)

1. Google Cloud Console → new project → OAuth consent screen (External, test users: your email).
2. Credentials → Create OAuth Client ID → **Android**: package `com.demi.englishspeakd` + your SHA-1
   (Android Studio → Gradle → `signingReport`).
3. Credentials → Create OAuth Client ID → **Web application** → copy its Client ID.
4. Paste it in `app/src/main/res/values/google_signin.xml` as `google_web_client_id`.
5. Rebuild. Sign-in will work on a real phone.

Until the ID is pasted, the app shows a clear message and **Guest mode works fully** —
learning, bot, quiz, everything. Nothing is blocked, nothing crashes.

## Build the APK (free)

1. Open this folder in **Android Studio** (Hedgehog or newer).
2. Let Gradle sync. No API keys, no backend, no paid service needed.
3. Run on a phone / emulator, or:
   ```bash
   ./gradlew assembleDebug
   ```
   APK: `app/build/outputs/apk/debug/app-debug.apk`

Min SDK 24 (Android 7.0+), target SDK 34. Kotlin + XML layouts.
Free things only: Android TTS + SpeechRecognizer, Play Services Auth,
local JSON data, SharedPreferences. No server.

## How DEMi sells it (honest model)

- **Free version first** — learners try lessons + bot + quiz, trust builds.
- **Pro ₹99 one-time** — DM on @im666demi → UPI payment → unlock code → Pro unlocks.
- **Where to sell:** Instagram (@im666demi), WhatsApp / Telegram groups,
  friends / classmates who want English for jobs and interviews.
- **First proof target:** 10 Pro sales × ₹99 = **₹990** (11 sales crosses ₹1,000).

No guaranteed income — a learner has to find it useful enough to pay ₹99.
The free lessons + voice bot are what make that decision possible.

## Project structure

```
app/src/main/
  java/com/demi/englishspeakd/
    MainActivity.kt      # dashboard, Google sign-in, Pro unlock, lesson list
    BotActivity.kt       # AI voice bot screen (speak / listen / type)
    BotEngine.kt         # offline conversation engine + grammar tips
    VoiceBot.kt          # Android TTS + SpeechRecognizer wrapper
    AuthStore.kt         # Google Sign-In + Guest mode
    WordsActivity.kt     # daily words + daily sentences
    LessonActivity.kt    # lesson phrases, tap-to-reveal, streak
    QuizActivity.kt / ResultActivity.kt
    DrillActivity.kt     # 30-second speaking drills
    DataStore.kt         # loads all JSON from assets
    Models.kt            # Lesson, Word, Sentence, Conversation, etc.
    ProStore.kt          # Pro unlock + progress + optional Gemini key
  assets/                # lessons, words, sentences, quiz, drills, conversations
  res/layout/            # activity_main, activity_bot, activity_lesson,
                         #   activity_quiz, activity_result, item_phrase
  res/values/google_signin.xml  # paste your Web Client ID here
tools/generate_data.py / generate_speakd_data.py
```

## Contribute

- Wrong English sentence? Open an issue with the lesson / word ID.
- More interview scenarios? Add them to `tools/generate_speakd_data.py`.
- Keep English natural and correct — no copied textbook dumps.

## License

MIT — free to use, share and build on. See [LICENSE](LICENSE).
