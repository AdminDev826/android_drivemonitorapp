package eco_driving;

/**
 * Created by Dev on 3/22/2017.
 */
public class DrivingScore {

    String trackerID = "";
    String score = "";
    String speeding = "";
    String driving = "";
    String idling = "";

    public String getScore() {
        return score;
    }

    public String getTrackerID() {
        return trackerID;
    }

    public void setTrackerID(String trackerID) {
        this.trackerID = trackerID;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSpeeding() {
        return speeding;
    }

    public void setSpeeding(String speeding) {
        this.speeding = speeding;
    }

    public String getDriving() {
        return driving;
    }

    public void setDriving(String driving) {
        this.driving = driving;
    }

    public String getIdling() {
        return idling;
    }

    public void setIdling(String idling) {
        this.idling = idling;
    }
}
