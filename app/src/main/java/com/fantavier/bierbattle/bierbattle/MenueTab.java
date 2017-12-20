package com.fantavier.bierbattle.bierbattle;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Mir on 14.12.2017.
 */

public class MenueTab extends Fragment {

    private static final String TAG = "MenueTab";
    private DatabaseReference usersRef;
    private ImageButton kamera_btn;

    public TextView username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menue_tab, container, false);
        ImageButton zurueck = (ImageButton) rootView.findViewById(R.id.bier);
        kamera_btn = (ImageButton) rootView.findViewById(R.id.kamera);
        final Activity activity = getActivity();
        kamera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
                integrator.setOrientationLocked(false);
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
            Intent startLogin = new Intent(getActivity(), Login.class);
            startActivity(startLogin);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if(result != null){
            if(result.getContents()== null){
                Toast.makeText(getActivity(),"You cancelled the scanning",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(),result.getContents(),Toast.LENGTH_LONG).show();
            }
        }
        else{

            super.onActivityResult(requestCode,resultCode,data);
        }

    }


}



