package com.example.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.data.entity.BookmarkEntity;
import com.example.data.entity.QuestionEntity;

import java.util.List;

@Dao
public interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addBookmark(BookmarkEntity bookmark);

    @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
    void removeBookmark(long questionId);

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
    boolean isBookmarkedNow(long questionId);

    @Query("SELECT q.* FROM questions q " +
           "JOIN bookmarks b ON q.id = b.questionId " +
           "ORDER BY b.bookmarkedAt DESC")
    List<QuestionEntity> getBookmarkedQuestionsSync();

    @Query("SELECT COUNT(*) FROM bookmarks")
    int getBookmarkCountSync();
}
