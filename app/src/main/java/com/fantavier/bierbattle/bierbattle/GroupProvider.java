package com.fantavier.bierbattle.bierbattle;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 15.12.2017.
 */

public class GroupProvider {

    private static final String TAG = "GroupProvider";
    private static DatabaseReference mDbRef;
    private static GroupProvider.GroupDataListener groupListener;
    private static GroupProvider.MemberNameListener memberNameListener;
    private static String groupId;

    public GroupProvider(String groupId){
        GroupProvider.groupId = groupId;
        loadGroupData();
    }

    public interface GroupDataListener {
        void onGroupeDataChanged(Group group);
    }

    public interface MemberNameListener {
        void onMemberNameListener();
    }


    public class Group {
        private String groupId;
        private String category;
        private String starttime;
        private String endtime;
        private boolean active;
        private List<Member> members;

        public Group(
                String groupId,
                String category,
                String starttime,
                String endtime,
                List<Member> members,
                boolean active)
        {
            this.groupId = groupId;
            this.active = active;
            this.category = category;
            this.starttime = starttime;
            this.endtime = endtime;
            this.members = members;
        }

        public ArrayList<String> getMemberStrings(){
            ArrayList<String> memberStrings = new ArrayList<String>();

            for(Member member : this.members){
                StringBuilder builder = new StringBuilder();
                builder.append("Name: ");
                builder.append(member.name);
                builder.append(" Punkte: ");
                builder.append(member.points);
                memberStrings.add(builder.toString());
            }
            return memberStrings;
        }
    }

    public class Member implements Comparable {
        private String memberId;
        private String name = "";
        private int points;
        private boolean active;
        public Member(String memberId, String points, boolean active){
            this.memberId = memberId;
            this.points = Integer.parseInt(points);
            this.active = active;
            setMemberName(this.memberId);
        }

        public void setMemberName(String memberId){
            mDbRef = FirebaseDatabase.getInstance().getReference("users");
            DatabaseReference userRef = mDbRef.child(memberId);

            userRef.child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Member.this.name = dataSnapshot.getValue().toString();
                        memberNameListener.onMemberNameListener();
                    } catch (Exception e){
                        Log.d(TAG, e.getMessage());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                }
            });
        }

        public int getPoints(){
            return this.points;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            int comparePoints = ((Member)o).getPoints();
            return comparePoints-this.points;
        }

    }

    public void setGroupDataListener(GroupDataListener listener){
        groupListener = listener;
    }
    public void setMemberNameListener(MemberNameListener listener) { memberNameListener = listener; }

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
                            Group group = getGroupData(dataSnapshot);
                            groupListener.onGroupeDataChanged(group);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                }
            });
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private Group getGroupData(DataSnapshot dataSnapshot){
        String key = dataSnapshot.getKey();
        boolean active = false;
        String category = "";
        String starttime = "";
        String endtime = "";
        List<Member> members = new ArrayList<Member>();

        for(DataSnapshot child : dataSnapshot.getChildren()){

            switch(child.getKey()){
                case "active":
                    active = (boolean) child.getValue();
                    break;
                case "category":
                    category = child.getValue().toString();
                    break;
                case "starttime":
                    starttime = child.getValue().toString();
                    break;
                case "endtime":
                    endtime = child.getValue().toString();
                    break;
                case "members":
                    members = getMembers(child);
                    break;
            }

        }

        return new Group(key, category, starttime, endtime, members, active);
    }

    public List<Member> getMembers(DataSnapshot membersDS){
        List<Member> members = new ArrayList<Member>();

        String key = "";
        String points = "0";
        boolean active = false;
        for(DataSnapshot memberDS : membersDS.getChildren()){
            key = memberDS.getKey();
            for(DataSnapshot memberData : memberDS.getChildren()){
                switch(memberData.getKey()){
                    case "points":
                        points = memberData.getValue().toString();
                        break;
                    case "active":
                        active = (boolean) memberData.getValue();
                        break;
                }
            }
            Member member = new Member(key, points, active);
            members.add(member);
        }

        Collections.sort(members);
        return members;
    }
}