package com.example.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.data.dao.BookmarkDao;
import com.example.data.dao.MockTestResultDao;
import com.example.data.dao.QuestionAttemptDao;
import com.example.data.dao.QuestionDao;
import com.example.data.dao.SpacedReviewDao;
import com.example.data.entity.BookmarkEntity;
import com.example.data.entity.MockTestResultEntity;
import com.example.data.entity.QuestionAttemptEntity;
import com.example.data.entity.QuestionEntity;
import com.example.data.entity.SpacedReviewEntity;
import com.example.data.seed.QuestionDataSeeder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                QuestionEntity.class,
                QuestionAttemptEntity.class,
                SpacedReviewEntity.class,
                MockTestResultEntity.class,
                BookmarkEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class SprintDatabase extends RoomDatabase {

    public abstract QuestionDao questionDao();
    public abstract QuestionAttemptDao questionAttemptDao();
    public abstract SpacedReviewDao spacedReviewDao();
    public abstract MockTestResultDao mockTestResultDao();
    public abstract BookmarkDao bookmarkDao();

    private static volatile SprintDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static SprintDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SprintDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    SprintDatabase.class,
                                    "sprint_database.db"
                            )
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        QuestionDataSeeder seeder = new QuestionDataSeeder();
                                        INSTANCE.questionDao().insertAll(seeder.getInitialQuestions());
                                    });
                                }
                            })
                            .build();

                    // Ensure question bank is seeded even if database was created empty
                    databaseWriteExecutor.execute(() -> {
                        int count = INSTANCE.questionDao().getQuestionCount();
                        if (count == 0) {
                            QuestionDataSeeder seeder = new QuestionDataSeeder();
                            INSTANCE.questionDao().insertAll(seeder.getInitialQuestions());
                        }
                    });
                }
            }
        }
        return INSTANCE;
    }
}
