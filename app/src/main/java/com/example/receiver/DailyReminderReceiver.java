package com.example.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.MainActivity;
import com.example.SprintApplication;

public class DailyReminderReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1001;
    public static final String ACTION_DAILY_REMINDER = "com.aistudio.sprint.ACTION_DAILY_REMINDER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Reschedule after reboot (Topic 10)
            ReminderScheduler.scheduleDailyReminder(context);
            return;
        }

        // Post Notification (Lecture 10)
        showStudyNotification(context);

        // Reschedule for next day
        ReminderScheduler.scheduleDailyReminder(context);
    }

    private void showStudyNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SprintApplication.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("⚡ Sprint Admission Practice")
                .setContentText("Keep your momentum! Solve today's 10 questions to maintain speed.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Exam day is approaching! Reinforce your weak chapters with instant explanations and timed drills."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException ignored) {
        }
    }
}
