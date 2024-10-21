package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VendorFinanceActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;

    protected void onCreate(Bundle savedInstanceState) {
        preferencesHelper = new PreferencesHelper(this);
        Long id = preferencesHelper.getUserId();
        if (id == null || id.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_finance);

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);

        long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }

        //Bottom Navigation
        bnv = findViewById(R.id.nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.vendor_home) {
                    Intent intent = new Intent(VendorFinanceActivity.this, VendorHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_profile) {
                    Intent intent = new Intent(VendorFinanceActivity.this, VendorProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_notification) {
                    Intent intent = new Intent(VendorFinanceActivity.this, NotificationActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_setting) {
                    Intent intent = new Intent(VendorFinanceActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_finance) {

                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }
}
