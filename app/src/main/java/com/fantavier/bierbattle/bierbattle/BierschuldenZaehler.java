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
    //String animalList[] = {"Lion","Tiger","Monkey","Elephant","Dog","Cat","Camel"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bierschulden);
        Button buttonNew = (Button) findViewById(R.id.zurueck);

        buttonNew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                /* Intent i = new Intent(BierschuldenZaehler.this, MainActivity.class);
                startActivity(i); */
                finish();
            }
        });

       // ListView simpleList = (ListView) findViewById(R.id.plusview);

        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.bierschulden, R.id.textView, animalList);
        //simpleList.setAdapter(arrayAdapter);

    }
}
