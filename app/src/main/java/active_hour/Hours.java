package active_hour;

/**
 * Created by Farhan on 9/22/2016.
 */

public class Hours {

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart_time() {
        return Start_time;
    }

    public void setStart_time(String start_time) {
        Start_time = start_time;
    }

    public String getEnd_time() {
        return End_time;
    }

    public void setEnd_time(String end_time) {
        End_time = end_time;
    }

    public String getTrackerID() {
        return TrackerID;
    }

    public void setTrackerID(String trackerID) {
        TrackerID = trackerID;
    }

    String day;
    String Start_time;
    String End_time;
    String TrackerID;
}
