package com.example.smartdispatch_auth.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.smartdispatch_auth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RequestNotificationRequester extends FirebaseMessagingService {

    Map<String, String> hashMap = new HashMap<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        hashMap = remoteMessage.getData();
        if (hashMap.get("user").equals("requester"))
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), hashMap.get("type"));
    }

    private void showNotification(String title, String body, String type) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_02";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("TEST");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        if (type.equals("connected")) {
            Intent i = new Intent("vehicle_alloted");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        } else {
            Intent i = new Intent("vehicle_reached");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }

    }

    @Override
    public void onNewToken(String s) {

        Map<String, Object> token = new HashMap<>();
        token.put("token", s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseFirestore.getInstance().collection("Vehicles").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(token, SetOptions.merge());
    }
}
