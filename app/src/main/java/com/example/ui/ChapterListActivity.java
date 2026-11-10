package com.example.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.SprintApplication;
import com.example.data.dao.ChapterStats;
import com.example.data.entity.QuestionEntity;
import com.example.data.repository.SprintRepository;
import com.example.databinding.ActivityChapterListBinding;
import com.example.ui.adapter.ChapterAdapter;
import com.example.ui.adapter.ChapterUiModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ChapterListActivity (Java) demonstrating:
 * - Intent Extra extraction (Lecture 8)
 * - RecyclerView with custom adapters (Lecture 9)
 * - Room data operations (Lecture 11)
 */
public class ChapterListActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "extra_subject";

    private ActivityChapterListBinding binding;
    private SprintRepository repository;
    private String subjectName = "Physics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChapterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = ((SprintApplication) getApplication()).getRepository();

        if (getIntent().hasExtra(EXTRA_SUBJECT)) {
            subjectName = getIntent().getStringExtra(EXTRA_SUBJECT);
        }

        binding.tvSubjectTitle.setText(subjectName);
        binding.tvSubjectSubtitle.setText(subjectName + " • Select a chapter to practice questions with instant solutions");
        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChapters();
    }

    private void setupRecyclerView() {
        binding.rvChapters.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadChapters() {
        repository.getQuestionsBySubject(subjectName, questions -> {
            repository.getChapterPerformanceForSubject(subjectName, statsList -> {
                Map<String, ChapterStats> statsMap = new HashMap<>();
                if (statsList != null) {
                    for (ChapterStats s : statsList) {
                        statsMap.put(s.chapter, s);
                    }
                }

                Set<String> seen = new HashSet<>();
                List<String> distinctChapters = new ArrayList<>();
                for (QuestionEntity q : questions) {
                    if (seen.add(q.getChapter())) {
                        distinctChapters.add(q.getChapter());
                    }
                }

                List<ChapterUiModel> uiModels = new ArrayList<>();
                for (String chap : distinctChapters) {
                    int qCount = 0;
                    for (QuestionEntity q : questions) {
                        if (chap.equals(q.getChapter())) qCount++;
                    }

                    ChapterStats s = statsMap.get(chap);
                    int attempts = s != null ? s.totalAttempts : 0;
                    int correct = s != null ? s.correctAttempts : 0;
                    int accuracy = (attempts > 0) ? (int) (((double) correct / attempts) * 100) : 0;
                    int avgSpeed = s != null ? (int) s.avgTimeSeconds : 0;

                    uiModels.add(new ChapterUiModel(chap, subjectName, qCount, attempts, accuracy, avgSpeed));
                }

                binding.rvChapters.setAdapter(new ChapterAdapter(uiModels, selectedChapter -> {
                    // Explicit Intent to PracticeActivity (Lecture 8)
                    Intent intent = new Intent(ChapterListActivity.this, PracticeActivity.class);
                    intent.putExtra(PracticeActivity.EXTRA_SUBJECT, subjectName);
                    intent.putExtra(PracticeActivity.EXTRA_CHAPTER, selectedChapter.getChapterName());
                    startActivity(intent);
                }));
            });
        });
    }
}
