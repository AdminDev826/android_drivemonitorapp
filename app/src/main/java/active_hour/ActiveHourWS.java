package active_hour;

/**
 * Created by jft on 10/8/17.
 */

public class ActiveHourWS {

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "{" +
                "\"day\":" + day +
                ", \"startTime\":" + "\"" + startTime + "\"" +
                ", \"endTime\":" + "\"" + endTime + "\"" +
                "}";
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    int day;
    String startTime;
    String endTime;
}
