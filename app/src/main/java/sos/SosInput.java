package sos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.List;

import assing_task.RSSPullService;
import io.fabric.sdk.android.Fabric;
import util.Utils;

/**
 * Created by Farhan on 7/25/2016.
 */
public class SosInput extends Activity {
    TextView idtvSave;
    String loginURL;
    ProgressDialog progressDialog;
    EditText idetMessage, idetCallNotification;
    EditText idetSMSNotification, idetSMSNotification1, idetSMSNotification2, idetSMSNotification3, idetSMSNotification4;
    TextView idtvAddSmS, idtvAddSmS1, idtvAddSmS2, idtvAddSmS3;
    LinearLayout idllSMS, idllSMS1, idllSMS2, idllSMS3;


    EditText idetEmailNotification, idetEmailNotification1, idetEmailNotification2, idetEmailNotification3, idetEmailNotification4;
    TextView idtvAddEmail, idtvAddEmail1, idtvAddEmail2, idtvAddEmail3;
    LinearLayout idllEmail, idllEmail1, idllEmail2, idllEmail3;

    Context context;
    String status;
    String hashCode;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.sos_input);
        context = this;
        idetMessage = (EditText) findViewById(R.id.idetMessage);
        idetSMSNotification = (EditText) findViewById(R.id.idetSMSNotification);
        idetSMSNotification1 = (EditText) findViewById(R.id.idetSMSNotification1);
        idetSMSNotification2 = (EditText) findViewById(R.id.idetSMSNotification2);
        idetSMSNotification3 = (EditText) findViewById(R.id.idetSMSNotification3);
        idetSMSNotification4 = (EditText) findViewById(R.id.idetSMSNotification4);

        idtvAddSmS = (TextView) findViewById(R.id.idtvAddSmS);
        idtvAddSmS1 = (TextView) findViewById(R.id.idtvAddSmS1);
        idtvAddSmS2 = (TextView) findViewById(R.id.idtvAddSmS2);
        idtvAddSmS3 = (TextView) findViewById(R.id.idtvAddSmS3);

        idllSMS = (LinearLayout) findViewById(R.id.idllSMS);
        idllSMS1 = (LinearLayout) findViewById(R.id.idllSMS1);
        idllSMS2 = (LinearLayout) findViewById(R.id.idllSMS2);
        idllSMS3 = (LinearLayout) findViewById(R.id.idllSMS3);


        idetEmailNotification = (EditText) findViewById(R.id.idetEmailNotification);

        idetEmailNotification.setEnabled(false);
        idetEmailNotification.setText(" ");

        idetEmailNotification1 = (EditText) findViewById(R.id.idetEmailNotification1);
        idetEmailNotification2 = (EditText) findViewById(R.id.idetEmailNotification2);
        idetEmailNotification3 = (EditText) findViewById(R.id.idetEmailNotification3);
        idetEmailNotification4 = (EditText) findViewById(R.id.idetEmailNotification4);

        idtvAddEmail = (TextView) findViewById(R.id.idtvAddEmail);
        idtvAddEmail1 = (TextView) findViewById(R.id.idtvAddEmail1);
        idtvAddEmail2 = (TextView) findViewById(R.id.idtvAddEmail2);
        idtvAddEmail3 = (TextView) findViewById(R.id.idtvAddEmail3);

        idllEmail = (LinearLayout) findViewById(R.id.idllEmail);
        idllEmail1 = (LinearLayout) findViewById(R.id.idllEmail1);
        idllEmail2 = (LinearLayout) findViewById(R.id.idllEmail2);
        idllEmail3 = (LinearLayout) findViewById(R.id.idllEmail3);


        idtvAddSmS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllSMS.setVisibility(View.VISIBLE);
            }
        });

        idtvAddSmS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllSMS1.setVisibility(View.VISIBLE);
            }
        });

        idtvAddSmS2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllSMS2.setVisibility(View.VISIBLE);
            }
        });

        idtvAddSmS3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllSMS3.setVisibility(View.VISIBLE);
            }
        });


        idtvAddEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                idllEmail.setVisibility(View.VISIBLE);
            }
        });

        idtvAddEmail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllEmail1.setVisibility(View.VISIBLE);
            }
        });

        idtvAddEmail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllEmail2.setVisibility(View.VISIBLE);
            }
        });

        idtvAddEmail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idllEmail3.setVisibility(View.VISIBLE);
            }
        });


        idetCallNotification = (EditText) findViewById(R.id.idetCallNotification);

        idtvSave = (TextView) findViewById(R.id.idtvSave);

        idtvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "";
                hashCode = "";
                String strSMS1 = idetSMSNotification.getText().toString();
                String strSMS2 = idetSMSNotification1.getText().toString();
                String strSMS3 = idetSMSNotification2.getText().toString();
                String strSMS4 = idetSMSNotification3.getText().toString();
                String strSMS5 = idetSMSNotification4.getText().toString();

                String sosPhoneCallNumber = idetCallNotification.getText().toString();
                if (idetMessage.getText().length() == 0) {
                    Toast.makeText(context, "Please enter the message...", Toast.LENGTH_LONG).show();
                } else {
//                    progressDialog = ProgressDialog.show(context, "",
//                            "Saving SOS...", true);
//                    AsyncTaskRunner runner = new AsyncTaskRunner();
//                    runner.execute();

                    String str_SMSs = "";
                    if (strSMS1.length() > 0)
                        str_SMSs += strSMS1;
                    if (strSMS2.length() > 0)
                        str_SMSs += ", " + strSMS2;
                    if (strSMS3.length() > 0)
                        str_SMSs += ", " + strSMS3;
                    if (strSMS4.length() > 0)
                        str_SMSs += ", " + strSMS4;
                    if (strSMS5.length() > 0)
                        str_SMSs += ", " + strSMS5;

                    if (str_SMSs.equals("")) {
                        Toast.makeText(context, "Please enter the Phone Number ...", Toast.LENGTH_LONG).show();
                    } else {
                        Utils.savePreferences("sos_message", idetMessage.getText().toString(), context);
                        Utils.savePreferences("sos_sms_numbers", str_SMSs, context);
                        Utils.savePreferences("sos_phone_call", sosPhoneCallNumber, context);
                        Toast.makeText(context, "Success !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loadDemo();
    }

    private void loadDemo() {
        String message = Utils.getPreferences("sos_message", context);
        String phone_numbers = Utils.getPreferences("sos_sms_numbers", context);
        String phoneCallNumber = Utils.getPreferences("sos_phone_call", context);
        Log.e("p", " " + phone_numbers);
        String[] smsNumArray = phone_numbers.split(", ");

        idetMessage.setText(message);
        idetCallNotification.setText(phoneCallNumber);
        if (smsNumArray.length > 0) {
            idetSMSNotification.setText(smsNumArray[0]);
            if (smsNumArray.length > 1) {
                idetSMSNotification1.setText(smsNumArray[1]);
                idllSMS.setVisibility(View.VISIBLE);
                if (smsNumArray.length > 2) {
                    idllSMS1.setVisibility(View.VISIBLE);
                    idetSMSNotification2.setText(smsNumArray[2]);
                    if (smsNumArray.length > 3) {
                        idllSMS2.setVisibility(View.VISIBLE);
                        idetSMSNotification3.setText(smsNumArray[3]);
                        if (smsNumArray.length > 4) {
                            idllSMS3.setVisibility(View.VISIBLE);
                            idetSMSNotification4.setText(smsNumArray[4]);
                        }
                    }
                }
            }
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            String message = null;
            String num = "03004048202";
            String smsNumber = "\"" + num + "\"";
            String emailID = "";
            String callNumber = "03004048202";
            String hashCode = Utils.getPreferences("hashCode", context);

            try {
                List<String> list = new ArrayList<>();
                message = URLEncoder.encode(idetMessage.getText().toString(), "UTF-8");
                String smsNumber0 = URLEncoder.encode(idetSMSNotification.getText().toString(), "UTF-8");
                String smsNumber1 = URLEncoder.encode(idetSMSNotification1.getText().toString(), "UTF-8");
                String smsNumber2 = URLEncoder.encode(idetSMSNotification2.getText().toString(), "UTF-8");
                String smsNumber3 = URLEncoder.encode(idetSMSNotification3.getText().toString(), "UTF-8");
                String smsNumber4 = URLEncoder.encode(idetSMSNotification4.getText().toString(), "UTF-8");

                if (smsNumber0.length() != 0)
                    list.add("\"" + smsNumber0 + "\"");


                if (smsNumber1.length() != 0)
                    list.add("\"" + smsNumber1 + "\"");


                if (smsNumber2.length() != 0)
                    list.add("\"" + smsNumber2 + "\"");

                if (smsNumber3.length() != 0)
                    list.add("\"" + smsNumber3 + "\"");

                if (smsNumber4.length() != 0)
                    list.add("\"" + smsNumber4 + "\"");

                for (int i = 0; i < list.size(); i++) {
                    smsNumber += "," + list.get(i);
                }

                emailID = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
                callNumber = URLEncoder.encode(idetCallNotification.getText().toString(), "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            loginURL = "https://api.navixy.com/v2/tracker/rule/update?hash=" + hashCode +
                    "&rule={%22id%22:116669,%22description%22:%22%22,%22extended_params%22:{},%22type%22:%22sos%22,%22primary_text%22:" + "\"" + message + "\"" +
                    ",%22secondary_text%22:%22%22,%22alerts%22:{%22sms_phones%22:[" + smsNumber + "],%22phones%22:[],%22emails%22:[],%22push_enabled%22:true}," +
                    "%22suspended%22:false,%22zone_id%22:0,%22name%22:%22Pressing%20SOS%20button%22,%22trackers%22:[100040]," +
                    "%22param%22:0,%22schedule%22:[{%22type%22:%22weekly%22,%22from%22:{%22weekday%22:1,%22time%22:%2200:00:00%22},%22to%22:{%22weekday%22:7,%22time%22:%2223:59:59%22}}]," +
                    "%22group_id%22:3}";
//            loginURL = "https://api.navixy.com/v2/tracker/rule/update?hash=" + hashCode +
//                    "&rule={%22id%22:116669,%22description%22:%22%22,%22extended_params%22:{},%22type%22:%22sos%22,%22primary_text%22:" + "\"" + message + "\"" +
//                    ",%22secondary_text%22:%22%22,%22alerts%22:{%22sms_phones%22:["+smsNumber+"],%22phones%22:[" + "\"" + callNumber + "\"" + "],%22emails%22:[" + "\"" +
//                    emailID + "\"" + "],%22push_enabled%22:true},%22suspended%22:false,%22zone_id%22:0,%22name%22:%22Pressing%20SOS%20button%22,%22trackers%22:[100040]," +
//                    "%22param%22:0,%22schedule%22:[{%22type%22:%22weekly%22,%22from%22:{%22weekday%22:1,%22time%22:%2200:00:00%22},%22to%22:{%22weekday%22:7,%22time%22:%2223:59:59%22}}]," +
//                    "%22group_id%22:3}";
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(loginURL, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (SosInput.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                return;
            }

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            progressDialog.dismiss();
            progressDialog = null;
            try {

                if (status.equals("true")) {
                    Toast.makeText(context, "Saved Successfully", Toast.LENGTH_LONG).show();

                    finish();
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

}
