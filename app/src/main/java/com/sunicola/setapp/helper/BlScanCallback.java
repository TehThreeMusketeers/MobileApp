package com.sunicola.setapp.helper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kolev on 12/03/2018.
 */


public class BlScanCallback extends ScanCallback {
    private HashMap mScanResults;

    public BlScanCallback (){
        super();
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        addScanResult(result);

    }
    @Override
    public void onScanFailed(int errorCode) {

    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult result : results) {
            addScanResult(result);
        }
    }

    private void addScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String deviceName = device.getName();
        System.out.println("BLTH SCAN RESULTS ARE: " +deviceName);
        mScanResults.put(deviceName, device);
    }

};
