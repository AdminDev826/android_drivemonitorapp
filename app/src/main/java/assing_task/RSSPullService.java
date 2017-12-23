package assing_task;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.EcallingActivity;
import com.lineztech.farhan.vehicaltarckingapp.ParkingGuardActivity;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.SmartDefenseActivity;
import com.lineztech.farhan.vehicaltarckingapp.StartingActivity;
import com.lineztech.farhan.vehicaltarckingapp.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import active_hour.ActiveHour;
import active_hour.Hours;
import dashboard.Trackers;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.ExclusionTimePeriod;
import util.Utils;

public class RSSPullService extends Service {

    private static final String TAG = "HelloService";
    LocalBroadcastManager broadcaster;
    Context context;
    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";
    private boolean isRunning = false;
    ArrayList<notification_alerts.list_notification.Notification> arrTrackingHistory;
    String hashCode;
    String urltrackingHistory;
    String current_time = "";
    String saved_time = "";
    String prev_status;
    int arrTrackingHistory_size = 0;
    public Dialog dialog;
    boolean isTimeIn = false;

    @Override
    public void onCreate() {
        Fabric.with(this, new Crashlytics());
        Log.i(TAG, "Service onCreate");
        context = this;
        isRunning = true;
        arrTrackingHistory = new ArrayList<notification_alerts.list_notification.Notification>();
        hashCode = Utils.getPreferences("hashCode", context);
        broadcaster = LocalBroadcastManager.getInstance(this);
        sendResult("servise message has been deleverd");
    }

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if (message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
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
                        Log.i(TAG, "Service running");
                        sendResult("servise message has been deleverd");
                        arrTrackingHistory.clear();
                        updateData();
                    }
                }
                Toast.makeText(context, "Service stated", Toast.LENGTH_LONG).show();
                stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.i(TAG, "Service onDestroy");
    }

    private void updateData() {
        urltrackingHistory = "https://api.navixy.com/v2/history/unread/list?limit=1&type=tracker&hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, urltrackingHistory, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                arrTrackingHistory_size = listtrackingHistory.length();
                                saved_time = Utils.getPreferences("saved_time", context);

                                for (int i = 0; i < listtrackingHistory.length(); i++) {
                                    JSONObject c = listtrackingHistory.getJSONObject(i);
                                    notification_alerts.list_notification.Notification track = new notification_alerts.list_notification.Notification();
                                    track.setId(c.getString("id"));
                                    track.setEvent(c.getString("event"));
                                    track.setMessage(c.getString("message"));
                                    track.setTime(c.getString("time"));
                                    track.setTracker_id(c.getString("tracker_id"));
                                    track.setAddress(c.getString("address"));
                                    current_time = track.getTime();
                                    arrTrackingHistory.add(track);
                                }
                                updateUI();
                            } else {
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("service ", " mai fail");
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

    @SuppressLint("SimpleDateFormat")
    private void updateUI() {
//        testDefense();
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        ExclusionTimePeriod isInTest = new ExclusionTimePeriod();
        if (arrTrackingHistory.size() > 0) {
            String tID = arrTrackingHistory.get(0).getTracker_id();
            if (!current_time.equals("") && !current_time.equals(saved_time)) {
                String isNotificationOn = Utils.getPreferences("sNotification", context);
                String park_guard = Utils.getPreferences("park-guard_" + tID, context);
                String auto_protect = Utils.getPreferences("auto-protect_" + tID, context);
                String strIDAlarm_ignition = Utils.getPreferences("playAlarm_ignition_" + tID, context);
                String strIDAlarm_parking = Utils.getPreferences("playAlarm_parking_" + tID, context);
                String playingAlarm = Utils.getPreferences("alarm_playing", context);
                String batteryAlarm = Utils.getPreferences("battery_alarm_" + tID, context);
                String message = arrTrackingHistory.get(0).getMessage();
                String address = arrTrackingHistory.get(0).getAddress();
                String strTime = arrTrackingHistory.get(0).getTime();
                Utils.savePreferences("saved_time", current_time, context);

                String[] parts = message.split(":");
                if (parts.length < 2) {
                    return;
                }
                String deviceName = parts[0];
                String state = parts[1];
                parts = address.split(", ");
                String road_name = parts[0];

                if (isNotificationOn.equals("ON")) {
                    NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent intent = new Intent(context, StartingActivity.class);
                    intent.putExtra("notify", "notify");
                    PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder.setSmallIcon(R.mipmap.ic_launcher_noti)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText("" + message + ",\n" + strTime)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("" + message + ",\n" + strTime))
//                            .setDefaults(Notification.DEFAULT_SOUND)
                                .setContentIntent(pending);
                    }
//                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                builder.setSound(alarmSound);
                    builder.setAutoCancel(true);
                    Notification notification = builder.getNotification();
                    notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
                    builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_LIGHTS;
//                notification.defaults |= Notification.DEFAULT_SOUND;
                    String idSVibration = Utils.getPreferences("idSVibration", context);
                    if (idSVibration.equals("ON")) {
                        notification.defaults |= Notification.DEFAULT_VIBRATE;
                    }
                    notif.notify(R.mipmap.ic_launcher_noti, notification);
                }

                Hours hours = null;
                String connectionStatus = null;

                try{
                    DatabaseHandler db = new DatabaseHandler(context);
                    connectionStatus = db.getStatus(tID);
                    @SuppressLint("SimpleDateFormat") DateFormat format2;
                    format2 = new SimpleDateFormat("EEEE");
                    String finalDay = format2.format(new Date());
                    hours = db.getSETime(tID, finalDay);
                    db.close();


                    if (arrTrackingHistory.get(0).getEvent().equals("crash_alarm")) {
                        if (Utils.getPreferences("ecalling_" + tID, context).equals("ON")) {
                            Intent intent = new Intent(context, EcallingActivity.class);
                            intent.putExtra("tracker_id", tID);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "E-calling is disabled", Toast.LENGTH_SHORT).show();
                        }
                    } else if (arrTrackingHistory.get(0).getEvent().equals("sos")) {
                        String antiHijacking = "";
                        try{
                            antiHijacking = Utils.getPreferences("anti-hijacking_" + tID, context);
                        }catch (Exception e){
                            Log.e("Util.getPreferences", "AntiHijacking Error:" + tID);
                        }

                        if (antiHijacking.equals("ON")) {
                            startService(new Intent(context, AntiHijackingCountDownTimer.class));
                            openActiveHour(tID);
                        } else {
                            Toast.makeText(context, "Anti Hi-jacking is disabled", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!playingAlarm.equals("YES") && connectionStatus.equals("active")) {
                        if (checkPowerCut(message) && batteryAlarm.equals("ON")) {
                            Utils.savePreferences("alarm_playing", "YES", context);
                            String vName = "External power cut on " + deviceName + " at " + road_name;
                            Intent intent1 = new Intent(context, SmartDefenseActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.putExtra("vName", vName);
                            intent1.putExtra("alarm_id", tID);
                            startActivity(intent1);
                        } else if (state.equals(" Parking start") || state.contains("OFF")) {
                            Utils.savePreferences("alarm_playing", "YES", context);
                            Intent intent1 = new Intent(context, ParkingGuardActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.putExtra("alarm_id", tID);
                            startActivity(intent1);
                        } else if (state.contains(" ON") && park_guard.equals("ON")) {
                            Utils.savePreferences("alarm_playing", "YES", context);
                            Intent intent1 = new Intent(context, ParkingGuardActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent1.putExtra("alarm_id", tID);
                            intent1.putExtra("Ignition", "Ignition Detected, Enable Engine?");
                            startActivity(intent1);
                        } else {
                            try {
                                String sTime = hours.getStart_time();
                                String eTime = hours.getEnd_time();
                                if (sTime != null) {
                                    if (sTime.contains("a.m.")) {
                                        sTime = sTime.replace("a.m.", "AM");
                                    } else if (sTime.contains("p.m.")) {
                                        sTime = sTime.replace("p.m.", "PM");
                                    }
                                    Date date = null;
                                    try {
                                        date = parseFormat.parse(sTime);
                                        isInTest.setTimeStart(displayFormat.format(date) + ":00");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (eTime != null) {
                                    if (eTime.contains("a.m.")) {
                                        eTime = eTime.replace("a.m.", "AM");
                                    } else if (eTime.contains("p.m.")) {
                                        eTime = eTime.replace("p.m.", "PM");
                                    }
                                    Date date = null;
                                    try {
                                        date = parseFormat.parse(eTime);
                                        isInTest.setTimeEnd(displayFormat.format(date) + ":00");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                isTimeIn = isInTest.isNowInPeriod();
                                if (isTimeIn == false) {
                                    String vName;
                                    if (state.equals(" Parking end") && strIDAlarm_parking.equals("ON")) {
                                        Utils.savePreferences("alarm_playing", "YES", context);
                                        vName = "Parking end Detected on " + deviceName + " at " + road_name;
                                        Intent intent1 = new Intent(context, SmartDefenseActivity.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent1.putExtra("vName", vName);
                                        intent1.putExtra("alarm_id", tID);
                                        startActivity(intent1);
                                    } else if (state.contains(" ON") && strIDAlarm_ignition.equals("ON")) {
                                        Utils.savePreferences("alarm_playing", "YES", context);
                                        vName = "Ignition Detected on " + deviceName + " at " + road_name;
                                        Intent intent1 = new Intent(getBaseContext(), SmartDefenseActivity.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent1.putExtra("vName", vName);
                                        intent1.putExtra("alarm_id", tID);
                                        startActivity(intent1);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("eee", "inside catch");
                                e.printStackTrace();
                            }
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                prev_status = state;
            }
        }
    }

    private boolean checkPowerCut(String message) {
        if (message.contains("power cut")) {
            return true;
        }
        return false;
    }

    public void openActiveHour(String tID) {
        int position = 0;
        Utils.savePreferences("TrackerID", tID, context);
        List<Trackers> trackerList;
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        trackerList = databaseHandler.getTrackerList();
        databaseHandler.close();
        for (int i = 0; i < trackerList.size(); i++) {
            if (trackerList.get(i).getTrackerID().equals(tID)) {
                position = i;
                break;
            }
        }

        String userID = Utils.getPreferences("c_user_id", context);
        String balance = Utils.getPreferences("c_balance", context);
        String userNameStr = Utils.getPreferences("c_full_name", context);
        Intent intent = new Intent(RSSPullService.this, UserInfo.class);
        intent.putExtra("userID", userID);
        intent.putExtra("userName", userNameStr);
        intent.putExtra("balance", balance);
        intent.putExtra("position", "" + position);
        Utils.savePreferences("Sos_pressed_" + tID, "true", context);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void testDefense() {
        String playingAlarm = Utils.getPreferences("alarm_playing", context);
        if (!playingAlarm.equals("YES")) {
            Utils.savePreferences("alarm_playing", "YES", context);
//            Intent i = new Intent("smart_defense");
//            i.putExtra("vName", "VNAME");
//            i.putExtra("alarm_id", "124189");
//            sendBroadcast(i);
            Intent intent1 = new Intent(context, ParkingGuardActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("vName", "VNAME");
            intent1.putExtra("alarm_id", "143985");
            startActivity(intent1);
        }
    }
}







