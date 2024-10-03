package com.example.galacticore;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        //
        ProgressBar progressBar = view.findViewById(R.id.current_goal_bar);
        progressBar.getProgressDrawable().setColorFilter(Color.CYAN, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(0);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //rocket animation
        ImageView rocket = (ImageView) view.findViewById(R.id.rocket_home);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rocket_animation);
        rocket.startAnimation(animation);



        //transaction list
        String[] list = {"Food", "Tuition", "Book"};
        ListView listView = (ListView) view.findViewById(R.id.listView_transaction);
        ArrayAdapter<String> adopter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adopter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(position==0){
            Toast.makeText(getActivity(), "$500", Toast.LENGTH_SHORT).show();
        }
        if(position==1){
            Toast.makeText(getActivity(), "$3,000", Toast.LENGTH_SHORT).show();
        }
        if(position==2){
            Toast.makeText(getActivity(), "$80", Toast.LENGTH_SHORT).show();
        }
    }
}