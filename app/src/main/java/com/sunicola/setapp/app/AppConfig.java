package com.sunicola.setapp.app;

/**
 * Created by soaresbo on 26/01/2018.
 * Make sure you change the IP address for the server
 * address or for your machines if running a local server
 */

public class AppConfig {
    // API location
    private static String address = "http://sccug-330-04.lancs.ac.uk:80/api/v1/";
    private static String addressCloud = "http://setapp.cloud:80/api/v1/";

    // Server user register url
    public static String URL_ACCOUNTS = addressCloud+ "accounts/";
    // Server user login url
    public static String URL_SESSION = addressCloud + "accounts/session/";
    //Server urser NotifyToken
    public static String URL_NOTIFICATION= addressCloud+ "accounts/notifytoken/";

    // Server devices for this user url
    public static String URL_DEVICES = addressCloud+ "devices/";
    // Server all device types url
    public static String URL_DEVICES_TYPES = addressCloud+ "devices/types/";
    // Server groups for this user url
    public static String URL_GROUPS = addressCloud+ "devices/groups/";
    // Server group types for this user url
    public static String URL_GROUPS_TYPES = addressCloud+ "devices/groups/types/";

    //Test URL
    public static String URL_TEST = "https://requestb.in/u6u74fu6";
}
