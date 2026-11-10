package com.example.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks")
public class BookmarkEntity {

    @PrimaryKey
    private long questionId;

    private long bookmarkedAt;

    public BookmarkEntity() {
        this.bookmarkedAt = System.currentTimeMillis();
    }

    @Ignore
    public BookmarkEntity(long questionId) {
        this.questionId = questionId;
        this.bookmarkedAt = System.currentTimeMillis();
    }

    @Ignore
    public BookmarkEntity(long questionId, long bookmarkedAt) {
        this.questionId = questionId;
        this.bookmarkedAt = bookmarkedAt;
    }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public long getBookmarkedAt() { return bookmarkedAt; }
    public void setBookmarkedAt(long bookmarkedAt) { this.bookmarkedAt = bookmarkedAt; }
}
