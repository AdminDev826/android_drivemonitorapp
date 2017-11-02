package com.lineztech.farhan.vehicaltarckingapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import db.DatabaseHandler;
import util.Utils;

public class EcallingActivity extends AppCompatActivity {

    Context context;
    MediaPlayer mPlayer;
    CountDownTimer callCountdownTimer;
    AlertDialog alertDialog;
    AudioManager audioManager;
    TelephonyManager telephonyManager;
    String trackerId;
    String phoneCallNumber;
    boolean timerFinish = false;
    AsyncTask musicAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        trackerId = getIntent().getStringExtra("tracker_id");
        phoneCallNumber = Utils.getPreferences("sos_phone_call", context);

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mPlayer = MediaPlayer.create(context, R.raw.danger_alarm);
//        mPlayer.start();

        musicAsync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mPlayer.start();
                return null;
            }
        }.execute();

        DatabaseHandler db = new DatabaseHandler(context);
        String message = "Accident Detected for " + db.getTrakerName(trackerId)
                + ", Call your emergency contact?";
        db.close();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Accident Detected");
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!timerFinish) {
                    callCountdownTimer.cancel();
                }
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                sendSOS();

                try {
                    alertDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                callPhone(phoneCallNumber);
                closeEcalling();
            }
        });

        alertBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                if (!timerFinish) {
                    callCountdownTimer.cancel();
                }
                try {
                    alertDialog.dismiss();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                closeEcalling();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                callCountdownTimer = new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
//                        timer.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        if (mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;
                        }
                        timerFinish = true;
                        try {
                            alertDialog.dismiss();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        sendSOS();
                        callPhone(phoneCallNumber);
                        closeEcalling();
                    }
                }.start();
            }
        });
        alertDialog.show();
    }


    private void closeEcalling() {
        finish();
    }

    private void sendSOSSms(String contact, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, 12);
        } else {
            smsManager.sendTextMessage(contact, null, message, null, null);
        }
    }

    private boolean sendSOS() {
        String message = "Accident detected for ";
        String contacts = Utils.getPreferences("sos_sms_numbers", context);
        DatabaseHandler db = new DatabaseHandler(context);
        String lat = db.getLatbyTID(trackerId);
        String lon = db.getLngbyTID(trackerId);
        message += db.getTrakerName(trackerId);
        String locationURl = "https://maps.google.com/maps?q=" + lat + "," + lon;
        db.close();
        message += " \n Location: " + locationURl;
        if (contacts.length() < 1) {
            Toast.makeText(context, "Please set SOS contacts", Toast.LENGTH_SHORT).show();
        } else {
            String[] aryContracts = contacts.split(", ");
            for (String contact : aryContracts) {
                sendSOSSms(contact, message);
            }
            Toast.makeText(context, "SOS messages sent successfully", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12) {
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                sendSOS();
                callPhone(phoneCallNumber);
            } else {
                Toast.makeText(getApplicationContext(), "Sms will not be sent", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 100) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(phoneCallNumber);
            } else {
                Toast.makeText(getApplicationContext(), "Call will not be made", Toast.LENGTH_SHORT).show();
            }
        }
    }

    boolean callFromApp = false;
    boolean callFromOffHook = false;

    private void callPhone(String phoneNumber) {
        callFromApp = true;

        if (phoneNumber != null && !phoneNumber.equals("")) {
            Toast.makeText(context, "Calling your emergency contact", Toast.LENGTH_SHORT).show();
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            telephonyManager.listen(new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    super.onCallStateChanged(state, incomingNumber);
                    switch (state) {

                        case TelephonyManager.CALL_STATE_OFFHOOK: //Call is established
                            if (callFromApp) {
                                callFromApp = false;
                                callFromOffHook = true;

                                try {
                                    Thread.sleep(500); // Delay 0,5 seconds to handle better turning on loudspeaker
                                } catch (InterruptedException e) {
                                }

                                //Activate loudspeaker
                                AudioManager audioManager = (AudioManager)
                                        getSystemService(Context.AUDIO_SERVICE);
                                audioManager.setMode(AudioManager.MODE_IN_CALL);
                                audioManager.setSpeakerphoneOn(true);
                            }
                            break;

                        case TelephonyManager.CALL_STATE_IDLE: //Call is finished
                            if (callFromOffHook) {
                                callFromOffHook = false;
                                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                audioManager.setMode(AudioManager.MODE_NORMAL); //Deactivate loudspeaker
                                telephonyManager.listen(this, // Remove listener
                                        PhoneStateListener.LISTEN_NONE);
                            }
                            break;
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);

//            Log.e("manager", " " + audioManager.isSpeakerphoneOn());

//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        while(true) {
//                            sleep(1000);
//                            audioManager.setMode(AudioManager.MODE_IN_CALL);
//                            if (!audioManager.isSpeakerphoneOn())
//                                audioManager.setSpeakerphoneOn(true);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            thread.start();

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
            } else {
                startActivity(intent);
            }
        } else {
            Toast.makeText(context, "Please set phone number", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
