package com.appdevgenie.shuttleservice.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.activities.MainActivity;

public class NotificationUtils {

    private static final String TRIP_REMINDER_CHANNEL_ID = "reminder_notification_id";
    private static final int NOTIFICATION_ID = 254;
    private static final int PENDING_INTENT_REQUEST_CODE = 101;

    public static void remindCommuterOfTrip(Context context, String destination){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    TRIP_REMINDER_CHANNEL_ID,
                    "PrimaryNotification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, TRIP_REMINDER_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notify)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("Shuttle departs in 30 minutes for " + destination)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("blah blah"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private static PendingIntent contentIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                PENDING_INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );
    }

    private static Bitmap largeIcon(Context context){
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notify);
    }
}
