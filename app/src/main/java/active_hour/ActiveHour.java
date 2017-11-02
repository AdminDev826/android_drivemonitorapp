package active_hour;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import assing_task.AntiHijackingCountDownTimer;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;


/**
 * Created by Farhan on 8/4/2016.
 */
public class ActiveHour extends Activity implements TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Context context;
    TextView idbtnGReportMain, seekSeconds, saveCountDownTimer, counterTextView;
    TextView idtvDateRange1, idtvDateRange2, idtvListActivHours, selectedDay, timerDisplay, cancelEvent, shutdownVehicle;
    TextView txtMon, txtTu, txtWen, txtTh, txtFr, txtSa, txtSu;
    LinearLayout selectedLayout, generalLayout, antiHiJackingSettingContainer, antiHiJackingActiveContainer;
    Switch sw_defence, sw_guard, sw_park_shut, sw_park_audio, sw_park_delay, sw_park_imm, idSAlarm_ignition, idSAlarm_parking, switchBattery, switchAntihiJacking, switchEcalling;
    SeekBar seekbarSeconds;
    boolean isStarTime = true;

    String startTime;
    String endTime;
    String saved_tracker = "", trackerID;
    String day;

    boolean isMoCheck = true;
    boolean is2Check = true;
    boolean is3Check = true;
    boolean is4Check = true;
    boolean is5Check = true;
    boolean is6Check = true;
    boolean is7Check = true;
    DateFormat timeFormat = new SimpleDateFormat("hh:mm");
    DateFormat requiredTimeFormat = new SimpleDateFormat("hh:mm a");
    private BroadcastReceiver smartDefenceDataSyncReceiver;
    String sosPressed;
    CountDownTimer countDownTimer;
    MediaPlayer mPlayer;
    boolean countDownTimerRunning = false;
    BroadcastReceiver broadcastReceiver;
    ProgressDialog progressDialog;
    private List<Boolean> jArrOutPutsEngineON = new ArrayList<Boolean>();
    private List<Boolean> jArrOutPuts = new ArrayList<Boolean>();
    private List<String> jPuttsOnPosition = new ArrayList<String>();
    private List<String> jPuttss = new ArrayList<String>();
    String status, description, hashCode;
    ImageView incrementerView, decrementerView;

    Messenger mService = null;
    boolean mBound, shutDownComplete;
    AlertDialog alertView = null;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.active_hour);
        context = this;
        trackerID = Utils.getPreferences("TrackerID", context);
        hashCode = Utils.getPreferences("hashCode", context);
        idtvDateRange1 = (TextView) findViewById(R.id.idtvDateRange1);
        idtvDateRange2 = (TextView) findViewById(R.id.idtvDateRange2);
        idbtnGReportMain = (TextView) findViewById(R.id.idbtnGReportMain);
        idtvListActivHours = (TextView) findViewById(R.id.idtvListActivHours);
        selectedDay = (TextView) findViewById(R.id.active_hour_txt);

        seekSeconds = (TextView) findViewById(R.id.txtSeconds);
        seekbarSeconds = (SeekBar) findViewById(R.id.seekBarSeconds);
        idSAlarm_ignition = (Switch) findViewById(R.id.idSAlarm_ignition);
        idSAlarm_parking = (Switch) findViewById(R.id.idSAlarm_parking);
        switchBattery = (Switch) findViewById(R.id.switchBattery);
        sw_defence = (Switch) findViewById(R.id.switch_autoprotect);
        sw_guard = (Switch) findViewById(R.id.switch_parkguard);
        sw_park_shut = (Switch) findViewById(R.id.switch_park_shut);
        sw_park_audio = (Switch) findViewById(R.id.switch_park_audio);
        sw_park_delay = (Switch) findViewById(R.id.switch_park_delay);
        sw_park_imm = (Switch) findViewById(R.id.switch_park_imm);

        switchAntihiJacking = (Switch) findViewById(R.id.switch_antihijacking);
        saveCountDownTimer = (TextView) findViewById(R.id.savecountdowntimer);
        antiHiJackingSettingContainer = (LinearLayout) findViewById(R.id.antihijackingsettingcontainer);
        antiHiJackingActiveContainer = (LinearLayout) findViewById(R.id.antihijackingactive);

        selectedLayout = (LinearLayout) findViewById(R.id.ah_selected_layout);
        generalLayout = (LinearLayout) findViewById(R.id.ah_general_layout);

        txtMon = (TextView) findViewById(R.id.active_hour_txt_mon);
        txtTu = (TextView) findViewById(R.id.active_hour_txt_Tu);
        txtWen = (TextView) findViewById(R.id.active_hour_txt_We);
        txtTh = (TextView) findViewById(R.id.active_hour_txt_Th);
        txtFr = (TextView) findViewById(R.id.active_hour_txt_Fr);
        txtSa = (TextView) findViewById(R.id.active_hour_txt_Sa);
        txtSu = (TextView) findViewById(R.id.active_hour_txt_Su);

        mPlayer = MediaPlayer.create(context, R.raw.notification);

        timerDisplay = (TextView) findViewById(R.id.timerDisplay);
        cancelEvent = (TextView) findViewById(R.id.cancelevent);
        shutdownVehicle = (TextView) findViewById(R.id.shutdown);
        incrementerView = (ImageView) findViewById(R.id.incrementCounter);
        decrementerView = (ImageView) findViewById(R.id.decrementCounter);
        counterTextView = (TextView) findViewById(R.id.counterTextView);
        switchEcalling = (Switch) findViewById(R.id.ecallingswitch);

        smartDefenceDataSyncReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                initUI();
            }
        };

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String seconds = intent.getStringExtra("time");
                boolean shutDownComplete = intent.getBooleanExtra("shutDownComplete", false);
                String trackerName = intent.getStringExtra("DeviceName");
                Log.e("s", "sss" + seconds + shutDownComplete + trackerName);
                if (alertView != null && alertView.isShowing()) {
                    TextView timerDisplay = (TextView) ((AlertDialog) alertView).findViewById(R.id.counter_dialog_txt_time);
                    timerDisplay.setText("Initiating shutdown sequence in : " + seconds);
                    if (seconds.equals("0")) {
                        Utils.savePreferences("Sos_pressed_" + trackerID, "false", context);
                        sosPressed = Utils.getPreferences("Sos_pressed_" + trackerID, context);
                        if (alertView.isShowing()) {
                            alertView.dismiss();
                        }
                        if (mBound) {
                            mBound = false;
                            unbindService(mConnection);
                        }
                        progressDialog = ProgressDialog.show(context, "",
                                "Loading...", true);
                    }
                }

                if (shutDownComplete) {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage(trackerName + " has been disabled !");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        };

        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        final Date dateobj = new Date();

        sw_defence.setOnCheckedChangeListener(this);
        sw_defence.setOnClickListener(this);
        sw_guard.setOnClickListener(this);
        sw_guard.setOnCheckedChangeListener(this);
        sw_park_shut.setOnCheckedChangeListener(this);
        sw_park_audio.setOnCheckedChangeListener(this);
        sw_park_audio.setOnClickListener(this);
        sw_park_delay.setOnCheckedChangeListener(this);
        sw_park_imm.setOnCheckedChangeListener(this);
        idSAlarm_ignition.setOnCheckedChangeListener(this);
        idSAlarm_parking.setOnCheckedChangeListener(this);
        switchBattery.setOnCheckedChangeListener(this);

        switchAntihiJacking.setOnCheckedChangeListener(this);
        switchAntihiJacking.setOnClickListener(this);
        saveCountDownTimer.setOnClickListener(this);
        incrementerView.setOnClickListener(this);
        decrementerView.setOnClickListener(this);
        switchEcalling.setOnCheckedChangeListener(this);

        sosPressed = Utils.getPreferences("Sos_pressed_" + trackerID, context);

        seekbarSeconds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekSeconds.setText(progress + " s");
                Utils.savePreferences("seek_seconds_" + trackerID, progress + "", context);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        txtMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMoCheck == true) {
                    txtMon.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    isMoCheck = false;
                } else {
                    txtMon.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    isMoCheck = true;
                }

            }
        });
        txtTu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is2Check == true) {
                    txtTu.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is2Check = false;
                } else {
                    txtTu.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is2Check = true;
                }

            }
        });
        txtWen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is3Check == true) {
                    txtWen.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is3Check = false;
                } else {
                    txtWen.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is3Check = true;
                }

            }
        });
        txtTh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is4Check == true) {
                    txtTh.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is4Check = false;
                } else {
                    txtTh.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is4Check = true;
                }

            }
        });
        txtFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is5Check == true) {
                    txtFr.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is5Check = false;
                } else {
                    txtFr.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is5Check = true;
                }

            }
        });
        txtSa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is6Check == true) {
                    txtSa.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is6Check = false;
                } else {
                    txtSa.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is6Check = true;
                }

            }
        });
        txtSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is7Check == true) {
                    txtSu.setBackgroundResource(R.drawable.borderlayout_dayes_nonselect);
                    is7Check = false;
                } else {
                    txtSu.setBackgroundResource(R.drawable.borderlayout_week_dayes);
                    is7Check = true;
                }

            }
        });

        idtvListActivHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AssignActiveHours.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        day = intent.getStringExtra("day");
        startTime = intent.getStringExtra("start_time");
        endTime = intent.getStringExtra("end_time");
        saved_tracker = intent.getStringExtra("saved_tracker");
        int start_hour = 0;
        int start_min = 0;
        int end_hour = 0;
        int end_min = 0;


        if (day != null) {
            try {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) generalLayout.getLayoutParams();
                params.height = 0;
                generalLayout.setLayoutParams(params);

                selectedDay.setText(day);
                idbtnGReportMain.setText("Update");
                idtvDateRange1.setText(startTime);
                idtvDateRange2.setText(endTime);
                String[] str_start_time = startTime.split(":");
                String[] str_end_time = endTime.split(":");
                start_hour = Integer.parseInt(str_start_time[0]);
                start_min = Integer.parseInt(str_start_time[1]);
                end_hour = Integer.parseInt(str_end_time[0]);
                end_min = Integer.parseInt(str_end_time[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) selectedLayout.getLayoutParams();
            params.height = 0;
            selectedLayout.setLayoutParams(params);
        }

        final int finalStart_hour = start_hour;
        final int finalStart_min = start_min;
        idtvDateRange1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarTime = false;
                TimePickerDialog dpd = null;
                if (day != null) {
                    dpd = TimePickerDialog.newInstance(ActiveHour.this, finalStart_hour, finalStart_min, 0, false);
                } else {
                    Calendar now = Calendar.getInstance();
                    dpd = TimePickerDialog.newInstance(
                            ActiveHour.this,
                            now.get(Calendar.HOUR),
                            now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND),
                            false
                    );
                }

                dpd.setAccentColor(Color.parseColor("#9C27B0"));
                dpd.setTitle("Please select time");
                dpd.show(getFragmentManager(), "TimepickerDialog");
            }
        });


        // Show a datepicker when the dateButton is clicked
        final int finalEnd_hour = end_hour;
        final int finalEnd_min = end_min;
        idtvDateRange2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarTime = true;
                TimePickerDialog dpd = null;
                if (day != null) {
                    dpd = TimePickerDialog.newInstance(ActiveHour.this, finalEnd_hour, finalEnd_min, 0, false);
                } else {
                    Calendar now = Calendar.getInstance();
                    dpd = TimePickerDialog.newInstance(
                            ActiveHour.this,
                            now.get(Calendar.HOUR),
                            now.get(Calendar.MINUTE),
                            now.get(Calendar.SECOND),
                            false
                    );
                }

                dpd.setAccentColor(Color.parseColor("#9C27B0"));
                dpd.setTitle("Please select time");
                dpd.show(getFragmentManager(), "TimepickerDialog");

            }
        });

        idbtnGReportMain.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (day != null) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.updateExistingTime(day, startTime, endTime, saved_tracker);
                    String token = Utils.getPreferences("portalToken", context);
                    Log.e("token", " " + token);
                    if (token.equalsIgnoreCase("")) {
                        loginToPortalServer();
                    } else {
                        sendActiveHoursDataToServer(token, generateActiveHoursParams());
                    }
                    Toast.makeText(context, "Update Success !", Toast.LENGTH_LONG).show();
                } else {
                    saveData();
                }
            }
        });
        initUI();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    public void muteAudio() {
        if (!mBound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, AntiHijackingCountDownTimer.MUTE_AUDIO, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(smartDefenceDataSyncReceiver, new IntentFilter("smartDefenceDataSyncReceiver"));
        registerReceiver(broadcastReceiver, new IntentFilter("AntiHijacking"));
        if (sosPressed.equals("true")) {
            bindService(new Intent(this, AntiHijackingCountDownTimer.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        try {
            if (smartDefenceDataSyncReceiver != null) {
                unregisterReceiver(smartDefenceDataSyncReceiver);
            }
            if (smartDefenceDataSyncReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void initUI() {
        if (sosPressed.equals("true")) {
            showDialogSoundOff();
        }
//        String sosPressed = Utils.getPreferences("Sos_pressed_" + trackerID, context);
        String auto_protect = Utils.getPreferences("auto-protect_" + trackerID, context);
        String park_guard = Utils.getPreferences("park-guard_" + trackerID, context);
        String park_shut = Utils.getPreferences("park-shut_" + trackerID, context);
        String park_audio = Utils.getPreferences("park-audio_" + trackerID, context);
        String park_delay = Utils.getPreferences("park-delay_" + trackerID, context);
        String park_imm = Utils.getPreferences("park-imm_" + trackerID, context);

        String antiHijacking = Utils.getPreferences("anti-hijacking_" + trackerID, context);
        String ecalling = Utils.getPreferences("ecalling_" + trackerID, context);
        String antiHijackingCountDownTime = Utils.getPreferences("anti-hijacking_countdown_time_" + trackerID, context);

        String txtSavedSeconds = Utils.getPreferences("seek_seconds_" + trackerID, context);
        String txtSavedBattery = Utils.getPreferences("battery_alarm_" + trackerID, context);
        String strIDAlarm_ignition = Utils.getPreferences("playAlarm_ignition_" + trackerID, context);
        String strIDAlarm_parking = Utils.getPreferences("playAlarm_parking_" + trackerID, context);

        if (txtSavedSeconds.length() > 0) {
            seekbarSeconds.setProgress(Integer.parseInt(txtSavedSeconds));
            seekSeconds.setText(txtSavedSeconds + " s");
        }

        if (ecalling.equals("ON")) {
            switchEcalling.setChecked(true);
        } else {
            switchEcalling.setChecked(false);
        }

        if (antiHijacking.equals("ON")) {
            switchAntihiJacking.setChecked(true);
        } else {
            switchAntihiJacking.setChecked(false);
        }

        if (antiHijackingCountDownTime.equals("")) {
            counterTextView.setText("1");
        } else {
            counterTextView.setText(antiHijackingCountDownTime);
        }

        if (strIDAlarm_ignition.equals("ON")) {
            idSAlarm_ignition.setChecked(true);
        } else {
            idSAlarm_ignition.setChecked(false);
        }

        if (park_guard.equals("ON")) {
            sw_guard.setChecked(true);
        } else {
            sw_guard.setChecked(false);
        }

        if (strIDAlarm_parking.equals("ON")) {
            idSAlarm_parking.setChecked(true);
        } else {
            idSAlarm_parking.setChecked(false);
        }

        if (txtSavedBattery.equals("ON"))
            switchBattery.setChecked(true);
        else
            switchBattery.setChecked(false);

        if (auto_protect.equals("ON")) {
            sw_defence.setChecked(true);
        } else {
            sw_defence.setChecked(false);
        }

        if (!park_shut.equals("ON")) {
            sw_park_shut.setChecked(false);
        }
        if (!park_audio.equals("ON")) {
            sw_park_audio.setChecked(false);
        }
        if (!park_delay.equals("ON")) {
            sw_park_delay.setChecked(false);
        }
        if (!park_imm.equals("ON")) {
            sw_park_imm.setChecked(false);
        }
    }

    private void saveData() {
        String trackerID = Utils.getPreferences("TrackerID", context);
        String day = "";
        if ((!isMoCheck && !is2Check && !is3Check && !is4Check && !is5Check && !is6Check && !is7Check) || idtvDateRange1.getText().toString().equals("Start Time") || idtvDateRange2.getText().toString().equals("End Time")) {
            Toast.makeText(context, "Please select a day/time", Toast.LENGTH_LONG).show();
        } else {
            DatabaseHandler db = new DatabaseHandler(context);
            Hours hours = new Hours();
            hours.setStart_time(startTime);
            hours.setEnd_time(endTime);
            hours.setTrackerID(trackerID);
            if (isMoCheck) {
                day = "Monday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            if (is2Check) {
                day = "Tuesday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            if (is3Check) {
                day = "Wednesday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            if (is4Check) {
                day = "Thursday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            if (is5Check) {
                day = "Friday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            if (is6Check) {
                day = "Saturday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            if (is7Check) {
                day = "Sunday";
                String ifExist = db.getCountDays(trackerID, day);
                if (Integer.parseInt(ifExist) == 0) {
                    hours.setDay(day);
                    db.addActiveHours(hours);
                } else {
                    db.updateExistingTime(day, startTime, endTime, trackerID);
                }
            }
            String token = Utils.getPreferences("portalToken", context);
            Log.e("token", " " + token);
            if (token.equalsIgnoreCase("")) {
                loginToPortalServer();
            } else {
                sendActiveHoursDataToServer(token, generateActiveHoursParams());
            }
            Toast.makeText(context, "Save Success !", Toast.LENGTH_LONG).show();
        }
    }

    private Map generateActiveHoursParams() {
        DatabaseHandler db = new DatabaseHandler(context);
        Map params = new HashMap<>();
        ArrayList<Hours> listtrackingHistory;
        ArrayList<ActiveHourWS> listActiveHourWS = new ArrayList<>();
        listtrackingHistory = (ArrayList<Hours>) db.getActiveHoursList();
        for (int i = 0; i < listtrackingHistory.size(); i++) {
            ActiveHourWS activeHourWS = new ActiveHourWS();
            activeHourWS.setDay(getDayAsInteger(listtrackingHistory.get(i).getDay()));
            activeHourWS.setStartTime(convertTime(listtrackingHistory.get(i).getStart_time()));
            Log.e("setStartTime", " " + activeHourWS.getStartTime());
            Log.e("getStartTime", " " + listtrackingHistory.get(i).getStart_time());
            activeHourWS.setEndTime(convertTime(listtrackingHistory.get(i).getEnd_time()));
            listActiveHourWS.add(activeHourWS);
        }
        params.put("trackerId", trackerID);
        params.put("day", listActiveHourWS.toString());
        params.put("isAutoProtectEnable", String.valueOf(sw_defence.isChecked()));

        return params;
    }

    private String convertTime(String time) {
        try {
            return requiredTimeFormat.format(timeFormat.parse(time));
        } catch (ParseException e) {
            Log.e("convertTime", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return "";
    }

    private int getDayAsInteger(String dayName) {
        if (dayName.equals("Sunday")) {
            return 1;
        }
        if (dayName.equals("Monday")) {
            return 2;
        }
        if (dayName.equals("Tuesday")) {
            return 3;
        }
        if (dayName.equals("Wednesday")) {
            return 4;
        }
        if (dayName.equals("Thursday")) {
            return 5;
        }
        if (dayName.equals("Friday")) {
            return 6;
        }
        if (dayName.equals("Saturday")) {
            return 7;
        }
        return 1;
    }

    private void loginToPortalServer() {
        String url = String.format("%sapp/login", AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.optBoolean("success")) {
                        String token = responseObject.optString("token");
                        Utils.savePreferences("portalToken", token, context);

                        sendActiveHoursDataToServer(token, generateActiveHoursParams());
                    }
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

    private void sendActiveHoursDataToServer(final String token, final Map params) {
        String url = String.format("%sevent/syncAutoProtectFromApp", AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    if (error.networkResponse.statusCode == 403) {
                        loginToPortalServer();
                    } else {
                        sendActiveHoursDataToServer(token, params);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }

            @Override
            protected Map getParams() throws AuthFailureError {
                return params;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "sendDataToPortalServer");
    }

    private void syncAutoProtectEnabled(final Map<String, String> params) {
        String url = String.format("%sevent/syncAutoProtectFromApp", AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Utils.getPreferences("portalToken", context));
                return headers;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "syncAutoProtectEnabled");
    }

    private void syncAntihijackingEnabled(final Map<String, String> params) {
        String url = String.format("%sevent/syncAntiHijackingFromApp", AppSingleton.BASE_PORTAL_URL);
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Utils.getPreferences("portalToken", context));
                return headers;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "syncAntihijackingEnabled");
    }


    private void syncParkGuardEnabled(final Map<String, String> params) {
        String url = String.format("%sevent/syncParkGuardFromApp", AppSingleton.BASE_PORTAL_URL);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Log.e("eeee", "eeee" + new String(error.networkResponse.data));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Utils.getPreferences("portalToken", context));
                return headers;
            }
        };
        AppSingleton.getInstance(context).addToRequestQueue(request, "syncAutoProtectEnabled");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

        String time = null;
        String sHour;
        String sMinut;
        sHour = "" + hourOfDay;
        sMinut = "" + minute;

        if (hourOfDay > -1 && hourOfDay < 10) {
            sHour = "0" + hourOfDay;
            Log.d("Time", "" + time);
        }
        if (minute > -1 && minute < 10) {
            sMinut = "0" + minute;
        }


        time = "" + sHour + ":" + sMinut;

        if (isStarTime) {
            idtvDateRange2.setText(time);
            endTime = time;
        } else {
            idtvDateRange1.setText(time);
            startTime = time;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_autoprotect:
                if (isChecked) {
                    sw_guard.setChecked(false);
                    Utils.savePreferences("auto-protect_" + trackerID, "ON", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
//                    sw_guard.setChecked(true);
                    Utils.savePreferences("auto-protect_" + trackerID, "OFF", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_parkguard:
                if (isChecked) {
                    sw_defence.setChecked(false);
                    Utils.savePreferences("park-guard_" + trackerID, "ON", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
//                    sw_defence.setChecked(true);
                    Utils.savePreferences("park-guard_" + trackerID, "OFF", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_park_shut:
                if (isChecked) {
                    Utils.savePreferences("park-shut_" + trackerID, "ON", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.savePreferences("park-shut_" + trackerID, "OFF", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_park_audio:
                if (isChecked) {
                    Utils.savePreferences("park-audio_" + trackerID, "ON", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.savePreferences("park-audio_" + trackerID, "OFF", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_park_delay:
                if (isChecked) {
                    Utils.savePreferences("park-delay_" + trackerID, "ON", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.savePreferences("park-delay_" + trackerID, "OFF", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.switch_park_imm:
                if (isChecked) {
                    Utils.savePreferences("park-imm_" + trackerID, "ON", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Utils.savePreferences("park-imm_" + trackerID, "OFF", context);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.idSAlarm_ignition:
                if (isChecked == true) {
                    Utils.savePreferences("playAlarm_ignition_" + trackerID, "ON", context);
                } else {
                    Utils.savePreferences("playAlarm_ignition_" + trackerID, "OFF", context);
                }
                Toast.makeText(context, "Alarm for Ignition on Detected :  " + Utils.getPreferences("playAlarm_ignition_" + trackerID, context),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.idSAlarm_parking:
                if (isChecked == true) {
                    Utils.savePreferences("playAlarm_parking_" + trackerID, "ON", context);
                } else {
                    Utils.savePreferences("playAlarm_parking_" + trackerID, "OFF", context);
                }
                Toast.makeText(context, "Alarm for Parking end Detected :  " + Utils.getPreferences("playAlarm_parking_" + trackerID, context),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.switchBattery:
                if (isChecked == true) {
                    Utils.savePreferences("battery_alarm_" + trackerID, "ON", context);
                } else {
                    Utils.savePreferences("battery_alarm_" + trackerID, "OFF", context);
                }
                Toast.makeText(context, "External power cut alarm: " + Utils.getPreferences("battery_alarm_" + trackerID, context),
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.switch_antihijacking:
                if (isChecked) {
                    Utils.savePreferences("anti-hijacking_" + trackerID, "ON", context);
                    Utils.savePreferences("anti-hijacking_countdown_time_" + trackerID, counterTextView.getText().toString(), context);

                } else {
                    Utils.savePreferences("anti-hijacking_" + trackerID, "OFF", context);
                    Utils.savePreferences("anti-hijacking_countdown_time_" + trackerID, counterTextView.getText().toString(), context);
                }
                break;
            case R.id.ecallingswitch:
                if (isChecked) {
                    Utils.savePreferences("ecalling_" + trackerID, "ON", context);
                } else {
                    Utils.savePreferences("ecalling_" + trackerID, "OFF", context);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Map<String, String> params = new HashMap<>();
        int countDownTime = Integer.parseInt(counterTextView.getText().toString());
        boolean checked = false;
        switch (view.getId()) {
            case R.id.switch_park_audio:
                checked = ((Switch) view).isChecked();
                params.clear();
                params.put("trackerId", trackerID);
                String parkGuard = Utils.getPreferences("park-guard_" + trackerID, context);
                params.put("enableAudio", String.valueOf(checked));
                if (parkGuard.equals("ON")) {
                    params.put("isParkGuardEnable", "true");
                } else {
                    params.put("isParkGuardEnable", "false");
                }
                syncParkGuardEnabled(params);
                break;
            case R.id.switch_autoprotect:
                checked = ((Switch) view).isChecked();
                params.clear();
                params.put("trackerId", trackerID);
                params.put("isAutoProtectEnable", String.valueOf(checked));
                syncAutoProtectEnabled(params);
                break;
            case R.id.switch_parkguard:
                checked = ((Switch) view).isChecked();
                params.clear();
                params.put("trackerId", trackerID);
                String enableAudio = Utils.getPreferences("park-audio_" + trackerID, context);
                params.put("isParkGuardEnable", String.valueOf(checked));
                if (enableAudio.equals("ON")) {
                    params.put("enableAudio", "true");
                } else {
                    params.put("enableAudio", "false");
                }
                syncParkGuardEnabled(params);
                break;
            case R.id.switch_antihijacking:
                checked = ((Switch) view).isChecked();
                params.clear();
                params.put("trackerId", trackerID);
                params.put("isAntiHiJackingEnable", String.valueOf(checked));
                Log.e("e", "isAntiHiJackingEnable " + params.get("isAntiHiJackingEnable"));
                params.put("antiHiJackingCountDownTime", counterTextView.getText().toString());
                syncAntihijackingEnabled(params);
                break;
            case R.id.savecountdowntimer:
                params.clear();
                String antihijakingEnable = Utils.getPreferences("anti-hijacking_" + trackerID, context);
                if (antihijakingEnable.equals("ON")) {
                    params.put("isAntiHiJackingEnable", "true");
                } else {
                    params.put("isAntiHiJackingEnable", "false");
                }
                params.put("trackerId", trackerID);
                params.put("antiHiJackingCountDownTime", counterTextView.getText().toString());
                Utils.savePreferences("anti-hijacking_countdown_time_" + trackerID, counterTextView.getText().toString(), context);
                syncAntihijackingEnabled(params);
                break;
            case R.id.incrementCounter:
                if (countDownTime == 30) {
                    Toast.makeText(context, "Timer cannot be greater than 30 minutes", Toast.LENGTH_SHORT).show();
                } else {
                    countDownTime++;
                    counterTextView.setText(String.valueOf(countDownTime));
                }
                break;
            case R.id.decrementCounter:
                if (countDownTime == 1) {
                    Toast.makeText(context, "Timer cannot be lesser than 1 minute", Toast.LENGTH_SHORT).show();
                } else {
                    countDownTime--;
                    counterTextView.setText(String.valueOf(countDownTime));
                }
                break;
        }
    }

    private void EngineGetStop() {
        jArrOutPuts.clear();
        String description = "";
        String connectionStatusURL = "https://api.navixy.com/v2/tracker/get_state?tracker_id=" + trackerID + "&hash=" + hashCode;
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
            String connectionStatusURL = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/output/set?hash=" + hashCode + "&output=" + (i + 1) + "&enable=true&tracker_id=" + trackerID;
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
                                        String trackerName = db.getTLabelbyID("" + trackerID);
//                                        Toast.makeText(context, trackerName + " has been enabled !", Toast.LENGTH_LONG).show();
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage(trackerName + " has been disabled !");
                                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                        db.updateTrackerConnectionOnly(trackerID, "signal_lost");
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

    public void showDialogSoundOff() {
        DatabaseHandler db = new DatabaseHandler(context);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.anti_hijacking_dialog, null);
        final TextView txtTime = (TextView) layout.findViewById(R.id.counter_dialog_txt_time);
        final TextView txtMessage = (TextView) layout.findViewById(R.id.counter_dialog_txt_message);
        final TextView txtShutdown = (TextView) layout.findViewById(R.id.counter_dialog_txt_shutdown);
        final TextView txtIgnore = (TextView) layout.findViewById(R.id.counter_dialog_txt_ignore);
        CheckBox muteCheckBox = (CheckBox) layout.findViewById(R.id.muteCheckBox);
        alertDialog.setView(layout);
        txtMessage.setText("SOS button pressed on " + db.getTLabelbyID(trackerID));
        db.close();
        alertView = alertDialog.create();
        alertView.setCancelable(false);
        alertView.setCanceledOnTouchOutside(false);
        alertView.show();
        alertView.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        final AlertDialog finalAlertView = alertView;

        txtShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EngineGetStop();
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                stopService(new Intent(ActiveHour.this, AntiHijackingCountDownTimer.class));
                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                }
                Utils.savePreferences("Sos_pressed_" + trackerID, "false", context);

                if (alertView.isShowing()) {
                    alertView.dismiss();
                }
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
                stopService(new Intent(ActiveHour.this, AntiHijackingCountDownTimer.class));
                Utils.savePreferences("Sos_pressed_" + trackerID, "false", context);
                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                }
                if (alertView.isShowing()) {
                    alertView.dismiss();
                }

            }
        });

        muteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                muteAudio();
            }
        });
    }
}
