package com.example.cs218marketmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.util.PreferencesHelper;

public class SettingsActivity extends AppCompatActivity {
    private Button btnLogout, btnResetPassword;
    private PreferencesHelper preferencesHelper;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);

        btnLogout = findViewById(R.id.btnLogout);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResetPasswordDialog(); // Show dialog to get new password
            }
        });

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

    private void showResetPasswordDialog() {
        // Create a dialog to input the new password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString();
                if (isValidPassword(newPassword)) {
                    resetPassword(newPassword);
                } else {
                    Toast.makeText(SettingsActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean isValidPassword(String password) {
        // Implement your password validation logic (length, complexity, etc.)
        return password.length() >= 6; // Example: At least 6 characters
    }

    private void resetPassword(String newPassword) {
        Long userId = preferencesHelper.getUserId(); // Get the user ID from preferences
        if (userId != null) {
            boolean success = databaseHelper.updateUserPassword(userId, newPassword);
            if (success) {
                Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
