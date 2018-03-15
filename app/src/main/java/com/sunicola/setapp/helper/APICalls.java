/*
 * Copyright (c) 25. 2. 2018. Orber Soares Bom Jesus
 */

package com.sunicola.setapp.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunicola.setapp.app.AppConfig;
import com.sunicola.setapp.app.AppController;
import com.sunicola.setapp.objects.Group;
import com.sunicola.setapp.objects.GroupType;
import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.storage.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.sunicola.setapp.helper.Util.setHeaders;


public class APICalls {
    private static final String TAG = APICalls.class.getSimpleName();
    private Context _context;
    private String sessionToken;
    private SQLiteHandler db;

    /**
     * API calls Constructor
     * @param context
     */
    public APICalls(Context context) {
        this._context = context;
        //Setup stuff
        db = new SQLiteHandler(_context);
        HashMap<String, String> user = db.getUserDetails();
        sessionToken = user.get("session_token");
    }

    /***********************************
     * Photon Calls
     **********************************/
    /**
     * Updates all photons table in SQLite with data received from server
     */
    public void updateAllDevices() {
        // Tag used to cancel the request
        String tag_string_req = "req_allDevices";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,AppConfig.URL_DEVICES, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        db.deletePhotonData();
                        try{
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i=0; i<jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String deviceId = jsonArray.getJSONObject(i).getString("deviceId");
                                String deviceType = jsonArray.getJSONObject(i).getString("deviceType");
                                String deviceName = jsonArray.getJSONObject(i).getString("deviceName");
                                String deviceGroup = jsonArray.getJSONObject(i).getString("group");
                                db.addPhoton(new Photon(Integer.toString(id),deviceId,deviceType,deviceName,deviceGroup));
                                Log.d(TAG,"Saved Photon with id: " + deviceId);
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Server Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue getting all photons data from server", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /**
     * Updates DeviceTypes Database Table in SQLite
     */
    public void updateAllDeviceTypes(){
        String tag_string_req = "req_all_device_types";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_DEVICES_TYPES,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            db.deleteDeviceType();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i=0; i<jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String value = jsonArray.getJSONObject(i).getString("name");
                                db.addDeviceType(Integer.toString(id),value);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, "getting device Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue getting data from server", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Registers a new photon under this users account
     * @param devID
     * @param devType
     */
    public void registerPhoton(String devID, int devType, String devName) {
        // Tag used to cancel the request
        String tag_string_req = "req_newDevice";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("deviceId", devID);
            jsonBody.put("deviceType", devType);
            jsonBody.put("deviceName", devName);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,AppConfig.URL_DEVICES, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String deviceId = response.getString("deviceId");
                            Toast.makeText(_context,
                                    "Photon " + deviceId + " was created", LENGTH_SHORT)
                                    .show();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration photon Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue adding photon data from server", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /***********************************
     * Group Calls
     **********************************/

    /**
     * Registers new Group
     * @param groupName
     * @param groupType
     * @param devices
     */
    public void registerGroup(String groupName, int groupType, ArrayList<String> devices, int state) {
        // Tag used to cancel the request
        String tag_string_req = "req_newDeviceGroup";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", groupName);
            jsonBody.put("groupType", groupType);
            jsonBody.put("devices", devices);
            jsonBody.put("state", devices);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,AppConfig.URL_GROUPS, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String groupName = response.getString("name");
                            int groupId = response.getInt("id");
                            int groupType = response.getInt("groupType");
                            int state = response.getInt("state");
                            db.addNewGroup(new Group(groupId,groupName,groupType,state));
                            Log.e(TAG, groupName + " ADDED");
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Group Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue adding photon data from server", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /**
     * Updates GroupTypes Database Table in SQLite
     */
    public void updateAllGroups(){
        String tag_string_req = "req_all_group";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_GROUPS,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            db.deleteGroupData();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i=0; i<jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String value = jsonArray.getJSONObject(i).getString("name");
                                int type = jsonArray.getJSONObject(i).getInt("groupType");
                                int state = jsonArray.getJSONObject(i).getInt("state");
                                db.addNewGroup(new Group(id,value,type,state));
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, "getting device Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue getting data from server", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //FIXME: Needsto be looked at because it adds to db but might need changing since current way can't be read back
    /**
     * Updates GrouTypes Database Table in SQLite
     */
    public void updateAllGroupTypes(){
        String tag_string_req = "req_all_group_types";
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_GROUPS_TYPES,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            db.deleteGroupType();
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i=0; i<jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String name = jsonArray.getJSONObject(i).getString("name");
                                JSONArray states = jsonArray.getJSONObject(i).getJSONArray("states");
                                JSONArray variables = jsonArray.getJSONObject(i).getJSONArray("variables");

                                HashMap<String,String> statesMap = new HashMap<>();
                                for (int j = 0; j <states.length() ; j++) {
                                    statesMap.put(Integer.toString(states.getJSONObject(j).getInt("id")),states.getJSONObject(j).getString("state"));
                                }

                                HashMap<String,String> variablesMap = new HashMap<>();
                                for (int b = 0; b <variables.length() ; b++) {
                                    variablesMap.put(Integer.toString(variables.getJSONObject(b).getInt("id")),variables.getJSONObject(b).getString("variable"));
                                }
                                db.addNewGroupType(new GroupType(id,name,statesMap,variablesMap));
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, "getting group types Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue getting data from server", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /***********************************
     * Triggers
     **********************************/
    /**
     * Creates Trigger for this group of photons
     * @param groupID
     * @param state
     * @param valueType
     * @param operator
     * @param value
     * @param function
     */
    public void sendTrigger(int groupID, int state, int valueType, int operator, String value, String function){
        // Tag used to cancel the request
        String tag_string_req = "req_sendTrigger";
        JSONObject jsonBody = new JSONObject();
        JSONObject obj = new JSONObject();
        String act = null;
        if(function.equals("setLight") || function.equals("soundAlarm") || function.equals("stopAlarm")){
            act = "9";
        }

        try {
            obj.put("function", function);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(obj);
        try {
            jsonBody.put("state", state);
            jsonBody.put("valuetype", valueType);
            jsonBody.put("operator", operator);
            jsonBody.put("value", value);
            jsonBody.put("localActions", jsonArray);
            System.out.println("DEBUG" +jsonArray);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        //FIXME: THIS SHOULDNT BE HARDCODED
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,AppConfig.URL_DEVICES+"9/triggers/", jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Trigger descr. Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "Issue sending trigger description", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }


    /***********************************
     * Firebase Calls
     **********************************/

    //TODO: don't know why but it keeps returning an error needs to be fixed
    /**
     * Creates firebase token on the server
     * @param fToken
     */
    public void sendFirebaseToken(String fToken){
        //need to make sure the user is logged in, so the server knows who the token belongs to.
        String tag_string_req = "req_newNotification";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token", fToken);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,AppConfig.URL_NOTIFICATION, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Notification Sent");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Notification Post Error" + error.getMessage());
                        //Toast.makeText(_context,"Issue adding notification token", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    //TODO: don't know why but it keeps returning an error needs to be fixed
    /**
     * Patches Firebase Token in case of expiry
     * @param fToken
     */
    public void patchFirebaseToken(String fToken){
        //TODO: add functionality to be able to PATCH (update) a token in case its refreshed on this endpoint:  PATCH /api/v1/accounts/notifytoken (to update) (when it exists)
        //need to make sure the user is logged in, so the server knows who the token belongs to.
        String tag_string_req = "req_patchNotification";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token", fToken);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT,AppConfig.URL_NOTIFICATION, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Notification Patched");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Notification Patch Error" + error.getMessage());
                        //Toast.makeText(_context,"Issue patching notification token", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /**
     * User location call
     */
    public void patchUserLocation(String location){

        //need to make sure the user is logged in, so the server knows who the token belongs to.
        String tag_string_req = "req_patchLocation";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("zone", location);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT,AppConfig.URL_LOCATION, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG,"Location Patched");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Location patch error" + error.getMessage());
                        //Toast.makeText(_context,"Issue patching notification token", Toast.LENGTH_LONG).show();
                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders(sessionToken);
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }
}
