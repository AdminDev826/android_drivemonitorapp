package geofence_21;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import maps.GPSTracker;
import util.Utils;


public class MapGeoFence<GeoPoint> extends FragmentActivity {

    GoogleMap googleMap;
    ArrayList<LatLng> arrayPoints = null;
    ProgressDialog progressDialog;
    Context context;
    SupportMapFragment fm;
    AlertDialog.Builder alertDialogBuilder;
    GPSTracker gps;
    String cLocationURL;
    String url;
    TextView btnCancel, btnShareLocation;
    String status, lat, lon, address;
    String trackerID;
    double longitude;
    LinearLayout idllButtons;
    double latitude;
    String strRadoius;
    int maxRadius;
    int radious;
    LatLng ll;
    protected Marker myPositionMarker;
    TextView idtvSavegeoFence;
    SeekBar seekBarS1;
    LinearLayout idllsetGeo;
    TextView iddtvS1;
    Double longitude_c = 0.0;
    String title;
    Double latitude_c = 0.0;
    String trackerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.maps_geofence);

        seekBarS1 = (SeekBar) findViewById(R.id.seekBarMap);
        iddtvS1 = (TextView) findViewById(R.id.iddtvMapRadious);
        idtvSavegeoFence = (TextView) findViewById(R.id.idtvSavegeoFence);
        idllsetGeo = (LinearLayout) findViewById(R.id.idllsetGeo);
        context = this;

        DatabaseHandler db = new DatabaseHandler(context);
        trackerID = Utils.getPreferences("TrackerID", context);
        trackerName = db.getTLabelbyID("" + trackerID);
        gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        arrayPoints = new ArrayList<LatLng>();
        fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap(); // display zoom map

        idtvSavegeoFence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (maxRadius < 20) {
                    Toast.makeText(context, "Radios must be greater than 20 k.m", Toast.LENGTH_LONG).show();
                }else {
                    progressDialog = ProgressDialog.show(context, "",
                            "Saving Geo fence...", true);
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute();
                }

            }
        });

        try{
            String lat = db.getLatbyTID(trackerID);
            String lon = db.getLngbyTID(trackerID);

            latitude_c = Double.parseDouble(lat);
            longitude_c = Double.parseDouble(lon);
            ll = new LatLng(latitude_c, longitude_c);
        }catch (Exception e){
            e.printStackTrace();
        }


        if (googleMap != null) {

            googleMap.setMyLocationEnabled(true);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude_c, longitude_c)).zoom(10).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            googleMap.addMarker(new MarkerOptions()
                    .position(ll)
                    .title(trackerName + " is here !")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            loadData();
        }


        seekBarS1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onStopTrackingTouch(SeekBar bar) {
                int value = bar.getProgress(); // the value of the seekBar progress
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                iddtvS1.setText(paramInt + " K.M"); // here in textView the percent will be shown
                googleMap.clear();
                drawCircle(new LatLng(latitude_c, longitude_c), paramInt * 1000);
                maxRadius = paramInt;
                createMarker(latitude_c, longitude_c);
                radious = paramInt;
                Utils.savePreferencesInt("radious", paramInt, context);
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                googleMap.clear();
                seekBarS1.setProgress(0);
                idllsetGeo.setVisibility(View.VISIBLE);
                idtvSavegeoFence.setVisibility(View.VISIBLE);
//                latitude_c = latLng.latitude;
//                longitude_c = latLng.longitude;
//                googleMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .title("You are here")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
        });
    }

    private void loadData() {
        AsyncTaskLoadData loader = new AsyncTaskLoadData();
        loader.execute();
    }

    private void drawCircle(LatLng point, int radious) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(radious);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x500000ff);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        googleMap.addCircle(circleOptions);

    }
    private class AsyncTaskLoadData extends AsyncTask<String, String, String>{

        int rad = 0;
        double lat = 0.0, lng = 0.0;

        @Override
        protected String doInBackground(String... params) {
            try{
                String datas = Utils.getPreferences("geofence-" + trackerID + "-trackerName-" + trackerName, context);
                String[] elements = datas.split(", ");
                for(String s1: elements) {
                    String[] keyValue = s1.split(":");
                    if(keyValue[0].equals("radius")){
                        rad = Integer.parseInt(keyValue[1]);
                    }
                    else if(keyValue[0].equals("lat")){
                        lat = Double.parseDouble(keyValue[1]);
                    }
                    else if(keyValue[0].equals("lng")){
                        lng = Double.parseDouble(keyValue[1]);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                LatLng latLng = new LatLng(lat, lng);
                drawCircle(latLng, rad * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        protected void onPreExecute() {

            JSONObject params = new JSONObject();

            try {
//                params.put("id", 62271);
                params.put("label", "label");
                params.put("type", "circle");
                params.put("color", "E91E63");
                params.put("address", "address");
                params.put("radius", radious);
                JSONObject center = new JSONObject();
                center.put("lat", latitude_c);
                center.put("lng", longitude_c);
                params.put("center", center);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String strParams = params.toString();
            String hashCode = Utils.getPreferences("hashCode", context);
            url = "http://smarttrack.iconnectcloudsolutions.com/api-v2/zone/create?hash=" + hashCode + "&zone=" + strParams;
            Log.d("URL", url);
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCallError(url, ServiceHandler.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Response: ", "> " + jsonStr);
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

            progressDialog.dismiss();

            try {
                if (status.equals("true")) {
                    saveGeofence();
                    Toast.makeText(context, "Saved Successfully", Toast.LENGTH_LONG).show();
                    idllsetGeo.setVisibility(View.GONE);
                    idtvSavegeoFence.setVisibility(View.GONE);
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

    private void saveGeofence() {
        String data = "radius:" + radious + ", lat:" + latitude_c + ", lng:" + longitude_c;

        Utils.savePreferences("geofence-" + trackerID + "-trackerName-" + trackerName, data, context);
    }


    protected void createMarker(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        myPositionMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

}





