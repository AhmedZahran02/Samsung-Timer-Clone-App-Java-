package com.zahran.timerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

public class SettingUpTimer extends Fragment{

    Button startBtn;
    FragmentManager fragmentManager;
    NumberPicker hourspicker,minutespicker,secondspicker;
    long millisecresult;
    boolean backPressed=false;
    boolean done=false;

    public SettingUpTimer() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startBtn=view.findViewById(R.id.startbtn);
        hourspicker = view.findViewById(R.id.numberPicker1);
        minutespicker = view.findViewById(R.id.numberPicker2);
        secondspicker = view.findViewById(R.id.numberPicker3);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
                if (!backPressed){
                    Toast.makeText(getContext(),"press again to exit",Toast.LENGTH_SHORT).show();
                    backPressed=true;
                }
                else if(!done){
                    getActivity().onBackPressed();
                    done=true;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        hourspicker.setMinValue(0);
        hourspicker.setMaxValue(99);
        hourspicker.setWrapSelectorWheel(true);
        hourspicker.setValue(hourspicker.getValue());

        hourspicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i<10){
                    return "0"+Integer.toString(i);
                }
                return Integer.toString(i + 0);
            }
        });

        minutespicker.setMinValue(0);
        minutespicker.setMaxValue(59);
        minutespicker.setWrapSelectorWheel(true);
        minutespicker.setValue(minutespicker.getValue());

        minutespicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i<10){
                    return "0"+Integer.toString(i);
                }
                return Integer.toString(i);
            }
        });

        secondspicker.setMinValue(0);
        secondspicker.setMaxValue(59);
        secondspicker.setWrapSelectorWheel(true);
        secondspicker.setValue(secondspicker.getValue());

        secondspicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                if (i<10){
                    return "0"+Integer.toString(i);
                }
                return Integer.toString(i);
            }
        });

        if (savedInstanceState!=null){
            hourspicker.setValue(savedInstanceState.getInt("hours"));
            minutespicker.setValue(savedInstanceState.getInt("minutes"));
            secondspicker.setValue(savedInstanceState.getInt("seconds"));
        }

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                millisecresult=hourspicker.getValue()*60*60*1000+minutespicker.getValue()*60*1000+secondspicker.getValue()*1000;

                if (millisecresult>0){
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, TimerStarted.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    TimerStarted.startTime=millisecresult;
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_up_timer, container, false);
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (hourspicker!=null)
        outState.putInt("hours",hourspicker.getValue());
        if (minutespicker!=null)
            outState.putInt("minutes",minutespicker.getValue());
        if (secondspicker!=null)
            outState.putInt("seconds",secondspicker.getValue());
    }
}