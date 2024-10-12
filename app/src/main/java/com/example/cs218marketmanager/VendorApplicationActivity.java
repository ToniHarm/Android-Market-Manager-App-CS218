package com.example.cs218marketmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.util.PreferencesHelper;

public class VendorApplicationActivity extends AppCompatActivity {
    private TextView breadcrumbText;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_application);

        preferencesHelper = new PreferencesHelper(this);
        Long userId = preferencesHelper.getUserId();
        if (userId == null || userId.toString().isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        breadcrumbText = findViewById(R.id.breadcrumbText);

        breadcrumbText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VendorApplicationActivity.this, VendorHomeActivity.class);
                intent.putExtra("userId", preferencesHelper.getUserId());
                startActivity(intent);
            }
        });
    }




}
