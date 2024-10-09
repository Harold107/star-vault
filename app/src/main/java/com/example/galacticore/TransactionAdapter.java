package com.example.galacticore;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import java.util.Locale;


public class TransactionAdapter extends ArrayAdapter<Transaction> {
    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, R.layout.item_transaction, transactions);
    }

    int incomeGreen = Color.argb(255,21,180,1);
    int expenseRed = Color.argb(255,170,20,20);

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
        }

        TextView categoryView = convertView.findViewById(R.id.textViewCategory);
        TextView amountView = convertView.findViewById(R.id.textViewAmount);

        Transaction transaction = getItem(position);
        if (transaction != null) {
            categoryView.setText(transaction.getCategory());
            String amountText = (transaction.isIncome() ? "+" : "-") + String.format(Locale.getDefault(), "$%.2f", transaction.getAmount());
            amountView.setText(amountText);
            amountView.setTextColor(transaction.isIncome() ? incomeGreen : expenseRed);
        }

        return convertView;
    }
}