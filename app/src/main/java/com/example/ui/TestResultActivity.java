package com.example.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.MainActivity;
import com.example.SprintApplication;
import com.example.data.entity.QuestionEntity;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityTestResultBinding;
import com.example.ui.adapter.QuestionReviewUiModel;
import com.example.ui.adapter.TestResultReviewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * TestResultActivity (Java) demonstrating:
 * - Admission negative marking scorecard
 * - Speed per question breakdown
 * - RecyclerView list with detailed answers (Lecture 9)
 */
public class TestResultActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT_ID = "extra_result_id";

    private ActivityTestResultBinding binding;
    private SprintRepository repository;
    private long resultId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();

        if (getIntent().hasExtra(EXTRA_RESULT_ID)) {
            resultId = getIntent().getLongExtra(EXTRA_RESULT_ID, 0L);
        }

        binding.btnDoneHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        binding.btnRetakeTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, MockTestActivity.class);
            startActivity(intent);
            finish();
        });

        setupRecyclerView();
        loadTestResult();
    }

    private void setupRecyclerView() {
        binding.rvTestReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadTestResult() {
        repository.getMockTestResult(resultId, result -> {
            if (result == null) return;

            binding.tvResultScore.setText(String.format(Locale.US, "%.2f / %d.00", result.getScoreAchieved(), result.getTotalQuestions()));
            double correctMarks = result.getCorrectCount() * 1.0;
            double penaltyMarks = result.getWrongCount() * 0.25;
            binding.tvResultMarkingBreakdown.setText(
                    String.format(Locale.US, "+%.2f (Correct) - %.2f (Penalty) = %.2f", correctMarks, penaltyMarks, result.getScoreAchieved())
            );

            binding.tvResultCorrectCount.setText(String.valueOf(result.getCorrectCount()));
            binding.tvResultWrongCount.setText(String.valueOf(result.getWrongCount()));
            binding.tvResultSkippedCount.setText(String.valueOf(result.getSkippedCount()));

            int attempted = result.getCorrectCount() + result.getWrongCount();
            int accuracy = (attempted > 0) ? (int) (((double) result.getCorrectCount() / attempted) * 100) : 0;
            binding.tvResultAccuracy.setText(accuracy + "%");

            int totalSec = result.getTotalTimeSeconds();
            int min = totalSec / 60;
            int sec = totalSec % 60;
            binding.tvResultTotalTime.setText(String.format(Locale.US, "%02d:%02d", min, sec));

            int avgSpeed = (attempted > 0) ? totalSec / attempted : 0;
            binding.tvResultAvgSpeed.setText(avgSpeed + "s");

            if (result.getWrongCount() > 0) {
                binding.tvSpacedRepNotice.setText(
                        "⚡ " + result.getWrongCount() + " missed question(s) were added to your Spaced Revision Queue. They will come back in 1 day until mastered."
                );
            } else {
                binding.tvSpacedRepNotice.setText(
                        "🎉 Flawless test! Zero mistakes were added to your revision queue."
                );
            }

            // Parse question IDs and times
            List<Long> questionIds = new ArrayList<>();
            String[] qIdTokens = result.getQuestionIdsCsv().split(",");
            for (String tok : qIdTokens) {
                try {
                    questionIds.add(Long.parseLong(tok.trim()));
                } catch (NumberFormatException ignored) {}
            }

            List<Integer> selectedOptions = new ArrayList<>();
            String[] selTokens = result.getSelectedOptionsCsv().split(",");
            for (String tok : selTokens) {
                try {
                    selectedOptions.add(Integer.parseInt(tok.trim()));
                } catch (NumberFormatException ignored) {}
            }

            List<Integer> times = new ArrayList<>();
            String[] timeTokens = result.getTimesPerQuestionCsv().split(",");
            for (String tok : timeTokens) {
                try {
                    times.add(Integer.parseInt(tok.trim()));
                } catch (NumberFormatException ignored) {}
            }

            repository.getQuestionsByIds(questionIds, questionList -> {
                Map<Long, QuestionEntity> questionMap = new HashMap<>();
                if (questionList != null) {
                    for (QuestionEntity q : questionList) {
                        questionMap.put(q.getId(), q);
                    }
                }

                List<QuestionReviewUiModel> reviewModels = new ArrayList<>();
                for (int i = 0; i < questionIds.size(); i++) {
                    long qId = questionIds.get(i);
                    QuestionEntity q = questionMap.get(qId);
                    if (q == null) continue;

                    int sel = (i < selectedOptions.size()) ? selectedOptions.get(i) : -1;
                    int spent = (i < times.size()) ? times.get(i) : 0;

                    reviewModels.add(new QuestionReviewUiModel(i + 1, q, sel, spent));
                }

                binding.rvTestReviews.setAdapter(new TestResultReviewAdapter(reviewModels));
            });
        });
    }
}
