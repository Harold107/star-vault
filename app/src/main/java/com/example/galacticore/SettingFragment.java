package com.example.galacticore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class SettingFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting,container,false);

        createPopUpWindow(view);

        return view;
    }
    private void createPopUpWindow(View fragmentView) {
        Switch reminderSwitch = fragmentView.findViewById(R.id.reminder_switch);

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showPopUpWindow(fragmentView);
            }
        });
    }
    private void showPopUpWindow(View fragmentView) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.reminder_poput, null);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);

        TimePicker timePicker = popUpView.findViewById(R.id.Settings_timePicker);
        Button setAlarmButton = popUpView.findViewById(R.id.btnTimer);

        setAlarmButton.setOnClickListener(v -> {
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Check if permission is granted before setting the alarm
            if (isExactAlarmPermissionGranted()) {
                // Set the alarm
                setAlarm(hour, minute);

                // Dismiss the popup after setting the alarm
                popupWindow.dismiss();

                Toast.makeText(getContext(), "Alarm set for: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
            } else {
                // If permission is not granted, direct the user to app settings
                requestExactAlarmPermission();
            }
        });

        // Show the popup window
        popupWindow.showAtLocation(fragmentView, Gravity.CENTER, 0, 0);
    }

    private void setAlarm(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Toast.makeText(getContext(), "AlarmManager not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private boolean isExactAlarmPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // For Android versions lower than S, permission is not required
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }
}