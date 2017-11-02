package driver_details;

/**
 * Created by Farhan on 7/28/2016.
 */
public class Driver  {

    private String id = "";
    private String avatar_file_name;
    private String tracker_id = "";
    private String first_name = "";
    private String middle_name = "";
    private String last_name = "";
    private String email = "";
    private String vin = "";
    private String chassis_number = "";
    private String fuel_type = "";
    private String norm_avg_fuel_consumption = "";
    private String liability_insurance_valid_till = "";
    private String department_id = "";
    private String lat = "";
    private String lng = "";
    private String address = "";
    private String model = "";
    private String phone = "";
    private String driver_license_number = "";
    private String driver_license_cats = "";
    private String driver_license_valid_till = "";
    private String hardware_key = "";
    private String reg_number = "";
    private String connection = "";
    private String cont_trackerID = "";
    private String last_update = "";
    private String max_speed = "";
    private String subtype = "";
    private String arage_id = "";
    private String payload_weight = "";
    private String payload_height = "";
    private String payload_lenth = "";
    private String payload_width = "";
    private String passengers = "";
    private String fuel_grade = "";
    private String fuel_tank_volume = "";
    private String wheel_arrangement = "";
    private String tyre_size = "";
    private String tyres_number = "";
    private String liability_insurance_policy_number = "";
    private String free_insurance_policy_number = "";
    private String free_insurance_valid_till = "";
    private String icon_id = "";
    private int[] tags;
    private String garage_id = "";
    private String type = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGarage_id() {
        return garage_id;
    }

    public void setGarage_id(String garage_id) {
        this.garage_id = garage_id;
    }

    public String getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(String max_speed) {
        this.max_speed = max_speed;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getArage_id() {
        return arage_id;
    }

    public void setArage_id(String arage_id) {
        this.arage_id = arage_id;
    }

    public String getPayload_weight() {
        return payload_weight;
    }

    public void setPayload_weight(String payload_weight) {
        this.payload_weight = payload_weight;
    }

    public String getPayload_height() {
        return payload_height;
    }

    public void setPayload_height(String payload_height) {
        this.payload_height = payload_height;
    }

    public String getPayload_lenth() {
        return payload_lenth;
    }

    public void setPayload_lenth(String payload_lenth) {
        this.payload_lenth = payload_lenth;
    }

    public String getPayload_width() {
        return payload_width;
    }

    public void setPayload_width(String payload_width) {
        this.payload_width = payload_width;
    }

    public String getPassengers() {
        return passengers;
    }

    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }

    public String getFuel_grade() {
        return fuel_grade;
    }

    public void setFuel_grade(String fuel_grade) {
        this.fuel_grade = fuel_grade;
    }

    public String getFuel_tank_volume() {
        return fuel_tank_volume;
    }

    public void setFuel_tank_volume(String fuel_tank_volume) {
        this.fuel_tank_volume = fuel_tank_volume;
    }

    public String getWheel_arrangement() {
        return wheel_arrangement;
    }

    public void setWheel_arrangement(String wheel_arrangement) {
        this.wheel_arrangement = wheel_arrangement;
    }

    public String getTyre_size() {
        return tyre_size;
    }

    public void setTyre_size(String tyre_size) {
        this.tyre_size = tyre_size;
    }

    public String getTyres_number() {
        return tyres_number;
    }

    public void setTyres_number(String tyres_number) {
        this.tyres_number = tyres_number;
    }

    public String getLiability_insurance_policy_number() {
        return liability_insurance_policy_number;
    }

    public void setLiability_insurance_policy_number(String liability_insurance_policy_number) {
        this.liability_insurance_policy_number = liability_insurance_policy_number;
    }

    public String getFree_insurance_policy_number() {
        return free_insurance_policy_number;
    }

    public void setFree_insurance_policy_number(String free_insurance_policy_number) {
        this.free_insurance_policy_number = free_insurance_policy_number;
    }

    public String getFree_insurance_valid_till() {
        return free_insurance_valid_till;
    }

    public void setFree_insurance_valid_till(String free_insurance_valid_till) {
        this.free_insurance_valid_till = free_insurance_valid_till;
    }

    public String getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(String icon_id) {
        this.icon_id = icon_id;
    }

    public int[] getTags() {
        return tags;
    }

    public void setTags(int[] tags) {
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar_file_name() {
        return avatar_file_name;
    }

    public void setAvatar_file_name(String avatar_file_name) {
        this.avatar_file_name = avatar_file_name;
    }

    public String getTracker_id() {
        return tracker_id;
    }

    public void setTracker_id(String tracker_id) {
        this.tracker_id = tracker_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getHardware_key() {
        return hardware_key;
    }

    public void setHardware_key(String hardware_key) {
        this.hardware_key = hardware_key;
    }

    public String getDriver_license_valid_till() {
        return driver_license_valid_till;
    }

    public void setDriver_license_valid_till(String driver_license_valid_till) {
        this.driver_license_valid_till = driver_license_valid_till;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDriver_license_cats() {
        return driver_license_cats;
    }

    public void setDriver_license_cats(String driver_license_cats) {
        this.driver_license_cats = driver_license_cats;
    }

    public String getDriver_license_number() {
        return driver_license_number;
    }

    public void setDriver_license_number(String driver_license_number) {
        this.driver_license_number = driver_license_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public String getReg_number() {
        return reg_number;
    }

    public void setReg_number(String reg_number) {
        this.reg_number = reg_number;
    }



    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }



    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getChassis_number() {
        return chassis_number;
    }

    public void setChassis_number(String chassis_number) {
        this.chassis_number = chassis_number;
    }

    public String getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public String getLiability_insurance_valid_till() {
        return liability_insurance_valid_till;
    }

    public void setLiability_insurance_valid_till(String liability_insurance_valid_till) {
        this.liability_insurance_valid_till = liability_insurance_valid_till;
    }

    public String getNorm_avg_fuel_consumption() {
        return norm_avg_fuel_consumption;
    }

    public void setNorm_avg_fuel_consumption(String norm_avg_fuel_consumption) {
        this.norm_avg_fuel_consumption = norm_avg_fuel_consumption;
    }


    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getCont_trackerID() {
        return cont_trackerID;
    }

    public void setCont_trackerID(String cont_trackerID) {
        this.cont_trackerID = cont_trackerID;
    }



    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }


}
