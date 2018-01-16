package com.fantavier.bierbattle.bierbattle.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.fantavier.bierbattle.bierbattle.MainActivity;
import com.fantavier.bierbattle.bierbattle.R;
import com.fantavier.bierbattle.bierbattle.TerminDetail;


public class NotificationHelper extends ContextWrapper {

    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "com.fantavier.bierbattle.ONE";
    public static final String CHANNEL_ONE_NAME = "Channel One";

    public NotificationHelper(String channel,Context base) {
        super(base);
        createChannels(channel);
    }



    public void createChannels(String channel) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(channel,
                    channel, notifManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }

    public NotificationCompat.Builder getNotification1(String title, String body,String id) {

        Intent resultIntent = new Intent(this, TerminDetail.class);
        resultIntent.putExtra("appointmentKey",id);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
               resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.beer)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

    }

    public void notify(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}
