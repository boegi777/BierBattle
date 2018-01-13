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
    public List<Member> getMembers() { return members; }

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
                            break;
                        case "endtime":
                            Group.this.endtime = groupData.getValue().toString();
                            break;*/
                        case "members":
                            Group.this.members = loadMembers(groupData);
                            break;
                        case "appointments":
                            Group.this.appointments = loadAppointments(groupData);
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

    private List<Appointment> loadAppointments(DataSnapshot appointmentsDS){
            Integer oldCount = 0;
            if(Appointment.count > 0){
                oldCount = Appointment.count;
            }
            appointments = new ArrayList<>();
            Appointment.clearThreadList();
            for(DataSnapshot appointmentDS : appointmentsDS.getChildren()) {
                    Appointment appointment = new Appointment(this);
                    appointment.loadObjectProperties(appointmentDS.getKey());
                    appointments.add(appointment);
            }
            Appointment.count = appointments.size();
            if(oldCount != 0 && oldCount < Appointment.count){
                DataProvider.appointmentCreatedListener.onAppointmentCreatedListener();
            }
        return appointments;
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
}
