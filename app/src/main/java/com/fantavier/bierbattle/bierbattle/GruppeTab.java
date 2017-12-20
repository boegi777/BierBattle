package com.fantavier.bierbattle.bierbattle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mir on 14.12.2017.
 */

public class GruppeTab extends Fragment {

    private static final String TAG = "GruppeTab";
    public static ListView groupList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gruppe_tab, container, false);
        groupList = (ListView) rootView.findViewById(R.id.GroupList);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        MainActivity main = (MainActivity) getActivity();
        main.setGroupListener();
    }
}
