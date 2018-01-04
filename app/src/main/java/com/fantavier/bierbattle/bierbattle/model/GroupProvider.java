package com.fantavier.bierbattle.bierbattle.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Paul on 15.12.2017.
 */

public class GroupProvider {

    private static final String TAG = "GroupProvider";
    private static DatabaseReference mDbRef;
    public static GroupProvider.GroupDataListener groupListener;
    public static GroupProvider.MemberTitleListener memberTitleListener;
    public static GroupProvider.AppointmentTitleListener appointmentTitleListener;
    public static GroupProvider.VoteChangedListener voteChangedListener;
    private static String groupId;
    private static Group group;

    public GroupProvider(String groupId){
        GroupProvider.groupId = groupId;
        loadGroupData();
    }

    public interface DatabaseReferenceObject{
        DatabaseReference getDbRef();
        void initObjectProperties(String id);
    }

    public interface GroupDataListener {
        void onGroupeDataChanged(Group group);
    }

    public interface MemberTitleListener {
        void onMemberTitleChangedListener();
    }

    public interface AppointmentTitleListener {
        void onAppointmentTitleChangedListener();
    }

    public interface VoteChangedListener {
        void onVoteChanged();
    }

    public void setGroupDataListener(GroupDataListener listener){ groupListener = listener; }
    public void setMemberTitleListener(MemberTitleListener listener) { memberTitleListener = listener; }
    public void setAppointmentTitleListener(AppointmentTitleListener listener) { appointmentTitleListener = listener; }
    //public void setVoteChangedListener(VoteChangedListener listener) { voteChangedListener = listener; }

    private void loadGroupData(){
        try {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDbRef = FirebaseDatabase.getInstance().getReference("groups");
            DatabaseReference groupRef = mDbRef.child(GroupProvider.groupId);

            groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (groupListener != null) {
                            setGroupData(dataSnapshot);
                            groupListener.onGroupeDataChanged(GroupProvider.group);
                        }
                    } catch (Exception e) {
                        throw e;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                }
            });
        } catch (Exception e){
            throw e;
        }
    }

    private void setGroupData(DataSnapshot dataSnapshot){
        if(GroupProvider.group == null){
            GroupProvider.group = new Group(dataSnapshot.getKey());
        }
    }
}