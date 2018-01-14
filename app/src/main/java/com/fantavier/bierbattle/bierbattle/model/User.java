package com.fantavier.bierbattle.bierbattle.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Paul on 09.01.2018.
 */

public class User implements DataProvider.DatabaseReferenceObject {

    private DatabaseReference dbRef;
    private String userId;
    private String username;
    private String category;
    private HashMap<String, Boolean> groups;

    public String getUserId(){ return userId; }
    public String getUsername() { return username; }
    public String getCategory() { return category; }
    public HashMap<String, Boolean> getGroups(){ return groups; }

    @Override
    public DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference("users").child(userId);
    }

    @Override
    public void loadObjectProperties(String id) {
        this.userId = id;
        this.dbRef = getDbRef();
        this.dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot appointmentDS) {
                for(DataSnapshot appointmentData : appointmentDS.getChildren()) {
                    switch (appointmentData.getKey()) {
                        case "username":
                            User.this.username = appointmentData.getValue().toString();
                            break;
                        case "groups":
                            User.this.groups = initGroups(appointmentData);
                            break;
                        case "category":
                            User.this.category = appointmentData.getValue().toString();
                            break;
                    }
                    DataProvider.userListener.onUserDataChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private HashMap<String, Boolean> initGroups(DataSnapshot groupsDS){
        HashMap<String, Boolean> groups = new HashMap<>();

        for(DataSnapshot group : groupsDS.getChildren()){
            HashMap<String, Object> value = (HashMap<String, Object>) group.getValue();
            groups.put(group.getKey().toString(), (Boolean) value.get("active"));
        }

        return groups;

    }
}
