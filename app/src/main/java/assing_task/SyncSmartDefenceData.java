package assing_task;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import active_hour.Hours;
import db.DatabaseHandler;
import notification_alerts.list_notification.Notification;
import util.AppSingleton;
import util.Utils;

/**
 * Created by jft on 10/8/17.
 */

public class SyncSmartDefenceData extends Service {

    private static final String TAG = "HelloService";
    LocalBroadcastManager broadcaster;
    Context context;
    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";
    private boolean isRunning = false;
    ArrayList<Notification> arrTrackingHistory;
    String hashCode;
    String url;
    String current_time = "";
    String saved_time = "";
    String prev_status;
    int arrTrackingHistory_size = 0;
    public Dialog dialog;
    boolean isTimeIn = false;
    String trackerID, token;

    DateFormat requiredTimeFormat = new SimpleDateFormat("HH:mm");
    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        context = this;
        isRunning = true;
        hashCode = Utils.getPreferences("hashCode", context);
        token = Utils.getPreferences("portalToken", context);
        broadcaster = LocalBroadcastManager.getInstance(this);
//        sendResult("servise message has been deleverd");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i > -1; i++) {
                    try {
                        Thread.sleep(8000);
                    } catch (Exception e) {
                    }
                    if (isRunning) {
                        Map<String, String> params = new HashMap<>();
                        trackerID = Utils.getPreferences("TrackerID", context);
                        params.put("trackerId", trackerID);
                        Log.e("tracker id", " " + params.get("trackerId"));
////                        Log.i(TAG, "Service running");
////                        sendResult("service message has been deleverd");
////                        arrTrackingHistory.clear();
                        getUpdatedData(params);
                    }
                }
                Toast.makeText(context, "Service stated", Toast.LENGTH_LONG).show();
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.i(TAG, "Service onDestroy");
    }

    private void getUpdatedData(final Map<String, String> params) {
        String url = String.format("%sevent/getSmartDefenceDataForMobile", AppSingleton.BASE_PORTAL_URL);
        StringRequest jsonRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("getUpdatedData", " " + response);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    String status = responseObject.optString("isSmartDefenceAvailable");
                    if (status.equals("true")) {
                        JSONObject smartDefenceObject = responseObject.optJSONObject("smartDefence");
                        boolean isParkGuardEanbled = smartDefenceObject.optBoolean("parkGuardEnable");
                        boolean isAutoProtectEnable = smartDefenceObject.optBoolean("autoProtectEnable");
                        boolean enableAudio = smartDefenceObject.optBoolean("enableAudio");
                        int trackerId = smartDefenceObject.optInt("trackerId");
                        JSONArray activeHourWSArray = smartDefenceObject.optJSONArray("activeHourWS");
                        String antiHiJackingCountDownTime = String.valueOf(smartDefenceObject.optInt("antiHiJackingCountDownTime"));
                        boolean isAntiHiJackingEnable = smartDefenceObject.optBoolean("antiHiJackingEnable");
                        updateData(isParkGuardEanbled, isAutoProtectEnable, enableAudio, trackerId, activeHourWSArray, antiHiJackingCountDownTime, isAntiHiJackingEnable);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 401){
                        loginToPortalServer();
                    }
                    Log.e("eeee", "eeeeeee" + new String(error.networkResponse.data));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "getSmartDefenceDataFromPortal");
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginToPortalServer();
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

    private void updateData(boolean isParkGuardEnable, boolean isAutoProtectEnable, boolean enableAudio, int trackerId, JSONArray activeHourWSArray, String antiHiJackingCountDownTime, boolean isAntiHiJackingEnable) {
        if (isAutoProtectEnable) {
            Utils.savePreferences("auto-protect_" + trackerId, "ON", context);
        } else {
            Utils.savePreferences("auto-protect_" + trackerId, "OFF", context);
        }

        if (isParkGuardEnable) {
            Utils.savePreferences("park-guard_" + trackerId, "ON", context);
        } else {
            Utils.savePreferences("park-guard_" + trackerId, "OFF", context);
        }

        if (enableAudio) {
            Utils.savePreferences("park-audio_" + trackerId, "ON", context);
        } else {
            Utils.savePreferences("park-audio_" + trackerId, "OFF", context);
        }
        if (isAntiHiJackingEnable) {
            Utils.savePreferences("anti-hijacking_" + trackerId, "ON", context);
        } else {
            Utils.savePreferences("anti-hijacking_" + trackerId, "OFF", context);
        }

        if (antiHiJackingCountDownTime.equals("")) {
            Utils.savePreferences("anti-hijacking_countdown_time_" + trackerId, "1", context);
        } else {
            Utils.savePreferences("anti-hijacking_countdown_time_" + trackerId, antiHiJackingCountDownTime, context);
        }

        DatabaseHandler db = new DatabaseHandler(context);

        ArrayList<Hours> listtrackingHistory = (ArrayList<Hours>) db.getActiveHoursList();
        for (int i = 0; i < listtrackingHistory.size(); i++) {
            Hours hours = new Hours();
            hours.setTrackerID(String.valueOf(trackerId));
            hours.setDay(listtrackingHistory.get(i).getDay());
            deleteRow(hours);
        }


        for (int i = 0; i < activeHourWSArray.length(); i++) {
            JSONObject activeHourWSObject = activeHourWSArray.optJSONObject(i);
            int day = activeHourWSObject.optInt("day");
            String startTime = activeHourWSObject.optString("startTime");
            String strDay = convertIntegerDayToStringDay(day);
            String endTime = activeHourWSObject.optString("endTime");
            try {
                endTime = requiredTimeFormat.format(timeFormat.parse(endTime));
                startTime = requiredTimeFormat.format(timeFormat.parse(startTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Hours hours = new Hours();
            hours.setStart_time(startTime);
            hours.setEnd_time(endTime);
            hours.setTrackerID(String.valueOf(trackerId));

            String ifExist = db.getCountDays(String.valueOf(trackerId), strDay);
            if (Integer.parseInt(ifExist) == 0) {
                hours.setDay(strDay);
                db.addActiveHours(hours);
            } else {
                db.updateExistingTime(strDay, startTime, endTime, String.valueOf(trackerId));
            }
        }
//        sendBroadcast(new Intent("smartDefenceDataSyncReceiver"));
    }

    private void deleteRow(Hours hours) {
        DatabaseHandler db = new DatabaseHandler(context);
        String tracker_id = db.getTrackerID(hours.getTrackerID());
        db.removeExistingTime(hours.getDay(), hours.getTrackerID());
        db.close();
    }

    private String convertIntegerDayToStringDay(int day) {
        switch (day) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }
        return "Sunday";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
