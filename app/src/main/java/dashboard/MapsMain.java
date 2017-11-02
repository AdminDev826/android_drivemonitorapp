package dashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import maps.GPSTracker;
import util.AppSingleton;
import util.Utils;

public class MapsMain<GeoPoint> extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    static final LatLng HAMBURG = new LatLng(-34.560534328753185, -58.450496099237384);
    Marker hamburg;

    static final LatLng KIEL = new LatLng(-34.656746124700099, -58.396479163201406);
    List<Trackers> trackerList = new ArrayList<Trackers>();
    String config_username;
    String config_password;
    String status, lat, lon, speed, address;
    String url;
    String cLocationURL;
    DatabaseHandler db;
    JSONArray jsonn;
    JSONObject c;
    ArrayList<String> lati, longi;
    GoogleMap googleMap;
    ArrayList<LatLng> arrayPoints = null;
    ProgressDialog progressDialog;
    Context context;
    SupportMapFragment fm;
    AlertDialog.Builder alertDialogBuilder;
    LinearLayout maps_cancel_layout;
    GPSTracker gps;
    double latitude;
    double longitude;
    double geo1Int;
    double geo2Int;
    LatLng ll;
    LinearLayout idllButtons;
    boolean notify_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maps);
        maps_cancel_layout = (LinearLayout) findViewById(R.id.maps_cancel_layout);

        latitude = 17.9932;
        longitude = -76.92631;
        db = new DatabaseHandler(getApplicationContext());
        lati = new ArrayList<>();
        longi = new ArrayList<>();
        idllButtons = (LinearLayout) findViewById(R.id.idllButtons);
        idllButtons.setVisibility(View.GONE);
        alertDialogBuilder = new AlertDialog.Builder(this);
        arrayPoints = new ArrayList<LatLng>();
        fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap(); // display zoom map
//        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        ll = new LatLng(latitude, longitude);
        context = this;

        String strLat = getIntent().getStringExtra("lat");
        String strLng = getIntent().getStringExtra("lng");
        String message = getIntent().getStringExtra("message");
        if(strLat.length() > 0 && strLng.length() > 0 && message.length() > 0){
            ((LinearLayout.LayoutParams)maps_cancel_layout.getLayoutParams()).height = 0;
            notify_flag = true;
            LatLng latLng = new LatLng(Double.parseDouble(strLat), Double.parseDouble(strLng));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.title(message);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.addMarker(options);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }else{
            progressDialog = new ProgressDialog(context);
            progressDialog = ProgressDialog.show(context, "",
                    "Loading...", true);

//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            runner.execute();
            updateData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Your logic that service will perform will be placed here
                    //In this example we are just looping and waits for 1000 milliseconds in each loop.
                    for (int i = 0; i > -1; i++) {
                        try {

                            Thread.sleep(5000);
                        } catch (Exception e) {
                        }

                        updateData();
//                        AsyncTaskRunner runner = new AsyncTaskRunner();
//                        runner.execute();
                        Log.d("log", " Loop has been running ");
//                    Toast.makeText(context, "Loop has been running", Toast.LENGTH_LONG).show();

                    }

                    //Stop service once it finishes its task

                }
            }).start();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 13));
        }
    }

    public void onMapClick(LatLng point) {
    }

    @Override
    public void onMapLongClick(LatLng point) {
    }


    private void updateData(){
        String hashCode = Utils.getPreferences("hashCode", context);
        lati.clear();
        longi.clear();
        trackerList = db.getTrackerList();
        for (int i = 0; i < trackerList.size(); i++) {
            String url = "https://api.navixy.com/v2/tracker/get_last_gps_point?tracker_id=" + trackerList.get(i).getTrackerID() + "&hash="+hashCode;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                status = response.getString("success");
                                if(status.equals("true")){
                                    JSONObject jsonUserInfo = response.getJSONObject("value");
                                    lat = jsonUserInfo.getString("lat");
                                    lon = jsonUserInfo.getString("lng");
                                    lati.add(lat);
                                    longi.add(lon);

                                    if (lati.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "Sorry there is no record to show", Toast.LENGTH_LONG).show();
                                    } else {
                                        int x = 0;
                                        while (x != lati.size()) {
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            String lat = lati.get(x);
                                            String lon = longi.get(x);
                                            try {
                                                geo1Int = Double.parseDouble(lat);
                                            } catch (Exception e) {
                                                geo1Int = 0.0;
                                            }
                                            try {
                                                geo2Int = Double.parseDouble(lon);
                                            } catch (Exception e) {
                                                geo2Int = 0.0;
                                            }
                                            if (geo1Int != 0.0 && geo2Int != 0.0) {
                                                try {
                                                    markerOptions.position(new LatLng(31.554606, 74.357158));
                                                    markerOptions.position(new LatLng(31.418714, 73.079107));
                                                    markerOptions.position(new LatLng(geo1Int, geo2Int));
                                                    googleMap.addMarker(markerOptions);
                                                    Log.d("Latparse", "" + geo1Int);
                                                    Log.d("Longparse", "" + geo2Int);

                                                } catch (Exception e) {

                                                }
                                            }
                                            x++;
                                        }
                                    }
                                }else{
                                    Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
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
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            ServiceHandler shh = new ServiceHandler();
            lati.clear();
            longi.clear();
            trackerList = db.getTrackerList();
            for (int i = 0; i < trackerList.size(); i++) {
//                listTrackerLable.add(trackerList.get(i).getTrackerID());

                String jsonStrr = shh.makeServiceCall("https://api.navixy.com/v2/tracker/get_last_gps_point?tracker_id="+trackerList.get(i).getTrackerID()+"&hash=a431e0079fdcf15b7f5f8ebd14abd28f", ServiceHandler.GET);
                Log.d("Response: ", "> " + jsonStrr);
                if (jsonStrr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStrr);
                        status = jsonObj.getString("success");
                        JSONObject jsonUserInfo = jsonObj.getJSONObject("value");
                        lat = jsonUserInfo.getString("lat");
                        lon = jsonUserInfo.getString("lng");
                        lati.add(lat);
                        longi.add(lon);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServiceHandler", "Couldn't get any data from the url");
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (lati.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Sory there is no record to show", Toast.LENGTH_LONG).show();
            } else {

                int x = 0;
                while (x != lati.size()) {
                    MarkerOptions markerOptions = new MarkerOptions();
//                    Toast.makeText(getApplicationContext(), " lat: " + lati.get(x) + "\n " + x, Toast.LENGTH_SHORT).show();

                    String lat = lati.get(x);
                    String lon = longi.get(x);

                    try {
                        geo1Int = Double.parseDouble(lat);
                    } catch (Exception e) {
                        geo1Int = 0.0;
                    }

                    try {
                        geo2Int = Double.parseDouble(lon);
                    } catch (Exception e) {
                        geo2Int = 0.0;
                    }

                    if(geo1Int!=0.0&&geo2Int!=0.0) {

                        try{
                            markerOptions.position(new LatLng(31.554606, 74.357158));
                            markerOptions.position(new LatLng(31.418714, 73.079107));
                            markerOptions.position(new LatLng(geo1Int, geo2Int));
//                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.texiicon);
//                            markerOptions.icon(icon);
                            googleMap.addMarker(markerOptions);
                            Log.d("Latparse", "" + geo1Int);
                            Log.d("Longparse", "" + geo2Int);

                        }

                        catch (Exception e){

                        }


                    }
                    x++;
                }
            }

        }

        protected void onProgressUpdate(String... text) {
        }
    }
}