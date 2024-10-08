package com.example.galacticore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import android.view.*;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.PendingIntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class Settings_Page extends AppCompatActivity {

    String[] item = {"USD", "EUR", "GBP", "JPY", "CAD", "CNY", "MXN", "AUD", "RUB", "INR", "KRW"};

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_page);
        WindowInsert();
        Menu();
        CreatePopUpWindow();
    }

    private void CreatePopUpWindow() {
        LayoutInflater inflater =(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView=inflater.inflate(R.layout.reminder_poput, null);

        int width= ViewGroup.LayoutParams.MATCH_PARENT;
        int height=ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable=true;

        final PopupWindow popupWindow=new PopupWindow(popUpView, width, height, focusable);

        TimePicker timepicker = popUpView.findViewById(R.id.Settings_timePicker);
        Button setAlarmButton = popUpView.findViewById(R.id.btnTimer);

        setAlarmButton.setOnClickListener(v ->{
            int hour = timepicker.getHour();
            int minute = timepicker.getMinute();

            setAlarm(hour, minute);

            popupWindow.dismiss();

            Toast.makeText(Settings_Page.this, "Alarm Set for: " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        });

        Switch reminderSwitch = findViewById(R.id.reminder_switch);
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    popupWindow.showAtLocation(buttonView, Gravity.CENTER, 0, 0);
                }else{
                    if(popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }

    private void setAlarm(int hour, int minute){
        AlarmManager AlarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void WindowInsert(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void Menu(){
        textInputLayout = findViewById(R.id.textInputLayout);
        autoCompleteTextView = findViewById(R.id.dropdown_menu);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItem = adapterView.getItemAtPosition(i).toString();
            autoCompleteTextView.setText(selectedItem,false);
            Toast.makeText(Settings_Page.this, "Item: " + selectedItem, Toast.LENGTH_SHORT).show();
        });
    }

}