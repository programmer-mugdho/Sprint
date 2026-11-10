package com.example.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    private static final String PREF_NAME = "sprint_user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_TARGET_UNI = "target_university";
    private static final String KEY_AVATAR_BADGE = "avatar_badge";
    private static final String KEY_JOIN_DATE = "join_date";

    private final SharedPreferences prefs;

    public UserManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean registerUser(String name, String email, String password, String targetUni) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_NAME, name != null && !name.trim().isEmpty() ? name : "Aspirant")
                .putString(KEY_USER_EMAIL, email.trim().toLowerCase())
                .putString(KEY_USER_PASSWORD, password)
                .putString(KEY_TARGET_UNI, targetUni != null && !targetUni.trim().isEmpty() ? targetUni : "BUET / Engineering")
                .putString(KEY_AVATAR_BADGE, "BUET Aspirant")
                .putLong(KEY_JOIN_DATE, System.currentTimeMillis())
                .apply();
        return true;
    }

    public boolean loginUser(String email, String password) {
        String savedEmail = prefs.getString(KEY_USER_EMAIL, "");
        String savedPass = prefs.getString(KEY_USER_PASSWORD, "");

        if (savedEmail.equalsIgnoreCase(email.trim()) && savedPass.equals(password)) {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply();
            return true;
        }

        // Default test account fallback if no user registered yet
        if (savedEmail.isEmpty() && !email.trim().isEmpty() && !password.isEmpty()) {
            prefs.edit()
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .putString(KEY_USER_NAME, email.split("@")[0])
                    .putString(KEY_USER_EMAIL, email.trim().toLowerCase())
                    .putString(KEY_USER_PASSWORD, password)
                    .putString(KEY_TARGET_UNI, "BUET / Engineering")
                    .putString(KEY_AVATAR_BADGE, "Engineering Aspirant")
                    .putLong(KEY_JOIN_DATE, System.currentTimeMillis())
                    .apply();
            return true;
        }

        return false;
    }

    public void updateProfile(String name, String targetUni, String badge) {
        prefs.edit()
                .putString(KEY_USER_NAME, name)
                .putString(KEY_TARGET_UNI, targetUni)
                .putString(KEY_AVATAR_BADGE, badge)
                .apply();
    }

    public void logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Aspirant");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "student@sprint.edu");
    }

    public String getTargetUniversity() {
        return prefs.getString(KEY_TARGET_UNI, "BUET / Engineering");
    }

    public String getAvatarBadge() {
        return prefs.getString(KEY_AVATAR_BADGE, "BUET Aspirant");
    }

    public long getJoinDate() {
        return prefs.getLong(KEY_JOIN_DATE, System.currentTimeMillis());
    }
}
