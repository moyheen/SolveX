package com.moyheen.user.solvex.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moyheen.user.solvex.R;

/**
 * Created by moyheen on 21-Dec-14.
 */
public class PersonalBestFragment extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_best, container, false);
        return rootView;
    }
}