package com.sunicola.setapp.helper;

import android.app.Notification;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.sunicola.setapp.app.SETNotifications;

/**
 * Created by kolev on 20/02/2018.
 * Service to receive notifications from the server and create push notifications on device automatically.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private RemoteMessage.Notification remote;


    @Override
    /** This method is called when the server sends a message to the particular token that we've sent
     * using FirebaseService.java The method receives a RemoteMessage, logs the sender, then turns
     * the RemoteMessage into a notification, logs the body of the notification.
     * None of these should need to be changed, unless we want more complex notifications with a payload.
     * @param remoteMessage the remote message sent by the server
     */
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here. (this is only relevant if the message has a body)
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FIREBASE", "MSG From: " + remoteMessage.getFrom());



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            remote = remoteMessage.getNotification();
            Log.d("FIREBASE", "Message Notification Body: " + remote.getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        SETNotifications sn = new SETNotifications(getApplicationContext());

        //issue notification to physical device
        sn.issueNotification(remote.getTitle(), remote.getBody(), null);
    }
}
