package dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.Geofence;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import active_hour.ActiveHour;
import db.DatabaseHandler;
import driver_details.Driver;

import geofence_21.MapGeoFence;
import io.fabric.sdk.android.Fabric;
import maps.MapsMain;
import sos.GMailSender;
import sos.Sos;
import sos.SosInput;
import tracking_history.Track;
import tracking_history.TrackingHistory;
import util.AppSingleton;
import util.Utils;


@SuppressLint("ValidFragment")
public class DashboardFragment extends Fragment {
    View v;
    boolean _isVisible = true;
    Thread thread;
    String trackerID;
    Context context;
    String movement_status;
    LinearLayout idtvMapView;
    LinearLayout idlnObd2;
    Switch idtvStartEngine;
    String cLocationURL, lastUpdateURL;
    ProgressDialog progressDialog;
    Dialog dialog;
    TextView idtvAddess, idtvSpeed, idtvLastUpdated, idtvMovementStatus, one_txt_driven;
    String status, lat, lon, speed, address;
    LinearLayout idtvHistory, idtvAssigndTask, idtvEchoDriving, idtvGeoFence;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    GMailSender sender;
    String connectionStatusURL;
    String connection_status;
    String description;
    String hashCode;
    private List<Boolean> jArrOutPutsEngineON = new ArrayList<Boolean>();
    private List<Boolean> jArrOutPuts = new ArrayList<Boolean>();
    private List<String> jPuttsOnPosition = new ArrayList<String>();
    private List<String> jPuttss = new ArrayList<String>();
    private List<String> jPuttssPositionOFF = new ArrayList<String>();

    @SuppressLint("ValidFragment")
    public DashboardFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) return v;
        v = inflater.inflate(R.layout.fragment_one, container, false);
        init();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        _isVisible = true;
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        _isVisible = false;
    }

    private void initData() {
        try {
            DatabaseHandler db = new DatabaseHandler(context);
            String lastUpdate = db.getLastUpdate(trackerID);
            db.close();
            idtvLastUpdated.setText("Last Updated: " + lastUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLastTrip();
        updateData();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (_isVisible) {
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateData();
                }
            }
        });
        thread.start();
    }

    private void init() {
        context = getActivity();
        Fabric.with(context, new Crashlytics());

        idtvAddess = (TextView) v.findViewById(R.id.idtvAddess);
        idtvMapView = (LinearLayout) v.findViewById(R.id.idtvMapView);
        idlnObd2 = (LinearLayout) v.findViewById(R.id.idlnObd2);
        idtvMovementStatus = (TextView) v.findViewById(R.id.idtvMovementStatus);
        idtvLastUpdated = (TextView) v.findViewById(R.id.idtvLastUpdated);
        idtvHistory = (LinearLayout) v.findViewById(R.id.idtvHistory);
        idtvEchoDriving = (LinearLayout) v.findViewById(R.id.idtvEchoDriving);
        idtvAssigndTask = (LinearLayout) v.findViewById(R.id.idtvAssigndTask);
        idtvGeoFence = (LinearLayout) v.findViewById(R.id.idtvGeoFence);
        idtvSpeed = (TextView) v.findViewById(R.id.idtvSpeed);
        one_txt_driven = (TextView) v.findViewById(R.id.one_txt_driven);

        sender = new GMailSender("sos@iconnectcloudsolutions.com", "Cisco_12");
        hashCode = Utils.getPreferences("hashCode", context);
        trackerID = Utils.getPreferences("TrackerID", context);

        idtvMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsMain.class);
                intent.putExtra("trackerID", trackerID);
                startActivity(intent);
            }
        });

        idtvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrackingHistory.class);
                intent.putExtra("trackerID", trackerID);
                startActivity(intent);
            }
        });


        idtvAssigndTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, AssignTaskMain.class);
//                intent.putExtra("trackerID", trackerID);
//                startActivity(intent);
                sosSend();
            }
        });
        idtvEchoDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, EcoDriving.class);
//                intent.putExtra("trackerID", trackerID);
//                startActivity(intent);
                sosMenu();
            }
        });
        idtvGeoFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapGeoFence.class);
                intent.putExtra("trackerID", trackerID);
                startActivity(intent);
            }
        });

        idlnObd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, OBDMain.class);
//                intent.putExtra("trackerID", trackerID);
//                startActivity(intent);
//                ===============
                Intent intent = new Intent(context, ActiveHour.class);
                startActivity(intent);
            }
        });
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.d("permission", "" + permissionCheck);
    }

    private void sosSend() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sos_pressed_real);
        TextView idtvCancelSosReal = (TextView) dialog.findViewById(R.id.idtvCancelSosReal);
        TextView idtvMessage = (TextView) dialog.findViewById(R.id.idtvMessage);
        TextView idtvAddsos = (TextView) dialog.findViewById(R.id.idtvAddsos);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        idtvMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                status = "";
//                progressDialog = ProgressDialog.show(context, "",
//                        "Sending SOS Message...", true);
//                AsyncSosPressed runner = new AsyncSosPressed();
//                runner.execute();
                sendSMSDemo();
            }
        });
        idtvAddsos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(context, SosInput.class);
                startActivity(intent);
            }
        });
        idtvCancelSosReal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void sendSMSDemo() {
        String message = Utils.getPreferences("sos_message", context);
        String phone_nubers = Utils.getPreferences("sos_emails", context);
        if (phone_nubers.length() < 1) {
            Toast.makeText(context, "Please save sos contact.", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] smsNumArray = phone_nubers.split(", ");

        DatabaseHandler db = new DatabaseHandler(context);
        String lat = db.getLatbyTID(trackerID);
        String lon = db.getLngbyTID(trackerID);
        db.close();
        String locationURl = "https://maps.google.com/maps?q=" + lat + "," + lon;

        for (int k = 0; k < smsNumArray.length; k++) {
            String smsNum = smsNumArray[k];
            sendSMS(smsNum, message + " \n Location: " + locationURl);
        }
        Toast.makeText(context, "Message Sent !", Toast.LENGTH_SHORT).show();
    }

    private void getLastTrip() {
        String currentTime, lastTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date date = new Date();
        lastTime = dateFormat.format(cal.getTime());
        currentTime = dateFormat.format(date);
        try {
            lastTime = URLEncoder.encode(lastTime, "utf-8");
            currentTime = URLEncoder.encode(currentTime, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://api.navixy.com/v2/track/list?tracker_id=" + trackerID + "&from=" + lastTime + "&to=" + currentTime + "&hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                if (listtrackingHistory.length() > 0) {
                                    JSONObject c = listtrackingHistory.getJSONObject(listtrackingHistory.length() - 1);
                                    Track track = new Track();
                                    track.setStart_address(c.getString("start_address"));
                                    track.setEnd_address(c.getString("end_address"));
                                    track.setStart_date(c.getString("start_date"));
                                    track.setEnd_date(c.getString("end_date"));
                                    track.setLength(c.getString("length"));
                                    try {
                                        track.setNorm_fuel_consumed(c.getString("norm_fuel_consumed"));
                                    } catch (Exception e) {
                                        track.setNorm_fuel_consumed("-1");
                                    }
                                    setLastTrip(track);
                                } else {
                                    one_txt_driven.setText("0.0km covered in last trip");
                                }
                            } else {
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

    private void setLastTrip(Track track) {
        try {
            DatabaseHandler db = new DatabaseHandler(context);
            String trackerName = db.getTLabelbyID("" + trackerID);
            db.close();
            String value = trackerName + " covered " + track.getLength() + "Km ";
            if (track.getNorm_fuel_consumed().equals("-1")) {
                value += "in last trip";
            } else {
                value += "and used " + track.getNorm_fuel_consumed() + "liters of fuel in last trip";
            }
            one_txt_driven.setText(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sosMenu() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.immobilizer);
        idtvStartEngine = (Switch) dialog.findViewById(R.id.idtvStartEngine);
        TextView idtvCancel = (TextView) dialog.findViewById(R.id.idtvCancel);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        DatabaseHandler db = new DatabaseHandler(context);
        String connectionStatus = db.getStatus(trackerID);
        if (connectionStatus == null) {
            connectionStatus = "active";
        }
        if (connectionStatus.equals("active")) {
            idtvStartEngine.setChecked(true);
        } else {
            idtvStartEngine.setChecked(false);
            idtvStartEngine.setEnabled(false);
            Toast.makeText(context, "The tracker is disconnected", Toast.LENGTH_SHORT).show();
        }
        db.close();

        idtvStartEngine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    status = "";
                    progressDialog = ProgressDialog.show(context, "",
                            "Waiting...", true);
                    EngineGetStart();

                } else {
                    status = "";
                    progressDialog = ProgressDialog.show(context, "",
                            "Waiting.....", true);
                    EngineGetStop();
                }
            }
        });
        idtvCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class AsyncSosPressed extends AsyncTask<String, String, String> {
        String sosListUrl;
        List<Sos> sosList = new ArrayList<>();

        protected void onPreExecute() {
            sosListUrl = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/rule/list?hash=" + hashCode;
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(sosListUrl, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    JSONArray jsonVashiaclLisst = jsonObj.getJSONArray("list");
                    for (int i = 0; i < jsonVashiaclLisst.length(); i++) {
                        Sos sos = new Sos();
                        JSONObject vahicalInfo = jsonVashiaclLisst.getJSONObject(i);
                        String type = vahicalInfo.getString("type");
                        if (type.equals("sos")) {
                            String primary_text = vahicalInfo.getString("primary_text");
                            JSONObject alertsObj = vahicalInfo.getJSONObject("alerts");
                            JSONArray arr_sms_phones = alertsObj.getJSONArray("sms_phones");
                            JSONArray arr_emails = alertsObj.getJSONArray("emails");
                            JSONArray arr_phones = alertsObj.getJSONArray("phones");
                            sos.setArr_Email(arr_emails);
                            sos.setArr_Sms(arr_sms_phones);
                            sos.setArr_Number(arr_phones);
                            sos.setMessage(primary_text);
                            sosList.add(sos);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        protected void onPostExecute(String result) {

            try {
                if (status.equals("true")) {
                    DatabaseHandler db = new DatabaseHandler(context);
//                    String trackerID = Utils.getPreferences("TrackerID", context);
                    String lat = db.getLatbyTID(trackerID);
                    String lon = db.getLngbyTID(trackerID);
                    String locationURl = "https://maps.google.com/maps?q=" + lat + "," + lon;
                    for (int i = 0; i < sosList.size(); i++) {
                        String message = sosList.get(i).getMessage();
                        JSONArray smsNumArray = sosList.get(i).getArr_Sms();
                        JSONArray emailArray = sosList.get(i).getArr_Email();

                        for (int j = 0; j < emailArray.length(); j++) {
                            String emailID = (String) emailArray.get(j);
                            try {
                                new MyAsyncClass(emailID, message + " \n Location: " + locationURl).execute();
                            } catch (Exception ex) {
                                Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        for (int k = 0; k < smsNumArray.length(); k++) {
                            String smsNum = (String) smsNumArray.get(k);
                            sendSMS(smsNum, message + " \n Location: " + locationURl);
                        }

                    }
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
                    progressDialog = null;
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Not saved successfully", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(String... text) {
        }

    }

    private void sendSMS(String phoneNumber, String message) {
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                new Intent(), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission was granted, yay!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "permission denied, boo!", Toast.LENGTH_LONG).show();
                }
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission was granted, yay!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "permission denied, boo!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void updateData() {
        cLocationURL = "https://api.navixy.com/v2/tracker/get_last_gps_point?tracker_id=" + trackerID + "&hash=" + hashCode;
        lastUpdateURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackerID + "&hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, cLocationURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                JSONObject jsonUserInfo = response.getJSONObject("value");
                                lat = jsonUserInfo.getString("lat");
                                lon = jsonUserInfo.getString("lng");
                                address = jsonUserInfo.getString("address");
                                DatabaseHandler db = new DatabaseHandler(context);
                                db.updateTrackerLat(trackerID, lat, lon);
                                db.close();
                                updateUser();
                            } else {
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

    private void updateUser() {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, lastUpdateURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                Driver driver = new Driver();
                                JSONObject state = response.getJSONObject("state");
                                JSONObject gps = state.getJSONObject("gps");
                                String last_update = gps.getString("updated");
                                speed = gps.getString("speed");
                                connection_status = state.getString("connection_status");
                                movement_status = state.getString("movement_status");

                                driver.setConnection(connection_status);
                                driver.setCont_trackerID(trackerID);
                                driver.setLast_update(last_update);
                                DatabaseHandler db = new DatabaseHandler(context);
                                db.updateTrackerConnection(trackerID, connection_status, last_update);
                                db.close();

                                updateUI();
                            } else {
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

    private void updateUI() {
        if (address.isEmpty()) {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
                if (addresses.size() > 0) {
                    addresses.get(0).getCountryName();
                    address = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getCountryName();
                    idtvAddess.setText(address);

                    Utils.savePreferences("lat", lat, context);
                    Utils.savePreferences("lon", lon, context);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            idtvAddess.setText(address);
        }
        idtvSpeed.setText("Speed " + speed + " KMPH");
        DatabaseHandler db = new DatabaseHandler(context);
        String lastUpdate = db.getLastUpdate(trackerID);
        db.close();
        if (movement_status.equals("parked"))
            idtvMovementStatus.setText("Parked");
        else if (movement_status.equals("stopped"))
            idtvMovementStatus.setText("Stopped");
        else if (movement_status.equals("moving"))
            idtvMovementStatus.setText("Moving");
        else
            idtvMovementStatus.setText("" + movement_status);

        if (movement_status.equals("Parked") || movement_status.equals("Stopped") || movement_status.equals("Moving")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            try {
                if (!lastUpdate.equals("")) {
                    Date date1 = sdf.parse(lastUpdate);
                    Date date2 = sdf.parse(date);
                    long diff = date2.getTime() - date1.getTime();//as given
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    long days = TimeUnit.MILLISECONDS.toDays(diff);
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    if (minutes < 60) {
                        idtvLastUpdated.setText(minutes + " mins ago");
                    } else if (hours > 0 && hours < 25) {
                        idtvLastUpdated.setText(hours + " hours ago");
                    } else if (days > 0) {
                        idtvLastUpdated.setText(days + " days ago");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {
        String emailAddress;
        String message;

        public MyAsyncClass(String emailID, String message_) {
            emailAddress = emailID;
            message = message_;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Mail Sent By Smart Tracker App", "" + message, "sos@iconnectcloudsolutions.com", emailAddress);


            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(context, "SOS messages sent successfully", Toast.LENGTH_LONG).show();
        }
    }

    private void EngineGetStart() {
        jArrOutPutsEngineON.clear();
        connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackerID + "&hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                JSONObject state = response.getJSONObject("state");
                                JSONArray jarray = state.getJSONArray("outputs");
                                for (int i = 0; i < jarray.length(); i++) {
                                    jArrOutPutsEngineON.add((Boolean) jarray.get(i));
                                }
                                EngineApplyStart();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
//                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void EngineApplyStart() {
        final List<String> jPuttsOn = new ArrayList<String>();
        status = "";
        description = "";
        jPuttsOn.clear();
        jPuttsOnPosition.clear();
        for (int i = 0; i < jArrOutPutsEngineON.size(); i++) {
            try {
                String outp = jArrOutPutsEngineON.get(i).toString();
                if (outp.equals("false")) {
                    jPuttsOn.add("true");
                    int pos = i + 1;
                    jPuttsOnPosition.add("" + pos);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (jPuttsOn.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, "All outputs are already ON", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < jPuttsOn.size(); i++) {
            connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + jPuttsOnPosition.get(i) + "&enable=true&tracker_id=" + trackerID;
            final int finalI = i;
            final String finalTID = trackerID;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                status = response.getString("success");
                                if (status.equals("true")) {
                                    if (finalI == jPuttsOn.size() - 1) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Action performed", Toast.LENGTH_LONG).show();
                                        DatabaseHandler db = new DatabaseHandler(context);
                                        db.updateTrackerConnectionOnly(finalTID, "active");
                                        db.close();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressDialog.dismiss();
//                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
        }
    }

    private void EngineGetStop() {
        status = "";
        jArrOutPuts.clear();
        description = "";
        connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackerID + "&hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                JSONObject state = response.getJSONObject("state");
                                JSONArray jarray = state.getJSONArray("outputs");
                                for (int i = 0; i < jarray.length(); i++) {
                                    jArrOutPuts.add((Boolean) jarray.get(i));
                                }
                                EngineApplyStop();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
//                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void EngineApplyStop() {
        jPuttss.clear();
        jPuttssPositionOFF.clear();
        status = "";
        description = "";

        for (int i = 0; i < jArrOutPuts.size(); i++) {
            try {
                String outp = jArrOutPuts.get(i).toString();
//                    if (outp.equals("true")) {
                jPuttss.add("false");
                int pos = i + 1;
                jPuttssPositionOFF.add("" + pos);
//                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jPuttss.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, "All outputs are already off", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < jPuttss.size(); i++) {
            connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + jPuttssPositionOFF.get(i) + "&enable=false&tracker_id=" + trackerID;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                status = response.getString("success");
                                if (status.equals("true")) {
                                    if (finalI == jPuttss.size() - 1) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Action performed", Toast.LENGTH_LONG).show();
                                        DatabaseHandler db = new DatabaseHandler(context);
                                        db.updateTrackerConnectionOnly(trackerID, "signal_lost");
                                        db.close();
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressDialog.dismiss();
//                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
        }
    }
}
