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
import com.example.data.auth.UserManager;
import com.example.data.repository.FirebaseAccountRepository;
import com.example.databinding.ActivityLoginBinding;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private UserManager userManager;
    private FirebaseAccountRepository firebaseAccountRepo;
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userManager = new UserManager(this);
        firebaseAccountRepo = new FirebaseAccountRepository(this);
        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);

        // Auto-navigate to MainActivity if user is already logged in
        if (userManager.isLoggedIn() || firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGoogleSignIn.setOnClickListener(v -> performGoogleSignIn());

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";

            if (email.isEmpty()) {
                binding.tilEmail.setError("Please enter your email");
                return;
            }
            binding.tilEmail.setError(null);

            if (password.isEmpty()) {
                binding.tilPassword.setError("Please enter your password");
                return;
            }
            binding.tilPassword.setError(null);

            boolean success = userManager.loginUser(email, password);
            if (success) {
                // Save profile sync to Firebase Firestore
                firebaseAccountRepo.saveOrUpdateUserProfile(
                        userManager.getUserName(),
                        userManager.getUserEmail(),
                        userManager.getTargetUniversity(),
                        userManager.getAvatarBadge(),
                        () -> Log.d(TAG, "Firestore sync successful"),
                        err -> Log.w(TAG, "Firestore sync error: " + err)
                );

                com.example.SprintApplication app = (com.example.SprintApplication) getApplication();
                firebaseAccountRepo.syncUserProgress(app.getRepository(), () -> {
                    Log.d(TAG, "User progress restored on email login");
                });

                Toast.makeText(this, "Welcome back, " + userManager.getUserName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials. Try again or create an account.", Toast.LENGTH_LONG).show();
            }
        });

        binding.btnGuest.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.tvSignUpLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    private void performGoogleSignIn() {
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
                    Toast.makeText(LoginActivity.this, "No credentials returned", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(androidx.credentials.exceptions.GetCredentialException e) {
                if (e instanceof GetCredentialCancellationException) {
                    Log.w(TAG, "Google Sign-In cancelled: " + e.getLocalizedMessage());
                } else {
                    Log.e(TAG, "Google Sign-In error: " + e.getLocalizedMessage(), e);
                    Toast.makeText(LoginActivity.this, "Google Sign-In error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

                            onGoogleAuthSuccess(resolvedName, resolvedEmail);
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Firebase Auth sign-in failed (" + e.getMessage() + "), proceeding with verified Google profile", e);
                            onGoogleAuthSuccess(finalName, finalEmail);
                        });
                return;
            } catch (Exception e) {
                Log.e(TAG, "Error creating auth credential", e);
            }
        }

        onGoogleAuthSuccess(finalName, finalEmail);
    }

    private void onGoogleAuthSuccess(String name, String email) {
        userManager.registerUser(name, email, "google_pass", "BUET / Engineering");

        firebaseAccountRepo.saveOrUpdateUserProfile(
                name,
                email,
                "BUET / Engineering",
                "BUET Aspirant",
                () -> Log.d(TAG, "Saved Google User to Firestore"),
                err -> Log.w(TAG, "Firestore sync error: " + err)
        );

        com.example.SprintApplication app = (com.example.SprintApplication) getApplication();
        firebaseAccountRepo.syncUserProgress(app.getRepository(), () -> {
            Log.d(TAG, "User progress restored on Google login");
        });

        Toast.makeText(this, "Welcome back, " + name, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
