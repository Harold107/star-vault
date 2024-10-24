package com.example.galacticore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel extends ViewModel {
    private final MutableLiveData<Integer> level = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> stars = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> xp = new MutableLiveData<>(0);

    public LiveData<Integer> getLevel() { return level; }
    public LiveData<Integer> getStars() { return stars; }
    public LiveData<Integer> getXp() { return xp; }

    public void updateProgress(double savings, double goal) {
        int progress = (int)((savings / goal) * 100);
        int currentXp = progress * 10;
        int currentLevel = (currentXp / 1000) + 1;
        int currentStars = progress / 20;

        xp.setValue(currentXp);
        level.setValue(currentLevel);
        stars.setValue(currentStars);
    }
}