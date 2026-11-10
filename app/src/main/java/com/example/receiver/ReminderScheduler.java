package com.example.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class ReminderScheduler {

    public static void scheduleDailyReminder(Context context) {
        scheduleDailyReminder(context, 19, 0); // 7:00 PM default
    }

    public static void scheduleDailyReminder(Context context, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, DailyReminderReceiver.class);
        intent.setAction(DailyReminderReceiver.ACTION_DAILY_REMINDER);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        try {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        } catch (SecurityException ignored) {
        }
    }

    public static void triggerTestNotificationNow(Context context) {
        Intent intent = new Intent(context, DailyReminderReceiver.class);
        intent.setAction(DailyReminderReceiver.ACTION_DAILY_REMINDER);
        context.sendBroadcast(intent);
    }
}
