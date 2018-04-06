package com.lineztech.farhan.vehicaltarckingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import assing_task.RSSPullService;
import assing_task.SyncSmartDefenceData;
import dashboard.Trackers;
import db.DatabaseHandler;
import driver_details.Driver;
import eco_driving.DrivingScore;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/25/2016.
 */
public class LoginActivity extends Activity {
    TextView btnLoging;
    String loginURL;
    ProgressBar progressBar;
    EditText etUsername, etPassword;
    CheckBox checkBoxRemember;
    BroadcastReceiver receiver;
    String userID, fullName, balance, hashCode;
    Context context;
    DatabaseHandler db;
    String assetsListURL, status, userInfoURL, connectionStatusURL, carInfoURL;
    double spent_cost = 0;
    List<Cars> supplierNames;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setGravity(Gravity.CENTER);
        rl.addView(progressBar);
        layout.addView(rl, params);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.login);
//        setContentView(R.layout.login_new);
        context = this;

        etUsername = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);

//        etUsername.setText("clarke.daniel@outlook.com");
//        etPassword.setText("July@1983");

        btnLoging = (TextView) findViewById(R.id.btnLogin);
        checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
        Utils.savePreferences("checked", "unchecked", context);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(RSSPullService.COPA_MESSAGE);
            }
        };

        String isLogged = Utils.getPreferences("Logged", context);

        //check if updated and force user to login again.
        String updated = Utils.getPreferences("updated", context);

        if (isLogged.equals("Logged") && updated.equals("true")) {
            startApp();
        }

        btnLoging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "";
                hashCode = "";
                if (etUsername.getText().length() == 0 && etPassword.getText().length() == 0) {
                    Toast.makeText(context, "Please enter username and password", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    registerUser();
                }
            }
        });
        db = new DatabaseHandler(context);
        db.refreshTable();
        db.deletConnectionStatus();
    }

    private void loginToPortalServer() {
        String url = String.format("%sapp/login", AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.optBoolean("success")) {
                        String token = responseObject.optString("token");
                        Utils.savePreferences("portalToken", token, context);
                        startService(new Intent(context, SyncSmartDefenceData.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse != null) {
//                    Log.e("networkResponse", " " + new String(error.networkResponse.data));
//                }
//                loginToPortalServer();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("navixyUserName", Utils.getPreferences("userName", context));
                params.put("navixyPassword", Utils.getPreferences("password", context));
                return params;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "loginPortal");
    }

    private void registerUser() {
        loginURL = "https://api.navixy.com/v2/user/auth?login=" + etUsername.getText().toString() + "&password=" + etPassword.getText().toString();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, loginURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                hashCode = response.getString("hash");
                                if (checkBoxRemember.isChecked()) {
                                    Utils.savePreferences("Logged", "Logged", context);
                                    Utils.savePreferences("updated", "true", context);
                                }
                                Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show();
                                updatePreferences();
                                // login to portal server.
                                loginToPortalServer();
                                startApp();
                            } else {
                                Toast.makeText(context, "Login Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            Log.e("Error", " JSONException");
                            e.printStackTrace();
                            Toast.makeText(context, "Login Fail !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "  " + error);
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(context, "Login Fail !", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void updatePreferences() {
        Utils.savePreferences("userName", etUsername.getText().toString(), context);
        Utils.savePreferences("password", etPassword.getText().toString(), context);
        Utils.savePreferences("hashCode", hashCode, context);
    }

    private void startApp() {
//        DatabaseHandler db = new DatabaseHandler(context);
//        db.refreshTable();
//        db.close();
//        Intent intent = new Intent(context, StartingActivity.class);
//        startActivity(intent);
//        finish();
        assetsListURL = "https://api.navixy.com/v2/tracker/list?hash=" + hashCode;
        userInfoURL = "https://api.navixy.com/v2/user/get_info?hash=" + hashCode;
        carInfoURL = "https://api.navixy.com/v2/vehicle/list?hash=" + hashCode;
        getAssetData();

    }
    private void runApp() {
        progressBar.setVisibility(View.GONE);
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
                        runApp();
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
        runApp();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(RSSPullService.COPA_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
}
