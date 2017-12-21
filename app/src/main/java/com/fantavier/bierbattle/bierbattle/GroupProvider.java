package com.fantavier.bierbattle.bierbattle;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
        private List<Appointment> appointments;

        public Group(
                String groupId,
                String category,
                String starttime,
                String endtime,
                List<Member> members,
                boolean active,
                List<Appointment> appointments)
        {
            this.groupId = groupId;
            this.active = active;
            this.category = category;
            this.starttime = starttime;
            this.endtime = endtime;
            this.members = members;
            this.appointments = appointments;
        }

        public ArrayList<String> getMemberStrings(){
            ArrayList<String> memberStrings = new ArrayList<String>();

            for(Member member : this.members){
                memberStrings.add(member.toString());
            }
            return memberStrings;
        }


        public ArrayList<String> getAppointmentStrings(){
            ArrayList<String> appointmentStrings = new ArrayList<String>();

            for(Appointment appointment : this.appointments){
                appointmentStrings.add(appointment.toString());
            }
            return appointmentStrings;
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
                        throw e;
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

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append("Name: ");
            builder.append(this.name);
            builder.append(" Punkte: ");
            builder.append(this.points);
            return builder.toString();
        }

    }

    public class Appointment {
        private String appointmentId;
        private String title;
        private String createtime;
        private String starttime;
        private Boolean votingend;
        private Boolean weekly;
        private HashMap<String, Boolean> votings;

        public Appointment(String appointmentIdId, String title, String createtime, String starttime, Boolean votingend, Boolean weekly, HashMap<String, Boolean> votings){
            this.appointmentId = appointmentIdId;
            this.title = title;
            this.createtime = createtime;
            this.starttime = starttime;
            this.votingend = votingend;
            this.weekly = weekly;
            this.votings = votings;
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append(this.title);
            if(votingend == false){
                builder.append("   -- Abstimmung --");
            }
            return builder.toString();
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

    private Group getGroupData(DataSnapshot dataSnapshot){
        String key = dataSnapshot.getKey();
        boolean active = false;
        String category = "";
        String starttime = "";
        String endtime = "";
        List<Member> members = new ArrayList<Member>();
        List<Appointment> appointments = new ArrayList<Appointment>();

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
                case "appointments":
                    appointments = getAppointments(child);
                    break;
            }

        }

        return new Group(key, category, starttime, endtime, members, active, appointments);
    }

    private List<Member> getMembers(DataSnapshot membersDS){
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

    private List<Appointment> getAppointments(DataSnapshot appointmentsDS){
        List<Appointment> appointments = new ArrayList<Appointment>();
        String key = "";
        String title = "";
        String createtime = "";
        String starttime = "";
        boolean votingend = false;
        HashMap<String, Boolean> votings = new HashMap<>();
        boolean weekly = false;

        for(DataSnapshot appointmentDS : appointmentsDS.getChildren()){
            key = appointmentDS.getKey();
            for(DataSnapshot appointmentData : appointmentDS.getChildren()) {
                switch (appointmentData.getKey()) {
                    case "title":
                        title = appointmentData.getValue().toString();
                        break;
                    case "createtime":
                        createtime = appointmentData.getValue().toString();
                        break;
                    case "starttime":
                        starttime = appointmentData.getValue().toString();
                        break;
                    case "votingend":
                        votingend = (boolean) appointmentData.getValue();
                        break;
                    case "votings":
                        votings = getVotings(appointmentData);
                        break;
                    case "weekly":
                        weekly = (boolean) appointmentData.getValue();
                        break;
                }
            }
            Appointment appointment = new Appointment(key, title, createtime, starttime, votingend, weekly, votings);
            appointments.add(appointment);
        }
        return appointments;
    }

    private HashMap<String, Boolean> getVotings(DataSnapshot votingsDS){
        HashMap<String, Boolean> votings = new HashMap<>();

        for(DataSnapshot voting : votingsDS.getChildren()){
            boolean value = (boolean) voting.getValue();
            votings.put(voting.getKey().toString(), value);
        }

        return votings;

    }
}