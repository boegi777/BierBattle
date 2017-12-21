package com.fantavier.bierbattle.bierbattle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 15.12.2017.
 */

public class UserProvider {

    private static final String TAG = "UserProvider";
    private static DatabaseReference mDbRef;
    private static UsernameListener usernameListener;
    private static ActiveGroupListener activeGroupListener;

    public UserProvider() {
        loadUserData();
    }

    public interface UsernameListener {
        void onUsernameChanged(String username);
    }

    public interface ActiveGroupListener {
        void onActiveGroupChanged(String groupId);
    }

    public static void createUser(Map<String, String> userData) {
        /* Prüfen, ob Daten korrekt sind!! */
        mDbRef = FirebaseDatabase.getInstance().getReference("users").child(userData.get("uid"));
        userData.remove("uid");
        mDbRef.setValue(userData);
    }

    public void setUsernameListener(UsernameListener listener) {
        usernameListener = listener;
    }

    public void setActiveGroupListener(ActiveGroupListener listener) {
        activeGroupListener = listener;
    }

    private void loadUserData() {
        try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDbRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference userRef = mDbRef.child(uid);
            userRef.keepSynced(true);

            userRef.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (usernameListener != null) {
                            usernameListener.onUsernameChanged(dataSnapshot.getValue().toString());
                        }
                    } catch (Exception e) {
                        throw e;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    /* Fehlerbehandlung implementieren!! */
                }
            });
            userRef.child("groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (activeGroupListener != null) {
                            String activeGroupId = "";
                            HashMap<String, Boolean> groups = (HashMap<String, Boolean>) dataSnapshot.getValue();

                            for (Map.Entry<String, Boolean> entry : groups.entrySet()) {
                                if (entry.getValue() == true) {
                                    activeGroupListener.onActiveGroupChanged(entry.getKey().toString());
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch(Exception e){
          throw e;
        }
    }
}