package routine_maintinance;

/**
 * Created by Farhan on 7/29/2016.
 */
public class RoutineMaintinance {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getWear_percentage() {
        return wear_percentage;
    }

    public void setWear_percentage(String wear_percentage) {
        this.wear_percentage = wear_percentage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getVehicle_label() {
        return vehicle_label;
    }

    public void setVehicle_label(String vehicle_label) {
        this.vehicle_label = vehicle_label;
    }

    public String getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(String completion_date) {
        this.completion_date = completion_date;
    }

    private String id;
    private String vehicle_id;
    private String status;
    private String end_date;
    private String wear_percentage;
    private String description;
    private String cost;
    private String vehicle_label;
    private String completion_date;
}
