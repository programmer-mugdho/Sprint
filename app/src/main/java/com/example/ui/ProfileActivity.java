package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.SprintApplication;
import com.example.data.auth.UserManager;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityProfileBinding;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserManager userManager;
    private SprintRepository repository;
    private com.example.data.repository.FirebaseAccountRepository firebaseAccountRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);
        repository = ((SprintApplication) getApplication()).getRepository();
        firebaseAccountRepo = new com.example.data.repository.FirebaseAccountRepository(this);

        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        if (!userManager.isLoggedIn()) {
            binding.tvUserName.setText("Guest Student");
            binding.tvUserEmail.setText("Not signed in");
            binding.chipTargetUni.setText("Sign in to track target university");
            binding.btnLogout.setText("Sign In / Create Account");
        } else {
            binding.tvUserName.setText(userManager.getUserName());
            binding.tvUserEmail.setText(userManager.getUserEmail());
            binding.chipTargetUni.setText(userManager.getAvatarBadge() + " • " + userManager.getTargetUniversity());
            binding.btnLogout.setText("Sign Out");
        }

        if (userManager.isLoggedIn() && firebaseAccountRepo.isUserSignedIn()) {
            firebaseAccountRepo.syncUserProgress(repository, this::refreshProfileMetrics);
        } else {
            refreshProfileMetrics();
        }
    }

    private void refreshProfileMetrics() {
        // Load repository overall performance asynchronously
        repository.getOverallPerformance(perf -> {
            runOnUiThread(() -> {
                if (perf != null) {
                    binding.tvStatSolved.setText(String.valueOf(perf.getTotalSolved()));
                    binding.tvStatAccuracy.setText(perf.getAccuracyPercent() + "%");
                } else {
                    binding.tvStatSolved.setText("0");
                    binding.tvStatAccuracy.setText("0%");
                }
            });
        });

        // Load completed mock tests count
        repository.getCompletedMockTestCount(count -> {
            runOnUiThread(() -> {
                binding.tvStatMockCount.setText(String.valueOf(count));
            });
        });
    }

    private void setupListeners() {
        binding.btnEditProfile.setOnClickListener(v -> {
            if (!userManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, EditProfileActivity.class));
            }
        });

        binding.btnViewLeaderboard.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaderboardActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            if (userManager.isLoggedIn()) {
                userManager.logout();
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                repository.clearUserData(null);
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
    }
}
