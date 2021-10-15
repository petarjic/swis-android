package com.swis.android.ApiService;

import android.os.Bundle;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.swis.android.util.AppConstants;
import com.swis.android.util.NotificationHelper;
import com.swis.android.util.PrefsHelper;
import com.swis.android.util.SwisNotification;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationHelper notificationHelper;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        PrefsHelper.getPrefsHelper().savePref(AppConstants.DEVICE_TOKEN, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null && remoteMessage.getData() != null) {
            final SwisNotification notificationFromData = getNotificationFromData(remoteMessage.getData());
            notificationHelper = new NotificationHelper(getApplicationContext(), notificationFromData);
            notificationHelper.getNotification().notify(new Random().nextInt(9999));
        }
    }


    private SwisNotification getNotificationFromData(Map<String, String> data) {

        SwisNotification nisinNotification = new SwisNotification();
        nisinNotification.setTitle(data.containsKey("title") ? data.get("title") : "SWIS");
        nisinNotification.setBody(data.containsKey("body") ? data.get("body") : "");
        nisinNotification.setId(data.containsKey("id") ? data.get("id") : "");
        nisinNotification.setMain_post_id(data.containsKey("main_post_id") ? data.get("main_post_id") : "");
        nisinNotification.setType(data.containsKey("type") ? Integer.parseInt(data.get("type")) : -1);
        return nisinNotification;
    }
}
