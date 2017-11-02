package maps;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev on 2/23/2017.
 */
public class TrackLocation {

    String Label;
    String trackerID;
    String StrSpeed;
    String movement_status;
    String lastUpdate = "";
    Boolean updated = true;
    List<String> listLat = new ArrayList<>();
    List<String> listLong = new ArrayList<>();

    public float getBearing(){
        float bearing = 0;
        if(listLong.size() > 1){
            Location prev = new Location("");
            Location nex = new Location("");
            int listLength = listLat.size();
            prev.setLatitude(Double.parseDouble(listLat.get(listLength - 2)));
            prev.setLongitude(Double.parseDouble(listLong.get(listLength - 2)));
            nex.setLatitude(Double.parseDouble(listLat.get(listLength - 1)));
            nex.setLongitude(Double.parseDouble(listLong.get(listLength - 1)));

            bearing = prev.bearingTo(nex);
        }
        return bearing;
    }

    public List<LatLng> getLatLongList() {
        List<LatLng> ListLatLong = new ArrayList<>();
        LatLng latLng;
        for ( int i = 0; i < listLat.size(); i++ ) {
            float flat = Float.parseFloat(listLat.get(i));
            float flong = Float.parseFloat(listLong.get(i));

            latLng = new LatLng(flat, flong);
            ListLatLong.add(latLng);
        }
        return ListLatLong;
    }

    public String getMovement_status() {
        String cap = movement_status.substring(0, 1).toUpperCase() + movement_status.substring(1);
        return cap;
    }

    public void setMovement_status(String movement_status) {
        this.movement_status = movement_status;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public String getTrackerID() {
        return trackerID;
    }

    public void setTrackerID(String trackerID) {
        this.trackerID = trackerID;
    }

    public void addData(String speed, String latt, String longg1, String movement){
        this.updated = true;
        if(listLat.size() > 0){
            if(listLat.get(listLat.size() - 1).equals(latt) && listLong.get(listLong.size() - 1).equals(longg1)){
                this.updated = false;
                return;
            }
        }
        this.StrSpeed = speed;
        this.movement_status = movement;
        if ( this.listLat.size() > 1 ){
            this.listLat.remove(0);
        }
        this.listLat.add(latt);
        if(this.listLong.size() > 1){
            this.listLong.remove(0);
        }
        this.listLong.add(longg1);
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void addLat(String lat){
        if(listLat.size() > 0){
            if(listLat.get(listLat.size() - 1).equals(lat)){
                return;
            }
        }
        if ( listLat.size() > 1 ){
            listLat.remove(0);
        }
        listLat.add(lat);
    }
    public void addLong(String long1){
        if(listLong.size() > 0){
            if(listLong.get(listLong.size() - 1).equals(long1)){
                return;
            }
        }
        if(listLong.size() > 1){
            listLong.remove(0);
        }
        listLong.add(long1);
    }
    public String getLastLat() {
        return listLat.get(listLat.size() - 1);
    }
    public String getLastLong() {
        return listLong.get(listLong.size() - 1);
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getStrSpeed() {
        return StrSpeed;
    }

    public void setStrSpeed(String strSpeed) {
        StrSpeed = strSpeed;
    }

    public List<String> getListLat() {
        return listLat;
    }

    public void setListLat(List<String> listLat) {
        this.listLat = listLat;
    }

    public List<String> getListLong() {
        return listLong;
    }

    public void setListLong(List<String> listLong) {
        this.listLong = listLong;
    }
}
