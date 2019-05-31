package com.sagi.gambling.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.activities.MainActivity;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

public class ListenerFireService extends Service {


    public static int idNotification = 0;

    public ListenerFireService() {
        Log.d("ListenerFireService", "ListenerFireService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ListenerFireService", "onStartCommand");
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Log.d("ListenerFireService", "onCreate");

        startListener();
        super.onCreate();
    }

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    private void startListener() {
        myRef.child(FireBaseConstant.REQUEST_FREINDS_GROUPS_TABLE).child(SharedPreferencesHelper.getInstance(getApplicationContext()).getUser().textEmailForFirebase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ListenerFireService", "onDataChange");

                int lastRequest = SharedPreferencesHelper.getInstance(getApplicationContext()).getLastCountRequest();
                long currentCountRequest = dataSnapshot.getChildrenCount();

                Log.d("ListenerFireService", "lastRequest: " + lastRequest + ", currentCountRequest: " + currentCountRequest);

                if (lastRequest < currentCountRequest) {
//                    showNotification(1 + idNotification + " New Request", "Press to enter");
                    showNotificationNew(1 + idNotification + " New Request", "Press to enter");
                }
                SharedPreferencesHelper.getInstance(getApplicationContext()).setLastCountRequest((int) currentCountRequest);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNotificationNew(String title, String body) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GeneralConstants.REQUEST_SCREEN_KEY, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//        bigText.bigText(title);
//        bigText.setBigContentTitle(title);
//        bigText.setSummaryText("Text in detail");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo_not_free);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(idNotification++, mBuilder.build());
    }

//    private void showNotification(String title, String body) {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        //Create the intent that’ll fire when the user taps the notification//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(GeneralConstants.REQUEST_SCREEN_KEY, true);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setSmallIcon(R.drawable.logo_not_free);
//        mBuilder.setContentTitle(title);
//        mBuilder.setContentText(body);
//        mBuilder.setAutoCancel(true);
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(idNotification++, mBuilder.build());
//    }


}
