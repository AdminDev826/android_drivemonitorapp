package assing_task;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lineztech.farhan.vehicaltarckingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import active_hour.ActiveHour;
import db.DatabaseHandler;
import util.AppSingleton;
import util.Utils;

public class AntiHijackingCountDownTimer extends Service {

    CountDownTimer countDownTimer = null;
    Context context;
    String tracker_id;
    int countdownTimerValue;
    MediaPlayer mediaPlayer;

    String status, description, hashCode;
    private List<Boolean> jArrOutPutsEngineON = new ArrayList<Boolean>();
    private List<Boolean> jArrOutPuts = new ArrayList<Boolean>();
    private List<String> jPuttsOnPosition = new ArrayList<String>();
    private List<String> jPuttss = new ArrayList<String>();
    boolean audioMuteState = false;

    @Override
    public void onCreate() {
        context = this;
        tracker_id = Utils.getPreferences("TrackerID", context);
        hashCode = Utils.getPreferences("hashCode", context);
        mediaPlayer = MediaPlayer.create(context, R.raw.anhti_hijacking);
        countdownTimerValue = 1;
        String antiHijackingCountDownTime = Utils.getPreferences("anti-hijacking_countdown_time_" + tracker_id, context);
        if (antiHijackingCountDownTime.equals("")) {
            countdownTimerValue *= 60;
            countdownTimerValue *= 1000;
        } else {
            countdownTimerValue = Integer.parseInt(antiHijackingCountDownTime);
            countdownTimerValue *= 60;
            countdownTimerValue *= 1000;
        }

        countDownTimer = new CountDownTimer(countdownTimerValue, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = (millisUntilFinished / 1000);
                String remainingTime = getTimeString(millisUntilFinished);
                Log.e("sec", " " + remainingTime);
                sendBroadcast(new Intent("AntiHijacking").putExtra("time", remainingTime));
                if (seconds <= 60 && seconds % 15 == 0) {
                    mediaPlayer.start();
                }
            }

            @Override
            public void onFinish() {
                Utils.savePreferences("Sos_pressed_" + tracker_id, "false", context);
                sendBroadcast(new Intent("AntiHijacking").putExtra("time", "0"));
                EngineGetStop();
            }
        }.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public static String getTimeString(long millis) {
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        if (minutes < 1) {
            return String.format(Locale.getDefault(), "%02d", seconds);
        }
        return String.format(Locale.getDefault(), "%02d", minutes) + " : " + String.format(Locale.getDefault(), "%02d", seconds);
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        Utils.savePreferences("Sos_pressed_" + tracker_id, "false", context);
        Log.e("service", "Timer cancelled");
        super.onDestroy();
    }

    public static final int MUTE_AUDIO = 1;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MUTE_AUDIO:
                    if (!audioMuteState) {
                        mediaPlayer.setVolume(0, 0);
                        audioMuteState = true;
                    } else {
                        audioMuteState = false;
                        mediaPlayer.setVolume(1, 1);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void EngineGetStop() {
        jArrOutPuts.clear();
        String description = "";
        String connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + tracker_id + "&hash=" + hashCode;
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
//                        Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
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

    private void EngineApplyStop() {
        jPuttss.clear();
        String description = "";

        if (jArrOutPuts.size() == 0) {
            Toast.makeText(context, "All outputs are already off", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < jArrOutPuts.size(); i++) {
            String connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + (i + 1) + "&enable=true&tracker_id=" + tracker_id;
            final int finalI = i;
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, connectionStatusURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("success");
                                if (status.equals("true")) {
                                    if (finalI == jArrOutPuts.size() - 1) {
                                        DatabaseHandler db = new DatabaseHandler(context);
                                        String trackerName = db.getTLabelbyID("" + tracker_id);
//                                        Toast.makeText(context, trackerName + " has been disabled !", Toast.LENGTH_LONG).show();
                                        sendBroadcast(new Intent("AntiHijacking").putExtra("shutDownComplete", true).putExtra("DeviceName", trackerName));
                                        stopSelf();
//                                        sendBroadcast(new Intent("AntiHijacking").putExtra("shutDownComplete",true));
//                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                                        builder.setMessage(trackerName + " has been disabled !");
//                                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//                                        builder.show();
                                        db.updateTrackerConnectionOnly(tracker_id, "signal_lost");
                                        db.close();
                                    }
                                } else {
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
//                            Toast.makeText(context, "Server Error ! \n" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    }
}
