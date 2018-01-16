package com.fantavier.bierbattle.bierbattle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Mir on 14.12.2017.
 */

public class GruppeTab extends Fragment {

    private static final String TAG = "GruppeTab";
    public static ListView groupList;
    public static TextView endtime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gruppe_tab, container, false);
        groupList = (ListView) rootView.findViewById(R.id.GroupList);
        endtime = rootView.findViewById(R.id.endtime);
        return rootView;
    }
}
