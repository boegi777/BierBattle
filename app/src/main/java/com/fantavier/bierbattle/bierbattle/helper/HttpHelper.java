package com.fantavier.bierbattle.bierbattle.helper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpHelper {

    private static final String TAG = "HttpHelper";

    public static void sendPost(final String urlString, final HashMap<String, Object> body){
        Iterator it = body.entrySet().iterator();
        String parametersString = "";
        int count = 0;
        while(it.hasNext()){
            count++;
            Map.Entry parameter = (Map.Entry) it.next();
            parametersString += parameter.getKey() + "=" + parameter.getValue();
            if(count < body.size()){
                parametersString += "&";
            }
        }
        final String parameters = parametersString;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!urlString.isEmpty()){
                        URL url = new URL(urlString);

                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");

                        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
                        httpRequestBodyWriter.write(parameters);
                        httpRequestBodyWriter.close();

                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        in.close();
                    }
                } catch(IOException ex){
                    Thread.interrupted();
                    Log.d(TAG, ex.getMessage());
                }
            }
        });
        thread.start();
    }
}
