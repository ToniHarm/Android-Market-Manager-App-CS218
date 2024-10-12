package com.example.cs218marketmanager;

import android.app.Activity;
import android.content.Intent; // Import Intent to navigate to another Activity
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

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

                    startActivity(new Intent(LoginActivity.this, HomeFragment.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        textViewNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }


}
