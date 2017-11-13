package add_assets;
//
//import android.app.Activity;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.lineztech.farhan.vehicaltarckingapp.R;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//public class AddNewAssetsTest extends Activity implements View.OnClickListener {
//
//    TextView tvIsConnected;
//    EditText etName,etCountry,etTwitter;
//    Button btnPost;
//
//    Person person;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // get reference to the views
//        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
//        etName = (EditText) findViewById(R.id.etName);
//        etCountry = (EditText) findViewById(R.id.etCountry);
//        etTwitter = (EditText) findViewById(R.id.etTwitter);
//        btnPost = (Button) findViewById(R.id.btnPost);
//
//        // check if you are connected or not
//        if(isConnected()){
//            tvIsConnected.setBackgroundColor(0xFF00CC00);
//            tvIsConnected.setText("You are conncted");
//        }
//        else{
//            tvIsConnected.setText("You are NOT conncted");
//        }
//
//        // add click listener to Button "POST"
//        btnPost.setOnClickListener(this);
//
//    }
//
//    public static String POST(String url, Person person){
//        InputStream inputStream = null;
//        String result = "";
//        try {
//
//            // 1. create HttpClient
//            HttpClient httpclient = new DefaultHttpClient();
//
//            // 2. make POST request to the given URL
//            HttpPost httpPost = new HttpPost(url);
//
//            String json = "";
//
//            // 3. build jsonObject
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("name", person.getName());
//            jsonObject.accumulate("country", person.getCountry());
//            jsonObject.accumulate("twitter", person.getTwitter());
//
//            // 4. convert JSONObject to JSON to String
//            json = jsonObject.toString();
//
//            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
//            // ObjectMapper mapper = new ObjectMapper();
//            // json = mapper.writeValueAsString(person);
//
//            // 5. set json to StringEntity
//            StringEntity se = new StringEntity(json);
//
//            // 6. set httpPost Entity
//            httpPost.setEntity(se);
//
//            // 7. Set some headers to inform server about the type of the content
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//
//            // 8. Execute POST request to the given URL
//            HttpResponse httpResponse = httpclient.execute(httpPost);
//
//            // 9. receive response as inputStream
//            inputStream = httpResponse.getEntity().getContent();
//
//            // 10. convert inputstream to string
//            if(inputStream != null)
//                result = convertInputStreamToString(inputStream);
//            else
//                result = "Did not work!";
//
//        } catch (Exception e) {
//            Log.d("InputStream", e.getLocalizedMessage());
//        }
//
//        // 11. return result
//        return result;
//    }
//
//    public boolean isConnected(){
//        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected())
//            return true;
//        else
//            return false;
//    }
//    @Override
//    public void onClick(View view) {
//
//        switch(view.getId()){
//            case R.id.btnPost:
//                if(!validate())
//                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
//                // call AsynTask to perform network operation on separate thread
//                new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
//                break;
//        }
//
//    }
//    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//
//            person = new Person();
//            person.setName(etName.getText().toString());
//            person.setCountry(etCountry.getText().toString());
//            person.setTwitter(etTwitter.getText().toString());
//
//            return POST(urls[0],person);
//        }
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private boolean validate(){
//        if(etName.getText().toString().trim().equals(""))
//            return false;
//        else if(etCountry.getText().toString().trim().equals(""))
//            return false;
//        else if(etTwitter.getText().toString().trim().equals(""))
//            return false;
//        else
//            return true;
//    }
//    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
//        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
//        String line = "";
//        String result = "";
//        while((line = bufferedReader.readLine()) != null)
//            result += line;
//
//        inputStream.close();
//        return result;
//
//    }
//}



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lineztech.farhan.vehicaltarckingapp.R;

import io.fabric.sdk.android.Fabric;

public class AddNewAssetsTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.add_new_asset);

        new Loader().execute();

    }

    class Loader extends AsyncTask<Void, Void, JSONObject>{
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                dialog = new ProgressDialog(AddNewAssetsTest.this, ProgressDialog.THEME_HOLO_LIGHT);
            }else{
                dialog = new ProgressDialog(AddNewAssetsTest.this);
            }
            dialog.setMessage(Html.fromHtml("<b>"+"Loading..."+"</b>"));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            return postJsonObject("http://smarttrack.iconnectcloudsolutions.com/api-v2/vehicle/create?hash=6cdccdd25775d8ff6f1a3f252b1c7c44&vehicle", makingJson());
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result!=null) {
                dialog.dismiss();
            }else {
                Toast.makeText(AddNewAssetsTest.this, "Successfully post json object", Toast.LENGTH_LONG).show();
            }
        }

    }

    public JSONObject makingJson() {

        JSONObject params = new JSONObject();

        try {
//            params.put("email", "karn.neelmani@gmail.com");
//            params.put("name", "Neelmani Karn");

            params.put("id",0);
            params.put("tracker_id",100040);
            params.put("label","111");
            params.put("max_speed",111);
            params.put("model","");
            params.put("type","special");
            params.put("subtype", "mobile_crane");
            params.put("garage_id",null);
            params.put("reg_number","");
            params.put("vin","");
            params.put("chassis_number","");
            params.put("payload_weight",32000);
            params.put("payload_height",1.0);
            params.put("payload_length",1.2);
            params.put("payload_width",1.0);
            params.put("passengers",4);
            params.put("fuel_type","diesel");
            params.put("fuel_grade","");
            params.put("norm_avg_fuel_consumption",9.0);
            params.put("fuel_tank_volume",50);
            params.put("wheel_arrangement","4*6");
            params.put("tyre_size","111");
            params.put("tyres_number",4);
            params.put("liability_insurance_policy_number","111");
            params.put("liability_insurance_valid_till","2015-03-01");
            params.put("free_insurance_policy_number","111");
            params.put("free_insurance_valid_till",null);
            params.put("icon_id",55);
            params.put("avatar_file_name",null);
            int[] intArray = new int[] {4,5};
            params.put("tags",intArray);
            params.put("photo","");
            params.put("empty_group","");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }


    public JSONObject postJsonObject(String url, JSONObject loginJobj){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL

            //http://localhost:9000/api/products/GetAllProducts
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 4. convert JSONObject to JSON to String

            json = loginJobj.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        JSONObject json = null;
        try {

            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 11. return result

        return json;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
