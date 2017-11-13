package eco_driving;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lineztech.farhan.vehicaltarckingapp.R;
import com.lineztech.farhan.vehicaltarckingapp.ServiceHandler;
import com.numetriclabz.numandroidcharts.ChartData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import util.Utils;

public class StackBarChartt extends AppCompatActivity {
    ProgressDialog progressDialog;
    Context context;
    String reportURL;
    String status;
    Float[] value1, value2;
    List<Float> stackedBarValue = new ArrayList<>();
    List<String> stackedBarLabels = new ArrayList<>();
    LinearLayout idllRating, idllTotalPenalty, idllSummary, idllInner;
    StackedBarChart idStackchart;
    BarChart chart;
    ArrayList arrSummary;
    ListView idlvSummary;
    boolean isllRatingLayoutVisible = true;
    boolean isllTotalPenltyLayoutVisible = true;
    boolean isllSummaryLayoutVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.stackbar_chart);
        idllRating = (LinearLayout) findViewById(R.id.idllRating);
        idllTotalPenalty = (LinearLayout) findViewById(R.id.idllTotalPenalty);
        idllSummary = (LinearLayout) findViewById(R.id.idllSummary);
        idllInner = (LinearLayout) findViewById(R.id.idllInner);
        idStackchart = (StackedBarChart) findViewById(R.id.idStackchart);
        chart = (BarChart) findViewById(R.id.chart);
        idlvSummary = (ListView) findViewById(R.id.idlvSummary);
        context = this;
        arrSummary = new ArrayList<String>();
        status = "";
        progressDialog = ProgressDialog.show(context, "",
                "Generating Report...", true);
        AsyncReport runner = new AsyncReport();
        runner.execute();


        idllRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isllRatingLayoutVisible == false) {
                    chart.setVisibility(View.VISIBLE);
                    isllRatingLayoutVisible = true;
                } else {
                    chart.setVisibility(View.GONE);
                    isllRatingLayoutVisible = false;
                }

            }
        });

        idllTotalPenalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isllTotalPenltyLayoutVisible == false) {
                    idStackchart.setVisibility(View.VISIBLE);
                    isllTotalPenltyLayoutVisible = true;
                } else {
                    idStackchart.setVisibility(View.GONE);
                    isllTotalPenltyLayoutVisible = false;
                }

            }
        });

        idllSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isllSummaryLayoutVisible == false) {
                    idllInner.setVisibility(View.VISIBLE);
                    idlvSummary.setVisibility(View.VISIBLE);
                    isllSummaryLayoutVisible = true;
                } else {
                    idllInner.setVisibility(View.GONE);
                    idlvSummary.setVisibility(View.GONE);
                    isllSummaryLayoutVisible = false;
                }

            }
        });


    }


    private ArrayList<BarDataSet> getDataSet(float value, float value1, float value2) {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(value, 0); // Jan
        BarEntry v2e2 = new BarEntry(value1, 1); // Jan
        BarEntry v2e3 = new BarEntry(value2, 2); // Jan
        valueSet2.add(v2e1);
        valueSet2.add(v2e2);
        valueSet2.add(v2e3);

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
//        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues(String title, String title1, String title2) {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add(title);
        xAxis.add(title1);
        xAxis.add(title2);
        return xAxis;
    }

    String tilte;
    String dblvalue;
    String tilte1;
    String tilte2;
    String dblvalue1;
    String dblvalue2;
    String stackLbl0;

    private class AsyncReport extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            String hashCode = Utils.getPreferences("hashCode", context);
            String reportID = Utils.getPreferences("reportID", context);
            reportURL = "https://api.navixy.com/v2/report/tracker/retrieve?hash=" + hashCode + "&report_id=" + reportID;
        }

        protected String doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(reportURL, ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    status = jsonObj.getString("success");
                    JSONObject report = jsonObj.getJSONObject("report");
                    JSONArray sheets = report.getJSONArray("sheets");

                    JSONObject c = sheets.getJSONObject(0);
                    String heade = c.getString("header");
                    JSONArray sections = c.getJSONArray("sections");

                    JSONObject stacked_bar_chart = sections.getJSONObject(0);
                    JSONObject simple_bar_chart = sections.getJSONObject(1);
                    JSONObject summary = sections.getJSONObject(2);

                    JSONArray summaryArray = summary.getJSONArray("data");
                    JSONObject rowObj = summaryArray.getJSONObject(0);

                    JSONArray rowArray = rowObj.getJSONArray("rows");

                    for (int i = 0; i < rowArray.length(); i++) {
                        SummaryCons summaryCons = new SummaryCons();
                        JSONObject rowObjMain = rowArray.getJSONObject(i);
                        JSONObject avg_penalty = rowObjMain.getJSONObject("avg_penalty");
                        String v = avg_penalty.getString("v");
                        summaryCons.setAvg_penalty(v);

                        JSONObject penalties_number = rowObjMain.getJSONObject("penalties_number");
                        String v1 = penalties_number.getString("v");
                        summaryCons.setPenalties_number(v1);

                        JSONObject name = rowObjMain.getJSONObject("name");
                        String v2 = name.getString("v");
                        summaryCons.setName(v2);

                        JSONObject rating = rowObjMain.getJSONObject("rating");
                        String v3 = rating.getString("v");
                        summaryCons.setRating(v3);

                        JSONObject mileage = rowObjMain.getJSONObject("mileage");
                        String v4 = mileage.getString("v");
                        summaryCons.setMileage(v4);

                        arrSummary.add(summaryCons);
                    }


                    String typet = stacked_bar_chart.getString("type");
                    JSONObject y_axis = stacked_bar_chart.getJSONObject("y_axis");


                    String label = y_axis.getString("label");

                    JSONArray data = stacked_bar_chart.getJSONArray("data");
                    JSONArray data_simple_bar_chart = simple_bar_chart.getJSONArray("bars");

                    JSONObject obj_simple_bar_chart = data_simple_bar_chart.getJSONObject(0);

                    tilte = obj_simple_bar_chart.getString("title");
                    Log.d("tilte", tilte);

                    JSONObject valuesObj = obj_simple_bar_chart.getJSONObject("x");
                    dblvalue = valuesObj.getString("v");

                    JSONObject obj_simple_bar_chart1 = data_simple_bar_chart.getJSONObject(1);
                    tilte1 = obj_simple_bar_chart1.getString("title");
                    Log.d("tilte", tilte1);
                    JSONObject valuesObj1 = obj_simple_bar_chart1.getJSONObject("x");
                    dblvalue1 = valuesObj1.getString("v");


                    JSONObject obj_simple_bar_chart2 = data_simple_bar_chart.getJSONObject(2);
                    tilte2 = obj_simple_bar_chart2.getString("title");
                    Log.d("tilte", tilte2);
                    JSONObject valuesObj2 = obj_simple_bar_chart2.getJSONObject("x");
                    dblvalue2 = valuesObj2.getString("v");


                    for (int i = 0; i < data.length(); i++) {
                        JSONObject x = data.getJSONObject(i);

                        JSONObject xx = x.getJSONObject("x");
                        String stackLbl0 = xx.getString("v");
                        stackedBarLabels.add(stackLbl0);
                        JSONObject bars = x.getJSONObject("bars");
                        JSONObject idling = bars.getJSONObject("idling");
                        JSONObject speeding = bars.getJSONObject("speeding");
                        JSONObject harsh_driving = bars.getJSONObject("harsh_driving");

                        String idling_v = idling.getString("v");
                        String speeding_v = speeding.getString("v");
                        String harsh_driving_v = harsh_driving.getString("v");
                        Log.d("idling_v", idling_v);
                        Log.d("speeding_v", speeding_v);
                        Log.d("harsh_driving_v", harsh_driving_v);

                        value1 = new Float[]{Float.parseFloat(idling_v), Float.parseFloat(speeding_v)};
                        stackedBarValue.add(Float.parseFloat(idling_v));
                        stackedBarValue.add(Float.parseFloat(speeding_v));
                        stackedBarValue.add(Float.parseFloat(harsh_driving_v));
                        value2 = new Float[]{3f, 5f};
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
            progressDialog.dismiss();
            try {

                if (status.equals("true")) {

                    List<ChartData> value = new ArrayList<>();
                    value.add(new ChartData(value1, "idling"));
                    value.add(new ChartData(value2, "speeding"));
                    value.add(new ChartData(value1, "harsh_driving"));
                    List<String> h_lables = new ArrayList<>();

                    for (int i = 0; i < stackedBarLabels.size(); i++) {
                        h_lables.add(stackedBarLabels.get(i));
                    }


                    idStackchart.setHorizontal_label(h_lables);
                    idStackchart.setData(value);
                    idStackchart.setDescription("Stacked bar Chart");


                    BarData data = new BarData(getXAxisValues(tilte, tilte1, tilte2), getDataSet(Float.parseFloat(dblvalue), Float.parseFloat(dblvalue1), Float.parseFloat(dblvalue2)));
                    chart.setData(data);
                    chart.setDescription("");
                    chart.animateXY(2000, 2000);
                    chart.invalidate();


                    AdptorSummary adapter = new AdptorSummary(context, arrSummary);
                    idlvSummary.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(context, "Problem in generating report", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        protected void onProgressUpdate(String... text) {
        }

    }


}