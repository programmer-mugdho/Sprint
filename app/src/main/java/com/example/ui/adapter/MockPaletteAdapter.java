package com.example.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.databinding.ItemMockQuestionPaletteBinding;

import java.util.HashMap;
import java.util.Map;

public class MockPaletteAdapter extends RecyclerView.Adapter<MockPaletteAdapter.PaletteViewHolder> {

    public interface OnPaletteClickListener {
        void onPaletteClick(int index);
    }

    private final int totalQuestions;
    private final OnPaletteClickListener listener;
    private int currentQuestionIndex = 0;
    private final Map<Integer, Integer> userAnswers = new HashMap<>();

    public MockPaletteAdapter(int totalQuestions, OnPaletteClickListener listener) {
        this.totalQuestions = totalQuestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMockQuestionPaletteBinding binding = ItemMockQuestionPaletteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new PaletteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PaletteViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return totalQuestions;
    }

    public void selectQuestion(int index) {
        int prev = currentQuestionIndex;
        currentQuestionIndex = index;
        notifyItemChanged(prev);
        notifyItemChanged(currentQuestionIndex);
    }

    public void answerRecorded(int index, int option) {
        userAnswers.put(index, option);
        notifyItemChanged(index);
    }

    public void answerCleared(int index) {
        userAnswers.remove(index);
        notifyItemChanged(index);
    }

    class PaletteViewHolder extends RecyclerView.ViewHolder {
        private final ItemMockQuestionPaletteBinding binding;

        public PaletteViewHolder(ItemMockQuestionPaletteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.tvPaletteNumber.setText(String.valueOf(position + 1));
            Context context = binding.getRoot().getContext();

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);

            if (position == currentQuestionIndex) {
                drawable.setColor(ContextCompat.getColor(context, R.color.primary));
                binding.tvPaletteNumber.setTextColor(Color.WHITE);
            } else if (userAnswers.containsKey(position)) {
                drawable.setColor(ContextCompat.getColor(context, R.color.success_green));
                binding.tvPaletteNumber.setTextColor(Color.WHITE);
            } else {
                drawable.setColor(ContextCompat.getColor(context, R.color.chip_bg));
                binding.tvPaletteNumber.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            }

            binding.tvPaletteNumber.setBackground(drawable);
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onPaletteClick(position);
            });
        }
    }
}
