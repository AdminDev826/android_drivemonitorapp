package assing_task;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lineztech.farhan.vehicaltarckingapp.R;

import java.util.List;

import maps.MapsMain;

public class AdptorAssignTask extends BaseAdapter {
    Context context;
    String layoutRating;
    float rating;
    String CourseID, CouresName;
    int showLastLayOut = 0;
    protected List<AssignTask> listSearchedCourses;
    LayoutInflater inflater;

    public AdptorAssignTask(Context context, List<AssignTask> listSearchedCourses) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.CourseID = CourseID;
        this.CouresName = CouresName;
    }

    public int getCount() {
        return listSearchedCourses.size();
    }

    public AssignTask getItem(int position) {
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
        ViewHolder holder;
        final AssignTask track = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.assign_tast_adapter, parent, false);
            holder.idtvType = (TextView) convertView.findViewById(R.id.idtvType);
            holder.idtvEvent = (TextView) convertView.findViewById(R.id.idtvEvent);
            holder.idtvTime = (TextView) convertView.findViewById(R.id.idtvTime);
            holder.idtvMessage = (TextView) convertView.findViewById(R.id.idtvMessage);
            holder.idtvAddresss = (TextView) convertView.findViewById(R.id.idtvAddresss);
            holder.idtvTrack = (TextView) convertView.findViewById(R.id.idtvTrack);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.idtvType.setText(track.getId());
        holder.idtvEvent.setText(track.getMessage());
        holder.idtvTime.setText(track.getAddress());
        holder.idtvMessage.setText(track.getTime());
        holder.idtvAddresss.setText(track.getTracker_id());
        holder.idtvTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapAssign.class);
                intent.putExtra("lat", track.getLat());
                intent.putExtra("lng", track.getLng());
                intent.putExtra("radios", track.getRadious());
                intent.putExtra("title", track.getAddress());
                intent.putExtra("address", track.getNewAddress());
                context.startActivity(intent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView idtvType, idtvEvent, idtvTime, idtvMessage, idtvAddresss, idtvTrack;
    }


}
