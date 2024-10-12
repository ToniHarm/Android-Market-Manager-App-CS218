package com.example.cs218marketmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RegisterManagerActivity extends Activity {
    private EditText usernameEditText, emailEditText, passwordEditText, firstNameEditText, lastNameEditText;
    private Button registerButton;
    private DatabaseHelper databaseHelper;
    private BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_register);

        usernameEditText = findViewById(R.id.usernameRegisterEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordRegisterEditText);
        registerButton = findViewById(R.id.managerRegisterButton);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);

        databaseHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    Toast.makeText(RegisterManagerActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (databaseHelper.usernameExists(username)) {
                    Toast.makeText(RegisterManagerActivity.this, "Username Already Exists!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (databaseHelper.emailExists(email)) {
                    Toast.makeText(RegisterManagerActivity.this, "Email Already Exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setRole(User.Role.MANAGER);
                long result = databaseHelper.addUser(user);

                if (result > 0) {
                    Toast.makeText(RegisterManagerActivity.this, "Manager Registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterManagerActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterManagerActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Removed the "Existing User" text view handling

        bnv = findViewById(R.id.admin_nav_view);
        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Using if-else for navigation
                if (item.getItemId() == R.id.admin_home) {
                    Intent intent = new Intent(RegisterManagerActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.admin_register) {
                    // Handle admin register action if needed
                }
                return false; // Default return false for unhandled cases
            }
        });
    }
}
