package com.example.galacticore;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;
import com.example.galacticore.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static AppDatabase db;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialized Room database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "transaction-database")
                .fallbackToDestructiveMigration()
                .build();

        // Set up Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = binding.bottomNavigationView;
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Set up the FAB for adding new transactions
            binding.newTransactionBtn.setOnClickListener(v ->
                    navController.navigate(R.id.action_homeFragment_to_addTransactionFragment)
            );

            // Set up bottom navigation item selection
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.bnt_home) {
                    navController.navigate(R.id.bnt_home);
                    return true;
                } else if (itemId == R.id.bnt_setting) {
                    navController.navigate(R.id.settingFragment);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}