package com.example.galacticore;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

                    // rocket position based on fuel score
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
            int reward = (int) (Math.random() * 50);  // random reward
            fuel += reward;
        }
    }

    private void saveFuel() {
        // saving improves fuel efficiency.
        fuel += 10;  // example......
    }

    private void gameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Game Over");
        builder.setMessage("Your final score is: " + score + "\n\n" +
                "If you would like to learn more about saving and investing, click here.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Learn More", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.investopedia.com/articles/investing/022516/saving-vs-investing-understanding-key-differences.asp"));
                startActivity(browserIntent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}