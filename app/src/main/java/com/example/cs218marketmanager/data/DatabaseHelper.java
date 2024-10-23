package com.example.cs218marketmanager.data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.cs218marketmanager.data.model.Notification;
import com.example.cs218marketmanager.data.model.User;
import com.example.cs218marketmanager.data.model.VendorApplication;
import com.example.cs218marketmanager.data.model.Vendor;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "marketmanager.db";
    private static final int DATABASE_VERSION = 7;
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
    private static final String COLUMN_VENDOR_ID = "vendor_id";
    private static final String COLUMN_STALL_NUMBER = "stallNumber";
    private static final String COLUMN_RENT = "rent";
    private static final String COLUMN_PRODUCT_PIC = "productPic";
    private static final String COLUMN_TOTAL_BALANCE = "balance";
    private static final String COLUMN_TOTAL_FINES = "fine";
    private static final String COLUMN_TOTAL_PAYMENT = "payment";

    //PAYMENT table
    private static final String TABLE_PAYMENT = "vendorPayments";
    private static final String COLUMN_PAYMENT_DATE = "paymentDate";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DESCRIPTION = "description";

    //Notification table
    private static final String TABLE_NOTIFICATION = "notification";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TIMESTAMP = "notifTime";
    private static final String COLUMN_IS_READ = "isRead";


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
                + COLUMN_TOTAL_BALANCE + " REAL DEFAULT 0.0, "
                + COLUMN_TOTAL_FINES + " REAL DEFAULT 0.0, "
                + COLUMN_TOTAL_PAYMENT + " REAL DEFAULT 0.0, "
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_VENDOR_TABLE);

        // Create PAYMENT table
        String CREATE_PAYMENT_TABLE = "CREATE TABLE " + TABLE_PAYMENT + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VENDOR_ID + " INTEGER, "
                + COLUMN_AMOUNT + " REAL NOT NULL, "
                + COLUMN_PAYMENT_DATE + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_VENDOR_ID + ") REFERENCES " + TABLE_VENDOR + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_PAYMENT_TABLE);

        // Create NOTIFICATION table
        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_NOTIFICATION + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VENDOR_ID + " INTEGER, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_IS_READ + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COLUMN_VENDOR_ID + ") REFERENCES " + TABLE_VENDOR + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_NOTIFICATION_TABLE);

        addDefaultAdminUserIfNotExists(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 7) {
            String CREATE_NOTIFICATION_TABLE = "CREATE TABLE " + TABLE_NOTIFICATION + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_VENDOR_ID + " INTEGER, "
                    + COLUMN_MESSAGE + " TEXT, "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + COLUMN_IS_READ + " INTEGER DEFAULT 0, "
                    + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
            db.execSQL(CREATE_NOTIFICATION_TABLE);
        }
    };
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
        values.put(COLUMN_TOTAL_BALANCE, 100);

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

    public String getVendorStall(Long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String stallNumber = null;

        // Query to get the stall number for the given user ID
        Cursor cursor = db.query(TABLE_VENDOR,
                new String[]{COLUMN_STALL_NUMBER}, // Columns to retrieve
                COLUMN_USER_ID + "=?", // Selection
                new String[]{String.valueOf(userId)}, // Selection args
                null, null, null); // Group By, Having, Order By

        // Check if a result was found
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                stallNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STALL_NUMBER));
            }
            cursor.close();
        }

        return stallNumber;
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


    public void addVendorProductPicture(long vendorId, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_PIC, imageBytes); // Store the product picture
        // Update the product picture in the vendor table for the specific userId
        long result = db.update(TABLE_VENDOR, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        if (result == 0) {
            // Handle the case where no rows were updated (i.e., vendor ID not found)
            Log.e("DatabaseHelper", "Failed to update product picture for user ID: " + userId);
        } else {
            Log.d("DatabaseHelper", "Product picture updated for user ID: " + userId);
        }
        db.close(); // Close the database
    }




    // Add user profile picture for the user
    public void addUserProfilePicture(long userId, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase(); // Open a writable database
        ContentValues values = new ContentValues(); // Create a ContentValues object to hold the data

        values.put(COLUMN_PROFILE_PIC, imageBytes); // Store the profile picture

        // Use an UPSERT pattern to insert or update the profile picture
        // Adjust the SQL statement according to your schema
        long result = db.update(TABLE_USER, values, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});

        if (result == 0) {
            // Handle the case where no rows were updated (i.e., user ID not found)
            Log.e("DatabaseHelper", "Failed to update profile picture for user ID: " + userId);
        } else {
            Log.d("DatabaseHelper", "Profile picture updated for user ID: " + userId);
        }

        db.close(); // Close the database
    }

    // Get user profile picture for the user
    public byte[] getUserProfilePicture(long userId) {
        SQLiteDatabase db = this.getReadableDatabase(); // Open a readable database
        byte[] profilePic = null;

        // Query the database for the profile picture based on the user ID
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_PROFILE_PIC},  // Select the profile picture column
                COLUMN_ID + "=?",  // Where clause (match user ID)
                new String[]{String.valueOf(userId)},  // Where argument (user ID)
                null, null, null);  // No group by, having, or order by

        // If a result is found, retrieve the profile picture
        if (cursor != null && cursor.moveToFirst()) {
            profilePic = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PIC));
        }

        if (cursor != null) {
            cursor.close(); // Close the cursor to avoid memory leaks
        }

        db.close(); // Close the database connection
        return profilePic; // Return the profile picture
    }

    public byte[] getVendorProductPicture(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] productPic = null;
        // Query the database for the product picture based on the user ID
        Cursor cursor = db.query(TABLE_VENDOR,
                new String[]{COLUMN_PRODUCT_PIC}, // Select the product picture column
                COLUMN_USER_ID + "=?", // Where clause (match user ID)
                new String[]{String.valueOf(userId)}, // Where argument (user ID)
                null, null, null); // No group by, having, or order by
        // If a result is found, retrieve the product picture
        if (cursor != null && cursor.moveToFirst()) {
            productPic = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PIC));
        }
        if (cursor != null) {
            cursor.close(); // Close the cursor to avoid memory leaks
        }
        db.close(); // Close the database connection
        return productPic; // Return the product picture
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

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());//or however you name your fields

        // Update the user based on user ID
        db.update("users", values, "id = ?", new String[]{String.valueOf(user.getId())});
        db.close(); // Close the database connection
    }

    public boolean updateApplicationStatus(long applicationId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_VENDOR_APPLICATION, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applicationId)});

        return result > 0; // Return true if update was successful
    }

    public boolean vendorExists(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VENDOR,
                new String[]{COLUMN_ID}, // Select only the vendor ID to check existence
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    public Vendor getVendorFinance(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Vendor vendor = null;

        String selectQuery = "SELECT * FROM " + TABLE_VENDOR + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            vendor = new Vendor();
            vendor.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            vendor.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            vendor.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_BALANCE)));
            vendor.setPayment(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PAYMENT)));
            vendor.setFine(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_FINES)));
            // Add any additional financial data you may have
        }

        cursor.close();
        db.close();
        return vendor;
    }

    public List<String> getAllStallNumbers() {
        List<String> stallNumbers = new ArrayList<>();
        // Query to get all stall numbers from Vendor table
        String query = "SELECT stallNumber FROM vendors";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                stallNumbers.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stallNumbers;
    }

    public boolean vendorExists(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VENDOR,
                new String[]{COLUMN_ID}, // Select only the vendor ID to check existence
                COLUMN_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    public long getVendorIdByStallNumber(String stallNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM vendors WHERE stallNumber = ?", new String[]{stallNumber});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long vendorId = cursor.getLong(0);
                cursor.close();
                return vendorId;
            }
            cursor.close();
        }

        return -1; // Return -1 if vendor not found
    }

    public boolean addPaymentForVendor(String stallNumber, double paymentAmount, String paymentDate, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Start a transaction to ensure data integrity
        db.beginTransaction();
        try {
            // Get vendor ID using stall number
            long vendorId = getVendorIdByStallNumber(stallNumber);
            if (vendorId == -1) {
                return false; // Vendor not found
            }

            // Insert payment into Payments table
            ContentValues paymentValues = new ContentValues();
            paymentValues.put("vendor_id", vendorId); // Use vendorId here
            paymentValues.put("amount", paymentAmount);
            paymentValues.put("paymentDate", paymentDate);
            paymentValues.put("description", description);

            long paymentResult = db.insert("vendorPayments", null, paymentValues);

            // Update total payments and balance due for the vendor
            String updateQuery = "UPDATE vendors SET payment = payment + ?, balance = balance - ? WHERE id = ?";
            SQLiteStatement statement = db.compileStatement(updateQuery);
            statement.bindDouble(1, paymentAmount);
            statement.bindDouble(2, paymentAmount);
            statement.bindLong(3, vendorId);
            statement.executeUpdateDelete();

            db.setTransactionSuccessful();

            // Check if payment was inserted successfully
            return paymentResult != -1; // Returns true if payment was inserted
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        } finally {
            db.endTransaction();
        }
    }

    public boolean addFineForVendor(String stallNumber, double fineAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Get vendor ID using stall number
            long vendorId = getVendorIdByStallNumber(stallNumber);
            if (vendorId == -1) {
                return false; // Vendor not found
            }

            // Get current balance from the vendors table
            double currentBalance = getBalanceDueByStallNumber(stallNumber);

            // Add fine amount to current balance
            double newBalance = currentBalance + fineAmount;

            // Update the balance in the vendors table
            ContentValues balanceValues = new ContentValues();
            balanceValues.put("balance", newBalance);

            // Update the vendor's balance in the vendors table
            int updateResult = db.update("vendors", balanceValues, "id = ?", new String[]{String.valueOf(vendorId)});

            // Check if the balance update was successful
            if (updateResult == -1) {
                return false; // Update failed
            }

            // Insert fine into the vendors
            ContentValues fineValues = new ContentValues();
            fineValues.put("fine", fineAmount);
            long fineResult = db.insert("vendors", null, fineValues);

            // Commit the transaction
            db.setTransactionSuccessful();

            // Return true if everything was successful
            return fineResult != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        } finally {
            db.endTransaction();
        }
    }


    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public double getBalanceDueByStallNumber(String stallNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT balance FROM vendors WHERE stallNumber = ?";
        Cursor cursor = db.rawQuery(query, new String[]{stallNumber});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                double balanceDue = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_BALANCE));
                cursor.close();
                return balanceDue;
            }
            cursor.close();
        }
        return 0; // Default value if no balance due found
    }

    public boolean createNotification(Long vendorId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VENDOR_ID, vendorId);
        values.put(COLUMN_MESSAGE, message);
        long result = db.insert(TABLE_NOTIFICATION, null, values);
        return result != -1;  // Return true if successful
    }

    public List<Notification> getNotifications(Long vendorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Notification> notifications = new ArrayList<>();

        // Query to get notifications for a specific vendor, ordered by timestamp
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION + " WHERE " + COLUMN_VENDOR_ID + " = ? " + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        // Pass the vendorId as an argument to bind to the query
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(vendorId)});

        if (cursor.moveToFirst()) {
            do {
                // Retrieve data from the cursor
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)); // id
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)); // message
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)); // timestamp
                boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1; // isRead

                // Create the Notification object and set its properties
                Notification notification = new Notification();
                notification.setId(id);
                notification.setMessage(message);
                notification.setTimestamp(timestamp);
                notification.setRead(isRead);

                // Add the notification to the list
                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Close the cursor after use
        return notifications;
    }

    public Long getVendorIdForUser(Long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long vendorId = null;

        String selectQuery = "SELECT " + COLUMN_ID + " FROM " + TABLE_VENDOR + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                vendorId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            }
            cursor.close();
        }

        return vendorId;
    }









}
