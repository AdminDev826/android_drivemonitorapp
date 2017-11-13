package assing_task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

import io.fabric.sdk.android.Fabric;
import util.Utils;

/**
 * Created by Farhan on 7/29/2016.
 */
public class AssignTaskMain extends Activity {
    Context context;
    TextView idtvBackAssignTask, idtvYesterday, idtvWeek, idtvMonth, idtvYear;
    ListView listTrackingHistory;
    ProgressDialog progressDialog;
    ArrayList arrTrackingHistory;
    LinearLayout idllDays;
    String urltrackingHistory;
    String currentTime;
    String hashCode, TrackerID = "37712";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.assign_task_main);
        listTrackingHistory = (ListView) findViewById(R.id.idlvTrackingHistory);
        idtvBackAssignTask = (TextView) findViewById(R.id.idtvBackAssignTask);
        idllDays = (LinearLayout) findViewById(R.id.idllDays);
        context = this;
        arrTrackingHistory = new ArrayList<String>();
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = getIntent().getStringExtra("trackerID");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        currentTime = dateTime.replace("/", "-");
        try {
            String hashCode = Utils.getPreferences("hashCode", context);
            currentTime = URLEncoder.encode(currentTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/task/list?hash="+hashCode+"&statuses:[\"unassigned\",\"assigned\",\"arrived\",\"done\",\"failed\",\"delayed\",\"faulty\"]&from:2016-07-28%2000:00:00&to:2016-07-28%2023:59:59&trackers:["+TrackerID+"]";

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        idtvBackAssignTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            arrTrackingHistory.clear();
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(urltrackingHistory, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String success = jsonObj.getString("success");
                    if (success.equals("true")) {
                        JSONArray listtrackingHistory = jsonObj.getJSONArray("list");
                        for (int i = 0; i < listtrackingHistory.length(); i++) {
                            JSONObject c = listtrackingHistory.getJSONObject(i);
                            AssignTask track = new AssignTask();
                            track.setId(c.getString("status"));
                            track.setEvent(c.getString("status"));
                            track.setMessage(c.getString("description"));
                            track.setTime(c.getString("from"));
                            track.setTracker_id(c.getString("to"));
                            track.setAddress(c.getString("label"));

                            JSONObject location = c.getJSONObject("location");
                            track.setLat(location.getString("lat"));
                            track.setLng(location.getString("lng"));
                            track.setNewAddress(location.getString("address"));
                            track.setRadious(location.getString("radius"));

                            arrTrackingHistory.add(track);
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
            if (AssignTaskMain.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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
                AdptorAssignTask adapter = new AdptorAssignTask(context, arrTrackingHistory);
                listTrackingHistory.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listTrackingHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }
}

