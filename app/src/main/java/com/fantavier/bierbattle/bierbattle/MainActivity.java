package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.fantavier.bierbattle.bierbattle.model.DataProvider;
import com.fantavier.bierbattle.bierbattle.model.Group;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DataProvider dataProvider = null;
    public static Group activeGroup;
    public static Location location;

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = new Location();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(4);

        setListener();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0:
                    MenueTab main = new MenueTab();
                    return main;
                case 1:
                    GruppeTab gruppe = new GruppeTab();
                    return gruppe;
                case 2:
                    TermineTab termine = new TermineTab();
                    return termine;
                case 3:
                    TeilnehmerTab teilnehmer = new TeilnehmerTab();
                    return teilnehmer;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    public void setListener(){
        try {
            if (MainActivity.dataProvider == null) {
                MainActivity.dataProvider = new DataProvider();
                MainActivity.dataProvider.init();
            }

            setUsernameListener();
            setGroupDataListener();

        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void setUsernameListener(){
        MainActivity.dataProvider.setUsernameListener(new DataProvider.UsernameListener(){
            @Override
            public void onUsernameChanged(String username) {
                MenueTab.username.setText(username);
            }
        });
    }

    private void setGroupDataListener(){
        MainActivity.dataProvider.setGroupDataListener(new DataProvider.GroupDataListener() {
            @Override
            public void onGroupeDataChanged(Group group) {
                activeGroup = group;

                MainActivity.dataProvider.setAppointmentDataListener(new DataProvider.AppointmentDataListener() {
                    @Override
                    public void onAppointmentDataChangedListener() {
                        List<String> appointments = activeGroup.getAppointmentTitles();
                        ArrayAdapter<String> appointmentAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, appointments);
                        TermineTab.appointmentList.setAdapter(appointmentAdapter);
                    }
                });

                MainActivity.dataProvider.setMemberDataListener(new DataProvider.MemberDataListener() {
                    @Override
                    public void onMemberDataChangedListener() {
                        List<String> members = activeGroup.getMemberTitles();
                        ArrayAdapter<String> groupMemberAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, members);
                        GruppeTab.groupList.setAdapter(groupMemberAdapter);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy(){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onDestroy();
    }

}



