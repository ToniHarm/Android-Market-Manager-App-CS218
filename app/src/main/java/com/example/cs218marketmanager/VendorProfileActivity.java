package com.example.cs218marketmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cs218marketmanager.data.DatabaseHelper;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.util.PreferencesHelper;

public class VendorProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView profileDetailsTextView,textViewStallNumber;
    private Button editButton;
    private DatabaseHelper databaseHelper;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesHelper = new PreferencesHelper(this);
        Long id = preferencesHelper.getUserId();
        if(id == null || id.toString().isEmpty()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_profile);

        //Initialize Views
        profileImageView = findViewById(R.id.profilePicture);
        profileDetailsTextView = findViewById(R.id.profileDetailsTextView);
        textViewStallNumber = findViewById(R.id.textViewStallNumber);
        editButton = findViewById(R.id.editButton);

        databaseHelper = new DatabaseHelper(this);
        preferencesHelper = new PreferencesHelper(this);

        long userId = preferencesHelper.getUserId();
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                displayUserDetails(user);
            } else {
                // Handle case when user is not found in the database
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle invalid user ID
            Toast.makeText(this, "Invalid user ID!", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayUserDetails(User user) {

//        if(user.getProfilePic() != null) {
//            byte[] imageBytes = user.getProfilePic();
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            profileImageView.setImageBitmap(decodedImage);
//        }

        profileDetailsTextView.setText(
                "Username: " + user.getUsername()+ "\n" +
                "Email: " + user.getEmail()+ "\n" +
                "First Name: " + user.getFirstName()+ "\n" +
                "Last Name: " + user.getLastName()+ "\n"

        );

//        textViewStallNumber.setText(
//                "S"
//        );

    }
}
