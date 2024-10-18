package com.example.galacticore;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.galacticore.databinding.FragmentHomeBinding;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TransactionAdapter adapter;
    private List<Transaction> transactions = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTransactionList();
        loadTransactions();

        // Rocket animation
        ImageView rocket = (ImageView) getView().findViewById(R.id.rocket_home);
        Animation rocket_fly = AnimationUtils.loadAnimation(this.getContext(), R.anim.rocket_animation);
        rocket.setAnimation(rocket_fly);
        // current goal color
        TextView goal = (TextView) getView().findViewById((R.id.textView_goalNumber));
        TextView congrats = (TextView) getView().findViewById(R.id.congrats_text);
        setTextViewColor(goal, getResources().getColor(R.color.txt_lightPink),
                getResources().getColor(R.color.txt_darkPink));
        setTextViewColor(congrats, getResources().getColor(R.color.txt_lightPink),
                getResources().getColor(R.color.txt_darkPink));
    }

    private void setTextViewColor(TextView textView, int...color) {
        TextPaint paint = textView.getPaint();
        float width = paint.measureText(textView.getText().toString());

        Shader shader = new LinearGradient(0, 0, width, textView.getTextSize(), color, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader);
        textView.setTextColor(color[0]);
    }

    private void setupTransactionList() {
        adapter = new TransactionAdapter(requireContext(), transactions);
        binding.listViewTransaction.setAdapter(adapter);
        binding.listViewTransaction.setOnItemClickListener((parent, view, position, id) -> {
            Transaction selectedTransaction = transactions.get(position);
            showTransactionOptions(selectedTransaction);
        });
    }

    private void showTransactionOptions(Transaction transaction) {
        String[] options = {"View Details", "Modify", "Delete"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Transaction Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // View Details
                            navigateToTransactionDetail(transaction);
                            break;
                        case 1: // Modify
                            navigateToModifyTransaction(transaction);
                            break;
                        case 2: // Delete
                            deleteTransaction(transaction);
                            break;
                    }
                })
                .show();
    }

    private void navigateToTransactionDetail(Transaction transaction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("transaction", transaction);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_transactionDetailFragment, bundle);
    }

    private void navigateToModifyTransaction(Transaction transaction) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("transaction", transaction);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_homeFragment_to_addTransactionFragment, bundle);
    }

    private void deleteTransaction(Transaction transaction) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        MainActivity.db.transactionDao().delete(transaction);
                        requireActivity().runOnUiThread(this::loadTransactions);
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
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
        binding.textViewMonthlyIncomeNumber.setText(String.format(Locale.getDefault(), "$%.2f", totalIncome));
        binding.textViewMonthlyExpensesNumber.setText(String.format(Locale.getDefault(), "$%.2f", totalExpenses));
        double balance = totalIncome - totalExpenses;
        binding.textViewMonthlyBalanceNumber.setText(String.format(Locale.getDefault(), "$%.2f", balance));

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        binding.textViewViewDate.setText(sdf.format(new Date()));

        // Update goal progress
        double goalAmount = 9009.00; // Assuming the goal is $1,000.01
        int progress = (int) ((totalIncome / goalAmount) * 100);
        binding.currentGoalBar.setProgress(Math.min(progress, 100));
        binding.textViewGoalNumber.setText(prettyNumber(goalAmount));

        goalAnim();
    }

    private String prettyNumber(double goal) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        return "$" + nf.format(goal);
    }

    private void goalAnim() {
        int progress = binding.currentGoalBar.getProgress();
        CardView backdrop = (CardView) getView().findViewById(R.id.cardView_mainBackdrop);
        ImageView rocket_fly = (ImageView) getView().findViewById(R.id.rocket_home);
        ImageView moon = (ImageView) getView().findViewById(R.id.moon_home);
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.current_goal_bar);
        ImageView rocket_rest = (ImageView) getView().findViewById(R.id.rocket_rest);
        TextView congrats = (TextView) getView().findViewById(R.id.congrats_text);

        if(progress == 100){
            //Toast.makeText(getActivity(), "Reach Goal", Toast.LENGTH_SHORT).show();
            //backdrop move down
            Animation moveDown = AnimationUtils.loadAnimation(this.getContext(), R.anim.move_down_animation);
            backdrop.startAnimation(moveDown);
            //fade out
            Animation fadeOut = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_out_animation);
            progressBar.startAnimation(fadeOut);
            rocket_fly.startAnimation(fadeOut);
            //moon move center
            Animation moveCenter = AnimationUtils.loadAnimation(this.getContext(), R.anim.move_center_animation);
            moon.startAnimation(moveCenter);
            //fade in
            Animation fadeIn = AnimationUtils.loadAnimation(this.getContext(), R.anim.fade_in_animation);
            rocket_rest.startAnimation(fadeIn);
            congrats.setAlpha(1);
            congrats.startAnimation(fadeIn);
        }
        else{

        }
    }



    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}