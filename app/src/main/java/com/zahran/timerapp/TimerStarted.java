package com.zahran.timerapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TimerStarted extends Fragment {

    Activity mactivity;
    Context mcontext;
    private Button pauseBtn,cancelBtn;
    private TextView time;
    private CountDownTimer timer ;
    public static long startTime=4*60*1000;
    long untilFinish=0;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProgressBar progressBar;
    boolean isTimerRunning=false;
    boolean backPressed=false;
    private static final String ACTION_STOP_SERVICE = "Stop";
    private MediaPlayer player;
    int notificationId = 0;

    void setStartTime(long millis){
        startTime=millis;
    }

    public TimerStarted() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mactivity=getActivity();
        mcontext=getContext();
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e("TAG", "onViewStateRestored");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pauseBtn= view.findViewById(R.id.pauseandresumebtn);
        cancelBtn= view.findViewById(R.id.cancelbtn);
        time= view.findViewById(R.id.time);
        progressBar = view.findViewById(R.id.progressBar);
        untilFinish=startTime;

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
                if (!backPressed){
                    Toast.makeText(getContext(),"press again to exit",Toast.LENGTH_SHORT).show();
                    backPressed=true;
                }
                else{
                    getActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        if(savedInstanceState !=null) {

            untilFinish = savedInstanceState.getLong("untilfinish");
            String checkpause = savedInstanceState.getString("pausebtnval");
            time.setText(savedInstanceState.getString("timestring"));
            isTimerRunning=savedInstanceState.getBoolean("isTimerRunning");

            if (checkpause.equals("Pause")) {
                progressBar.setProgress((int) ((double) (untilFinish-1000) / (double) startTime * 100));
                StartTimer(untilFinish,getContext(),savedInstanceState);
                pauseBtn.setText("Pause");
                pauseBtn.setBackgroundColor(getResources().getColor(R.color.bloodycolor));
                isTimerRunning=true;
            } else {
                    pauseBtn.setText("Resume");
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.purplecolor));
                isTimerRunning=false;
            }

        }

        if (savedInstanceState==null){
            progressBar.setProgress(100);
            String stime=setTimeString(startTime);
            time.setText(stime);
            StartTimer(startTime,getContext(),savedInstanceState);
        }

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pauseBtn.getText().toString().equals(getString(R.string.pause))){
                    timer.cancel();
                    pauseBtn.setText(getString(R.string.resume));
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.purplecolor));
                    isTimerRunning=false;
                }
                else{
                    progressBar.setProgress((int)((double)(untilFinish-1000)/(double)startTime*100));
                    StartTimer(untilFinish,getContext(),savedInstanceState);
                    pauseBtn.setText(getString(R.string.pause));
                    pauseBtn.setBackgroundColor(getResources().getColor(R.color.bloodycolor));
                    isTimerRunning=true;
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                time.setText(getResources().getText(R.string.time0));
                fragmentTransaction
                        .replace(R.id.fragmentContainerView, SettingUpTimer.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer_started, container, false);
    }

    public void StartTimer(long millis,Context context,@Nullable Bundle savedInstanceState){
        timer  =new CountDownTimer(millis,1000) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTick(long millisUntilFinished) {
                if (startTime-millisUntilFinished<1000){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                if (millisUntilFinished<=6000){
                        progressBar.setProgressDrawable(mactivity.getDrawable(R.drawable.circularringred));
                }
                untilFinish=millisUntilFinished;
                progressBar.setProgress((int)((double)(untilFinish-1000)/(double)startTime*100));
                String stime=setTimeString(millisUntilFinished);
                time.setText(stime);
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                makeNotification();
                mactivity.startService(new Intent(getContext(), TimerAlarm.class).setAction("Start"));
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
                isTimerRunning=false;
            }
        }.start();

        isTimerRunning=true;
    }

    private void makeNotification() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());

        //Channel
        String CHANNEL_ID = "001";
        NotificationChannel channel;
        NotificationManager notificationManager = ContextCompat.getSystemService(getContext(), NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = manager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, "Timer App", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Timer App Description");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(channel);
            }
        }

        //Intent
        Intent stopSelf = new Intent(getContext(), TimerAlarm.class);
        stopSelf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        stopSelf.setAction(this.ACTION_STOP_SERVICE);

        PendingIntent StopAlarm = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            StopAlarm = PendingIntent.getService(getContext(), 0, stopSelf, PendingIntent.FLAG_MUTABLE);
        } else {
            StopAlarm = PendingIntent.getService(getContext(), 0, stopSelf, PendingIntent.FLAG_ONE_SHOT);
        }

        //Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setContentTitle("Timer App")
                .setContentText("Time Up,Stop the Alarm")
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.clockapp)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setAutoCancel(true)
                .addAction(R.drawable.ic_baseline_stop_24, ACTION_STOP_SERVICE, StopAlarm)
                .setContentIntent(StopAlarm)
                .setTicker("Notification");
        manager.notify(notificationId, builder.build());
    }

    public String setTimeString(long millisUntilFinished){
        String stime="";
        long hours = millisUntilFinished/1000/60/60;
        long minutes = millisUntilFinished/1000/60 - hours*60;
        long seconds = millisUntilFinished/1000 - hours*60*60 - minutes*60;

        if (hours!=0){
            if (hours<10){
                stime+="0";
            }
            stime+=hours+" : ";
        }
        if (!(hours==0&&minutes==0)){
            if (minutes<10){
                stime+="0";
            }
            stime+=minutes+" : ";
        }
        if (seconds<10){
            stime+="0";
        }
        stime+=seconds;
        return stime;
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("untilfinish",untilFinish);
        if (pauseBtn!=null)
        outState.putString("pausebtnval",pauseBtn.getText().toString());
        if (time!=null)
        outState.putString("timestring",time.getText().toString());
        outState.putBoolean("isTimerRunning",isTimerRunning);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("TAG", "onAttach:");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("TAG", "onDetach:");
    }
}