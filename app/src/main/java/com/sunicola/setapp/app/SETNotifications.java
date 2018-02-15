package com.sunicola.setapp.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import static com.segment.analytics.internal.Utils.getSystemService;

/**
 * Created by kolev on 15/02/2018.
 */

public class Notifications {

    private Context ctx;
    private String chn_ID = "SETAPP_Notification_Channel";

    public Notifications(Context context){

        ctx = context;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(ctx, Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = chn_ID
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);
        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
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


}
