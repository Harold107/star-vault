package com.example.galacticore;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.galacticore.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    long insert(Goal goal);

    @Update
    void update(Goal goal);

    @Query("SELECT * FROM goals WHERE isCompleted = 0 LIMIT 1")
    Goal getCurrentGoal();

    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY completedDate DESC")
    List<Goal> getCompletedGoals();

    @Query("SELECT COUNT(*) FROM goals WHERE isCompleted = 1")
    int getCompletedGoalsCount();

    @Query("SELECT SUM(starsEarned) FROM goals")
    int getTotalStarsEarned();
}