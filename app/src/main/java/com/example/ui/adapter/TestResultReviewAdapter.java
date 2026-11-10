package com.example.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.data.entity.QuestionEntity;
import com.example.databinding.ItemResultReviewBinding;

import java.util.List;

public class TestResultReviewAdapter extends RecyclerView.Adapter<TestResultReviewAdapter.ReviewViewHolder> {

    private final List<QuestionReviewUiModel> items;

    public TestResultReviewAdapter(List<QuestionReviewUiModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResultReviewBinding binding = ItemResultReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ItemResultReviewBinding binding;

        public ReviewViewHolder(ItemResultReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuestionReviewUiModel item) {
            QuestionEntity q = item.getQuestion();
            binding.tvReviewQuestionIdx.setText("Question " + item.getIndex() + " • " + q.getSubject() + " (" + q.getChapter() + ")");
            binding.tvReviewTimeSpent.setText("Time: " + item.getTimeSpentSeconds() + "s");

            boolean isCorrect = item.getUserSelectedOption() == q.getCorrectOptionIndex();
            boolean isSkipped = item.getUserSelectedOption() < 0;

            Context context = binding.getRoot().getContext();
            if (isSkipped) {
                binding.tvReviewMarkBadge.setText("0.00 (Skipped)");
                binding.tvReviewMarkBadge.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                binding.tvReviewUserAnswer.setText("Your Answer: None (Skipped)");
                binding.tvReviewUserAnswer.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            } else if (isCorrect) {
                binding.tvReviewMarkBadge.setText("+1.00 Mark");
                binding.tvReviewMarkBadge.setTextColor(ContextCompat.getColor(context, R.color.success_green));
                char optLetter = (char) ('A' + item.getUserSelectedOption());
                String optText = getOptionText(q, item.getUserSelectedOption());
                binding.tvReviewUserAnswer.setText("Your Answer: " + optLetter + ". " + optText + " (Correct)");
                binding.tvReviewUserAnswer.setTextColor(ContextCompat.getColor(context, R.color.success_green));
            } else {
                binding.tvReviewMarkBadge.setText("-0.25 Mark");
                binding.tvReviewMarkBadge.setTextColor(ContextCompat.getColor(context, R.color.error_red));
                char optLetter = (char) ('A' + item.getUserSelectedOption());
                String optText = getOptionText(q, item.getUserSelectedOption());
                binding.tvReviewUserAnswer.setText("Your Answer: " + optLetter + ". " + optText + " (Incorrect)");
                binding.tvReviewUserAnswer.setTextColor(ContextCompat.getColor(context, R.color.error_red));
            }

            char correctLetter = (char) ('A' + q.getCorrectOptionIndex());
            String correctText = getOptionText(q, q.getCorrectOptionIndex());
            binding.tvReviewCorrectAnswer.setText("Correct Answer: " + correctLetter + ". " + correctText);

            binding.tvReviewQuestionText.setText(q.getQuestionText());
            String formulaPart = (q.getFormula() != null && !q.getFormula().trim().isEmpty())
                    ? "\nFormula: " + q.getFormula() : "";
            binding.tvReviewExplanation.setText("Solution:\n" + q.getExplanation() + formulaPart);
        }

        private String getOptionText(QuestionEntity q, int index) {
            switch (index) {
                case 0: return q.getOptionA();
                case 1: return q.getOptionB();
                case 2: return q.getOptionC();
                case 3: return q.getOptionD();
                default: return "";
            }
        }
    }
}
