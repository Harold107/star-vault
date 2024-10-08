package com.example.galacticore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.wakeup_audio);
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }

        Toast.makeText(context, "Alarm ringing!", Toast.LENGTH_SHORT).show();
    }

}
