package com.sunicola.setapp.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sunicola.setapp.app.AppConfig;
import com.sunicola.setapp.app.AppController;
import com.sunicola.setapp.objects.Group;
import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.storage.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * Created by soaresbo on 05/02/2018.
 */

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

    /**
     * NEEDS WORK
     * Returns HashMap containing all device types supported by the SDK
     * @return
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
                                String value = jsonArray.getJSONObject(i).getString("value");
                                db.addDeviceType(Integer.toString(id),value);
                                Log.d(TAG,"PHOTON TYPES RESPONSE: " + id +" "+ value);
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
                return setHeaders();
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * DONE
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
                return setHeaders();
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /**
     * DONE
     * Returns HashMap with all photons under this user
     */
    public void updateAllPhotons() {
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
                                Toast.makeText(_context,
                                        "Photons List Updated", Toast.LENGTH_SHORT).show();
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
                return setHeaders();
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    //FIXME: Make the url use groupID var in the future
    public void sendTrigger(int groupID, int state, int valueType, int operator, String value, String function){
        // Tag used to cancel the request
        String tag_string_req = "req_sendTrigger";
        JSONObject jsonBody = new JSONObject();

        JSONObject obj = new JSONObject();
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
            System.out.println("LOOK HERE" +jsonBody);
            Log.e("LOOK HERE", jsonBody.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,AppConfig.URL_GROUPS+"2/triggers/", jsonBody,
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
                return setHeaders();
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);


    }

    /**
     * NEEDS WORK
     * Returns Photon object with photon requested
     */
    public void getPhotonById(int id) {
        // Tag used to cancel the request
        String tag_string_req = "req_allDevices";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,AppConfig.URL_DEVICES+id+"/", null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String deviceId = response.getString("deviceId");
                            String deviceType = response.getString("deviceType");
                            String deviceName = response.getString("deviceName");
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
                return setHeaders();
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /**
     * Registers new Group
     * @param groupName
     * @param groupType
     * @param devices
     */
    public void registerGroup(String groupName, int groupType, ArrayList<String> devices) {
        // Tag used to cancel the request
        String tag_string_req = "req_newDeviceGroup";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", groupName);
            jsonBody.put("groupType", groupType);
            jsonBody.put("devices", devices);
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
                            db.addNewGroup(new Group(groupName,groupId,groupType));
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
                return setHeaders();
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
    }

    /**
     * Sets Headers for request
     * @return
     */
    private Map<String,String> setHeaders(){
        Map<String, String> headers= new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Token " + sessionToken);
        Log.d("Session Token Stored: ", "Token " + sessionToken);
        return headers;
    }
}
