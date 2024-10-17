package com.example.galacticore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.galacticore.databinding.FragmentTransactionDetailBinding;
import java.util.Locale;

public class TransactionDetailFragment extends Fragment {
    private FragmentTransactionDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Transaction transaction = (Transaction) requireArguments().getSerializable("transaction");
        if (transaction != null) {
            binding.textViewDate.setText(transaction.getDate());
            binding.textViewCategory.setText(transaction.getCategory());
            binding.textViewAmount.setText(String.format(Locale.getDefault(), "%.2f", transaction.getAmount()));
            binding.textViewType.setText(transaction.isIncome() ? "Income" : "Expense");

            // Display the notes
            String notes = transaction.getNote();
            if (notes != null && !notes.isEmpty()) {
                binding.textViewNote.setText(notes);
                binding.textViewNote.setVisibility(View.VISIBLE);
                binding.textViewNoteLabel.setVisibility(View.VISIBLE);
            } else {
                binding.textViewNote.setVisibility(View.GONE);
                binding.textViewNoteLabel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}