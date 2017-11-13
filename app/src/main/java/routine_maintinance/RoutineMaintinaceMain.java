package routine_maintinance;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import dashboard.DashboardFragment1;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/29/2016.
 */
public class RoutineMaintinaceMain extends Activity {
    Context context;
    public static TextView txt_scheduled_cost, txt_spent_cost;
    ListView listTrackingHistory;
    AdptorRoutineMaintinance adapter;
    ProgressDialog progressDialog;
    ArrayList<RoutineMaintinance> arrRoutinemaintinence;
//    ArrayList<RoutineMaintinance> savedData = new ArrayList<>();
    LinearLayout idllDays;
    String urlMaintinenceDate;
    String currentTime;
    String hashCode, TrackerID = "37712";
    String vehicle_id = "";
    public static double scheduled_cost = 0.0, spent_cost = 0.0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.routine_maintenence_main);
        listTrackingHistory = (ListView) findViewById(R.id.idlvTrackingHistory);
        idllDays = (LinearLayout) findViewById(R.id.idllDays);
        txt_scheduled_cost = (TextView) findViewById(R.id.routine_maintenance_txt_scheduled);
        txt_spent_cost = (TextView) findViewById(R.id.routine_maintenance_spent);
        Button btnCreate = (Button) findViewById(R.id.maintenance_create);
        context = this;
        arrRoutinemaintinence = new ArrayList<>();
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = Utils.getPreferences("TrackerID", context);
        vehicle_id = getIntent().getStringExtra("vehicle_id");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vehicle_id.length() < 1){
                    Toast.makeText(context, "Please create car info !", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(context, MaintenanceActivity.class);
                i.putExtra("vehicle_id", vehicle_id);
                startActivity(i);
            }
        });
        listTrackingHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listTrackingHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alertDialog = new AlertDialog.Builder(RoutineMaintinaceMain.this).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Are you sure to delete this item ?");
                alertDialog.setIcon(R.drawable.ic_launcher);

                alertDialog.setButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRow(position);
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

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        currentTime = dateTime.replace("/", "-");
        try {
            currentTime = URLEncoder.encode(currentTime, "utf-8");
            urlMaintinenceDate = "https://api.navixy.com/v2/vehicle/service_task/list?hash=" + hashCode;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        DatabaseHandler dbb = new DatabaseHandler(context);
//        savedData = dbb.getCostsByID(TrackerID);
//        aryMaintenance = dbb.getCostsByID(TrackerID);
//        dbb.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    private void deleteRow(final int position) {
        RoutineMaintinance item = (RoutineMaintinance)arrRoutinemaintinence.get(position);
        String taskID = item.getId();
        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        String url = "https://api.navixy.com/v2/vehicle/service_task/delete?hash=" + hashCode + "&task_id=" + taskID;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                setRemoveData(arrRoutinemaintinence.get(position));
                                arrRoutinemaintinence.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(context, "Delete Success !", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(context, "Server Error ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    void setRemoveData(RoutineMaintinance removedData){
//        DatabaseHandler db = new DatabaseHandler(context);
//        boolean isSaved = db.isExistTask(removedData.getId());
//        if(isSaved){
//            db.removeTask(removedData.getId());
//            removedData.setStatus("");
//            DashboardFragment1.updateMaintenance(removedData);
//            double tmp = Double.parseDouble(removedData.getCost());
//            spent_cost -= tmp;
//            txt_spent_cost.setText("$"+spent_cost);
//        }
//        else
        if(removedData.getStatus().equals("created")){
            double tmp = Double.parseDouble(removedData.getCost());
            scheduled_cost -= tmp;
            txt_scheduled_cost.setText(scheduled_cost + "$");
        }else if(removedData.getStatus().equals("done")){
            double tmp = Double.parseDouble(removedData.getCost());
            spent_cost -= tmp;
            txt_spent_cost.setText("$"+spent_cost);
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            scheduled_cost = 0.0;
            spent_cost = 0.0;
            arrRoutinemaintinence.clear();
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
                            String cTrackerID = c.getString("vehicle_id");
                            if(cTrackerID.equals(vehicle_id)) {
                                RoutineMaintinance routineMaintinance = new RoutineMaintinance();
                                routineMaintinance.setId(c.getString("id"));
                                routineMaintinance.setVehicle_id(cTrackerID);
                                String cStatus = c.getString("status");
                                routineMaintinance.setStatus(cStatus);
                                JSONObject prediction = c.getJSONObject("prediction");
                                routineMaintinance.setEnd_date(prediction.getString("end_date"));
                                routineMaintinance.setWear_percentage(prediction.getString("wear_percentage"));

                                routineMaintinance.setDescription(c.getString("description"));
                                String str_cost = c.getString("cost");
                                double tmp = Double.parseDouble(str_cost);
                                if(cStatus.equals("created")){
                                    scheduled_cost += tmp;
                                }else if(cStatus.equals("done")){
                                    spent_cost += tmp;
                                }
                                routineMaintinance.setCost(str_cost);
                                routineMaintinance.setVehicle_label(c.getString("vehicle_label"));
                                routineMaintinance.setCompletion_date(c.getString("completion_date"));
                                arrRoutinemaintinence.add(routineMaintinance);
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
            if (RoutineMaintinaceMain.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            try {
                if (arrRoutinemaintinence.isEmpty()) {
                    Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
                }else{
                    Collections.sort(arrRoutinemaintinence, new Comparator<RoutineMaintinance>() {
                        public int compare(RoutineMaintinance o1, RoutineMaintinance o2) {
                            try {
                                return getDate(o2.getEnd_date()).compareTo(getDate(o1.getEnd_date()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });

                    txt_scheduled_cost.setText("$"+scheduled_cost);
                    txt_spent_cost.setText("$"+spent_cost);
                    adapter = new AdptorRoutineMaintinance(context, arrRoutinemaintinence, TrackerID, hashCode);
                    listTrackingHistory.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        protected void onProgressUpdate(String... text) {
        }
    }

    private Date getDate(String end_date) throws ParseException {
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tmp = format.parse(end_date);
        return tmp;
    }

//    private boolean isSaved(String id) {
//        for(RoutineMaintinance routineMaintinance : savedData){
//            if(routineMaintinance.getId().equals(id)){
//                return true;
//            }
//        }
//        return false;
//    }
}

