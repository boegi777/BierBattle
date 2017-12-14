package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Mir on 14.12.2017.
 */

public class MenueTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menue_tab, container, false);
        Button zaehler = (Button) rootView.findViewById(R.id.uebersicht);
        zaehler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),BierschuldenZaehler.class);
                startActivity(i);

            }
        });
        return rootView;
    }

}



