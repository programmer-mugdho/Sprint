package com.example;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.data.database.SprintDatabase;
import com.example.data.repository.SprintRepository;
import com.example.receiver.ReminderScheduler;

public class SprintApplication extends Application {

    public static final String CHANNEL_ID = "sprint_study_reminders";

    private SprintDatabase database;
    private SprintRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        database = SprintDatabase.getDatabase(this);
        repository = new SprintRepository(database);

        createNotificationChannel();
        ReminderScheduler.scheduleDailyReminder(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(android.app.Activity activity, android.os.Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(android.app.Activity activity) {
                com.example.util.InsetsUtil.applySystemBarsPadding(activity);
            }

            @Override
            public void onActivityResumed(android.app.Activity activity) {
                com.example.util.InsetsUtil.applySystemBarsPadding(activity);
            }

            @Override
            public void onActivityPaused(android.app.Activity activity) {}

            @Override
            public void onActivityStopped(android.app.Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(android.app.Activity activity, android.os.Bundle outState) {}

            @Override
            public void onActivityDestroyed(android.app.Activity activity) {}
        });
    }

    public SprintRepository getRepository() {
        return repository;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Sprint Study Reminders";
            String descriptionText = "Notifications to remind students to complete daily admission test practice";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(descriptionText);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
