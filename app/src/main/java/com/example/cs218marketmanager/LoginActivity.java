package com.example.cs218marketmanager;

import android.app.Activity;
import android.content.Intent; // Import Intent to navigate to another Activity
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;

public class LoginActivity extends Activity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView textViewNewUser;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        usernameEditText = findViewById(R.id.et_user_name_login);
        passwordEditText = findViewById(R.id.et_password_login);
        loginButton = findViewById(R.id.btnlogin);
        textViewNewUser = findViewById(R.id.tv_register_from_login);

        databaseHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // Check from database
                User user = databaseHelper.getUser(username);
                if (user != null && password.equals(user.getPassword())) {
                    // Successful login
                    // Save username and userId in shared preferences
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", username);
                    editor.putLong("userId", user.getId());
                    editor.apply();
                    PreferencesHelper preferencesHelper = new PreferencesHelper(LoginActivity.this);

                    // Check user role and navigate accordingly
                    if (user.getRole() == User.Role.ADMIN) {
                        // Navigate to Admin Home
                        startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                    } else if (user.getRole() == User.Role.VENDOR) {
                        // Navigate to Vendor Home
                        startActivity(new Intent(LoginActivity.this, VendorHomeActivity.class));
                    } else if (user.getRole() == User.Role.MANAGER) {
                        // Navigate to Manager Home
                        startActivity(new Intent(LoginActivity.this, ManagerHomeActivity.class));
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        textViewNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterVendorActivity.class));
            }
        });
    }


}
