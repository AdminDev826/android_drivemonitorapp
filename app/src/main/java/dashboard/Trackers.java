package dashboard;

import java.io.Serializable;

/**
 * Created by Farhan on 8/23/2016.
 */
public class Trackers implements Serializable{

    public String getTrackerID() {
        return trackerID;
    }

    public void setTrackerID(String trackerID) {
        this.trackerID = trackerID;
    }

    public String getTrackerLabel() {
        return trackerLabel;
    }

    public void setTrackerLabel(String trackerLabel) {
        this.trackerLabel = trackerLabel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String trackerID;
    private String trackerLabel;
    private String id;

}
