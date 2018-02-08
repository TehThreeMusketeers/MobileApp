package com.sunicola.setapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sunicola.setapp.R;
import com.sunicola.setapp.app.AppConfig;
import com.sunicola.setapp.app.AppController;
import com.sunicola.setapp.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFirstName = findViewById(R.id.firstName);
        inputLastName = findViewById(R.id.lastName);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        SessionManager session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String firstName = inputFirstName.getText().toString().trim();
                String lastName = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("first_name", firstName);
                        jsonBody.put("last_name", lastName);
                        jsonBody.put("email", email);
                        jsonBody.put("password", password);
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    registerUser(jsonBody);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(JSONObject jsonBody) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        JsonObjectRequest objectRequest = new JsonObjectRequest(AppConfig.URL_ACCOUNTS, jsonBody,
                new Response.Listener<JSONObject>()
                {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Register Response: " + response.toString());
                    hideDialog();
                    Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                    // Launch login activity
                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();

                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "User Account Couldn't be created", Toast.LENGTH_LONG).show();
                    hideDialog();
                }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
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
}
