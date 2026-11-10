package com.example.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.SprintApplication;
import com.example.data.repository.FirebaseAccountRepository;
import com.example.data.repository.LeaderboardUser;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityLeaderboardBinding;
import com.example.service.StudySyncService;
import com.example.ui.adapter.LeaderboardAdapter;

import java.util.List;

/**
 * LeaderboardActivity (Java) demonstrating:
 * - Real-time global peer rankings from Firebase Firestore (Lecture 11)
 * - Starting an Android Service for background cloud backup (Lecture 10)
 * - BroadcastReceiver listening for Service completion (Lecture 10)
 * - RecyclerView for peer rankings (Lecture 9)
 */
public class LeaderboardActivity extends AppCompatActivity {

    private ActivityLeaderboardBinding binding;
    private SprintRepository repository;
    private FirebaseAccountRepository firebaseAccountRepo;

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && StudySyncService.ACTION_SYNC_COMPLETE.equals(intent.getAction())) {
                binding.pbSync.setVisibility(View.GONE);
                binding.btnSyncNow.setEnabled(true);
                binding.tvSyncStatus.setText("Cloud backup complete • Synced with Firebase");
                Toast.makeText(
                        LeaderboardActivity.this,
                        "Cloud sync successful! Refreshing live rankings.",
                        Toast.LENGTH_SHORT
                ).show();
                loadLeaderboardData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();
        firebaseAccountRepo = new FirebaseAccountRepository(this);

        binding.btnLeaderboardBack.setOnClickListener(v -> finish());

        binding.rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        setupSyncButton();
        loadLeaderboardData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        binding.pbSync.setVisibility(View.VISIBLE);
        firebaseAccountRepo.fetchRealLeaderboard(new FirebaseAccountRepository.LeaderboardCallback() {
            @Override
            public void onLoaded(List<LeaderboardUser> users) {
                runOnUiThread(() -> {
                    binding.pbSync.setVisibility(View.GONE);
                    com.example.data.auth.UserManager userManager = new com.example.data.auth.UserManager(LeaderboardActivity.this);
                    String currentUserName = userManager.isLoggedIn() ? userManager.getUserName() : null;

                    if (users != null && !users.isEmpty()) {
                        binding.rvLeaderboard.setAdapter(new LeaderboardAdapter(users));
                        binding.tvSyncStatus.setText("Live ranking of " + users.size() + " registered students");
                    } else {
                        // Personalized fallback with actual user name
                        binding.rvLeaderboard.setAdapter(new LeaderboardAdapter(repository.getLeaderboardForUser(currentUserName)));
                        binding.tvSyncStatus.setText("Admission cohort rankings");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    binding.pbSync.setVisibility(View.GONE);
                    com.example.data.auth.UserManager userManager = new com.example.data.auth.UserManager(LeaderboardActivity.this);
                    String currentUserName = userManager.isLoggedIn() ? userManager.getUserName() : null;
                    binding.rvLeaderboard.setAdapter(new LeaderboardAdapter(repository.getLeaderboardForUser(currentUserName)));
                    binding.tvSyncStatus.setText("Showing cohort rankings");
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(StudySyncService.ACTION_SYNC_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(syncReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(syncReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(syncReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void setupSyncButton() {
        binding.btnSyncNow.setOnClickListener(v -> {
            binding.pbSync.setVisibility(View.VISIBLE);
            binding.btnSyncNow.setEnabled(false);
            binding.tvSyncStatus.setText("Syncing with cloud database...");

            // Explicitly start Android Service (Lecture 10)
            StudySyncService.startSync(this);
        });
    }
}
