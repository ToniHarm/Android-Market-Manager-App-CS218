package com.example.cs218marketmanager;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs218marketmanager.adapters.VendorApplicationAdapter;
import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.VendorApplication;

import java.util.ArrayList;
import java.util.List;

public class VendorApprovalActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VendorApplicationAdapter adapter;
    private List<VendorApplication> applicationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_application_approval); // Use the correct layout for the activity

        recyclerView = findViewById(R.id.recyclerViewVendorApplications);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VendorApplicationAdapter(applicationsList, this);
        recyclerView.setAdapter(adapter);

        loadVendorApplication();
    }

    private void loadVendorApplication() {
        // Add logic here to fetch data from the database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<VendorApplication> applications = databaseHelper.getAllVendorApplication();
        adapter.updateApplications(applications);
    }
}
