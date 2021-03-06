package com.sunicola.setapp.helper;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by kolev on 20/02/2018.
 * This class is used to allow for Firebase functionality in the SETApp.
 */

public class FirebaseService extends FirebaseInstanceIdService {

    private String refreshedToken;
    private SessionManager session;
    private APICalls api;

    //This refreshes the token. The callback fires whenever a new token is generated. Use the getToken() methods to retrieve token.
    @Override
    public void onTokenRefresh(){
        session = new SessionManager(getApplicationContext());
        api = new APICalls(getApplicationContext());

        Log.d("FIREBASE", "TOKEN REFRESHED");
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("FIREBASE TOKEN IS: " +refreshedToken);
        //TODO: Test and confirm these api calls work correctly and are received by the server.

        if(session.isLoggedIn()){
            Log.d("FIREBASE", "Token refreshed, patching token.");
            api.patchFirebaseToken(refreshedToken);
        }


    }


    /**
     * On initial startup of the app, Firebase generates a token for the client app instance.
     * This method retrieves that token.
     * @return returns Firebase token as string or null if token not generated yet
     */
    public String getToken(){
        return refreshedToken;
    }
}
