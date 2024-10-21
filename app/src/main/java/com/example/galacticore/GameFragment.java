package com.example.galacticore;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameFragment extends Fragment {

    private ProgressBar fuelBar;
    private ImageView rocket;
    private TextView scoreView;
    private int score = 0;
    private int fuel = 100;
    private boolean isGameRunning = true;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        fuelBar = view.findViewById(R.id.fuel_bar);
        rocket = view.findViewById(R.id.rocket_image);
        scoreView = view.findViewById(R.id.score_view);
        Button investButton = view.findViewById(R.id.invest_button);
        Button saveButton = view.findViewById(R.id.save_button);

        investButton.setOnClickListener(v -> invest());
        saveButton.setOnClickListener(v -> saveFuel());

        startGameLoop();

        return view;
    }

    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isGameRunning) {
                    // decrease fuel over time
                    fuel--;
                    fuelBar.setProgress(fuel);

                    // update rocket position based on fuel or score
                    score++;
                    scoreView.setText("SCORE: " + score);

                    if (fuel > 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        isGameRunning = false;
                        gameOver();
                    }
                }
            }
        }, 1000);
    }

    private void invest() {
        // investing decrease fuel for risk but possible bonus fuel
        if (fuel >= 20) {
            fuel -= 20;
            int reward = (int) (Math.random() * 50);  // Random reward
            fuel += reward;
        }
    }

    private void saveFuel() {
        // saving improves fuel efficiency.
        fuel += 10;  // example......
    }

    private void gameOver() {
        // need to put this in later
    }
}