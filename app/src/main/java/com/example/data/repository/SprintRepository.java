package com.example.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.example.data.dao.BookmarkDao;
import com.example.data.dao.ChapterStats;
import com.example.data.dao.MockTestResultDao;
import com.example.data.dao.QuestionAttemptDao;
import com.example.data.dao.QuestionDao;
import com.example.data.dao.SpacedReviewDao;
import com.example.data.database.SprintDatabase;
import com.example.data.entity.BookmarkEntity;
import com.example.data.entity.MockTestResultEntity;
import com.example.data.entity.QuestionAttemptEntity;
import com.example.data.entity.QuestionEntity;
import com.example.data.entity.SpacedReviewEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SprintRepository {

    public interface RepositoryCallback<T> {
        void onResult(T result);
    }

    private final QuestionDao questionDao;
    private final QuestionAttemptDao attemptDao;
    private final SpacedReviewDao reviewDao;
    private final MockTestResultDao testDao;
    private final BookmarkDao bookmarkDao;
    private final ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public SprintRepository(SprintDatabase database) {
        this.questionDao = database.questionDao();
        this.attemptDao = database.questionAttemptDao();
        this.reviewDao = database.spacedReviewDao();
        this.testDao = database.mockTestResultDao();
        this.bookmarkDao = database.bookmarkDao();
        this.executor = SprintDatabase.databaseWriteExecutor;
    }

    public void getQuestionsBySubject(String subject, RepositoryCallback<List<QuestionEntity>> callback) {
        executor.execute(() -> {
            List<QuestionEntity> result = questionDao.getQuestionsBySubjectSync(subject);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getQuestionsByChapter(String subject, String chapter, RepositoryCallback<List<QuestionEntity>> callback) {
        executor.execute(() -> {
            List<QuestionEntity> result = questionDao.getQuestionsByChapterSync(subject, chapter);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getChaptersBySubject(String subject, RepositoryCallback<List<String>> callback) {
        executor.execute(() -> {
            List<String> result = questionDao.getChaptersBySubjectSync(subject);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getMockTestQuestions(int count, RepositoryCallback<List<QuestionEntity>> callback) {
        executor.execute(() -> {
            List<QuestionEntity> result = questionDao.getRandomQuestions(count);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getQuestionsByIds(List<Long> ids, RepositoryCallback<List<QuestionEntity>> callback) {
        executor.execute(() -> {
            List<QuestionEntity> result = questionDao.getQuestionsByIds(ids);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void recordAttempt(long questionId, int selectedOption, boolean isCorrect,
                              int timeSpentSeconds, String sessionType, Runnable onComplete) {
        executor.execute(() -> {
            QuestionAttemptEntity attempt = new QuestionAttemptEntity(
                    questionId, selectedOption, isCorrect, timeSpentSeconds, sessionType
            );
            attemptDao.insertAttempt(attempt);

            // Handle Spaced Repetition queue
            SpacedReviewEntity existing = reviewDao.getReviewItem(questionId);
            long oneDayMs = 24 * 60 * 60 * 1000L;

            if (!isCorrect) {
                if (existing != null) {
                    existing.setIntervalDays(1);
                    existing.setNextReviewTimestamp(System.currentTimeMillis() + oneDayMs);
                    existing.setTimesWrong(existing.getTimesWrong() + 1);
                    existing.setMastered(false);
                    existing.setLastReviewedTimestamp(System.currentTimeMillis());
                    reviewDao.upsertReview(existing);
                } else {
                    SpacedReviewEntity review = new SpacedReviewEntity(
                            questionId, 1, System.currentTimeMillis() + oneDayMs,
                            1, 0, false, System.currentTimeMillis()
                    );
                    reviewDao.upsertReview(review);
                }
            } else if (existing != null && !existing.isMastered()) {
                int newCorrect = existing.getTimesCorrect() + 1;
                existing.setTimesCorrect(newCorrect);
                existing.setLastReviewedTimestamp(System.currentTimeMillis());

                if (newCorrect >= 3) {
                    existing.setMastered(true);
                    reviewDao.markMastered(questionId);
                } else {
                    int nextInterval = (existing.getIntervalDays() == 1) ? 3 : 7;
                    existing.setIntervalDays(nextInterval);
                    existing.setNextReviewTimestamp(System.currentTimeMillis() + (nextInterval * oneDayMs));
                    reviewDao.upsertReview(existing);
                }
            }

            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void getDueReviewQuestions(RepositoryCallback<List<QuestionEntity>> callback) {
        executor.execute(() -> {
            List<QuestionEntity> result = reviewDao.getDueReviewQuestionsSync();
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void getPendingReviewCount(RepositoryCallback<Integer> callback) {
        executor.execute(() -> {
            int count = reviewDao.getPendingReviewCountSync();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    public void saveMockTestResult(String title, int totalQuestions, int correctCount,
                                   int wrongCount, int skippedCount, int totalTimeSeconds,
                                   List<Long> questionIds, List<Integer> selectedOptions,
                                   List<Integer> timesPerQuestion, RepositoryCallback<Long> callback) {
        executor.execute(() -> {
            // Standard Engineering Admission Scoring: +1.0 for correct, -0.25 for wrong
            double score = (correctCount * 1.0) - (wrongCount * 0.25);

            StringBuilder qIdsStr = new StringBuilder();
            for (int i = 0; i < questionIds.size(); i++) {
                if (i > 0) qIdsStr.append(",");
                qIdsStr.append(questionIds.get(i));
            }

            StringBuilder selStr = new StringBuilder();
            for (int i = 0; i < selectedOptions.size(); i++) {
                if (i > 0) selStr.append(",");
                selStr.append(selectedOptions.get(i));
            }

            StringBuilder timeStr = new StringBuilder();
            for (int i = 0; i < timesPerQuestion.size(); i++) {
                if (i > 0) timeStr.append(",");
                timeStr.append(timesPerQuestion.get(i));
            }

            MockTestResultEntity result = new MockTestResultEntity(
                    title, totalQuestions, correctCount, wrongCount, skippedCount,
                    score, totalTimeSeconds, qIdsStr.toString(), selStr.toString(), timeStr.toString()
            );

            long insertedId = testDao.insertResult(result);
            mainHandler.post(() -> callback.onResult(insertedId));
        });
    }

    public void getMockTestResult(long id, RepositoryCallback<MockTestResultEntity> callback) {
        executor.execute(() -> {
            MockTestResultEntity result = testDao.getResultById(id);
            mainHandler.post(() -> callback.onResult(result));
        });
    }

    public void toggleBookmark(long questionId, RepositoryCallback<Boolean> callback) {
        executor.execute(() -> {
            boolean isBookmarked = bookmarkDao.isBookmarkedNow(questionId);
            if (isBookmarked) {
                bookmarkDao.removeBookmark(questionId);
                mainHandler.post(() -> callback.onResult(false));
            } else {
                bookmarkDao.addBookmark(new BookmarkEntity(questionId));
                mainHandler.post(() -> callback.onResult(true));
            }
        });
    }

    public void isBookmarked(long questionId, RepositoryCallback<Boolean> callback) {
        executor.execute(() -> {
            boolean bookmarked = bookmarkDao.isBookmarkedNow(questionId);
            mainHandler.post(() -> callback.onResult(bookmarked));
        });
    }

    public void getOverallPerformance(RepositoryCallback<OverallPerformance> callback) {
        executor.execute(() -> {
            int total = attemptDao.getTotalAttemptCountSync();
            int correct = attemptDao.getCorrectAttemptCountSync();
            Double avgTime = attemptDao.getAverageTimeSecondsSync();
            int accuracy = (total > 0) ? (int) (((double) correct / total) * 100) : 0;
            OverallPerformance perf = new OverallPerformance(total, correct, accuracy, avgTime != null ? avgTime : 0.0);
            mainHandler.post(() -> callback.onResult(perf));
        });
    }

    public void getWeakChapters(RepositoryCallback<List<String>> callback) {
        executor.execute(() -> {
            List<String> weak = attemptDao.getWeakChaptersSync();
            mainHandler.post(() -> callback.onResult(weak));
        });
    }

    public void getChapterPerformanceForSubject(String subject, RepositoryCallback<List<ChapterStats>> callback) {
        executor.execute(() -> {
            List<ChapterStats> stats = attemptDao.getChapterPerformanceForSubjectSync(subject);
            mainHandler.post(() -> callback.onResult(stats));
        });
    }

    public void clearUserData(Runnable onComplete) {
        executor.execute(() -> {
            attemptDao.deleteAllAttempts();
            testDao.deleteAllResults();
            reviewDao.deleteAllReviews();
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void restoreSyncedData(List<MockTestResultEntity> results, Runnable onComplete) {
        executor.execute(() -> {
            testDao.deleteAllResults();
            attemptDao.deleteAllAttempts();

            if (results != null) {
                int attemptCounter = 1;
                for (MockTestResultEntity res : results) {
                    testDao.insertResult(res);

                    int totalQ = res.getTotalQuestions();
                    int correct = res.getCorrectCount();
                    int wrong = res.getWrongCount();
                    int avgTime = totalQ > 0 ? Math.max(5, res.getTotalTimeSeconds() / totalQ) : 30;

                    for (int i = 0; i < correct; i++) {
                        attemptDao.insertAttempt(new QuestionAttemptEntity(
                                (long) (1000 + (attemptCounter++)), 1, true, avgTime, "MOCK_TEST"
                        ));
                    }
                    for (int i = 0; i < wrong; i++) {
                        attemptDao.insertAttempt(new QuestionAttemptEntity(
                                (long) (2000 + (attemptCounter++)), 0, false, avgTime, "MOCK_TEST"
                        ));
                    }
                }
            }

            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void getCompletedMockTestCount(RepositoryCallback<Integer> callback) {
        executor.execute(() -> {
            int count = testDao.getCompletedTestsCountSync();
            mainHandler.post(() -> callback.onResult(count));
        });
    }

    public List<LeaderboardUser> getLeaderboard() {
        return getLeaderboardForUser(null);
    }

    public List<LeaderboardUser> getLeaderboardForUser(String currentUserName) {
        String displayName = (currentUserName != null && !currentUserName.trim().isEmpty())
                ? currentUserName + " (You)" : "You";
        List<LeaderboardUser> list = new ArrayList<>();
        list.add(new LeaderboardUser(1, "Tanvir Ahmed", "Notre Dame College", 94.50, 96, false));
        list.add(new LeaderboardUser(2, "Sadia Rahman", "Rajuk Uttara Model", 91.25, 93, false));
        list.add(new LeaderboardUser(3, "Mahmudul Hasan", "Dhaka College", 88.75, 90, false));
        list.add(new LeaderboardUser(4, displayName, "BUET Aspirant", 84.50, 87, true));
        list.add(new LeaderboardUser(5, "Anika Tabassum", "Viqarunnisa Noon", 82.00, 85, false));
        list.add(new LeaderboardUser(6, "Rohan Chowdhury", "Chittagong College", 79.25, 82, false));
        list.add(new LeaderboardUser(7, "Farhan Kabir", "Sylhet MC College", 76.50, 80, false));
        return list;
    }
}
