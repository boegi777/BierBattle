package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fantavier.bierbattle.bierbattle.model.DataProvider;
import com.google.firebase.database.DatabaseReference;



public class MenueTab extends Fragment {

    private static final String TAG = "MenueTab";
    private DatabaseReference usersRef;
    private ImageButton kamera_btn;

    public static TextView username = null;
    public static TextView rank = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menue_tab, container, false);

        username = rootView.findViewById(R.id.username);
        rank = rootView.findViewById(R.id.rank);
        ImageButton zurueck = (ImageButton) rootView.findViewById(R.id.bier);
        kamera_btn = (ImageButton) rootView.findViewById(R.id.kamera);
        kamera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanner = new Intent(getActivity(), QRScanner.class);
                startActivity(scanner);
            }
        });

        zurueck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BierschuldenZaehler.class);
                startActivity(i);

            }
        });


         return rootView;

    }
}



