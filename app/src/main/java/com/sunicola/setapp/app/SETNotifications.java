package com.sunicola.setapp.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.sunicola.setapp.R;

import static android.provider.Settings.System.getString;
import static com.segment.analytics.internal.Utils.getResourceString;
import static com.segment.analytics.internal.Utils.getSystemService;

/**
 * Created by kolev on 15/02/2018.
 */

public class SETNotifications {

    private Context ctx;
    private String chn_ID = "SETAPP_Notification_Channel"; //id of the channel

    private String name = Resources.getSystem().getString(R.string.channel_name); //user-visible name of the channel

    private String description = Resources.getSystem().getString(R.string.channel_description); // The user-visible description of the channel.



    public SETNotifications(Context context){

        ctx = context;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(ctx, Context.NOTIFICATION_SERVICE);

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
