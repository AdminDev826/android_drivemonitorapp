package routine_maintinance;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lineztech.farhan.vehicaltarckingapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dashboard.DashboardFragment1;
import db.DatabaseHandler;
import util.AppSingleton;

public class AdptorRoutineMaintinance extends BaseAdapter {
    Context context;
    String TrackerID, hashCode;
    ProgressDialog progressDialog;
    protected List<RoutineMaintinance> listSearchedCourses;
    LayoutInflater inflater;

    public AdptorRoutineMaintinance(Context context, List<RoutineMaintinance> listSearchedCourses, String trackid, String hashCode) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        TrackerID = trackid;
        this.hashCode = hashCode;
    }
    public int getCount() {
        return listSearchedCourses.size();
    }

    public RoutineMaintinance getItem(int position) {
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
        final ViewHolder holder;
        final RoutineMaintinance track = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.routinemaintence_adaptor, parent, false);
            holder.idtvEstimated = (TextView) convertView.findViewById(R.id.idtvEstimated);
            holder.idtvVehicle = (TextView) convertView.findViewById(R.id.idtvVehicle);
            holder.idtvServiceWork = (TextView) convertView.findViewById(R.id.idtvServiceWork);
            holder.idtvCost = (TextView) convertView.findViewById(R.id.idtvCost);
            holder.idtvStatus = (TextView) convertView.findViewById(R.id.idtvStatus);
            holder.tvCompleted = (TextView) convertView.findViewById(R.id.maintenance_tvCompleted);
            holder.loCompleted = (LinearLayout) convertView.findViewById(R.id.maintenance_lo_completed);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.idtvEstimated.setText(track.getEnd_date());
        holder.idtvVehicle.setText(track.getVehicle_label());
        holder.idtvServiceWork.setText(track.getDescription());
        holder.idtvCost.setText(track.getCost());
        holder.idtvStatus.setText(track.getStatus());

        if(track.getStatus().equals("done")){
            holder.loCompleted.getLayoutParams().height = 0;
        }
        else {
            holder.tvCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                updateData(track, holder);
                }
            });
        }

        return convertView;
    }
    void updateData(final RoutineMaintinance track, final ViewHolder holder){
        progressDialog = ProgressDialog.show(context, "",
                "Please wait ...", true);
        String url = "http://api.navixy.com/v2/vehicle/service_task/set_status/?"+"hash="+hashCode+"&task_id="+track.getId()+"&status=done";
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("success");
                            if(status.equals("true")){
                                updateViewHolder(track, holder);
                            }else{
                                Toast.makeText(context, "Failed !", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Toast.makeText(context, "Failed !", Toast.LENGTH_SHORT).show();
                    }
                });
        AppSingleton.getInstance(context).addToRequestQueue(jsonRequest, "smartdrive");
    }
    void updateViewHolder(RoutineMaintinance track, ViewHolder holder){
        double tmp = Double.parseDouble(track.getCost());
        RoutineMaintinaceMain.spent_cost += tmp;
        RoutineMaintinaceMain.txt_spent_cost.setText("$"+RoutineMaintinaceMain.spent_cost);
        if(track.getStatus().equals("created")){
            RoutineMaintinaceMain.scheduled_cost -= tmp;
            RoutineMaintinaceMain.txt_scheduled_cost.setText("$"+RoutineMaintinaceMain.scheduled_cost);
        }
        holder.loCompleted.getLayoutParams().height = 0;
        holder.idtvStatus.setText("done");
        DashboardFragment1.updateMaintenance(track);
        Toast.makeText(context, "Success !", Toast.LENGTH_SHORT).show();
    }

    private class ViewHolder {
        TextView idtvEstimated, idtvVehicle, idtvServiceWork, idtvCost, idtvStatus, tvCompleted;
        LinearLayout loCompleted;
    }
}
