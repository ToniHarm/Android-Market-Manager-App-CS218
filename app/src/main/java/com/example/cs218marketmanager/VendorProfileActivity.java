package com.example.cs218marketmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private static final int EDIT_PROFILE_REQUEST_CODE = 0; // Defined as a class-level constant

    private ImageView profileImageView, productImageView;
    private TextView profileDetailsTextView, textViewStallNumber, textViewProduct;
    private BottomNavigationView bnv;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesHelper = new PreferencesHelper(this);
        Long id = preferencesHelper.getUserId();
        if (id == null || id.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        // Initialize Views
        profileImageView = findViewById(R.id.profilePicture);
        productImageView = findViewById(R.id.imageViewProductPhoto); // Assuming this exists in the layout
        profileDetailsTextView = findViewById(R.id.profileDetailsTextView);
        textViewStallNumber = findViewById(R.id.textViewStallNumber);
        textViewProduct = findViewById(R.id.textViewProduct);

        // Find the Edit Profile button by its ID
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VendorProfileActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE); // Start with request code
            }
        });

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                displayUserDetails(user);
                displayVendorDetails(userId);
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }

        // Load saved images
        loadImages();

        // Bottom Navigation
        bnv = findViewById(R.id.nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
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
            }
            // Load product image
            byte[] productImageBytes = databaseHelper.getVendorProductPicture(userId); // Fetch from vendor table
            if (productImageBytes != null) {
                Bitmap productImage = BitmapFactory.decodeByteArray(productImageBytes, 0, productImageBytes.length);
                productImageView.setImageBitmap(productImage);
            } else {
                Toast.makeText(this, "No product image found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayUserDetails(User user) {
        profileDetailsTextView.setText(
                "Username: " + user.getUsername() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "First Name: " + user.getFirstName() + "\n" +
                        "Last Name: " + user.getLastName() + "\n"
        );
    }

    private void displayVendorDetails(Long userId) {
        Vendor vendor = databaseHelper.getVendorDetails(userId); // Retrieve vendor details
        if (vendor != null) {
            textViewStallNumber.setText("Stall Number: " + vendor.getStallNumber());
            // If products is a list, convert it to a comma-separated string
            List<String> productsList = vendor.getProducts(); // Assuming getProducts() returns a List<String>
            if (productsList != null && !productsList.isEmpty()) {
                // Join the list elements into a single string without brackets
                String productsString = TextUtils.join(", ", productsList); // e.g., "Product1, Product2, Product3"
                textViewProduct.setText(productsString);
            } else {
                textViewProduct.setText("No products available");
            }
        } else {
            Log.e("VendorProfile", "Vendor details not found for user ID: " + userId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            User updatedUser = (User) data.getSerializableExtra("UpdatedUser");
            if (updatedUser != null) {
                displayUserDetails(updatedUser);
                loadUserProfilePicture(updatedUser.getId()); // Load updated profile picture from the database
            }
        }
    }

    private void loadUserProfilePicture(long userId) {
        byte[] profileImageBytes = databaseHelper.getUserProfilePicture(userId); // Fetch profile image BLOB
        if (profileImageBytes != null) {
            Bitmap profileImage = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
            profileImageView.setImageBitmap(profileImage);
        }
    }
}
