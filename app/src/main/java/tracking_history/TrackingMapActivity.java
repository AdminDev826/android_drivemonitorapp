package tracking_history;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lineztech.farhan.vehicaltarckingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

public class TrackingMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ProgressDialog progressDialog;
    String hashCode, TrackerID;
    ArrayList<TrackDetail> trackDetails;
    Context context;
    String sdate, edate;
    ArrayList<LatLng> points;
    PolylineOptions lineOptions = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_maps);
        MarkerPoints = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;
        hashCode = Utils.getPreferences("hashCode", context);
        TrackerID = Utils.getPreferences("TrackerID", context);
        Intent i = getIntent();
        sdate = i.getStringExtra("sdate");
        edate = i.getStringExtra("edate");
        loadData();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void showRoute() {
        if(trackDetails.size() > 2 && mMap != null) {
            TrackDetail ST = trackDetails.get(0);
            TrackDetail ET = trackDetails.get(trackDetails.size()-1);
            LatLng origin = new LatLng(Double.parseDouble(ST.getLat()),Double.parseDouble(ST.getLng()));
            LatLng dest = new LatLng(Double.parseDouble(ET.getLat()),Double.parseDouble(ET.getLng()));

            MarkerPoints.add(origin);
            MarkerPoints.add(dest);

            MarkerOptions options = new MarkerOptions();
            MarkerOptions optionsDes = new MarkerOptions();

            options.position(origin);
            options.title("Start Point: " + ST.getAddress());
            optionsDes.position(dest);
            optionsDes.title("End Point: " + ET.getAddress());

            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            optionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.addMarker(options);
            mMap.addMarker(optionsDes);

            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    origin, 14);
            mMap.animateCamera(location);

            lineOptions = new PolylineOptions();
            points = new ArrayList<>();
            for (int i = 0; i < trackDetails.size(); i++) {
                TrackDetail T = trackDetails.get(i);
                double lat = Double.parseDouble(T.getLat());
                double lng = Double.parseDouble(T.getLng());
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.RED);
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    private void loadData() {
        trackDetails = new ArrayList<>();
        String url = "https://api.navixy.com/v2/track/read?tracker_id=" + TrackerID + "&from=" + sdate + "&to=" + edate + "&hash=" + hashCode;
        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                for (int i = 0; i < listtrackingHistory.length(); i++) {
                                    JSONObject c = listtrackingHistory.getJSONObject(i);
                                    TrackDetail track = new TrackDetail();
                                    track.setLat(c.getString("lat"));
                                    track.setLng(c.getString("lng"));
                                    track.setAddress(c.getString("address"));
                                    track.setStatellites(c.getString("satellites"));
                                    track.setGet_time(c.getString("get_time"));
                                    track.setMileage(c.getString("mileage"));
                                    track.setHeading(c.getString("heading"));
                                    track.setSpeed(c.getString("speed"));
                                    trackDetails.add(track);
                                }
                                showRoute();
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
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
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

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
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

}