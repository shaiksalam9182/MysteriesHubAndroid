package com.salam.naradh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class FcmService extends FirebaseMessagingService {



    int count;
    String title,body;
    Map<String,String> data;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        data =remoteMessage.getData();

        Log.e("NotificationData",data.toString());


        sendNotification(body,"wait",title);

    }

    private void sendNotification(String msgBody, String data, String title) {
        Intent intent = new Intent(this, Splash.class);
        intent.putExtra("admin_video_id",data);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channel_id = "RetailDetail_Push_Notification";
        CharSequence channelname = "Push Notifications";
        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= 26) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(channel_id, channelname, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, channel_id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(Color.BLACK);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        } else {
            notificationBuilder.setColor(Color.BLACK);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        }
        notificationBuilder.setContentTitle("")
                .setContentText(msgBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msgBody))
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setSound(defaultSoundUri).setTicker(msgBody)
                .setContentIntent(pendingIntent).setNumber(count);

        notificationManager.notify(count /* ID of notification */, notificationBuilder.build());
        Log.e("count", "" + count);
    }


}
