package com.sunicola.setapp.activity;

import android.app.Application;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sunicola.setapp.R;
import com.sunicola.setapp.helper.SQLiteHandler;
import com.sunicola.setapp.helper.SessionManager;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.Result;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView userName;
    private TextView userEmail;

    private SQLiteHandler db;
    private SessionManager session;

    private String accessToken;

    private ParticleDeviceSetupLibrary.DeviceSetupCompleteReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initalise particle SDKs
        ParticleDeviceSetupLibrary.init(this.getApplicationContext());
        ParticleCloudSDK.init(this.getApplicationContext());

        //Used to receive device ID after claiming process completes
        receiver = new ParticleDeviceSetupLibrary.DeviceSetupCompleteReceiver() {

            //This starts DeviceType activity and passes it the device ID of the photon that was just claimed using photon setup
            @Override
            public void onSetupSuccess(@NonNull String configuredDeviceId) {
                Toast.makeText(MainActivity.this, "Setup successful.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MainActivity.this, DeviceType.class);
                i.putExtra("deviceID", configuredDeviceId);

                startActivity(i);
                receiver.unregister(getApplicationContext());

            }

            @Override
            public void onSetupFailure() {

                Toast.makeText(MainActivity.this, "Setup failed", Toast.LENGTH_SHORT).show();

            }
        };



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = navigationView.getHeaderView(0).findViewById(R.id.username);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.useremail);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String firstName = user.get("first_name");
        String lastName = user.get("last_name");
        String name = firstName + " " + lastName;
        String email = user.get("email");
        accessToken = user.get("access_token");

        // Displaying the user details on the screen
        userName.setText(name);
        userEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addPhoton) {
            // Launching the login activity
        } else if (id == R.id.nav_manage) {
            //Will be used to code specific environments
        } else if (id == R.id.nav_share) {
            //Will be used to give friend or family access to account
        } else if (id == R.id.nav_logOut) {
            logoutUser();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called when the user touches the SetupDevice button
     * This sets the access token to whatever is needed, then calls the device setup library to start
     * the setup process
     */
    public void onSetupDevice(View view) {

        ParticleCloud cloud = ParticleCloudSDK.getCloud();

        //this Async class helps make a separate intent for making particle API calls, as
        //they tend to make blocking network calls and Android doesn't allow those from the UI thread

        Async.executeAsync(cloud, new Async.ApiProcedure<ParticleCloud>() {

            @Override
            public Void callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.setAccessToken(accessToken);
                System.out.println("Access token set! Token: " +cloud.getAccessToken());
                return null;
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                System.out.println("Failed to make SDK call for access token injection");
            }
        });

        //this starts the setup activity. Change the second parameter to whatever
        //activity is needed for custom device type setup

        receiver.register(this);

        ParticleDeviceSetupLibrary.startDeviceSetup(this, MainActivity.class);

    }

}
