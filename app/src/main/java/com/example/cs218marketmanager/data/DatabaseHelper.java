package com.example.cs218marketmanager.data;
import static android.app.DownloadManager.COLUMN_ID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.data.model.VendorApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "marketmanager.db";
    private static final int DATABASE_VERSION = 2;
    //Tables
    private static final String TABLE_USER = "users";
    private static final String TABLE_VENDOR_APPLICATION = "vendorApplication";

    private static final String COLUMN_ID = "id";


    // Column names for USER table
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FIRST_NAME = "firstName";
    private static final String COLUMN_LAST_NAME = "lastName";
    private static final String COLUMN_PROFILE_PIC = "profilePic";
    private static final String COLUMN_ROLE = "role";

    // Column names for APPLICATION table
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_FIRST_NAME + " TEXT,"
                + COLUMN_LAST_NAME + " TEXT,"
                + COLUMN_PROFILE_PIC + " BLOB,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT" +
                ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_APPLICATION_TABLE = "CREATE TABLE " + TABLE_VENDOR_APPLICATION + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_APPLICATION_TABLE);

        addDefaultAdminUserIfNotExists(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            addDefaultAdminUserIfNotExists(db);
        }
    }
    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_FIRST_NAME,user.getFirstName());
        values.put(COLUMN_LAST_NAME,user.getLastName());
        values.put(COLUMN_ROLE, user.getRole().toString());

        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result;
    }

    public void addDefaultAdminUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "admin"); // Admin username
        values.put(COLUMN_EMAIL, "admin@market.com"); // Admin email
        values.put(COLUMN_PASSWORD, "admin123"); // Admin password (hash it if needed)
        values.put(COLUMN_FIRST_NAME, "Admin");
        values.put(COLUMN_LAST_NAME, "User");
        values.put(COLUMN_ROLE, "ADMIN"); // Role for admin user

        // Insert the admin user into the database
        db.insert(TABLE_USER, null, values);
    }

    private void addDefaultAdminUserIfNotExists(SQLiteDatabase db) {
        // Check if the admin user already exists
        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "='admin'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        // If the admin user does not exist, add it
        if (!cursor.moveToFirst()) {
            addDefaultAdminUser(db);
        }
        cursor.close();
    }

    public void addVendorApplication(long userId, List<String> productNames, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        String joinedProductNames = TextUtils.join(",", productNames); // Using TextUtils to join
        values.put(COLUMN_PRODUCT_NAME, joinedProductNames);
        values.put(COLUMN_STATUS, status);

        db.insert(TABLE_VENDOR_APPLICATION, null, values);
        db.close();
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USERNAME + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{username});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();

            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            if (idIndex != -1) user.setId(cursor.getInt(idIndex));

            int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            if (usernameIndex != -1) user.setUsername(cursor.getString(usernameIndex));

            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            if (emailIndex != -1) user.setEmail(cursor.getString(emailIndex));

            int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            if (passwordIndex != -1) user.setPassword(cursor.getString(passwordIndex));

            int roleIndex = cursor.getColumnIndex(COLUMN_ROLE);
            if (roleIndex != -1) user.setRole(User.Role.valueOf(cursor.getString(roleIndex)));
        }


        cursor.close();
        db.close();
        return user;
    }
    public boolean usernameExists(String username){
        User user = getUser(username);
        if(user == null){
            return false;
        }
        else
            return true;
    }
    public boolean emailExists(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{email});
        if(cursor.moveToFirst()) {
            return true;
        }
        else{
            return false;
        }
    }
    private String getColumnValue(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index != -1) {
            return cursor.getString(index);
        } else {
            return "";
        }
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("users",
                new String[]{"id", "username", "email","password","firstName","lastName","profilePic", "role"},
                "id=?", // Selection criteria
                new String[]{String.valueOf(userId)}, // Selection arguments
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String tempId = getColumnValue(cursor,"id");
            Long id = Long.parseLong(tempId);
            String username = getColumnValue(cursor,"username");
            String email = getColumnValue(cursor,"email");
            String password = getColumnValue(cursor,"password");
            String firstName = getColumnValue(cursor,"firstName");
            String lastName = getColumnValue(cursor,"lastName");
            int profilePicColumnIndex = cursor.getColumnIndex("profilePic");
            byte[] profilePic = cursor.getBlob(profilePicColumnIndex);

            String roleString = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            User.Role role = User.Role.valueOf(roleString);

            User user = new User(id,username,email,firstName,lastName,profilePic,role);
            user.setPassword(password);
            System.out.println("User password from db helper: " + password);
            cursor.close();
            return user;
        }
        return null;
    }

    public List<String> getUserProducts(long userId) {
        List<String> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_PRODUCT_NAME + " FROM " + TABLE_VENDOR_APPLICATION +
                " WHERE " + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                products.add(productName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public String getApplicationStatus(long userId) {
        String status = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_STATUS + " FROM " + TABLE_VENDOR_APPLICATION +
                " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            }
            cursor.close();
        }

        return status; // This will return null if no status is found
    }


    public List<VendorApplication> getAllVendorApplications() {
        List<VendorApplication> vendorApplications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_VENDOR_APPLICATION;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));

                // Fetch the product names and split them into a list
                String productNamesString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                List<String> products = new ArrayList<>();
                if (productNamesString != null && !productNamesString.isEmpty()) {
                    String[] productArray = productNamesString.split(","); // Split by comma
                    for (String product : productArray) {
                        products.add(product.trim()); // Add trimmed product names to the list
                    }
                }

                User user = getUserById(userId); // Fetch the User details
                VendorApplication vendorApplication = new VendorApplication(id, userId, user, products, status);
                vendorApplications.add(vendorApplication);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return vendorApplications;
    }

    public boolean updateApplicationStatus(long applicationId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        // Update the row where the application ID matches
        int result = db.update("vendorApplication", values, "id = ?", new String[]{String.valueOf(applicationId)});

        // Return true if update was successful
        return result > 0;
    }








}
