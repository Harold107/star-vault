package com.example.galacticore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroup;
    private EditText editTextDate;
    private Spinner spinnerCategory;
    private EditText editTextAmount;
    private EditText editTextNote;
    private Button buttonSave;
    private ViewGroup calculatorLayout;

    private boolean isIncome = true;
    private List<String> incomeCategories = Arrays.asList("Salary", "Investments", "Gifts", "Other");
    private List<String> expenseCategories = Arrays.asList("Food", "Transport", "Entertainment", "Bills", "Other");

    private StringBuilder currentInput = new StringBuilder();
    private String currentOperation = "";
    private double firstOperand = 0;
    private boolean isNewOperation = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        toggleGroup = view.findViewById(R.id.toggleGroup);
        editTextDate = view.findViewById(R.id.editTextDate);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextNote = view.findViewById(R.id.editTextNote);
        buttonSave = view.findViewById(R.id.buttonSave);
        calculatorLayout = view.findViewById(R.id.calculatorLayout);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToggleGroup();
        setupCategorySpinner();
        setupDateInput();
        setupCalculator();
        setupSaveButton();
    }

    private void setupToggleGroup() {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isIncome = checkedId == R.id.btnIncome;
                updateCategorySpinner();
            }
        });
        toggleGroup.check(R.id.btnIncome);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                isIncome ? incomeCategories : expenseCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void updateCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                isIncome ? incomeCategories : expenseCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDateInput() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        editTextDate.setText(currentDate);
    }

    private void setupCalculator() {
        String[] buttonLabels = {
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "C", "0", ".", "="
        };

        for (String label : buttonLabels) {
            MaterialButton button = new MaterialButton(requireContext());
            button.setText(label);
            button.setOnClickListener(v -> onCalculatorButtonClick(label));
            calculatorLayout.addView(button);
        }
    }

    private void onCalculatorButtonClick(String label) {
        switch (label) {
            case "=":
                calculateResult();
                break;
            case "C":
                clearCalculator();
                break;
            case "+":
            case "-":
            case "×":
                setOperation(label);
                break;
            default:
                appendNumber(label);
                break;
        }
        updateAmountDisplay();
    }

    private void appendNumber(String number) {
        if (isNewOperation) {
            currentInput = new StringBuilder(number);
            isNewOperation = false;
        } else {
            currentInput.append(number);
        }
    }

    private void setOperation(String operation) {
        if (!currentInput.toString().isEmpty()) {
            if (!currentOperation.isEmpty()) {
                calculateResult();
            }
            firstOperand = Double.parseDouble(currentInput.toString());
            currentOperation = operation;
            isNewOperation = true;
        }
    }

    private void calculateResult() {
        if (!currentOperation.isEmpty() && !isNewOperation) {
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
            currentInput = new StringBuilder(String.valueOf(result));
            currentOperation = "";
            isNewOperation = true;
        }
    }

    private void clearCalculator() {
        currentInput = new StringBuilder();
        currentOperation = "";
        firstOperand = 0;
        isNewOperation = true;
    }

    private void updateAmountDisplay() {
        editTextAmount.setText(currentInput.toString());
    }

    private void setupSaveButton() {
        buttonSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String date = editTextDate.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String amountStr = editTextAmount.getText().toString();
        String note = editTextNote.getText().toString();

        if (date.isEmpty() || category.isEmpty() || amountStr.isEmpty()) {
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
}