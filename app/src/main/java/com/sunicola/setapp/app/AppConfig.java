package com.sunicola.setapp.app;

/**
 * Created by soaresbo on 26/01/2018.
 * Make sure you change the IP address for the server
 * address or for your machines if running a local server
 */

public class AppConfig {
    // API location
    private static String address = "http://sccug-330-04.lancs.ac.uk:80/api/v1/";

    // Server user register url
    public static String URL_ACCOUNTS = address+ "accounts/";
    // Server user login url
    public static String URL_SESSION = address + "accounts/session/";

    // Server devices for this user url
    public static String URL_DEVICES = address+ "devices/";
    // Server all device types url
    public static String URL_DEVICES_TYPES = address+ "devices/types/";

    // Server groups for this user url
    public static String URL_GROUPS = address+ "devices/groups/";
    // Server group types for this user url
    public static String URL_GROUPS_TYPES = address+ "devices/groups/types/";

    //Test URL
    //public static String URL_TEST = "https://requestb.in/";
}
