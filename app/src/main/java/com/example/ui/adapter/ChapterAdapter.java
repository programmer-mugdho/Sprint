package com.example.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemChapterCardBinding;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    public interface OnChapterClickListener {
        void onChapterClick(ChapterUiModel chapter);
    }

    private final List<ChapterUiModel> chapters;
    private final OnChapterClickListener listener;

    public ChapterAdapter(List<ChapterUiModel> chapters, OnChapterClickListener listener) {
        this.chapters = chapters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterCardBinding binding = ItemChapterCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ChapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.bind(chapters.get(position));
    }

    @Override
    public int getItemCount() {
        return chapters != null ? chapters.size() : 0;
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder {
        private final ItemChapterCardBinding binding;

        public ChapterViewHolder(ItemChapterCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChapterUiModel item) {
            binding.tvChapterName.setText(item.getChapterName());
            binding.tvChapterStats.setText(item.getQuestionCount() + " Questions • " + item.getAttemptsCount() + " Attempts");

            if (item.getAttemptsCount() > 0) {
                binding.tvChapterStatus.setText("Mastery: " + item.getAccuracyPercent() + "%");
                binding.progressChapterAccuracy.setProgress(item.getAccuracyPercent());
            } else {
                binding.tvChapterStatus.setText("Not Attempted");
                binding.progressChapterAccuracy.setProgress(0);
            }

            binding.btnPracticeChapter.setOnClickListener(v -> {
                if (listener != null) listener.onChapterClick(item);
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onChapterClick(item);
            });
        }
    }
}
