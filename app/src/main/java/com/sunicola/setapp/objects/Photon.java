package com.sunicola.setapp.objects;

/**
 * Created by soaresbo on 06/02/2018.
 */

public class Photon {
    private int id;
    private String deviceId, deviceType, deviceName;

    public Photon(int id, String deviceId, String deviceType, String deviceName){
        this.id = id;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
    }

    public int getId() {
        return id;
    }
    public String getDeviceId() {return deviceId;}
    public String getDeviceName() {
        return deviceName;
    }
    public String getDeviceType() {
        return deviceType;
    }
}
