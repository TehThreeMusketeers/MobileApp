package com.sunicola.setapp.helper;

/**
 * Created by soaresbo on 26/01/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Session table name
    private static final String TABLE_USER = "user";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_UID = "uid";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FIRST_NAME+ " TEXT,"
                + KEY_LAST_NAME+ " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_UID + " TEXT,"
                + KEY_ACCESS_TOKEN + " TEXT,"
                + KEY_SESSION_TOKEN + " TEXT,"
                + KEY_REFRESH_TOKEN + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser( String ... args) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (args.length==4){
            values.put(KEY_UID, "N/A"); // user ID
            values.put(KEY_FIRST_NAME, args[0]); // First Name
            values.put(KEY_LAST_NAME, args[1]); // Last Name
            values.put(KEY_EMAIL, args[2]); // Email
            values.put(KEY_ACCESS_TOKEN, "N/A"); // Access Token ? needs to be set up later
            values.put(KEY_SESSION_TOKEN, args[3]); // Session Token
            values.put(KEY_REFRESH_TOKEN, "N/A"); //Refresh Token used when Access token expires
        }
        else if (args.length == 6){
            values.put(KEY_UID, args[0]); // user ID
            values.put(KEY_FIRST_NAME, args[1]); // First Name
            values.put(KEY_LAST_NAME, args[2]); // Last Name
            values.put(KEY_EMAIL, args[3]); // Email
            values.put(KEY_ACCESS_TOKEN, args[4]); // Access Token
            values.put(KEY_SESSION_TOKEN, "N/A"); // Session Token (Only acquired at login)
            values.put(KEY_REFRESH_TOKEN, args[5]); // Refresh Token used when Access token expires
        }
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New user inserted into SQLite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()){
            String[] names  = cursor.getColumnNames();
            for (String name: names) {
                user.put(name,cursor.getString(cursor.getColumnIndex(name)));
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from SQLite: " + user.toString());
        return user;
    }

    /**
     * Recreate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
