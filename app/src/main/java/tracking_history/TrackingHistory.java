package tracking_history;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;
import com.lineztech.farhan.vehicaltarckingapp.UserInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/29/2016.
 */
public class TrackingHistory extends Activity {
    Context context;
    ListView listTrackingHistory;
    ProgressDialog progressDialog;
    Spinner idspSelectTimeDuration;
    ArrayList arrTrackingHistory;
    String urltrackingHistory;
    String currentTime;
    String hashCode, TrackerID = "37712";
    AdapterTrackingHistory adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.tracking_history);
        listTrackingHistory = (ListView) findViewById(R.id.idlvTrackingHistory);
        idspSelectTimeDuration = (Spinner) findViewById(R.id.idspSelectTimeDuration);
        context = this;
        arrTrackingHistory = new ArrayList<String>();
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = getIntent().getStringExtra("trackerID");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String temp  = dateFormat.format(date);
        try {
            currentTime = URLEncoder.encode(temp, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        idspSelectTimeDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0:
                        today();
                        break;
                    case 1:
                        yesterday();
                        break;
                    case 2:
                        thisWeek();
                        break;
                    case 3:
                        thisMonth();
                        break;
                    case 4:
                        showCustomPeriod();
                        break;
                    default:
                        today();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showCustomPeriod() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        AlertDialog alertView = null;
        final View layout = getLayoutInflater().inflate(R.layout.select_period_dialog_layout, null);
        final EditText startDate = (EditText) layout.findViewById(R.id.date_dialog_startdate);
        final EditText endDate = (EditText) layout.findViewById(R.id.date_dialog_enddate);
        TextView txtOK = (TextView) layout.findViewById(R.id.date_dialog_confirm);
        alertDialog.setView(layout);
        alertView = alertDialog.create();
        alertView.show();
        alertView.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final AlertDialog finalAlertView = alertView;
        txtOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sdate = startDate.getText().toString();
                String edate = endDate.getText().toString();
//                if(!isLegalDate(sdate)){
//                    startDate.setText("");
//                    Toast.makeText(context, "Input a valid Date !", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if(!isLegalDate(edate)){
//                    endDate.setText("");
//                    Toast.makeText(context, "Input a valid Date !", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                try{

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = formatter.parse(sdate);
                    Date date2 = formatter.parse(edate);
                    if (date2.compareTo(date1)<0)
                    {
                        Toast.makeText(context, "Input date correctly !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Input date correctly !", Toast.LENGTH_SHORT).show();
                    return;
                }
                sdate = sdate + " 00:00:00";
                edate = edate + " 23:59:59";
                try {
                    sdate = URLEncoder.encode(sdate, "utf-8");
                    edate = URLEncoder.encode(edate, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                arrTrackingHistory.clear();
                urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;
                progressDialog = ProgressDialog.show(context, "",
                        "Loading...", true);
                loadData();
                finalAlertView.dismiss();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
            }
        });
    }
    boolean isLegalDate(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        return sdf.parse(s, new ParsePosition(0)) != null;
    }

    private void loadData(){
        arrTrackingHistory.clear();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, urltrackingHistory, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                for (int i = 0; i < listtrackingHistory.length(); i++) {
                                    JSONObject c = listtrackingHistory.getJSONObject(i);
                                    Track track = new Track();
                                    track.setStart_address(c.getString("start_address"));
                                    track.setEnd_address(c.getString("end_address"));
                                    track.setStart_date(c.getString("start_date"));
                                    track.setEnd_date(c.getString("end_date"));
                                    track.setLength(c.getString("length"));
                                    arrTrackingHistory.add(track);
                                }
                                updateUI();
                            }else{
                                try{
                                    JSONObject jsonMSG = response.getJSONObject("status");
                                    String msg = jsonMSG.getString("description");
                                    Toast.makeText(context, "Fail !\n"+msg, Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    Toast.makeText(context, "Fail !\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
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
                        progressDialog.dismiss();
                        updateUI();
//                        Toast.makeText(context, "Server Error !", Toast.LENGTH_SHORT).show();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void updateUI() {
        if (arrTrackingHistory.isEmpty()) {
            listTrackingHistory.setAdapter(null);
            try {
                adapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList sendData = new ArrayList<>();
            for (int i = arrTrackingHistory.size() - 1; i > -1 ; i--) {
                sendData.add(arrTrackingHistory.get(i));
            }
            adapter = new AdapterTrackingHistory(context, sendData, hashCode, TrackerID);
            listTrackingHistory.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            arrTrackingHistory.clear();
        }
        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(urltrackingHistory, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String success = jsonObj.getString("success");
                    if (success.equals("true")) {
                        JSONArray listtrackingHistory = jsonObj.getJSONArray("list");
                        for (int i = 0; i < listtrackingHistory.length(); i++) {
                            JSONObject c = listtrackingHistory.getJSONObject(i);
                            Track track = new Track();
                            track.setStart_address(c.getString("start_address"));
                            track.setEnd_address(c.getString("end_address"));
                            track.setStart_date(c.getString("start_date"));
                            track.setEnd_date(c.getString("end_date"));
                            track.setLength(c.getString("length"));
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
            if (TrackingHistory.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                if (arrTrackingHistory.isEmpty()) {
                    listTrackingHistory.setAdapter(null);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new AdapterTrackingHistory(context, arrTrackingHistory, hashCode, TrackerID);
                    listTrackingHistory.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }

    public void today() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String newTime = dateFormat.format(cal.getTime());
        try {
            arrTrackingHistory.clear();
            newTime = URLEncoder.encode(newTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list?tracker_id=" + TrackerID + "&from=" + newTime + "&to=" + currentTime + "&hash=" + hashCode;
            progressDialog = ProgressDialog.show(context, "",
                    "Loading...", true);
//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            runner.execute();
            loadData();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(newTime);

    }


    public void yesterday() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        String newTime = dateFormat.format(cal.getTime());
        try {
            arrTrackingHistory.clear();
            newTime = URLEncoder.encode(newTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + newTime + "&to=" + currentTime + "&hash=" + hashCode;

            progressDialog = ProgressDialog.show(context, "",
                    "Loading...", true);
            loadData();
//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            runner.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(newTime);

    }

    public void thisWeek() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        String newTime = dateFormat.format(cal.getTime());
        try {
            arrTrackingHistory.clear();
            newTime = URLEncoder.encode(newTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + newTime + "&to=" + currentTime + "&hash=" + hashCode;

            progressDialog = ProgressDialog.show(context, "",
                    "Loading...", true);
            loadData();
//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            runner.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(newTime);

    }

    public void thisMonth() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        String newTime = dateFormat.format(cal.getTime());
        try {
            arrTrackingHistory.clear();
            newTime = URLEncoder.encode(newTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + newTime + "&to=" + currentTime + "&hash=" + hashCode;

            progressDialog = ProgressDialog.show(context, "",
                    "Loading...", true);
            loadData();
//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            runner.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(newTime);
    }

    public void customPeriod() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -120);
        String newTime = dateFormat.format(cal.getTime());
        try {
            arrTrackingHistory.clear();
            newTime = URLEncoder.encode(newTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + newTime + "&to=" + currentTime + "&hash=" + hashCode;
            progressDialog = ProgressDialog.show(context, "",
                    "Loading...", true);
            loadData();
//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            runner.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(newTime);
    }
}