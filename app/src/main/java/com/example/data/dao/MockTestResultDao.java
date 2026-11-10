package com.example.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.data.entity.MockTestResultEntity;

import java.util.List;

@Dao
public interface MockTestResultDao {

    @Insert
    long insertResult(MockTestResultEntity result);

    @Query("SELECT * FROM mock_test_results ORDER BY timestamp DESC")
    List<MockTestResultEntity> getAllResultsSync();

    @Query("SELECT * FROM mock_test_results WHERE id = :id LIMIT 1")
    MockTestResultEntity getResultById(long id);

    @Query("SELECT COUNT(*) FROM mock_test_results")
    int getCompletedTestsCountSync();

    @Query("SELECT AVG(scoreAchieved) FROM mock_test_results")
    Double getAverageScoreSync();

    @Query("DELETE FROM mock_test_results")
    void deleteAllResults();
}
