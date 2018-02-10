package com.sunicola.setapp.objects;

/**
 * Created by soaresbo on 06/02/2018.
 */

public class Photon {
    private String id, deviceId, deviceType, deviceName, deviceGroup;

    /**
     * Used during SQLite getAllPhotons
     */
    public Photon(){
    }

    public Photon(String id, String deviceId, String deviceType, String deviceName){
        this.id = id;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
    }

    public String getId() {
        return id;
    }
    public String getDeviceId() {return deviceId;}
    public String getDeviceType() {
        return deviceType;
    }
    public String getDeviceName() {return deviceName;}

    public String getDeviceGroup() {
        if (deviceGroup!=null)
            return deviceGroup;
        else{
            return "Not Assigned To Group";
        }
    }
    public void setId(String id) {this.id = id;}
    public void setDeviceId(String deviceId) {this.deviceId = deviceId;}
    public void setDeviceType(String deviceType) {this.deviceType = deviceType;}
    public void setDeviceName(String deviceName) {this.deviceName = deviceName;}
    public void setDeviceGroup(String deviceGroup) {this.deviceGroup = deviceGroup;}

    /**
     * Used by SQLite Handler to ensure data is added to the right variable
     * @param tobeChanged
     * @param data
     */
    public void globalSetter(String tobeChanged, String data){
        switch (tobeChanged){
            case "id":
                setId(data);
                break;
            case "deviceId":
                setDeviceId(data);
                break;
            case "deviceType":
                setDeviceType(data);
                break;
            case "deviceName":
                setDeviceName(data);
                break;
            case "deviceGroup":
                setDeviceGroup(data);
                break;
            default:
                System.out.print(tobeChanged+ " couldn't be found");
                break;
        }
    }
}
