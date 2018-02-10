package com.sunicola.setapp.storage;

/**
 * Created by soaresbo on 26/01/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {
    // All Static variables

    //To be used during debugging
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SET_Storage";

    // Table Names table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_PHOTON = "photons";
    private static final String TABLE_GROUPS = "groups";

    // Table Columns user
    private static final String KEY_DB_UID = "dbID";
    private static final String KEY_USER_UID = "id";
    private static final String KEY_USER_FIRST_NAME = "first_name";
    private static final String KEY_USER_LAST_NAME = "last_name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_SESSION_TOKEN = "session_token";
    private static final String KEY_USER_REFRESH_TOKEN = "refresh_token";

    // Table Columns photons
    private static final String KEY_DB_PID = "dbID";
    private static final String KEY_DEVICE_API_ID = "id";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_DEVICE_TYPE = "deviceType";
    private static final String KEY_DEVICE_NAME = "deviceName";
    private static final String KEY_DEVICE_GROUP = "deviceGroup";

    // Table Columns groups
    private static final String KEY_DB_GID = "dbID";
    private static final String KEY_GROUP_ID = "groupId";
    private static final String KEY_GROUP_NAME = "groupName";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE =
            "CREATE TABLE "
                + TABLE_USER + "("
                + KEY_DB_UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_UID + " TEXT UNIQUE,"
                + KEY_USER_FIRST_NAME+ " TEXT,"
                + KEY_USER_LAST_NAME+ " TEXT,"
                + KEY_USER_EMAIL + " TEXT UNIQUE,"
                + KEY_USER_ACCESS_TOKEN + " TEXT,"
                + KEY_USER_SESSION_TOKEN + " TEXT,"
                + KEY_USER_REFRESH_TOKEN + " TEXT"
            + ")";

        String CREATE_PHOTON_TABLE =
            "CREATE TABLE "
                + TABLE_PHOTON + "("
                + KEY_DB_PID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DEVICE_API_ID + " TEXT UNIQUE,"
                + KEY_DEVICE_ID + " TEXT UNIQUE,"
                + KEY_DEVICE_TYPE + " TEXT,"
                + KEY_DEVICE_NAME + " TEXT,"
                + KEY_DEVICE_GROUP + " TEXT"
            + ")";

        String CREATE_GROUP_TABLE =
            "CREATE TABLE "
                    + TABLE_GROUPS + "("
                    + KEY_DB_GID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_GROUP_ID+ " TEXT UNIQUE,"
                    + KEY_GROUP_NAME+ " TEXT"
            + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PHOTON_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_UID, user.getId()); // user ID
        values.put(KEY_USER_FIRST_NAME, user.getFirst_name()); // First Name
        values.put(KEY_USER_LAST_NAME, user.getLast_name()); // Last Name
        values.put(KEY_USER_EMAIL, user.getEmail()); // Email
        values.put(KEY_USER_ACCESS_TOKEN, user.getAccess_token()); // Access Token ? needs to be set up later
        values.put(KEY_USER_SESSION_TOKEN, user.getSession_token()); // Session Token
        values.put(KEY_USER_REFRESH_TOKEN, user.getRefresh_token()); //Refresh Token used when Access token expires
        // Inserting Row
        long user_id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New user inserted into SQLite: " + user_id);
    }

    /**
     * Adds photon data to the db in the according key
     * @param photon
     */
    public void addPhoton(Photon photon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_API_ID, photon.getId());
        values.put(KEY_DEVICE_ID, photon.getDeviceId());
        values.put(KEY_DEVICE_TYPE, photon.getDeviceType());
        values.put(KEY_DEVICE_NAME, photon.getDeviceName());
        values.put(KEY_DEVICE_GROUP, photon.getDeviceGroup());
        // Inserting Row
        long photon_id = db.insert(TABLE_PHOTON, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New photon inserted into SQLite: " + photon_id);
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
     * Returns List containig photon objects found in SQLite
     * @return
     */
    public List<Photon> getAllPhotons() {
        List<Photon> photonList = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_PHOTON;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, (String[])null);
        if(cursor.moveToFirst()) {
            String[] names  = cursor.getColumnNames();
            do {
                Photon photon = new Photon();
                for (String name : names){
                    photon.globalSetter(name,cursor.getString(cursor.getColumnIndex(name)));
                }
                photonList.add(photon);
            } while(cursor.moveToNext());
        }
        return photonList;
    }


    /**
     * Deletes all user table
     * */
    public void deleteUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from SQLite");
    }

    public void deletePhotonData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PHOTON, null, null);
        db.close();
        Log.d(TAG, "Deleted all photon from SQLite");
    }

}
