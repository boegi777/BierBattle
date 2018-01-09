package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Mir on 14.12.2017.
 */

public class TermineTab extends Fragment {

    private static final String TAG = "TermineTab";
    public static ListView appointmentList;
    public static Button createAppointmentButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.termine_tab, container, false);
        appointmentList = (ListView) rootView.findViewById(R.id.AppointmentList);

        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent appointmentDetails = new Intent(rootView.getContext(), TerminDetail.class);
                Integer index = i;
                appointmentDetails.putExtra("Index", index.toString());
                startActivity(appointmentDetails);
            }
        });

        createAppointmentButton = (Button) rootView.findViewById(R.id.terminErstellen);

        createAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createAppointment = new Intent(rootView.getContext(), TerminErstellen.class);
                startActivity((createAppointment));
            }
        });

        return rootView;
    }
}
