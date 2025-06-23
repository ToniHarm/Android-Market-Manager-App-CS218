package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerHomeActivity extends AppCompatActivity {
    private PreferencesHelper preferencesHelper;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bnv;
    private TextView profileDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call this first to properly initialize

        // Initialize preferences and database helpers
        preferencesHelper = new PreferencesHelper(this);
        databaseHelper = new DatabaseHelper(this);

        // Check if user ID is valid
        Long id = preferencesHelper.getUserId();
        if (id == null || id == -1) {
            navigateToLogin();
            return; // Exit the method early
        }

        setContentView(R.layout.activity_manager_home);
        profileDetailsTextView = findViewById(R.id.profileDetailsTextView);
        bnv = findViewById(R.id.manager_nav_view);

        // Fetch and display user details
        User user = databaseHelper.getUserById(id);
        if (user != null) {
            displayUserDetails(user);
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
        }

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bnv = findViewById(R.id.manager_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.manager_home) {
                    // Do nothing, as we are already in HomeFragment
                    return true;
                } else if (item.getItemId() == R.id.manager_application) {
                    Intent intent = new Intent(ManagerHomeActivity.this, ManagerApprovalActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_payment) {
                    Intent intent = new Intent(ManagerHomeActivity.this, ManagerAddPayment.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_vendors) {
                    Intent intent = new Intent(ManagerHomeActivity.this, VendorListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_setting) {
                    Intent intent = new Intent(ManagerHomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }

    private void displayUserDetails(User user) {
        profileDetailsTextView.setText(
                "Username: " + user.getUsername() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "First Name: " + user.getFirstName() + "\n" +
                "Last Name: " + user.getLastName() + "\n"
        );
    }
}
