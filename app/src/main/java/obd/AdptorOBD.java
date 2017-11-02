package obd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lineztech.farhan.vehicaltarckingapp.R;

import java.util.List;

public class AdptorOBD extends BaseAdapter {
    Context context;
    String layoutRating;
    float rating;
    String CourseID, CouresName;
    int showLastLayOut = 0;
    protected List<OBD> listSearchedCourses;
    LayoutInflater inflater;

    public AdptorOBD(Context context, List<OBD> listSearchedCourses) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.CourseID = CourseID;
        this.CouresName = CouresName;
    }

    public int getCount() {
        return listSearchedCourses.size();
    }

    public OBD getItem(int position) {
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
        final OBD track = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.adator_obd_main, parent, false);
            holder.idtvValue = (TextView) convertView.findViewById(R.id.idtvValue);
            holder.idtvName = (TextView) convertView.findViewById(R.id.idtvName_);
            holder.idtvIcon = (TextView) convertView.findViewById(R.id.idtvIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String unit = track.getUnits();

        String name = track.getName();

        if (name.equals("obd_coolant_t")||name.equals("can_coolant_t")) {
            name = "Coolant temperature";
            unit = "°C";
        } else if (name.equals("obd_speed")||name.equals("can_speed")) {
            name = "Speed";
            unit = "km/h";
        } else if (name.equals("obd_throttle")||name.equals("can_throttle")) {
            name = "Throttle";
        } else if (name.equals("obd_fuel")||name.equals("can_fuel")) {
            name = "Fuel";
            unit = "L";
        } else if (name.equals("obd_consumption")||name.equals("can_consumption")) {
            name = "Fuel consumption";
            unit = "L";
        } else if (name.equals("obd_engine_load")||name.equals("can_engine_load")) {
            name = "Engine load";
        }else if(name.equals("obd_rpm")||name.equals("can_rpm")){
            name = "RPM";
        }else if(name.equals("can_mileage")||name.equals("obd_mileage")){
            name = "Mileage";
        }

        holder.idtvName.setText(name);
        holder.idtvValue.setText(track.getValue()+" "+unit);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView idtvValue, idtvName, idtvIcon;
    }


}
