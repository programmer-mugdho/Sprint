package com.example.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.data.entity.QuestionEntity;
import com.example.data.entity.SpacedReviewEntity;

import java.util.List;

@Dao
public interface SpacedReviewDao {

    @Query("SELECT * FROM spaced_reviews WHERE questionId = :questionId LIMIT 1")
    SpacedReviewEntity getReviewItem(long questionId);

    @Query("SELECT COUNT(*) FROM spaced_reviews WHERE isMastered = 0")
    int getPendingReviewCountSync();

    @Query("SELECT q.* FROM questions q " +
           "JOIN spaced_reviews r ON q.id = r.questionId " +
           "WHERE r.isMastered = 0 " +
           "ORDER BY r.nextReviewTimestamp ASC")
    List<QuestionEntity> getDueReviewQuestionsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertReview(SpacedReviewEntity review);

    @Query("UPDATE spaced_reviews SET isMastered = 1 WHERE questionId = :questionId")
    void markMastered(long questionId);

    @Query("DELETE FROM spaced_reviews")
    void deleteAllReviews();
}
