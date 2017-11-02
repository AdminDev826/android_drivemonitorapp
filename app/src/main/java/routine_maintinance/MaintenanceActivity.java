package routine_maintinance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.TimeZone;

import util.AppSingleton;
import util.Utils;

public class MaintenanceActivity extends Activity implements TimePickerDialog.OnTimeSetListener {

    Context context;
    String hashCode;
    String strDateTime;
    String vehicle_id = "";
    String maintence_id = "";
    String url;
    Button btnSave;
    TextView txt_date_scheduled_on;
    EditText txt_date_remind_before, txt_mileage_current_mileage, txt_mileage_target_mileage, txt_mileage_remind_before, txt_mileage_email_notification, txt_phone, txt_name, txt_cost;
    Switch sc_notification_on_screen, sc_push_notification, sc_date, sc_mileage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_layout);
        context = getApplicationContext();
        hashCode = Utils.getPreferences("hashCode", context);
        vehicle_id = getIntent().getStringExtra("vehicle_id");

        txt_name = (EditText) findViewById(R.id.cm_name);
        txt_cost = (EditText) findViewById(R.id.cm_cost);
        txt_date_scheduled_on = (TextView) findViewById(R.id.cm_date_scheduled_on);
        txt_date_remind_before = (EditText) findViewById(R.id.cm_date_remind_before);
        txt_mileage_current_mileage = (EditText) findViewById(R.id.cm_mileage_current);
        txt_mileage_target_mileage = (EditText) findViewById(R.id.cm_mileage_target);
        txt_mileage_remind_before = (EditText) findViewById(R.id.cm_mileage_remind);
        txt_mileage_email_notification = (EditText) findViewById(R.id.cm_mileage_email);
        txt_phone = (EditText) findViewById(R.id.cm_phone_number);

        sc_notification_on_screen = (Switch) findViewById(R.id.cm_notification_screen);
        sc_push_notification = (Switch) findViewById(R.id.cm_notification_push);
        sc_date = (Switch) findViewById(R.id.cm_date);
        sc_mileage = (Switch) findViewById(R.id.cm_mileage);

        txt_date_scheduled_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSave = (Button) findViewById(R.id.cm_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        sc_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sc_mileage.setChecked(false);
                    txt_date_scheduled_on.setEnabled(true);
                    txt_date_remind_before.setEnabled(true);
                }else{
                    sc_mileage.setChecked(true);
                    txt_date_scheduled_on.setEnabled(false);
                    txt_date_remind_before.setEnabled(false);
                    txt_date_scheduled_on.setText("");
                    txt_date_remind_before.setText("");
                }
            }
        });
        sc_mileage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sc_date.setChecked(false);
                    txt_mileage_current_mileage.setEnabled(true);
                    txt_mileage_target_mileage.setEnabled(true);
                    txt_mileage_remind_before.setEnabled(true);
                }else{
                    sc_date.setChecked(true);
                    txt_mileage_current_mileage.setText("");
                    txt_mileage_target_mileage.setText("");
                    txt_mileage_remind_before.setText("");
                    txt_mileage_current_mileage.setEnabled(false);
                    txt_mileage_target_mileage.setEnabled(false);
                    txt_mileage_remind_before.setEnabled(false);
                }
            }
        });
//        loadData();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);

            strDateTime = String.valueOf(year1) + "-" + String.valueOf(month1)
                    + "-" + String.valueOf(day1);
            showTimePicker();
//            txt_date_scheduled_on.setText(String.valueOf(year1) + "-" + String.valueOf(month1)
//                    + "-" + String.valueOf(day1));
        }
    };
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        DatePickerDialog datePicker = new DatePickerDialog(MaintenanceActivity.this,
                R.style.DatePickerTheme, datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();
    }
    private void showTimePicker() {
        TimePickerDialog dpd = null;
        Calendar now = Calendar.getInstance();
        dpd = TimePickerDialog.newInstance(
                MaintenanceActivity.this,
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                false
        );

        dpd.setAccentColor(Color.parseColor("#9C27B0"));
        dpd.setTitle("Please select time");
        dpd.show(getFragmentManager(), "TimepickerDialog");
    }

    private void loadData() {
        String url = "https://api.navixy.com/v2/vehicle/service_task/list?hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                for (int i = 0; i < listtrackingHistory.length(); i++) {
                                    JSONObject c = listtrackingHistory.getJSONObject(i);
                                    RoutineMaintinance routineMaintinance = new RoutineMaintinance();

                                    routineMaintinance.setId(c.getString("id"));
                                    routineMaintinance.setVehicle_id(c.getString("vehicle_id"));
                                    routineMaintinance.setStatus(c.getString("status"));

                                    JSONObject prediction = c.getJSONObject("prediction");
                                    routineMaintinance.setEnd_date(prediction.getString("end_date"));
                                    routineMaintinance.setWear_percentage(prediction.getString("wear_percentage"));

                                    routineMaintinance.setDescription(c.getString("description"));
                                    routineMaintinance.setCost(c.getString("cost"));
                                    routineMaintinance.setVehicle_label(c.getString("vehicle_label"));
                                    routineMaintinance.setCompletion_date(c.getString("completion_date"));

                                }
                            }else{
                                Toast.makeText(context, " Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, " Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void save() throws JSONException {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String str_scheduled_on = txt_date_scheduled_on.getText().toString();
        String str_date_remind = txt_date_remind_before.getText().toString();
        String str_mileage_current = txt_mileage_current_mileage.getText().toString();
        String str_mileage_target = txt_mileage_target_mileage.getText().toString();
        String str_mileage_remind = txt_mileage_remind_before.getText().toString();
        String str_email = txt_mileage_email_notification.getText().toString();
        String str_phone = txt_phone.getText().toString();
        String str_name = txt_name.getText().toString();
        String str_cost = txt_cost.getText().toString();

        JSONObject jsonData = new JSONObject();
        JSONObject jsonCondition = new JSONObject();

        jsonData.put("vehicle_id", vehicle_id);
        jsonData.put("comment", "");

        if(str_name.length() < 1){
            Toast.makeText(context, "Input Maintenance name !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(str_cost.length() < 1){
            Toast.makeText(context, "Input Maintenance cost !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(sc_date.isChecked()){
            JSONObject jsonDate = new JSONObject();
            if(str_scheduled_on.length() < 1 || str_date_remind.length() < 1){
                Toast.makeText(context, "Input data !", Toast.LENGTH_SHORT).show();
                return;
            }else{
                jsonDate.put("end", str_scheduled_on);
                jsonDate.put("notification_interval", str_date_remind);
                jsonCondition.put("date", jsonDate);
            }
        }
        if(sc_mileage.isChecked()){
            JSONObject jsonMileage = new JSONObject();
            if(str_mileage_current.length() < 1 || str_mileage_target.length() < 1 || str_mileage_remind.length() < 1){
                Toast.makeText(context, "Input mileage data !", Toast.LENGTH_SHORT).show();
                return;
            }else{
                jsonMileage.put("limit", str_mileage_target);
                jsonMileage.put("notification_interval", str_mileage_remind);
                jsonCondition.put("mileage", jsonMileage);
            }
        }
        if(str_email.length() < 1 || !str_email.matches(emailPattern)){
            Toast.makeText(context, "Input email address !", Toast.LENGTH_SHORT).show();
            return;
        }
        if(str_phone.length() < 1){
            Toast.makeText(context, "Input phone number !", Toast.LENGTH_SHORT).show();
            return;
        }

        jsonData.put("description", str_name);
        jsonData.put("cost", str_cost);
        jsonData.put("conditions", jsonCondition);
        JSONObject jsonStart = new JSONObject();
        jsonData.put("start", jsonStart);

        JSONObject jsonNotification = new JSONObject();
        JSONArray jsonPhones = new JSONArray();
        jsonPhones.put(str_phone);
        jsonNotification.put("sms_phones", jsonPhones);
        JSONArray jsonEmails = new JSONArray();
        jsonEmails.put(str_email);
        jsonNotification.put("emails", jsonEmails);

        if(sc_push_notification.isChecked()){
            jsonNotification.put("push_enabled", "true");
        }else{
            jsonNotification.put("push_enabled", "false");
        }
        jsonData.put("notifications", jsonNotification);
        String uploadData = jsonData.toString();
        try {
            uploadData = URLEncoder.encode(uploadData, "utf-8");
            url = "https://api.navixy.com/v2/vehicle/service_task/create?hash=" + hashCode + "&task=" + uploadData;
//            postData(uploadData);
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void postData(String uploadData) {
        String url = "https://api.navixy.com/v2/vehicle/service_task/create?hash=" + hashCode + "&task=" + uploadData;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                Intent intent = new Intent(context, RoutineMaintinaceMain.class);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("vehicle_id", vehicle_id);
                                startActivity(intent);
                                Toast.makeText(context, " Success !", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, " Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, " Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
//                        Toast.makeText(context, " Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String time = null;
        String sHour;
        String sMinut;
        sHour = "" + hourOfDay;
        sMinut = "" + minute;

        if (hourOfDay > -1 && hourOfDay < 10) {
            sHour = "0" + hourOfDay;
        }
        if (minute > -1 && minute < 10) {
            sMinut = "0" + minute;
        }
        time = "" + sHour + ":" + sMinut;

        String dateTime = strDateTime + " " + time + ":00";
        txt_date_scheduled_on.setText(dateTime);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        String status;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(context, "",
//                    "Saving ...", true);
            btnSave.setEnabled(false);
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCallError(url, ServiceHandler.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Response: ", "> " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");

                    if (status.equals("false")) {
                        JSONObject stat;
                        stat = jsonObj.getJSONObject("status");
                        status = stat.getString("description");
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
//            progressDialog.dismiss();
            if(status.equals("true")){
                Toast.makeText(context, "Success !", Toast.LENGTH_SHORT).show();
            }else{
                btnSave.setEnabled(true);
                Toast.makeText(context, "Fail ! \n" + status, Toast.LENGTH_SHORT).show();
            }
        }

        protected void onProgressUpdate(String... text) {
        }

    }
}
