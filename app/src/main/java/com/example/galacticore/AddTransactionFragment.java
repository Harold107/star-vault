package com.example.galacticore;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.galacticore.databinding.FragmentAddTransactionBinding;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {

    private FragmentAddTransactionBinding binding;
    private boolean isIncome = true;
    private final String[] incomeCategories = {"Salary", "Investments", "Gifts", "Other"};
    private final String[] expenseCategories = {"Food", "Transport", "Entertainment", "Bills", "Other"};
    private TextView categoryTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryTextView = binding.textViewCategory;

        setupToggleGroup();
        setupDatePicker();
        setupCategorySelection();
        setupSaveButton();
        setupCalculator();
    }

    private void setupToggleGroup() {
        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isIncome = checkedId == R.id.btnIncome;
                updateCategorySelection();
            }
        });
        // Set initial selection
        binding.toggleGroup.check(R.id.btnIncome);
    }

    private void setupDatePicker() {
        binding.editTextDate.setOnClickListener(v -> showDatePickerDialog());
        // Set current date as default
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        binding.editTextDate.setText(sdf.format(new Date()));
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    binding.editTextDate.setText(sdf.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupCategorySelection() {
        categoryTextView.setOnClickListener(v -> showCategoryDialog());
        updateCategorySelection();
    }

    private void showCategoryDialog() {
        String[] categories = isIncome ? incomeCategories : expenseCategories;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Category")
                .setItems(categories, (dialog, which) -> {
                    categoryTextView.setText(categories[which]);
                });
        builder.create().show();
    }

    private void updateCategorySelection() {
        categoryTextView.setText(R.string.select_category);
    }

    private void setupSaveButton() {
        binding.buttonSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String date = binding.editTextDate.getText().toString();
        String category = categoryTextView.getText().toString();
        String amountStr = binding.editTextAmount.getText().toString();
        String note = binding.editTextNote.getText().toString();

        if (date.isEmpty() || category.equals(getString(R.string.select_category)) || amountStr.isEmpty()) {
            Toast.makeText(getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        Transaction transaction = new Transaction(date, category, amount, note, isIncome);

        new Thread(() -> {
            MainActivity.db.transactionDao().insert(transaction);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), R.string.transaction_saved, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            });
        }).start();
    }

    private void setupCalculator() {
        View.OnClickListener calculatorClickListener = v -> {
            String currentAmount = binding.editTextAmount.getText().toString();
            String buttonText = ((android.widget.Button) v).getText().toString();

            switch (buttonText) {
                case "C":
                    binding.editTextAmount.setText("");
                    break;
                case "=":
                    try {
                        Expression expression = new ExpressionBuilder(currentAmount).build();
                        double result = expression.evaluate();
                        binding.editTextAmount.setText(String.valueOf(result));
                    } catch (Exception e) {
                        binding.editTextAmount.setText("Error");
                    }
                    break;
                default:
                    binding.editTextAmount.setText(currentAmount + buttonText);
                    break;
            }
        };

        // Attach the listener to all calculator buttons
        binding.button0.setOnClickListener(calculatorClickListener);
        binding.button1.setOnClickListener(calculatorClickListener);
        binding.button2.setOnClickListener(calculatorClickListener);
        binding.button3.setOnClickListener(calculatorClickListener);
        binding.button4.setOnClickListener(calculatorClickListener);
        binding.button5.setOnClickListener(calculatorClickListener);
        binding.button6.setOnClickListener(calculatorClickListener);
        binding.button7.setOnClickListener(calculatorClickListener);
        binding.button8.setOnClickListener(calculatorClickListener);
        binding.button9.setOnClickListener(calculatorClickListener);
        binding.buttonDot.setOnClickListener(calculatorClickListener);
        binding.buttonClear.setOnClickListener(calculatorClickListener);
        binding.buttonEquals.setOnClickListener(calculatorClickListener);
        binding.buttonPlus.setOnClickListener(calculatorClickListener);
        binding.buttonMinus.setOnClickListener(calculatorClickListener);
        binding.buttonMultiply.setOnClickListener(calculatorClickListener);
        binding.buttonDivide.setOnClickListener(calculatorClickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}