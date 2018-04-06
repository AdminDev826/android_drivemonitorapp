package com.lineztech.farhan.vehicaltarckingapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import assing_task.RSSPullService;
import assing_task.SyncSmartDefenceData;
import db.DatabaseHandler;
import io.fabric.sdk.android.Fabric;
import util.AppSingleton;
import util.Utils;

/**
 * Created by Farhan on 7/25/2016.
 */
public class LoginActivity extends Activity {
    TextView btnLoging;
    String loginURL;
    ProgressBar progressBar;
    EditText etUsername, etPassword;
    Context context;
    String status;
    String hashCode;
    CheckBox checkBoxRemember;
    BroadcastReceiver receiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.login);
//        setContentView(R.layout.login_new);
        context = this;

        etUsername = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);

//        etUsername.setText("clarke.daniel@outlook.com");
//        etPassword.setText("July@1983");

        btnLoging = (TextView) findViewById(R.id.btnLogin);
        checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
        Utils.savePreferences("checked", "unchecked", context);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(RSSPullService.COPA_MESSAGE);
            }
        };

        String isLogged = Utils.getPreferences("Logged", context);

        //check if updated and force user to login again.
        String updated = Utils.getPreferences("updated", context);

        if (isLogged.equals("Logged") && updated.equals("true")) {
            startApp();
        }

        btnLoging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "";
                hashCode = "";
                if (etUsername.getText().length() == 0 && etPassword.getText().length() == 0) {
                    Toast.makeText(context, "Please enter username and password", Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    registerUser();
                }
            }
        });
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
                        startService(new Intent(context, SyncSmartDefenceData.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse != null) {
//                    Log.e("networkResponse", " " + new String(error.networkResponse.data));
//                }
//                loginToPortalServer();
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

    private void registerUser() {
        loginURL = "https://api.navixy.com/v2/user/auth?login=" + etUsername.getText().toString() + "&password=" + etPassword.getText().toString();
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, loginURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        try {
                            status = response.getString("success");
                            if (status.equals("true")) {
                                hashCode = response.getString("hash");
                                if (checkBoxRemember.isChecked()) {
                                    Utils.savePreferences("Logged", "Logged", context);
                                    Utils.savePreferences("updated", "true", context);
                                }
                                Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show();
                                updatePreferences();
                                // login to portal server.
                                loginToPortalServer();
                                startApp();
                            } else {
                                Toast.makeText(context, "Login Fail !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            Log.e("Error", " JSONException");
                            e.printStackTrace();
                            Toast.makeText(context, "Login Fail !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", "  " + error);
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(context, "Login Fail !", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }

    private void updatePreferences() {
        Utils.savePreferences("userName", etUsername.getText().toString(), context);
        Utils.savePreferences("password", etPassword.getText().toString(), context);
        Utils.savePreferences("hashCode", hashCode, context);
    }

    private void startApp() {
        DatabaseHandler db = new DatabaseHandler(context);
        db.refreshTable();
        db.close();
        Intent intent = new Intent(context, StartingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(RSSPullService.COPA_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
}
