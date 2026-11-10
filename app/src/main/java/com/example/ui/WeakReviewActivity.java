package com.example.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.R;
import com.example.SprintApplication;
import com.example.data.entity.QuestionEntity;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityWeakReviewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * WeakReviewActivity (Java) demonstrating:
 * - Scientific Spaced Repetition queue for wrong questions
 * - Chapter performance and mastery progression
 */
public class WeakReviewActivity extends AppCompatActivity {

    private ActivityWeakReviewBinding binding;
    private SprintRepository repository;

    private List<QuestionEntity> dueQuestions = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isAnswerSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeakReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();

        binding.btnWeakBack.setOnClickListener(v -> finish());

        setupOptionClicks();
        setupNextButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDueReviews();
    }

    private void setupOptionClicks() {
        binding.tvReviewOptA.setOnClickListener(v -> checkOption(0));
        binding.tvReviewOptB.setOnClickListener(v -> checkOption(1));
        binding.tvReviewOptC.setOnClickListener(v -> checkOption(2));
        binding.tvReviewOptD.setOnClickListener(v -> checkOption(3));
    }

    private void setupNextButton() {
        binding.btnNextReviewItem.setOnClickListener(v -> {
            if (currentIndex < dueQuestions.size() - 1) {
                currentIndex++;
                displayCurrentQuestion();
            } else {
                Toast.makeText(this, "All pending reviews cleared! Keep up the momentum.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadDueReviews() {
        repository.getDueReviewQuestions(list -> {
            dueQuestions = list != null ? list : new ArrayList<>();
            if (dueQuestions.isEmpty()) {
                binding.tvDueCountLabel.setText("0 questions due today");
                binding.layoutEmptyQueue.setVisibility(View.VISIBLE);
                binding.layoutActiveReview.setVisibility(View.GONE);
            } else {
                binding.tvDueCountLabel.setText(dueQuestions.size() + " questions due for revision today");
                binding.layoutEmptyQueue.setVisibility(View.GONE);
                binding.layoutActiveReview.setVisibility(View.VISIBLE);
                if (currentIndex >= dueQuestions.size()) {
                    currentIndex = 0;
                }
                displayCurrentQuestion();
            }
        });
    }

    private void displayCurrentQuestion() {
        if (dueQuestions.isEmpty() || currentIndex < 0 || currentIndex >= dueQuestions.size()) return;

        QuestionEntity q = dueQuestions.get(currentIndex);
        isAnswerSubmitted = false;

        binding.tvReviewMeta.setText(q.getSubject() + " • " + q.getChapter() + " (Revision #" + (currentIndex + 1) + ")");
        binding.tvReviewQText.setText(q.getQuestionText());

        binding.tvReviewOptA.setText("A. " + q.getOptionA());
        binding.tvReviewOptB.setText("B. " + q.getOptionB());
        binding.tvReviewOptC.setText("C. " + q.getOptionC());
        binding.tvReviewOptD.setText("D. " + q.getOptionD());

        resetOptionViews();
        binding.layoutReviewSolution.setVisibility(View.GONE);
        binding.btnNextReviewItem.setVisibility(View.GONE);
    }

    private void checkOption(int selected) {
        if (isAnswerSubmitted || dueQuestions.isEmpty()) return;
        isAnswerSubmitted = true;

        QuestionEntity q = dueQuestions.get(currentIndex);
        boolean isCorrect = selected == q.getCorrectOptionIndex();

        TextView[] optionViews = new TextView[]{
                binding.tvReviewOptA,
                binding.tvReviewOptB,
                binding.tvReviewOptC,
                binding.tvReviewOptD
        };

        TextView selectedView = optionViews[selected];
        TextView correctView = optionViews[q.getCorrectOptionIndex()];

        if (isCorrect) {
            selectedView.setBackgroundResource(R.drawable.bg_option_correct);
            binding.tvReviewSolutionStatus.setText("🎉 Correct! Mastery interval increased.");
            binding.tvReviewSolutionStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else {
            selectedView.setBackgroundResource(R.drawable.bg_option_wrong);
            correctView.setBackgroundResource(R.drawable.bg_option_correct);
            binding.tvReviewSolutionStatus.setText("❌ Still incorrect. Will resurface tomorrow for practice.");
            binding.tvReviewSolutionStatus.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        }

        String formulaPart = (q.getFormula() != null && !q.getFormula().trim().isEmpty())
                ? "\nFormula: " + q.getFormula() : "";
        binding.tvReviewSolutionExplanation.setText("Solution:\n" + q.getExplanation() + formulaPart);
        binding.layoutReviewSolution.setVisibility(View.VISIBLE);
        binding.btnNextReviewItem.setVisibility(View.VISIBLE);

        repository.recordAttempt(q.getId(), selected, isCorrect, 15, "SPACED_REVISION", null);
    }

    private void resetOptionViews() {
        binding.tvReviewOptA.setBackgroundResource(R.drawable.bg_option_default);
        binding.tvReviewOptB.setBackgroundResource(R.drawable.bg_option_default);
        binding.tvReviewOptC.setBackgroundResource(R.drawable.bg_option_default);
        binding.tvReviewOptD.setBackgroundResource(R.drawable.bg_option_default);
    }
}
