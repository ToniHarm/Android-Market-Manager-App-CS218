package com.example.cs218marketmanager;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs218marketmanager.data.DatabaseHelper;

public class ForgetPasswordActivity extends Activity {

    private EditText usernameEditText, securityQuestionEditText, securityAnswerEditText, newPasswordEditText;
    private Button resetPasswordButton;
    private DatabaseHelper dbHelper; // Assuming you have a DatabaseHelper class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);

        // Initialize views
        usernameEditText = findViewById(R.id.etUsername);
        securityQuestionEditText = findViewById(R.id.etSecurityQuestion);
        securityAnswerEditText = findViewById(R.id.etSecurityAnswer);
        newPasswordEditText = findViewById(R.id.etNewPassword);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        dbHelper = new DatabaseHelper(this);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String securityQuestion = securityQuestionEditText.getText().toString();
                String securityAnswer = securityAnswerEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                if (dbHelper.verifySecurityAnswer(username, securityQuestion, securityAnswer)) {
                    dbHelper.updatePassword(username, newPassword);
                    Toast.makeText(ForgetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Invalid details. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


