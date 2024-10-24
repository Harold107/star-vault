package com.example.galacticore;

```java
        package com.example.galacticore;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.example.galacticore.MainActivity;
import com.example.galacticore.R;
import com.example.galacticore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GameFragment extends Fragment {
    private View view;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int level = 1;
    private int xp = 0;
    private int nextLevelXp = 1000;
    private int stars = 0;
    private List<String> achievements = new ArrayList<>();
    private static final String PREFS_NAME = "GamePrefs";
    private boolean isAnimating = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game, container, false);
        loadSavedGameState();
        initializeViews();
        return view;
    }

    private void loadSavedGameState() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        level = prefs.getInt("level", 1);
        xp = prefs.getInt("xp", 0);
        stars = prefs.getInt("stars", 0);
        Set<String> savedAchievements = prefs.getStringSet("achievements", new HashSet<>());
        achievements = new ArrayList<>(savedAchievements);
        nextLevelXp = level * 1000;
    }

    private void saveGameState() {
        SharedPreferences.Editor editor = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putInt("level", level);
        editor.putInt("xp", xp);
        editor.putInt("stars", stars);
        editor.putStringSet("achievements", new HashSet<>(achievements));
        editor.apply();
    }

    private void initializeViews() {
        setupGameBackground();
        setupRocketAnimation();
        updateStats();
        loadProgress();
        setupGameCards();
    }

    private void setupGameBackground() {
        ConstraintLayout gameSpace = view.findViewById(R.id.gameSpace);
        gameSpace.post(() -> {
            int width = gameSpace.getWidth();
            int height = gameSpace.getHeight();

            for (int i = 0; i < 20; i++) {
                ImageView star = createStar(width, height);
                gameSpace.addView(star);
                animateStar(star);
            }
        });
    }

    private ImageView createStar(int maxWidth, int maxHeight) {
        ImageView star = new ImageView(requireContext());
        star.setImageResource(R.drawable.star);
        star.setAlpha(0.3f + (float)(Math.random() * 0.4f));

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                (int)(16 * getResources().getDisplayMetrics().density),
                (int)(16 * getResources().getDisplayMetrics().density));

        params.leftMargin = (int)(Math.random() * maxWidth);
        params.topMargin = (int)(Math.random() * maxHeight);
        star.setLayoutParams(params);

        return star;
    }

    private void animateStar(ImageView star) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(star, "alpha",
                star.getAlpha(), star.getAlpha() - 0.2f);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    private void setupRocketAnimation() {
        ImageView rocket = view.findViewById(R.id.rocketShip);
        ObjectAnimator animator = ObjectAnimator.ofFloat(rocket, "translationY", 0f, -50f);
        animator.setDuration(2000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void loadProgress() {
        new Thread(() -> {
            double totalSavings = MainActivity.db.transactionDao().getTotalIncome();
            SharedPreferences prefs = requireContext().getSharedPreferences("GoalPrefs", Context.MODE_PRIVATE);
            float currentGoal = prefs.getFloat("current_goal", 40006.00f);

            int progress = (int)((totalSavings / currentGoal) * 100);
            xp = progress * 10;
            level = (xp / 1000) + 1;
            nextLevelXp = level * 1000;
            stars = progress / 20;

            handler.post(() -> {
                updateStats();
                checkAchievements();
            });
        }).start();
    }

    private void updateStats() {
        ((TextView) view.findViewById(R.id.levelText)).setText("Level " + level);
        ((TextView) view.findViewById(R.id.xpText)).setText("XP: " + xp + "/" + nextLevelXp);
        ((TextView) view.findViewById(R.id.starsText)).setText(String.valueOf(stars));
        ((ProgressBar) view.findViewById(R.id.xpProgress)).setProgress((int)((float)xp / nextLevelXp * 100));
        ((TextView) view.findViewById(R.id.achievementsText)).setText(String.valueOf(achievements.size()));
    }

    private void setupGameCards() {
        setupStatsCard();
        setupAchievementsCard();
        setupBoostCard();
    }

    private void setupStatsCard() {
        CardView statsCard = view.findViewById(R.id.statsCard);
        statsCard.setRadius(30f);
        statsCard.setCardElevation(10f);
    }

    private void setupAchievementsCard() {
        CardView achievementsCard = view.findViewById(R.id.achievementsCard);
        achievementsCard.setRadius(30f);
        achievementsCard.setCardElevation(10f);
    }

    private void setupBoostCard() {
        CardView boostCard = view.findViewById(R.id.boostCard);
        boostCard.setRadius(30f);
        boostCard.setCardElevation(10f);
    }

    private void checkAchievements() {
        new Thread(() -> {
            double totalSavings = MainActivity.db.transactionDao().getTotalIncome();

            if (totalSavings >= 1000 && !achievements.contains("First1000")) {
                addAchievement("First1000", "Saved your first $1,000!");
            }

            List<Transaction> transactions = MainActivity.db.transactionDao().getRecentTransactions();
            if (hasSevenDayStreak(transactions)) {
                addAchievement("WeekStreak", "7 days of consistent saving!");
            }

            if (stars >= 10) {
                addAchievement("StarCollector", "Collected 10 stars!");
            }

            handler.post(this::updateStats);
        }).start();
    }

    private boolean hasSevenDayStreak(List<Transaction> transactions) {
        if (transactions.size() < 7) return false;

        Set<String> dates = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Transaction transaction : transactions) {
            dates.add(transaction.getDate());
            if (dates.size() >= 7) return true;
        }

        return false;
    }

    private void addAchievement(String title, String description) {
        if (!achievements.contains(title)) {
            achievements.add(title);
            showAchievementPopup(title, description);
            saveGameState();
        }
    }

    private void showAchievementPopup(String title, String description) {
        handler.post(() -> {
            CardView popup = view.findViewById(R.id.achievementPopup);
            popup.setVisibility(View.VISIBLE);
            popup.setAlpha(1f);

            ((TextView) popup.findViewById(R.id.achievementTitle)).setText(title);
            ((TextView) popup.findViewById(R.id.achievementDescription)).setText(description);

            handler.postDelayed(() -> {
                popup.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction(() -> popup.setVisibility(View.GONE))
                        .start();
            }, 3000);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        saveGameState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        view = null;
    }
}
```