package com.lazysecs.nota.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lazysecs.nota.R;
import com.lazysecs.nota.activities.MainActivity;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = "taskExpired";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        Intent intentMainActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = builder
                .setContentText(intent.getExtras().getString("description"))
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setContentTitle(intent.getExtras().getString("title"))
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(new Random().nextInt(100), notification);
    }
}