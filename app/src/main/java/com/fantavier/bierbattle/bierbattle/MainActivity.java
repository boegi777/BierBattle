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

import com.fantavier.bierbattle.bierbattle.model.Group;
import com.fantavier.bierbattle.bierbattle.model.GroupProvider;
import com.fantavier.bierbattle.bierbattle.model.UserProvider;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public static String activeGroupId;
    public static UserProvider userProvider = null;
    public static GroupProvider groupProvider = null;
    public static Group activeGroup;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(3);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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
            // Show 3 total pages.
            return 4;
        }
    }

    public void setGroupListener(){
        try {
            if (MainActivity.userProvider == null) {
                MainActivity.userProvider = new UserProvider();
            }
            MainActivity.userProvider.setActiveGroupListener(new UserProvider.ActiveGroupListener() {
                @Override
                public void onActiveGroupChanged(String groupId) {
                    Log.d(TAG, groupId);
                    MainActivity.activeGroupId = groupId;
                    if (MainActivity.groupProvider == null) {
                        MainActivity.groupProvider = new GroupProvider(MainActivity.activeGroupId);
                    }
                    MainActivity.groupProvider.setGroupDataListener(new GroupProvider.GroupDataListener() {
                        @Override
                        public void onGroupeDataChanged(Group group) {
                            activeGroup = group;

                            List<String> appointments = activeGroup.getAppointmentTitles();
                            ArrayAdapter<String> appointmentAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, appointments);
                            TermineTab.appointmentList.setAdapter(appointmentAdapter);

                            MainActivity.groupProvider.setMemberTitleListener(new GroupProvider.MemberTitleListener() {
                                @Override
                                public void onMemberTitleChangedListener() {
                                    List<String> members = activeGroup.getMemberTitles();
                                    ArrayAdapter<String> groupMemberAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, members);
                                    GruppeTab.groupList.setAdapter(groupMemberAdapter);
                                }
                            });
                        }
                    });
                }
            });
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy(){
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onDestroy();
    }
}
