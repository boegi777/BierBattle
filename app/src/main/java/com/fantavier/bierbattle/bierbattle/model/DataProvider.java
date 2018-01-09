package com.fantavier.bierbattle.bierbattle.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DataProvider {

    private static final String TAG = "DataProvider";
    private static DatabaseReference mDbRef;
    private static DatabaseReference userRef;
    private static DatabaseReference groupRef;
    private static DataProvider.UsernameListener usernameListener;
    public static DataProvider.GroupDataListener groupListener;
    public static DataProvider.MemberDataListener memberDataListener;
    public static DataProvider.AppointmentDataListener appointmentDataListener;
    private static String groupId;
    private static Group group;

    public DataProvider(){
        loadUserData();
    }

    public interface DatabaseReferenceObject{
        DatabaseReference getDbRef();
        void initObjectProperties(String id);
    }

    public interface UsernameListener {
        void onUsernameChanged(String username);
    }

    public interface GroupDataListener {
        void onGroupeDataChanged(Group group);
    }

    public interface MemberDataListener {
        void onMemberDataChangedListener();
    }

    public interface AppointmentDataListener {
        void onAppointmentDataChangedListener();
    }

    public void setUsernameListener(DataProvider.UsernameListener listener) { usernameListener = listener; }
    public void setGroupDataListener(GroupDataListener listener){ groupListener = listener; }
    public void setMemberDataListener(MemberDataListener listener) { memberDataListener = listener; }
    public void setAppointmentDataListener(AppointmentDataListener listener) { appointmentDataListener = listener; }

    public static void createUser(Map<String, String> userData) {
        try {
            /* Daten überprüfen */
            mDbRef = FirebaseDatabase.getInstance().getReference("users").child(userData.get("uid"));
            userData.remove("uid");
            mDbRef.setValue(userData);
        } catch(Exception ex){
            throw ex;
        }
    }

    private void loadUserData() {
        try {
            setUserRef();
            initUsernameChangedListener();
            setActiveGroupId();
        } catch(Exception e){
            throw e;
        }
    }

    private void loadGroupData(){
        try {
            setGroupRef();
            initGroupDataChangedListener();
        } catch (Exception e){
            throw e;
        }
    }

    private void setGroupData(DataSnapshot dataSnapshot){
        if(DataProvider.group == null){
            DataProvider.group = new Group(dataSnapshot.getKey());
        }
    }

    private void setUserRef(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference("users");
        userRef = mDbRef.child(uid);
    }

    private void initUsernameChangedListener(){
        userRef.child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (usernameListener != null) {
                    usernameListener.onUsernameChanged(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void setActiveGroupId(){
        userRef.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> groups = (HashMap<String, Boolean>) dataSnapshot.getValue();
                for (Map.Entry<String, Boolean> entry : groups.entrySet()) {
                    if (entry.getValue() == true) {
                        DataProvider.groupId = entry.getKey().toString();
                        loadGroupData();
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

    private void setGroupRef(){
        mDbRef = FirebaseDatabase.getInstance().getReference("groups");
        groupRef = mDbRef.child(DataProvider.groupId);
    }

    private void initGroupDataChangedListener(){
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (groupListener != null) {
                    setGroupData(dataSnapshot);
                    groupListener.onGroupeDataChanged(DataProvider.group);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
}