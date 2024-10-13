package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.util.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class VendorApplicationActivity extends AppCompatActivity {
    private TextView breadcrumbText;
    private PreferencesHelper preferencesHelper;
    private DatabaseHelper databaseHelper;
    private Long userId; // Changed to class-level variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_application);

        preferencesHelper = new PreferencesHelper(this);
        userId = preferencesHelper.getUserId(); // Get the logged-in user's ID
        if (userId == null || userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        breadcrumbText = findViewById(R.id.breadcrumbText);
        databaseHelper = new DatabaseHelper(this);

        // Set click listener for breadcrumb navigation
        breadcrumbText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorApplicationActivity.this, VendorHomeActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });


        findViewById(R.id.savePButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedProducts();
            }
        });
    }

    private void saveSelectedProducts() {
        if (userId == null || userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedProducts = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.checkbox_carrot)).isChecked()) {
            selectedProducts.add("Carrot");
        }
        if (((CheckBox) findViewById(R.id.checkbox_tomato)).isChecked()) {
            selectedProducts.add("Tomato");
        }
        if (((CheckBox) findViewById(R.id.checkbox_papayas)).isChecked()) {
            selectedProducts.add("Papayas");
        }
        if (((CheckBox) findViewById(R.id.checkbox_mangoes)).isChecked()) {
            selectedProducts.add("Mangoes");
        }
        if (((CheckBox) findViewById(R.id.checkbox_pineapple)).isChecked()) {
            selectedProducts.add("Pineapple");
        }
        if (((CheckBox) findViewById(R.id.checkbox_coconut)).isChecked()) {
            selectedProducts.add("Coconut");
        }
        if (((CheckBox) findViewById(R.id.checkbox_cabbage)).isChecked()) {
            selectedProducts.add("Cabbage");
        }
        if (((CheckBox) findViewById(R.id.checkbox_dalo)).isChecked()) {
            selectedProducts.add("Dalo");
        }
        if (((CheckBox) findViewById(R.id.checkbox_breadfruit)).isChecked()) {
            selectedProducts.add("Breadfruit");
        }
        if (((CheckBox) findViewById(R.id.checkbox_cassava)).isChecked()) {
            selectedProducts.add("Cassava");
        }
        if (((CheckBox) findViewById(R.id.checkbox_lettuce)).isChecked()) {
            selectedProducts.add("Lettuce");
        }
        if (((CheckBox) findViewById(R.id.checkbox_bean)).isChecked()) {
            selectedProducts.add("Bean");
        }

        // Check if any products are selected
        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Please select at least one product.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Join the selected products into a single string
        String status = "PENDING";
        databaseHelper.addVendorApplication(userId, selectedProducts, status);

        Toast.makeText(this, "Products saved successfully!", Toast.LENGTH_SHORT).show();
    }
}
