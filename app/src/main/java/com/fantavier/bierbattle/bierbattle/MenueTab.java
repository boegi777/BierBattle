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
import android.widget.Toast;

import com.fantavier.bierbattle.bierbattle.model.UserProvider;
import com.google.firebase.database.DatabaseReference;



public class MenueTab extends Fragment {

    private static final String TAG = "MenueTab";
    private DatabaseReference usersRef;
    private ImageButton kamera_btn;

    public TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menue_tab, container, false);
         /*if(!requestLocationUpdates()){
            Intent j = new Intent(getApplicationContext(),Location.class);
            startService(j);
        }*/
        ImageButton zurueck = (ImageButton) rootView.findViewById(R.id.bier);
        kamera_btn = (ImageButton) rootView.findViewById(R.id.kamera);
        kamera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanner = new Intent(getActivity(), QRScanner.class);
                startActivity(scanner);
            }
        });

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
    /* @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }*/

    /* private boolean requestLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET}, 1);
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionResults(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates();
                }
                break;
        }
    hhh}*/
}



