package com.sunicola.setapp.activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sunicola.setapp.Manifest;
import com.sunicola.setapp.R;
import com.sunicola.setapp.app.SETNotifications;
import com.sunicola.setapp.fragments.EnvironmentFragment;
import com.sunicola.setapp.fragments.ActuatorsFragment;
import com.sunicola.setapp.fragments.PhotonListFragment;
import com.sunicola.setapp.fragments.TriggerFragment;
import com.sunicola.setapp.helper.APICalls;
import com.sunicola.setapp.helper.BlScanCallback;
import com.sunicola.setapp.helper.SessionManager;
import com.sunicola.setapp.storage.SQLiteHandler;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TriggerFragment.OnFragmentInteractionListener, BeaconConsumer {



    private static final int REQUEST_ENABLE_BT = 1;
    private SQLiteHandler db;
    private APICalls api;
    private HashMap<String, String> user;
    private SessionManager session;
    private String accessToken;
    private ParticleCloud cloud;

    private ParticleDeviceSetupLibrary.DeviceSetupCompleteReceiver receiver;

    private BeaconManager beaconManager;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// find the retained fragment on activity restarts

        /*String tag = "ActuatorsFragment";
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager.findFragmentByTag(tag) != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ActuatorsFragment homeFragment = new ActuatorsFragment();
            fragmentTransaction.add(R.id.screen_area, homeFragment, tag);
            fragmentTransaction.commit();
        }*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialise particle SDKs
        ParticleDeviceSetupLibrary.init(this.getApplicationContext());
        ParticleCloudSDK.init(this.getApplicationContext());

        //init beacon manager
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        cloud = ParticleCloudSDK.getCloud();

        //Add notification support
        SETNotifications notifications = new SETNotifications(getApplicationContext());

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }else{
            //Permission already granted
            System.out.println("Fine location perm  granted");
        }


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, 3);

        }else{
            //Permission already granted
            System.out.println("Bluetooth admin perm  granted");
        }

        notifications.issueNotification("Test", "This is a test notification", null);

        //Used to receive device ID after claiming process completes
        receiver = new ParticleDeviceSetupLibrary.DeviceSetupCompleteReceiver() {
            //This starts DeviceType activity and passes it the device ID and name of the photon that was just claimed using photon setup
            @Override
            public void onSetupSuccess(@NonNull String configuredDeviceId) {
                Toast.makeText(MainActivity.this, "Setup successful.", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MainActivity.this, DeviceType.class);

                i.putExtra("deviceID", configuredDeviceId);

                //FIXME this causes an exception with the cloud sdk, fix.
                try {
                    i.putExtra("name", cloud.getDevice(configuredDeviceId).getName());
                } catch (ParticleCloudException e) {
                    e.printStackTrace();
                    System.out.println("Particle cloud Exception while trying to put device name in Intent at MainActivity");
                }

                startActivity(i);
                receiver.unregister(getApplicationContext());
            }

            @Override
            public void onSetupFailure() {
                Toast.makeText(MainActivity.this, "Setup failed", Toast.LENGTH_SHORT).show();

            }
        };

            //Initialise Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Gets Drawer Elements
        TextView userName = navigationView.getHeaderView(0).findViewById(R.id.username);
        TextView userEmail = navigationView.getHeaderView(0).findViewById(R.id.useremail);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // API calls handler
        api = new APICalls(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        //TODO Send firebase token to server, and replace old token with new one if needed
        api.sendFirebaseToken(FirebaseInstanceId.getInstance().getToken());
        api.patchFirebaseToken(FirebaseInstanceId.getInstance().getToken());
        Log.d("FIREBASE", "Sending initial token " +FirebaseInstanceId.getInstance().getToken());

        // Fetching user details from SQLite
        user = db.getUserDetails();
        String firstName = user.get("first_name");
        String lastName = user.get("last_name");
        String email = user.get("email");
        accessToken = user.get("access_token");

        cloud.setAccessToken(accessToken); //Particle cloud access token is set when the user logs in

        // Displaying the user details on the screen
        userName.setText(String.format("%s %s", firstName, lastName));
        userEmail.setText(email);

        //Displays first Fragment at login
        displaySelectedScreen(R.id.nav_allDevices);

        FloatingActionButton fab1 = findViewById(R.id.fab12);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Create your own trigger rules", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Fragment fragment = new TriggerFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.commit();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        // closes nav drawer if open on back press
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        //TODO: but sometimes back pressing may cause the app to close
        //Fixes fragment stacking issue
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
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
        Fragment fragment = null;
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId){
        Fragment fragment = null;

        switch (itemId){
            case R.id.nav_allDevices:
                fragment = new PhotonListFragment();
                break;
            case R.id.nav_addPhoton:
                setupDevice();
                break;
            case R.id.nav_logOut:
                logoutUser();
                break;
            case R.id.nav_usr_Env:
                fragment = new EnvironmentFragment();
                break;
            case R.id.actuators_control:
                fragment = new ActuatorsFragment();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.screen_area, fragment).addToBackStack("my_fragment");
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),"Good Bye " + user.get("first_name"),Toast.LENGTH_LONG).show();
        db.deleteUserData();
        db.deletePhotonData();
        finish();
    }
    /**
     * Called when the user touches the SetupDevice button
     * This sets the access token to whatever is needed, then calls the device setup library to start
     * the setup process
     */
    public void setupDevice() {
        System.out.println("ACCESS TOKEN ACCORDING TO SQL: " +accessToken);
        System.out.println("ACCESS TOKEN IS" +cloud.getAccessToken());

        //this Async class helps make a separate intent for making particle API calls, as
        //they tend to make blocking network calls and Android doesn't allow those from the UI thread

        Async.executeAsync(cloud, new Async.ApiProcedure<ParticleCloud>() {
            @Override
            public Void callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                System.out.println("Access token set! Token: " +cloud.getAccessToken());
                ParticleDeviceSetupLibrary.startDeviceSetup(getApplicationContext(), MainActivity.class);
                return null;
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException exception) {
                System.out.println("Failed to make SDK call for access token injection");
            }
        });
        receiver.register(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i("BLUETOOTH", "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("BLUETOOTH", "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {

                Log.i("BLUETOOTH", "I have just switched from seeing/not seeing beacons: "+state);
            }
        });


        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }
}




