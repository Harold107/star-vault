package com.example.galacticore;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.galacticore.databinding.DialogTransactionOptionsBinding;
import com.example.galacticore.databinding.FragmentHomeBinding;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TransactionAdapter adapter;
    private final List<Transaction> transactions = new ArrayList<>();
    private boolean isAnimationInProgress = false;
    private double currentGoal = 40006.00;
    private static final String PREFS_NAME = "GoalPrefs";
    private static final String KEY_CURRENT_GOAL = "current_goal";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadSavedGoal();
        setupUI();
        setupTransactionList();
        loadTransactions();
    }

    private void loadSavedGoal() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentGoal = prefs.getFloat(KEY_CURRENT_GOAL, (float) currentGoal);
    }

    private void setupUI() {
        // Initial setup
        binding.warningDialog.setVisibility(View.GONE);
        binding.congratsText.setAlpha(0f);
        binding.rocketRest.setAlpha(0f);

        // Setup rocket animation
        Animation rocketFly = AnimationUtils.loadAnimation(requireContext(), R.anim.rocket_animation);
        binding.rocketHome.startAnimation(rocketFly);

        // Setup reset goal button
        binding.resetConfirm.setOnClickListener(v -> handleGoalReset());
    }

    private void setupTransactionList() {
        adapter = new TransactionAdapter(requireContext(), transactions);
        binding.listViewTransaction.setAdapter(adapter);
        binding.listViewTransaction.setOnItemClickListener((parent, view, position, id) -> {
            Transaction selectedTransaction = transactions.get(position);
            showTransactionOptions(selectedTransaction);
        });
    }

    private void loadTransactions() {
        new Thread(() -> {
            List<Transaction> loadedTransactions = MainActivity.db.transactionDao().getRecentTransactions();
            double totalIncome = MainActivity.db.transactionDao().getTotalIncome();
            double totalExpenses = MainActivity.db.transactionDao().getTotalExpenses();

            requireActivity().runOnUiThread(() -> {
                transactions.clear();
                transactions.addAll(loadedTransactions);
                adapter.notifyDataSetChanged();
                updateUI(totalIncome, totalExpenses);
            });
        }).start();
    }

    private void updateUI(double totalIncome, double totalExpenses) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        binding.textViewMonthlyIncomeNumber.setText(currencyFormat.format(totalIncome));
        binding.textViewMonthlyExpensesNumber.setText(currencyFormat.format(totalExpenses));
        binding.textViewMonthlyBalanceNumber.setText(currencyFormat.format(totalIncome - totalExpenses));
        binding.textViewGoalNumber.setText(currencyFormat.format(currentGoal));

        // Update date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        binding.textViewViewDate.setText(dateFormat.format(new Date()));

        // Update progress
        int progress = (int) ((totalIncome / currentGoal) * 100);
        binding.currentGoalBar.setProgress(Math.min(progress, 100));

        if (progress >= 100 && !isAnimationInProgress) {
            startGoalCompleteAnimation();
        }
    }

    private void handleGoalReset() {
        onDetach();
        String newGoalStr = binding.resetNumber.getText().toString();
        if (newGoalStr.isEmpty()) {
            Toast.makeText(getContext(), R.string.invalid_goal_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double newGoal = Double.parseDouble(newGoalStr);
            if (newGoal <= 0) {
                Toast.makeText(getContext(), R.string.invalid_goal_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            // Save new goal
            SharedPreferences.Editor editor = requireContext()
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit();
            editor.putFloat(KEY_CURRENT_GOAL, (float) newGoal);
            editor.apply();

            currentGoal = newGoal;
            Toast.makeText(getContext(), "New goal set successfully!", Toast.LENGTH_SHORT).show();

            // Reset UI and navigate
            resetUIAfterGoalSet();

            // Force reload home fragment
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack(R.id.homeFragment, true);
            navController.navigate(R.id.homeFragment);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), R.string.invalid_goal_amount, Toast.LENGTH_SHORT).show();
        }
    }

    private void startGoalCompleteAnimation() {
        isAnimationInProgress = true;

        // Fade out current UI
        Animation fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_animation);
        binding.currentGoalBar.startAnimation(fadeOut);
        binding.rocketHome.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                binding.currentGoalBar.setVisibility(View.INVISIBLE);
                binding.rocketHome.setVisibility(View.INVISIBLE);
                showGoalCompleteUI();
            }
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
        });

        // Move moon to center
        Animation moveCenter = AnimationUtils.loadAnimation(requireContext(), R.anim.move_center_animation);
        binding.moonHome.startAnimation(moveCenter);
    }

    private void showGoalCompleteUI() {
        Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_animation);

        binding.congratsText.setAlpha(1f);
        binding.warningDialog.setVisibility(View.VISIBLE);
        binding.rocketRest.setVisibility(View.VISIBLE);

        binding.congratsText.startAnimation(fadeIn);
        binding.warningDialog.startAnimation(fadeIn);
        binding.rocketRest.startAnimation(fadeIn);
    }

    private void resetUIAfterGoalSet() {
        Animation fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_animation);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                binding.warningDialog.setVisibility(View.GONE);
                binding.congratsText.setVisibility(View.GONE);
                binding.rocketRest.setVisibility(View.GONE);
                binding.currentGoalBar.setVisibility(View.VISIBLE);
                binding.rocketHome.setVisibility(View.VISIBLE);

                isAnimationInProgress = false;
                binding.currentGoalBar.setProgress(0);
                loadTransactions();
            }
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
        });

        binding.warningDialog.startAnimation(fadeOut);
        binding.congratsText.startAnimation(fadeOut);
        binding.rocketRest.startAnimation(fadeOut);
    }

    private void showTransactionOptions(Transaction transaction) {
        Dialog dialog = new Dialog(requireContext());
        DialogTransactionOptionsBinding dialogBinding = DialogTransactionOptionsBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.modifyButton.setOnClickListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transaction", transaction);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_addTransactionFragment, bundle);
        });

        dialogBinding.deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteTransaction(transaction);
        });

        dialog.show();
    }

    private void deleteTransaction(Transaction transaction) {
        new Thread(() -> {
            MainActivity.db.transactionDao().delete(transaction);
            requireActivity().runOnUiThread(this::loadTransactions);
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}