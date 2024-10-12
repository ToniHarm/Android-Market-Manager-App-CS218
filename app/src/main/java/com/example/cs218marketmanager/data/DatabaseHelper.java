package com.example.cs218marketmanager.data;
import static android.app.DownloadManager.COLUMN_ID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cs218marketmanager.data.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "marketmanager.db";
    private static final int DATABASE_VERSION = 2;
    //Tables
    private static final String TABLE_USER = "users";

    // Column names for USER table
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FIRST_NAME = "firstName";
    private static final String COLUMN_LAST_NAME = "lastName";
    private static final String COLUMN_PROFILE_PIC = "profilePic";
    private static final String COLUMN_ROLE = "role";

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



}
