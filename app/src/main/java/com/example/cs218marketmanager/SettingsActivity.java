package com.example.cs218marketmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.util.PreferencesHelper;

public class SettingsActivity extends AppCompatActivity {
    private Button btnLogout;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesHelper = new PreferencesHelper(this);
        btnLogout = findViewById(R.id.btnLogout);

        // Call the method to set up listeners
        setupListeners();
    }

    private void clearUserSession() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove user-specific data
        editor.remove("username");
        editor.remove("userId");

        // Clear the editor to apply changes
        editor.apply();
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> {
            clearUserSession(); // Clear user data
            Toast.makeText(SettingsActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Close this activity
        });
    }
}
