package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs218marketmanager.adapters.NotificationAdapter;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.Notification;
import com.example.cs218marketmanager.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification); // Use the correct layout for the activity

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();

        // Check if userId is valid
        if (userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Important to return after starting the activity
        }

        notificationRecyclerView = findViewById(R.id.recyclerViewNotifications);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this); // Initialize DatabaseHelper

        // Retrieve vendorId for the logged-in user
        Long vendorId = databaseHelper.getVendorIdForUser(userId);

        // Check if vendorId is valid
        if (vendorId == null) {
            // Handle the case where vendorId is not found
            // You may want to show a message or handle it accordingly
            // For example, redirect to another activity or show a dialog
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
    }
}
