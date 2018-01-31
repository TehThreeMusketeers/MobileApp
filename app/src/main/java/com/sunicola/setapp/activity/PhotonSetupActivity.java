package com.sunicola.setapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sunicola.setapp.R;

import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;

public class PhotonSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParticleDeviceSetupLibrary.init(this.getApplicationContext());
        ParticleCloudSDK.init(this.getApplicationContext());

        setContentView(R.layout.activity_photon_setup);
    }

    /** Called when the user touches the SetupDevice button
     * This sets the access token to whatever is needed, then calls the device setup library to start
     * the setup process*/
    public void onSetupDevice(View view) {
        Log.d("MainActivity", "onSetupDevice called");
        ParticleCloudSDK.getCloud().setAccessToken("fcdcd6d331480f5039f926248ede06b989069fb3");

        System.out.println("ACCESS TOKEN IS" +ParticleCloudSDK.getCloud().getAccessToken());

        ParticleDeviceSetupLibrary.startDeviceSetup(this, PhotonSetupActivity.class);
    }
}
