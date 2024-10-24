package com.example.cs218marketmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 101;

    private ImageView profileImageView, productImageView;
    private EditText editTextEmail, editFirstName, editLastName;
    private EditText editTextUsername;
    private Bitmap profileImageBitmap, productImageBitmap;
    private int currentPhotoTarget;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_vendor_profile);

        // Initialize Views
        profileImageView = findViewById(R.id.editProfilePicture);
        productImageView = findViewById(R.id.imageViewProductPhoto);
        editTextUsername = findViewById(R.id.editUserName);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editTextEmail = findViewById(R.id.editTextEmail);

        // Initialize Buttons
        Button uploadProfilePictureButton = findViewById(R.id.uploadProPicButton);
        Button uploadProductPhotoButton = findViewById(R.id.uploadProdPicButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button saveProfileButton = findViewById(R.id.saveProfileButton); // Changed to Button type

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);

        // Load current profile and product photos from the database
        loadCurrentUserDetailsAndImages();

        // Set OnClickListeners for Camera buttons
        uploadProfilePictureButton.setOnClickListener(v -> {
            currentPhotoTarget = R.id.editProfilePicture;
            handleCameraPermission();
        });

        uploadProductPhotoButton.setOnClickListener(v -> {
            currentPhotoTarget = R.id.imageViewProductPhoto;
            handleCameraPermission();
        });

        // Save the image and other changes to the database
        saveProfileButton.setOnClickListener(view -> {
            String firstName = editLastName.getText().toString();
            String lastName = editLastName.getText().toString();
            String email = editTextEmail.getText().toString();
            String username = editTextUsername.getText().toString();

            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            Long userId = prefs.getLong("userId", -1L);
            if (userId != -1) {
                // Save images to the database before updating user
                if (profileImageBitmap != null) {
                    saveImageToDatabase(profileImageBitmap, true);
                }
                if (productImageBitmap != null) {
                    saveImageToDatabase(productImageBitmap, false);
                }
                boolean isUpdated = databaseHelper.updateUser(userId, username, email, firstName, lastName);
                if (isUpdated) {
                    Toast.makeText(EditProfileActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfileActivity.this, VendorProfileActivity.class);
                    startActivity(intent);
                    finish(); // End the EditProfileActivity
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Discard changes and restore the original state
        cancelButton.setOnClickListener(v -> {
            loadCurrentUserDetailsAndImages(); // Reload the original images
            Toast.makeText(EditProfileActivity.this, "Changes discarded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, VendorProfileActivity.class);
            startActivity(intent);
            finish(); // End the EditProfileActivity
        });
    }

    private void loadCurrentUserDetailsAndImages() {
        long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            // Load user details
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                editTextUsername.setText(user.getUsername());
                editTextEmail.setText(user.getEmail());
                // Assuming you have getter methods for first and last names
                editFirstName.setText(user.getFirstName());
                editLastName.setText(user.getLastName());
            } else {
                Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
            }

            // Load the profile picture from the USER table
            byte[] profileImageBytes = databaseHelper.getUserProfilePicture(userId);
            if (profileImageBytes != null) {
                profileImageBitmap = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
                profileImageView.setImageBitmap(profileImageBitmap);
            }

            // Load the product picture from the VENDOR table
            byte[] productImageBytes = databaseHelper.getVendorProductPicture(userId);
            if (productImageBytes != null) {
                productImageBitmap = BitmapFactory.decodeByteArray(productImageBytes, 0, productImageBytes.length);
                productImageView.setImageBitmap(productImageBitmap);
            }
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");

            if (imageBitmap != null) {
                // Check which ImageView to update
                if (currentPhotoTarget == R.id.editProfilePicture) {
                    profileImageView.setImageBitmap(imageBitmap);
                    profileImageBitmap = imageBitmap; // Store the current image for saving later
                } else if (currentPhotoTarget == R.id.imageViewProductPhoto) {
                    productImageView.setImageBitmap(imageBitmap);
                    productImageBitmap = imageBitmap; // Store the current image for saving later
                }
            } else {
                Toast.makeText(this, "Failed to capture image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image capture cancelled.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToDatabase(Bitmap imageBitmap, boolean isProfileImage) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            if (isProfileImage) {
                databaseHelper.addUserProfilePicture(userId, imageBytes);
                Toast.makeText(this, "Profile picture saved to database", Toast.LENGTH_SHORT).show();
            } else {
                if (databaseHelper.vendorExists(userId)) {
                    databaseHelper.addVendorProductPicture(userId, imageBytes);
                    Toast.makeText(this, "Product picture saved to database", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Vendor details not found for user ID: " + userId, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Camera Functionalities
    private void handleCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    /** @noinspection deprecation */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
