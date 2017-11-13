package active_hour;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/29/2016.
 */
public class AssignActiveHours extends Activity {
    Context context;
    ListView listTrackingHistory;
    ProgressDialog progressDialog;
    ArrayList arrTrackingHistory;
    LinearLayout idllDays;
    String currentTime;
    String hashCode, TrackerID = "37712";
    AdptorAssignhours adapter;
    DateFormat timeFormat = new SimpleDateFormat("hh:mm");
    DateFormat requiredTimeFormat = new SimpleDateFormat("hh:mm a");
    String auto_protect, token;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.assign_active_hours);
        listTrackingHistory = (ListView) findViewById(R.id.idlvTrackingHistory);
        idllDays = (LinearLayout) findViewById(R.id.idllDays);
        context = this;
        arrTrackingHistory = new ArrayList<String>();
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = Utils.getPreferences("TrackerID", context);
        auto_protect = Utils.getPreferences("auto-protect_" + TrackerID, context);
        auto_protect = Utils.getPreferences("auto-protect_" + TrackerID, context);
        token = Utils.getPreferences("portalToken", context);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        currentTime = dateTime.replace("/", "-");
        try {
            currentTime = URLEncoder.encode(currentTime, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        listTrackingHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hours track;
                track = (Hours) arrTrackingHistory.get(i);
                DatabaseHandler db = new DatabaseHandler(context);
                String tracker_id = db.getTrackerID(track.getTrackerID());
                db.close();
                Intent intent = new Intent(context, ActiveHour.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("day", track.getDay());
                intent.putExtra("saved_tracker", tracker_id);
                intent.putExtra("start_time", track.getStart_time());
                intent.putExtra("end_time", track.getEnd_time());
                startActivity(intent);
            }
        });
        listTrackingHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alertDialog = new AlertDialog.Builder(AssignActiveHours.this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure to delete this item ?");
                alertDialog.setIcon(R.drawable.ic_launcher);

                alertDialog.setButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Hours hours = (Hours) arrTrackingHistory.get(position);
                        deleteRow(hours);
                        arrTrackingHistory.remove(position);
                        sendActiveHoursDataToServer(token, generateActiveHoursParams());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(context, "Delete Success !", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.setButton2("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                return true;
            }
        });

        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    private String convertTime(String time) {
        try {
            return requiredTimeFormat.format(timeFormat.parse(time));
        } catch (ParseException e) {
            Log.e("convertTime", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return "";
    }

    private int getDayAsInteger(String dayName) {
        if (dayName.equals("Sunday")) {
            return 1;
        }
        if (dayName.equals("Monday")) {
            return 2;
        }
        if (dayName.equals("Tuesday")) {
            return 3;
        }
        if (dayName.equals("Wednesday")) {
            return 4;
        }
        if (dayName.equals("Thursday")) {
            return 5;
        }
        if (dayName.equals("Friday")) {
            return 6;
        }
        if (dayName.equals("Saturday")) {
            return 7;
        }
        return 1;
    }


    private Map generateActiveHoursParams() {
        DatabaseHandler db = new DatabaseHandler(context);
        Map params = new HashMap<>();
        ArrayList<Hours> listtrackingHistory;
        ArrayList<ActiveHourWS> listActiveHourWS = new ArrayList<>();
        listtrackingHistory = (ArrayList<Hours>) db.getActiveHoursList();
        for (int i = 0; i < listtrackingHistory.size(); i++) {
            ActiveHourWS activeHourWS = new ActiveHourWS();
            activeHourWS.setDay(getDayAsInteger(listtrackingHistory.get(i).getDay()));
            activeHourWS.setStartTime(convertTime(listtrackingHistory.get(i).getStart_time()));
            activeHourWS.setEndTime(convertTime(listtrackingHistory.get(i).getEnd_time()));
            listActiveHourWS.add(activeHourWS);
        }
        params.put("trackerId", TrackerID);
        params.put("day", listActiveHourWS.toString());
        params.put("isAutoProtectEnable", auto_protect);

        return params;
    }


    private void sendActiveHoursDataToServer(final String token, final Map params) {
        String url = String.format("%sevent/syncAutoProtectFromApp", AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    sendActiveHoursDataToServer(token, params);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }

            @Override
            protected Map getParams() throws AuthFailureError {
                return params;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "sendDataToPortalServer");
    }

    private void deleteRow(Hours hours) {
        DatabaseHandler db = new DatabaseHandler(context);
        String tracker_id = db.getTrackerID(hours.getTrackerID());
        db.removeExistingTime(hours.getDay(), tracker_id);
        db.close();
    }

    private void checkDelete() {
        AlertDialog alertDialog = new AlertDialog.Builder(
                AssignActiveHours.this).create();
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you going to delete this item ?");
        alertDialog.setIcon(R.drawable.ic_launcher);

        alertDialog.setButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setButton2("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Toast.makeText(getApplicationContext(), "You clicked on Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            arrTrackingHistory.clear();
        }

        protected String doInBackground(String... params) {
            try {
                DatabaseHandler db = new DatabaseHandler(context);
                ArrayList<Hours> listtrackingHistory = new ArrayList<Hours>();
                listtrackingHistory = (ArrayList<Hours>) db.getActiveHoursList();
//                    listtrackingHistory = (ArrayList<Hours>) db.getActiveHoursListByTracker(TrackerID);
                for (int i = 0; i < listtrackingHistory.size(); i++) {
                    listtrackingHistory.get(i).getDay();
                    Hours track = new Hours();
                    track.setDay(listtrackingHistory.get(i).getDay());
                    track.setStart_time(listtrackingHistory.get(i).getStart_time());
                    track.setEnd_time(listtrackingHistory.get(i).getEnd_time());
                    track.setTrackerID(db.getTrakerName(listtrackingHistory.get(i).getTrackerID()));
                    arrTrackingHistory.add(track);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            if (AssignActiveHours.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            progressDialog.dismiss();
            progressDialog = null;
            try {
                if (arrTrackingHistory.isEmpty()) {
                    Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
                }
                adapter = new AdptorAssignhours(context, arrTrackingHistory);
                listTrackingHistory.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(String... text) {
        }

    }
}

