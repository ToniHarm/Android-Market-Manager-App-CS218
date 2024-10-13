package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerHomeActivity extends AppCompatActivity {
    private PreferencesHelper preferencesHelper;
    private BottomNavigationView bnv;

    private TextView textViewFirstName;
    private DatabaseHelper databaseHelper; // Add a database helper


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        databaseHelper = new DatabaseHelper(this);

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();


        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }



        // Initialize the TextView for manager details
        textViewFirstName = findViewById(R.id.textViewFirstName);

        // Display manager details
        displayManagerDetails(userId);

        bnv = findViewById(R.id.manager_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.manager_home) {
                    // Do nothing, as we are already in HomeFragment
                    return true;
                }
                else if (item.getItemId() == R.id.profile_vendors) {
                    Intent intent = new Intent(ManagerHomeActivity.this, ViewVendorAccountsActivity.class);
                    startActivity(intent);
                    return true;}

                else if (item.getItemId() == R.id.manager_application) {
                    Intent intent = new Intent(ManagerHomeActivity.this, VendorApprovalActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_payment) {
                    Intent intent = new Intent(ManagerHomeActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }

    // Method to retrieve and display the manager's details
    private void displayManagerDetails(Long userId) {
        // Fetch manager details directly from the database
        User manager = databaseHelper.getUserById(userId);

        // Check if the user exists and update the TextView
        if (manager != null) {
            String displayText = "Name: " + manager.getFirstName() + " " + manager.getLastName() + "\nEmail: " + manager.getEmail();
            textViewFirstName.setText(displayText); // Set the manager's details in the TextView
        } else {
            textViewFirstName.setText("Manager details not found."); // Handle case where manager details aren't available
        }
    }
}
