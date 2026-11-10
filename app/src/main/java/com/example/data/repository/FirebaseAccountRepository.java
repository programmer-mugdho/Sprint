package com.example.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseAccountRepository {

    private static final String TAG = "FirebaseAccountRepo";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public interface LeaderboardCallback {
        void onLoaded(List<LeaderboardUser> users);
        void onError(String error);
    }

    public FirebaseAccountRepository(Context context) {
        this.db = FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id));
        this.auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserSignedIn() {
        return auth.getCurrentUser() != null;
    }

    public void saveOrUpdateUserProfile(String name, String email, String targetUni, String avatarBadge,
                                        Runnable onSuccess, java.util.function.Consumer<String> onError) {
        FirebaseUser currentUser = auth.getCurrentUser();
        String uid = currentUser != null ? currentUser.getUid() : "guest_" + System.currentTimeMillis();

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", uid);
        userData.put("name", name != null && !name.trim().isEmpty() ? name : "Aspirant");
        userData.put("email", email != null ? email : "student@sprint.edu");
        userData.put("targetUniversity", targetUni != null && !targetUni.trim().isEmpty() ? targetUni : "BUET / Engineering");
        userData.put("avatarBadge", avatarBadge != null ? avatarBadge : "BUET Aspirant");
        userData.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users").document(uid)
                .set(userData, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User profile saved to Firebase Firestore for uid: " + uid);
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user profile to Firestore: " + e.getLocalizedMessage(), e);
                    if (onError != null) onError.accept(e.getLocalizedMessage());
                });
    }

    public void recordExamResult(String testTitle, int totalQuestions, int correctCount, int wrongCount,
                                 int skippedCount, double scoreAchieved, int totalTimeSeconds,
                                 String userName, String targetUni,
                                 Runnable onSuccess, java.util.function.Consumer<String> onError) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            if (onError != null) onError.accept("Please sign in to record your exam on the leaderboard.");
            return;
        }

        String uid = currentUser.getUid();
        double accuracy = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100.0 : 0.0;

        // 1. Create a detailed exam_result document
        Map<String, Object> examData = new HashMap<>();
        examData.put("userId", uid);
        examData.put("userName", userName != null ? userName : "Aspirant");
        examData.put("targetUniversity", targetUni != null ? targetUni : "BUET / Engineering");
        examData.put("testTitle", testTitle);
        examData.put("scoreAchieved", scoreAchieved);
        examData.put("totalQuestions", totalQuestions);
        examData.put("correctCount", correctCount);
        examData.put("wrongCount", wrongCount);
        examData.put("skippedCount", skippedCount);
        examData.put("accuracyPercent", Math.round(accuracy));
        examData.put("totalTimeSeconds", totalTimeSeconds);
        examData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("exam_results").add(examData)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Exam result added with ID: " + docRef.getId());

                    // 2. Update user profile's leaderboard metrics
                    Map<String, Object> userUpdate = new HashMap<>();
                    userUpdate.put("bestScore", scoreAchieved);
                    userUpdate.put("accuracyPercent", Math.round(accuracy));
                    userUpdate.put("testsTaken", FieldValue.increment(1));
                    userUpdate.put("totalScore", FieldValue.increment(scoreAchieved));
                    userUpdate.put("totalSolved", FieldValue.increment(totalQuestions));
                    userUpdate.put("totalCorrect", FieldValue.increment(correctCount));
                    userUpdate.put("lastTestAt", FieldValue.serverTimestamp());

                    db.collection("users").document(uid)
                            .set(userUpdate, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(v -> {
                                Log.d(TAG, "User score updated in Firestore");
                                if (onSuccess != null) onSuccess.run();
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Warning: failed to update user aggregate score: " + e.getLocalizedMessage());
                                if (onSuccess != null) onSuccess.run();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save exam result to Firestore: " + e.getLocalizedMessage(), e);
                    if (onError != null) onError.accept(e.getLocalizedMessage());
                });
    }

    public void fetchRealLeaderboard(LeaderboardCallback callback) {
        // Query users collection ordered by bestScore or updatedAt
        db.collection("users")
                .limit(50)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<LeaderboardUser> userList = new ArrayList<>();
                    FirebaseUser current = auth.getCurrentUser();
                    String currentUid = current != null ? current.getUid() : "";

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String uid = doc.getString("userId");
                        if (uid == null) uid = doc.getId();

                        String name = doc.getString("name");
                        if (name == null || name.trim().isEmpty()) name = "Student";

                        String college = doc.getString("targetUniversity");
                        if (college == null || college.trim().isEmpty()) {
                            college = doc.getString("avatarBadge");
                        }
                        if (college == null || college.trim().isEmpty()) college = "BUET Aspirant";

                        Double score = doc.getDouble("bestScore");
                        if (score == null) {
                            score = doc.getDouble("totalScore");
                        }
                        double finalScore = score != null ? score : 0.0;

                        Long acc = doc.getLong("accuracyPercent");
                        int finalAcc = acc != null ? acc.intValue() : 0;

                        boolean isCurrent = currentUid.equals(uid);

                        userList.add(new LeaderboardUser(0, name, college, finalScore, finalAcc, isCurrent));
                    }

                    // Sort descending by score
                    userList.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));

                    // Assign accurate ranks 1..N
                    List<LeaderboardUser> rankedList = new ArrayList<>();
                    for (int i = 0; i < userList.size(); i++) {
                        LeaderboardUser u = userList.get(i);
                        rankedList.add(new LeaderboardUser(
                                i + 1,
                                u.getName(),
                                u.getCollege(),
                                u.getTotalScore(),
                                u.getAccuracy(),
                                u.isCurrentUser()
                        ));
                    }

                    callback.onLoaded(rankedList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load leaderboard from Firestore", e);
                    callback.onError(e.getLocalizedMessage());
                });
    }

    public void getUserProfile(java.util.function.Consumer<Map<String, Object>> onSuccess, java.util.function.Consumer<String> onError) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            if (onError != null) onError.accept("No user signed in");
            return;
        }

        String uid = currentUser.getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                        onSuccess.accept(documentSnapshot.getData());
                    } else {
                        if (onError != null) onError.accept("User document not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch profile: " + e.getLocalizedMessage(), e);
                    if (onError != null) onError.accept(e.getLocalizedMessage());
                });
    }

    public void syncUserProgress(SprintRepository repository, Runnable onComplete) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        String uid = currentUser.getUid();
        Log.d(TAG, "Syncing user progress from Firestore for UID: " + uid);

        db.collection("exam_results")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        Log.d(TAG, "No past exam results found in Firestore for UID: " + uid);
                        syncUserAggregateStats(uid, repository, onComplete);
                        return;
                    }

                    List<com.example.data.entity.MockTestResultEntity> results = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String title = doc.getString("testTitle");
                        if (title == null || title.isEmpty()) title = "Engineering Admission Mock Test";
                        Long totalQ = doc.getLong("totalQuestions");
                        Long correct = doc.getLong("correctCount");
                        Long wrong = doc.getLong("wrongCount");
                        Long skipped = doc.getLong("skippedCount");
                        Double score = doc.getDouble("scoreAchieved");
                        Long totalTime = doc.getLong("totalTimeSeconds");

                        int intTotal = totalQ != null ? totalQ.intValue() : 0;
                        int intCorrect = correct != null ? correct.intValue() : 0;
                        int intWrong = wrong != null ? wrong.intValue() : 0;
                        int intSkipped = skipped != null ? skipped.intValue() : 0;
                        double doubleScore = score != null ? score : 0.0;
                        int intTime = totalTime != null ? totalTime.intValue() : 0;

                        com.example.data.entity.MockTestResultEntity entity = new com.example.data.entity.MockTestResultEntity(
                                title, intTotal, intCorrect, intWrong, intSkipped,
                                doubleScore, intTime, "", "", ""
                        );
                        results.add(entity);
                    }

                    repository.restoreSyncedData(results, () -> {
                        Log.d(TAG, "Restored " + results.size() + " test results into local database");
                        if (onComplete != null) onComplete.run();
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed syncing exam_results from Firestore: " + e.getMessage(), e);
                    syncUserAggregateStats(uid, repository, onComplete);
                });
    }

    private void syncUserAggregateStats(String uid, SprintRepository repository, Runnable onComplete) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        Long totalSolved = doc.getLong("totalSolved");
                        Long totalCorrect = doc.getLong("totalCorrect");
                        Long testsTaken = doc.getLong("testsTaken");
                        Double bestScore = doc.getDouble("bestScore");

                        if (totalSolved != null && totalSolved > 0) {
                            int solved = totalSolved.intValue();
                            int correct = totalCorrect != null ? totalCorrect.intValue() : solved;
                            int wrong = Math.max(0, solved - correct);
                            int tests = testsTaken != null ? testsTaken.intValue() : 1;
                            double score = bestScore != null ? bestScore : 0.0;

                            List<com.example.data.entity.MockTestResultEntity> dummyList = new ArrayList<>();
                            for (int t = 0; t < tests; t++) {
                                dummyList.add(new com.example.data.entity.MockTestResultEntity(
                                        "Engineering Admission Mock Test",
                                        solved / Math.max(1, tests),
                                        correct / Math.max(1, tests),
                                        wrong / Math.max(1, tests),
                                        0, score, 600, "", "", ""
                                ));
                            }
                            repository.restoreSyncedData(dummyList, onComplete);
                            return;
                        }
                    }
                    if (onComplete != null) onComplete.run();
                })
                .addOnFailureListener(e -> {
                    if (onComplete != null) onComplete.run();
                });
    }
}
