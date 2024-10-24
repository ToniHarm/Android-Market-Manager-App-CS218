package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class VendorHomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView textViewStatus, textStallnumber, textSelectedStall;
    private Button vendorAppButton, saveButton;
    private GridLayout gridLayoutStalls;
    private PreferencesHelper preferencesHelper;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bnv;
    private CheckBox selectedStallCheckBox = null;
    private GoogleMap mMap;

    //For refreshing
    private ActivityResultLauncher<Intent> vendorAppLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // Refresh the application status when returning from VendorApplicationActivity
                        refreshApplicationStatus();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_home);

        //initialize attributes
        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();

        vendorAppButton = findViewById(R.id.btnVendorApp);
        textViewStatus = findViewById(R.id.textViewStatus);
        gridLayoutStalls = findViewById(R.id.gridLayoutStalls);
        textStallnumber = findViewById(R.id.textStallnumber);
        textSelectedStall = findViewById(R.id.textSelectedStall);
        saveButton = findViewById(R.id.saveButton);

        //check if user is logged in
        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Get the SupportMapFragment and register for the map ready callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Async call to load the map
        }

        // Application button to launch the VendorApplicationActivity for Refreshing
        vendorAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorHomeActivity.this, VendorApplicationActivity.class);
                intent.putExtra("userId", preferencesHelper.getUserId());
                vendorAppLauncher.launch(intent);
            }
        });

        //Home Page Activity
        if (userId != -1) {
            String applicationStatus = databaseHelper.getApplicationStatus(userId);

            if (applicationStatus != null && !applicationStatus.isEmpty()) {
                // Display the application status in a TextView
                textViewStatus.setText("Application Status: " + applicationStatus);
                vendorAppButton.setVisibility(View.GONE);

                // Check if the application is APPROVED
                if ("APPROVED".equalsIgnoreCase(applicationStatus)) {
                    // If approved, show stall number and save button
                    textStallnumber.setVisibility(View.VISIBLE);
                    gridLayoutStalls.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                } else {
                    // If not approved, hide stall number and save button
                    textStallnumber.setVisibility(View.GONE);
                    gridLayoutStalls.setVisibility(View.GONE);
                    saveButton.setVisibility(View.GONE);
                }
            } else {
                // No application found, ensure the stall and save button are hidden
                textViewStatus.setText("No application found.");
                textStallnumber.setVisibility(View.GONE);
                gridLayoutStalls.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        } else {
            // Handle invalid user ID
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }

        //Start vendor application activity when application is submitted
        vendorAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorHomeActivity.this, VendorApplicationActivity.class);
                intent.putExtra("userId", preferencesHelper.getUserId());
                startActivity(intent);
            }
        });

        // Query the database for all taken stalls
        List<String> takenStalls = databaseHelper.getTakenStalls();
        String selectedStall = databaseHelper.getVendorStall(userId);

        // Load the stall selection logic
        loadStalls(userId);

        //Save selected stall
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedStallCheckBox != null && selectedStallCheckBox.isChecked()) {
                    String selectedStall = selectedStallCheckBox.getText().toString();
                    if (updateVendorStall(userId, selectedStall)) {
                        Toast.makeText(VendorHomeActivity.this, "Stall selected successfully!", Toast.LENGTH_SHORT).show();

                        // Hide the grid layout and save button after stall selection
                        gridLayoutStalls.setVisibility(View.GONE);
                        saveButton.setVisibility(View.GONE);
                        textStallnumber.setVisibility(View.GONE);

                        // Update the application status to reflect the stall selection
                        textViewStatus.setText("Stall " + selectedStall + " selected. Application approved.");

                    } else {
                        Toast.makeText(VendorHomeActivity.this, "Failed to update stall. Try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VendorHomeActivity.this, "Please select a stall.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Bottom Navigation
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
                    Intent intent = new Intent(VendorHomeActivity.this, VendorFinanceActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.vendor_setting) {
                    Intent intent = new Intent(VendorHomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }

                return false; // Default return false for unhandled cases
            }
        });

        refreshApplicationStatus();

    }

    private void loadStalls(Long userId) {
        // Query the database for all taken stalls
        List<String> takenStalls = databaseHelper.getTakenStalls();
        String selectedStall = databaseHelper.getVendorStall(userId); // Get the selected stall for the user

        // Check if all stalls are taken
        if (takenStalls.size() == gridLayoutStalls.getChildCount()) {
            textViewStatus.setText("All stalls are occupied.");
            gridLayoutStalls.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < gridLayoutStalls.getChildCount(); i++) {
                View view = gridLayoutStalls.getChildAt(i);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    String stallNumber = checkBox.getText().toString();

                    // Hide the checkbox if the stall is already taken
                    if (takenStalls.contains(stallNumber)) {
                        checkBox.setVisibility(View.GONE);
                    } else {
                        // Show the selected stall
                        if (selectedStall != null) {
                            checkBox.setChecked(stallNumber.equals(selectedStall)); // Check the selected stall
                            checkBox.setEnabled(false); // Disable interactions with the checkbox
                        }

                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedStallCheckBox == null) {
                                    selectedStallCheckBox = checkBox; // Mark current as selected
                                    textSelectedStall.setText("Selected Stall: " + stallNumber); // Display selected stall
                                    textSelectedStall.setVisibility(View.VISIBLE); // Show the TextView

                                } else {
                                    Toast.makeText(VendorHomeActivity.this, "You can only select one stall.", Toast.LENGTH_SHORT).show();
                                    checkBox.setChecked(false); // Uncheck the newly selected checkbox
                                }
                            }
                        });
                    }
                }
            }
        }

        // Display selected stall if exists
        if (selectedStall != null) {
            textSelectedStall.setText("Selected Stall: " + selectedStall);
            textSelectedStall.setVisibility(View.VISIBLE); // Show the TextView
            gridLayoutStalls.setVisibility(View.GONE); // Hide the stall selection grid
            saveButton.setVisibility(View.GONE); // Hide the save button
        }
    }


    private boolean updateVendorStall(Long userId, String stallNumber) {
        // Call the method in DatabaseHelper to update the stall number
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        return databaseHelper.updateVendorStall(userId, stallNumber);
    }


    // Refresh method to reload the application status
    private void refreshApplicationStatus() {
        Long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            String applicationStatus = databaseHelper.getApplicationStatus(userId);

            if (applicationStatus != null && !applicationStatus.isEmpty()) {
                textViewStatus.setText("Application Status: " + applicationStatus);

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
                textStallnumber.setVisibility(View.GONE);
                gridLayoutStalls.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for Suva Municipal Market (Example LatLng coordinates)
        LatLng suvaMarket = new LatLng(-18.136160894299973, 178.4250712491223);  // Example coordinates for Suva Market
        mMap.addMarker(new MarkerOptions().position(suvaMarket).title("Suva Municipal Market"));

        // Move camera to the location and set zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(suvaMarket, 15f));
    }

}
