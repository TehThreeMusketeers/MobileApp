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
import com.sunicola.setapp.objects.Photon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by soaresbo on 05/02/2018.
 */

public class APICalls {
    private static final String TAG = APICalls.class.getSimpleName();
    private Context _context;
    private String sessionToken;

    /**
     * API calls Constructor
     * @param context
     */
    public APICalls(Context context) {
        this._context = context;
        //Setup stuff
        SQLiteHandler db = new SQLiteHandler(_context);
        HashMap<String, String> user = db.getUserDetails();
        sessionToken = user.get("session_token");
    }

    /**
     * Returns HashMap containing all device types supported by the SDK
     * @return
     */
    public HashMap<Integer,String> getAllDeviceTypes(){
        String tag_string_req = "req_all_device_types";
        HashMap<Integer,String> types = new HashMap<>();
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_DEVICES_TYPES,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i=0; i<jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String value = jsonArray.getJSONObject(i).getString("value");
                                types.put(id,value);
                            }
                            Log.d("Response", types.get(1));
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
        return types;
    }

    /**
     * Registers a new photon under this users account
     * @param devID
     * @param devType
     */
    public void registerPhoton(String devID, int devType) {
        // Tag used to cancel the request
        String tag_string_req = "req_newDevice";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("deviceId", devID);
            jsonBody.put("deviceType", devType);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,AppConfig.URL_DEVICES, jsonBody,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String id = response.getString("id");
                            String deviceId = response.getString("deviceId");
                            String deviceType = response.getString("deviceType");
                            String deviceName = response.getString("deviceName");
                            Log.d(TAG,id +" "+ deviceId +" "+deviceType+" "+" "+deviceName);
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
     * Returns HashMap with all photons under this user
     */
    public HashMap<Integer,Photon> getAllPhotons() {
        // Tag used to cancel the request
        String tag_string_req = "req_allDevices";
        HashMap<Integer,Photon> devices = new HashMap<>();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,AppConfig.URL_DEVICES, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i=0; i<jsonArray.length(); i++) {
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String deviceId = jsonArray.getJSONObject(i).getString("deviceId");
                                String deviceType = jsonArray.getJSONObject(i).getString("deviceType");
                                String deviceName = jsonArray.getJSONObject(i).getString("deviceName");
                                Photon temp = new Photon(id,deviceId,deviceType,deviceName);
                                devices.put(i,temp);
                                Log.d(TAG,id +" "+ deviceId +" "+deviceType+" "+" "+deviceName);
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
        return devices;
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
