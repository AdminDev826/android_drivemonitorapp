package com.lineztech.farhan.vehicaltarckingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import add_assets.AddNewAsset;
import assing_task.RSSPullService;
import assing_task.SyncSmartDefenceData;
import dashboard.CustomViewIconTextTabsFragment;
import dashboard.Trackers;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import liveo.Model.HelpLiveo;
import liveo.interfaces.OnItemClickListener;
import liveo.interfaces.OnPrepareOptionsMenuLiveo;
import liveo.navigationliveo.NavigationLiveo;
import sos.GMailSender;
import sos.Sos;
import sos.SosInput;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/25/2016.
 */
public class UserInfo extends NavigationLiveo implements OnItemClickListener {

    Context context;
    BroadcastReceiver broadcastReceiver;
    CustomViewIconTextTabsFragment mFragmentt;
    public static ProgressBar progressBar;
    ProgressDialog progressDialog;
    private HelpLiveo mHelpLiveo;
    List<Trackers> trackerList;
    TextView idbtnOK;
    Switch sNotification, idSVibration, idtvStartEngine;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    String userID, balance, userNameStr, sosListUrl, status, currentPosition, connectionStatusURL, alarm_id, tracker_id, hashCode, vName;
    MediaPlayer mPlayer;
    List<Sos> sosList = new ArrayList<>();
    public Dialog dialog;
    GMailSender sender;
    private List<Boolean> jArrOutPutsEngineON = new ArrayList<Boolean>();
    private List<String> jPuttsOnPosition = new ArrayList<String>();
    private List<String> jPuttss = new ArrayList<String>();
    private List<String> jPuttssPositionOFF = new ArrayList<String>();
    private List<Boolean> jArrOutPuts = new ArrayList<Boolean>();
    private List<String> jPuttsOn = new ArrayList<String>();
    int cPosition;
    Uri lastPictureUri;
    public static int SELECT_PICTURE_REQUEST_CODE = 69;

    @Override
    public void onInt(Bundle savedInstanceState) {
        context = this;
        ViewGroup layout = (ViewGroup) findViewById(android.R.id.content).getRootView();
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setGravity(Gravity.CENTER);
        rl.addView(progressBar);
        layout.addView(rl, params);
        hashCode = Utils.getPreferences("hashCode", context);
        tracker_id = Utils.getPreferences("TrackerID", context);
        Fabric.with(this, new Crashlytics());
        String strPhotoPath = Utils.getPreferences("userPhoto", context);
        if (strPhotoPath.equals("")) {
            this.userPhoto.setImageResource(R.drawable.ic_rudsonlive);
        } else {
            Uri targetLink = Uri.parse(strPhotoPath);
            BitmapFactory.Options op = new BitmapFactory.Options();
            Bitmap targetBitmap = DecodeUtils.decodeBitmap(this.context, targetLink, op, 320, 320, 1, 2);
            this.userPhoto.setImageBitmap(targetBitmap);
        }
        sender = new GMailSender("sos@iconnectcloudsolutions.com", "Cisco_12");
        userID = Utils.getPreferences("c_user_id", context);
        balance = Utils.getPreferences("c_balance", context);
        userNameStr = Utils.getPreferences("c_full_name", context);
        Intent intent = getIntent();
        currentPosition = intent.getStringExtra("position");
        trackerList = new ArrayList<>();

        Intent intent1 = getIntent();
        String OutOf = intent1.getStringExtra("OutOf");
        String notify = intent1.getStringExtra("notify");

        this.userName.setText("" + userID);
        this.userBalance.setText("Balance: " + balance);
        this.userName.setPadding(10, 0, 0, 0);
        this.userBalance.setPadding(10, 0, 0, 0);
        this.userEmail.setText("" + userNameStr);

        this.addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNewAsset.class);
                startActivity(intent);
            }
        });

        this.userEmail.setPadding(10, 0, 0, 0);
        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        DatabaseHandler db = new DatabaseHandler(context);
        trackerList = db.getTrackerList();
        for (int i = 0; i < trackerList.size(); i++) {
            int status = 0;
            String tID = trackerList.get(i).getTrackerID();
            if (tID == null || tID.isEmpty()) {
                tID = "90824";
            }
            String connectionStatus = db.getStatus(tID);
            db.close();
            if (connectionStatus == null) {
                connectionStatus = "active";
            }
            if (connectionStatus.equals("active")) {
                status = 1;
            } else {
                status = 0;
            }
            mHelpLiveo.add(trackerList.get(i).getTrackerLabel(), R.drawable.ic_directions_car_black_24dp, status);
        }

        if (currentPosition == null) {
            cPosition = 0;
        } else {
            cPosition = Integer.parseInt(currentPosition);
        }
        if (notify != null) {
            if (notify.equals("notify")) {
                mFragmentt = new CustomViewIconTextTabsFragment(true);
            } else {
                mFragmentt = new CustomViewIconTextTabsFragment(false);
            }
        } else {
            mFragmentt = new CustomViewIconTextTabsFragment(false);
        }

        FragmentManager mFragmentManagerr = getSupportFragmentManager();
        Utils.savePreferences("TrackerID", trackerList.get(cPosition).getTrackerID(), context);
        if (mFragmentt != null) {
            mFragmentManagerr.beginTransaction().replace(R.id.containerr, mFragmentt).commit();
        }
        with(this) // default theme is dark
                .startingPosition(cPosition) //Starting position in the list
                .addAllHelpItem(mHelpLiveo.getHelp())
                .footerItem("Settings", R.drawable.ic_settings_applications_24dp)
                .setOnClickUser(onClickPhoto)
                .setOnPrepareOptionsMenu(onPrepare)
                .setOnClickFooter(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.settings);
                        sNotification = (Switch) dialog.findViewById(R.id.sNotification);
                        idSVibration = (Switch) dialog.findViewById(R.id.idSVibration);
                        Switch switchBattery = (Switch) dialog.findViewById(R.id.switchBattery);
                        SeekBar seekbarSeconds = (SeekBar) dialog.findViewById(R.id.seekBarSeconds);
                        final TextView seekSeconds = (TextView) dialog.findViewById(R.id.txtSeconds);
                        Switch idSAlarm_ignition = (Switch) dialog.findViewById(R.id.idSAlarm_ignition);
                        Switch idSAlarm_parking = (Switch) dialog.findViewById(R.id.idSAlarm_parking);
                        idbtnOK = (TextView) dialog.findViewById(R.id.idbtnOK);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);

                        String txtSavedSeconds = Utils.getPreferences("seek_seconds_" + tracker_id, context);
                        if (txtSavedSeconds.length() > 0) {
                            seekbarSeconds.setProgress(Integer.parseInt(txtSavedSeconds));
                            seekSeconds.setText(txtSavedSeconds + " s");
                        }
                        String txtSavedBattery = Utils.getPreferences("battery_alarm_" + tracker_id, context);

                        String stThrowOrder = Utils.getPreferences("sNotification", context);
                        if (stThrowOrder.equals("ON")) {
                            sNotification.setChecked(true);
                        } else {
                            sNotification.setChecked(false);
                        }
                        if (txtSavedBattery.equals("ON"))
                            switchBattery.setChecked(true);
                        else
                            switchBattery.setChecked(false);

                        switchBattery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked == true) {
                                    Utils.savePreferences("battery_alarm_" + tracker_id, "ON", context);
                                } else {
                                    Utils.savePreferences("battery_alarm_" + tracker_id, "OFF", context);
                                }
                                Toast.makeText(context, "External power cut alarm: " + Utils.getPreferences("battery_alarm_" + tracker_id, context),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        seekbarSeconds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                seekSeconds.setText(progress + " s");
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        sNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked == true) {
                                    Utils.savePreferences("sNotification", "ON", context);
                                } else {
                                    Utils.savePreferences("sNotification", "OFF", context);
                                }
                                Toast.makeText(context, "Notifications " + Utils.getPreferences("sNotification", context),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        String stidSVibration = Utils.getPreferences("idSVibration", context);
                        if (stidSVibration.equals("ON")) {
                            idSVibration.setChecked(true);
                        } else {
                            idSVibration.setChecked(false);
                        }
                        idSVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked == true) {
                                    Utils.savePreferences("idSVibration", "ON", context);
                                } else {
                                    Utils.savePreferences("idSVibration", "OFF", context);
                                }
                                Toast.makeText(context, "Vibration " + Utils.getPreferences("idSVibration", context),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        String strIDAlarm_ignition = Utils.getPreferences("playAlarm_ignition_" + tracker_id, context);
                        String strIDAlarm_parking = Utils.getPreferences("playAlarm_parking_" + tracker_id, context);
                        if (strIDAlarm_ignition.equals("ON")) {
                            idSAlarm_ignition.setChecked(true);
                        } else {
                            idSAlarm_ignition.setChecked(false);
                        }
                        idSAlarm_ignition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked == true) {
                                    Utils.savePreferences("playAlarm_ignition_" + tracker_id, "ON", context);
                                } else {
                                    Utils.savePreferences("playAlarm_ignition_" + tracker_id, "OFF", context);
                                }
                                Toast.makeText(context, "Alarm for Ignition on Detected :  " + Utils.getPreferences("playAlarm_ignition_" + tracker_id, context),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (strIDAlarm_parking.equals("ON")) {
                            idSAlarm_parking.setChecked(true);
                        } else {
                            idSAlarm_parking.setChecked(false);
                        }
                        idSAlarm_parking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked == true) {
                                    Utils.savePreferences("playAlarm_parking_" + tracker_id, "ON", context);
                                } else {
                                    Utils.savePreferences("playAlarm_parking_" + tracker_id, "OFF", context);
                                }
                                Toast.makeText(context, "Alarm for Parking end Detected :  " + Utils.getPreferences("playAlarm_parking_" + tracker_id, context),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        idbtnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String tmp = seekSeconds.getText().toString();
                                String tmpAry[] = tmp.split(" ");
                                Utils.savePreferences("seek_seconds_" + tracker_id, tmpAry[0], context);

                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                })
                .footerSecondItem("Add Assets", R.drawable.ic_add_circle_outline_24dp)
                .setOnClickFooterSecond(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://smarttrack.iconnectcloudsolutions.com/pro/"));
                            startActivity(myIntent);

                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "No application can handle this request."
                                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                })
                .footerLogoutItem("Logout", R.drawable.ic_exit_to_app_24dp)
                .setOnClickFooterLogout(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
//                        showDialogSoundOff();
                    }
                })
                .build();

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (OutOf != null) {
            if (OutOf.equals("OutOf")) {
                alarm_id = intent1.getStringExtra("alarm_id");
                vName = intent1.getStringExtra("vName");

                AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                mPlayer = MediaPlayer.create(context, R.raw.danger_alarm);
                mPlayer.setLooping(true); // Set looping
                mPlayer.start();
                showDialogSoundOff();
            }
        }
        Intent service_intent = new Intent(context, RSSPullService.class);
        startService(service_intent);
        startService(new Intent(context, SyncSmartDefenceData.class));
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        databaseHandler.removeAllTime();
    }

    public Uri openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = UUID.randomUUID().toString() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                Uri selectedImageUri = processPhotoResponseWith(data, lastPictureUri, UUID.randomUUID().toString());

                BitmapFactory.Options op = new BitmapFactory.Options();
                Bitmap targetBitmap = DecodeUtils.decodeBitmap(this.context, selectedImageUri, op, 320, 320, 1, 2);
                userPhoto.setImageBitmap(targetBitmap);
                String newURI = selectedImageUri.toString();
                Utils.savePreferences("userPhoto", newURI, context);
            }
        }
    }

    public Uri processPhotoResponseWith(Intent data, Uri outputFileUri, String defaultName) {
        final boolean isCamera;
        if (data == null) {
            isCamera = true;
        } else {
            final String action = data.getAction();
            if (action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }

        Uri temporaryURI = null;
        Uri selectedImageUri = null;

        if (isCamera) {
            temporaryURI = outputFileUri;
            selectedImageUri = temporaryURI;
        } else {
            temporaryURI = data.getData();
            String imageNameFile = defaultName + ".jpeg";

            String sourceFilename = getRealPathFromURI(temporaryURI);

            String destinationFilename = getFilesDir() + "/" + imageNameFile;

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            try {
                bis = new BufferedInputStream(new FileInputStream(sourceFilename));
                bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
                byte[] buf = new byte[1024];
                bis.read(buf);
                do {
                    bos.write(buf);
                } while (bis.read(buf) != -1);

                File file = new File(getFilesDir() + "/" + imageNameFile);
                selectedImageUri = Uri.fromFile(file);

            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {

                }
            }
        }
        return selectedImageUri;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("GeoFence2", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    alarm_id = intent.getStringExtra("alarm_id");
                    vName = intent.getStringExtra("vName");

                    AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    mPlayer = MediaPlayer.create(context, R.raw.danger_alarm);
                    mPlayer.setLooping(true); // Set looping
                    mPlayer.start();
                    showDialogSoundOff();
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("smart_defense"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission was granted, yay!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "permission denied, boo!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
//            vName = "Ignition Detected on Demo 1";
//            Intent intent1 = new Intent(context, SmartDefenseActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent1.putExtra("vName", vName);
//            intent1.putExtra("alarm_id", "233390");
//            startActivity(intent1);
            sosMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Confirm Log out...");
        alertDialog.setMessage("Are you sure want to log out?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Utils.savePreferences("Logged", null, getApplicationContext());
                Utils.savePreferences("hashCode", null, getApplicationContext());
                AppSingleton.getInstance(context).getRequestQueue().cancelAll("smartdrive");

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                getApplicationContext().deleteDatabase("TrackingApp");
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showDialogSoundOff() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        AlertDialog alertView = null;
        View layout = getLayoutInflater().inflate(R.layout.counter_dialog_layout, null);
        final TextView txtTime = (TextView) layout.findViewById(R.id.counter_dialog_txt_time);
        final TextView txtMessage = (TextView) layout.findViewById(R.id.counter_dialog_txt_message);
        final TextView txtShutdown = (TextView) layout.findViewById(R.id.counter_dialog_txt_shutdown);
        final TextView txtIgnore = (TextView) layout.findViewById(R.id.counter_dialog_txt_ignore);
        alertDialog.setView(layout);
        txtMessage.setText("" + vName);
        alertView = alertDialog.create();
        alertView.setCancelable(false);
        alertView.setCanceledOnTouchOutside(false);
        alertView.show();
        alertView.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final AlertDialog finalAlertView = alertView;
        txtShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EngineGetStop();
                if (mPlayer != null)
                    mPlayer.stop();
                if (finalAlertView.isShowing()) {
                    finalAlertView.dismiss();
                }
            }
        });
        txtIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer != null)
                    mPlayer.stop();
                if (finalAlertView.isShowing()) {
                    finalAlertView.dismiss();
                    closeDefense();
                }
            }
        });
        new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTime.setText("Initiating shutdown sequence in : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (mPlayer != null)
                    mPlayer.stop();
                if (finalAlertView.isShowing()) {
                    EngineGetStop();
                    finalAlertView.dismiss();
                }
            }
        }.start();
    }


    public void onItemClick(int position) {
        if (currentPosition == null) {
            cPosition = 0;
        } else {
            cPosition = Integer.parseInt(currentPosition);
        }
        if (position == cPosition) {
            closeDrawer();
        } else {
            Utils.savePreferences("TrackerID", trackerList.get(position).getTrackerID(), context);
            Intent intent = new Intent(context, UserInfo.class);
            intent.putExtra("userID", userID);
            intent.putExtra("userName", userNameStr);
            intent.putExtra("balance", balance);
            intent.putExtra("position", "" + position);
            startActivity(intent);
            finish();
        }
    }

    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
        }
    };

    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lastPictureUri = openImageIntent();
        }
    };

    public void onClick(View v) {
        closeDrawer();
    }


    public void sosMenu() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sos1);

        LinearLayout idllSos = (LinearLayout) dialog.findViewById(R.id.idllSos);
        LinearLayout idllActiveHour = (LinearLayout) dialog.findViewById(R.id.idllActiveHour);
        TextView idtvCancel = (TextView) dialog.findViewById(R.id.idtvCancel);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        idllSos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
//                sosEmergency();

                Intent intent = new Intent(context, SosInput.class);
                startActivity(intent);
            }
        });
        idllActiveHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ActiveHour.class);
//                startActivity(intent);
                dialog.dismiss();

                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                AsyncSosPressed runner = new AsyncSosPressed();
                runner.execute();
                if (sendSOS()) {
                    dialog.dismiss();
                }

//                showAntiHijackingAlert();
            }
        });
        idtvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void sosEmergency() {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sos_pressed_real);
        TextView idtvCancelSosReal = (TextView) dialog.findViewById(R.id.idtvCancelSosReal);
        TextView idtvMessage = (TextView) dialog.findViewById(R.id.idtvMessage);
        TextView idtvAddsos = (TextView) dialog.findViewById(R.id.idtvAddsos);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        idtvMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                status = "";
//                progressBar.setVisibility(View.VISIBLE);
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                AsyncSosPressed runner = new AsyncSosPressed();
//                runner.execute();
//                if (sendSOS()) {
//                    dialog.dismiss();
//                }

//                showAntiHijackingAlert();
            }
        });


        idtvAddsos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(context, SosInput.class);
                startActivity(intent);

            }
        });

        idtvCancelSosReal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private boolean sendSOS() {
        String message = Utils.getPreferences("sos_message", context);
        String contracts = Utils.getPreferences("sos_sms_numbers", context);
        DatabaseHandler db = new DatabaseHandler(context);
        String lat = db.getLatbyTID(tracker_id);
        String lon = db.getLngbyTID(tracker_id);
        String locationURl = "https://maps.google.com/maps?q=" + lat + "," + lon;
        db.close();
        message += " \n Location: " + locationURl;
        if (contracts.length() < 1) {
            Toast.makeText(context, "Please set SOS contracts", Toast.LENGTH_SHORT).show();
        } else {
            String[] aryContracts = contracts.split(", ");
            for (String contact : aryContracts) {
                sendSMS(contact, message);
            }
            Toast.makeText(context, "SOS messages sent successfully", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private class AsyncSosPressed extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            sosListUrl = "http://smarttrack.iconnectcloudsolutions.com/api-v2/tracker/rule/list?hash=" + hashCode;
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(sosListUrl, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    JSONArray jsonVashiaclLisst = jsonObj.getJSONArray("list");
                    for (int i = 0; i < jsonVashiaclLisst.length(); i++) {
                        Sos sos = new Sos();
                        JSONObject vahicalInfo = jsonVashiaclLisst.getJSONObject(i);
                        String type = vahicalInfo.getString("type");

                        if (type.equals("sos")) {
                            String primary_text = vahicalInfo.getString("primary_text");
                            JSONObject alertsObj = vahicalInfo.getJSONObject("alerts");
                            JSONArray arr_sms_phones = alertsObj.getJSONArray("sms_phones");
                            JSONArray arr_emails = alertsObj.getJSONArray("emails");
                            JSONArray arr_phones = alertsObj.getJSONArray("phones");
                            sos.setArr_Email(arr_emails);
                            sos.setArr_Sms(arr_sms_phones);
                            sos.setArr_Number(arr_phones);
                            sos.setMessage(primary_text);
                            sosList.add(sos);
                        }
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

            try {
                if (status.equals("true")) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    String lat = db.getLatbyTID(tracker_id);
                    String lon = db.getLngbyTID(tracker_id);
                    String locationURl = "https://maps.google.com/maps?q=" + lat + "," + lon;
                    for (int i = 0; i < sosList.size(); i++) {
                        String message = sosList.get(i).getMessage();
                        JSONArray smsNumArray = sosList.get(i).getArr_Sms();
                        JSONArray emailArray = sosList.get(i).getArr_Email();

                        for (int j = 0; j < emailArray.length(); j++) {
                            String emailID = (String) emailArray.get(j);
                            try {
                                new MyAsyncClass(emailID, message + " \n Location: " + locationURl).execute();
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        for (int k = 0; k < smsNumArray.length(); k++) {
                            String smsNum = (String) smsNumArray.get(k);
                            sendSMS(smsNum, message + " \n Location: " + locationURl);
                        }
                    }
                    if (UserInfo.this.isFinishing()) { // or call isFinishing() if min sdk version < 17
                        return;
                    }

                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    dialog.dismiss();

//                    finish();
                } else {
                    Toast.makeText(context, "Not saved successfully", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected void onProgressUpdate(String... text) {
        }

    }

    private void sendSMS(String phoneNumber, String message) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {
        String emailAddress;
        String message;

        public MyAsyncClass(String emailID, String message_) {
            emailAddress = emailID;
            message = message_;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Mail Sent By Smart Tracker App", "" + message, "sos@iconnectcloudsolutions.com", emailAddress);


            } catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "SOS messages sent successfully", Toast.LENGTH_LONG).show();
        }
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
                                        db.close();
                                        closeDefense();
                                    } else {
                                        Toast.makeText(context, "Fail !", Toast.LENGTH_SHORT).show();
                                        closeDefense();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                Toast.makeText(context, "Server Error ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(context, "Server Error ! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                        db.updateTrackerConnectionOnly(alarm_id, "signal_lost");
                                        db.close();
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
            AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smart_defense");
        }
    }

    private void closeDefense() {
        Utils.savePreferences("alarm_playing", "NO", context);
    }
}


