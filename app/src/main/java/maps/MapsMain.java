package maps;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.Utils;


public class MapsMain<GeoPoint> extends FragmentActivity implements GoogleMap.OnMapClickListener {

    GoogleMap googleMap;
    ArrayList<LatLng> arrayPoints = null;
    ProgressDialog progressDialog;
    Context context;
    SupportMapFragment fm;
    AlertDialog.Builder alertDialogBuilder;
    GPSTracker gps;
    String cLocationURL;
    TextView btnCancel, btnShareLocation,idtvSpeed;
    String status, lat, lon, address, speed;
    String trackerID;
    String title;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.maps);
        gps = new GPSTracker(MapsMain.this);
        context = this;
        trackerID = getIntent().getStringExtra("trackerID");


        DatabaseHandler db = new DatabaseHandler(context);
         title = db.getTLabelbyID("" + trackerID);


        btnShareLocation = (TextView) findViewById(R.id.btnShareLocation);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        idtvSpeed = (TextView) findViewById(R.id.idtvSpeed);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationURl = "https://maps.google.com/maps?q=" + lat + "," + lon;

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "abc@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Location");
                emailIntent.putExtra(Intent.EXTRA_TEXT, locationURl);
                startActivity(Intent.createChooser(emailIntent, "Share Location..."));

            }
        });
        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        callAsynchronousTask();
        alertDialogBuilder = new AlertDialog.Builder(this);
        arrayPoints = new ArrayList<LatLng>();
        fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap(); // display zoom map
        if(checkLocationPermission())
            googleMap.setMyLocationEnabled(true);
//        googleMap.setMyLocationEnabled(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void onMapClick(LatLng point) {
    }

    public void onMapLongClick(LatLng point) {
    }

    public class AsyncTaskRunner extends AsyncTask<String, String, String> {


        protected void onPreExecute() {
            String hashCode = Utils.getPreferences("hashCode", context);
            cLocationURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackerID + "&hash=" + hashCode;
        }

        protected String doInBackground(String... params) {
            ServiceHandler shh = new ServiceHandler();
            String jsonStrr = shh.makeServiceCall(cLocationURL, ServiceHandler.GET);
            if (jsonStrr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStrr);
                    status = jsonObj.getString("success");
                    JSONObject state = jsonObj.getJSONObject("state");
                    JSONObject gps = state.getJSONObject("gps");
                   JSONObject location = gps.getJSONObject("location");
                   lat = location.getString("lat");
                    lon = location.getString("lng");
                    String last_update = gps.getString("updated");
                    speed = gps.getString("speed");
//                    connection_status = state.getString("connection_status");
//                    movement_status = state.getString("movement_status");




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String result) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }


            try {
                idtvSpeed.setText("Speed "+speed +" km/h");
                googleMap.getFocusedBuilding();
                if (googleMap != null) {
                    googleMap.setOnMapClickListener(MapsMain.this);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                } else {
                    Toast.makeText(getApplicationContext(), "You dont have the Google maps on your device", Toast.LENGTH_SHORT).show();

                }

                googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                googleMap.addMarker(markerOptions
                        .title(title)).showInfoWindow();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            AsyncTaskRunner runner = new AsyncTaskRunner();
                            runner.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
    }
}





