package com.example.pigeonlineandroidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.pigeonlineandroidapp.entities.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

// Service for Firebase and Notifications.
public class PigeOnlineAndroidAppService extends FirebaseMessagingService {
    public PigeOnlineAndroidAppService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // super.onMessageReceived(message);
        if (message.getNotification() != null) {
            createNotificationChannel();
            NotificationCompat.Builder builder = new NotificationCompat.
                    Builder(this, "1")
                    .setSmallIcon(R.drawable.im4)
                    .setContentTitle(message.getNotification().getTitle())
                    .setContentText(message.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
            Intent intent = new Intent();
            intent.setAction("onMessageReceived");
            intent.putExtra("from", message.getNotification().getTitle());
            intent.putExtra("content", message.getNotification().getBody());
            intent.putExtra("date", formatter.format(date));
            sendBroadcast(intent);
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", "myChannel", importance);
            channel.setDescription("Demo Channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}