package com.example;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityMainBinding;
import com.example.receiver.ReminderScheduler;
import com.example.ui.ChapterListActivity;
import com.example.ui.LeaderboardActivity;
import com.example.ui.MockTestActivity;
import com.example.ui.PracticeActivity;
import com.example.ui.WeakReviewActivity;
import com.example.ui.adapter.SubjectAdapter;
import com.example.ui.adapter.SubjectUiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity (Java) demonstrating:
 * - Activity Lifecycle callbacks (Lecture 8)
 * - XML layout & ViewBinding / Click Listeners (Lecture 8)
 * - Explicit Intents (Lecture 8)
 * - Scalable RecyclerView & custom item layouts (Lecture 9)
 * - Component communication & Study Reminders (Lecture 10)
 * - Data handling with Room repository (Lecture 11)
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private SprintRepository repository;
    private com.example.data.repository.FirebaseAccountRepository firebaseAccountRepo;

    private String firstWeakChapter = null;
    private String firstWeakSubject = "Physics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity lifecycle initiated");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();
        firebaseAccountRepo = new com.example.data.repository.FirebaseAccountRepository(this);

        setupSubjectsRecyclerView();
        setupClickListeners();
        requestNotificationPermissionIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity visible");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Updating dashboard metrics");
        loadDashboardData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity destroyed");
    }

    private void setupSubjectsRecyclerView() {
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(this));

        List<SubjectUiModel> list = new ArrayList<>();
        list.add(new SubjectUiModel("Physics", "Mechanics, Electricity, Waves, Thermodynamics", "Drill", R.color.physics_color));
        list.add(new SubjectUiModel("Chemistry", "Atomic Structure, Bonding, Equilibrium, Organic", "Drill", R.color.chemistry_color));
        list.add(new SubjectUiModel("Higher Math", "Differential & Integral Calculus, Geometry", "Drill", R.color.math_color));
        list.add(new SubjectUiModel("English", "Grammar, Prepositions, Vocabulary, Analogies", "Drill", R.color.english_color));

        binding.rvSubjects.setAdapter(new SubjectAdapter(list, subject -> {
            // Explicit Intent launching ChapterListActivity (Lecture 8)
            Intent intent = new Intent(MainActivity.this, ChapterListActivity.class);
            intent.putExtra(ChapterListActivity.EXTRA_SUBJECT, subject.getName());
            startActivity(intent);
        }));
    }

    private void setupClickListeners() {
        // Timed Mock Test
        binding.btnStartMockTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MockTestActivity.class);
            startActivity(intent);
        });
        binding.cardStartMockTest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MockTestActivity.class);
            startActivity(intent);
        });

        // Spaced Repetition Revision Queue
        binding.cardRevisionQueue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeakReviewActivity.class);
            startActivity(intent);
        });

        // Weekly Leaderboard & Cloud Sync
        binding.cardLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        // Test Notification Dispatcher (Lecture 10)
        binding.btnTestReminder.setOnClickListener(v -> {
            ReminderScheduler.triggerTestNotificationNow(this);
            Toast.makeText(this, "Daily study reminder notification posted!", Toast.LENGTH_SHORT).show();
        });

        // Student Profile / Account Sign In
        binding.btnProfile.setOnClickListener(v -> {
            com.example.data.auth.UserManager userManager = new com.example.data.auth.UserManager(this);
            if (userManager.isLoggedIn()) {
                startActivity(new Intent(MainActivity.this, com.example.ui.ProfileActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, com.example.ui.LoginActivity.class));
            }
        });

        // Weak Chapters Drill Button
        binding.btnDrillWeak.setOnClickListener(v -> {
            if (firstWeakChapter != null) {
                Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
                intent.putExtra(PracticeActivity.EXTRA_SUBJECT, firstWeakSubject);
                intent.putExtra(PracticeActivity.EXTRA_CHAPTER, firstWeakChapter);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, WeakReviewActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadDashboardData() {
        com.example.data.auth.UserManager userManager = new com.example.data.auth.UserManager(this);
        if (userManager.isLoggedIn() && firebaseAccountRepo.isUserSignedIn()) {
            firebaseAccountRepo.syncUserProgress(repository, this::refreshDashboardUi);
        } else {
            refreshDashboardUi();
        }
    }

    private void refreshDashboardUi() {
        // Load overall performance
        repository.getOverallPerformance(perf -> {
            binding.tvTotalSolved.setText(String.valueOf(perf.getTotalSolved()));
            binding.tvAccuracyPercent.setText(perf.getAccuracyPercent() + "%");
            binding.tvAvgSpeed.setText(String.format(Locale.US, "%.0fs", perf.getAverageTimeSeconds()));
        });

        // Load due spaced review count
        repository.getPendingReviewCount(count -> {
            binding.tvRevisionQueueCount.setText(String.valueOf(count));
            binding.tvRevisionQueueSub.setText(count + " questions due");
        });

        // Detect weak chapters (<60% accuracy)
        repository.getWeakChapters(weakChapters -> {
            if (weakChapters != null && !weakChapters.isEmpty()) {
                firstWeakChapter = weakChapters.get(0);
                firstWeakSubject = "Physics";
                binding.cardWeakAlert.setVisibility(View.VISIBLE);
                binding.tvWeakChaptersLabel.setText("Focus needed: " + String.join(", ", weakChapters) + ". Tap to reinforce!");
            } else {
                binding.cardWeakAlert.setVisibility(View.GONE);
            }
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }
    }
}
