package com.fantavier.bierbattle.bierbattle;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Paul on 15.12.2017.
 */

public class UserProvider {

    private static final String TAG = "UserProvider";
    private static DatabaseReference mDbRef;
    private UsernameListener usernameListener;

    public UserProvider(){
        usernameListener = null;
        loadUserData();
    }

    public interface UsernameListener {
        void onUsernameChanged(String username);
    }

    public static void createUser(Map<String, String> userData){
        /* Prüfen, ob Daten korrekt sind!! */
        mDbRef = FirebaseDatabase.getInstance().getReference("users").child(userData.get("uid"));
        mDbRef.setValue(userData);
    }

    public void setUsernameListener(UsernameListener listener){
        usernameListener = listener;
    }

    private void loadUserData(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference userRef = mDbRef.child(uid);

        userRef.child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (usernameListener != null) {
                        usernameListener.onUsernameChanged(dataSnapshot.getValue().toString());
                    }
                } catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    /* Fehlerbehandlung implementieren!! */
            }
        });
    }
}