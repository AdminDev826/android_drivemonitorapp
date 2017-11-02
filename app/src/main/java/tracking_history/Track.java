package tracking_history;

/**
 * Created by Farhan on 7/29/2016.
 */
public class Track {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTracker_id() {
        return tracker_id;
    }

    public void setTracker_id(String tracker_id) {
        this.tracker_id = tracker_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIs_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }

    private String type;
    private String id;
    private String event;
    private String message;
    private String time;
    private String tracker_id;
    private String address;
    private String is_read;
    private String norm_fuel_consumed;
    private String speed;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getNorm_fuel_consumed() {
        return norm_fuel_consumed;
    }

    public void setNorm_fuel_consumed(String norm_fuel_consumed) {
        this.norm_fuel_consumed = norm_fuel_consumed;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getEnd_address() {
        return end_address;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }

    private String start_date;
    private String end_date;
    private String length;
    private String end_address;
    private String start_address;

}
