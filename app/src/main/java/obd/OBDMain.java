package obd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.Utils;

/**
 * Created by Farhan on 7/29/2016.
 */
public class OBDMain extends Activity {
    Context context;
    TextView idtvBack, idtvIgnitionStatus, idtvWeek, idtvMonth, idtvYear;
    ListView idlvOBD;
    ProgressDialog progressDialog;
    ArrayList arrOBD;
    String urltrackingHistory;
    String currentTime;



    String hashCode, TrackerID = "37712";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.obd_list_view);
        idlvOBD = (ListView) findViewById(R.id.idlvOBD);
        idtvBack = (TextView) findViewById(R.id.idtvBack);
        idtvIgnitionStatus = (TextView) findViewById(R.id.idtvIgnitionStatus);
        context = this;
        arrOBD = new ArrayList<String>();
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = getIntent().getStringExtra("trackerID");
        DatabaseHandler db = new DatabaseHandler(context);
        String connectionStatus = db.getStatus(TrackerID);

        if(connectionStatus.equals("signal_lost")){
            connectionStatus = "Signal Lost";
        }else if(connectionStatus.equals("active")){
            connectionStatus = "Online";
        }else if(connectionStatus.equals("offline")){
            connectionStatus = "Offline";
        }

        idtvIgnitionStatus.setText(""+connectionStatus);
        db.close();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        currentTime = dateTime.replace("/", "-");
        try {
            String hashCode = Utils.getPreferences("hashCode", context);
            String trackerID = Utils.getPreferences("TrackerID",context);
            currentTime = URLEncoder.encode(currentTime, "utf-8");
            urltrackingHistory = "http://demo.navixy.com/api-v2/tracker/get_diagnostics?hash="+hashCode+"&tracker_id="+trackerID;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(currentTime);

        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();





        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                for (int i = 0; i > -1; i++) {
                    try {

                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }

                    AsyncTaskRunnerRepeate runner = new AsyncTaskRunnerRepeate();
                    runner.execute();
//                    Log.d("log", " Loop has been running ");
//                    Toast.makeText(context, "Loop has been running", Toast.LENGTH_LONG).show();

                }

                //Stop service once it finishes its task

            }
        }).start();








        idtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            arrOBD.clear();
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(urltrackingHistory, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            Log.d("URL task list: ", "> " + urltrackingHistory);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String success = jsonObj.getString("success");
                    if (success.equals("true")) {
                        JSONArray listtrackingHistory = jsonObj.getJSONArray("inputs");
                        for (int i = 0; i < listtrackingHistory.length(); i++) {
                            JSONObject c = listtrackingHistory.getJSONObject(i);
                            OBD track = new OBD();
                            track.setValue(c.getString("value"));
                            track.setName(c.getString("name"));
                            track.setType(c.getString("type"));
                            track.setUnits_type(c.getString("units_type"));
                            track.setUnits(c.getString("units"));

                            arrOBD.add(track);
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
            if (OBDMain.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            progressDialog.dismiss();
            progressDialog = null;
            try {
                if (arrOBD.isEmpty()) {
                    Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
                }else {
                    AdptorOBD adapter = new AdptorOBD(context, arrOBD);
                    idlvOBD.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }


    private class AsyncTaskRunnerRepeate extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            arrOBD.clear();
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(urltrackingHistory, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            Log.d("URL task list: ", "> " + urltrackingHistory);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String success = jsonObj.getString("success");
                    if (success.equals("true")) {
                        JSONArray listtrackingHistory = jsonObj.getJSONArray("inputs");
                        for (int i = 0; i < listtrackingHistory.length(); i++) {
                            JSONObject c = listtrackingHistory.getJSONObject(i);
                            OBD track = new OBD();
                            track.setValue(c.getString("value"));
                            track.setName(c.getString("name"));
                            track.setType(c.getString("type"));
                            track.setUnits_type(c.getString("units_type"));
                            track.setUnits(c.getString("units"));

                            arrOBD.add(track);
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
                if (arrOBD.isEmpty()) {
//                    Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
                }else {
                    AdptorOBD adapter = new AdptorOBD(context, arrOBD);
                    idlvOBD.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }





}

