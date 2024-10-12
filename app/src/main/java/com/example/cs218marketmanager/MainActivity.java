package com.example.cs218marketmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in or not
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            // User is logged in, redirect to HomePageActivity
            startActivity(new Intent(MainActivity.this, HomeFragment.class));
        }

        // Close MainActivity so it's not in the back stack
        finish();
    }
}
