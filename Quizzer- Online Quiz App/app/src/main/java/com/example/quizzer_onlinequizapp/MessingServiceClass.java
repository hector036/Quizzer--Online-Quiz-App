package com.example.quizzer_onlinequizapp;

import android.app.Notification;
import android.app.Service;
import android.graphics.Color;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static java.lang.System.load;

public class MessingServiceClass extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    public void showNotification(String title, String message){

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long vibrate[]={100,600,100,600};

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1010")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.icon_notification)
                .setAutoCancel(true)
                .setContentText(message)
                .setVibrate(vibrate)
                .setLights(Color.GREEN,1000,2000)
                .setSound(soundUri);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(101,builder.build());
    }
}
