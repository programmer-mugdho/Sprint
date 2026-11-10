# Sprint - Engineering Admission Prep Android App

Sprint is an engineering university admission test preparation app covering Physics, Chemistry, Higher Math, and English. Designed to satisfy all 4 lecture modules (Topics 8–11), the app features chapter-wise practice with instant feedback, admission-style timed mock tests with negative marking (+1.0 / -0.25), smart spaced repetition for weak chapters, daily study reminder broadcasts, and an offline-first Room database with cloud sync.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following key decisions were confirmed based on your requirements and syllabus topics:

- **Confirmed UI Architecture**: Classic XML layouts with ViewBinding, RecyclerView, custom adapters, and ViewHolders (matching Lectures 8 & 9).
- **Confirmed Marking Scheme**: Standard engineering entrance scoring (+1.00 for correct answers, -0.25 penalty for wrong answers, 0 for skipped questions).
- **Confirmed Persistence Architecture**: Offline-first Room SQLite database pre-seeded with rich chapter-wise questions, paired with background sync services and Firebase cloud sync for weekly leaderboards.
- **Academic Component Mapping**:
  - *Topic 8 (Activities & Intents)*: Multi-activity architecture (`MainActivity`, `PracticeActivity`, `MockTestActivity`, `TestResultActivity`, `WeakReviewActivity`, `LeaderboardActivity`) with explicit/implicit intents and bundle parameter passing.
  - *Topic 9 (RecyclerView & Custom Views)*: Subject & Chapter selection lists, Question palette grids, Test Result breakdown, and Weekly Leaderboard all built with `RecyclerView` + custom `ViewHolder` layouts and item animations.
  - *Topic 10 (Services & Broadcast Receivers)*: `DailyStudyReminderReceiver` with `AlarmManager` for study habit notifications, and `SyncBackgroundService` for background sync and test timer management.
  - *Topic 11 (Data Handling & Room / Firebase)*: Room DAO with reactive Flows/coroutines, JSON question pack parser, and Firebase Firestore cloud sync.

---

## 1. Overview & Core Concept

- **What It Does**: Provides Bangladeshi and global engineering aspirants (BUET, CKET, Engineering clusters) an all-in-one preparation engine. Students practice subject chapters at their own pace, take timed mock tests matching real exam pressure, identify weak areas through analytics, and systematically clear past errors via an automated spaced-repetition queue.
- **Target Audience**: Engineering admission candidates preparing for Physics, Chemistry, Higher Mathematics, and English.
- **Key Value**: Works 100% offline out-of-the-box, tracks exact time per question to optimize speed, automatically surfaces weak chapters, and helps build daily study discipline through scheduled reminders.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Home Dashboard**: Quick stats (Total solved, overall accuracy, streak counter, weak subject alerts), primary action buttons ("Start Full Mock Test", "Chapter Practice", "Revision Queue (Spaced Repetition)", "Weak Chapter Drill").
2. **Chapter-Wise Practice**: Select Subject (Physics, Chemistry, Math, English) -> Select Chapter -> Interactive quiz card layout with immediate answer explanation, bookmarking toggle, and formula hints.
3. **Timed Full Mock Test**: Real exam simulation with countdown timer, question palette drawer/grid (answered, marked for review, unattempted), answer selection, and quick submission.
4. **Result & Speed Analytics**: Comprehensive scorecard showing total score (accounting for -0.25 penalties), accuracy %, time spent per question vs. average, and chapter-by-chapter mastery breakdown.
5. **Spaced Repetition Review Queue**: Wrongly answered questions automatically enter a spaced repetition queue (re-tested after 1 day, 3 days, 7 days) until mastered.
6. **Leaderboard & Cloud Sync**: View weekly peer rankings, filter by subject, and sync progress to the cloud.

### Visual Identity & Theme
- **Aesthetic Direction**: High-energy, academic, and focused "Engineering Sprint" design with dark slate blue surfaces, crisp electric amber/cyan accents, and high-contrast typography.
- **Color Palette**:
  - `primary`: `#1A237E` (Deep Navy) & `#2962FF` (Royal Electric Blue)
  - `secondary`: `#00B0FF` (Cyan Accent)
  - `accent / warning`: `#FF9100` (Amber for timer and streak)
  - `success`: `#00C853` (Emerald Green for correct answers)
  - `error`: `#D50000` (Crimson for negative marks)
  - `surface / background`: `#F5F7FA` (Clean Off-White) / `#FFFFFF` (Card White)
- **Typography**: Clean, highly readable system font hierarchy with bold headings and distinct monospace indicators for math formulas and timers.
- **UI Feedback**: Card elevation changes on selection, smooth RecyclerView transitions, color-coded answer feedback, and tactile button states.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: XML ViewBinding + RecyclerView vs. Pure Compose**
  - *Chosen Approach*: Classic XML layouts with ViewBinding and custom RecyclerView adapters across all screens.
  - *Why*: Directly fulfills the course syllabus requirements (Topics 8 & 9) while providing rock-solid Android performance and standard component lifecycles.
- **Decision 2: Offline-First Room Database with Pre-loaded Question Bank**
  - *Chosen Approach*: Bundle a curated set of engineering questions across all 4 subjects in an assets JSON/Room pre-population script, with full offline operation.
  - *Why*: Students study during commutes or areas with spotty internet; tests and practice sessions must never stutter or fail due to network drops.
- **Decision 3: Android BroadcastReceiver + AlarmManager for Daily Reminders**
  - *Chosen Approach*: Standard system `BroadcastReceiver` listening to alarms and device boot, firing notifications with high-priority study encouragement.
  - *Why*: Directly covers Topic 10 requirements and creates an authentic mobile study routine.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          Sprint Application                            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
          ┌─────────────────────────┴────────────────────────┐
          ▼                                                  ▼
┌──────────────────────────┐                       ┌─────────────────────┐
│     UI Layer (XML)       │                       │ Android Components  │
│  - Activities (Topic 8)  │                       │ - Services (Topic10)│
│  - ViewBinding           │                       │ - Receivers (Alarm) │
│  - RecyclerView (Topic 9)│                       │ - Explicit Intents  │
└─────────┬────────────────┘                       └──────────┬──────────┘
          │                                                   │
          ▼                                                   ▼
┌──────────────────────────┐                       ┌─────────────────────┐
│  Presentation / VM Layer │                       │  Background Worker  │
│  - Quiz & Mock ViewModels│                       │  - Study Reminder   │
│  - Analytics Engine      │                       │  - Firestore Sync   │
└─────────┬────────────────┘                       └──────────┬──────────┘
          │                                                   │
          └─────────────────────────┬─────────────────────────┘
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Data Layer (Topic 11)                           │
│  - AppDatabase (Room SQLite)                                           │
│  - QuestionDao, MockTestDao, PerformanceDao, BookmarkDao               │
│  - Repositories: QuestionRepo, TestRepo, SyncRepo                      │
│  - Local JSON Assets Seeder (Physics, Chemistry, Math, English)        │
│  - Firebase Firestore Remote Sync & Weekly Leaderboard                 │
└────────────────────────────────────────────────────────────────────────┘
```

### Data Models & Entities
1. **QuestionEntity**: `id`, `subject` (Physics, Chemistry, Math, English), `chapter`, `questionText`, `formulaLatex`, `optionA`, `optionB`, `optionC`, `optionD`, `correctOptionIndex`, `explanation`, `difficulty`.
2. **QuestionAttemptEntity**: `id`, `questionId`, `selectedOption`, `isCorrect`, `timeSpentSeconds`, `timestamp`, `testSessionId`.
3. **SpacedRepetitionEntity**: `questionId`, `intervalDays`, `nextReviewTimestamp`, `streakCorrect`, `isMastered`.
4. **MockTestSessionEntity**: `id`, `testTitle`, `totalQuestions`, `correctCount`, `wrongCount`, `skippedCount`, `scoreAchieved`, `totalTimeSeconds`, `timestamp`.
5. **BookmarkEntity**: `questionId`, `bookmarkedAt`, `notes`.
6. **LeaderboardEntry**: `userId`, `userName`, `collegeOrTarget`, `totalScore`, `testsCompleted`, `rank`.

### Step-by-Step Implementation Roadmap
1. **Build Configuration**: Enable ViewBinding in `app/build.gradle.kts`, ensure AppCompat / Material dependencies are properly configured, and register metadata.
2. **Database & Room Layer**: Define entities, DAOs, Room database, and write an asset seeder for Physics, Chemistry, Higher Math, and English questions.
3. **Android Components (BroadcastReceiver & Service)**: Implement `DailyStudyReminderReceiver`, `TestTimerService`, and permission setup.
4. **Adapter & ViewHolders (RecyclerViews)**: Create custom layouts for subjects, chapters, question list, test summary cards, and leaderboard.
5. **Activity Implementations**:
   - `MainActivity`: Overview cards, streak, progress summary, quick launch navigation.
   - `ChapterListActivity` & `PracticeActivity`: Chapter selection and instant-feedback practice.
   - `MockTestActivity`: Timed exam interface with question navigation palette and submit confirmation.
   - `TestResultActivity`: Mark breakdown (+1.0 / -0.25), question review, time-spent analysis.
   - `WeakAreasReviewActivity`: Spaced repetition revision session.
   - `LeaderboardActivity`: Weekly leaderboard with sync.
6. **Verification & Testing**: Compile, run unit tests, and verify end-to-end user journeys.
