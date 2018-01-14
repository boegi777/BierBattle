package com.fantavier.bierbattle.bierbattle.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 09.01.2018.
 */

public class User implements DataProvider.DatabaseReferenceObject {

    private DatabaseReference dbRef;
    private String userId;
    private String username;
    private String category;
    private Boolean active = false;
    private Boolean loaded = true;
    private HashMap<String, HashMap<String, Object>> groups;

    public String getUserId(){ return userId; }
    public String getUsername() { return username; }
    public String getCategory() { return category; }
    public Boolean getActive() { return active; }
    public Boolean isLoaded() { return loaded; }
    public HashMap<String, HashMap<String, Object>> getGroups(){ return groups; }

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
            public void onDataChange(DataSnapshot userDS) {
                for(DataSnapshot userData : userDS.getChildren()) {
                    switch (userData.getKey()) {
                        case "username":
                            User.this.username = userData.getValue().toString();
                            break;
                        case "active":
                            User.this.active = Boolean.parseBoolean(userData.getValue().toString());
                        case "groups":
                            User.this.groups = initGroups(userData);
                            break;
                        case "category":
                            User.this.category = userData.getValue().toString();
                            break;
                    }
                    loaded = true;
                    if(DataProvider.isActiveUser(userId));
                        DataProvider.userListener.onUserDataChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ");
        builder.append(this.username);
        builder.append(" Punkte: ");
        builder.append(getPoints());
        return builder.toString();
    }

    public int getPoints(){
        Integer points = 0;
        for(Map.Entry<String, HashMap<String, Object>> group : groups.entrySet()){
            for(Map.Entry<String, Object> groupData : group.getValue().entrySet()){
                if(groupData.getKey().equals("points")){
                    points += Integer.parseInt(groupData.getValue().toString());
                }
            }
        }
        return points;
    }

    private HashMap<String, HashMap<String, Object>> initGroups(DataSnapshot groupsDS){
        HashMap<String, HashMap<String, Object>> groups = new HashMap<>();

        for(DataSnapshot group : groupsDS.getChildren()){
            HashMap<String, Object> value = (HashMap<String, Object>) group.getValue();
            groups.put(group.getKey().toString(), value);
        }

        return groups;

    }
}
