package com.fantavier.bierbattle.bierbattle.model;

import android.util.Log;

import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Group implements DataProvider.DatabaseReferenceObject{

    private static final String TAG = "Group";
    private DatabaseReference dbRef = null;
    private String groupId = "";
    //private String category = "";
    //private String starttime = "";
    //private String endtime = "";
    //private boolean active = false;
    private List<Member> members = null;
    private List<Appointment> appointments = null;

    public String getGroupId(){ return this.groupId; }
    //public String getCategory() { return this.category; }

    public List<Appointment> getAppointments(){
        return appointments;
    }

    public Appointment getAppointment(Integer index) {
        checkAppointmentActiveStatus(index);
        return this.appointments.get(index);
    }

    public Member getMember(String uid) throws ExceptionHelper.MemberNotFoundException{
        for(Member member : members){
            if(member.getMemberId().equals(uid)){
                return member;
            }
        }
        throw new ExceptionHelper.MemberNotFoundException();
    }

    public ArrayList<String> getMemberTitles(){
        ArrayList<String> memberStrings = new ArrayList<String>();

        Collections.sort(members);
        for(Member member : this.members){
            memberStrings.add(member.toString());
        }
        return memberStrings;
    }


    public ArrayList<String> getAppointmentTitles(){
        ArrayList<String> appointmentStrings = new ArrayList<String>();
        try {
            for (Appointment appointment : this.appointments) {
                if(appointment.getActive() || !appointment.getVotingend())
                    appointmentStrings.add(appointment.toString());

            }

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return appointmentStrings;
    }

    public boolean createAppointment(
            String title,
            String date,
            String time,
            String location,
            Boolean weekly
    ){
        try {
            HashMap<String, Object> appointment = new HashMap<>();

            if(title.isEmpty() || date.isEmpty() || time.isEmpty()){
                throw new ExceptionInInitializerError("missing values");
            }
            Long currentTime = new Date().getTime();

            appointment.put("active", false);
            appointment.put("title", title);
            appointment.put("date", date);
            appointment.put("time", time);
            appointment.put("location", location);
            appointment.put("votingend", false);
            appointment.put("weekly", weekly);
            appointment.put("createtime", currentTime.toString());

            return dbRef.child("appointments").push().setValue(appointment).isSuccessful();
        } catch(Exception  e){
            throw e;
        }
    }

    @Override
    public DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference("groups").child(groupId);
    }

    @Override
    public void loadObjectProperties(String id) {
        this.groupId = id;
        this.dbRef = getDbRef();
        this.dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupDS) {
                for (DataSnapshot groupData : groupDS.getChildren()) {
                    switch (groupData.getKey()) {
                        /*case "active":
                            Group.this.active = Boolean.parseBoolean(groupData.getValue().toString());
                            break;
                        case "category":
                            Group.this.category = groupData.getValue().toString();
                            break;
                        case "starttime":
                            Group.this.starttime = groupData.getValue().toString();
                            break;
                        case "endtime":
                            Group.this.endtime = groupData.getValue().toString();
                            break;*/
                        case "members":
                            Group.this.members = getMembers(groupData);
                            break;
                        case "appointments":
                            Group.this.appointments = getAppointments(groupData);
                            break;
                    }
                }
                DataProvider.groupListener.onGroupeDataChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public Boolean checkAppointmentStarts(){
        Boolean starts = false;
        for(Appointment appointment : getAppointments()){
            if (appointment.isStarted()) {
                starts =  true;
                break;
            }
        }
        return starts;
    }

    public void checkAppointmentActiveStatus(int index){
        if(!appointments.get(index).getActive() && appointments.get(index).getVotingend())
            appointments.remove(index);
    }

    private List<Appointment> getAppointments(DataSnapshot appointmentsDS){
        List<Appointment> appointments = new ArrayList<Appointment>();
        for(DataSnapshot appointmentDS : appointmentsDS.getChildren()){
            Appointment appointment = new Appointment(this);
            appointment.loadObjectProperties(appointmentDS.getKey());
            appointments.add(appointment);
        }
        return appointments;
    }

    private List<Member> getMembers(DataSnapshot membersDS){
        List<Member> members = new ArrayList<Member>();
        for(DataSnapshot memberDS : membersDS.getChildren()){
            Member member = new Member( this);
            member.loadObjectProperties(memberDS.getKey());
            members.add(member);
        }
        return members;
    }
}
