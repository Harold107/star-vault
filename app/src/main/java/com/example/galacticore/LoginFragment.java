package com.example.galacticore;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;

    // Preset username and password for demonstration
    private String correctUsername = "admin";
    private String correctPassword = "12345";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        usernameInput = view.findViewById(R.id.username_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_btn);

        // Set onClickListener for the login button
        loginButton.setOnClickListener(v -> checkLogin(view));

        return view;
    }

    // Method to check username and password
    private void checkLogin(View view) {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter both Username and Password", Toast.LENGTH_SHORT).show();
        } else if (username.equals(correctUsername) && password.equals(correctPassword)) {
            // Login successful
            Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
            //Nav to home page
        } else {
            // Login failed
            Toast.makeText(getActivity(), "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
        }
    }
}


































