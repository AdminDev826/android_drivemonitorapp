package eco_driving;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;
import com.lineztech.farhan.vehicaltarckingapp.StartingActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dashboard.Trackers;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.Utils;


/**
 * Created by Farhan on 8/4/2016.
 */
public class EcoDriving extends Activity implements DatePickerDialog.OnDateSetListener {
    LinearLayout idllPenaltySettings;
    Context context;
    TextView idbtnGReportMain;
    ProgressDialog dialog;
    String status;
    String loginURL;
    TextView idtvMo, idtvSu, idtvTu, idtvWe, idtvSa, idtvTh, idtvFr;
    TextView idtvDateRange1, idtvDateRange2;
    boolean isMoCheck = true;
    boolean is2Check = true;
    boolean isStartDate = true;
    boolean is3Check = true;
    boolean is4Check = true;
    boolean is5Check = true;
    boolean is6Check = true;
    boolean is7Check = true;
    String tID;
    ProgressDialog progressDialog;
    String reportID;
    String percent_ready;
    String isReportGenratedURL;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.eco_driving);
        context = this;
        idllPenaltySettings = (LinearLayout) findViewById(R.id.idllPenaltySettings);
        idtvMo = (TextView) findViewById(R.id.idtvMo);
        idtvTu = (TextView) findViewById(R.id.idtvTu);
        idtvWe = (TextView) findViewById(R.id.idtvWe);
        idtvTh = (TextView) findViewById(R.id.idtvTh);
        idtvFr = (TextView) findViewById(R.id.idtvFr);
        idtvSa = (TextView) findViewById(R.id.idtvSa);
        idtvSu = (TextView) findViewById(R.id.idtvSu);
        idtvDateRange1 = (TextView) findViewById(R.id.idtvDateRange1);
        idtvDateRange2 = (TextView) findViewById(R.id.idtvDateRange2);
        idbtnGReportMain = (TextView) findViewById(R.id.idbtnGReportMain);

        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        final Date dateobj = new Date();
        Log.d("date ", "" + df.format(dateobj));
        idtvDateRange1.setText("" + df.format(dateobj));
        idtvDateRange2.setText("" + df.format(dateobj));

        // Show a datepicker when the dateButton is clicked
        idtvDateRange1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = false;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EcoDriving.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.setAccentColor(Color.parseColor("#9C27B0"));
                dpd.setTitle("DatePicker Title");
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });



        // Show a datepicker when the dateButton is clicked
        idtvDateRange2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EcoDriving.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                    dpd.setAccentColor(Color.parseColor("#9C27B0"));
                    dpd.setTitle("DatePicker Title");
                dpd.show(getFragmentManager(), "Datepickerdialog");

            }
        });



        idtvMo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMoCheck == true) {
                    idtvMo.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    isMoCheck = false;
                } else {
                    idtvMo.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    isMoCheck = true;
                }

            }
        });


        idtvTu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is2Check == true) {
                    idtvTu.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is2Check = false;
                } else {
                    idtvTu.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is2Check = true;
                }

            }
        });


        idtvWe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is3Check == true) {
                    idtvWe.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is3Check = false;
                } else {
                    idtvWe.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is3Check = true;
                }

            }
        });


        idtvTh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is4Check == true) {
                    idtvTh.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is4Check = false;
                } else {
                    idtvTh.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is4Check = true;
                }

            }
        });


        idtvFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is5Check == true) {
                    idtvFr.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is5Check = false;
                } else {
                    idtvFr.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is5Check = true;
                }

            }
        });


        idtvSa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is6Check == true) {
                    idtvSa.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is6Check = false;
                } else {
                    idtvSa.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is6Check = true;
                }

            }
        });


        idtvSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is7Check == true) {
                    idtvSu.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is7Check = false;
                } else {
                    idtvSu.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is7Check = true;
                }

            }
        });


        // Setup the new range seek bar
        final RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(this);
        // Set the range
        rangeSeekBar.setRangeValues(00, 24);
        rangeSeekBar.setSelectedMinValue(1);
        rangeSeekBar.setSelectedMaxValue(23);
        rangeSeekBar.getSelectedMinValue();
        rangeSeekBar.getSelectedMaxValue();
        rangeSeekBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Value: " + rangeSeekBar.getSelectedMinValue(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplication(), "Value: " + rangeSeekBar.getSelectedMaxValue(), Toast.LENGTH_LONG).show();
            }
        });


        idllPenaltySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PenallitySettings.class);
                startActivity(intent);
            }
        });

        LinearLayout layout = (LinearLayout) findViewById(R.id.seekbar_placeholder);
        layout.addView(rangeSeekBar);


        idbtnGReportMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                status = "";
                reportID = "";
                progressDialog = ProgressDialog.show(context, "",
                        "Generating Report...", true);
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();
            }
        });


    }


    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute;
        idtvDateRange1.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = ""+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

        if (isStartDate){
            idtvDateRange2.setText(date);
        }else {
            idtvDateRange1.setText(date);
        }



    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            String hash = "42283600130d5f2c1cf02ac5420ff8e3";
            JSONObject paramsTime_filter = new JSONObject();
            try {
                paramsTime_filter.put("from", "00:00:00");
                paramsTime_filter.put("to", "23:59:59");
                paramsTime_filter.put("weekdays", "[1,2,3,4,5,6,7]");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject paramsPlugin = new JSONObject();
            try {
                paramsPlugin.put("plugin_id", "46");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {


                List<Trackers> trackerList;
                DatabaseHandler db = new DatabaseHandler(context);
                trackerList = db.getTrackerList();
                for (int i = 0; i < trackerList.size(); i++) {
                     tID = tID+","+ trackerList.get(i).getTrackerID();
                }
                tID = tID.substring(5);

                String from = URLEncoder.encode("2016-07-01 00:00:00", "utf-8");
                String to = URLEncoder.encode("2016-09-16 23:59:59", "utf-8");
                String geocoder = URLEncoder.encode("osm", "utf-8");
                String trackers = URLEncoder.encode("[90824,100040]", "utf-8");
                String type = URLEncoder.encode("service", "utf-8");
//                String time_filter = URLEncoder.encode(, "utf-8");
                String plugin = URLEncoder.encode(String.valueOf(paramsPlugin), "utf-8");
                String hashcode = Utils.getPreferences("hashCode", context);
                loginURL = "https://api.navixy.com/v2/report/tracker/generate?hash=" + hashcode + "" +
                        "&from=" + from + "&to=" + to + "&geocoder=" + geocoder + "&" +
                        "trackers=["+tID+"]&type=service&time_filter={%22from%22:%2200:00:00%22,%22to%22:%2223:59:59%22," +
                        "%22weekdays%22:[1,2,3,4,5,6,7]}&plugin={%22plugin_id%22:46}";

                Log.d("URL: ", "> " + loginURL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(loginURL, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    reportID = jsonObj.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (EcoDriving.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            try {

                if (status.equals("true")) {
                    Utils.savePreferences("reportID", reportID, context);

                    AsyncIsReportGenrated runner = new AsyncIsReportGenrated();
                    runner.execute();

                } else {
                    Toast.makeText(context, "Problem in generating report", Toast.LENGTH_LONG).show();


                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    progressDialog.dismiss();
                    progressDialog = null;

                }
            } catch (Exception e) {
                e.printStackTrace();

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
                progressDialog = null;
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }

    private class AsyncIsReportGenrated extends AsyncTask<String, String, String> {
        protected void onPreExecute() {

            String hashCode = Utils.getPreferences("hashCode", context);

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
            if (EcoDriving.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }


            try {

                if (status.equals("true")) {
                    if (percent_ready.equals("100")) {

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();
                        progressDialog = null;
                        Intent intent = new Intent(context, StackBarChartt.class);
                        intent.putExtra("reportID", reportID);
                        startActivity(intent);
                    } else {
                        AsyncIsReportGenrated runner = new AsyncIsReportGenrated();
                        runner.execute();
                    }


                } else {
                    Toast.makeText(context, "Problem in generating report", Toast.LENGTH_LONG).show();


                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    progressDialog.dismiss();
                    progressDialog = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
                progressDialog = null;
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }


}
