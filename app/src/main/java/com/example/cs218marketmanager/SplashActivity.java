package com.example.cs218marketmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Handle window insets for edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        // Apply fade-in animation to the TextView with a delay
//        new Handler().postDelayed(() -> {
//            // Create fade-in animation for the text
//            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
//            fadeIn.setDuration(1500); // Animation duration (1.5 seconds)
//            animatedTextView.startAnimation(fadeIn);
//            animatedTextView.setVisibility(View.VISIBLE); // Make text visible after animation
//        }, 2000); // Delay before starting animation (2 seconds)

        // Set a delay before launching the MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close SplashActivity
        }, 4500); // Duration for splash screen (4.5 seconds)
    }
}
