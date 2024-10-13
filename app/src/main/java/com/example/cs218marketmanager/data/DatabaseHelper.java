package com.example.cs218marketmanager.data;
import static android.app.DownloadManager.COLUMN_ID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.data.model.VendorApplication;
import com.example.cs218marketmanager.data.model.Vendor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "marketmanager.db";
    private static final int DATABASE_VERSION = 3;
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

    // VENDOR table
    private static final String TABLE_VENDOR = "vendors";
    private static final String COLUMN_STALL_NUMBER = "stallNumber";
    private static final String COLUMN_RENT = "rent";
    private static final String COLUMN_PRODUCT_PIC = "productPic";

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

        // Create VENDOR table
        String CREATE_VENDOR_TABLE = "CREATE TABLE " + TABLE_VENDOR + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID + " INTEGER, "
                + COLUMN_PRODUCT_NAME + " TEXT, "
                + COLUMN_STALL_NUMBER + " TEXT, "
                + COLUMN_RENT + " TEXT, "
                + COLUMN_PRODUCT_PIC + " BLOB,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_VENDOR_TABLE);

        addDefaultAdminUserIfNotExists(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Create the VENDOR table if upgrading to version 3 (or later)
        if (oldVersion < 3) {
            String CREATE_VENDOR_TABLE = "CREATE TABLE " + TABLE_VENDOR + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USER_ID + " INTEGER, "
                    + COLUMN_PRODUCT_NAME + " TEXT, "
                    + COLUMN_STALL_NUMBER + " TEXT, "
                    + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
            db.execSQL(CREATE_VENDOR_TABLE);
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
        values.put(COLUMN_STATUS, status); // Ensure status is correct

        db.insert(TABLE_VENDOR_APPLICATION, null, values);
        db.close();
    }

    // Method to save vendor details into the vendor table
    public boolean saveVendorDetails(Long userId, String firstName, String lastName, String email, List<String> products) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        String joinedProductNames = TextUtils.join(",", products);
        values.put(COLUMN_PRODUCT_NAME, joinedProductNames);

        long result = db.insert(TABLE_VENDOR, null, values);
        db.close();
        return result != -1;
    }

    // Method to set the stall number to null in the vendor table
    public boolean setStallNumberToNull(Long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STALL_NUMBER, (String) null); // Setting stall number to null

        long result = db.update(TABLE_VENDOR, values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result != -1;
    }

    // Method to update the stall number and fixed rent for a vendor
    public boolean updateVendorStall(Long userId, String stallNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STALL_NUMBER, stallNumber);
        values.put(COLUMN_RENT, 100);

        long result = db.update(TABLE_VENDOR, values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result != -1; // Return true if the update was successful
    }


    public List<String> getTakenStalls() {
        List<String> takenStalls = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_STALL_NUMBER + " FROM " + TABLE_VENDOR + " WHERE stallNumber IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String stallNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STALL_NUMBER));
                takenStalls.add(stallNumber);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return takenStalls;
    }



    public Vendor getVendorDetails(Long userId) {
        Vendor vendor = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + COLUMN_STALL_NUMBER + ", " + COLUMN_PRODUCT_NAME + " FROM " + TABLE_VENDOR + " WHERE user_id = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            vendor = new Vendor();

            int stallNumberIndex = cursor.getColumnIndex(COLUMN_STALL_NUMBER);
            int productsIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);

            if (stallNumberIndex != -1 && productsIndex != -1) {
                vendor.setStallNumber(cursor.getString(stallNumberIndex));

                // Retrieve products as a comma-separated string and split into a List
                String productsString = cursor.getString(productsIndex);
                List<String> productList = Arrays.asList(productsString.split(",")); // Split by comma
                vendor.setProducts(productList);
            } else {
                Log.e("DatabaseQuery", "Invalid column index");
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return vendor;
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
    private static String getColumnValue(Cursor cursor, String columnName) {
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

    // Method to get Manager details by ID (returns name and email)
    public String[] getManagerDetailsById(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Assuming manager details are stored in the 'users' table with role as 'MANAGER'
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL},
                COLUMN_ID + "=? AND " + COLUMN_ROLE + "=?",
                new String[]{String.valueOf(id), "MANAGER"},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            cursor.close();

            // Return full name (first + last) and email
            return new String[]{firstName + " " + lastName, email};
        }

        if (cursor != null) {
            cursor.close();
        }

        return null; // Return null if no details are found
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


    public List<VendorApplication> getAllVendorApplication() {
        List<VendorApplication> vendorApplication = new ArrayList<>();
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
                VendorApplication vendorApplications = new VendorApplication(id, userId, user, products, status);
                vendorApplication.add(vendorApplications);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return vendorApplication;
    }


    public List<VendorApplication> getApprovedVendorApplications() {
        List<VendorApplication> vendorApplications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query for vendor applications with "approved" status
        String selectQuery = "SELECT * FROM " + TABLE_VENDOR_APPLICATION + " WHERE " + COLUMN_STATUS + " = ?";
        Log.d("VendorApp", "Executing query: " + selectQuery); // Log the query

        Cursor cursor = db.rawQuery(selectQuery, new String[]{"approved"});

        // Check if the cursor has data
        Log.d("VendorApp", "Number of rows in cursor: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));

                Log.d("VendorApp", "Application ID: " + id + ", User ID: " + userId + ", Status: " + status);

                // Fetch the product names and split them into a list
                String productNamesString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                Log.d("VendorApp", "Product names: " + productNamesString);
                List<String> products = new ArrayList<>();
                if (productNamesString != null && !productNamesString.isEmpty()) {
                    String[] productArray = productNamesString.split(","); // Split by comma
                    for (String product : productArray) {
                        products.add(product.trim()); // Add trimmed product names to the list
                    }
                }

                User user = getUserById(userId); // Fetch the User details
                if (user != null) {
                    Log.d("VendorApp", "User found: " + user.getUsername() + " (" + user.getEmail() + ")");
                } else {
                    Log.e("VendorApp", "User with ID " + userId + " not found.");
                }

                VendorApplication vendorApplication = new VendorApplication(id, userId, user, products, status);
                vendorApplications.add(vendorApplication);
            } while (cursor.moveToNext());
        } else {
            Log.e("VendorApp", "No approved vendor applications found.");
        }

        cursor.close();
        db.close();
        return vendorApplications;
    }

    public List<User> getManagers() {
        List<User> managers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query for users with the role of "MANAGER"
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_PROFILE_PIC, COLUMN_PASSWORD, COLUMN_ROLE},
                COLUMN_ROLE + "=?",
                new String[]{"MANAGER"},
                null, null, null);

        // Loop through the results and add each manager to the list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
                byte[] profilePic = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PIC));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
                User.Role role = User.Role.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));

                User user = new User(id, username, email, firstName, lastName, profilePic, role);
                user.setPassword(password); // Set the password if needed
                managers.add(user);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return managers; // Return the list of managers
    }

//    public boolean deleteManagerByEmail(String email) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean isDeleted = false;  // Track whether the deletion was successful
//
//        // Use a try-catch block to handle potential exceptions
//        try {
//            // Delete the manager where the email matches
//            int rowsAffected = db.delete("ManagerTable", "email=?", new String[]{email});
//
//            // Check if any rows were affected
//            isDeleted = rowsAffected > 0;
//
//            // Log the result
//            Log.d("DatabaseHelper", "Delete operation successful: " + isDeleted + " for email: " + email);
//        } catch (Exception e) {
//            // Log the exception
//            Log.e("DatabaseHelper", "Error deleting manager by email: " + email, e);
//        } finally {
//            // Close the database if necessary
//            db.close();
//        }
//
//        return isDeleted;
//    }


    // Method to delete a manager by email
    public boolean deleteManagerById(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Log the email we are trying to delete
        Log.d("DatabaseHelper", "Attempting to delete manager with email: " + email);

        // Execute the delete query: only delete users where the role is MANAGER and the email matches
        int rowsAffected = db.delete("users", "email = ? AND role = ?", new String[]{email, "MANAGER"});

        db.close(); // Always close the database connection

        // Log the result
        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Manager deleted successfully.");
            return true; // Return true if at least one row was deleted
        } else {
            Log.d("DatabaseHelper", "No manager found with the email: " + email);
            return false; // Return false if no rows were affected (i.e., no manager was found)
        }
    }


    public boolean updateApplicationStatus(long applicationId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_VENDOR_APPLICATION, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applicationId)});

        return result > 0; // Return true if update was successful
    }

}
