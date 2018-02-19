package com.sunicola.setapp.app;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.sunicola.setapp.R;

import static android.provider.Settings.System.getString;
import static com.segment.analytics.internal.Utils.getResourceString;
import static com.segment.analytics.internal.Utils.getSystemService;

/**
 * Created by kolev on 15/02/2018.
 * This provides a wrapper for notifications and alerts on the SET app.
 * This functionality will only work on Android API 17 or higher.
 */

public class SETNotifications {

    private Context ctx;
    private String chn_ID = "SETAPP_Notification_Channel"; //id of the channel
    private String name = "SET Notifications"; //user-visible name of the channel
    private String description = "Monitor the happenings in your SET environment"; // The user-visible description of the channel.
    private NotificationManager mNotificationManager;

    private int n_ID = 0; //notification ID, adds +1 for each notification


    /** Create a SET notification object. To change the name and description of the notification channel,
     * change the channel_name and channel_description values in res/strings.
     * @param context  Application context
     */

    @TargetApi(Build.VERSION_CODES.O)
    public SETNotifications(Context context){

        ctx = context;

        //construct notification channel

        mNotificationManager = (NotificationManager) getSystemService(ctx, Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(chn_ID, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);


    }

    /** Used to read the notification channel settings. Once you have the notification channel,
     * call getVibrationPattern() and getSound() to find out what settings the user has set.
     * Also can use getImportance() to find out if a channel has been blocked.
     *
     * @return Notification Channel
     */
    public NotificationChannel getNotificationChannel(){
        return getNotificationChannel();
    }


    /**
     * Issue a notification to appear on the android device.
     *
     * @param title The large text at the top of the notification
     * @param content The smaller text underneath the title
     * @param pi A pending intent to be executed (usually open an activity) when the user clicks on the notification. Leave null if no action is to be taken.
     * @return Returns the ID of the notification as an int, starting from 1. This can be used to, for example,cancel the notification
     * by passing its ID number to cancelNotification(int ID).
     */
    public int issueNotification(String title, String content, PendingIntent pi){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, chn_ID);
        builder.setSmallIcon(R.drawable.notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentTitle(title);
        builder.setContentText(content);
        if (pi != null){
            builder.setContentIntent(pi);
        }

        mNotificationManager.notify(n_ID, builder.build());

        n_ID++;

        return n_ID;
    }

    /**
     * Cancel notification with the corresponding ID.
     * @param ID ID of the notification, as returned by issueNotification().
     */
    public void cancelNotification(int ID){
        mNotificationManager.cancel(ID);
    }

    /**
     * Cancel all previously issued notifications.
     */
    public void cancelAll(){
        mNotificationManager.cancelAll();
    }

}


