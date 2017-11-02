package obd;

/**
 * Created by Farhan on 10/6/2016.
 */

public class OBD {

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnits_type() {
        return units_type;
    }

    public void setUnits_type(String units_type) {
        this.units_type = units_type;
    }

    public String getUser_time() {
        return user_time;
    }

    public void setUser_time(String user_time) {
        this.user_time = user_time;
    }

    String value;
    String label;
    String name;
    String units;
    String type;
    String units_type;
    String user_time;
}
