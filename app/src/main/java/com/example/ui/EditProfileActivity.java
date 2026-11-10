package com.example.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.R;
import com.example.data.auth.UserManager;
import com.example.data.repository.FirebaseAccountRepository;
import com.example.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private ActivityEditProfileBinding binding;
    private UserManager userManager;
    private FirebaseAccountRepository firebaseAccountRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userManager = new UserManager(this);
        firebaseAccountRepo = new FirebaseAccountRepository(this);

        // Pre-fill existing profile data
        binding.etName.setText(userManager.getUserName());
        binding.etTargetUni.setText(userManager.getTargetUniversity());

        String currentBadge = userManager.getAvatarBadge();
        if ("CKRUET Aspirant".equals(currentBadge)) {
            binding.chipCkruet.setChecked(true);
        } else if ("DU Science Aspirant".equals(currentBadge)) {
            binding.chipDu.setChecked(true);
        } else if ("Engineering & Science".equals(currentBadge)) {
            binding.chipMedical.setChecked(true);
        } else {
            binding.chipBuet.setChecked(true);
        }

        binding.btnSaveChanges.setOnClickListener(v -> {
            String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
            String targetUni = binding.etTargetUni.getText() != null ? binding.etTargetUni.getText().toString().trim() : "";

            if (name.isEmpty()) {
                binding.tilName.setError("Name cannot be empty");
                return;
            }
            binding.tilName.setError(null);

            String selectedBadge = "BUET Aspirant";
            int checkedChipId = binding.cgBadges.getCheckedChipId();
            if (checkedChipId == R.id.chipCkruet) {
                selectedBadge = "CKRUET Aspirant";
            } else if (checkedChipId == R.id.chipDu) {
                selectedBadge = "DU Science Aspirant";
            } else if (checkedChipId == R.id.chipMedical) {
                selectedBadge = "Engineering & Science";
            }

            userManager.updateProfile(name, targetUni, selectedBadge);

            // Sync profile updates to Firebase Firestore
            firebaseAccountRepo.saveOrUpdateUserProfile(
                    name,
                    userManager.getUserEmail(),
                    targetUni,
                    selectedBadge,
                    () -> Log.d(TAG, "Profile update synced to Firestore"),
                    err -> Log.e(TAG, "Firestore update warning: " + err)
            );

            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
