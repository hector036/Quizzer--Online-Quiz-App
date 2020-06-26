package com.khan.quizzer_onlinequizapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final String notificationType = remoteMessage.getData().get("notificationType");

        if (notificationType.equals("WeeklyTestNotification") || notificationType.equals("ImportantNewsNotification")) {

            final String pTitle = remoteMessage.getData().get("pTitle");
            final String pDescription = remoteMessage.getData().get("pDescription");
            String photoUrl = remoteMessage.getData().get("photoUrl");

            final Bitmap[] bitmap = {null};

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(photoUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            // TODO Do some work: pass this bitmap
                            showBigPictureNotification("" + pTitle, "" + pDescription, bitmap[0],notificationType);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        }else if (notificationType.equals("GeneralMessage")) {
            final String pTitle = remoteMessage.getData().get("pTitle");
            final String pDescription = remoteMessage.getData().get("pDescription");
            String bigText = remoteMessage.getData().get("bigText");

            showBigTextNotification("" + pTitle, "" + pDescription, bigText, notificationType);

        }

    }

    private void showBigTextNotification(String pTitle, String pDescription, String bigText, String notificationType) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpPostNotificationChannel(notificationManager);
        }

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long vibrate[] = {100, 600, 100, 600};
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "" + ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(notificationUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText).setSummaryText(""))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setTicker(pTitle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setLights(Color.GREEN, 1000, 2000);


        notificationManager.notify(notificationID, notificationBuilder.build());

    }


    private void showBigPictureNotification(String pTitle, String pDescription, Bitmap bitmap, String notificationType) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpPostNotificationChannel(notificationManager);
        }

        Intent intent;
        if (notificationType.equals("WeeklyTestNotification")) {
            intent = new Intent(this, TestsActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);

        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long vibrate[] = {100, 600, 100, 600};
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "" + ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(bitmap)
                .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(notificationUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(largeIcon))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setTicker(pTitle)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(vibrate)
                .setLights(Color.GREEN, 1000, 2000);


        notificationManager.notify(notificationID, notificationBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpPostNotificationChannel(NotificationManager notificationManager) {

        CharSequence chanelName = "New Notification";
        String channelDescription = "Device to device weekly test notification";

        NotificationChannel adminChanel = new NotificationChannel(ADMIN_CHANNEL_ID, chanelName, NotificationManager.IMPORTANCE_HIGH);
        adminChanel.setDescription(channelDescription);
        adminChanel.enableLights(true);
        adminChanel.setLightColor(Color.GREEN);
        adminChanel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChanel);
        }

    }
}
