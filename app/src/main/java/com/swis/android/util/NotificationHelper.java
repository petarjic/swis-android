package com.swis.android.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.swis.android.R;
import com.swis.android.activity.HomeActivity;
import com.swis.android.activity.SplashActivity;

import java.util.Calendar;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager notificationManager;
    private SwisNotification mNotificationData;
    public static final String CHANNEL_ONE_ID = "com.swis.android.First";
    public static final String CHANNEL_ONE_NAME = "TestOne";
    private Notification.Builder mnBuilder;
    public NotificationHelper(Context base, SwisNotification notificationData) {
        super(base);
        mNotificationData = notificationData;
        createChannels();
    }

    public void createChannels() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(alarmSound, audioAttributes);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(notificationChannel);
        }
    }
    public NotificationHelper getNotification() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            mnBuilder = new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID);
        }else {
            mnBuilder = new Notification.Builder(getApplicationContext());
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            mnBuilder.setColor(getResources().getColor(R.color.colorAccent));
        }

        Intent intent1 = new Intent(getApplicationContext(), SplashActivity.class);
        Bundle bundle1 = new Bundle();
        bundle1.putInt(AppConstants.NOTIFICATIONS.TYPE,mNotificationData.getType());
        bundle1.putString(AppConstants.NOTIFICATIONS.ID,mNotificationData.getId());
        bundle1.putString(AppConstants.NOTIFICATIONS.TITLE,mNotificationData.getTitle());
        bundle1.putString(AppConstants.NOTIFICATIONS.BODY,mNotificationData.getBody());
        bundle1.putString(AppConstants.NOTIFICATIONS.MAIN_POST_ID,mNotificationData.getMain_post_id());
        intent1.putExtras(bundle1);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 123, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        mnBuilder.setContentIntent(pendingIntent);
        mnBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mnBuilder.setContentTitle(mNotificationData.getTitle());
        mnBuilder.setContentText(mNotificationData.getBody());
        mnBuilder.setAutoCancel(true);
        mnBuilder.setPriority(Notification.PRIORITY_HIGH);
        mnBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mnBuilder.setShowWhen(true);

        return this;
    }

    public void notify(int id) {
        getManager().notify(id, mnBuilder.build());
    }
    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
