package com.fantavier.bierbattle.bierbattle.model;

import android.util.Log;

import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group implements DataProvider.DatabaseReferenceObject{

    private static final String TAG = "Group";
    private DatabaseReference dbRef = null;
    private DataProvider.PropertiesLoaded propertiesLoaded = null;
    private String groupId = "";
    //private String category = "";
    //private String starttime = "";
    private Long endtime = 0l;
    //private boolean active = false;
    private List<Member> members = null;
    private HashMap<String, Appointment> appointments = null;
    private Thread endtimeWatcher = null;

    public Group(){
        appointments = new HashMap<>();
    }

    public String getGroupId(){ return this.groupId; }
    public Long getEndtime(){ return this.endtime; }
    //public String getCategory() { return this.category; }

    public HashMap<String, Appointment> getAppointments(){
        return appointments;
    }
    public List<Member> getMembers() { return members; }

    public Appointment getAppointment(Integer index, String key) {
        if(index != null){
            List keys = new ArrayList(this.appointments.keySet());
            String appointmentId = keys.get(index).toString();
            checkAppointmentActiveStatus(appointmentId);
            return this.appointments.get(appointmentId);

        } else if (key != null){
            checkAppointmentActiveStatus(key);
            return this.appointments.get(key);
        }
        return null;
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

    public Integer getRankOfMember(String uid) throws ExceptionHelper.MemberNotFoundException{
        for(int i = 0; i < getMembers().size(); i++){
            if(getMembers().get(i).getMemberId().equals(uid)){
                return i+1;
            }
        }
        throw new ExceptionHelper.MemberNotFoundException();
    }


    public ArrayList<String> getAppointmentTitles(){
        ArrayList<String> appointmentStrings = new ArrayList<String>();
        try {
            for (Map.Entry<String, Appointment> appointment : this.appointments.entrySet()) {
                if(appointment.getValue().getActive() || !appointment.getValue().getVotingend())
                    appointmentStrings.add(appointment.getValue().toString());

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
            appointment.put("createtime", currentTime);

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
                            break;*/
                        case "endtime":
                            Group.this.endtime = Long.parseLong(groupData.getValue().toString());
                            break;
                        case "members":
                            Group.this.members = loadMembers(groupData);
                            break;
                        case "appointments":
                            Group.this.appointments = loadAppointments(groupData);
                            break;
                    }
                }
                DataProvider.groupListener.onGroupeDataChanged();
                Group.this.createEndtimeWatcher();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public void setPropertiesLoaded(DataProvider.PropertiesLoaded listener) {
        propertiesLoaded = listener;
    }

    public Boolean checkAppointmentStarts(){
        Boolean starts = false;
        for(Map.Entry<String, Appointment> appointment : appointments.entrySet()){
            if (appointment.getValue().isStarted() && !appointment.getValue().isBlocked()) {
                appointment.getValue().setBlocked(true);
                starts =  true;
                break;
            }
        }
        return starts;
    }

    public void checkAppointmentActiveStatus(String appointmentId){
        if(!appointments.get(appointmentId).getActive() && appointments.get(appointmentId).getVotingend())
            appointments.remove(appointmentId);
    }

    private HashMap<String, Appointment> loadAppointments(DataSnapshot appointmentsDS){
            Appointment.clearThreadList();
            for(DataSnapshot appointmentDS : appointmentsDS.getChildren()) {
                    Appointment appointment = appointments.get(appointmentDS.getKey().toString());
                    if(appointment == null){
                        appointment = new Appointment(this);
                        appointment.loadObjectProperties(appointmentDS.getKey());
                        appointments.put(appointmentDS.getKey(), appointment);
                    } else {
                        appointment.loadObjectProperties(appointmentDS.getKey());
                    }
            }
            watchAppointments();
        return appointments;
    }

    private void watchAppointments(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean running = true;
                    while(running){
                        if(checkAppointmentsLoaded()){
                            if(DataProvider.appointmentListener != null){
                                running = false;
                                DataProvider.appointmentListener.onAppointmentDataChangedListener();
                                Thread.interrupted();
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("WatchAppointments");
        thread.start();
    }

    private boolean checkAppointmentsLoaded(){
        for(Map.Entry<String, Appointment> appointment : appointments.entrySet()){
            if(!appointment.getValue().isLoaded()){
                return false;
            }
        }
        return true;
    }

    private List<Member> loadMembers(DataSnapshot membersDS){
        List<Member> members = new ArrayList<Member>();
        for(DataSnapshot memberDS : membersDS.getChildren()){
            Member member = new Member( this);
            member.loadObjectProperties(memberDS.getKey());
            members.add(member);
        }
        return members;
    }

    public void createEndtimeWatcher(){
        Long endtime = Group.this.getEndtime();
        final Long restTime = endtime - System.currentTimeMillis();
        DataProvider.endtimeListener.onEndtimeChanged();
        if(restTime <= 0){
            DataProvider.roundEndListener.onRoundEnd();
        } else {
            Group.this.endtimeWatcher = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(restTime);
                        DataProvider.roundEndListener.onRoundEnd();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            Group.this.endtimeWatcher.setName("endtimeWatcher");
            Group.this.endtimeWatcher.start();
        }
    }

}
