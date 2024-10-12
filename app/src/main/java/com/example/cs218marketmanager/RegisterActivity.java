package com.example.cs218marketmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;

public class RegisterActivity extends Activity {
    private EditText usernameEditText, emailEditText, passwordEditText,firstNameEditText,lastNameEditText;
    TextView textViewExistingUser;
    private Button registerButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        usernameEditText = findViewById(R.id.usernameRegisterEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordRegisterEditText);
        registerButton = findViewById(R.id.completeRegisterButton);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        textViewExistingUser = findViewById(R.id.textViewExistingUser);

        databaseHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty()||lastName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(databaseHelper.usernameExists(username)){
                    Toast.makeText(RegisterActivity.this, "Username Already Exists!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(databaseHelper.emailExists(email)){
                    Toast.makeText(RegisterActivity.this, "Email Already Exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                long result = databaseHelper.addUser(user);

                if (result > 0) {
                    Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textViewExistingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
