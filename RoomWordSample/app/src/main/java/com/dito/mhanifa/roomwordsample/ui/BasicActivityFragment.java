package com.dito.mhanifa.roomwordsample.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dito.mhanifa.roomwordsample.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BasicActivityFragment extends Fragment {

    public BasicActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_basic, container, false);
    }
}
