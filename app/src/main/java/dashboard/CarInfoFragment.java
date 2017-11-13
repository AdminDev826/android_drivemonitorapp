package dashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import driver_details.Driver;
import driver_details.DriverDetails;
import io.fabric.sdk.android.Fabric;
import routine_maintinance.RoutineMaintinaceMain;
import util.AppSingleton;
import util.Utils;


public class CarInfoFragment extends Fragment {

    View v;
    Driver driver;
    DriverDetails driverDetail;
    Spinner idtvfuel_type, vehicle_type;
    TextView idtvRoutineMaintinanece, idtvInsuranceExpiry, txtSave, txtCancel, driver_expiry_date, driver_txt_save, driver_txt_cancel, lblDriver_Name;
    EditText idtvLicence, idtvLicenceExp, idtvVin, idtvchassis_number, idtvnorm_avg_fuel_consumption, idtvDriverName;
    String strDriverName, strLicense, strLicenseExp, strVin, strVhassis_number, strVehicle_type, strVfuel_type, strVnorm_avg_fuel_consumption, expDate;
    EditText driver_firstName, driver_lastName, driver_middleName, driver_address, driver_phone, driver_email, driver_license_number, driver_license_class;
    String str_driver_firstName, str_driver_lastName, str_driver_middleName, str_driver_address, str_driver_phone, str_driver_email, str_driver_license_number, str_driver_license_class, str_driver_expiry_date;

    Context context;
    String tID;
    String hashCode;
    String updateURL, uploadData;

    boolean dataFlag = true;

    public CarInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(v != null) return v;
        v = inflater.inflate(R.layout.driver_details_new, container, false);
        init();
        return v;
    }

    private void init() {
        context = getActivity();
        Fabric.with(context, new Crashlytics());
        driver = new Driver();
        driverDetail = new DriverDetails();
        lblDriver_Name = (TextView) v.findViewById(R.id.lblDriverName);
        idtvDriverName = (EditText) v.findViewById(R.id.idtvDriverName);
        idtvLicence = (EditText) v.findViewById(R.id.idtvLicence);
        idtvLicenceExp = (EditText) v.findViewById(R.id.idtvLicenceExp);
        idtvVin = (EditText) v.findViewById(R.id.idtvVin);
        txtSave = (TextView) v.findViewById(R.id.carinfo_txt_save);
        txtCancel = (TextView) v.findViewById(R.id.carinfo_txt_cancel);
        driver_txt_save = (TextView) v.findViewById(R.id.carinfo_txt_driver_save);
        driver_txt_cancel = (TextView) v.findViewById(R.id.carinfo_txt_driver_cancel);
        driver_expiry_date = (TextView) v.findViewById(R.id.carinfo_txt_driverExpiry);
        idtvchassis_number = (EditText) v.findViewById(R.id.idtvchassis_number);
        idtvRoutineMaintinanece = (TextView) v.findViewById(R.id.idtvRoutineMaintinanece);
        idtvfuel_type = (Spinner) v.findViewById(R.id.idtvfuel_type);
        vehicle_type = (Spinner) v.findViewById(R.id.vehicle_type);
        idtvnorm_avg_fuel_consumption = (EditText) v.findViewById(R.id.idtvnorm_avg_fuel_consumption);
        idtvInsuranceExpiry = (TextView) v.findViewById(R.id.idtvInsuranceExpiry);
        driver_firstName = (EditText) v.findViewById(R.id.carinfo_txt_firstname);
        driver_lastName = (EditText) v.findViewById(R.id.carinfo_txt_lastname);
        driver_middleName = (EditText) v.findViewById(R.id.carinfo_txt_middlename);
        driver_address = (EditText) v.findViewById(R.id.carinfo_txt_address);
        driver_phone = (EditText) v.findViewById(R.id.carinfo_txt_phone);
        driver_email = (EditText) v.findViewById(R.id.carinfo_txt_email);
        driver_license_number = (EditText) v.findViewById(R.id.carinfo_txt_licenseNumber);
        driver_license_class = (EditText) v.findViewById(R.id.carinfo_txt_licenseClass);

        List<String> list = new ArrayList<String>();
        list.add("");
        list.add("petrol");
        list.add("diesel");
        list.add("gas");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);
        idtvfuel_type.setAdapter(dataAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.vehicle_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_type.setAdapter(adapter);

        EditTextEditable(false);
        EditTextEditable_driver(false);

        idtvInsuranceExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        driver_expiry_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker1();
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(txtSave.getText().toString().equals("Edit")){
                        EditTextEditable(true);
                        txtSave.setText("Save");
                    }else{
                        save();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSave.setText("Edit");
                EditTextEditable(false);
            }
        });
        driver_txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(driver_txt_save.getText().toString().equals("Edit")){
                        EditTextEditable_driver(true);
                        driver_txt_save.setText("Save");
                    }else{
                        save_driver();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        driver_txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver_txt_save.setText("Edit");
                EditTextEditable_driver(false);
            }
        });

        tID = Utils.getPreferences("TrackerID", getActivity());
        hashCode = Utils.getPreferences("hashCode", context);


        idtvRoutineMaintinanece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoutineMaintinaceMain.class);
                intent.putExtra("trackerID", tID);
                intent.putExtra("vehicle_id", driver.getId());
                startActivity(intent);
            }
        });
        idtvDriverName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                lblDriver_Name.setText(idtvDriverName.getText().toString());
                return false;
            }
        });

        LoadData();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);

            idtvInsuranceExpiry.setText(String.valueOf(year1) + "-" + String.valueOf(month1)
                    + "-" + String.valueOf(day1));
        }
    };
    private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);

            driver_expiry_date.setText(String.valueOf(year1) + "-" + String.valueOf(month1)
                    + "-" + String.valueOf(day1));
        }
    };
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        DatePickerDialog datePicker = new DatePickerDialog(context,
                R.style.DatePickerTheme, datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();
    }
    private void showDatePicker1() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        DatePickerDialog datePicker = new DatePickerDialog(context,
                R.style.DatePickerTheme, datePickerListener1,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();
    }

    private void EditTextEditable(boolean b) {
        if(b){
            idtvDriverName.setEnabled(true);
            idtvLicence.setEnabled(true);
            idtvLicenceExp.setEnabled(true);
            idtvVin.setEnabled(true);
            idtvchassis_number.setEnabled(true);
            idtvfuel_type.setEnabled(true);
            vehicle_type.setEnabled(true);
            idtvnorm_avg_fuel_consumption.setEnabled(true);
            idtvInsuranceExpiry.setEnabled(true);
        }else{
            idtvDriverName.setEnabled(false);
            idtvLicence.setEnabled(false);
            idtvLicenceExp.setEnabled(false);
            idtvVin.setEnabled(false);
            idtvchassis_number.setEnabled(false);
            idtvfuel_type.setEnabled(false);
            vehicle_type.setEnabled(false);
            idtvnorm_avg_fuel_consumption.setEnabled(false);
            idtvInsuranceExpiry.setEnabled(false);
        }
    }
    private void EditTextEditable_driver(boolean b) {
        if(b){
            driver_firstName.setEnabled(true);
            driver_lastName.setEnabled(true);
            driver_middleName.setEnabled(true);
            driver_address.setEnabled(true);
            driver_phone.setEnabled(true);
            driver_email.setEnabled(true);
            driver_license_number.setEnabled(true);
            driver_license_class.setEnabled(true);
            driver_expiry_date.setEnabled(true);
        }else{
            driver_firstName.setEnabled(false);
            driver_lastName.setEnabled(false);
            driver_middleName.setEnabled(false);
            driver_address.setEnabled(false);
            driver_phone.setEnabled(false);
            driver_email.setEnabled(false);
            driver_license_number.setEnabled(false);
            driver_license_class.setEnabled(false);
            driver_expiry_date.setEnabled(false);
        }
    }
    private void save_driver() throws JSONException {
        str_driver_firstName = driver_firstName.getText().toString();
        str_driver_lastName = driver_lastName.getText().toString();
        str_driver_middleName = driver_middleName.getText().toString();
        str_driver_address = driver_address.getText().toString();
        str_driver_phone = driver_phone.getText().toString();
        str_driver_email = driver_email.getText().toString();
        str_driver_license_number = driver_license_number.getText().toString();
        str_driver_license_class = driver_license_class.getText().toString();
        str_driver_expiry_date = driver_expiry_date.getText().toString();

        if(str_driver_firstName.equals("")||str_driver_lastName.equals("")||str_driver_address.equals("")||str_driver_phone.equals("")||str_driver_email.equals("")||str_driver_license_number.equals("")||str_driver_license_class.equals("")||str_driver_expiry_date.equals("")){
            Toast.makeText(context, "Please input all !", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonobj = new JSONObject();
        JSONObject jsonLocation = new JSONObject();
        if(driverDetail.getId().length() > 0){
            jsonobj.put("id", driverDetail.getId());
            jsonLocation.put("lat", driverDetail.getLat());
            jsonLocation.put("lng", driverDetail.getLng());
        }else{
            String lat = Utils.getPreferences(tID + "= lat", context);
            String lng = Utils.getPreferences(tID + "= lng", context);
            jsonLocation.put("lat", lat);
            jsonLocation.put("lng", lng);
        }
        jsonLocation.put("address", str_driver_address);
        jsonobj.put("location", jsonLocation);
        jsonobj.put("tracker_id", tID);
        jsonobj.put("first_name", str_driver_firstName);
        jsonobj.put("middle_name", str_driver_middleName);
        jsonobj.put("last_name", str_driver_lastName);
        jsonobj.put("email", str_driver_email);
        jsonobj.put("phone", str_driver_phone);
        jsonobj.put("driver_license_number", str_driver_license_number);
        jsonobj.put("driver_license_cats", str_driver_license_class);
        jsonobj.put("driver_license_valid_till", str_driver_expiry_date);
        jsonobj.put("hardware_key", driverDetail.getHardware_key());
        jsonobj.put("department_id", driverDetail.getDepartment_id());

        String uploadData = jsonobj.toString();
        try {
            uploadData = URLEncoder.encode(uploadData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url;
        if(driverDetail.getId().length() > 0){
            url = "https://api.navixy.com/v2/employee/update?hash=" + hashCode + "&employee=" + uploadData;
        }else{
            url = "https://api.navixy.com/v2/employee/create?hash=" + hashCode + "&employee=" + uploadData;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                if(driverDetail.getId().equals("")) {
                                    driverDetail.setId(response.getString("id"));
                                }
                                Toast.makeText(context, "SUCCESS !", Toast.LENGTH_SHORT).show();
                                EditTextEditable_driver(false);
                                driver_txt_save.setText("Edit");
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
                        try{
                            if(error.networkResponse.statusCode == 400){
                                Toast.makeText(context, "SUCCESS !", Toast.LENGTH_SHORT).show();
                                EditTextEditable_driver(false);
                                driver_txt_save.setText("Edit");
                            }else{
//                                Toast.makeText(context, "Fail ! \n Server Error !!!", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
        
    }

    private void save() throws JSONException {
        strDriverName = idtvDriverName.getText().toString();
        strLicense = idtvLicence.getText().toString();
        strLicenseExp = idtvLicenceExp.getText().toString();
        strVin = idtvVin.getText().toString();
        strVhassis_number = idtvchassis_number.getText().toString();
        strVfuel_type = idtvfuel_type.getSelectedItem().toString();
        strVehicle_type = vehicle_type.getSelectedItem().toString();
        strVnorm_avg_fuel_consumption = idtvnorm_avg_fuel_consumption.getText().toString();
        expDate = idtvInsuranceExpiry.getText().toString();

        if(strDriverName.equals("")||strLicense.equals("")||strLicenseExp.equals("")||strVin.equals("")||strVhassis_number.equals("")||strVfuel_type.equals("")||strVehicle_type.equals("")||strVnorm_avg_fuel_consumption.equals("")||expDate.equals("")){
            Toast.makeText(context, "Please input all !", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonobj = new JSONObject();
        if(dataFlag){
            updateURL = "https://api.navixy.com/v2/vehicle/create?hash=" + hashCode;

            jsonobj.put("tracker_id", tID);
            jsonobj.put("label", strDriverName);
            jsonobj.put("max_speed", "0");
            jsonobj.put("model", strLicenseExp);
            jsonobj.put("type", strVehicle_type);
            jsonobj.put("subtype", "");
            jsonobj.put("garage_id", "1");
            jsonobj.put("reg_number", strLicense);
            jsonobj.put("vin", strVin);
            jsonobj.put("chassis_number", strVhassis_number);
            jsonobj.put("payload_weight", "0");
            jsonobj.put("payload_height", "0");
            jsonobj.put("payload_length", "0");
            jsonobj.put("payload_width", "0");
            jsonobj.put("passengers", "0");
            jsonobj.put("fuel_type", strVfuel_type);
            jsonobj.put("fuel_grade", "");
            jsonobj.put("norm_avg_fuel_consumption", strVnorm_avg_fuel_consumption);
            jsonobj.put("fuel_tank_volume", "0");
            jsonobj.put("wheel_arrangement", "");
            jsonobj.put("tyres_number", "0");
            jsonobj.put("tyre_size", "");
            jsonobj.put("liability_insurance_policy_number", "");
            jsonobj.put("liability_insurance_valid_till", expDate);
            jsonobj.put("free_insurance_policy_number", "");
            jsonobj.put("free_insurance_valid_till", "");
        }else{
            updateURL = "https://api.navixy.com/v2/vehicle/update?hash=" + hashCode + "&tracker_id=" + tID;
            jsonobj.put("id", driver.getId());
            jsonobj.put("tracker_id", tID);
            jsonobj.put("label", strDriverName);
            jsonobj.put("max_speed", driver.getMax_speed());
            jsonobj.put("model", strLicenseExp);
            jsonobj.put("type", driver.getType());
            jsonobj.put("subtype", driver.getSubtype());
            if(driver.getGarage_id().equals("null"))
                jsonobj.put("garage_id", "1");
            else
                jsonobj.put("garage_id", driver.getGarage_id());
            jsonobj.put("reg_number", strLicense);
            jsonobj.put("vin", strVin);
            jsonobj.put("chassis_number", strVhassis_number);
            if(driver.getPayload_weight().equals("0.0") || driver.getPayload_weight().equals("null"))
                jsonobj.put("payload_weight", "0");
            else
                jsonobj.put("payload_weight", driver.getPayload_weight());
            if(driver.getPayload_height().equals("null"))
                jsonobj.put("payload_height", "0");
            else
                jsonobj.put("payload_height", driver.getPayload_height());
            if(driver.getPayload_lenth().equals("null"))
                jsonobj.put("payload_length", "0");
            else
                jsonobj.put("payload_length", driver.getPayload_lenth());
            if(driver.getPayload_width().equals("null"))
                jsonobj.put("payload_width", "0");
            else
                jsonobj.put("payload_width", driver.getPayload_width());
            if(driver.getPassengers().equals("null"))
                jsonobj.put("passengers", "0");
            else
                jsonobj.put("passengers", driver.getPassengers());
            jsonobj.put("fuel_type", strVfuel_type);
            jsonobj.put("fuel_grade", driver.getFuel_grade());
            jsonobj.put("norm_avg_fuel_consumption", strVnorm_avg_fuel_consumption);
            if(driver.getFuel_tank_volume().equals("null"))
                jsonobj.put("fuel_tank_volume", "0");
            else
                jsonobj.put("fuel_tank_volume", driver.getFuel_tank_volume());
            jsonobj.put("wheel_arrangement", driver.getWheel_arrangement());
            jsonobj.put("tyres_number", driver.getTyres_number());
            jsonobj.put("tyre_size", driver.getTyre_size());
            jsonobj.put("liability_insurance_policy_number", driver.getLiability_insurance_policy_number());
            jsonobj.put("liability_insurance_valid_till", expDate);
            jsonobj.put("free_insurance_policy_number", driver.getFree_insurance_policy_number());
            if(driver.getFree_insurance_valid_till().equals("null"))
                jsonobj.put("free_insurance_valid_till", "2016-11-24");
            else
                jsonobj.put("free_insurance_valid_till", driver.getFree_insurance_valid_till());
        }

        uploadData = jsonobj.toString();
        try {
            uploadData = URLEncoder.encode(uploadData, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        String status;
        String request = "";
        protected void onPreExecute() {
            request = updateURL + "&vehicle=" + uploadData;
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCallError(request, ServiceHandler.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    if(dataFlag == true) {
                        driver.setId(jsonObj.getString("id"));
                        updateURL = "https://api.navixy.com/v2/vehicle/update?hash=" + hashCode + "&tracker_id=" + tID;
                        dataFlag = false;
                    }

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
            try {
                if (status.equals("true")) {
                    Toast.makeText(context, "SUCCESS !", Toast.LENGTH_SHORT).show();
                    EditTextEditable(false);
                    txtSave.setText("Edit");
                } else {
//                    Toast.makeText(context, "SERVER ERROR !\n" + status, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(String... text) {
        }
    }


    private void LoadData(){
        String carInfoURL = "https://api.navixy.com/v2/vehicle/list?hash=" + hashCode;
        String driverInfoURL = "https://api.navixy.com/v2/employee/list?hash=" + hashCode;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, carInfoURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray driversList = response.getJSONArray("list");
                                for (int i = 0; i < driversList.length(); i++) {
                                    JSONObject driverInfo = driversList.getJSONObject(i);
                                    if(driverInfo.getString("tracker_id").equals(tID)){
                                        dataFlag = false;
                                        String id = driverInfo.getString("id");
                                        driver.setId(id);
                                        driver.setTracker_id(driverInfo.getString("tracker_id"));
                                        driver.setType(driverInfo.getString("type"));
                                        driver.setFirst_name(driverInfo.getString("label"));
                                        driver.setModel(driverInfo.getString("model"));
                                        driver.setReg_number(driverInfo.getString("reg_number"));
                                        driver.setVin(driverInfo.getString("vin"));
                                        driver.setChassis_number(driverInfo.getString("chassis_number"));
                                        driver.setFuel_type(driverInfo.getString("fuel_type"));
                                        driver.setNorm_avg_fuel_consumption(driverInfo.getString("norm_avg_fuel_consumption"));
                                        driver.setLiability_insurance_valid_till(driverInfo.getString("liability_insurance_valid_till"));
                                        driver.setMax_speed(driverInfo.getString("max_speed"));

//                        if(driverInfo.getJSONObject("subtype") != null)
//                            driver.setSubtype(driverInfo.getString("subtype"));
//                        else
                                        driver.setSubtype("universal");

                                        driver.setGarage_id(driverInfo.getString("garage_id"));
                                        driver.setPayload_weight(driverInfo.getString("payload_weight"));
                                        driver.setPayload_height(driverInfo.getString("payload_height"));
                                        driver.setPayload_lenth(driverInfo.getString("payload_length"));
                                        driver.setPayload_width(driverInfo.getString("payload_width"));
                                        driver.setPassengers(driverInfo.getString("passengers"));
                                        driver.setFuel_grade(driverInfo.getString("fuel_grade"));
                                        driver.setFuel_tank_volume(driverInfo.getString("fuel_tank_volume"));
                                        driver.setWheel_arrangement(driverInfo.getString("wheel_arrangement"));
                                        driver.setTyre_size(driverInfo.getString("tyre_size"));
                                        driver.setTyres_number(driverInfo.getString("tyres_number"));
                                        driver.setLiability_insurance_policy_number(driverInfo.getString("liability_insurance_policy_number"));
                                        driver.setFree_insurance_policy_number(driverInfo.getString("free_insurance_policy_number"));
                                        driver.setFree_insurance_valid_till(driverInfo.getString("free_insurance_valid_till"));

                                        updateUI();
                                    }
                                }
                            }else{
                                dataFlag = true;
                                updateURL = "https://api.navixy.com/v2/vehicle/create?hash=" + hashCode;
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            dataFlag = true;
                            updateURL = "https://api.navixy.com/v2/vehicle/create?hash=" + hashCode;
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");

        JsonObjectRequest jsonRequest1 = new JsonObjectRequest
                (Request.Method.GET, driverInfoURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray driversList = response.getJSONArray("list");
                                for (int i = 0; i < driversList.length(); i++) {
                                    JSONObject driverInfo = driversList.getJSONObject(i);
                                    if(driverInfo.getString("tracker_id").equals(tID)){
                                        driverDetail.setId(driverInfo.getString("id"));
                                        driverDetail.setTracker_id(driverInfo.getString("tracker_id"));
                                        driverDetail.setFirst_name(driverInfo.getString("first_name"));
                                        driverDetail.setMiddle_name(driverInfo.getString("middle_name"));
                                        driverDetail.setLast_name(driverInfo.getString("last_name"));
                                        driverDetail.setEmail(driverInfo.getString("email"));
                                        driverDetail.setPhone(driverInfo.getString("phone"));
                                        driverDetail.setDriver_license_number(driverInfo.getString("driver_license_number"));
                                        driverDetail.setDriver_license_cats(driverInfo.getString("driver_license_cats"));
                                        driverDetail.setDriver_license_valid_till(driverInfo.getString("driver_license_valid_till"));
                                        driverDetail.setHardware_key(driverInfo.getString("hardware_key"));
                                        driverDetail.setDepartment_id(driverInfo.getString("department_id"));
                                        try{
                                            JSONObject locationObj = driverInfo.getJSONObject("location");
                                            driverDetail.setLat(locationObj.getString("lat"));
                                            driverDetail.setLng(locationObj.getString("lng"));
                                            driverDetail.setAddress(locationObj.getString("address"));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        updateUI_driver();
                                    }
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
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest1, "smartdrive");
    }

    private void updateUI_driver() {
        driver_firstName.setText(driverDetail.getFirst_name());
        driver_lastName.setText(driverDetail.getLast_name());
        driver_middleName.setText(driverDetail.getMiddle_name());
        driver_address.setText(driverDetail.getAddress());
        driver_phone.setText(driverDetail.getPhone());
        driver_email.setText(driverDetail.getEmail());
        driver_license_number.setText(driverDetail.getDriver_license_number());
        driver_license_class.setText(driverDetail.getDriver_license_cats());
        driver_expiry_date.setText(driverDetail.getDriver_license_valid_till());
    }

    private void updateUI() {
        idtvDriverName.setText(driver.getFirst_name());
        lblDriver_Name.setText(driver.getFirst_name());
        idtvLicence.setText(driver.getReg_number());
        idtvLicenceExp.setText(driver.getModel());
        idtvVin.setText(driver.getVin());
        idtvchassis_number.setText(driver.getChassis_number());
        if(driver.getFuel_type().equals("petrol")){
            idtvfuel_type.setSelection(1);
        }else if(driver.getFuel_type().equals("diesel")){
            idtvfuel_type.setSelection(2);
        }else{
            idtvfuel_type.setSelection(3);
        }
        switch (driver.getType()){
            case "car":
                vehicle_type.setSelection(1);
                break;
            case "truck":
                vehicle_type.setSelection(2);
                break;
            case "bus":
                vehicle_type.setSelection(3);
                break;
            case "special":
                vehicle_type.setSelection(4);
                break;
            default:
                break;
        }
        idtvnorm_avg_fuel_consumption.setText(driver.getNorm_avg_fuel_consumption());
        expDate = driver.getLiability_insurance_valid_till();
        if (expDate.equals("null"))
            idtvInsuranceExpiry.setText("");
        else
            idtvInsuranceExpiry.setText(driver.getLiability_insurance_valid_till());
    }
}
