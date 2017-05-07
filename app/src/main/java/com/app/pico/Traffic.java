package com.app.pico;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by rlou on 5/7/17.
 */

public class Traffic {

    private final String API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private final String RETURN_TYPE = "json";
    private final String API_URL = API_BASE_URL + RETURN_TYPE;
    private final String MODE = "driving";
    private final String MODEL_TYPE = "pessimistic";

    private float startLat;
    private float startLong;
    private float endLat;
    private float endLong;
    //private Calendar departTime;
    private String departTime;
    private String duration_in_traffic;

    ApplicationInfo ai;

    private String apiKey;


    public Traffic(float startLat, float startLong, float endLat, float endLong, String departTime, Context context){
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.departTime = departTime;

        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            apiKey = ai.metaData.getString("com.google.android.geo.API_KEY");
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void getTraffic(){
        new RequestTask().execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            try {
                cal.setTime(dateFormat.parse(departTime));
            } catch (Exception ex){
                ex.printStackTrace();
            }

            String requestURL = API_URL + "?" +
                    "origin=" + startLat + "," + startLong + "&" +
                    "destination=" + endLat + "," + endLong + "&" +
                    "departure_time=" + cal.getTimeInMillis() + "&" +
                    "mode=" + MODE + "&" +
                    "traffic_model=" + MODEL_TYPE + "&" +
                    "key=" + apiKey;

            HttpURLConnection conn = null;
            String response = "";

            try {
                URL url = new URL(requestURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                String strLine = null;
                while ((strLine = reader.readLine()) != null)
                {
                    response+=strLine;
                }
                reader.close();

                JSONObject responseJson = new JSONObject(response);

                JSONArray routesArray = responseJson.getJSONArray("routes");
                JSONObject route = routesArray.getJSONObject(0);

                JSONArray legs = route.getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);

                JSONObject traffic = leg.getJSONObject("duration_in_traffic");
                duration_in_traffic = traffic.getString("text");


            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                if(conn != null)
                {
                    conn.disconnect();
                }
            }
            return duration_in_traffic;
        }


    }
}
