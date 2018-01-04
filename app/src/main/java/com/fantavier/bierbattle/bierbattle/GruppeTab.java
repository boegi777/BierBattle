package com.fantavier.bierbattle.bierbattle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    public void onResume(){
        super.onResume();
        MainActivity main = (MainActivity) getActivity();
        main.setGroupListener();
    }
}
