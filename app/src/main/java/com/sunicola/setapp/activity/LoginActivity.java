package com.sunicola.setapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sunicola.setapp.R;
import com.sunicola.setapp.app.AppConfig;
import com.sunicola.setapp.app.AppController;
import com.sunicola.setapp.helper.SQLiteHandler;
import com.sunicola.setapp.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnLinkToRegister = findViewById(R.id.btnLinkToRegisterScreen);
        Button devMode = findViewById(R.id.btnDev);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    makeText(getApplicationContext(),
                            "Please enter the credentials!", LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        //Dev button
        devMode.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                setDevMode(view);
            }
        });

    }

    /**
     * function to verify login details on the server
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        String basicAuth = encodeHeaders(email, password);

        JsonObjectRequest strReq = new JsonObjectRequest(Method.POST,AppConfig.URL_SESSION,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONObject user = response.getJSONObject("user");
                            String first_name = user.getString("first_name");
                            String last_name = user.getString("last_name");
                            String email = user.getString("email");
                            String session_token = response.getString("token");
                            //String access_token = response.getString("access_token");
                            //String refresh_token = response.getString("refresh_token");

                            // Inserting row in users table
                            db.deleteUsers();
                            db.addUser(first_name, last_name, email, session_token);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        makeText(getApplicationContext(), "User successfully loggedIn.", LENGTH_LONG).show();

                        session.setLogin(true);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(TAG, "Login Error: " + error.getMessage());
                        makeText(getApplicationContext(),"User Account Couldn't be created", LENGTH_LONG).show();
                        hideDialog();
                    }
                }
                ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers= new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Basic " + basicAuth);
                Log.d("Auth Generated:", "Basic " + basicAuth);
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //If devMode button is clicked, skip login and go straight into main activity
    private void setDevMode(View view) {
        session.setLogin(true);
        System.out.println("DEV MODE ON");
        Intent dev = new Intent(this, MainActivity.class);
        this.startActivity(dev);
    }

    /**
     * Creates Basic Auth from email and password passed
     * and returns string with basicAuth
     * @param email The user email that will to be encoded
     * @param password The user password that will be encoded
     * @return The string that the function returns which is a Base64 encoded email+password
     */
    private String encodeHeaders( String email, String password) {
        String credentials = email + ":" + password;
        return Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
    }
}
