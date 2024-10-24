package com.example.cs218marketmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.data.model.Vendor;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class VendorProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 101;
    private static final int EDIT_PROFILE_REQUEST_CODE = 0;

    private ImageView profileImageView, productImageView;
    private TextView profileDetailsTextView, textViewStallNumber, textViewProduct;
    private BottomNavigationView bnv;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;

    private Button editButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        // Initialize Views
        profileImageView = findViewById(R.id.profilePicture);
        productImageView = findViewById(R.id.imageViewProductPhoto);
        profileDetailsTextView = findViewById(R.id.profileDetailsTextView);
        textViewStallNumber = findViewById(R.id.textViewStallNumber);
        textViewProduct = findViewById(R.id.textViewProduct);
        editButton = findViewById(R.id.editButton);

        editButton.setVisibility(View.GONE); // Initially hide the button

        // Initialize database and preferences helpers
        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        long userId = preferencesHelper.getUserId();

        // Check for a valid user ID
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                displayUserDetails(user);
                displayVendorDetails(userId);
                updateEditButtonVisibility(userId);
            } else {
                Toast.makeText(this, "User details not found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Exit on invalid user ID
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        // Load saved images
        loadImages();

        // Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        // Bottom Navigation
        BottomNavigationView bnv = findViewById(R.id.nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.vendor_home) {
                    Intent intent = new Intent(VendorProfileActivity.this, VendorHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_profile) {
                    return true;
                } else if (item.getItemId() == R.id.vendor_notification) {
                    Intent intent = new Intent(VendorProfileActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_finance) {
                    Intent intent = new Intent(VendorProfileActivity.this, VendorFinanceActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_setting) {
                    Intent intent = new Intent(VendorProfileActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false; // Default return false for unhandled cases
            }
        });
    }

    private void loadImages() {
        long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            // Load profile image
            byte[] profileImageBytes = databaseHelper.getUserProfilePicture(userId);
            if (profileImageBytes != null) {
                Bitmap profileImage = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
                profileImageView.setImageBitmap(profileImage);
            } else {
                profileImageView.setImageResource(R.drawable.profile_avatar); // Set a default image if none found
            }
            // Load product image
            byte[] productImageBytes = databaseHelper.getVendorProductPicture(userId); // Fetch from vendor table
            if (productImageBytes != null) {
                Bitmap productImage = BitmapFactory.decodeByteArray(productImageBytes, 0, productImageBytes.length);
                productImageView.setImageBitmap(productImage);
            } else {
                productImageView.setImageResource(R.drawable.vegetable_market); // Set a default image if none found
            }
        } else {
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUserDetails(User user) {
        profileDetailsTextView.setText(
                "" + user.getUsername() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "First Name: " + user.getFirstName() + "\n" +
                        "Last Name: " + user.getLastName() + "\n"
        );
    }
    private void displayVendorDetails(Long userId) {
        Vendor vendor = databaseHelper.getVendorDetails(userId);
        if (vendor != null) {
            textViewStallNumber.setText("Stall Number: " + vendor.getStallNumber());
            List<String> productsList = vendor.getProducts();
            textViewProduct.setText((productsList != null && !productsList.isEmpty())
                    ? TextUtils.join(", ", productsList)
                    : "No products available");
        } else {
            Log.e("VendorProfile", "Vendor details not found for user ID: " + userId);
            textViewStallNumber.setText("No stall number available");
            textViewProduct.setText("No products available");
        }
    }

    private void updateEditButtonVisibility(Long userId) {
        String applicationStatus = databaseHelper.getApplicationStatus(userId);
        editButton.setVisibility("APPROVED".equalsIgnoreCase(applicationStatus) ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            User updatedUser = (User) data.getSerializableExtra("UpdatedUser");
            if (updatedUser != null) {
                displayUserDetails(updatedUser);
                loadUserProfilePicture(updatedUser.getId());
            }
        }
    }

    private void loadUserProfilePicture(long userId) {
        byte[] profileImageBytes = databaseHelper.getUserProfilePicture(userId);
        if (profileImageBytes != null) {
            Bitmap profileImage = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
            profileImageView.setImageBitmap(profileImage);
        } else {
            profileImageView.setImageResource(R.drawable.profile_avatar); // Set a default image if none found
        }
    }

    public void updateVendorApprovalStatus(long vendorId, boolean isApproved) {
        Vendor vendor = databaseHelper.getVendorDetails(vendorId);
        if (vendor != null) {
            vendor.setApplicationApproved(isApproved);
            editButton.setVisibility(isApproved ? View.VISIBLE : View.GONE);
        } else {
            Toast.makeText(this, "Vendor details not found!", Toast.LENGTH_SHORT).show();
        }
    }
}
