package notification_alerts.list_notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lineztech.farhan.vehicaltarckingapp.R;

import java.util.List;

import tracking_history.MapsActivity;

public class AdptorNotification extends BaseAdapter {
    Context context;
    String layoutRating;
    float rating;
    String CourseID, CouresName;
    int showLastLayOut = 0;
    protected List<Notification> listSearchedCourses;
    LayoutInflater inflater;

    public AdptorNotification(Context context, List<Notification> listSearchedCourses) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.CourseID = CourseID;
        this.CouresName = CouresName;
    }

    public int getCount() {
        return listSearchedCourses.size();
    }

    public Notification getItem(int position) {
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
        final Notification track = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.adaptor_notification, parent, false);
            holder.idtvType = (TextView) convertView.findViewById(R.id.idtvType);
            holder.idtvEvent = (TextView) convertView.findViewById(R.id.idtvEvent);
            holder.idtvTime = (TextView) convertView.findViewById(R.id.idtvTime);
            holder.idtvMessage = (TextView) convertView.findViewById(R.id.idtvMessage);
            holder.idtvAddresss = (TextView) convertView.findViewById(R.id.idtvAddresss);
            holder.idivNotificationIcon = (ImageView) convertView.findViewById(R.id.idivNotificationIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {


            holder.idtvType.setText(track.getType());
            holder.idtvEvent.setText(track.getId());
            holder.idtvTime.setText(track.getTime());
            String message = track.getMessage();
            try {
                String[] parts = message.split(":");
                String deviceName = parts[0]; // 004
                String state = parts[1]; // 034556

                if (state.equals(" Tracker switched ON or connection restored")) {
                    state = "Ignition started";
                    holder.idivNotificationIcon.setImageResource(R.drawable.e_start);
                } else if (state.equals(" Tracker switched OFF or lost connection")) {
                    state = "Ignition off";
                    holder.idivNotificationIcon.setImageResource(R.drawable.e_stop);
                } else if (state.contains(" ON")){
                    holder.idivNotificationIcon.setImageResource(R.drawable.e_start);
                } else if (state.contains("Ignition OFF")){
                    holder.idivNotificationIcon.setImageResource(R.drawable.e_stop);
                }  else if (state.contains("Parking")){
                    holder.idivNotificationIcon.setImageResource(R.drawable.parking_car);
                }  else if (state.contains("speed exceeding")){
                    holder.idivNotificationIcon.setImageResource(R.drawable.rocket);
                } else{
                    holder.idivNotificationIcon.setImageResource(R.drawable.ic_notifications_none_black_24dp);
                }
                holder.idtvMessage.setText(deviceName + " " + state + " near " + track.getAddress());

            } catch (Exception e) {
                e.printStackTrace();
                holder.idtvMessage.setText(message + " near " + track.getAddress());

            }



            holder.idtvAddresss.setText(track.getAddress());


        } catch (Exception e) {
            e.printStackTrace();
        }

//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, v.getId()+"", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(context, MapsActivity.class);
//                context.startActivity(i);
//            }
//        });

        return convertView;
    }

    private class ViewHolder {
        ImageView idivNotificationIcon;
        TextView idtvType, idtvEvent, idtvTime, idtvMessage, idtvAddresss;
    }


}
