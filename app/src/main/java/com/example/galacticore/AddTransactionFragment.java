package com.example.galacticore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.galacticore.databinding.FragmentAddTransactionBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {

    private FragmentAddTransactionBinding binding;
    private StringBuilder currentInput = new StringBuilder();
    private String currentOperation = "";
    private double firstOperand = 0;
    private boolean isNewOperation = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToggleGroup();
        setupCategorySpinner();
        setupSaveButton();
        setupCalculatorButtons();
        setupDateInput();
    }

    @SuppressLint("ResourceType")
    private void setupToggleGroup() {
        binding.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                // Handle Income selection
               // if (checkedId == R.id.btnIncome)
                 //   binding.btnDot.setBackgroundResource(getResources().getColor(R.color.gradient_2_purple));
              //  else if (checkedId == R.id.btnExpense) {
                    // Handle Expense selection
                   // binding.btnDot.setBackgroundColor(getResources().getColor(R.color.gradient_2_purple));
              //  }
            }
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setupSaveButton() {
        binding.buttonSave.setOnClickListener(v -> saveTransaction());
    }

    private void setupDateInput() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        binding.editTextDate.setText(currentDate);
    }

    private void setupCalculatorButtons() {
        View.OnClickListener numberListener = v -> {
            Button button = (Button) v;
            currentInput.append(button.getText().toString());
            updateAmountDisplay();
        };

        View.OnClickListener operationListener = v -> {
            Button button = (Button) v;
            if (!currentInput.toString().isEmpty()) {
                if (!isNewOperation) {
                    calculateResult();
                }
                firstOperand = Double.parseDouble(currentInput.toString());
                currentOperation = button.getText().toString();
                isNewOperation = false;
                currentInput.setLength(0);
            }
        };

        binding.btn0.setOnClickListener(numberListener);
        binding.btn1.setOnClickListener(numberListener);
        binding.btn2.setOnClickListener(numberListener);
        binding.btn3.setOnClickListener(numberListener);
        binding.btn4.setOnClickListener(numberListener);
        binding.btn5.setOnClickListener(numberListener);
        binding.btn6.setOnClickListener(numberListener);
        binding.btn7.setOnClickListener(numberListener);
        binding.btn8.setOnClickListener(numberListener);
        binding.btn9.setOnClickListener(numberListener);
        binding.btnDoubleZero.setOnClickListener(numberListener);
        binding.btnDot.setOnClickListener(v -> {
            if (!currentInput.toString().contains(".")) {
                currentInput.append(".");
                updateAmountDisplay();
            }
        });

        binding.btnPlus.setOnClickListener(operationListener);
        binding.btnMinus.setOnClickListener(operationListener);
        binding.btnMultiply.setOnClickListener(operationListener);

        binding.btnEquals.setOnClickListener(v -> calculateResult());


    }

    private void updateAmountDisplay() {
        binding.editTextAmount.setText(currentInput.toString());
    }

    private void calculateResult() {
        if (!currentInput.toString().isEmpty() && !currentOperation.isEmpty()) {
            double secondOperand = Double.parseDouble(currentInput.toString());
            double result = 0;
            switch (currentOperation) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                    result = firstOperand * secondOperand;
                    break;
            }
            currentInput.setLength(0);
            currentInput.append(result);
            updateAmountDisplay();
            isNewOperation = true;
        }
    }

    private void saveTransaction() {
        String date = binding.editTextDate.getText().toString();
        String category = binding.spinnerCategory.getSelectedItem().toString();
        String amountStr = binding.editTextAmount.getText().toString();
        String note = binding.editTextNote.getText().toString();
        boolean isIncome = binding.toggleGroup.getCheckedButtonId() == R.id.btnIncome;

        if (date.isEmpty() || amountStr.isEmpty() || category.isEmpty()) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
}