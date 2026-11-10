package com.example.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Android Service demonstrating Service lifecycle callbacks and background operations (Lecture 10).
 */
public class StudySyncService extends Service {

    private static final String TAG = "StudySyncService";
    public static final String ACTION_SYNC_DATA = "com.aistudio.sprint.ACTION_SYNC_DATA";
    public static final String ACTION_SYNC_COMPLETE = "com.aistudio.sprint.ACTION_SYNC_COMPLETE";
    public static final String EXTRA_SYNC_STATUS = "sync_status";

    private final ExecutorService serviceExecutor = Executors.newSingleThreadExecutor();

    public static void startSync(Context context) {
        Intent intent = new Intent(context, StudySyncService.class);
        intent.setAction(ACTION_SYNC_DATA);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "StudySyncService onCreate: Service initialized");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "StudySyncService onStartCommand with startId: " + startId);

        String action = intent != null ? intent.getAction() : ACTION_SYNC_DATA;
        if (ACTION_SYNC_DATA.equals(action)) {
            performBackgroundSync(startId);
        }

        return START_NOT_STICKY;
    }

    private void performBackgroundSync(int startId) {
        serviceExecutor.execute(() -> {
            try {
                Log.d(TAG, "Background sync running...");
                Thread.sleep(1200); // Simulate network sync
                Log.d(TAG, "Background sync complete.");

                // Broadcast completion intent (Lecture 10 communication pattern)
                Intent broadcastIntent = new Intent(ACTION_SYNC_COMPLETE);
                broadcastIntent.setPackage(getPackageName());
                broadcastIntent.putExtra(EXTRA_SYNC_STATUS, true);
                sendBroadcast(broadcastIntent);
            } catch (InterruptedException e) {
                Log.e(TAG, "Sync interrupted: " + e.getMessage());
            } finally {
                stopSelf(startId);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceExecutor.shutdown();
        Log.d(TAG, "StudySyncService onDestroy: Executor stopped");
    }
}
