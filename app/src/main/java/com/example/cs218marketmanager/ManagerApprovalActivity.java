package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs218marketmanager.adapters.VendorApplicationAdapter;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.VendorApplication;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ManagerApprovalActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    private PreferencesHelper preferencesHelper;
    private RecyclerView recyclerView;
    private VendorApplicationAdapter adapter;
    private List<VendorApplication> applicationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_approval); // Use the correct layout for the activity

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();
        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        recyclerView = findViewById(R.id.recyclerViewVendorApplications);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VendorApplicationAdapter(applicationsList, this);
        recyclerView.setAdapter(adapter);

        loadVendorApplications();

        bnv = findViewById(R.id.manager_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.manager_home) {
                    Intent intent = new Intent(ManagerApprovalActivity.this, ManagerHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_application) {

                    return true;
                } else if (item.getItemId() == R.id.manager_payment) {
                    Intent intent = new Intent(ManagerApprovalActivity.this, ManagerAddPayment.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_vendors) {
                    Intent intent = new Intent(ManagerApprovalActivity.this, VendorListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_setting) {
                    Intent intent = new Intent(ManagerApprovalActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }

    private void loadVendorApplications() {
        // Add logic here to fetch data from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<VendorApplication> applications = databaseHelper.getAllVendorApplication();
        adapter.updateApplications(applications);
    }
}
