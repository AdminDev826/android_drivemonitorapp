package com.lineztech.farhan.vehicaltarckingapp;

import android.util.Log;


import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {
    }

    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }
    public String makeServiceCallError(String url, int method) {
        return this.makeServiceCallError(url, method, null);
    }

    public String makeServiceCall(String url, int method, List<NameValuePair> params) {

        StringBuilder builder = new StringBuilder();
        HttpURLConnection urlConnection;

        try {


            URL urll = new URL(url);
            urlConnection = (HttpURLConnection) urll.openConnection();
            urlConnection.setConnectTimeout(17000);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
             response = builder.toString();

        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
          return response;
    }




    public String makeServiceCallError(String url, int method,
                                  List<NameValuePair> params) {

        StringBuilder builder = new StringBuilder();
        HttpURLConnection urlConnection;

        try {


            URL urll = new URL(url);
            urlConnection = (HttpURLConnection) urll.openConnection();
            urlConnection.setConnectTimeout(17000);
            urlConnection.setRequestMethod("POST");
//            InputStream in = new BufferedInputStream(urlConnection.getErrorStream());




            // Get the response code
            int statusCode = urlConnection.getResponseCode();

            InputStream is = null;

            if (statusCode >= 200 && statusCode < 400) {
                // Create an InputStream in order to extract the response object
                is = urlConnection.getInputStream();
            }
            else {
                is = urlConnection.getErrorStream();
            }
//




            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
            response = builder.toString();

        } catch (Exception e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return response;
    }











    public String doHttpUrlConnectionAction(String desiredUrl)
            throws Exception
    {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }




    public String doHttpUrlConnectionActionPost(String desiredUrl)
            throws Exception
    {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try
        {
            // create the HttpURLConnection
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);

            // give it 15 seconds to respond
            connection.setReadTimeout(15*1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        finally
        {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }



}
