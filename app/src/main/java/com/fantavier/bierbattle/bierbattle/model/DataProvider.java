package com.fantavier.bierbattle.bierbattle.model;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProvider {

    public static UserDataListener userListener = null;
    public static GroupDataListener groupListener = null;
    public static RankingDataListener rankingDataListener = null;
    public static AppointmentCreatedListener appointmentCreatedListener = null;
    public static MemberDataListener memberDataListener = null;
    public static AppointmentDataListener appointmentListener = null;
    public static AppointmentStartListener appointmentStartListener = null;
    public static AppointmentEndsListener appointmentEndsListener = null;
    public static VotingEndsListener votingEndsListener = null;

    private static final String TAG = "DataProvider";
    private static String groupId = "";
    private static Group group = null;
    private static User user = null;
    private static List<User> ranking = null;

    public DataProvider(){
        DataProvider.group = new Group();
        DataProvider.user = new User();
        DataProvider.ranking = new ArrayList<User>();
    }

    public interface DatabaseReferenceObject{
        DatabaseReference getDbRef();
        void loadObjectProperties(String id);
    }

    public interface UserDataListener {
        void onUserDataChanged();
    }

    public interface GroupDataListener {
        void onGroupeDataChanged();
    }

    public interface RankingDataListener{
        void onRankingDataListenerChanged();
    }

    public interface MemberDataListener {
        void onMemberDataChangedListener();
    }

    public interface AppointmentCreatedListener {
        void onAppointmentCreatedListener();
    }

    public interface AppointmentDataListener {
        void onAppointmentDataChangedListener();
    }

    public interface AppointmentStartListener{
        void onAppointmentStart(Appointment appointment);
    }

    public interface AppointmentEndsListener{
        void onAppointmentEnds(Appointment appointment);
    }

    public interface VotingEndsListener{
        void onVotingEnds(Appointment appointment);
    }

    public void setUserDataListener(UserDataListener listener) { userListener = listener; }
    public void setGroupDataListener(GroupDataListener listener){ groupListener = listener; }
    public void setRankingDataListener(RankingDataListener listener) { rankingDataListener = listener; }
    public void setMemberDataListener(MemberDataListener listener) { memberDataListener = listener; }
    public void setAppointmentCreatedListener(AppointmentCreatedListener listener) { appointmentCreatedListener = listener; }
    public void setAppointmentDataListener(AppointmentDataListener listener) { appointmentListener = listener; }
    public void setAppointmentStartListener(AppointmentStartListener listener) { appointmentStartListener = listener; }
    public void setAppointmentEndsListener(AppointmentEndsListener listener) { appointmentEndsListener = listener; }
    public void setVotingEndsListener(VotingEndsListener listener) { votingEndsListener = listener; }

    public static void createUser(Map<String, String> userData) {
        try {
            /* Daten überprüfen */
            DatabaseReference createUser = FirebaseDatabase.getInstance().getReference("users").child(userData.get("uid"));
            userData.remove("uid");
            createUser.setValue(userData);
        } catch(Exception ex){
            throw ex;
        }
    }

    public static Boolean isActiveUser(String uid){
        if(user.getUserId().equals((uid))){
            return true;
        } else {
            return false;
        }
    }

    public Group getActiveGroup(){
        return group;
    }

    public User getActiveUser(){
        return user;
    }

    public void loadData() {
        loadUserData();
        setActiveGroupId();
        loadRankingData();
    }

    public void setPointForActiveUser(int points)
            throws ExceptionHelper.AppointmentStartsException, ExceptionHelper.MemberNotFoundException{
        if(!getActiveGroup().checkAppointmentStarts()){
            throw new ExceptionHelper.AppointmentStartsException();
        }
        Member member = getActiveGroup().getMember(getActiveUser().getUserId());
        member.setPoints(points);

    }

    public String getUserrank() throws ExceptionHelper.MemberNotFoundException{
        return getActiveGroup().getRankOfMember(getActiveUser().getUserId()).toString();
    }

    public void checkAppointments(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String groupId = getActiveGroup().getGroupId();
                    URL url = new URL("https://us-central1-bierbattle.cloudfunctions.net/checkAppointments");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                    httpRequestBodyWriter.write("groupId="+groupId);
                    httpRequestBodyWriter.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                } catch(IOException ex){
                    Thread.interrupted();
                    Log.d(TAG, ex.getMessage());
                }
            }
        });
        thread.start();
    }
    private void loadUserData(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.loadObjectProperties(uid);
    }

    private void loadGroupData(){
        DataProvider.group.loadObjectProperties(groupId);
    }

    private void loadRankingData(){
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ranking.clear();
                for(DataSnapshot userDS : dataSnapshot.getChildren()){
                    User user = new User();
                    user.loadObjectProperties(userDS.getKey());
                    ranking.add(user);
                }
                watchRanking();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void watchRanking(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean running = true;
                    while(running){
                        if(checkRankingLoaded()){
                            if(DataProvider.rankingDataListener != null){
                                running = false;
                                DataProvider.rankingDataListener.onRankingDataListenerChanged();
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
        thread.setName("WatchRanking");
        thread.start();
    }

    private boolean checkRankingLoaded(){
        for(User user : ranking){
            if(!user.isLoaded()){
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> getRankingStrings(){
        ArrayList<String> userStrings = new ArrayList<String>();
        try {
            for (User user : ranking) {
                if(user.getActive())
                    userStrings.add(user.toString());
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
        return userStrings;
    }

    private void setActiveGroupId(){
        user.getDbRef().child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> groups = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                for (Map.Entry<String, HashMap<String, Object>> entry : groups.entrySet()) {
                    Boolean active = (Boolean) entry.getValue().get("active");
                    if (active == true) {
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
}