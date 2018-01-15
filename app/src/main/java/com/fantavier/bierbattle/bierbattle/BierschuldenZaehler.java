package com.fantavier.bierbattle.bierbattle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fantavier.bierbattle.bierbattle.model.DataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mir on 14.12.2017.
 */

public class BierschuldenZaehler extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bierschulden);
        Button buttonNew = (Button) findViewById(R.id.zurueck);
        ListView simpleList;

        buttonNew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MainActivity.dataProvider.getActiveUserBeerResults();

        final ListView max = (ListView) findViewById(R.id.plusview);
        final ListView min = (ListView) findViewById(R.id.minusview);

        //final String[] plus = new String[];

        MainActivity.dataProvider.setUsersBeercountLoadedListener(new DataProvider.UsersBeercountLoadedListener() {
            @Override
            public void onUsersBeercountLoaded(final HashMap<String, Integer> userData, final Boolean debts) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(debts == true){
                            final List<String> minus = new ArrayList<>();
                            Iterator it = userData.entrySet().iterator();
                            while(it.hasNext()){
                                Map.Entry entry = (Map.Entry) it.next();
                                String item = entry.getKey() + " " + entry.getValue().toString();
                                minus.add(item);
                            }
                            ArrayAdapter<String> bdapter = new ArrayAdapter<String>(BierschuldenZaehler.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, minus);

                            min.setAdapter(bdapter);
                        } else {
                            final List<String> plus = new ArrayList<>();
                            Iterator it = userData.entrySet().iterator();
                            while(it.hasNext()){
                                Map.Entry entry = (Map.Entry) it.next();
                                String item = entry.getKey() + " " + entry.getValue().toString();
                                plus.add(item);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(BierschuldenZaehler.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, plus);

                            max.setAdapter(adapter);
                        }
                    }
                });
            }
        });
    }
}
