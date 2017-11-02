package dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.fabric.sdk.android.Fabric;
import notification_alerts.list_notification.AdptorNotification;
import notification_alerts.list_notification.Notification;
import util.AppSingleton;
import util.Utils;


public class NotificationFragment extends Fragment {

    View v;
    Context context;
    ListView listTrackingHistory;
//    ProgressDialog progressDialog;
    ArrayList arrTrackingHistory;
    String urltrackingHistory;
    String currentTime;
    SwipeRefreshLayout swipeContainer;
    String hashCode, trackerID = "90824";
    int pageNumber = 1;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(v != null) return v;
        v = inflater.inflate(R.layout.notification, container, false);
        init();
        return  v;
    }

    private void init() {
        context = getActivity();
        Fabric.with(context, new Crashlytics());

        hashCode = Utils.getPreferences("hashCode", context);
        trackerID = Utils.getPreferences("TrackerID", context);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        listTrackingHistory = (ListView) v.findViewById(R.id.idlvTrackingHistory);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        listTrackingHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notification track = (Notification)arrTrackingHistory.get(i);
                LatLng latlong = (LatLng) track.getLatLng();
                String lat = String.valueOf(latlong.latitude);
                String lng = String.valueOf(latlong.longitude);

                Intent intent = new Intent(context, MapsMain.class);
                intent.putExtra("trackerID", trackerID);
                intent.putExtra("message", track.getMessage());
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
            }
        });

        arrTrackingHistory = new ArrayList<String>();
        hashCode = Utils.getPreferences("hashCode", context);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        currentTime = dateTime.replace("/", "-");
        try {
            String hashCode = Utils.getPreferences("hashCode", context);
            currentTime = URLEncoder.encode(currentTime, "utf-8");
            urltrackingHistory = "https://api.navixy.com/v2/history/unread/list?limit=120&type=tracker&hash="+hashCode;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(currentTime);
        loadData();
    }

    private void loadData(){
        swipeContainer.setRefreshing(true);
        arrTrackingHistory.clear();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, urltrackingHistory, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeContainer.setRefreshing(false);
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                JSONArray listtrackingHistory = response.getJSONArray("list");
                                for (int i = 0; i < listtrackingHistory.length(); i++) {
                                    JSONObject c = listtrackingHistory.getJSONObject(i);
                                    Notification track = new Notification();
                                    track.setId(c.getString("id"));
                                    track.setEvent(c.getString("event"));
                                    track.setMessage(c.getString("message"));
                                    track.setTime(c.getString("time"));
                                    track.setTracker_id(c.getString("tracker_id"));
                                    track.setAddress(c.getString("address"));

                                    JSONObject locationAry = c.getJSONObject("location");
                                    String lat = locationAry.getString("lat");
                                    String lng = locationAry.getString("lng");
                                    if(lat.length() > 0 && lng.length() > 0){
                                        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                                        track.setLatLng(latLng);
                                    }
                                    arrTrackingHistory.add(track);
                                }
                                if (arrTrackingHistory.isEmpty()) {
                                    Toast.makeText(context, "Sorry there is no record to show", Toast.LENGTH_SHORT).show();
                                }
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
                        swipeContainer.setRefreshing(false);
                        error.printStackTrace();
//                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void updateUI() {
        AdptorNotification adapter = new AdptorNotification(context, arrTrackingHistory);
        listTrackingHistory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
