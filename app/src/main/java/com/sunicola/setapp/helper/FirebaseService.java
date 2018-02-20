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

    //This refreshes the token. The callback fires whenever a new token is generated. Use the getToken() methods to retrieve token.
    @Override
    public void onTokenRefresh(){
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FIREBASE", "Refreshed token: " +refreshedToken);

        //TODO: Notify server that the token has changed.

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
