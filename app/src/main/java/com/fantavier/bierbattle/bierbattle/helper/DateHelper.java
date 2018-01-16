package com.fantavier.bierbattle.bierbattle.helper;


import android.util.Log;

import com.fantavier.bierbattle.bierbattle.model.Appointment;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Paul on 09.01.2018.
 */

public class DateHelper {

    private static final String TAG = "DateHelper";

    public static Long convertDateToMilliSec(String date, String time) {
        String[] dateArray = date.split("\\.");
        String[] timeArray = time.split(":");

        Integer day = Integer.parseInt(dateArray[0]);
        Integer month = Integer.parseInt(dateArray[1]) - 1;
        Integer year = Integer.parseInt(dateArray[2]);
        Integer hour = Integer.parseInt((timeArray[0]));
        Integer minute = Integer.parseInt((timeArray[1]));

        SimpleDateFormat df = new SimpleDateFormat("hh:mm");
        Date d1 = null;
        try {
            d1 = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return calendar.getTimeInMillis();
    }

    public static HashMap<String, Long> getTimeLeft(long timeDiff){
        HashMap<String, Long> timeDiffArray = new HashMap<>();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        timeDiff = timeDiff % daysInMilli;

        Long hours = timeDiff / hoursInMilli;
        timeDiff = timeDiff % hoursInMilli;
        timeDiffArray.put("hours", hours);

        Long minutes = timeDiff / minutesInMilli;
        timeDiff = timeDiff % minutesInMilli;
        timeDiffArray.put("minutes", minutes);

        Long seconds = timeDiff / secondsInMilli;
        timeDiffArray.put("seconds", seconds);

        return timeDiffArray;
    }
}
