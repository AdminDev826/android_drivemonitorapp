package dashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lineztech.farhan.vehicaltarckingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import tracking_history.AdapterTrackingHistory;
import tracking_history.Track;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Dev on 2/28/2017.
 */
public class TripsFragment extends Fragment {

    View view, layout;
    Context context;
    ListView listTrackingHistory;
    ProgressDialog progressDialog;
    Spinner idspSelectTimeDuration;
    ArrayList arrTrackingHistory;
    String urltrackingHistory;
    String hashCode, TrackerID = "37712";
    String currentTime = "";
    AdapterTrackingHistory adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null) return view;
        view = inflater.inflate(R.layout.trips_layout, container, false);
        layout = inflater.inflate(R.layout.select_period_dialog_layout, container, false);
        initView();
        return view;
    }

    private void initView() {
        context = getActivity();

        listTrackingHistory = (ListView) view.findViewById(R.id.idlvTrackingHistory);
        idspSelectTimeDuration = (Spinner) view.findViewById(R.id.idspSelectTimeDuration);
        arrTrackingHistory = new ArrayList<String>();
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = Utils.getPreferences("TrackerID", context);

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
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
            }
        });
    }

    private void loadData(){
        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
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
                        loadData();
//                        Toast.makeText(context, "Server Error !", Toast.LENGTH_SHORT).show();
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
//        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    public void today() {
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        String sdate = curYear + "-" + curMonth + "-" + curDay + " 00:00:00";
        String edate = curYear + "-" + curMonth + "-" + curDay + " 23:59:59";
        try {
            arrTrackingHistory.clear();
            sdate = URLEncoder.encode(sdate, "utf-8");
            edate = URLEncoder.encode(edate, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list?tracker_id=" + TrackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;
            loadData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void yesterday() {
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        String edate = curYear + "-" + curMonth + "-" + curDay + " 23:59:59";
        cal.add(Calendar.DATE, -1);
        int preYear = cal.get(Calendar.YEAR);
        int preMonth = cal.get(Calendar.MONTH) + 1;
        int preDay = cal.get(Calendar.DAY_OF_MONTH);
        String sdate = preYear + "-" + preMonth + "-" + preDay + " 00:00:00";
        try {
            arrTrackingHistory.clear();
            sdate = URLEncoder.encode(sdate, "utf-8");
            edate = URLEncoder.encode(edate, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;
            loadData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void thisWeek() {
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        String edate = curYear + "-" + curMonth + "-" + curDay + " 23:59:59";
//        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//        int preYear = cal.get(Calendar.YEAR);
//        int preMonth = cal.get(Calendar.MONTH) + 1;
//        int preDay = cal.get(Calendar.DAY_OF_MONTH);

        cal.add(Calendar.DATE, -7);
        int preYear = cal.get(Calendar.YEAR);
        int preMonth = cal.get(Calendar.MONTH) + 1;
        int preDay = cal.get(Calendar.DAY_OF_MONTH);

        String sdate = preYear + "-" + preMonth + "-" + preDay + " 00:00:00";

        try {
            arrTrackingHistory.clear();
            sdate = URLEncoder.encode(sdate, "utf-8");
            edate = URLEncoder.encode(edate, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;
            loadData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void thisMonth() {
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        int curMonth = cal.get(Calendar.MONTH) + 1;
        int curDay = cal.get(Calendar.DAY_OF_MONTH);
        String edate = curYear + "-" + curMonth + "-" + curDay + " 23:59:59";
//        String sdate = curYear + "-" + curMonth + "-" + "1 00:00:00";
        cal.add(Calendar.DATE, -30);
        int preYear = cal.get(Calendar.YEAR);
        int preMonth = cal.get(Calendar.MONTH) + 1;
        int preDay = cal.get(Calendar.DAY_OF_MONTH);

        String sdate = preYear + "-" + preMonth + "-" + preDay + " 00:00:00";

        try {
            arrTrackingHistory.clear();
            sdate = URLEncoder.encode(sdate, "utf-8");
            edate = URLEncoder.encode(edate, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/track/list/?tracker_id=" + TrackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;
            loadData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
   }
}
