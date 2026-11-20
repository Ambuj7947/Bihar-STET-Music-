# STET Music — Bihar STET Paper 1 (Music) CBT Mock Practice App

An offline-first Android application built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **Room Database** for final-day **Bihar STET Paper 1 (Music)** Computer-Based Test (CBT) mock practice.

---

## Official BSEB Bihar STET Paper 1 Exam Pattern

| Section | Subject | Questions | Marks | Marking Scheme |
| :--- | :--- | :---: | :---: | :---: |
| **Unit I** | Music (Specific Subject) | 100 | 100 | +1 / 0 (No Negative Marking) |
| **Unit II** | Teaching & General Skills (GK, Bihar GK, EVS/Science, Math & Reasoning) | 50 | 50 | +1 / 0 (No Negative Marking) |
| **Total** | **Full-Length CBT Paper** | **150** | **150** | **150 Minutes (2h 30m)** |

---

## Verified Question Bank (460 Questions)

All questions are bundled offline in `app/src/main/assets/` with verified answer keys and concise Hindi/Bilingual explanations:

- **277 Indian Classical Music Questions** (`questions_music_1.json`, `questions_music_2.json`) — Shruti, Swar, Murchhana, Granths, Ragas, Thaat, Taal, Notation (Bhatkhande & Paluskar), Gayaki, Instruments & Gharanas.
- **101 General Knowledge & Bihar GK Questions** (`questions_gk.json`) — Bilingual (Hindi + English) Bihar History, Geography, Polity, Economy & General Science.
- **82 General Reasoning Questions** (`questions_reasoning.json`) — Bilingual (English + Hindi) Analogy, Coding-Decoding, Direction Sense, Blood Relations, Series & Mathematical Operations.

---

## Key Features

- **10 Full-Length CBT Mock Tests (150 Qs / 150 Mins)**: Mocks #01–#03 systematically cover 100% of all 277 unique Music questions, and Mocks #01–#04 systematically cover 100% of all 183 unique GK & Reasoning questions.
- **15 Sectional Practice Drills**: Chapter-wise practice across all 15 syllabus sections.
- **Realistic CBT Simulator**:
  - Live `02:30:00` countdown timer & per-question stopwatch
  - Text zoom controls (`75%–130%`)
  - Flag for Review, Bookmark, Clear Response, Skip, and Save & Next controls
  - **Question Palette** bottom sheet with Unit I / Unit II switcher and 5-state status legend (Answered, Skipped, Flagged, Answered & Flagged, Not Visited)
- **Scorecard & Explanations**: Instant score breakdown (Unit I vs Unit II), circular score gauge, filterable review (`All`, `Wrong`, `Correct`, `Skipped`), and detailed explanations.
- **Performance Analytics**: Persistent Room-backed radial accuracy overview, 15 syllabus section strength meters, and attempt history.
- **Mistakes & Bookmarks Notebook**: Auto-captures wrong attempts and saved questions with one-tap **Practice in CBT Mode** and **Mark as Mastered**.
- **High-Yield Revision Notes**: 7 rapid revision capsules with structured tables (22 Shrutis & Rasa, Granths, Ragas, Taals, Artists/Gharanas, Bihar GK, and Reasoning shortcuts).
- **Complete Searchable Question Bank**: Instant Hindi/English keyword search, category/section filters, Answer Key visibility toggle, and custom CBT drills.

---

## Tech Stack & Architecture

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3 (`StetMusicTheme`, Edge-to-Edge, bundled `Plus Jakarta Sans`, `Noto Sans Devanagari`, `JetBrains Mono`)
- **Architecture**: MVVM (`ExamViewModel` + `StateFlow` + `ExamRepository`)
- **Local Storage**: Room Database (`stet_music_exam.db`) with atomic transactions and batch queries
- **Testing**: JUnit 4 + Robolectric (`ExampleUnitTest`, `ExampleRobolectricTest`)

---

## Project Structure

```text
app/src/main/
├── assets/
│   ├── questions_music_1.json      # Music Q1–Q140
│   ├── questions_music_2.json      # Music Q141–Q277
│   ├── questions_gk.json           # Bilingual GK & Bihar GK Q1–Q101
│   └── questions_reasoning.json    # Bilingual Reasoning Q1–Q82
├── java/com/example/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── Question.kt
│   │   ├── QuestionBankLoader.kt
│   │   ├── ExamRepository.kt
│   │   ├── RevisionNotesData.kt
│   │   └── local/                  # Room Entities, DAO & AppDatabase
│   ├── viewmodel/
│   │   └── ExamViewModel.kt
│   └── ui/
│       ├── theme/                  # Color, Type, Theme
│       ├── components/             # Shared Header, Bottom Nav, Cards
│       └── screens/                # Home, Tests, CBT, Overlays, Scorecard, Analytics, Mistakes, Notes, Bank
└── res/
    ├── font/                       # Bundled local TTF fonts
    ├── drawable-nodpi/             # Adaptive launcher icon foreground
    └── mipmap-anydpi-v26/          # Adaptive + Monochrome themed launcher icons
```

---

## Build & Run

### Prerequisites
- Android Studio Ladybug+ or JDK 11+
- Android SDK 36 (`minSdk = 24`, `targetSdk = 36`)

### Build Debug APK
```bash
gradle :app:assembleDebug
```

### Run Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

### Run Android Lint
```bash
gradle :app:lintDebug
```

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
