package active_hour;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lineztech.farhan.vehicaltarckingapp.R;

import java.util.List;

import assing_task.AssignTask;
import assing_task.MapAssign;

public class AdptorAssignhours extends BaseAdapter {
    Context context;
    String layoutRating;
    float rating;
    String CourseID, CouresName;
    int showLastLayOut = 0;
    protected List<Hours> listSearchedCourses;
    LayoutInflater inflater;

    public AdptorAssignhours(Context context, List<Hours> listSearchedCourses) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.CourseID = CourseID;
        this.CouresName = CouresName;
    }

    public int getCount() {
        return listSearchedCourses.size();
    }

    public Hours getItem(int position) {
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
        final Hours track = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.avtive_houras_adapter, parent, false);
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
        holder.idtvType.setText(track.getTrackerID());
        holder.idtvEvent.setText(track.getDay());
        holder.idtvTime.setText(track.getStart_time());
        holder.idtvMessage.setText(track.getEnd_time());
        holder.idtvAddresss.setText(track.getStart_time());

        return convertView;
    }

    private class ViewHolder {
        TextView idtvType, idtvEvent, idtvTime, idtvMessage, idtvAddresss, idtvTrack;
    }


}
