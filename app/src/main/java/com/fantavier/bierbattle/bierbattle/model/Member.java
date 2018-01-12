package com.fantavier.bierbattle.bierbattle.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Paul on 04.01.2018.
 */

public class Member implements Comparable, DataProvider.DatabaseReferenceObject {

    private static final String TAG = "Member";
    private DatabaseReference dbRef;
    private String memberId;
    private String name = "";
    private int points = 0;
    private boolean active;
    private Group parentRef = null;

    public Member(Group parentRef){
        this.parentRef = parentRef;
    }

    public String getMemberId(){ return this.memberId; }
    public int getPoints(){
        return this.points;
    }
    public void setPoints(Integer points) {
        points += getPoints();
        dbRef.child("points").setValue(points.toString());
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int comparePoints = ((Member)o).getPoints();
        return comparePoints-this.points;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ");
        builder.append(this.name);
        builder.append(" Punkte: ");
        builder.append(this.points);
        return builder.toString();
    }

    @Override
    public DatabaseReference getDbRef() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("groups");
        DatabaseReference groupRef = dbRef.child(parentRef.getGroupId());
        return groupRef.child("members").child(this.memberId);
    }

    @Override
    public void loadObjectProperties(String id) {
        this.memberId = id;
        this.dbRef = getDbRef();
        this.dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot memberDS) {
                for(DataSnapshot memberData : memberDS.getChildren()) {
                    switch(memberData.getKey()){
                        case "points":
                            Member.this.points = Integer.parseInt(memberData.getValue().toString());
                            break;
                        case "active":
                            Member.this.active = Boolean.parseBoolean(memberData.getValue().toString());
                            break;
                    }
                }
                Member.this.loadMemberName(Member.this.getMemberId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void loadMemberName(String memberId){
        try {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference userRef = usersRef.child(memberId);

            userRef.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Member.this.name = dataSnapshot.getValue().toString();
                    DataProvider.memberDataListener.onMemberDataChangedListener();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        } catch (Exception e){
            throw e;
        }
    }
}
