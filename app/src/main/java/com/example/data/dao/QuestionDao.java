package com.example.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.data.entity.QuestionEntity;

import java.util.List;

@Dao
public interface QuestionDao {

    @Query("SELECT * FROM questions ORDER BY id ASC")
    List<QuestionEntity> getAllQuestionsSync();

    @Query("SELECT * FROM questions ORDER BY id ASC")
    LiveData<List<QuestionEntity>> getAllQuestions();

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    QuestionEntity getQuestionById(long id);

    @Query("SELECT * FROM questions WHERE subject = :subject ORDER BY chapter ASC, id ASC")
    List<QuestionEntity> getQuestionsBySubjectSync(String subject);

    @Query("SELECT * FROM questions WHERE subject = :subject AND chapter = :chapter ORDER BY id ASC")
    List<QuestionEntity> getQuestionsByChapterSync(String subject, String chapter);

    @Query("SELECT DISTINCT chapter FROM questions WHERE subject = :subject ORDER BY chapter ASC")
    List<String> getChaptersBySubjectSync(String subject);

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    List<QuestionEntity> getRandomQuestions(int limit);

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    List<QuestionEntity> getQuestionsByIds(List<Long> ids);

    @Query("SELECT COUNT(*) FROM questions")
    int getQuestionCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<QuestionEntity> questions);
}
