package com.sunicola.setapp.app;

/**
 * Created by soaresbo on 26/01/2018.
 * Make sure you change the IP address for the server
 * address or for your machines if running a local server
 */

public class AppConfig {
    private static String address = "http://sccug-330-04.lancs.ac.uk:8000/api/v1/";

    // Server user register url
    public static String URL_REGISTER = address + "accounts";

    // Server user login url
    public static String URL_LOGIN = address + "accounts/session";
}
