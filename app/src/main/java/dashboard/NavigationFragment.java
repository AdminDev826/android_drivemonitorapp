package dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.lineztech.farhan.vehicaltarckingapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import maps.TrackLocation;
import util.AppSingleton;
import util.Utils;

public class NavigationFragment extends Fragment {

    Thread updateStatus_thread;
    MapView mMapView;
    TextView txtHybrid, txtSatellite, txtTerrain;
    ArrayList<String> arrayListCourseName;
    View v;
    List<Trackers> trackerList;
    List<TrackLocation> trackers;
    List<Marker> markerList;

    String status, lat, lon;
    boolean firstLoad;
    boolean _isVisible = true;
    String trackerID;
    GoogleMap googleMap;
    ProgressDialog progressDialog;
    Context context;
    AlertDialog.Builder alertDialogBuilder;
    double latitude;
    double longitude;
    double geo1Int;
    double geo2Int;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout
        if(v != null) return v;
        v = inflater.inflate(R.layout.activity_map_fragment, container,
                false);
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            return null;
//        }
        initView(savedInstanceState);

        return v;
    }

    private void initView(Bundle savedInstanceState) {
        context = getActivity();
        Fabric.with(context, new Crashlytics());
        trackerID = Utils.getPreferences("TrackerID", context);
        firstLoad = true;
        trackerList = new ArrayList<Trackers>();
        trackers = new ArrayList<>();
        markerList = new ArrayList<Marker>();

        mMapView = (MapView) v.findViewById(R.id.mapView);
        txtHybrid = (TextView) v.findViewById(R.id.txtHybrid);
        txtSatellite = (TextView) v.findViewById(R.id.txtSatellite);
        txtTerrain = (TextView) v.findViewById(R.id.txtTerrain);

        mMapView.onCreate(savedInstanceState);
        arrayListCourseName = new ArrayList<>();
        setHasOptionsMenu(true);
        arrayListCourseName = new ArrayList<>();
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        mMapView.onResume();// needed to get the map to display immediately
        latitude = 0;
        longitude = 0;

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        txtSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });
        txtTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        googleMap = mMapView.getMap();
        if (googleMap != null) {
            if(checkLocationPermission())
                googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });
        } else {
            Toast.makeText(getActivity(), "You dont have the Google maps on your device", Toast.LENGTH_SHORT).show();
        }
        initData();
    }
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
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
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    private void initData() {
        TrackLocation tmp;
        DatabaseHandler dbb = new DatabaseHandler(getActivity());
        trackerList = dbb.getTrackerList();
        dbb.close();
        for (int i = 0; i < trackerList.size(); i++) {
            tmp = new TrackLocation();
            tmp.setLabel(trackerList.get(i).getTrackerLabel());
            tmp.setTrackerID(trackerList.get(i).getTrackerID());
            trackers.add(tmp);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        _isVisible = true;
        if(mMapView != null){
            mMapView.onResume();
            loadData();
            updateStatus_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (_isVisible) {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                        }
                        loadData();
                    }
                }
            });
            updateStatus_thread.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        _isVisible = false;
        if(mMapView != null)
            mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mMapView != null)
            mMapView.onLowMemory();
    }


    private void loadData(){
        String hashCode = Utils.getPreferences("hashCode", context);
        for (int i = 0; i < trackers.size(); i++) {
            String url = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackers.get(i).getTrackerID() + "&hash=" + hashCode;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                status = response.getString("success");
                                if(status.equals("true")){
                                    JSONObject state = response.getJSONObject("state");
                                    JSONObject gps = state.getJSONObject("gps");
                                    String strSpeed = gps.getString("speed");
                                    JSONObject jsonUserInfo = gps.getJSONObject("location");
                                    lat = jsonUserInfo.getString("lat");
                                    lon = jsonUserInfo.getString("lng");
                                    String lastUpdate = state.getString("last_update");
                                    String movement_status = state.getString("movement_status");
                                    Log.e("TAG", "Last update :" + lastUpdate + "--tracker:" + trackers.get(finalI).getTrackerID() + ", speed: " + strSpeed);
                                    if(firstLoad && trackerID.equals(trackers.get(finalI).getTrackerID())){
                                        latitude = Double.parseDouble(lat);
                                        longitude = Double.parseDouble(lon);
                                    }
                                    trackers.get(finalI).addData(strSpeed, lat, lon, movement_status);

                                    if(finalI == trackers.size() - 1)
                                        updateUI();
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
    private void updateUI() {
        if(firstLoad && (latitude != 0 && longitude != 0)){
            firstLoad = false;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
        for(Marker marker : markerList){
            marker.remove();
        }
        markerList.clear();
        for(TrackLocation tracker : trackers) {
            MarkerOptions markerOptions = new MarkerOptions();
            try {
                geo1Int = Double.parseDouble(tracker.getLastLat());
            } catch (Exception e) {
                geo1Int = 0.0;
            }
            try {
                geo2Int = Double.parseDouble(tracker.getLastLong());
            } catch (Exception e) {
                geo2Int = 0.0;
            }
            if (geo1Int != 0.0 && geo2Int != 0.0) {
                try {
                    Marker marker;
                    if (tracker.getStrSpeed().equals("0")){
                        marker = googleMap.addMarker(markerOptions
                                .position(new LatLng(geo1Int, geo2Int))
                                .title(tracker.getLabel())
                                .snippet(tracker.getMovement_status())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking))
//                                .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(context, "arrow", 50, 60)))
                        );
                        markerList.add(marker);
                    } else {
                        marker = googleMap.addMarker(markerOptions
                                .position(new LatLng(geo1Int, geo2Int))
                                .title(tracker.getLabel())
                                .snippet("Moving, Speed: " + tracker.getStrSpeed() + " km/h")
                                .icon(BitmapDescriptorFactory.fromBitmap(Utils.resizeMapIcons(context, "arrow", 40, 50)))
                                .rotation(tracker.getBearing())
                                .anchor(0.5f, 0.5f)
                        );
                        markerList.add(marker);
                    }
                    if(tracker.getUpdated()){
                        List<LatLng> ListLatLong = tracker.getLatLongList();
                        PolylineOptions polylines = new PolylineOptions().width(5).color(Color.BLUE);
                        if(ListLatLong.size() > 1){
                            for(int j = 0; j < ListLatLong.size() - 1; j++){
                                polylines.add(ListLatLong.get(j), ListLatLong.get(j + 1));
                            }
                        }
                        Polyline line = googleMap.addPolyline(polylines);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}