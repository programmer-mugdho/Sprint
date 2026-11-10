package com.example.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databinding.ItemSubjectCardBinding;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    public interface OnSubjectClickListener {
        void onSubjectClick(SubjectUiModel subject);
    }

    private final List<SubjectUiModel> items;
    private final OnSubjectClickListener listener;

    public SubjectAdapter(List<SubjectUiModel> items, OnSubjectClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSubjectCardBinding binding = ItemSubjectCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private final ItemSubjectCardBinding binding;

        public SubjectViewHolder(ItemSubjectCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SubjectUiModel item) {
            binding.tvSubjectName.setText(item.getName());
            binding.tvSubjectDetails.setText(item.getDetails());
            binding.tvSubjectAccuracy.setText(item.getAccuracy());
            binding.viewSubjectAccent.setBackgroundColor(
                    ContextCompat.getColor(binding.getRoot().getContext(), item.getColorRes())
            );

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubjectClick(item);
                }
            });
        }
    }
}
