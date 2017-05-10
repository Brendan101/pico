package com.app.pico;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TrafficService extends IntentService {
    private final String API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private final String RETURN_TYPE = "json";
    private final String API_URL = API_BASE_URL + RETURN_TYPE;
    private final String MODE = "driving";
    private final int INITIAL_WAKE_THRESHOLD_MILLIS = 1000 * 60 * 30;
    private final int CHECK_INTERVAL = 1000 * 60 * 5;

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
        String command = intent.getStringExtra("command");;
        if(command.equals("initial")) {
            Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");
            setAlarm("pessimistic", alarm, command);

        } else if (command.equals("check")){
            Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");
            setAlarm("best_guess", alarm, command);

        } else if (command.equals("cancel")){
            Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");
            Intent startAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
            startAlarm.putExtra("alarm", alarm);
            startAlarm.putExtra("command", "alarm");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getID(), startAlarm, 0);
            alarmManager.cancel(alarmIntent);
        }
    }


    private void setAlarm(String model_type, Alarm alarm, String wake_type){
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
        initDepartTime.set(Calendar.SECOND, 0);

        // we have to parse string data into a calendar object because we don't store MM/dd/yyyy
        // which will default to unix epoch
        String tmpArrivalTime = alarm.getArrivalTimeAsString();
        String ampm = tmpArrivalTime.split(" ")[1];
        String[] rawArrivalTime = tmpArrivalTime.split(" ")[0].split(":");

        Calendar arrivalTime = Calendar.getInstance();
        arrivalTime.set(Calendar.HOUR, Integer.parseInt(rawArrivalTime[0]));
        arrivalTime.set(Calendar.MINUTE, Integer.parseInt(rawArrivalTime[1]));
        arrivalTime.set(Calendar.SECOND, 0);

        if (ampm.equalsIgnoreCase("am")) {
            arrivalTime.set(Calendar.AM_PM, Calendar.AM);
        } else {
            arrivalTime.set(Calendar.AM_PM, Calendar.PM);
        }

        // if current time is already past the arrival time, set alarm for tomorrow (only on initial creation)
        // TODO handle edge case where the user needed to depart in the past hit their desired arrival time in the future
        if (arrivalTime.getTimeInMillis() < initDepartTime.getTimeInMillis() && wake_type.equals("initial")){
            arrivalTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        initDepartTime = (Calendar) arrivalTime.clone();

        initDepartTime.add(Calendar.HOUR, -hourTime);
        initDepartTime.add(Calendar.MINUTE, -minTime);

        String[] trafficTime = getTraffic(initDepartTime, startLat, startLong, endLat, endLong, model_type).split(" ");

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

        int prepHour = alarm.getPrepTime()/60;
        int prepMin = alarm.getPrepTime()%60;

        initDepartTime.add(Calendar.HOUR, -prepHour);
        initDepartTime.add(Calendar.MINUTE, -prepMin);

        // check if we should alert the user to leave
        if (initDepartTime.getTimeInMillis() + CHECK_INTERVAL >= Calendar.getInstance().getTimeInMillis()){
            //set the alarm off
            Intent startAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
            startAlarm.putExtra("alarm", alarm);
            startAlarm.putExtra("command", "alarm");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getID(), startAlarm, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmIntent);

        } else {
            //TODO double check this. Above sets off the alarm. This part rechecks traffic until
            //alarm should go off, so this part should call this traffic service after some amount
            //of time again, right? RecheckTrafficReceiver is just renamed TempAlarmReceiver
            Intent setAlarm = new Intent(getApplicationContext(), RecheckTrafficReceiver.class);
            setAlarm.putExtra("alarm", alarm);
            setAlarm.putExtra("command", "alarm");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getID(), setAlarm, 0);

            if (wake_type.equals("initial")) {
                // wake up 30 minutes before worst case traffic scenario (+prep time) as predicted by Google
                alarmManager.set(AlarmManager.RTC_WAKEUP, initDepartTime.getTimeInMillis() + INITIAL_WAKE_THRESHOLD_MILLIS, alarmIntent);
                Log.i("ALARMSET", initDepartTime.getTime().toString());
            } else {
                // wake up 5 minutes later to check traffic again
                alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + CHECK_INTERVAL, alarmIntent);
            }
        }


    }

    /* CODE FOR TESTING THE ALARM
    private void setAlarm(String model_type, Alarm alarm, String wake_type){
        Intent startAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
        startAlarm.putExtra("alarm", alarm);
        startAlarm.putExtra("command", "alarm");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getID(), startAlarm, 0);
        //alarm pops up after 10 seconds
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, alarmIntent);
    }*/

    private String getDuration(float startLat, float startLong, float endLat, float endLong){
        String requestURL = API_URL + "?" +
                "origin=" + startLat + "," + startLong + "&" +
                "destination=" + endLat + "," + endLong + "&" +
                "mode=" + MODE + "&" +
                "key=" + apiKey;

        Log.i("REQUEST", requestURL);
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

    private String getTraffic(Calendar departTime, float startLat, float startLong, float endLat, float endLong, String model_type){

        String requestURL = API_URL + "?" +
                "origin=" + startLat + "," + startLong + "&" +
                "destination=" + endLat + "," + endLong + "&" +
                "departure_time=" + departTime.getTimeInMillis() + "&" +
                "mode=" + MODE + "&" +
                "traffic_model=" + model_type + "&" +
                "key=" + apiKey;

        Log.i("REQUEST", requestURL);

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
