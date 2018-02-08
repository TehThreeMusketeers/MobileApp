package com.sunicola.setapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.sunicola.setapp.R;

/* This class allows the user to choose a device type from a drop-down menu. The selected choice is
    then sent to the server and stored in the database. This can be updated at any time.
 */
public class DeviceType extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public boolean selected = false;
    public String selection;
    public String deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_type);

        Intent intent = getIntent();
        deviceID = intent.getStringExtra("deviceID");

        System.out.println("DeviceType activity started. Device ID is " +deviceID);

        Spinner spinner = (Spinner) findViewById(R.id.device_type_spinner);
        String[] arraySpinner = new String[] {
                "test1", "test2", "test3"
        };
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

    }


    public void onConfirm(View view){
        if (selected){
            System.out.println(deviceID +" is a " +selection);
            finish();
        }
        else{
            Toast.makeText(this, "Please select a device type", Toast.LENGTH_SHORT);
            //Do nothing
        }



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        selected = true;
        System.out.println("Selected " +parent.getItemAtPosition(pos));
        selection = parent.getItemAtPosition(pos).toString(); //store selection as string


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        selected = false;
    }


}
