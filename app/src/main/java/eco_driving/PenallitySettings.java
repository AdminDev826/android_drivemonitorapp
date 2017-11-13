package eco_driving;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;
import com.lineztech.farhan.vehicaltarckingapp.StartingActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import dashboard.Trackers;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.Utils;

/**
 * Created by Farhan on 8/4/2016.
 */
public class PenallitySettings extends Activity {
    LinearLayout idllSpeeding, idllSpeedingInner, idllIDling, idllIdleInner, idllHarshDriving, idllHarshDrivingInner;
    TextView idbtnGReport;
    Context context;
    //first layout
    String reportID;
    String tID;
    String status;
    String loginURL;
    String isReportGenratedURL;
    SeekBar seekBarS1, seekBarS2, seekBarS3, seekBarS4;
    TextView iddtvS1, iddtvS2, iddtvS3, iddtvS4;
    EditText iddetSL;

    ProgressDialog progressDialog;
    //second layout
    SeekBar seekBarHD1, seekBarHD2, seekBarHD3, seekBarHD4, seekBarHD5;
    TextView idtvHD1, idtvHD2, idtvHD3, idtvHD4, idtvHD5;   //second layout
    String percent_ready;
    //third layout
    SeekBar seekBarHA1;
    TextView idtvHA1;
    EditText idetHA1;


    boolean isSpeedingLayoutVisible = false;
    boolean isHDingLayoutVisible = false;
    boolean isIdleLayoutVisible = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.penalty_settings);

        idllSpeeding = (LinearLayout) findViewById(R.id.idllSpeeding);
        idllSpeedingInner = (LinearLayout) findViewById(R.id.idllSpeedingInner);
        idllIDling = (LinearLayout) findViewById(R.id.idllIDling);
        idllIdleInner = (LinearLayout) findViewById(R.id.idllIdleInner);
        idllHarshDrivingInner = (LinearLayout) findViewById(R.id.idllHarshDrivingInner);
        idllHarshDriving = (LinearLayout) findViewById(R.id.idllHarshDriving);
        idbtnGReport = (TextView) findViewById(R.id.idbtnGReport);
        context = this;
        //first layout
        seekBarS1 = (SeekBar) findViewById(R.id.seekBarS1);
        seekBarS2 = (SeekBar) findViewById(R.id.seekBarS2);
        seekBarS3 = (SeekBar) findViewById(R.id.seekBarS3);
        seekBarS4 = (SeekBar) findViewById(R.id.seekBarS4);
        iddtvS1 = (TextView) findViewById(R.id.iddtvS1);
        iddtvS2 = (TextView) findViewById(R.id.iddtvS2);
        iddtvS3 = (TextView) findViewById(R.id.iddtvS3);
        iddtvS4 = (TextView) findViewById(R.id.iddtvS4);
        iddetSL = (EditText) findViewById(R.id.iddetSL);

        //second layout
        seekBarHD1 = (SeekBar) findViewById(R.id.seekBarHD1);
        seekBarHD2 = (SeekBar) findViewById(R.id.seekBarHD2);
        seekBarHD3 = (SeekBar) findViewById(R.id.seekBarHD3);
        seekBarHD4 = (SeekBar) findViewById(R.id.seekBarHD4);
        seekBarHD5 = (SeekBar) findViewById(R.id.seekBarHD5);
        idtvHD1 = (TextView) findViewById(R.id.idtvHD1);
        idtvHD2 = (TextView) findViewById(R.id.idtvHD2);
        idtvHD3 = (TextView) findViewById(R.id.idtvHD3);
        idtvHD4 = (TextView) findViewById(R.id.idtvHD4);
        idtvHD5 = (TextView) findViewById(R.id.idtvHD5);

        //third layout
        seekBarHA1 = (SeekBar) findViewById(R.id.seekBarHA1);
        idtvHA1 = (TextView) findViewById(R.id.idtvHA1);
        idetHA1 = (EditText) findViewById(R.id.idetHA1);


        idllSpeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSpeedingLayoutVisible == false) {
                    idllSpeedingInner.setVisibility(View.VISIBLE);
                    isSpeedingLayoutVisible = true;
                } else {
                    idllSpeedingInner.setVisibility(View.GONE);
                    isSpeedingLayoutVisible = false;
                }

            }
        });


        idllHarshDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isHDingLayoutVisible == false) {
                    idllHarshDrivingInner.setVisibility(View.VISIBLE);
                    isHDingLayoutVisible = true;
                } else {
                    idllHarshDrivingInner.setVisibility(View.GONE);
                    isHDingLayoutVisible = false;
                }

            }
        });


        idllIDling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isIdleLayoutVisible == false) {
                    idllIdleInner.setVisibility(View.VISIBLE);
                    isIdleLayoutVisible = true;
                } else {
                    idllIdleInner.setVisibility(View.GONE);
                    isIdleLayoutVisible = false;
                }

            }
        });


        seekBarS1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                iddtvS1.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarS2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                iddtvS2.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarS3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                iddtvS3.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });

        seekBarS4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                iddtvS4.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarHD1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                idtvHD1.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarHD2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                idtvHD2.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarHD3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                idtvHD3.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarHD4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                idtvHD4.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarHD5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                idtvHD5.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        seekBarHA1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                idtvHA1.setText(paramInt + ""); // here in textView the percent will be shown
            }
        });


        idbtnGReport.setOnClickListener(new View.OnClickListener() {
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

                String from = URLEncoder.encode("2016-08-01 00:00:00", "utf-8");
                String to = URLEncoder.encode("2016-09-16 23:59:59", "utf-8");
                String geocoder = URLEncoder.encode("osm", "utf-8");
                String trackers = URLEncoder.encode("[100040,113483,114013]", "utf-8");
                String type = URLEncoder.encode("service", "utf-8");
//                String time_filter = URLEncoder.encode(, "utf-8");
                String plugin = URLEncoder.encode(String.valueOf(paramsPlugin), "utf-8");
                String hashcode = Utils.getPreferences("hashCode", context);
                loginURL = "https://api.navixy.com/v2/report/tracker/generate?hash=" + hashcode + "" +
                        "&from=" + from + "&to=" + to + "&geocoder=" + geocoder + "&" +
                        "trackers=["+tID+"]&type=service&time_filter={%22from%22:%2200:00:00%22,%22to%22:%2223:59:59%22," +
                        "%22weekdays%22:[1,2,3,4,5,6,7]}&plugin={%22plugin_id%22:46,%22harsh_driving_penalties%22:" +
                        "{%22harshAcceleration%22:" + idtvHD1.getText().toString() + ",%22harshBraking%22:" + idtvHD2.getText().toString() + ",%22harshTurn%22:" + idtvHD3.getText().toString() + "," +
                        "%22harshAccelerationNTurn%22:" + idtvHD4.getText().toString() + ",%22harshBrakingNTurn%22:" + idtvHD5.getText().toString() + "},%22speeding_penalties%22:" +
                        "{%2210%22:" + iddtvS1.getText().toString() + ",%2220%22:" + iddtvS2.getText().toString() + ",%2230%22:" + iddtvS3.getText().toString() + ",%2250%22:" + iddtvS4.getText().toString() + "},%22speed_limit%22:" + iddetSL.getText().toString() + ",%22idling_penalty%22:" +
                        "" + idtvHA1.getText().toString() + ",%22min_idling_duration%22:" + idetHA1.getText().toString() + ",%22min_speeding_duration%22:" + iddetSL.getText().toString() + ",%22use_vehicle_speed_limit%22" +
                        ":true}";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(loginURL, ServiceHandler.GET);
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
            if (PenallitySettings.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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
            if (PenallitySettings.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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
                        intent.putExtra("reportID",reportID);
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
