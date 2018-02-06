package com.sunicola.setapp.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sunicola.setapp.activity.MainActivity;
import com.sunicola.setapp.app.AppConfig;
import com.sunicola.setapp.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by soaresbo on 05/02/2018.
 */

public class APICalls {
    private static final String TAG = MainActivity.class.getSimpleName();
    Context _context;
    private SQLiteHandler db;
    private HashMap<String, String> user;
    private String sessionToken;

    /**
     * API calls Constructor
     * @param context
     */
    public APICalls(Context context) {
        this._context = context;
        //Setup stuff
        db = new SQLiteHandler(_context);
        user = db.getUserDetails();
        sessionToken = user.get("session_token");
    }

    public HashMap<Integer,String> getAllDeviceTypes(){
        String tag_string_req = "req_all_devices";
        HashMap<Integer,String> types = new HashMap<>();
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_GET_ALL_DEVICES,null,
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

                            Log.d("Response", jsonArray.getJSONObject(0).toString());
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        Toast.makeText(_context, "User successfully loggedIn.", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        Toast.makeText(_context,
                                "User Account Couldn't be created", Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return setHeaders();
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        return types;
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
