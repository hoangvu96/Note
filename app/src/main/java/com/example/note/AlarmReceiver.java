package com.example.note;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.note.view.impl.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context, intent);
    }

    public void showNotification(Context context, Intent intent) {
        String title = "";
        if (intent.getExtras()!=null){
            title = intent.getExtras().getString("title");
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app);
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Channel Name";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Intent mIntent = new Intent(context, MainActivity.class);
//            mIntent.putExtra("notification", true);
        PendingIntent pi = PendingIntent.getActivity(context, 0, mIntent, 0);
        Notification.Builder notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            notification = new Notification.Builder(context)
                    .setContentTitle("Note")
                    .setContentText(title)
                    .setSmallIcon(R.drawable.ic_app)
                    .setLargeIcon(largeIcon)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setContentIntent(pi)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(notifyID, notification.build());
        } else {
            Notification noti = new android.support.v4.app.NotificationCompat.Builder(context)
                    .setContentTitle("Note ")
                    .setContentText(title)
                    .setSmallIcon(R.drawable.ic_app)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pi)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(notifyID, noti);
        }
    }
}
