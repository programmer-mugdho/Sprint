package com.example.util;

import android.app.Activity;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InsetsUtil {

    public static void applySystemBarsPadding(Activity activity) {
        if (activity == null) return;
        View content = activity.findViewById(android.R.id.content);
        if (content != null) {
            applySystemBarsPadding(content);
        }
    }

    public static void applySystemBarsPadding(View view) {
        if (view == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );

            int statusBarHeight = insets.top;
            if (statusBarHeight <= 0) {
                int resourceId = v.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    statusBarHeight = v.getResources().getDimensionPixelSize(resourceId);
                }
                if (statusBarHeight <= 0) {
                    statusBarHeight = (int) (32 * v.getResources().getDisplayMetrics().density);
                }
            }

            v.setPadding(insets.left, statusBarHeight, insets.right, insets.bottom);
            return windowInsets;
        });

        ViewCompat.requestApplyInsets(view);
    }
}
