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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunicola.setapp.objects.Group;
import com.sunicola.setapp.objects.GroupType;
import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.objects.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {
    private ObjectMapper mapper = new ObjectMapper();

    //To be used during debugging
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SET_Storage";

    // Table Names table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_PHOTON = "photons";
    private static final String TABLE_PHOTON_TYPE =  "types";
    private static final String TABLE_GROUP = "groups";
    private static final String TABLE_GROUP_TYPE = "groupTypes";

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

    // Table Device Types
    private static final String KEY_DB_TID = "dbID";
    private static final String KEY_TYPE_ID = "id";
    private static final String KEY_TYPE_NAME = "value";

    // Table Columns groups
    private static final String KEY_DB_GID = "dbID";
    private static final String KEY_GROUP_ID = "id";
    private static final String KEY_GROUP_NAME = "name";
    private static final String KEY_GROUP_TYPE = "groupType";
    private static final String KEY_GROUP_STATE = "state";

    // Table Columns group Types
    private static final String KEY_DB_GTID = "dbID";
    private static final String KEY_GROUP_TYPE_ID = "id";
    private static final String KEY_GROUP_TYPE_OBJECT = "object";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE =
            "CREATE TABLE "
                + TABLE_USER + "("
                + KEY_DB_UID + " INTEGER PRIMARY KEY,"
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
                + KEY_DB_PID + " INTEGER PRIMARY KEY,"
                + KEY_DEVICE_API_ID + " TEXT UNIQUE,"
                + KEY_DEVICE_ID + " TEXT UNIQUE,"
                + KEY_DEVICE_TYPE + " TEXT,"
                + KEY_DEVICE_NAME + " TEXT,"
                + KEY_DEVICE_GROUP + " TEXT"
            + ")";

        String CREATE_TYPE_TABLE =
            "CREATE TABLE "
                + TABLE_PHOTON_TYPE + "("
                + KEY_DB_TID + " INTEGER PRIMARY KEY,"
                + KEY_TYPE_ID+ " TEXT UNIQUE,"
                + KEY_TYPE_NAME+ " TEXT"
             + ")";

        String CREATE_GROUP_TABLE =
            "CREATE TABLE "
                + TABLE_GROUP + "("
                + KEY_DB_GID + " INTEGER PRIMARY KEY,"
                + KEY_GROUP_ID+ " TEXT UNIQUE,"
                + KEY_GROUP_NAME+ " TEXT,"
                + KEY_GROUP_TYPE+ " TEXT"
            + ")";

        String CREATE_GROUP_TYPE_TABLE =
            "CREATE TABLE "
                + TABLE_GROUP_TYPE+ "("
                + KEY_DB_GTID+ " INTEGER PRIMARY KEY,"
                + KEY_GROUP_TYPE_ID+ " TEXT UNIQUE,"
                + KEY_GROUP_TYPE_OBJECT+ " TEXT"
            + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PHOTON_TABLE);
        db.execSQL(CREATE_TYPE_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_GROUP_TYPE_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTON_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_TYPE);
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
     * Returns List containing photon objects found in SQLite
     * @return
     */
    public List<Photon> getAllPhotons() {
        List<Photon> photonList = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_PHOTON;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
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
        Log.d(TAG, "Fetching photons from SQLite");
        return photonList;
    }

    public Photon[] getAllPhotonsArr() {
        ArrayList<Photon> photonList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PHOTON;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String[] names = cursor.getColumnNames();
            do {
                Photon photon = new Photon();
                for (String name : names) {
                    photon.globalSetter(name, cursor.getString(cursor.getColumnIndex(name)));
                }
                photonList.add(photon);
            } while (cursor.moveToNext());
        }
        Photon[] arr = photonList.toArray(new Photon[photonList.size()]);
        Log.d(TAG, "Fetching photons from SQLite");
        return arr;
    }

    /**
     * Adds new Device Type to DB
     * @param id
     * @param value
     */
    public void addDeviceType(String id, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE_ID, id);
        values.put(KEY_TYPE_NAME, value);
        // Inserting Row
        long deviceType_id= db.insert(TABLE_PHOTON_TYPE, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New device type inserted into SQLite: " + deviceType_id);
    }

    /**
     * Returns List containing photon objects found in SQLite
     * @return
     */
    public HashMap<String,String> getAllDeviceTypes() {
        HashMap<String, String> devTypes = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_PHOTON_TYPE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()){
            do {
                devTypes.put(cursor.getString(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("value")));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching device types from SQLite: " + devTypes.toString());
        return devTypes;
    }

    /**
     * Adds photon data to the db in the according key
     * @param group
     */
    public void addNewGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GROUP_ID, group.getGroupType());
        values.put(KEY_GROUP_NAME, group.getName());
        values.put(KEY_GROUP_TYPE, group.getGroupType());
        values.put(KEY_GROUP_STATE, group.getState());
        // Inserting Row
        long group_id = db.insert(TABLE_GROUP, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New Group inserted into SQLite: " + group_id);
    }

    /**
     * Returns List containing Group objects found in SQLite
     * @return
     */
    public List<Group> getDevGroups() {
        List<Group> groupList = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_GROUP;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                Group group = new Group();
                group.setId(cursor.getInt(cursor.getColumnIndex("id")));
                group.setName(cursor.getString(cursor.getColumnIndex("name")));
                group.setGroupType(cursor.getInt(cursor.getColumnIndex("groupType")));
                group.setState(cursor.getInt(cursor.getColumnIndex("state")));
                groupList.add(group);
            } while(cursor.moveToNext());
        }
        Log.d(TAG, "Fetching Groups from SQLite");
        return groupList;
    }

    /**
     * Add new group type
     * @param groupType
     */
    public void addNewGroupType(GroupType groupType) throws JsonProcessingException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String groupObj = mapper.writeValueAsString(groupType);
        values.put(KEY_GROUP_TYPE_ID, groupType.getId());
        values.put(KEY_GROUP_TYPE_OBJECT, groupObj);
        // Inserting Row
        long groupType_id = db.insert(TABLE_GROUP_TYPE, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New Group Type inserted into SQLite: " + groupType_id);
    }

    //FIXME: Not working
    /**
     * Returns a list with all group types
     * @return
     * @throws IOException
     */
    public List<GroupType> getAllGroupTypes() throws IOException {
        List<GroupType> devicesGroup = new ArrayList();
        String selectQuery = "SELECT * FROM " + TABLE_GROUP_TYPE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                GroupType groupType = mapper.readValue(String.valueOf(cursor.getInt(cursor.getColumnIndex("object"))), new TypeReference<GroupType>() { });
                devicesGroup.add(groupType);
            } while(cursor.moveToNext());
        }
        Log.d(TAG, "Fetching Groups from SQLite");
        return devicesGroup;
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
    public void deleteDeviceType() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PHOTON_TYPE, null, null);
        db.close();
        Log.d(TAG, "Deleted all device types from SQLite");
    }
    public void deleteGroupData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_GROUP, null, null);
        db.close();
        Log.d(TAG, "Deleted all groups from SQLite");
    }
    public void deleteGroupType() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_GROUP_TYPE, null, null);
        db.close();
        Log.d(TAG, "Deleted all group types from SQLite");
    }
}