package com.fantavier.bierbattle.bierbattle.model;

import android.util.Log;

import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;
import com.fantavier.bierbattle.bierbattle.helper.HttpHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    public static UsersBeercountLoadedListener usersBeercountLoadedListener = null;
    public static RoundEndingListener roundEndListener = null;


    private static final String TAG = "DataProvider";
    private static String groupId = "";
    private static Group group = null;
    private static User user = null;
    private static List<User> earningUsers = null;
    private static List<User> ranking = null;


    public DataProvider(){
        DataProvider.group = new Group();
        DataProvider.user = new User();
        DataProvider.ranking = new ArrayList<User>();
    }

    public interface DatabaseReferenceObject{
        DatabaseReference getDbRef();
        void loadObjectProperties(String id);
        void setPropertiesLoaded(PropertiesLoaded listener);
    }

    public interface PropertiesLoaded {
        void onPropertiesLoaded();
    }

    public interface UserDataListener {
        void onUserDataChanged();
    }

    public interface GroupDataListener {
        void onGroupeDataChanged();
    }

    public interface RoundEndingListener {
        void onRoundEnd();
    }

    public interface RankingDataListener{
        void onRankingDataListenerChanged();
    }

    public interface MemberDataListener {
        void onMemberDataChangedListener();
    }

    public interface AppointmentCreatedListener {
        void onAppointmentCreatedListener(Appointment appointment);
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

    public interface UsersBeercountLoadedListener{
        void onUsersBeercountLoaded(HashMap<String, Integer> userData, Boolean debts);
    }

    public void setUserDataListener(UserDataListener listener) { userListener = listener; }
    public void setGroupDataListener(GroupDataListener listener){ groupListener = listener; }
    public void setRoundEndListener(RoundEndingListener listener){ roundEndListener = listener; }
    public void setRankingDataListener(RankingDataListener listener) { rankingDataListener = listener; }
    public void setMemberDataListener(MemberDataListener listener) { memberDataListener = listener; }
    public void setAppointmentCreatedListener(AppointmentCreatedListener listener) { appointmentCreatedListener = listener; }
    public void setAppointmentDataListener(AppointmentDataListener listener) { appointmentListener = listener; }
    public void setAppointmentStartListener(AppointmentStartListener listener) { appointmentStartListener = listener; }
    public void setAppointmentEndsListener(AppointmentEndsListener listener) { appointmentEndsListener = listener; }
    public void setVotingEndsListener(VotingEndsListener listener) { votingEndsListener = listener; }
    public void setUsersBeercountLoadedListener(UsersBeercountLoadedListener listener) { usersBeercountLoadedListener = listener; }

    public static void createUser(Map<String, String> userData) {
        try {
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

    public void getActiveUserBeerResults(){
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final HashMap<String, Integer> debts = getActiveUser().getDepts();
                    final HashMap<String, Integer> debtsWithNames = new HashMap<>();

                    final HashMap<String, Integer> earnings = getActiveUser().getEarnings();
                    final HashMap<String, Integer> earningsWithNames = new HashMap<>();

                    earningUsers = new ArrayList<>();

                    Iterator itDebts = debts.entrySet().iterator();
                    while(itDebts.hasNext()){
                        Map.Entry debt = (Map.Entry) itDebts.next();
                        final Integer value = Integer.parseInt(debt.getValue().toString());
                        final User user = new User();
                        user.loadObjectProperties(debt.getKey().toString());
                        user.setPropertiesLoaded(new PropertiesLoaded() {
                            @Override
                            public void onPropertiesLoaded() {
                                debtsWithNames.put(user.getUsername(), value);
                                if(debtsWithNames.size() == debts.size()){
                                    DataProvider.usersBeercountLoadedListener.onUsersBeercountLoaded(debtsWithNames, true);
                                }
                            }
                        });
                    }
                    if(earnings == null){
                        throw new ExceptionHelper.BeerCounterException();
                    }
                    Iterator itEarnings = earnings.entrySet().iterator();
                    while(itEarnings.hasNext()){
                        Map.Entry entry = (Map.Entry) itEarnings.next();
                        final Integer value = Integer.parseInt(entry.getValue().toString());
                        final User user = new User();
                        user.loadObjectProperties(entry.getKey().toString());
                        user.setPropertiesLoaded(new PropertiesLoaded() {
                            @Override
                            public void onPropertiesLoaded() {
                                earningsWithNames.put(user.getUsername(), value);
                                if(earningsWithNames.size() == earnings.size()){
                                    DataProvider.usersBeercountLoadedListener.onUsersBeercountLoaded(earningsWithNames, false);
                                }
                            }
                        });
                        DataProvider.earningUsers.add(user);
                    }
                } catch(ExceptionHelper.BeerCounterException ex){
                    Log.d(TAG, ex.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    public void setPayedDebtsForUser(Integer i){
        User user = earningUsers.get(i);
        user.removeDebt(getActiveUser().getUserId());
        getActiveUser().removeEarning(user.getUserId());
    }

    public void finishRound(){
        HashMap<String, Object> body = new HashMap<>();
        body.put("groupId", getActiveGroup().getGroupId());
        String url = "https://us-central1-bierbattle.cloudfunctions.net/roundEnds";
        HttpHelper.sendPost(url, body);
    }

    public String getUserrank() throws ExceptionHelper.MemberNotFoundException{
        return getActiveGroup().getRankOfMember(getActiveUser().getUserId()).toString();
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