package com.fantavier.bierbattle.bierbattle.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import com.fantavier.bierbattle.bierbattle.R;

/**
 * Created by Paul on 09.01.2018.
 */

public class NotificationHelper extends ContextWrapper {

    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "com.fantavier.bierbattle.ONE";
    public static final String CHANNEL_ONE_NAME = "Channel One";

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    public Notification.Builder getNotification1(String title, String body) {
        if (Build.VERSION.SDK_INT >= 26) {
            return new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.beer)
                    .setAutoCancel(true);
        } else {
            return null;
        }
    }

    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}