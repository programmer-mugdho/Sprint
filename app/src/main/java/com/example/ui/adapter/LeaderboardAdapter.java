package com.example.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.data.repository.LeaderboardUser;
import com.example.databinding.ItemLeaderboardEntryBinding;

import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final List<LeaderboardUser> users;

    public LeaderboardAdapter(List<LeaderboardUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLeaderboardEntryBinding binding = ItemLeaderboardEntryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new LeaderboardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        private final ItemLeaderboardEntryBinding binding;

        public LeaderboardViewHolder(ItemLeaderboardEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(LeaderboardUser user) {
            Context context = binding.getRoot().getContext();
            binding.tvRank.setText("#" + user.getRank());
            binding.tvStudentName.setText(user.getName());
            binding.tvStudentCollege.setText(user.getCollege());
            binding.tvStudentScore.setText(String.format(Locale.US, "%.2f pts", user.getTotalScore()));
            binding.tvStudentAccuracy.setText(user.getAccuracy() + "% Acc");

            if (user.isCurrentUser()) {
                binding.cardLeaderboardItem.setCardBackgroundColor(Color.parseColor("#E8F0FE"));
                binding.cardLeaderboardItem.setStrokeColor(ContextCompat.getColor(context, R.color.primary_electric));
                binding.cardLeaderboardItem.setStrokeWidth(3);
                binding.tvStudentName.setTextColor(ContextCompat.getColor(context, R.color.primary));
            } else {
                binding.cardLeaderboardItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_surface));
                binding.cardLeaderboardItem.setStrokeColor(ContextCompat.getColor(context, R.color.border_subtle));
                binding.cardLeaderboardItem.setStrokeWidth(1);
                binding.tvStudentName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            }

            switch (user.getRank()) {
                case 1:
                    binding.tvRank.setTextColor(Color.parseColor("#B78103"));
                    binding.tvRank.setBackgroundColor(Color.parseColor("#FEF3C7"));
                    break;
                case 2:
                    binding.tvRank.setTextColor(Color.parseColor("#475569"));
                    binding.tvRank.setBackgroundColor(Color.parseColor("#E2E8F0"));
                    break;
                case 3:
                    binding.tvRank.setTextColor(Color.parseColor("#92400E"));
                    binding.tvRank.setBackgroundColor(Color.parseColor("#FFEDD5"));
                    break;
                default:
                    binding.tvRank.setTextColor(ContextCompat.getColor(context, R.color.primary));
                    binding.tvRank.setBackgroundColor(ContextCompat.getColor(context, R.color.chip_bg));
                    break;
            }
        }
    }
}
