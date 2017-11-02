package tracking_history;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.GeoPoint;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AdapterTrackingHistory extends BaseAdapter {
    Context context;
    String layoutRating;
    float rating;
    String CourseID, CouresName;
    ProgressDialog progressDialog;
    int showLastLayOut = 0;
    List<LatLng> list;
    protected List<Track> listSearchedCourses;
    LayoutInflater inflater;

    public AdapterTrackingHistory(Context context, List<Track> listSearchedCourses) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.CourseID = CourseID;
        this.CouresName = CouresName;
    }

    public int getCount() {
        return listSearchedCourses.size();
    }

    public Track getItem(int position) {
        return listSearchedCourses.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Track track = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.adaptor_tracking_history, parent, false);
            holder.idtvStartTime = (TextView) convertView.findViewById(R.id.idtvStartTime);
            holder.idtvEndTime = (TextView) convertView.findViewById(R.id.idtvEndTime);
            holder.idtvStartAddress = (TextView) convertView.findViewById(R.id.idtvStartAddress);
            holder.idtvEndAddress = (TextView) convertView.findViewById(R.id.idtvEndAddress);
            holder.idtvDistance = (TextView) convertView.findViewById(R.id.idtvDistance);
            holder.idtvTotaltime = (TextView) convertView.findViewById(R.id.idtvTotaltime);
            holder.textview_header_separator = (TextView) convertView.findViewById(R.id.textview_header_separator);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String starTime = track.getStart_date();
        String endTime = track.getEnd_date();

        starTime = starTime.replace("-", "/");
        endTime = endTime.replace("-", "/");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        try {
            Date date1 = simpleDateFormat.parse(starTime);
            Date date2 = simpleDateFormat.parse(endTime);
            System.out.println("checking date" + new SimpleDateFormat("hh:mm a").format(date1));
            holder.idtvStartTime.setText(new SimpleDateFormat("hh:mm a").format(date1));
            holder.idtvEndTime.setText(new SimpleDateFormat("hh:mm a").format(date2));
        } catch (ParseException ex) {
            System.out.println("Exception " + ex);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        try {
            Date date1 = sdf.parse(starTime);
            Date date2 = sdf.parse(endTime);
            long diff = date2.getTime() - date1.getTime();//as given

            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);

            Log.d("days", "" + days);
            Log.d("minuts", "" + minutes);
            Log.d("hours", "" + hours);

            seconds = seconds / 100;

            if (minutes < 60) {
                holder.idtvTotaltime.setText(minutes + "min " + seconds + "s");
            } else if (hours > 0 && hours < 25) {
                holder.idtvTotaltime.setText(hours + "hours" + minutes + "min");
            } else if (days > 0) {
                holder.idtvTotaltime.setText(days + " days ago");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.idtvStartAddress.setText(track.getStart_address());
        holder.idtvEndAddress.setText(track.getEnd_address());
        holder.idtvDistance.setText(track.getLength() + " km");


        if(position>0){
            String starTime1 = listSearchedCourses.get(position).getStart_date();
            String starTime2 = listSearchedCourses.get(position-1).getStart_date();

            starTime1 = starTime1.substring(0, 10);
            starTime1 = starTime1.replace("-", ".");

            starTime2 = starTime2.substring(0, 10);
            starTime2 = starTime2.replace("-", ".");

            if (starTime1.equals(starTime2)) {

                holder.textview_header_separator.setText(starTime1);
                holder.textview_header_separator.setVisibility(View.GONE);
            } else {
                holder.textview_header_separator.setText(starTime1);
                holder.textview_header_separator.setVisibility(View.VISIBLE);
            }

        }

        if(position==0){
            String starTime2 = listSearchedCourses.get(position).getStart_date();

            starTime2 = starTime2.substring(0, 10);
            starTime2 = starTime2.replace("/", ".");

            holder.textview_header_separator.setText(starTime2);



        }





//        listSearchedCourses.get(0).getStart_date();
//        listSearchedCourses.get(1).getStart_date();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(context, "Position: " + position, Toast.LENGTH_SHORT).show();

                progressDialog = ProgressDialog.show(context, "",
                        "Loading...", true);
                AsyncTaskRunnerLatLong runner = new AsyncTaskRunnerLatLong(track.getStart_address(), track.getEnd_address());
                runner.execute();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView idtvStartTime, idtvEndTime, idtvStartAddress, idtvEndAddress, idtvDistance, idtvTotaltime, textview_header_separator;
    }

    String sAddress;
    String eAddress;
    String latStartAd;
    String lngStartAd;
    String latEndAd;
    String lngEndAd;

    private class AsyncTaskRunnerLatLong extends AsyncTask<String, String, String> {
        public AsyncTaskRunnerLatLong(String startAddress, String endAddress) {

            sAddress = startAddress;
            eAddress = endAddress;
        }

        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {


            StringBuilder stringBuilder = new StringBuilder();
            try {

                sAddress = sAddress.replace(" ", "%20");
                String address = "Ascott%20Dr,%20Old%20Harbour,%20Jamaica";
                HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + sAddress + "&sensor=false");
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                stringBuilder = new StringBuilder();
                response = client.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(stringBuilder.toString());


                JSONArray arrayResult = jsonObject.getJSONArray("results");
                JSONObject main = arrayResult.getJSONObject(0);
                JSONObject geometry = main.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                latStartAd = lat;
                lngStartAd = lng;
                Log.d("lat: ", "" + lat);
                Log.d("lng: ", "" + lng);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            StringBuilder stringBuilderr = new StringBuilder();
            try {

                eAddress = eAddress.replace(" ", "%20");

                HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + eAddress + "&sensor=false");
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                stringBuilderr = new StringBuilder();
                response = client.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilderr.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObjectEnd;
            try {
                jsonObjectEnd = new JSONObject(stringBuilderr.toString());


                JSONArray arrayResult = jsonObjectEnd.getJSONArray("results");
                JSONObject main = arrayResult.getJSONObject(0);
                JSONObject geometry = main.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                String lat = location.getString("lat");
                String lng = location.getString("lng");
                latEndAd = lat;
                lngEndAd = lng;
                Log.d("lat: ", "" + lat);
                Log.d("lng: ", "" + lng);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("latStartAd", latStartAd);
            intent.putExtra("lngStartAd", lngStartAd);
            intent.putExtra("latEndAd", latEndAd);
            intent.putExtra("lngEndAd", lngEndAd);
            context.startActivity(intent);

//            Toast.makeText(context, "Pressed", Toast.LENGTH_SHORT).show();
        }

        protected void onProgressUpdate(String... text) {
        }

    }


}
