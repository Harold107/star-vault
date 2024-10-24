package com.example.galacticore;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
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

        // Initialize Room database
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

            // Setup bottom navigation with NavController
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Set up the FAB for adding new transactions
            binding.newTransactionBtn.setOnClickListener(v -> {
                if (navController.getCurrentDestination().getId() == R.id.homeFragment) {
                    navController.navigate(R.id.action_homeFragment_to_addTransactionFragment);
                }
            });

            // Handle visibility of navigation items
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                boolean showBottomNav = !(destination.getId() == R.id.loginFragment ||
                        destination.getId() == R.id.registrationFragment ||
                        destination.getId() == R.id.addTransactionFragment);

                bottomNav.setVisibility(showBottomNav ? View.VISIBLE : View.GONE);
                binding.newTransactionBtn.setVisibility(
                        destination.getId() == R.id.homeFragment ? View.VISIBLE : View.GONE
                );
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}