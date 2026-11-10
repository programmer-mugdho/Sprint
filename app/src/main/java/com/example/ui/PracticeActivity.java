package com.example.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.R;
import com.example.SprintApplication;
import com.example.data.entity.QuestionEntity;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityPracticeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * PracticeActivity (Java) demonstrating:
 * - Instant feedback and explanation revelation
 * - Handlers and timed operations (Lecture 10)
 * - Automatic Spaced Repetition queue integration
 * - Bookmarking mechanism
 */
public class PracticeActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "extra_subject";
    public static final String EXTRA_CHAPTER = "extra_chapter";

    private ActivityPracticeBinding binding;
    private SprintRepository repository;

    private String subjectName = "Physics";
    private String chapterName = "Mechanics";

    private List<QuestionEntity> questions = new ArrayList<>();
    private int currentIndex = 0;

    private boolean isAnswerSubmitted = false;
    private long questionStartTimeMs = 0;
    private boolean isBookmarkedCurrent = false;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (questionStartTimeMs > 0) {
                long elapsedSec = (SystemClock.elapsedRealtime() - questionStartTimeMs) / 1000;
                long min = elapsedSec / 60;
                long sec = elapsedSec % 60;
                binding.tvTimer.setText(String.format(Locale.US, "%02d:%02d", min, sec));
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();

        if (getIntent().hasExtra(EXTRA_SUBJECT)) {
            subjectName = getIntent().getStringExtra(EXTRA_SUBJECT);
        }
        if (getIntent().hasExtra(EXTRA_CHAPTER)) {
            chapterName = getIntent().getStringExtra(EXTRA_CHAPTER);
        }

        binding.tvPracticeChapterTitle.setText(chapterName + " Practice");
        binding.btnBack.setOnClickListener(v -> finish());

        setupOptionClickListeners();
        setupNavButtons();
        setupBookmarkButton();
        loadQuestions();
    }

    private void setupOptionClickListeners() {
        binding.layoutOptionA.setOnClickListener(v -> onOptionSelected(0));
        binding.layoutOptionB.setOnClickListener(v -> onOptionSelected(1));
        binding.layoutOptionC.setOnClickListener(v -> onOptionSelected(2));
        binding.layoutOptionD.setOnClickListener(v -> onOptionSelected(3));
    }

    private void setupNavButtons() {
        binding.btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayCurrentQuestion();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                displayCurrentQuestion();
            } else {
                Toast.makeText(this, "Chapter practice completed! Great effort.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupBookmarkButton() {
        binding.btnBookmark.setOnClickListener(v -> {
            if (questions.isEmpty() || currentIndex >= questions.size()) return;
            QuestionEntity q = questions.get(currentIndex);
            repository.toggleBookmark(q.getId(), isBookmarked -> {
                isBookmarkedCurrent = isBookmarked;
                updateBookmarkIcon(isBookmarked);
                String msg = isBookmarked ? "Question bookmarked" : "Bookmark removed";
                Toast.makeText(PracticeActivity.this, msg, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void loadQuestions() {
        repository.getQuestionsByChapter(subjectName, chapterName, list -> {
            if (list != null && !list.isEmpty()) {
                questions = list;
                currentIndex = 0;
                displayCurrentQuestion();
            } else {
                repository.getQuestionsBySubject(subjectName, fallbackList -> {
                    questions = fallbackList != null ? fallbackList : new ArrayList<>();
                    currentIndex = 0;
                    displayCurrentQuestion();
                });
            }
        });
    }

    private void displayCurrentQuestion() {
        if (questions.isEmpty()) return;

        QuestionEntity q = questions.get(currentIndex);
        isAnswerSubmitted = false;
        questionStartTimeMs = SystemClock.elapsedRealtime();

        startTimer();

        binding.tvPracticeProgress.setText("Question " + (currentIndex + 1) + " of " + questions.size());
        binding.tvTagSubject.setText(q.getSubject());
        binding.tvTagDifficulty.setText("Difficulty: " + q.getDifficulty());
        binding.tvQuestionText.setText(q.getQuestionText());

        if (q.getFormula() != null && !q.getFormula().trim().isEmpty()) {
            binding.tvFormulaHint.setVisibility(View.VISIBLE);
            binding.tvFormulaHint.setText("Formula: " + q.getFormula());
        } else {
            binding.tvFormulaHint.setVisibility(View.GONE);
        }

        binding.tvTextOptionA.setText(q.getOptionA());
        binding.tvTextOptionB.setText(q.getOptionB());
        binding.tvTextOptionC.setText(q.getOptionC());
        binding.tvTextOptionD.setText(q.getOptionD());

        resetOptionStyles();
        binding.cardExplanation.setVisibility(View.GONE);

        binding.btnPrev.setEnabled(currentIndex > 0);
        binding.btnNext.setText(currentIndex == questions.size() - 1 ? "FINISH PRACTICE" : "NEXT QUESTION");

        repository.isBookmarked(q.getId(), bookmarked -> {
            isBookmarkedCurrent = bookmarked;
            updateBookmarkIcon(bookmarked);
        });
    }

    private void onOptionSelected(int optionIndex) {
        if (isAnswerSubmitted || questions.isEmpty()) return;
        isAnswerSubmitted = true;
        stopTimer();

        QuestionEntity q = questions.get(currentIndex);
        int timeSpent = (int) Math.max(1, (SystemClock.elapsedRealtime() - questionStartTimeMs) / 1000);
        boolean isCorrect = optionIndex == q.getCorrectOptionIndex();

        LinearLayout[] optionLayouts = new LinearLayout[]{
                binding.layoutOptionA,
                binding.layoutOptionB,
                binding.layoutOptionC,
                binding.layoutOptionD
        };

        LinearLayout correctLayout = optionLayouts[q.getCorrectOptionIndex()];
        LinearLayout selectedLayout = optionLayouts[optionIndex];

        if (isCorrect) {
            selectedLayout.setBackgroundResource(R.drawable.bg_option_correct);
            binding.ivExplanationStatusIcon.setImageResource(R.drawable.ic_check_circle);
            binding.tvExplanationStatus.setText("Correct! (+1.0 Mark)");
            binding.tvExplanationStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else {
            selectedLayout.setBackgroundResource(R.drawable.bg_option_wrong);
            correctLayout.setBackgroundResource(R.drawable.bg_option_correct);
            binding.ivExplanationStatusIcon.setImageResource(R.drawable.ic_cancel_circle);
            binding.tvExplanationStatus.setText("Incorrect (-0.25 Mark). Queued for Spaced Review!");
            binding.tvExplanationStatus.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        }

        String formulaPart = (q.getFormula() != null && !q.getFormula().trim().isEmpty())
                ? "\nFormula applied: " + q.getFormula() : "";
        binding.tvExplanationText.setText("Solution:\n" + q.getExplanation() + formulaPart);
        binding.cardExplanation.setVisibility(View.VISIBLE);

        // Record attempt in database
        repository.recordAttempt(q.getId(), optionIndex, isCorrect, timeSpent, "PRACTICE", null);
    }

    private void resetOptionStyles() {
        binding.layoutOptionA.setBackgroundResource(R.drawable.bg_option_default);
        binding.layoutOptionB.setBackgroundResource(R.drawable.bg_option_default);
        binding.layoutOptionC.setBackgroundResource(R.drawable.bg_option_default);
        binding.layoutOptionD.setBackgroundResource(R.drawable.bg_option_default);
    }

    private void updateBookmarkIcon(boolean isBookmarked) {
        binding.btnBookmark.setImageResource(
                isBookmarked ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark
        );
    }

    private void startTimer() {
        stopTimer();
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}
