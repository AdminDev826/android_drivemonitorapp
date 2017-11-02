package com.lineztech.farhan.vehicaltarckingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dashboard.Trackers;
import db.DatabaseHandler;
import driver_details.Driver;
import eco_driving.DrivingScore;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/26/2016.
 */
public class StartingActivity extends Activity {

    String userID, fullName, balance, hashCode;
    Context context;
    DatabaseHandler db;
    String assetsListURL, status, userInfoURL, connectionStatusURL, carInfoURL;
//    ProgressBar progressBar;
    double spent_cost = 0;
    List<Cars> supplierNames;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        hashCode = Utils.getPreferences("hashCode", context);
        db = new DatabaseHandler(context);
        db.deletConnectionStatus();
        assetsListURL = "https://api.navixy.com/v2/tracker/list?hash=" + hashCode;
        userInfoURL = "https://api.navixy.com/v2/user/get_info?hash=" + hashCode;
        carInfoURL = "https://api.navixy.com/v2/vehicle/list?hash=" + hashCode;
        setContentView(R.layout.starting_activity);
        Fabric.with(this, new Crashlytics());

//        ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
//        progressBar = new ProgressBar(this,null,android.R.attr.progressBarStyleLarge);
//        progressBar.setIndeterminate(true);
//        progressBar.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams params = new
//                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
//        RelativeLayout rl = new RelativeLayout(this);
//        rl.setGravity(Gravity.CENTER);
//        rl.addView(progressBar);
//        layout.addView(rl,params);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getAssetData();
    }
    private void startApp(){
//        progressBar.setVisibility(View.GONE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runApp();
            }
        }, 2000);
    }

    private void runApp() {
        String firstLogin = Utils.getPreferences("logged_first",context);
        Utils.savePreferences("alarm_playing", "NO", context);

        if(firstLogin.length() < 1){
            Utils.savePreferences("logged_first", "logged", context);
            for (int i = 0; i < supplierNames.size(); i++) {
                String trackerID = supplierNames.get(i).getTrackerID();
                Utils.savePreferences("auto-protect_" + trackerID, "ON", context);
                Utils.savePreferences("park-guard_" + trackerID, "OFF", context);
                Utils.savePreferences("park-shut_" + trackerID, "ON", context);
                Utils.savePreferences("park-audio_" + trackerID, "ON", context);
                Utils.savePreferences("park-delay_" + trackerID, "ON", context);
                Utils.savePreferences("park-imm_" + trackerID, "ON", context);

                Utils.savePreferences("playAlarm_ignition_" + trackerID, "ON", context);
                Utils.savePreferences("playAlarm_parking_" + trackerID, "ON", context);
                Utils.savePreferences("seek_seconds_" + trackerID, "15", context);
                Utils.savePreferences("battery_alarm_" + trackerID, "ON", context);
            }
            Utils.savePreferences("sNotification", "ON", context);
            Utils.savePreferences("idSVibration", "ON", context);
        }

        Intent intent = new Intent(context, UserInfo.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", (Serializable) supplierNames);
        intent.putExtra("BUNDLE", args);
        startActivity(intent);
        finish();
    }

    private void getAssetData(){
        supplierNames = new ArrayList<>();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, assetsListURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // the response is already constructed as a JSONObject!
                        try {
                            status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray jsonVashiaclLisst = response.getJSONArray("list");
                                for (int i = 0; i < jsonVashiaclLisst.length(); i++) {
                                    Cars cars = new Cars();
                                    Trackers trackers = new Trackers();
                                    JSONObject vahicalInfo = jsonVashiaclLisst.getJSONObject(i);
                                    String label = vahicalInfo.getString("label");
                                    String trackerID = vahicalInfo.getString("id");
                                    trackers.setTrackerID(trackerID);
                                    trackers.setTrackerLabel(label);
                                    cars.setCarName(label);
                                    cars.setTrackerID(trackerID);
                                    supplierNames.add(cars);
                                    db.addTrackers(trackers);
                                }
                                getTrackerStatus();
                            }else{
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }
    private void getTrackerStatus() {
        for (int i = 0; i < supplierNames.size(); i++) {
            final String trackerID = supplierNames.get(i).getTrackerID();
            connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackerID + "&hash=" + hashCode;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            // the response is already constructed as a JSONObject!
                            try {
                                status = response.getString("success");
                                if (status.equals("true")) {
                                    Driver driver = new Driver();
                                    JSONObject state = response.getJSONObject("state");
                                    String connection_status = state.getString("connection_status");
                                    String last_update = state.getString("last_update");

                                    JSONObject gps = state.getJSONObject("gps");
                                    JSONObject location = gps.getJSONObject("location");
                                    String lat = location.getString("lat");
                                    String lng = location.getString("lng");
                                    Utils.savePreferences(trackerID + "= lat", lat, context);
                                    Utils.savePreferences(trackerID + "= lng", lng, context);

                                    driver.setConnection(connection_status);
                                    driver.setCont_trackerID(trackerID);
                                    driver.setLast_update(last_update);
                                    db.addConnectonStatus(driver);
                                    if (finalI == supplierNames.size() - 1) {
                                        getUserData();
                                    }
                                } else {
                                    Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                Toast.makeText(context, "Server Error ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
//                            Toast.makeText(context, "Server Error ! \n" + error.getMessage() + "\n tracker id = " + trackerID, Toast.LENGTH_SHORT).show();
                            if (finalI == supplierNames.size() - 1) {
                                getUserData();
                            }
                        }
                    });
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
        }
    }
    private void getUserData(){
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, userInfoURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // the response is already constructed as a JSONObject!
                        try {
                            status = response.getString("success");
                            if(status.equals("true")){
                                JSONObject jsonUserInfo = response.getJSONObject("user_info");
                                userID = jsonUserInfo.getString("id");
                                String f_name = jsonUserInfo.getString("first_name");
                                String m_name = jsonUserInfo.getString("middle_name");
                                String l_name = jsonUserInfo.getString("last_name");
                                balance = jsonUserInfo.getString("balance");
                                fullName = f_name + " " + m_name + " " + l_name;
                                Utils.savePreferences("c_user_id", userID, context);
                                Utils.savePreferences("c_full_name", fullName, context);
                                Utils.savePreferences("c_balance", balance, context);

                                db.deletAllPlayers();
//                                startApp();
                                getReportID();
                            }else{
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void getTotalSpend(){
        String url = "https://api.navixy.com/v2/vehicle/service_task/list?hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray jsonArray = response.getJSONArray("list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    String cStatus = c.getString("status");
                                    String str_cost = c.getString("cost");
                                    double tmp = Double.parseDouble(str_cost);
                                    if(cStatus.equals("done")){
                                        spent_cost += tmp;
                                    }
                                }
                                Utils.savePreferences("total_spent", spent_cost+"", context);
                                getTrackerStatus();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }
    private void getReportID(){
        String loginURL = "", tID = "";
        tID = supplierNames.get(0).getTrackerID();
        for (int i = 1; i < supplierNames.size(); i++) {
            tID += "," + supplierNames.get(i).getTrackerID();
        }
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        String edate = curYear + "-" + curMonth + "-" + curDay + " 23:59:59";
        String sdate = curYear + "-" + curMonth + "-" + curDay + " 00:00:00";
        try {
            sdate = URLEncoder.encode(sdate, "utf-8");
            edate = URLEncoder.encode(edate, "utf-8");
            String geocoder = URLEncoder.encode("osm", "utf-8");
            loginURL = "https://api.navixy.com/v2/report/tracker/generate?hash=" + hashCode + "" +
                    "&from=" + sdate + "&to=" + edate + "&geocoder=" + geocoder + "&" +
                    "trackers=["+tID+"]&type=service&time_filter={%22from%22:%2200:00:00%22,%22to%22:%2223:59:59%22,%22weekdays%22:[1,2,3,4,5,6,7]}&plugin={%22plugin_id%22:46}";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, loginURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if(status.equals("true")){
                                String reportID = response.getString("id");
                                String reportURL = "https://api.navixy.com/v2/report/tracker/retrieve?hash=" + hashCode + "&report_id=" + reportID;
                                getReportData(reportURL);
                            }else{
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        startApp();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }
    private void getReportData(final String reportURL){
        final List<DrivingScore> datas = new ArrayList<>();
        for(int i=0;i<supplierNames.size();i++){
            DrivingScore d = new DrivingScore();
            d.setTrackerID(supplierNames.get(i).getTrackerID());
            datas.add(d);
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, reportURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if(status.equals("true")){
                                JSONObject report = response.getJSONObject("report");
                                JSONArray sheets = report.getJSONArray("sheets");
                                JSONObject summery = sheets.getJSONObject(0);
                                JSONArray sections = summery.getJSONArray("sections");
                                JSONObject drivingData = sections.getJSONObject(0);
                                JSONArray data = drivingData.getJSONArray("data");
                                for(int i = 0; i < supplierNames.size(); i++){
                                    JSONObject c = data.getJSONObject(i);
                                    JSONObject bars = c.getJSONObject("bars");
                                    JSONObject idling_data = bars.getJSONObject("idling");
                                    String idling = idling_data.getString("v");
//                                    JSONObject speeding_data = bars.getJSONObject("speeding");
//                                    String speeding = speeding_data.getString("v");
                                    JSONObject driving_data = bars.getJSONObject("harsh_driving");
                                    String driving = driving_data.getString("v");
//                                    datas.get(i).setSpeeding(speeding);
                                    datas.get(i).setIdling(idling);
                                    datas.get(i).setDriving(driving);
                                }
                                JSONObject drivingScore = sections.getJSONObject(1);
                                JSONArray bars = drivingScore.getJSONArray("bars");
                                for(int i = 0; i < supplierNames.size(); i++){
                                    JSONObject d = bars.getJSONObject(i);
                                    JSONObject score_data = d.getJSONObject("x");
                                    String score = score_data.getString("v");
                                    datas.get(i).setScore(score);
                                }
                                JSONObject speedingObject = sections.getJSONObject(2);
                                JSONArray a = speedingObject.getJSONArray("data");
                                JSONObject b = a.getJSONObject(0);
                                JSONArray speedingAry = b.getJSONArray("rows");
                                for(int i = 0; i < supplierNames.size(); i++){
                                    JSONObject d = speedingAry.getJSONObject(i);
                                    JSONObject score_data = d.getJSONObject("penalties_number");
                                    String score = score_data.getString("v");
                                    datas.get(i).setSpeeding(score);
                                }
                                updateReport(datas);
                            }else{
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        getReportData(reportURL);
//                        startApp();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void updateReport(List<DrivingScore> datas) {
        for(DrivingScore d: datas){
            double tmp = Double.parseDouble(d.getScore());
            int intScore = (int) Math.round(tmp);
            tmp = Double.parseDouble(d.getDriving());
            int intDriving = (int) Math.round(tmp);
            tmp = Double.parseDouble(d.getSpeeding());
            int intSpeeding = (int) Math.round(tmp);
            tmp = Double.parseDouble(d.getIdling());
            int intIdling = (int) Math.round(tmp);
            Utils.savePreferences(d.getTrackerID()+"_drive", intScore+", Harsh Driving: "+intDriving+", Speeding: "+intSpeeding+", Idle: "+intIdling, context);
        }
        startApp();
    }
}
