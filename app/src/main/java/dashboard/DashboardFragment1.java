package dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.ParkingGuardActivity;
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
import routine_maintinance.RoutineMaintinance;
import sos.GMailSender;
import tracking_history.Track;
import util.AppSingleton;
import util.Utils;


@SuppressLint("ValidFragment")
public class DashboardFragment1 extends Fragment {
    View v;
    boolean _isVisible = true;
    Thread thread;
    String trackerID, trackerName, reportID;
    Context context;
    String cLocationURL, lastUpdateURL;
    Switch idtvStartEngine;
    ProgressDialog progressDialog;
    ArrayList<RoutineMaintinance> aryMaintenance = new ArrayList<>();
    Dialog dialog;
    String status, lat, lon, speed, address, txt_driving_status;
    TextView tv_tracker, tv_score, tv_driven_today, tv_speed, tv_harsh_acceleration, tv_speeding, tv_idle, tv_driving_status, tv_driving_on;
    TextView tv_last_km, tv_last_mins, tv_last_fuel, tv_last_speed, tv_event_1, tv_event_2;
    public static TextView tv_maintenance_year, tv_maintenance_month;
    LinearLayout item_smartdefence, item_curent_location, item_geofence, item_immobilizer;
    FrameLayout speed_needle_main, speed_needle_piece, frame_kms_driven;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    GMailSender sender;
    String connectionStatusURL;
    String connection_status;
    String description;
    String hashCode;
    boolean checkedValue = false;
    double yearCosts = 0.0, monthCosts = 0.0;
    private List<Boolean> jArrOutPutsEngineON = new ArrayList<Boolean>();
    private List<Boolean> jArrOutPuts = new ArrayList<Boolean>();
    private List<String> jPuttsOnPosition = new ArrayList<String>();
    private List<String> jPuttss = new ArrayList<String>();
    private List<String> jPuttssPositionOFF = new ArrayList<String>();

    @SuppressLint("ValidFragment")
    public DashboardFragment1() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) return v;
        v = inflater.inflate(R.layout.dashboard_layout_new2, container, false);
        init();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        _isVisible = true;
        _isVisible = true;
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        _isVisible = false;
    }

    private void initData() {
        getLastTrip();
//        getReportID();
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
        item_curent_location = (LinearLayout) v.findViewById(R.id.dashboard_item_current_location);
        item_geofence = (LinearLayout) v.findViewById(R.id.dashboard_item_geo_fence);
        item_immobilizer = (LinearLayout) v.findViewById(R.id.dashboard_item_immobilizer);
        item_smartdefence = (LinearLayout) v.findViewById(R.id.dashboard_item_smartdefence);
        speed_needle_main = (FrameLayout) v.findViewById(R.id.speed_needle_main);
        speed_needle_piece = (FrameLayout) v.findViewById(R.id.speed_needle_piece);
        frame_kms_driven = (FrameLayout) v.findViewById(R.id.frame_kms_driven);

        tv_tracker = (TextView) v.findViewById(R.id.dashboard_txt_trackerName);
        tv_score = (TextView) v.findViewById(R.id.dashboard_txt_score);
        tv_speed = (TextView) v.findViewById(R.id.dashboard_txt_speed);
        tv_driven_today = (TextView) v.findViewById(R.id.dashboard_txt_driven);
        tv_harsh_acceleration = (TextView) v.findViewById(R.id.dashboard_txt_harsh_acceleration);
        tv_speeding = (TextView) v.findViewById(R.id.dashboard_txt_speeding);
        tv_idle = (TextView) v.findViewById(R.id.dashboard_txt_hidle);
        tv_driving_status = (TextView) v.findViewById(R.id.dashboard_txt_status_title);
        tv_driving_on = (TextView) v.findViewById(R.id.dashboard_txt_current_driving_on);
        tv_last_km = (TextView) v.findViewById(R.id.dashboard_last_km);
        tv_last_mins = (TextView) v.findViewById(R.id.dashboard_last_mins);
        tv_last_fuel = (TextView) v.findViewById(R.id.dashboard_last_fuel);
        tv_last_speed = (TextView) v.findViewById(R.id.dashboard_last_speed);
        tv_maintenance_year = (TextView) v.findViewById(R.id.dashboard_maintenance_year);
        tv_maintenance_month = (TextView) v.findViewById(R.id.dashboard_maintenance_month);
        tv_event_1 = (TextView) v.findViewById(R.id.dashboard_txt_event_1);
        tv_event_2 = (TextView) v.findViewById(R.id.dashboard_txt_event_2);

        sender = new GMailSender("sos@iconnectcloudsolutions.com", "Cisco_12");
        hashCode = Utils.getPreferences("hashCode", context);
        trackerID = Utils.getPreferences("TrackerID", context);
        DatabaseHandler db = new DatabaseHandler(context);
        aryMaintenance = db.getCostsByID(trackerID);
        trackerName = db.getTLabelbyID("" + trackerID);
        db.close();
        tv_tracker.setText(trackerName);

        item_curent_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsMain.class);
                intent.putExtra("trackerID", trackerID);
                startActivity(intent);
            }
        });
        item_immobilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPass(1);
            }
        });
        item_geofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapGeoFence.class);
                intent.putExtra("trackerID", trackerID);
                startActivity(intent);
            }
        });
        item_smartdefence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPass(2);
//                Intent intent = new Intent(context, ActiveHour.class);
//                startActivity(intent);
            }
        });
        frame_kms_driven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomViewIconTextTabsFragment.tabLayout.getTabAt(2).select();
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
        getDrivenToday();
        getMaintenanceData();
        GetUpcomingEventRunner runner = new GetUpcomingEventRunner();
        runner.execute();

        String sos = Utils.getPreferences("Sos_pressed_" + trackerID, context);
        if (sos.equals("true")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false)
                    .setMessage("SOS button pressed on " + trackerName);
            final AlertDialog alertDialog = builder.create();
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Open SmartDefence", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                    checkPass(2);
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Ignore", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    void getMaintenanceData() {
        String url = "http://api.navixy.com/v2/vehicle/service_task/list?hash=" + hashCode;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                for (int i = 0; i < listtrackingHistory.length(); i++) {
                                    JSONObject c = listtrackingHistory.getJSONObject(i);
                                    String cTrackerID = c.getString("vehicle_label");
                                    if (cTrackerID.equals(trackerName)) {
                                        String str_cost = c.getString("cost");
                                        double cost = Double.parseDouble(str_cost);
                                        String cStatus = c.getString("status");
                                        if (cStatus.equals("done")) {
                                            String edate = c.getString("completion_date");
                                            try {
                                                Date date1 = sdf.parse(edate);
                                                Date curDate = new Date();
                                                if (date1.getYear() == curDate.getYear()) {
                                                    yearCosts += cost;
                                                    if (date1.getMonth() == curDate.getMonth()) {
                                                        monthCosts += cost;
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                setMaintenanceCost();
                            } else {
                                Log.e("getMaintenanceData", "yes");
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

    void setMaintenanceCost() {
        tv_maintenance_year.setText("$" + (int) yearCosts);
        tv_maintenance_month.setText("$" + (int) monthCosts);
    }

    void getMaintenanceData1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String total_spent = Utils.getPreferences("total_spent", context);
//        tv_maintenance_total.setText("$" + total_spent);
        double yearCosts = 0.0, monthCosts = 0.0;
        for (RoutineMaintinance data : aryMaintenance) {
            double cost = Double.parseDouble(data.getCost());
            try {
                Date date1 = sdf.parse(data.getEnd_date());
                Date curDate = new Date();
                if (date1.getYear() == curDate.getYear()) {
                    yearCosts += cost;
                    if (date1.getMonth() == curDate.getMonth()) {
                        monthCosts += cost;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        tv_maintenance_year.setText("$" + (int) yearCosts);
        tv_maintenance_month.setText("$" + (int) monthCosts);
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
                                        track.setSpeed(c.getString("max_speed"));
                                    } catch (Exception e) {
                                        track.setNorm_fuel_consumed("-1");
                                    }
                                    setLastTrip(track);
                                } else {
//                                    one_txt_driven.setText("0.0km covered in last trip");
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
            tv_last_km.setText(track.getLength() + "KM");
            tv_last_fuel.setText(track.getNorm_fuel_consumed() + " L");
            tv_last_speed.setText(track.getSpeed() + " Km/h");
            String mins = getTimePeriod(track.getStart_date(), track.getEnd_date());
            tv_last_mins.setText(mins);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimePeriod(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String returnValue = "0 MINS";
        try {
            Date date1 = sdf.parse(startDate);
            Date date2 = sdf.parse(endDate);
            long diff = date2.getTime() - date1.getTime();//as given
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            returnValue = minutes + " MINS";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public void openImmobilizer() {
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
            idtvStartEngine.setEnabled(false);
            Toast.makeText(context, "The tracker is disconnected", Toast.LENGTH_SHORT).show();
        }
        db.close();

        idtvStartEngine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("idtvStartEngine", " " + isChecked);
                if (!isChecked) {
                    status = "";
                    progressDialog = ProgressDialog.show(context, "",
                            "Waiting...", true);
                    EngineGetStop();
                } else {
                    status = "";
                    progressDialog = ProgressDialog.show(context, "",
                            "Waiting.....", true);
                    EngineGetStart();
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

    private void checkPass(final int selected) {
        final String p_pass = Utils.getPreferences("p_password", context);
        if (p_pass.length() < 1) {
            savePass();
        } else {
            String strtitle = "Please enter password.";
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.save_protect_layout);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            final TextView title = (TextView) dialog.findViewById(R.id.title);
            final EditText txtPass = (EditText) dialog.findViewById(R.id.txtPass);
            Button btnOK = (Button) dialog.findViewById(R.id.btnOK);

            title.setText(strtitle);

            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (txtPass.getText().toString().toLowerCase().equals(p_pass)) {
                        if (selected == 1)
                            openImmobilizer();
                        else {
                            Intent intent = new Intent(context, ActiveHour.class);
                            startActivity(intent);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Password is incorrect. Reset Password ?");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                savePass();
                            }
                        });
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            });
            dialog.show();
        }
    }

    private void savePass() {
        String p_question = Utils.getPreferences("p_question", context);
        if (p_question.length() < 1) {
            saveQuestion();
        } else {
            final String[] str_q = p_question.split("@");
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.save_protect_layout);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            final TextView title = (TextView) dialog.findViewById(R.id.title);
            final EditText txtPass = (EditText) dialog.findViewById(R.id.txtPass);
            Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
            title.setText(str_q[0]);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtPass.getText().toString().toLowerCase().equals(str_q[1])) {
                        dialog.dismiss();
                        saveProtect();
                    } else {
                        Toast.makeText(context, "Incorrect answer", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }
    }

    private void saveProtect() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_pass_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        final TextView title = (TextView) dialog.findViewById(R.id.title);
        final EditText txtPass = (EditText) dialog.findViewById(R.id.txtPass);
        final EditText txtConfirm = (EditText) dialog.findViewById(R.id.txtConfirm);
        title.setText("Save password");
        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPass = txtPass.getText().toString().toLowerCase();
                String strConfirm = txtConfirm.getText().toString().toLowerCase();
                if (strPass.length() != 4) {
                    Toast.makeText(context, "Input a 4 digit password", Toast.LENGTH_SHORT).show();
                } else if (strPass.equals(strConfirm)) {
                    Utils.savePreferences("p_password", strPass, context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Input the same Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void saveQuestion() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_question_layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        final TextView title = (TextView) dialog.findViewById(R.id.title);
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final EditText txtConfirm = (EditText) dialog.findViewById(R.id.txtPass);
        title.setText("Save question");
        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = txtConfirm.getText().toString().toLowerCase();
                if (answer.length() < 1) {
                    Toast.makeText(context, "Input a answer", Toast.LENGTH_SHORT).show();
                } else {
                    String data = spinner.getSelectedItem().toString() + "@" + answer;
                    Utils.savePreferences("p_question", data, context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    saveProtect();
                }
            }
        });
        dialog.show();
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
                                txt_driving_status = state.getString("movement_status");

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
                    tv_driving_on.setText(address);
                    Utils.savePreferences("lat", lat, context);
                    Utils.savePreferences("lon", lon, context);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            tv_driving_on.setText(address);
        }
        String cap = txt_driving_status.substring(0, 1).toUpperCase() + txt_driving_status.substring(1);
        tv_driving_status.setText("Currently " + cap + " On -");
        tv_speed.setText(speed + " KM");

        try {
            double d_speed = Double.parseDouble(speed);
            double degree;
            if (d_speed < 50.0) {
                degree = (d_speed + 10) * 3.0 + 180.0;
            } else {
                degree = (d_speed - 50) * 3.0;
            }
            speed_needle_main.setRotation((float) degree);
            speed_needle_piece.setRotation((float) degree);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDrivenToday() {
        String driving_data = Utils.getPreferences(trackerID + "_drive", context);
        if (driving_data.length() > 0) {
            String[] aryDriving = driving_data.split(", ");
            tv_score.setText(aryDriving[0]);
            tv_harsh_acceleration.setText(aryDriving[1]);
            tv_speeding.setText(aryDriving[2]);
            tv_idle.setText(aryDriving[3]);
        }
//
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -1);
//        String newTime = dateFormat.format(cal.getTime());
//        String currentTime = dateFormat.format(new Date());
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        String sdate = curYear + "-" + curMonth + "-" + curDay + " 00:00:00";
        String edate = curYear + "-" + curMonth + "-" + curDay + " 23:59:59";
        try {
            sdate = URLEncoder.encode(sdate, "utf-8");
            edate = URLEncoder.encode(edate, "utf-8");
            String url = "https://api.navixy.com/v2/track/list?tracker_id=" + trackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("success");
                                if (status.equals("true")) {
                                    JSONArray listtrackingHistory = response.getJSONArray("list");
                                    double kms = 0.0;
                                    for (int i = 0; i < listtrackingHistory.length(); i++) {
                                        JSONObject c = listtrackingHistory.getJSONObject(i);
                                        String tmp = c.getString("length");
                                        kms += Double.parseDouble(tmp);
//                                        Log.e("length===========",tmp + "=========" + i + "===" + kms);
                                    }
                                    setDrivenToday(kms);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("rrr", "rrrr");
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setDrivenToday(double kms) {
        int tmp = (int) kms;
        tv_driven_today.setText(tmp + "");
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
                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        jsonRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 20000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

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

        if (jArrOutPutsEngineON.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, "All outputs are already ON", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < jArrOutPutsEngineON.size(); i++) {
            connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + (i + 1) + "&enable=false&tracker_id=" + trackerID;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                status = response.getString("success");
                                if (status.equals("true")) {
                                    if (finalI == jArrOutPutsEngineON.size() - 1) {
                                        progressDialog.dismiss();
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(trackerName + " has been enabled !");
                                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                        DatabaseHandler db = new DatabaseHandler(context);
                                        db.updateTrackerConnectionOnly(trackerID, "active");
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
                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            jsonRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 20000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 2000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

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
                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        jsonRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 20000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void EngineApplyStop() {
        jPuttss.clear();
        jPuttssPositionOFF.clear();
        status = "";
        description = "";

        if (jArrOutPuts.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, "All outputs are already off", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < jArrOutPuts.size(); i++) {
            connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + (i + 1) + "&enable=true&tracker_id=" + trackerID;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("EngineApplyStop", " " + response);
                            try {
                                status = response.getString("success");
                                if (status.equals("true")) {
                                    if (finalI == jArrOutPuts.size() - 1) {
                                        progressDialog.dismiss();
//                                        Toast.makeText(context, trackerName + " has been enabled !", Toast.LENGTH_LONG).show();
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(trackerName + " has been disabled !");
                                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
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
                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            jsonRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 20000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 2000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
        }
    }

    private void getReportID() {
        String loginURL = "", tID = "";
        try {
            List<Trackers> trackerList;
            DatabaseHandler db = new DatabaseHandler(context);
            trackerList = db.getTrackerList();
            db.close();
            for (int i = 0; i < trackerList.size(); i++) {
                tID = tID + "," + trackerList.get(i).getTrackerID();
            }
            tID = tID.substring(5);

            String from = URLEncoder.encode("2016-07-01 00:00:00", "utf-8");
            String to = URLEncoder.encode("2016-09-16 23:59:59", "utf-8");
            String geocoder = URLEncoder.encode("osm", "utf-8");
            loginURL = "https://api.navixy.com/v2/report/tracker/generate?hash=" + hashCode + "" +
                    "&from=" + from + "&to=" + to + "&geocoder=" + geocoder + "&" +
                    "trackers=[" + tID + "]&type=service&time_filter={to:'8:00', from:'21:00', weekdays:[1,2,3,4,5,6,7]}&plugin={%22plugin_id%22:46}";

            Log.d("URL: ", "> " + loginURL);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, loginURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                reportID = response.getString("id");
                                getReportData();
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

    private void getReportData() {
        String reportURL = "https://api.navixy.com/v2/report/tracker/retrieve?hash=" + hashCode + "&report_id=" + reportID;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, reportURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                updateReport();
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

    private void updateReport() {
    }

    public static void updateMaintenance(RoutineMaintinance track) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        double cost = Double.parseDouble(track.getCost());
        try {
            Date date1 = sdf.parse(track.getEnd_date());
            Date curDate = new Date();
            double tmp;
            if (date1.getYear() == curDate.getYear()) {
                String total = tv_maintenance_year.getText().toString();
                if (total.length() > 0) {
                    total = total.substring(1);
                    if (track.getStatus().equals(""))
                        tmp = Double.parseDouble(total) - cost;
                    else
                        tmp = Double.parseDouble(total) + cost;
                } else {
                    tmp = cost;
                }
                tv_maintenance_year.setText("$" + (int) tmp);
                if (date1.getMonth() == curDate.getMonth()) {
                    total = tv_maintenance_month.getText().toString();
                    if (total.length() > 0) {
                        total = total.substring(1);
                        if (track.getStatus().equals(""))
                            tmp = Double.parseDouble(total) - cost;
                        else
                            tmp = Double.parseDouble(total) + cost;
                    } else {
                        tmp = cost;
                    }
                    tv_maintenance_month.setText("$" + (int) tmp);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class AsyncIsReportGenrated extends AsyncTask<String, String, String> {
        String isReportGenratedURL, percent_ready;

        protected void onPreExecute() {
            isReportGenratedURL = "https://api.navixy.com/v2/report/tracker/status?hash=" + hashCode + "&report_id=" + reportID;
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(isReportGenratedURL, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    percent_ready = jsonObj.getString("percent_ready");

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
                    if (percent_ready.equals("100")) {
                        AsyncReport runner = new AsyncReport();
                        runner.execute();
                    } else {
                        AsyncIsReportGenrated runner = new AsyncIsReportGenrated();
                        runner.execute();
                    }
                } else {
                    Toast.makeText(context, "Problem in generating report", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AsyncReport extends AsyncTask<String, String, String> {
        String reportURL;

        protected void onPreExecute() {
            reportURL = "https://api.navixy.com/v2/report/tracker/retrieve?hash=" + hashCode + "&report_id=" + reportID;
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(reportURL, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    JSONObject report = jsonObj.getJSONObject("report");
                    JSONArray sheets = report.getJSONArray("sheets");

                    JSONObject c = sheets.getJSONObject(0);
                    String heade = c.getString("header");
                    JSONArray sections = c.getJSONArray("sections");

                    JSONObject stacked_bar_chart = sections.getJSONObject(0);
                    JSONObject simple_bar_chart = sections.getJSONObject(1);
                    JSONObject summary = sections.getJSONObject(2);

                    JSONArray summaryArray = summary.getJSONArray("data");
                    JSONObject rowObj = summaryArray.getJSONObject(0);

                    JSONArray rowArray = rowObj.getJSONArray("rows");

                    for (int i = 0; i < rowArray.length(); i++) {
                    }


                    String typet = stacked_bar_chart.getString("type");
                    JSONObject y_axis = stacked_bar_chart.getJSONObject("y_axis");
                    String label = y_axis.getString("label");
                    JSONArray data = stacked_bar_chart.getJSONArray("data");
                    JSONArray data_simple_bar_chart = simple_bar_chart.getJSONArray("bars");
                    JSONObject obj_simple_bar_chart = data_simple_bar_chart.getJSONObject(0);

                    for (int i = 0; i < data.length(); i++) {
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
            progressDialog.dismiss();
            try {

                if (status.equals("true")) {

                } else {
                    Toast.makeText(context, "Problem in generating report", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetUpcomingEventRunner extends AsyncTask<String, String, String> {
        String urlMaintinenceDate = "https://api.navixy.com/v2/vehicle/service_task/list?hash=" + hashCode;
        ArrayList<RoutineMaintinance> sendData = new ArrayList<>();

        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(urlMaintinenceDate, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String success = jsonObj.getString("success");
                    if (success.equals("true")) {
                        JSONArray listtrackingHistory = jsonObj.getJSONArray("list");
                        for (int i = 0; i < listtrackingHistory.length(); i++) {
                            JSONObject c = listtrackingHistory.getJSONObject(i);
                            String cStatus = c.getString("status");
                            if (cStatus.equals("created")) {
                                RoutineMaintinance routineMaintinance = new RoutineMaintinance();
                                routineMaintinance.setId(c.getString("id"));
                                String cTrackerID = c.getString("vehicle_id");
                                routineMaintinance.setVehicle_id(cTrackerID);
                                routineMaintinance.setStatus(cStatus);
                                JSONObject prediction = c.getJSONObject("prediction");
                                routineMaintinance.setEnd_date(prediction.getString("end_date"));
                                routineMaintinance.setDescription(c.getString("description"));
                                routineMaintinance.setVehicle_label(c.getString("vehicle_label"));
                                sendData.add(routineMaintinance);
                            }
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
                if (!sendData.isEmpty()) {
                    int i = 0;
                    for (RoutineMaintinance r : sendData) {
                        String strDate = r.getEnd_date().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat newsdf = new SimpleDateFormat("MM/dd/yyyy");
                        if (i > 1)
                            return;
                        else if (i == 0) {
                            if (strDate.length() > 4) {
                                Date tmp = sdf.parse(strDate);
                                String endDate = newsdf.format(tmp);
                                tv_event_1.setText(r.getVehicle_label() + ": " + r.getDescription() + " - " + endDate);
                            } else {
                                tv_event_1.setText(r.getVehicle_label() + ": " + r.getDescription());
                            }
                        } else if (i == 1) {
                            if (strDate.length() > 4) {
                                Date tmp = sdf.parse(strDate);
                                String endDate = newsdf.format(tmp);
                                tv_event_2.setText(r.getVehicle_label() + ": " + r.getDescription() + " - " + endDate);
                            } else {
                                tv_event_2.setText(r.getVehicle_label() + ": " + r.getDescription());
                            }
                            return;
                        }
                        i++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(String... text) {
        }
    }
}
