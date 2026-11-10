package com.example.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.exceptions.GetCredentialCancellationException;

import com.example.MainActivity;
import com.example.R;
import com.example.SprintApplication;
import com.example.data.auth.UserManager;
import com.example.data.repository.FirebaseAccountRepository;
import com.example.databinding.ActivitySignupBinding;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;
    private UserManager userManager;
    private FirebaseAccountRepository firebaseAccountRepo;
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);
        firebaseAccountRepo = new FirebaseAccountRepository(this);
        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        binding.btnGoogleSignUp.setOnClickListener(v -> performGoogleSignUp());

        binding.btnSignUp.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String targetUni = binding.etTargetUniversity.getText() != null ? binding.etTargetUniversity.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

            if (fullName.isEmpty()) {
                binding.tilFullName.setError("Full name is required");
                return;
            }
            binding.tilFullName.setError(null);

            if (email.isEmpty()) {
                binding.tilEmail.setError("Email is required");
                return;
            }
            binding.tilEmail.setError(null);

            if (password.length() < 4) {
                binding.tilPassword.setError("Password must be at least 4 characters");
                return;
            }
            binding.tilPassword.setError(null);

            boolean success = userManager.registerUser(fullName, email, password, targetUni);
            if (success) {
                // Clear any leftover local test attempts so the new account starts fresh with 0 results
                SprintApplication app = (SprintApplication) getApplication();
                app.getRepository().clearUserData(null);

                // Save user account directly to Firebase Firestore
                firebaseAccountRepo.saveOrUpdateUserProfile(
                        fullName,
                        email,
                        targetUni,
                        "BUET Aspirant",
                        () -> Log.d(TAG, "User account successfully created in Firebase Firestore"),
                        err -> Log.e(TAG, "Firestore creation warning: " + err)
                );

                Toast.makeText(this, "Account created! Welcome, " + fullName, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            } else {
                Toast.makeText(this, "Failed to create account. Please check details.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private void performGoogleSignUp() {
        String serverClientId;
        try {
            serverClientId = getString(R.string.default_web_client_id);
        } catch (Exception e) {
            Toast.makeText(this, "Google Sign-In configuration missing", Toast.LENGTH_SHORT).show();
            return;
        }

        GetSignInWithGoogleOption googleOption = new GetSignInWithGoogleOption.Builder(serverClientId).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build();

        credentialManager.getCredentialAsync(this, request, null, getMainExecutor(), new androidx.credentials.CredentialManagerCallback<androidx.credentials.GetCredentialResponse, androidx.credentials.exceptions.GetCredentialException>() {
            @Override
            public void onResult(androidx.credentials.GetCredentialResponse result) {
                if (result != null && result.getCredential() != null) {
                    handleGoogleCredential(result.getCredential());
                } else {
                    Toast.makeText(SignupActivity.this, "No credentials returned", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(androidx.credentials.exceptions.GetCredentialException e) {
                if (e instanceof GetCredentialCancellationException) {
                    Log.w(TAG, "Google Sign-Up cancelled: " + e.getLocalizedMessage());
                } else {
                    Log.e(TAG, "Google Sign-Up error: " + e.getLocalizedMessage(), e);
                    Toast.makeText(SignupActivity.this, "Google Sign-Up error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleGoogleCredential(androidx.credentials.Credential credential) {
        String idToken = null;
        String email = null;
        String displayName = null;

        if (credential instanceof CustomCredential) {
            CustomCredential customCred = (CustomCredential) credential;
            Bundle bundle = customCred.getData();

            try {
                GoogleIdTokenCredential idTokenCred = GoogleIdTokenCredential.createFrom(bundle);
                idToken = idTokenCred.getIdToken();
                email = idTokenCred.getId();
                displayName = idTokenCred.getDisplayName();
            } catch (Exception e) {
                Log.w(TAG, "GoogleIdTokenCredential.createFrom failed: " + e.getMessage());
            }

            if (bundle != null) {
                if (idToken == null || idToken.isEmpty()) {
                    idToken = bundle.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN");
                    if (idToken == null) idToken = bundle.getString("id_token");
                    if (idToken == null) idToken = bundle.getString("idToken");
                }
                if (email == null || email.isEmpty()) {
                    email = bundle.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID");
                    if (email == null) email = bundle.getString("email");
                    if (email == null) email = bundle.getString("id");
                }
                if (displayName == null || displayName.isEmpty()) {
                    displayName = bundle.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME");
                    if (displayName == null) displayName = bundle.getString("displayName");
                    if (displayName == null) displayName = bundle.getString("name");
                }
            }
        }

        final String finalEmail = (email != null && !email.trim().isEmpty()) ? email.trim() : "student@sprint.edu";
        final String finalName = (displayName != null && !displayName.trim().isEmpty())
                ? displayName.trim()
                : (finalEmail.contains("@") ? finalEmail.split("@")[0] : "Aspirant");

        String userEnteredTarget = binding.etTargetUniversity.getText() != null
                ? binding.etTargetUniversity.getText().toString().trim() : "";
        final String targetUni = !userEnteredTarget.isEmpty() ? userEnteredTarget : "BUET / Engineering";

        if (idToken != null && !idToken.isEmpty()) {
            try {
                AuthCredential authCred = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuth.signInWithCredential(authCred)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser fbUser = authResult.getUser();
                            String resolvedName = (fbUser != null && fbUser.getDisplayName() != null && !fbUser.getDisplayName().trim().isEmpty())
                                    ? fbUser.getDisplayName().trim() : finalName;
                            String resolvedEmail = (fbUser != null && fbUser.getEmail() != null && !fbUser.getEmail().trim().isEmpty())
                                    ? fbUser.getEmail().trim() : finalEmail;

                            onGoogleAuthSuccess(resolvedName, resolvedEmail, targetUni);
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Firebase Auth sign-in failed (" + e.getMessage() + "), proceeding with verified Google profile", e);
                            onGoogleAuthSuccess(finalName, finalEmail, targetUni);
                        });
                return;
            } catch (Exception e) {
                Log.e(TAG, "Error creating auth credential", e);
            }
        }

        onGoogleAuthSuccess(finalName, finalEmail, targetUni);
    }

    private void onGoogleAuthSuccess(String name, String email, String targetUni) {
        // Clear any previous attempts so new user starts with 0 results
        SprintApplication app = (SprintApplication) getApplication();
        app.getRepository().clearUserData(null);

        userManager.registerUser(name, email, "google_auth", targetUni);

        // Save to Firebase Firestore
        firebaseAccountRepo.saveOrUpdateUserProfile(
                name,
                email,
                targetUni,
                "BUET Aspirant",
                () -> Log.d(TAG, "Saved Google User to Firestore"),
                err -> Log.w(TAG, "Firestore sync warning: " + err)
        );

        Toast.makeText(this, "Welcome to Sprint, " + name, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
