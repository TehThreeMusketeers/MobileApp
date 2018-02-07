package com.sunicola.setapp.app;

/**
 * Created by soaresbo on 26/01/2018.
 * Make sure you change the IP address for the server
 * address or for your machines if running a local server
 */

public class AppConfig {
    // API location
    private static String address = "http://sccug-330-04.lancs.ac.uk:8000/api/v1/";

    // Server user login url
    public static String URL_SESSION = address + "accounts/session/";

    // Server user register url
    public static String URL_ACCOUNTS = address+ "accounts/";

    // Server all device types url
    public static String URL_DEVICES_TYPES = address+ "devices/types/";

    // Server devices for this user url
    public static String URL_DEVICES = address+ "devices/";

    //Test URL
    //public static String URL_TEST = "https://requestb.in/";
}
