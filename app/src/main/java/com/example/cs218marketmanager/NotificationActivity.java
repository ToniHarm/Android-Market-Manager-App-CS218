package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs218marketmanager.adapters.NotificationAdapter;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.Notification;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();

        // Check if userId is valid
        if (userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        notificationRecyclerView = findViewById(R.id.recyclerViewNotifications);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        // Retrieve vendorId for the logged-in user
        Long vendorId = databaseHelper.getVendorIdForUser(userId);

        // Check if vendorId is valid
        if (vendorId == null) {
            Intent intent = new Intent(this, VendorHomeActivity.class); // Replace with your error handling activity
            startActivity(intent);
            finish();
            return; // Important to return to prevent further execution
        }

        // Fetch notifications for the vendor
        notifications = databaseHelper.getNotifications(vendorId);

        // Set up the RecyclerView with the adapter
        notificationAdapter = new NotificationAdapter(notifications, this);
        notificationRecyclerView.setAdapter(notificationAdapter);

        //Bottom Navigation
        bnv = findViewById(R.id.nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.vendor_home) {
                    Intent intent = new Intent(NotificationActivity.this, VendorHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_profile) {
                    Intent intent = new Intent(NotificationActivity.this, VendorProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_notification) {

                    return true;
                } else if (item.getItemId() == R.id.vendor_setting) {
                    Intent intent = new Intent(NotificationActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_finance) {
                    Intent intent = new Intent(NotificationActivity.this, VendorFinanceActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }
}
