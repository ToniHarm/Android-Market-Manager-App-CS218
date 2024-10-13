package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs218marketmanager.adapters.ManagerAdapter;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.Manager;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private PreferencesHelper preferencesHelper;
    private BottomNavigationView bnv;
    private RecyclerView recyclerViewManagers;
    private ManagerAdapter managerAdapter;
    private List<Manager> managerList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();

        if (userId == null || userId.toString().isEmpty()) {
            Log.d("AdminHomeActivity", "User ID is null or empty. Redirecting to Login.");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;  // Make sure to return after finishing the activity
        } else {
            Log.d("AdminHomeActivity", "User ID is: " + userId);
        }

        recyclerViewManagers = findViewById(R.id.recyclerViewManagers);
        recyclerViewManagers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this); // Replace with your actual database helper class

        // Fetch managers from database
        managerList = getManagersFromDatabase();

        managerAdapter = new ManagerAdapter(managerList,this);
        recyclerViewManagers.setAdapter(managerAdapter);


        bnv = findViewById(R.id.admin_nav_view);
        bnv.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.admin_home) {
                return true;
            } else if (item.getItemId() == R.id.admin_register) {
                Intent intent = new Intent(AdminHomeActivity.this, RegisterManagerActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    // Method to get managers from the database
    private List<Manager> getManagersFromDatabase() {
        List<User> users = databaseHelper.getManagers(); // Assuming this returns a list of User objects
        List<Manager> managers = new ArrayList<>();

        // Convert User objects to Manager objects using only name and email
        for (User user : users) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            managers.add(new Manager(fullName, user.getEmail())); // Only name and email
        }

        return managers; // Return the list of Manager objects
    }

}
