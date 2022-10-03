package com.zahran.timerapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class TimerAlarm extends Service {
    private MediaPlayer player;
    private static final String ACTION_STOP_SERVICE = "Stop";
    int notificationId = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        player = MediaPlayer.create(this, R.raw.alarm);
        player.setLooping(true);
        player.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(getBaseContext());
        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d("TAG", "called to cancel service");
            manager.cancel(notificationId);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

}