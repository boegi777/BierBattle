package com.fantavier.bierbattle.bierbattle.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Appointment implements DataProvider.DatabaseReferenceObject {
    private DatabaseReference dbRef;
    private String appointmentId;
    private String title;
    private Long createtime;
    private String date;
    private String time;
    private Boolean votingend;
    private Boolean weekly;
    private String location;
    private HashMap<String, Boolean> votings = null;
    private Group parentRef = null;
    private Boolean active;

    public Appointment(String appointmentId, Group parentRef){
        try {
            this.parentRef = parentRef;
            this.initObjectProperties(appointmentId);
        } catch(Exception e){
            throw e;
        }
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title) {
        dbRef.child("title").setValue(title);
    }

    public Long getCreatetime() { return this.createtime; }
    public void setCreatetime(Long createtime) {
        dbRef.child("createtime").setValue(createtime);
    }

    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        dbRef.child("date").setValue(date);
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){ dbRef.child("time").setValue(time); }

    public boolean getWeekly(){
        return this.weekly;
    }
    public void setWeekly(boolean weekly){
        dbRef.child("weekly").setValue(weekly);
    }

    public String getLocation(){
        return this.location;
    }
    public void setLocation(String location) { dbRef.child("location").setValue(location); }

    public HashMap<String, Boolean> getVotings(){ return this.votings; }
    public void setVotings(HashMap<String, Boolean> votings){ dbRef.child("votings").setValue(votings); }

    public Boolean getVotingend(){ return this.votingend; }
    public void setVotingend(Boolean votingend){ dbRef.child("votingend").setValue(votingend); }

    public Boolean getActive(){ return this.active; }
    public void setActive(Boolean active){ dbRef.child("active").setValue(active); }

    public void setVoting(String uid, Boolean voting){
        this.dbRef.child("votings").child(uid).setValue(voting);
    }

    private HashMap<String, Boolean> initVotings(DataSnapshot votingsDS){
        HashMap<String, Boolean> votings = new HashMap<>();

        for(DataSnapshot voting : votingsDS.getChildren()){
            boolean value = (boolean) voting.getValue();
            votings.put(voting.getKey().toString(), value);
        }

        return votings;

    }

    @Override
    public void initObjectProperties(String id) {
        this.appointmentId = id;
        this.dbRef = getDbRef();
        this.dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot appointmentDS) {
                for(DataSnapshot appointmentData : appointmentDS.getChildren()) {
                    switch (appointmentData.getKey()) {
                        case "title":
                            Appointment.this.title = appointmentData.getValue().toString();
                            break;
                        case "createtime":
                            Appointment.this.createtime = Long.parseLong(appointmentData.getValue().toString());
                            break;
                        case "date":
                            Appointment.this.date = appointmentData.getValue().toString();
                            break;
                        case "time":
                            Appointment.this.time = appointmentData.getValue().toString();
                            break;
                        case "votingend":
                            Appointment.this.votingend = Boolean.parseBoolean(appointmentData.getValue().toString());
                            break;
                        case "votings":
                            Appointment.this.votings = initVotings(appointmentData);
                            break;
                        case "weekly":
                            Appointment.this.weekly  = Boolean.parseBoolean(appointmentData.getValue().toString());
                            break;
                        case "location":
                            Appointment.this.location = appointmentData.getValue().toString();
                            break;
                        case "active":
                            Appointment.this.active  = Boolean.parseBoolean(appointmentData.getValue().toString());
                            break;
                    }
                }
                DataProvider.appointmentDataListener.onAppointmentDataChangedListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public DatabaseReference getDbRef() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("groups");
        DatabaseReference groupRef = dbRef.child(parentRef.getGroupId());
        return groupRef.child("appointments").child(this.appointmentId);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.title);
        if(this.votingend == false){
            builder.append("   -- Abstimmung -- ");
        }
        return builder.toString();
    }

}
