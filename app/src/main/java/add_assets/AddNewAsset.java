package add_assets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

import dashboard.Trackers;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.Utils;

/**
 * Created by Farhan on 7/25/2016.
 */
public class AddNewAsset extends Activity {
    TextView btnPost;
    String loginURL;
    ProgressDialog progressDialog;
    TextView idtvDimensions, idtvFuel, idtvWheelbase, idtvInsurance, idtvBackVehicle;
    LinearLayout idllDimensions, idllFuel, idllwheelbase, idllInsurance;
    String trackerID = null;
    List<Trackers> trackerList = new ArrayList<Trackers>();
    List<String> listTrackerLable = new ArrayList<String>();
    DatabaseHandler db;
    boolean showDimention = false;
    boolean showFuel = false;
    boolean showWheelbase = false;
    boolean showInsurance = false;
    Spinner idspsubTypes, idspTypes, idspFuelType, idspTrackers;
    EditText idetLabel, idetGarage, idetModel, idetVehicleRegistrationPlate, idetVIN, idetChassisNumber;

    //Dimension editText
    EditText idetCargoCapecity, idetCargoBayL, idetCargoBayW, idetCargoBayH, idetNumberOfPassengers;

    //Fuel Edittext
    EditText idetFuelGrade, idetFuelCons, idetTankCapacity;

    //Wheel Edittext
    EditText idetWheelH, idetWheelW, idetTyreSize, idetTyreNumber;

    //Insurance Edittext
    EditText idetInsurancePolicyNumber, idetInsurancePolicyNumber2;
    String fuelType;
    String type;
    String subType;
    Context context;
    String status;
    String hashCode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.add_new_asset);
        context = this;
        idetLabel = (EditText) findViewById(R.id.idetLabel);
        idetModel = (EditText) findViewById(R.id.idetModel);
        idetGarage = (EditText) findViewById(R.id.idetGarage);
        idtvBackVehicle = (TextView) findViewById(R.id.idtvBackVehicle);
        db = new DatabaseHandler(getApplicationContext());
        idetVehicleRegistrationPlate = (EditText) findViewById(R.id.idetVehicleRegistrationPlate);
        idetVIN = (EditText) findViewById(R.id.idetVIN);
        idetChassisNumber = (EditText) findViewById(R.id.idetChassisNumber);


        trackerList = db.getTrackerList();
        listTrackerLable.add("Select Tracker");
        for (int i = 0; i < trackerList.size(); i++) {
            listTrackerLable.add(trackerList.get(i).getTrackerLabel());
        }

        idspsubTypes = (Spinner) findViewById(R.id.idspsubTypes);
        idspTypes = (Spinner) findViewById(R.id.idspTypes);
        idspFuelType = (Spinner) findViewById(R.id.idspFuelType);
        idspTrackers = (Spinner) findViewById(R.id.idspTrackers);

        //Dimension editText
        idetCargoCapecity = (EditText) findViewById(R.id.idetCargoCapecity);
        idetCargoBayL = (EditText) findViewById(R.id.idetCargoBayL);
        idetCargoBayW = (EditText) findViewById(R.id.idetCargoBayW);
        idetCargoBayH = (EditText) findViewById(R.id.idetCargoBayH);
        idetNumberOfPassengers = (EditText) findViewById(R.id.idetNumberOfPassengers);

        //Fuel Edittext
        idetFuelGrade = (EditText) findViewById(R.id.idetFuelGrade);
        idetFuelCons = (EditText) findViewById(R.id.idetFuelCons);
        idetTankCapacity = (EditText) findViewById(R.id.idetTankCapacity);

        //Wheel arrangment Edittext
        idetWheelH = (EditText) findViewById(R.id.idetWheelH);
        idetWheelW = (EditText) findViewById(R.id.idetWheelW);
        idetTyreSize = (EditText) findViewById(R.id.idetTyreSize);
        idetTyreNumber = (EditText) findViewById(R.id.idetTyreNumber);


        //Insurance Edittext
        idetInsurancePolicyNumber = (EditText) findViewById(R.id.idetInsurancePolicyNumber);
        idetInsurancePolicyNumber2 = (EditText) findViewById(R.id.idetInsurancePolicyNumber2);

        idtvDimensions = (TextView) findViewById(R.id.idtvDimensions);
        idtvFuel = (TextView) findViewById(R.id.idtvFuel);
        idtvWheelbase = (TextView) findViewById(R.id.idtvWheelbase);
        idtvInsurance = (TextView) findViewById(R.id.idtvInsurance);

        idllDimensions = (LinearLayout) findViewById(R.id.idllDimensions);
        idllFuel = (LinearLayout) findViewById(R.id.idllFuel);
        idllwheelbase = (LinearLayout) findViewById(R.id.idllwheelbase);
        idllInsurance = (LinearLayout) findViewById(R.id.idllInsurance);


        idtvBackVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ArrayAdapter<String> dataAdapterGen = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, listTrackerLable);
        dataAdapterGen.setDropDownViewResource(android.R.layout.simple_list_item_1);
        idspTrackers.setAdapter(dataAdapterGen);

        String trackerLabel = idspTrackers.getSelectedItem().toString();

        if (trackerLabel.equals("Select Tracker")) {

        } else {
            trackerID = db.getTrackerID(trackerLabel);
        }


        idspTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                idspsubTypes.setVisibility(View.VISIBLE);
                if (position == 1) {


                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.array_subtype_car, R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    idspsubTypes.setAdapter(adapter);

                } else if (position == 2) {

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.array_subtype_truck, R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    idspsubTypes.setAdapter(adapter);

                } else if (position == 3) {

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.array_type_bus, R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    idspsubTypes.setAdapter(adapter);
                } else if (position == 4) {

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.array_type_special, R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    idspsubTypes.setAdapter(adapter);
                } else {

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.array_type_special, R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    idspsubTypes.setAdapter(adapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        idtvDimensions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showDimention == true) {
                    idllDimensions.setVisibility(View.GONE);
                    showDimention = false;
                } else {
                    idllDimensions.setVisibility(View.VISIBLE);
                    showDimention = true;
                }
            }
        });


        idtvFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showFuel == true) {
                    idllFuel.setVisibility(View.GONE);
                    showFuel = false;
                } else {
                    idllFuel.setVisibility(View.VISIBLE);
                    showFuel = true;
                }
            }
        });

        idtvWheelbase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showWheelbase == true) {
                    idllwheelbase.setVisibility(View.GONE);
                    showWheelbase = false;
                } else {
                    idllwheelbase.setVisibility(View.VISIBLE);
                    showWheelbase = true;
                }
            }
        });

        idtvInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showInsurance == true) {
                    idllInsurance.setVisibility(View.GONE);
                    showInsurance = false;
                } else {
                    idllInsurance.setVisibility(View.VISIBLE);
                    showInsurance = true;
                }
            }
        });


        btnPost = (TextView) findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "";
                hashCode = "";
                String label = idetLabel.getText().toString();
                String trackerLabel = idspTrackers.getSelectedItem().toString();
                String fuel_Type = idspFuelType.getSelectedItem().toString();
                String type_ = idspTypes.getSelectedItem().toString();
                String sub_type = idspsubTypes.getSelectedItem().toString();


                if (label.isEmpty() || fuel_Type.isEmpty() || type_.isEmpty()) {

                    Toast.makeText(context, "Please fill the necessary feilds", Toast.LENGTH_LONG).show();
                } else {

                    if (trackerLabel.equals("Select Tracker")) {

                    } else {
                        trackerID = db.getTrackerID(trackerLabel);
                    }


                    if (fuel_Type.equals("Select fuel type")) {
                        fuelType = "";
                        Toast.makeText(context, "Select fuel type", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        fuelType = fuel_Type;
                    }


                    if (type_.equals("Select Type")) {
                        type = "";
                    } else {
                        type = type_;
                    }


                    if (sub_type.equals("Select Subtype")) {
                        subType = "";
                    } else {
                        subType = sub_type;
                    }


                    progressDialog = ProgressDialog.show(context, "",
                            "Saving vehicle...", true);
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute();
                }


            }
        });
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            String label = null;
            String max_speed = null;
            String model = "";
            String emailID = null;

            String subtype = null;
            String callNumber = null;
            String garage_id = null;
            String reg_number = "";
            String vin = "";
            String chassis_number = "";
            String payload_weight = null;
            String payload_height = null;
            String payload_length = null;
            String payload_width = null;
            String passengers = null;
            String fuel_type = null;
            String fuel_grade = "";
            String norm_avg_fuel_consumption = null;
            String fuel_tank_volume = null;
            String wheel_arrangementH = null;
            String wheel_arrangementW = null;

            String tyre_size = null;
            String tyres_number = null;
            String liability_insurance_policy_number = "";
            String liability_insurance_valid_till = null;
            String free_insurance_policy_number = "";
            String free_insurance_valid_till = null;
            String icon_id = null;
            String avatar_file_name = null;
            String photo = "";
            String empty_group = "";


            try {

                label = URLEncoder.encode(idetLabel.getText().toString(), "UTF-8");
                model = idetModel.getText().toString();
                if (model.equals("")) {
                    model = "";
                } else {
                    model = URLEncoder.encode(idetModel.getText().toString(), "UTF-8");

                }
//                type = URLEncoder.encode(idetCallNotification.getText().toString(), "UTF-8");
//                subtype = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
                garage_id = URLEncoder.encode(idetGarage.getText().toString(), "UTF-8");
                reg_number = idetVehicleRegistrationPlate.getText().toString();
                if (reg_number.equals("")) {
                    reg_number = "";
                } else {
                    reg_number = URLEncoder.encode(reg_number, "UTF-8");
                }


                vin = idetVIN.getText().toString();
                if (vin.equals("")) {
                    vin = "";
                } else {
                    vin = URLEncoder.encode(vin, "UTF-8");
                }


                chassis_number = idetChassisNumber.getText().toString();
                if (chassis_number.equals("")) {
                    chassis_number = "";
                } else {
                    chassis_number = URLEncoder.encode(chassis_number, "UTF-8");
                }


                payload_weight = idetCargoCapecity.getText().toString();
                if (payload_weight.equals("")) {
                    payload_weight = null;
                } else {
                    payload_weight = URLEncoder.encode(payload_weight, "UTF-8");
                }


                payload_height = idetCargoBayH.getText().toString();
                if (payload_height.equals("")) {
                    payload_height = null;
                } else {
                    payload_height = URLEncoder.encode(payload_height, "UTF-8");
                }


                payload_length = idetCargoBayL.getText().toString();
                if (payload_length.equals("")) {
                    payload_length = null;
                } else {
                    payload_length = URLEncoder.encode(payload_length, "UTF-8");
                }


                payload_width = idetCargoBayW.getText().toString();
                if (payload_width.equals("")) {
                    payload_width = null;
                } else {
                    payload_width = URLEncoder.encode(payload_width, "UTF-8");
                }


                passengers = idetNumberOfPassengers.getText().toString();
                if (passengers.equals("")) {
                    passengers = null;
                } else {
                    passengers = URLEncoder.encode(passengers, "UTF-8");
                }

                //                fuel_type = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");


                fuel_grade = idetFuelGrade.getText().toString();
                if (fuel_grade.equals("")) {
                    fuel_grade = "";
                } else {
                    fuel_grade = URLEncoder.encode(fuel_grade, "UTF-8");
                }


                norm_avg_fuel_consumption = idetFuelCons.getText().toString();
                if (norm_avg_fuel_consumption.equals("")) {
                    norm_avg_fuel_consumption = null;
                } else {
                    norm_avg_fuel_consumption = URLEncoder.encode(norm_avg_fuel_consumption, "UTF-8");
                }

                fuel_tank_volume = idetTankCapacity.getText().toString();
                if (fuel_tank_volume.equals("")) {
                    fuel_tank_volume = null;
                } else {
                    fuel_tank_volume = URLEncoder.encode(fuel_tank_volume, "UTF-8");
                }


                wheel_arrangementH = idetWheelH.getText().toString();
                if (wheel_arrangementH.equals("")) {
                    wheel_arrangementH = "0";
                } else {
                    wheel_arrangementH = URLEncoder.encode(wheel_arrangementH, "UTF-8");
                }


                wheel_arrangementW = idetWheelW.getText().toString();
                if (wheel_arrangementW.equals("")) {
                    wheel_arrangementW = "0";
                } else {
                    wheel_arrangementW = URLEncoder.encode(wheel_arrangementW, "UTF-8");
                }


                tyre_size = idetTyreSize.getText().toString();
                if (tyre_size.equals("")) {
                    tyre_size = "";
                } else {
                    tyre_size = URLEncoder.encode(tyre_size, "UTF-8");
                }


                tyres_number = idetTyreNumber.getText().toString();
                if (tyres_number.equals("")) {
                    tyres_number = "0";
                } else {
                    tyres_number = URLEncoder.encode(tyres_number, "UTF-8");
                }

                liability_insurance_policy_number = URLEncoder.encode(idetInsurancePolicyNumber.getText().toString(), "UTF-8");
//                liability_insurance_valid_till = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
                free_insurance_policy_number = URLEncoder.encode(idetInsurancePolicyNumber2.getText().toString(), "UTF-8");
//                free_insurance_valid_till = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
//                icon_id = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
//                avatar_file_name = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
//                photo = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");
//                empty_group = URLEncoder.encode(idetEmailNotification.getText().toString(), "UTF-8");


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JSONObject params = new JSONObject();

            try {
//            params.put("email", "karn.neelmani@gmail.com");
//            params.put("name", "Neelmani Karn");

                params.put("id", 0);


                if (trackerID == null || trackerID.equals("")) {
                    params.put("tracker_id", null);
                } else {
                    params.put("tracker_id", Integer.parseInt(trackerID));
                }

                params.put("label", label);
                params.put("max_speed", 100);
                params.put("model", model);
                params.put("type", type);
                params.put("subtype", subType);
                params.put("garage_id", null);
                params.put("reg_number", reg_number);
                params.put("vin", vin);
                params.put("chassis_number", chassis_number);

                if (payload_weight == null) {
                    params.put("payload_weight", null);
                } else {
                    params.put("payload_weight", Integer.parseInt(payload_weight));
                }


                if (payload_height == null) {
                    params.put("payload_height", null);
                } else {
                    params.put("payload_height", Integer.parseInt(payload_height));
                }

                if (payload_length == null) {
                    params.put("payload_length", null);
                } else {
                    params.put("payload_length", Integer.parseInt(payload_length));
                }

                if (payload_width == null) {
                    params.put("payload_width", null);
                } else {
                    params.put("payload_width", Integer.parseInt(payload_width));
                }

                if (passengers == null) {
                    params.put("passengers", null);
                } else {
                    params.put("passengers", Integer.parseInt(passengers));
                }


                params.put("fuel_type", fuelType);
                params.put("fuel_grade", fuel_grade);


                if (norm_avg_fuel_consumption == null) {
                    params.put("norm_avg_fuel_consumption", null);
                } else {
                    params.put("norm_avg_fuel_consumption", Integer.parseInt(norm_avg_fuel_consumption));
                }


                if (fuel_tank_volume == null) {
                    params.put("fuel_tank_volume", null);
                } else {
                    params.put("fuel_tank_volume", Integer.parseInt(fuel_tank_volume));
                }


                params.put("wheel_arrangement", wheel_arrangementH + "*" + wheel_arrangementW);
                params.put("tyre_size", tyre_size);

                if (tyres_number == null) {
                    params.put("tyres_number", null);
                } else {
                    params.put("tyres_number", Integer.parseInt(tyres_number));
                }


                params.put("liability_insurance_policy_number", liability_insurance_policy_number);
                params.put("liability_insurance_valid_till", "2015-03-01");
                params.put("free_insurance_policy_number", free_insurance_policy_number);
                params.put("free_insurance_valid_till", null);
                params.put("icon_id", 55);
                params.put("avatar_file_name", null);
                JSONArray arr = new JSONArray();
                arr.put(1);
                arr.put(2);
                params.put("tags", arr);
                params.put("photo", "");
                params.put("empty_group", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String strParams = params.toString();
            String hashCode = Utils.getPreferences("hashCode", context);
            loginURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/vehicle/create?hash=" + hashCode + "&vehicle=" + strParams;
//            loginURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/vehicle/create?hash=1cd1c1b75c236fc930fd822424dfdd53&vehicle={%22id%22:0,%22tracker_id%22:100040,%22label%22:%22TestDevise%22,%22max_speed%22:111,%22model%22:%22%22,%22type%22:%22special%22,%22subtype%22:%22mobile_crane%22,%22reg_number%22:%22%22,%22vin%22:%22%22,%22chassis_number%22:%22%22,%22payload_weight%22:32000,%22payload_height%22:1,%22payload_length%22:1.2,%22payload_width%22:1,%22passengers%22:4,%22fuel_type%22:%22diesel%22,%22fuel_grade%22:%22%22,%22norm_avg_fuel_consumption%22:9,%22fuel_tank_volume%22:50,%22wheel_arrangement%22:%224*6%22,%22tyre_size%22:%22111%22,%22tyres_number%22:4,%22liability_insurance_policy_number%22:%22%22,%22liability_insurance_valid_till%22:%222015-03-01%22,%22free_insurance_policy_number%22:%22111%22,%22icon_id%22:55,%22tags%22:[1,2],%22photo%22:%22%22,%22empty_group%22:%22%22}";
            Log.d("URL", loginURL);
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCallError(loginURL, ServiceHandler.GET);
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
            if (AddNewAsset.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
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
                    Toast.makeText(context, "" + status, Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }

}
