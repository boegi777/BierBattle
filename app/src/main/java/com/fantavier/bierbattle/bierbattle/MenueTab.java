package com.fantavier.bierbattle.bierbattle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;



public class MenueTab extends Fragment {

    private static final String TAG = "MenueTab";
    private DatabaseReference usersRef;
    public TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menue_tab, container, false);
        ImageButton zurueck = (ImageButton) rootView.findViewById(R.id.bier);
        try {
            username = (TextView) rootView.findViewById(R.id.username);
            if(MainActivity.userProvider == null){
                MainActivity.userProvider = new UserProvider();
            }
            MainActivity.userProvider.setUsernameListener(new UserProvider.UsernameListener(){
                @Override
                public void onUsernameChanged(String username) {
                    MenueTab.this.username.setText(username);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }


        zurueck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BierschuldenZaehler.class);
                startActivity(i);

            }
        });


         return rootView;

    }

    @Override
    public void onStart(){
        super.onStart();
        try {
            if(MainActivity.userProvider == null){
                MainActivity.userProvider = new UserProvider();
            }
            MainActivity.userProvider.setUsernameListener(new UserProvider.UsernameListener(){
                @Override
                public void onUsernameChanged(String username) {
                    MenueTab.this.username.setText(username);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

}



