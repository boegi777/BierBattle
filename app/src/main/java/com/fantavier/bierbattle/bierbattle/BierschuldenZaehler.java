package com.fantavier.bierbattle.bierbattle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Mir on 14.12.2017.
 */

public class BierschuldenZaehler extends AppCompatActivity {
    ListView listView ;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bierschulden);
        Button buttonNew = (Button) findViewById(R.id.zurueck);
        ListView simpleList;
        ListView min;


        buttonNew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        listView = (ListView) findViewById(R.id.plusview);
        min = (ListView) findViewById(R.id.minusview);

        String[] plus = new String[] { "Paul   3", "Juri   1", "Peter   2"};

        String[] minus = new String[] { "Ursula   1", "Anja   5", "Frank   1", "Ulf   2"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, plus);

        ArrayAdapter<String> bdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, minus);

        listView.setAdapter(adapter);
        min.setAdapter(bdapter);
    }
}
