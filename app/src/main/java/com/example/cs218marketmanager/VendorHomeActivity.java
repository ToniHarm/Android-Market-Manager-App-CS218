package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VendorHomeActivity extends AppCompatActivity {
    private TextView textViewStatus, textStallnumber;
    private Button vendorAppButton, saveButton;
    private GridLayout gridLayoutStalls;
    private PreferencesHelper preferencesHelper;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();

        vendorAppButton = findViewById(R.id.btnVendorApp);
        textViewStatus = findViewById(R.id.textViewStatus);
        gridLayoutStalls = findViewById(R.id.gridLayoutStalls);
        textStallnumber = findViewById(R.id.textStallnumber);
        saveButton = findViewById(R.id.saveButton);


        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (userId != -1) {
            String applicationStatus = databaseHelper.getApplicationStatus(userId);
            if (applicationStatus != null) {
                // Display the application status in a TextView
                textViewStatus.setText("Application Status: " + applicationStatus);

                // Additional logic if you want to display different actions based on status
                if ("APPROVED".equalsIgnoreCase(applicationStatus)) {
                    textStallnumber.setVisibility(View.VISIBLE);
                    gridLayoutStalls.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                } else {
                    textStallnumber.setVisibility(View.GONE);
                    gridLayoutStalls.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            } else {
                textViewStatus.setText("No application found.");
            }
        } else {
            // Handle invalid user ID
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }


        vendorAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorHomeActivity.this, VendorApplicationActivity.class);
                intent.putExtra("userId", preferencesHelper.getUserId());
                startActivity(intent);
            }
        });



        bnv = findViewById(R.id.nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.vendor_home) {
                    // Do nothing, as we are already in HomeFragment
                    return true;
                } else if (item.getItemId() == R.id.vendor_profile) {
                    Intent intent = new Intent(VendorHomeActivity.this, VendorProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_notification) {
                    Intent intent = new Intent(VendorHomeActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_finance) {
                    Intent intent = new Intent(VendorHomeActivity.this, FinanceFragment.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }
}
