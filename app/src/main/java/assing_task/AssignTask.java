package assing_task;

/**
 * Created by Farhan on 7/29/2016.
 */
public class AssignTask {
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getRadious() {
        return radious;
    }

    public void setRadious(String radious) {
        this.radious = radious;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    private String lat;
    private String lng;
    private String radious;
    private String newAddress;
}
