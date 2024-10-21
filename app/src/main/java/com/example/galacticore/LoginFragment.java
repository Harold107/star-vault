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
import android.content.Context;
import android.content.SharedPreferences;

public class LoginFragment extends Fragment {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;

    // Preset username and password for test
    //private String correctUsername = "admin";
    //private String correctPassword = "12345";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        usernameInput = view.findViewById(R.id.username_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_btn);
        registerButton = view.findViewById(R.id.register_btn);

        // Set onClickListener for the login button
        loginButton.setOnClickListener(v -> checkLogin(view));


        loginButton.setOnClickListener(v -> checkLogin(view));
        registerButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registrationFragment));

        return view;
    }

//test
    // Method to check username and password
    private void checkLogin(View view) {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", null);
        String savedPassword = sharedPreferences.getString("password", null);


        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter both Username and Password", Toast.LENGTH_SHORT).show();
        } else if (username.equals(savedUsername) && password.equals(savedPassword)) {
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


































