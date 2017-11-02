package assing_task;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.CircleOptions;
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

import io.fabric.sdk.android.Fabric;
import maps.GPSTracker;
import util.Utils;


public class MapAssign<GeoPoint> extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    GoogleMap googleMap;
    ArrayList<LatLng> arrayPoints = null;
    ProgressDialog progressDialog;
    Context context;
    SupportMapFragment fm;
    AlertDialog.Builder alertDialogBuilder;
    GPSTracker gps;
    String cLocationURL;
    TextView btnCancel, btnShareLocation;
    String status, lat, lon, address;
    String trackerID;
    String strlat;
    LinearLayout idllButtons;
    String strLng;
    String strRadoius;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.maps);
        gps = new GPSTracker(MapAssign.this);
        idllButtons = (LinearLayout) findViewById(R.id.idllButtons);
        idllButtons.setVisibility(View.GONE);
        context = this;

        strRadoius = getIntent().getStringExtra("radios");
        strlat = getIntent().getStringExtra("lat");
        strLng = getIntent().getStringExtra("lng");
        title = getIntent().getStringExtra("title");

        arrayPoints = new ArrayList<LatLng>();
        fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap(); // display zoom map

        try {
            googleMap.getFocusedBuilding();
            if (googleMap != null) {
                googleMap.setOnMapClickListener(MapAssign.this);
                if (ActivityCompat.checkSelfPermission(MapAssign.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapAssign.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMapLongClickListener(MapAssign.this);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(strlat), Double.parseDouble(strLng))).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            } else {
                Toast.makeText(getApplicationContext(), "You dont have the Google maps on your device", Toast.LENGTH_SHORT).show();

            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(Double.parseDouble(strlat), Double.parseDouble(strLng)));
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

            googleMap.addMarker(markerOptions);

            LatLng latLng = new LatLng(Double.parseDouble(strlat),Double.parseDouble(strLng));

            drawCircle(latLng, Integer.parseInt(strRadoius));

        } catch (Exception e) {
        }


    }


    public void onMapClick(LatLng point) {
    }

    public void onMapLongClick(LatLng point) {
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





}





