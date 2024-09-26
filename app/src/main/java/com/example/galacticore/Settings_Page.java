package com.example.galacticore;

import android.os.Bundle;

import android.view.*;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.textfield.TextInputLayout;

public class Settings_Page extends AppCompatActivity {

    String[] item = {"USD", "EUR", "GBP", "JPY", "CAD", "CNY", "MXN", "AUD", "RUB", "INR", "KRW"};

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_page);
        WindowInsert();
        Menu();
        CreatePopUpWindow();
    }

    private void CreatePopUpWindow() {
        LayoutInflater inflater =(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView=inflater.inflate(R.layout.reminder_poput, null);

        int width= ViewGroup.LayoutParams.MATCH_PARENT;
        int height=ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable=true;

        final PopupWindow popupWindow=new PopupWindow(popUpView, width, height, focusable);

        Switch reminderSwitch = findViewById(R.id.reminder_switch);
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    popupWindow.showAtLocation(buttonView, Gravity.CENTER, 0, 0);
                }else{
                    if(popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }

    private void WindowInsert(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //Dropdown Menu Code for Currency
    private void Menu(){
        textInputLayout = findViewById(R.id.textInputLayout);
        autoCompleteTextView = findViewById(R.id.dropdown_menu);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItem = adapterView.getItemAtPosition(i).toString();
            autoCompleteTextView.setText(selectedItem,false);
            Toast.makeText(Settings_Page.this, "Item: " + selectedItem, Toast.LENGTH_SHORT).show();
        });
    }
    //Logout button code
   /* private void Logout(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            binding.logoutButton.setOnClickListener(v ->
                    NavHostFragment.findNavController(Settings_Page.this)
                            .navigate(R.id.action_to_settings_Page_SecondFragment_)
            );
    }*/
}