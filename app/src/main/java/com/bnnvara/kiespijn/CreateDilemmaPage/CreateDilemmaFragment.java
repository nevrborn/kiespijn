package com.bnnvara.kiespijn.CreateDilemmaPage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnnvara.kiespijn.R;


/**
 *
 */
public class CreateDilemmaFragment extends Fragment {


    public static Fragment newInstance() {
        return new CreateDilemmaFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_dilemma, container, false);

        // set up the references


        // set up the listeners


        return view;
    }
}