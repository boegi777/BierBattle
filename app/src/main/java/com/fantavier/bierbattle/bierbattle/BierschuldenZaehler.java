package com.fantavier.bierbattle.bierbattle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.fantavier.bierbattle.bierbattle.model.DataProvider;
import com.fantavier.bierbattle.bierbattle.ui.PopupListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mir on 14.12.2017.
 */
//Klasse für den Bierschuldenzähler
public class BierschuldenZaehler extends AppCompatActivity {
    public ListView max = null;
    public ListView min = null;

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

        max = (ListView) findViewById(R.id.plusview);
        min = (ListView) findViewById(R.id.minusview);

        max.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Snackbar popup = Snackbar.make(view, "Schuld begleichen?", 2000);
                popup.setAction("Begleichen", new PopupListener(i));
                popup.show();
                //MainActivity.dataProvider.getEarningUser(i);
            }
        });

    }

    public void onResume(){
        super.onResume();
        resumeViewData(this.max, this.min);
    }

    private void resumeViewData(final ListView max, final ListView min){
        MainActivity.dataProvider.getActiveUserBeerResults();
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
