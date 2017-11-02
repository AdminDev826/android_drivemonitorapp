package eco_driving;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lineztech.farhan.vehicaltarckingapp.R;

import java.util.List;

import assing_task.AssignTask;

public class AdptorSummary extends BaseAdapter {
    Context context;
    String layoutRating;
    float  rating;
    String CourseID,CouresName;
    int showLastLayOut = 0;
    protected List<SummaryCons> listSearchedCourses;
    LayoutInflater inflater;
    public AdptorSummary(Context context, List<SummaryCons> listSearchedCourses) {
        this.listSearchedCourses = listSearchedCourses;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.CourseID = CourseID;
        this.CouresName = CouresName;
    }
    public int getCount() {
        return listSearchedCourses.size();
    }

    public SummaryCons getItem(int position) {
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
        final SummaryCons summaryCons = listSearchedCourses.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.adaptor_summary, parent, false);
            holder.idtvName = (TextView) convertView.findViewById(R.id.idtvName);
            holder.idtvRating = (TextView) convertView.findViewById(R.id.idtvRating);
            holder.idtvMileage = (TextView) convertView.findViewById(R.id.idtvMileage);
            holder.idtvPenaltiesNumber = (TextView) convertView.findViewById(R.id.idtvPenaltiesNumber);
            holder.idtvAvgPenailtiez = (TextView) convertView.findViewById(R.id.idtvAvgPenailtiez);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.idtvName.setText(summaryCons.getName());
        holder.idtvRating.setText(summaryCons.getRating());
        holder.idtvMileage.setText(summaryCons.getMileage());
        holder.idtvPenaltiesNumber.setText(summaryCons.getPenalties_number());
        holder.idtvAvgPenailtiez.setText(summaryCons.getAvg_penalty());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView idtvName, idtvRating, idtvMileage, idtvPenaltiesNumber, idtvAvgPenailtiez;
    }


}
