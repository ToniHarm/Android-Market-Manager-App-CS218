package com.example.cs218marketmanager;

import static com.example.cs218marketmanager.R.id.imageViewProductPhoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.example.cs218marketmanager.data.model.Vendor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class VendorProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int IMAGE_CAPTURE_CODE = 101;

    private ImageView profileImageView, productImageView;
    private TextView profileDetailsTextView, textViewStallNumber, textViewProduct;
    private Button takePhotoButton1, takePhotoButton2, editButton;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;

    private int currentPhotoTarget; // To track which button was clicked

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
        productImageView = findViewById(imageViewProductPhoto); // Assuming this exists in the layout
        profileDetailsTextView = findViewById(R.id.profileDetailsTextView);
        textViewStallNumber = findViewById(R.id.textViewStallNumber);
        textViewProduct = findViewById(R.id.textViewProduct);
        editButton = findViewById(R.id.editButton);
        takePhotoButton1 = findViewById(R.id.takePhotoButton1); // Profile picture button
        takePhotoButton2 = findViewById(R.id.takePhotoButton2); // Product picture button

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

        // Set click listeners for the buttons
        takePhotoButton1.setOnClickListener(v -> {
            currentPhotoTarget = R.id.profilePicture; // Mark as profile image
            handleCameraPermission();
        });

        takePhotoButton2.setOnClickListener(v -> {
            currentPhotoTarget = imageViewProductPhoto; // Mark as product image
            handleCameraPermission();
        });
    }

    private void handleCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

            // Check which ImageView to update
            if (currentPhotoTarget == R.id.profilePicture) {
                profileImageView.setImageBitmap(imageBitmap);
                saveImageToInternalStorage(imageBitmap, "profile_image.jpg"); // Save profile image
            } else if (currentPhotoTarget == imageViewProductPhoto) {
                productImageView.setImageBitmap(imageBitmap);
                saveImageToInternalStorage(imageBitmap, "product_image.jpg"); // Save product image
            }
        }
    }

    private void saveImageToInternalStorage(Bitmap imageBitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadImages() {
        Bitmap profileImage = loadImageFromInternalStorage("profile_image.jpg");
        if (profileImage != null) {
            profileImageView.setImageBitmap(profileImage);
        }

        Bitmap productImage = loadImageFromInternalStorage("product_image.jpg");
        if (productImage != null) {
            productImageView.setImageBitmap(productImage);
        }
    }

    private Bitmap loadImageFromInternalStorage(String fileName) {
        try {
            FileInputStream fis = openFileInput(fileName);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
            List<String> productsList = vendor.getProducts();  // Assuming getProducts() returns a List<String>
            if (productsList != null && !productsList.isEmpty()) {
                // Join the list elements into a single string without brackets
                String productsString = TextUtils.join(", ", productsList);  // e.g., "Product1, Product2, Product3"
                textViewProduct.setText(productsString);
            } else {
                textViewProduct.setText("No products available");
            }
        } else {
            Log.e("VendorProfile", "Vendor details not found for user ID: " + userId);
        }
    }


}
