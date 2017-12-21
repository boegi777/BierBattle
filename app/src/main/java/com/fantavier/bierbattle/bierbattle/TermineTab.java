package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Mir on 14.12.2017.
 */

public class TermineTab extends Fragment {

    private static final String TAG = "TermineTab";
    public static ListView appointmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.termine_tab, container, false);
        appointmentList = (ListView) rootView.findViewById(R.id.AppointmentList);

        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent appointmentDetails = new Intent(rootView.getContext(), TerminDetail.class);
                startActivity(appointmentDetails);
            }
        });

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        MainActivity main = (MainActivity) getActivity();
        main.setGroupListener();
    }
}
