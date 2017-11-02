package com.lineztech.farhan.vehicaltarckingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DatabaseHandler;
import util.AppSingleton;
import util.Utils;

public class ParkingGuardActivity extends Activity {

    String hashCode, alarm_id, connectionStatusURL, strIgnition;
    Context context;
    MediaPlayer mPlayer;
    CountDownTimer timer = null;
    ProgressDialog progressDialog;
    private List<Boolean> jArrOutPutsEngineON = new ArrayList<Boolean>();
    private List<String> jPuttsOnPosition = new ArrayList<String>();
    private List<String> jPuttss = new ArrayList<String>();
    private List<String> jPuttssPositionOFF = new ArrayList<String>();
    private List<Boolean> jArrOutPuts = new ArrayList<Boolean>();
    private List<String> jPuttsOn = new ArrayList<String>();
    boolean canceled = false;
    String park_delay, park_audio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        hashCode = Utils.getPreferences("hashCode", context);
        Intent intent1 = getIntent();
        alarm_id = intent1.getStringExtra("alarm_id");
        strIgnition = intent1.getStringExtra("Ignition");
        String park_guard = Utils.getPreferences("park-guard_" + alarm_id, context);
        Log.e("guard_", " " + park_guard);
        park_audio = Utils.getPreferences("park-audio_" + alarm_id, context);
        park_delay = Utils.getPreferences("park-delay_" + alarm_id, context);

        if (strIgnition != null) {
            Utils.savePreferences("alarm_playing", "NO", context);// not delete
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Park Guard")
                    .setMessage(strIgnition)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
//                            Utils.savePreferences("auto-protect_" + alarm_id, "ON", context);
//                            Utils.savePreferences("park-guard_" + alarm_id, "OFF", context);
                            EngineGetStop();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            closeDefense();
                        }
                    });
            AlertDialog alertView = builder.create();
            alertView.setCancelable(false);
            alertView.setCanceledOnTouchOutside(false);
            alertView.show();
        } else if (park_guard.equals("OFF")) {
            Utils.savePreferences("alarm_playing", "NO", context);// not delete
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Park Guard")
                    .setMessage("Enable park guard feature ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utils.savePreferences("park-guard_" + alarm_id, "ON", context);
                            Utils.savePreferences("auto-protect_" + alarm_id, "OFF", context);
//                            openDialog();
                            String token = Utils.getPreferences("portalToken", context);
                            Map<String, String> params = new HashMap<>();
                            params.put("isParkGuardEnable", Utils.getPreferences("park-guard_" + alarm_id, context));
                            params.put("trackerId", alarm_id);
                            if (token.equals("")) {
                                loginToPortalServer();
                            } else {
                                sendParkingGuardToPortalServer(token, params);
                            }
                            closeDefense();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            closeDefense();
                        }
                    });
            AlertDialog alertView = builder.create();
            alertView.setCancelable(false);
            alertView.setCanceledOnTouchOutside(false);
            alertView.show();
        } else {
            openDialog();
        }
    }

    private void loginToPortalServer() {
        String url = String.format("%sapp/login",AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String token = "";
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.optBoolean("success")) {
                        token = responseObject.optString("token");
                        Utils.savePreferences("portalToken", token, context);
                    }
                    Map<String, String> params = new HashMap<>();

                    params.put("isParkGuardEnable", Utils.getPreferences("park-guard_" + alarm_id, context));
                    params.put("trackerId", alarm_id);
                    sendParkingGuardToPortalServer(token, params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginToPortalServer();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("navixyUserName", Utils.getPreferences("userName", context));
                params.put("navixyPassword", Utils.getPreferences("password", context));
                return params;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "loginPortal");
    }

    private void openDialog() {
        if (park_audio.equals("ON")) {
            AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            mPlayer = MediaPlayer.create(context, R.raw.notification);
            mPlayer.setLooping(true); // Set looping
            mPlayer.start();
        }
        if (park_delay.equals("ON")) {
            showDialogSoundOff();
        } else {
            EngineGetStop();
        }
    }

    private void sendParkingGuardToPortalServer(final String token, final Map<String, String> params) {
        String url = String.format("%sevent/syncParkGuardFromApp",AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Log.e("error", " " + new String(error.networkResponse.data));
                }
                sendParkingGuardToPortalServer(token, params);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "SendAutoProtectDataToServer");
    }

    public void showDialogSoundOff() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        AlertDialog alertView = null;
        View layout = getLayoutInflater().inflate(R.layout.counter_park_layout, null);
        final TextView txtTime = (TextView) layout.findViewById(R.id.counter_dialog_txt_time);
        final TextView txtShutdown = (TextView) layout.findViewById(R.id.counter_dialog_txt_shutdown);
        final TextView txtIgnore = (TextView) layout.findViewById(R.id.counter_dialog_txt_ignore);
        alertDialog.setView(layout);
        alertView = alertDialog.create();
        alertView.setCancelable(false);
        alertView.setCanceledOnTouchOutside(false);
        alertView.show();
        alertView.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final AlertDialog finalAlertView = alertView;
        txtShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                if (finalAlertView.isShowing()) {
                    finalAlertView.dismiss();
                }
                EngineGetStop();
            }
        });
        txtIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                if (canceled) {
                    closeDefense();
                } else {
                    txtTime.setText("activate park guard");
                    canceled = true;
                    timer.cancel();
                }
            }
        });
        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTime.setText("Park Guard will be activated in : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                if (finalAlertView.isShowing()) {
                    finalAlertView.dismiss();
                }
                EngineGetStop();
            }
        }.start();
    }

    private void EngineGetStart() {
        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        jArrOutPutsEngineON.clear();
        connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + alarm_id + "&hash=" + hashCode;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {
                                JSONObject state = response.getJSONObject("state");
                                JSONArray jarray = state.getJSONArray("outputs");
                                for (int i = 0; i < jarray.length(); i++) {
                                    jArrOutPutsEngineON.add((Boolean) jarray.get(i));
                                }
                                EngineApplyStart();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                closeDefense();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            closeDefense();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
//                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        closeDefense();
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
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smart_defense");
    }

    private void EngineApplyStart() {
        jPuttsOn.clear();
        jPuttsOnPosition.clear();
        for (int i = 0; i < jArrOutPutsEngineON.size(); i++) {
            try {
                String outp = jArrOutPutsEngineON.get(i).toString();
                if (outp.equals("false")) {
                    jPuttsOn.add("true");
                    int pos = i + 1;
                    jPuttsOnPosition.add("" + pos);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (jPuttsOn.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, "All outputs are already ON", Toast.LENGTH_LONG).show();
            closeDefense();
            return;
        }
        for (int i = 0; i < jPuttsOn.size(); i++) {
            connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + jPuttsOnPosition.get(i) + "&enable=true&tracker_id=" + alarm_id;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (finalI == jPuttsOn.size() - 1) {
                                progressDialog.dismiss();
                            }
                            try {
                                String status = response.getString("success");
                                if (finalI == jPuttsOn.size() - 1) {
                                    if (status.equals("true")) {
                                        Toast.makeText(context, "Action performed", Toast.LENGTH_LONG).show();
                                        DatabaseHandler db = new DatabaseHandler(context);
                                        db.updateTrackerConnectionOnly(alarm_id, "active");
                                        String trackerName = db.getTLabelbyID("" + alarm_id);
                                        db.close();
                                        if (strIgnition != null)
                                            showDialog(trackerName + " has been enabled");
                                        else
                                            showDialog(trackerName + " has been shutdown");
                                    } else {
                                        Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                        closeDefense();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                closeDefense();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressDialog.dismiss();
//                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            closeDefense();
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
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smart_defense");
        }
    }

    private void EngineGetStop() {
        progressDialog = ProgressDialog.show(context, "",
                "Loading...", true);
        connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + alarm_id + "&hash=" + hashCode;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {
                                JSONObject state = response.getJSONObject("state");
                                JSONArray jarray = state.getJSONArray("outputs");
                                for (int i = 0; i < jarray.length(); i++) {
                                    jArrOutPuts.add((Boolean) jarray.get(i));
                                }
                                EngineApplyStop();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                closeDefense();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            closeDefense();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
//                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        closeDefense();
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
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smart_defense");
    }

    private void EngineApplyStop() {
        jPuttss.clear();
        jPuttssPositionOFF.clear();

        for (int i = 0; i < jArrOutPuts.size(); i++) {
            try {
                String outp = jArrOutPuts.get(i).toString();
//                    if (outp.equals("true")) {
                jPuttss.add("false");
                int pos = i + 1;
                jPuttssPositionOFF.add("" + pos);
//                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jPuttss.size() == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, "All outputs are already off", Toast.LENGTH_LONG).show();
            closeDefense();
            return;
        }
        for (int i = 0; i < jPuttss.size(); i++) {
            connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + jPuttssPositionOFF.get(i) + "&enable=false&tracker_id=" + alarm_id;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (finalI == jPuttss.size() - 1) {
                                progressDialog.dismiss();
                            }
                            try {
                                String status = response.getString("success");
                                if (finalI == jPuttss.size() - 1) {
                                    if (status.equals("true")) {
                                        Toast.makeText(context, "Action performed", Toast.LENGTH_LONG).show();
                                        DatabaseHandler db = new DatabaseHandler(context);
                                        if (strIgnition == null)
                                            db.updateTrackerConnectionOnly(alarm_id, "signal_lost");
                                        String trackerName = db.getTLabelbyID("" + alarm_id);
                                        db.close();
                                        if (strIgnition != null)
                                            showDialog(trackerName + " has been enabled");
                                        else
                                            EngineGetStart();
                                    } else {
                                        Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                        closeDefense();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Fail ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                closeDefense();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            progressDialog.dismiss();
//                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            closeDefense();
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
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smart_defense");
        }
    }

    private void closeDefense() {
        Utils.savePreferences("alarm_playing", "NO", context);
        Intent i = new Intent(context, UserInfo.class);
        startActivity(i);
        finish();
    }

    private void showDialog(String title) {
        Utils.savePreferences("alarm_playing", "NO", context); // not delete
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(title);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                closeDefense();
            }
        });
        builder.show();
    }
}
