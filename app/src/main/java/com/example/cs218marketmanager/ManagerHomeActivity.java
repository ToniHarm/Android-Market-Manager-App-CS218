package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerHomeActivity extends AppCompatActivity {
    private PreferencesHelper preferencesHelper;
    private BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();
        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        bnv = findViewById(R.id.manager_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.manager_home) {
                    // Do nothing, as we are already in HomeFragment
                    return true;
                } else if (item.getItemId() == R.id.manager_application) {
                    Intent intent = new Intent(ManagerHomeActivity.this, VendorProfileActivity.class);
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
}
