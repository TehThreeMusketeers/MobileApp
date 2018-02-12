package com.sunicola.setapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sunicola.setapp.R;
import com.sunicola.setapp.helper.APICalls;
import com.sunicola.setapp.storage.SQLiteHandler;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;


/* This class allows the user to choose a device type from a drop-down menu. The selected choice is
    then sent to the server and stored in the database. This can be updated at any time.
 */
public class DeviceType extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private boolean selected = false;
    private int selection;
    private String deviceID;
    private String deviceName;
    //import class to handle api calls to server
    private APICalls api;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_type);

        Intent intent = getIntent();
        deviceID = intent.getStringExtra("deviceID");
        deviceName = intent.getStringExtra("name");

        System.out.println("DeviceType activity started. Device ID is " +deviceID); //DEBUG

        Spinner spinner = (Spinner) findViewById(R.id.device_type_spinner);

        /*Get list of device types from server, create a drop-down menu of them and allow the user
        to pick one
        */
        api = new APICalls(getApplicationContext());
        api.updateAlllDeviceTypes();

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String,String> deviceTypes = db.getAllDeviceTypes();
        String[] arraySpinner = new String[deviceTypes.size()];
        //populate the array using the hashMap
        for(int i=0; i<(deviceTypes.size()); i++){
            arraySpinner[i] = deviceTypes.get(String.valueOf(i+1));
        }
        //Use array to populate spinner object
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

    }

    public void onConfirm(View view){
        if (selected){
            System.out.println(deviceID +" is a " +selection); //DEBUG


            //Register a photon under the user profile on the server
            api.registerPhoton(deviceID,selection, deviceName); //String devID, int devType

            finish();

            Intent returnBtn = new Intent(getApplicationContext(),
                    MainActivity.class);

            startActivity(returnBtn);
        }
        else{
            makeText(this, "Please select a device type", LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selected = true;
        System.out.println("Selected " +parent.getItemAtPosition(pos));
        selection = pos; //store selection as int
        System.out.println("POSITION IS " +pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        selected = false;
    }
}
