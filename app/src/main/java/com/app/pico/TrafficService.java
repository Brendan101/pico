package com.app.pico;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TrafficService extends IntentService {
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_FINISHED = 2;
    private static final int STATUS_ERROR = -1;
    private static final int SLEEP_TIME = 5;
    private static final int INTERVAL_IN_MILLIS = 1000 * 60 * 5;

    private final String API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private final String RETURN_TYPE = "json";
    private final String API_URL = API_BASE_URL + RETURN_TYPE;
    private final String MODE = "driving";
    private final String MODEL_TYPE = "pessimistic";

    ApplicationInfo ai;
    private String apiKey;

    AlarmManager alarmManager;
    DBOperationsClass db;

    public TrafficService(){
        super("TrafficService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ai = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            apiKey = ai.metaData.getString("com.google.android.geo.API_KEY");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        db = new DBOperationsClass(this);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");
        Bundle b = new Bundle();
        if(command.equals("query")) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            float startLat = db.getLocationByID(alarm.getStartLocationID()).getLatitude();
            float startLong = db.getLocationByID(alarm.getStartLocationID()).getLongitude();
            float endLat = db.getLocationByID(alarm.getEndLocationID()).getLatitude();
            float endLong = db.getLocationByID(alarm.getEndLocationID()).getLongitude();

            String[] tmp = getDuration(startLat, startLong, endLat, endLong).split(" ");
            int hourTime = 0;
            int minTime = 0;

            if (tmp.length > 2) {
                hourTime = Integer.parseInt(tmp[0]);
                minTime = Integer.parseInt(tmp[2]);
            } else {
                minTime = Integer.parseInt(tmp[0]);
            }

            Calendar initDepartTime = Calendar.getInstance();
            initDepartTime.add(Calendar.HOUR, -hourTime);
            initDepartTime.add(Calendar.MINUTE, -minTime);

            String[] trafficTime = getTraffic(initDepartTime, startLat, startLong, endLat, endLong).split(" ");

            int hourTraffic = 0;
            int minTraffic = 0;

            if (tmp.length > 2) {
                hourTraffic = Integer.parseInt(trafficTime[0]);
                minTraffic = Integer.parseInt(trafficTime[2]);
            } else {
                minTraffic = Integer.parseInt(trafficTime[0]);
            }

            initDepartTime.add(Calendar.HOUR, -(hourTraffic-hourTime));
            initDepartTime.add(Calendar.MINUTE, -(minTraffic-minTime));

            //TODO return initDepartTime
            b.putInt("alarmID", alarm.getID());
            b.putSerializable("departTime", initDepartTime);
            receiver.send(STATUS_FINISHED, b);

            /*Calendar currTime = Calendar.getInstance();
            long diff = currTime.getTimeInMillis() - initDepartTime.getTimeInMillis();


            if (diff > INTERVAL_IN_MILLIS){
                // we're done, sound the alarm
            } else {
                // set an alarm for INTERVAL_IN_MILLIS later to recheck
            }*/

        }
    }

    private String getDuration(float startLat, float startLong, float endLat, float endLong){
        String requestURL = API_URL + "?" +
                "origin=" + startLat + "," + startLong + "&" +
                "destination=" + endLat + "," + endLong + "&" +
                "mode=" + MODE + "&" +
                "key=" + apiKey;

        HttpURLConnection conn = null;
        String response = "";
        String duration = "";

        try {
            URL url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
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

            duration = leg.getJSONObject("duration").getString("text");

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(conn != null)
            {
                conn.disconnect();
            }
        }

        return duration;
    }

    private String getTraffic(Calendar departTime, float startLat, float startLong, float endLat, float endLong){

        String requestURL = API_URL + "?" +
                "origin=" + startLat + "," + startLong + "&" +
                "destination=" + endLat + "," + endLong + "&" +
                "departure_time=" + departTime.getTimeInMillis() + "&" +
                "mode=" + MODE + "&" +
                "traffic_model=" + MODEL_TYPE + "&" +
                "key=" + apiKey;


        HttpURLConnection conn = null;
        String response = "";
        String duration = "";

        try {
            URL url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
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

            duration = leg.getJSONObject("duration_in_traffic").getString("text");

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(conn != null)
            {
                conn.disconnect();
            }
        }
        return duration;

    }
}
