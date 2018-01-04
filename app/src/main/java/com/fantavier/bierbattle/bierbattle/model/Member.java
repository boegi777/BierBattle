package com.fantavier.bierbattle.bierbattle.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Paul on 04.01.2018.
 */

public class Member implements GroupProvider.DatabaseReferenceObject, Comparable{
    private static final String TAG = "Member";
    private DatabaseReference dbRef;
    private String memberId;
    private String name = "";
    private int points;
    private boolean active;
    private Group parentRef = null;

    public Member(String memberId, Group parentRef){
        try {
            this.parentRef = parentRef;
            this.initObjectProperties(memberId);
        } catch (Exception e){
            throw e;
        }
    }

    public void setPoints(String points){ this.points = Integer.parseInt(points); }

    public void setActive(Boolean active) { this.active = active; }

    public void setMemberName(String memberId){
        try {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference userRef = usersRef.child(memberId);

            userRef.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Member.this.name = dataSnapshot.getValue().toString();
                        GroupProvider.memberTitleListener.onMemberTitleChangedListener();
                    } catch (Exception e) {
                        throw e;
                    }

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

    public int getPoints(){
        return this.points;
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
    public void initObjectProperties(String id) {
        this.memberId = id;
        this.dbRef = getDbRef();
        setMemberName(id);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
}
