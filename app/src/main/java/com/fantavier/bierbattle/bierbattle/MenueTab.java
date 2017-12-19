package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Mir on 14.12.2017.
 */

public class MenueTab extends Fragment {

    private static final String TAG = "MenueTab";
    private DatabaseReference usersRef;
    private UserProvider userProvider;

    public TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menue_tab, container, false);
        ImageButton zaehler = (ImageButton) rootView.findViewById(R.id.bier);

        try {
            username = (TextView) rootView.findViewById(R.id.username);
            userProvider = new UserProvider();
            userProvider.setUsernameListener(new UserProvider.UsernameListener(){
                @Override
                public void onUsernameChanged(String username) {
                    MenueTab.this.username.setText(username);
                }
            });

        } catch (Exception e) {
            Intent startLogin = new Intent(getActivity(), Login.class);
            startActivity(startLogin);
        }
        rootView = inflater.inflate(R.layout.menue_tab, container, false);
        Button zaehler = (Button) rootView.findViewById(R.id.uebersicht);
        Button logout = (Button) rootView.findViewById(R.id.logout);
        username = (TextView) rootView.findViewById(R.id.username);

        zaehler.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BierschuldenZaehler.class);
                startActivity(i);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent login = new Intent(getActivity(), Login.class);
                startActivity(login);
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



