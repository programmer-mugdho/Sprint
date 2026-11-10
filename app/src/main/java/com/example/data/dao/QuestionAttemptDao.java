package com.example.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.data.entity.QuestionAttemptEntity;

import java.util.List;

@Dao
public interface QuestionAttemptDao {

    @Insert
    long insertAttempt(QuestionAttemptEntity attempt);

    @Insert
    void insertAttempts(List<QuestionAttemptEntity> attempts);

    @Query("SELECT COUNT(*) FROM question_attempts")
    int getTotalAttemptCountSync();

    @Query("SELECT COUNT(*) FROM question_attempts WHERE isCorrect = 1")
    int getCorrectAttemptCountSync();

    @Query("SELECT AVG(timeSpentSeconds) FROM question_attempts WHERE timeSpentSeconds > 0")
    Double getAverageTimeSecondsSync();

    @Query("SELECT q.chapter AS chapter, q.subject AS subject, " +
           "COUNT(a.id) AS totalAttempts, " +
           "SUM(CASE WHEN a.isCorrect = 1 THEN 1 ELSE 0 END) AS correctAttempts, " +
           "AVG(a.timeSpentSeconds) AS avgTimeSeconds " +
           "FROM question_attempts a " +
           "JOIN questions q ON a.questionId = q.id " +
           "GROUP BY q.subject, q.chapter")
    List<ChapterStats> getChapterPerformanceStatsSync();

    @Query("SELECT q.chapter AS chapter, q.subject AS subject, " +
           "COUNT(a.id) AS totalAttempts, " +
           "SUM(CASE WHEN a.isCorrect = 1 THEN 1 ELSE 0 END) AS correctAttempts, " +
           "AVG(a.timeSpentSeconds) AS avgTimeSeconds " +
           "FROM question_attempts a " +
           "JOIN questions q ON a.questionId = q.id " +
           "WHERE q.subject = :subject " +
           "GROUP BY q.chapter")
    List<ChapterStats> getChapterPerformanceForSubjectSync(String subject);

    @Query("SELECT q.chapter " +
           "FROM question_attempts a " +
           "JOIN questions q ON a.questionId = q.id " +
           "GROUP BY q.subject, q.chapter " +
           "HAVING COUNT(a.id) >= 2 AND (CAST(SUM(CASE WHEN a.isCorrect = 1 THEN 1 ELSE 0 END) AS FLOAT) / COUNT(a.id)) < 0.6 " +
           "ORDER BY (CAST(SUM(CASE WHEN a.isCorrect = 1 THEN 1 ELSE 0 END) AS FLOAT) / COUNT(a.id)) ASC")
    List<String> getWeakChaptersSync();

    @Query("DELETE FROM question_attempts")
    void deleteAllAttempts();
}
