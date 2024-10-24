package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs218marketmanager.adapters.VendorAdapter;
import com.example.cs218marketmanager.adapters.VendorApplicationAdapter;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.Vendor;
import com.example.cs218marketmanager.data.model.VendorApplication;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class VendorListActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    private PreferencesHelper preferencesHelper;
    private RecyclerView recyclerView;
    private VendorAdapter vendorAdapter;
    private DatabaseHelper databaseHelper;
    private List<Vendor> vendorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_list);

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();
        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        recyclerView = findViewById(R.id.recyclerViewVendorProfile);

        vendorList = databaseHelper.getAllVendors();
        if (vendorList == null || vendorList.isEmpty()) {
            vendorList = new ArrayList<>();
        }

        vendorAdapter = new VendorAdapter(this, vendorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(vendorAdapter);

        bnv = findViewById(R.id.manager_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.manager_home) {
                    Intent intent = new Intent(VendorListActivity.this, ManagerHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_application) {
                    Intent intent = new Intent(VendorListActivity.this, ManagerApprovalActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_payment) {
                    Intent intent = new Intent(VendorListActivity.this, ManagerAddPayment.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.manager_vendors) {

                    return true;
                } else if (item.getItemId() == R.id.manager_setting) {
                    Intent intent = new Intent(VendorListActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });
    }
}
