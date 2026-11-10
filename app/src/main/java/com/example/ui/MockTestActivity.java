package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.R;
import com.example.SprintApplication;
import com.example.data.entity.QuestionEntity;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityMockTestBinding;
import com.example.ui.adapter.MockPaletteAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * MockTestActivity (Java) demonstrating:
 * - Timed full admission exam simulation
 * - Engineering negative marking rules (+1.0 / -0.25)
 * - RecyclerView palette navigation
 * - Per-question time tracking
 * - Handler-based countdown timer
 */
public class MockTestActivity extends AppCompatActivity {

    private ActivityMockTestBinding binding;
    private SprintRepository repository;

    private List<QuestionEntity> questions = new ArrayList<>();
    private int currentIndex = 0;

    private final Map<Integer, Integer> selectedAnswers = new HashMap<>();
    private final Map<Integer, Integer> timeSpentPerQuestion = new HashMap<>();
    private long currentQuestionEnterTimeMs = 0;

    private int countdownSeconds = 15 * 60; // 15 minutes
    private final Handler countdownHandler = new Handler(Looper.getMainLooper());
    private MockPaletteAdapter paletteAdapter;

    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            if (countdownSeconds > 0) {
                int min = countdownSeconds / 60;
                int sec = countdownSeconds % 60;
                binding.tvCountdownTimer.setText(String.format(Locale.US, "%02d:%02d", min, sec));
                countdownSeconds--;
                countdownHandler.postDelayed(this, 1000);
            } else {
                Toast.makeText(MockTestActivity.this, "Time is up! Submitting your test...", Toast.LENGTH_LONG).show();
                finishAndSubmitTest();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMockTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();

        setupOptionClickListeners();
        setupNavButtons();
        startCountdownTimer();
        loadMockQuestions();
    }

    private void loadMockQuestions() {
        repository.getMockTestQuestions(10, list -> {
            if (list != null && !list.isEmpty()) {
                questions = list;
                setupPalette();
                displayQuestion(0);
            } else {
                Toast.makeText(this, "Loading mock test questions...", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupPalette() {
        paletteAdapter = new MockPaletteAdapter(questions.size(), clickedIndex -> {
            recordTimeForCurrentQuestion();
            displayQuestion(clickedIndex);
        });
        binding.rvQuestionPalette.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvQuestionPalette.setAdapter(paletteAdapter);
    }

    private void setupOptionClickListeners() {
        binding.mockOptionA.setOnClickListener(v -> selectOption(0));
        binding.mockOptionB.setOnClickListener(v -> selectOption(1));
        binding.mockOptionC.setOnClickListener(v -> selectOption(2));
        binding.mockOptionD.setOnClickListener(v -> selectOption(3));

        binding.btnClearResponse.setOnClickListener(v -> {
            selectedAnswers.remove(currentIndex);
            paletteAdapter.answerCleared(currentIndex);
            highlightSelectedOption(null);
        });
    }

    private void setupNavButtons() {
        binding.btnMockPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                recordTimeForCurrentQuestion();
                displayQuestion(currentIndex - 1);
            }
        });

        binding.btnMockNext.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                recordTimeForCurrentQuestion();
                displayQuestion(currentIndex + 1);
            }
        });

        binding.btnMockSubmit.setOnClickListener(v -> showSubmitConfirmationDialog());
    }

    private void displayQuestion(int index) {
        if (questions.isEmpty() || index < 0 || index >= questions.size()) return;

        currentIndex = index;
        paletteAdapter.selectQuestion(index);
        currentQuestionEnterTimeMs = SystemClock.elapsedRealtime();

        QuestionEntity q = questions.get(index);
        binding.tvMockSubjectTag.setText(q.getSubject() + " • " + q.getChapter());
        binding.tvMockQuestionNumber.setText("Q. " + (index + 1) + " / " + questions.size());
        binding.tvMockQuestionText.setText(q.getQuestionText());

        binding.tvMockTextA.setText(q.getOptionA());
        binding.tvMockTextB.setText(q.getOptionB());
        binding.tvMockTextC.setText(q.getOptionC());
        binding.tvMockTextD.setText(q.getOptionD());

        Integer previousChoice = selectedAnswers.get(index);
        highlightSelectedOption(previousChoice);

        binding.btnMockPrev.setEnabled(currentIndex > 0);
        binding.btnMockNext.setEnabled(currentIndex < questions.size() - 1);
    }

    private void selectOption(int optionIndex) {
        selectedAnswers.put(currentIndex, optionIndex);
        paletteAdapter.answerRecorded(currentIndex, optionIndex);
        highlightSelectedOption(optionIndex);
    }

    private void highlightSelectedOption(Integer chosenIndex) {
        LinearLayout[] layouts = new LinearLayout[]{
                binding.mockOptionA,
                binding.mockOptionB,
                binding.mockOptionC,
                binding.mockOptionD
        };

        for (int i = 0; i < layouts.length; i++) {
            if (chosenIndex != null && chosenIndex == i) {
                layouts[i].setBackgroundResource(R.drawable.bg_option_selected);
            } else {
                layouts[i].setBackgroundResource(R.drawable.bg_option_default);
            }
        }
    }

    private void recordTimeForCurrentQuestion() {
        if (currentQuestionEnterTimeMs > 0) {
            int elapsed = (int) ((SystemClock.elapsedRealtime() - currentQuestionEnterTimeMs) / 1000);
            Integer existing = timeSpentPerQuestion.get(currentIndex);
            int total = (existing != null ? existing : 0) + elapsed;
            timeSpentPerQuestion.put(currentIndex, total);
        }
    }

    private void startCountdownTimer() {
        countdownHandler.removeCallbacks(countdownRunnable);
        countdownHandler.post(countdownRunnable);
    }

    private void stopCountdownTimer() {
        countdownHandler.removeCallbacks(countdownRunnable);
    }

    private void showSubmitConfirmationDialog() {
        int answeredCount = selectedAnswers.size();
        int totalCount = questions.size();
        new AlertDialog.Builder(this)
                .setTitle("Submit Mock Test?")
                .setMessage("You have answered " + answeredCount + " of " + totalCount + " questions.\n\nNegative marking (-0.25) applies for wrong answers.")
                .setPositiveButton("Submit Now", (dialog, which) -> finishAndSubmitTest())
                .setNegativeButton("Keep Reviewing", null)
                .show();
    }

    private void finishAndSubmitTest() {
        stopCountdownTimer();
        recordTimeForCurrentQuestion();

        int totalQuestions = questions.size();
        int correctCount = 0;
        int wrongCount = 0;
        int skippedCount = 0;

        List<Long> questionIds = new ArrayList<>();
        List<Integer> selectedOptionsList = new ArrayList<>();
        List<Integer> timesList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            QuestionEntity q = questions.get(i);
            questionIds.add(q.getId());

            Integer selected = selectedAnswers.get(i);
            int sel = selected != null ? selected : -1;
            selectedOptionsList.add(sel);

            Integer spent = timeSpentPerQuestion.get(i);
            int time = spent != null ? Math.max(1, spent) : 5;
            timesList.add(time);

            if (sel == -1) {
                skippedCount++;
            } else if (sel == q.getCorrectOptionIndex()) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }

        int totalTime = (15 * 60) - countdownSeconds;

        final int finalCorrect = correctCount;
        final int finalWrong = wrongCount;
        final int finalSkipped = skippedCount;

        final double scoreAchieved = (finalCorrect * 1.0) - (finalWrong * 0.25);

        // Upload exam result to Firebase Firestore
        com.example.data.auth.UserManager userManager = new com.example.data.auth.UserManager(this);
        com.example.data.repository.FirebaseAccountRepository fbRepo = new com.example.data.repository.FirebaseAccountRepository(this);
        fbRepo.recordExamResult(
                "Engineering Admission Mock Test",
                totalQuestions, finalCorrect, finalWrong, finalSkipped,
                scoreAchieved, totalTime,
                userManager.getUserName(),
                userManager.getTargetUniversity(),
                null, null
        );

        repository.saveMockTestResult(
                "Engineering Admission Mock Test",
                totalQuestions, finalCorrect, finalWrong, finalSkipped,
                totalTime, questionIds, selectedOptionsList, timesList,
                resultId -> {
                    // Record attempts and enqueue wrong answers to Spaced Repetition queue
                    for (int i = 0; i < questions.size(); i++) {
                        QuestionEntity q = questions.get(i);
                        int sel = selectedOptionsList.get(i);
                        boolean isCorrect = sel == q.getCorrectOptionIndex();
                        int time = timesList.get(i);

                        if (sel != -1) {
                            repository.recordAttempt(q.getId(), sel, isCorrect, time, "MOCK_TEST", null);
                        }
                    }

                    Intent intent = new Intent(MockTestActivity.this, TestResultActivity.class);
                    intent.putExtra(TestResultActivity.EXTRA_RESULT_ID, resultId);
                    startActivity(intent);
                    finish();
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountdownTimer();
    }
}
