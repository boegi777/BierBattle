package com.fantavier.bierbattle.bierbattle;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;


import com.fantavier.bierbattle.bierbattle.helper.ExceptionHelper;



//Klasse zur Lokalisierung
public class Location extends Service   {

    private LocationManager locationManager;
    private LocationListener listener;
    final double destLatitudeValue = 52.353413;
    final double destLongitudeValue = 9.724079;
    private double latitude;
    private double longitude;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission") //Berechtigung wurde in der Mainactivity.class abgefragt
    @Override
    public void onCreate() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        listener = new LocationListener() {

            //Wenn sich der Standort geändert hat wird die Distanz geprüft
            //Wenn diese kleiner als hundert Meter ist wird ein Punkt verteilt
            @Override
            public void onLocationChanged(android.location.Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (distance(destLatitudeValue, destLongitudeValue, latitude, longitude) < 0.1) {
                    try {
                            MainActivity.dataProvider.setPointForActiveUser(1);
                            Toast.makeText(getApplicationContext(), "Du hast ein Punkt erhalten", Toast.LENGTH_LONG).show();
                    } catch (ExceptionHelper.AppointmentStartsException e) {
                        e.printStackTrace();
                    } catch (ExceptionHelper.MemberNotFoundException e) {
                        e.printStackTrace();
                    }

                }

            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }

    //Berechtigung der Distanz
    private double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist;

    }
}






