package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import active_hour.Hours;
import dashboard.Trackers;
import driver_details.Driver;
import routine_maintinance.RoutineMaintinance;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 16;
    // Database Name
    private static final String DATABASE_NAME = "TrackingApp.db";
    private static final String TBLDriverDetails = "tbl_DriverDetails";
    private static final String IndexID = "IndexID";
    private static final String id = "id";
    private static final String tracker_id = "tracker_id";
    private static final String first_name = "first_name";
    private static final String model = "model";
    private static final String reg_number = "reg_number";
    private static final String vin = "vin";
    private static final String chassis_number = "chassis_number";
    private static final String fuel_type = "fuel_type";
    private static final String norm_avg_fuel_consumption = "norm_avg_fuel_consumption";
    private static final String liability_insurance_valid_till = "liability_insurance_valid_till";
    private static final String max_speed = "max_speed";
    private static final String type = "type";
    private static final String subtype = "subtype";
    private static final String garage_id = "garage_id";
    private static final String payload_weight = "payload_weight";
    private static final String payload_height = "payload_height";
    private static final String payload_length = "payload_length";
    private static final String payload_width = "payload_width";
    private static final String passengers = "passengers";
    private static final String fuel_grade = "fuel_grade";
    private static final String fuel_tank_volume = "fuel_tank_volume";
    private static final String wheel_arrangement = "wheel_arrangement";
    private static final String tyre_size = "tyre_size";
    private static final String tyres_number = "tyres_number";
    private static final String liability_insurance_policy_number = "liability_insurance_policy_number";
    private static final String free_insurance_policy_number = "free_insurance_policy_number";
    private static final String free_insurance_valid_till = "free_insurance_valid_till";


    private static final String tbl_connectonStatus = "tbl_connectonStatus";

    private static final String indexIDD = "id";
    private static final String con_tracker_id = "tracker_id";
    private static final String connection_Status = "connection_Status";
    private static final String last_update = "last_update";


    private static final String TblTrackers = "TblTrackers";
    private static final String indexIDDD = "id";
    private static final String tracker_id_new = "tracker_id";
    private static final String tracker_label = "label";
    private static final String tracker_lat = "lat";
    private static final String tracker_lng = "lng";


    private static final String TblMaintenance = "tbl_maintenace";
    private static final String taskID = "task_id";
    private static final String taskCost = "task_cost";
    private static final String TblActiveHours = "TblActiveHours";
    private static final String AHID = "AHID";
    private static final String day = "day";
    private static final String sTime = "Start_time";
    private static final String eTime = "End_time";
    private static final String TrackerID = "TrackerID";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TBLSCORE_CARD = "CREATE TABLE IF NOT EXISTS " + TBLDriverDetails + "("
                + IndexID + " INTEGER PRIMARY KEY,"
                + id + " TEXT,"
                + tracker_id + " TEXT,"
                + first_name + " TEXT,"
                + model + " TEXT,"
                + reg_number + " TEXT,"
                + vin + " TEXT,"
                + chassis_number + " TEXT,"
                + fuel_type + " TEXT,"
                + norm_avg_fuel_consumption + " TEXT,"
                + liability_insurance_valid_till + " TEXT,"
                + max_speed + " TEXT,"
                + type + " TEXT,"
                + subtype + " TEXT,"
                + garage_id + " TEXT,"
                + payload_height + " TEXT,"
                + payload_weight + " TEXT,"
                + payload_width + " TEXT,"
                + payload_length + " TEXT,"
                + passengers + " TEXT,"
                + fuel_grade + " TEXT,"
                + fuel_tank_volume + " TEXT,"
                + wheel_arrangement + " TEXT,"
                + tyre_size + " TEXT,"
                + tyres_number + " TEXT,"
                + liability_insurance_policy_number + " TEXT,"
                + free_insurance_policy_number + " TEXT,"
                + free_insurance_valid_till + " TEXT"
                + ")";


        String CREATE_TBL_CONNECTION_STATUS = "CREATE TABLE IF NOT EXISTS " + tbl_connectonStatus + "("
                + indexIDD + " INTEGER PRIMARY KEY,"
                + con_tracker_id + " TEXT,"
                + connection_Status + " TEXT,"
                + last_update + " TEXT"
                + ")";


        String CREATE_TBL_Trackers = "CREATE TABLE IF NOT EXISTS " + TblTrackers + "("
                + indexIDDD + " INTEGER PRIMARY KEY,"
                + tracker_id_new + " TEXT,"
                + tracker_label + " TEXT, "
                + tracker_lat + " TEXT, "
                + tracker_lng + " TEXT "
                + ")";


        String CREATE_TblActiveHours = "CREATE TABLE IF NOT EXISTS " + TblActiveHours + "("
                + AHID + " INTEGER PRIMARY KEY,"
                + day + " TEXT,"
                + sTime + " TEXT, "
                + eTime + " TEXT, "
                + TrackerID + " TEXT "
                + ")";
        String CREATE_TblMaintenance = "CREATE TABLE IF NOT EXISTS " + TblMaintenance + "("
                + AHID + " INTEGER PRIMARY KEY,"
                + taskID + " TEXT, "
                + taskCost + " TEXT, "
                + eTime + " TEXT, "
                + TrackerID + " TEXT "
                + ")";

        db.execSQL(CREATE_TBLSCORE_CARD);
        db.execSQL(CREATE_TBL_CONNECTION_STATUS);
        db.execSQL(CREATE_TBL_Trackers);
        db.execSQL(CREATE_TblActiveHours);
        db.execSQL(CREATE_TblMaintenance);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBLDriverDetails);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_connectonStatus);
        db.execSQL("DROP TABLE IF EXISTS " + TblTrackers);
        db.execSQL("DROP TABLE IF EXISTS " + TblActiveHours);
        onCreate(db);
    }

    public void refreshTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TBLDriverDetails);
        db.execSQL("DROP TABLE IF EXISTS " + tbl_connectonStatus);
        db.execSQL("DROP TABLE IF EXISTS " + TblTrackers);
//        db.execSQL("DROP TABLE IF EXISTS " + TblActiveHours);
        onCreate(db);
    }


    public void addDriverDetails(Driver driver) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, driver.getId()); // Course Name
        values.put(tracker_id, driver.getTracker_id()); // Course Name
        values.put(first_name, driver.getFirst_name()); // Course Name
        values.put(model, driver.getModel()); // Course Name
        values.put(reg_number, driver.getReg_number()); // Course Phone
        values.put(vin, driver.getVin()); // Course Name
        values.put(chassis_number, driver.getChassis_number()); // Course Name
        values.put(fuel_type, driver.getFuel_type()); // Course Name
        values.put(norm_avg_fuel_consumption, driver.getNorm_avg_fuel_consumption()); // Course Name
        values.put(liability_insurance_valid_till, driver.getLiability_insurance_valid_till()); // Course Name

        values.put(max_speed, driver.getMax_speed());
        values.put(type, driver.getType());
        values.put(subtype, driver.getSubtype());
        values.put(garage_id, driver.getGarage_id());
        values.put(payload_height, driver.getPayload_height());
        values.put(payload_length, driver.getPayload_lenth());
        values.put(payload_weight, driver.getPayload_weight());
        values.put(payload_width, driver.getPayload_width());
        values.put(passengers, driver.getPassengers());
        values.put(fuel_grade, driver.getFuel_grade());
        values.put(fuel_tank_volume, driver.getFuel_tank_volume());
        values.put(wheel_arrangement, driver.getWheel_arrangement());
        values.put(tyre_size, driver.getTyre_size());
        values.put(tyres_number, driver.getTyres_number());
        values.put(liability_insurance_policy_number, driver.getLiability_insurance_policy_number());
        values.put(free_insurance_policy_number, driver.getFree_insurance_policy_number());
        values.put(free_insurance_valid_till, driver.getFree_insurance_valid_till());

        db.insert(TBLDriverDetails, null, values);
        db.close(); // Closing database connection
    }

    public void updateDriverDetails(Driver driver) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, driver.getId()); // Course Name
        values.put(tracker_id, driver.getTracker_id()); // Course Name
        values.put(first_name, driver.getFirst_name()); // Course Name
        values.put(model, driver.getModel()); // Course Name
        values.put(reg_number, driver.getReg_number()); // Course Phone
        values.put(vin, driver.getVin()); // Course Name
        values.put(chassis_number, driver.getChassis_number()); // Course Name
        values.put(fuel_type, driver.getFuel_type()); // Course Name
        values.put(norm_avg_fuel_consumption, driver.getNorm_avg_fuel_consumption()); // Course Name
        values.put(liability_insurance_valid_till, driver.getLiability_insurance_valid_till()); // Course Name

        values.put(max_speed, driver.getMax_speed());
        values.put(type, driver.getType());
        values.put(subtype, driver.getSubtype());
        values.put(garage_id, driver.getGarage_id());
        values.put(payload_height, driver.getPayload_height());
        values.put(payload_length, driver.getPayload_lenth());
        values.put(payload_weight, driver.getPayload_weight());
        values.put(payload_width, driver.getPayload_width());
        values.put(passengers, driver.getPassengers());
        values.put(fuel_grade, driver.getFuel_grade());
        values.put(fuel_tank_volume, driver.getFuel_tank_volume());
        values.put(wheel_arrangement, driver.getWheel_arrangement());
        values.put(tyre_size, driver.getTyre_size());
        values.put(tyres_number, driver.getTyres_number());
        values.put(liability_insurance_policy_number, driver.getLiability_insurance_policy_number());
        values.put(free_insurance_policy_number, driver.getFree_insurance_policy_number());
        values.put(free_insurance_valid_till, driver.getFree_insurance_valid_till());

        String[] args = new String[]{driver.getTracker_id()};
        db.update(TBLDriverDetails, values, TrackerID + "=?", args);
    }


    public void addTrackers(Trackers trackers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(indexIDDD, trackers.getId()); // Course Name
        values.put(tracker_id_new, trackers.getTrackerID()); // Course Name
        values.put(tracker_label, trackers.getTrackerLabel()); // Course Name
        db.insert(TblTrackers, null, values);
        db.close(); // Closing database connection
    }


    public void addConnectonStatus(Driver driver) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(con_tracker_id, driver.getCont_trackerID()); // Course Name
        values.put(connection_Status, driver.getConnection()); // Course Name
        values.put(last_update, driver.getLast_update()); // Course Name
        db.insert(tbl_connectonStatus, null, values);
        db.close(); // Closing database connection
    }


    public void addActiveHours(Hours hours) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(day, hours.getDay()); // Course Name
        values.put(sTime, hours.getStart_time()); // Course Name
        values.put(eTime, hours.getEnd_time()); // Course Name
        values.put(TrackerID, hours.getTrackerID()); // Course Name
        db.insert(TblActiveHours, null, values);
        db.close(); // Closing database connection
    }

    public String getStatus(String tracker_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_connectonStatus + " WHERE tracker_id = '" + String.valueOf(tracker_id).trim() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            if (cursor != null)
                cursor.moveToFirst();
            String connectionStatus = null;
            connectionStatus = cursor.getString(2);
            cursor.close();
            return connectionStatus;
        } else {
            return null;
        }
    }


    public String getLastUpdate(String tracker_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tbl_connectonStatus + " WHERE tracker_id = '" + String.valueOf(tracker_id).trim() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            if (cursor != null)
                cursor.moveToFirst();
            String lastUpdated = null;
            lastUpdated = cursor.getString(3);
            cursor.close();
            return lastUpdated;
        } else {
            return null;
        }
    }


    public String getCountDays(String tracker_id, String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String itemname = null;
        String selectQuery = "SELECT count (TrackerID) as totalCount FROM " + TblActiveHours + " WHERE TrackerID = '" + String.valueOf(tracker_id).trim() + "'" + " AND day = '" + String.valueOf(day).trim() + "'";

        String q = "SELECT count (TrackerID) from TblActiveHours where TrackerID = '116931' AND  day = 'Monday'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            itemname = cursor.getString(cursor.getColumnIndex("totalCount"));
        }
        cursor.close();
        return itemname;
    }


    public int updateExistingTime(String day_, String s_time, String e_time, String trackerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(day, day_);
        values.put(sTime, s_time);
        values.put(eTime, e_time);
        values.put(TrackerID, trackerID);
        String[] args = new String[]{trackerID, day_};
        return db.update(TblActiveHours, values, TrackerID + "=? AND " + day + "=?", args);
    }

    public Hours getSETime(String trackerID, String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TblActiveHours + " WHERE  TrackerID = '" + String.valueOf(trackerID).trim() + "'" + " AND day = '" + day.trim() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            if (cursor != null)
                cursor.moveToFirst();
            Hours hours = new Hours();
            hours.setStart_time(cursor.getString(2));
            hours.setEnd_time(cursor.getString(3));
            // return course
            Log.e("eee","db"+cursor.getString(2));
            Log.e("eee","db"+cursor.getString(3));
            cursor.close();
            return hours;
        } else {
            return null;
        }
    }

    public List getTrackerList() {
        List<Trackers> trackerList = new ArrayList<Trackers>();
        String selectQuery = "SELECT * FROM " + TblTrackers;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Trackers trackers = new Trackers();
                trackers.setTrackerID(cursor.getString(1));
                trackers.setTrackerLabel(cursor.getString(2));

                trackerList.add(trackers);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trackerList;
    }


    public List getActiveHoursList() {
        List<Hours> trackerList = new ArrayList<Hours>();
        String selectQuery = "SELECT * FROM " + TblActiveHours;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Hours hours = new Hours();
                hours.setDay(cursor.getString(1));
                hours.setStart_time(cursor.getString(2));
                hours.setEnd_time(cursor.getString(3));
                hours.setTrackerID(cursor.getString(4));

                trackerList.add(hours);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trackerList;
    }

    public List getActiveHoursListByTracker(String trackerID) {
        List<Hours> trackerList = new ArrayList<Hours>();
        String selectQuery = "SELECT * FROM " + TblActiveHours + " WHERE TrackerID = '" + String.valueOf(trackerID).trim() + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Hours hours = new Hours();
                hours.setDay(cursor.getString(1));
                hours.setStart_time(cursor.getString(2));
                hours.setEnd_time(cursor.getString(3));
                hours.setTrackerID(cursor.getString(4));

                trackerList.add(hours);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trackerList;
    }


    public int updateTrackerLat(String trackerID, String t_lat, String t_lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(tracker_lat, t_lat);
        values.put(tracker_lng, t_lng);
        String[] args = new String[]{trackerID};
        return db.update(TblTrackers, values, tracker_id_new + "=?", args);
    }


    public int updateTrackerConnection(String trackerID, String connectionStatus, String lastUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(connection_Status, connectionStatus);
        values.put(last_update, lastUpdate);
        String[] args = new String[]{trackerID};
        return db.update(tbl_connectonStatus, values, con_tracker_id + "=?", args);

    }

    public int updateTrackerConnectionOnly(String trackerID, String connectionStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(connection_Status, connectionStatus);
        String[] args = new String[]{trackerID};
        return db.update(tbl_connectonStatus, values, con_tracker_id + "=?", args);

    }


    public String getTrackerID(String label) {
        String trackerID = null;
        String selectQuery = "SELECT tracker_id FROM TblTrackers WHERE label = '" + label.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            trackerID = cursor.getString(cursor.getColumnIndex("tracker_id"));
        }
        cursor.close();
        db.close();
        return trackerID;
    }


    public String getLatbyTID(String tID) {
        String trackerID = null;
        String selectQuery = "SELECT lat FROM TblTrackers WHERE tracker_id = '" + tID.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            trackerID = cursor.getString(cursor.getColumnIndex("lat"));
        }
        cursor.close();
        db.close();
        return trackerID;
    }


    public String getLngbyTID(String tID) {
        String trackerID = null;
        String selectQuery = "SELECT lng FROM TblTrackers WHERE tracker_id = '" + tID.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            trackerID = cursor.getString(cursor.getColumnIndex("lng"));
        }
        cursor.close();
        db.close();
        return trackerID;
    }


    public String getTLabel(String t_lat) {
        String trackerLabel = null;
        String selectQuery = "SELECT label FROM TblTrackers WHERE lat = '" + t_lat.trim() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            trackerLabel = cursor.getString(cursor.getColumnIndex("label"));
        }
        cursor.close();
        db.close();
        return trackerLabel;
    }


    public String getTLabelbyID(String tID) {
        String trackerLabel = null;
        String selectQuery = "SELECT label FROM TblTrackers WHERE tracker_id = '" + tID.trim() + "'";
//        String selectQuery = "SELECT tracker_id FROM " + TblTrackers +" WHERE label = "+label;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            trackerLabel = cursor.getString(cursor.getColumnIndex("label"));
        }
        cursor.close();
        db.close();
        return trackerLabel;
    }


    public String getTrakerName(String tID) {
        String trackerLabel = null;
        String selectQuery = "SELECT label FROM TblTrackers WHERE tracker_id = '" + tID.trim() + "'";
//        String selectQuery = "SELECT tracker_id FROM " + TblTrackers +" WHERE label = "+label;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            trackerLabel = cursor.getString(cursor.getColumnIndex("label"));
        }
        cursor.close();
        db.close();
        return trackerLabel;
    }


    public Driver getDriverDetails(String tracker_id, String label) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TBLDriverDetails + " WHERE tracker_id = '" + String.valueOf(tracker_id).trim() + "'" + " OR first_name = '" + label.trim() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            if (cursor != null)
                cursor.moveToFirst();
//            String temp;
//            for(int i = 2; i < 28; i++){
//                temp = cursor.getString(i);
//                Log.e("TAG + " + i, temp);
//            }
            Driver driver = new Driver();
            driver.setId(cursor.getString(1));
            driver.setTracker_id(cursor.getString(2));
            driver.setFirst_name(cursor.getString(3));
            driver.setModel(cursor.getString(4));
            driver.setReg_number(cursor.getString(5));
            driver.setVin(cursor.getString(6));
            driver.setChassis_number(cursor.getString(7));
            driver.setFuel_type(cursor.getString(8));
            driver.setNorm_avg_fuel_consumption(cursor.getString(9));
            driver.setLiability_insurance_valid_till(cursor.getString(10));

            driver.setMax_speed(cursor.getString(11));
            driver.setType(cursor.getString(12));
            driver.setSubtype(cursor.getString(13));
            driver.setGarage_id(cursor.getString(14));
            driver.setPayload_height(cursor.getString(15));
            driver.setPayload_lenth(cursor.getString(16));
            driver.setPayload_weight(cursor.getString(17));
            driver.setPayload_width(cursor.getString(18));
            driver.setPassengers(cursor.getString(19));
            driver.setFuel_grade(cursor.getString(20));
            driver.setFuel_tank_volume(cursor.getString(21));
            driver.setWheel_arrangement(cursor.getString(22));
            driver.setTyre_size(cursor.getString(23));
            driver.setTyres_number(cursor.getString(24));
            driver.setLiability_insurance_policy_number(cursor.getString(25));
            driver.setFree_insurance_policy_number(cursor.getString(26));
            driver.setFree_insurance_valid_till(cursor.getString(27));


            // return course
            cursor.close();
            return driver;
        } else {
            return null;
        }
    }

    public void deletAllPlayers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TBLDriverDetails);
        db.close();
    }

    public void deletConnectionStatus() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + tbl_connectonStatus);
        db.close();

    }

    public void deletTracker() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TblTrackers);
        db.close();
    }

    public void removeExistingTime(String day, String trackerID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DELETE FROM " + TblActiveHours + " WHERE  TrackerID = '" + String.valueOf(trackerID).trim() + "'" + " AND day = '" + day.trim() + "'";
        db.execSQL(sql);
        db.close();
    }

    public void removeAllTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TblActiveHours + " WHERE  TrackerID = 143985";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.e("ee", " " + cursor.getColumnName(i));
            }
        }
        cursor.close();
        db.close();
    }

    public ArrayList<RoutineMaintinance> getCostsByID(String tracker_id) {
        String selectQuery = "SELECT * FROM " + TblMaintenance + " WHERE TrackerID = '" + String.valueOf(tracker_id).trim() + "'";
        ArrayList<RoutineMaintinance> routineMaintinances = new ArrayList<RoutineMaintinance>();
        RoutineMaintinance temp;
        Cursor c = this.getReadableDatabase().rawQuery(selectQuery, null);
        while (c.moveToNext()) {
            temp = new RoutineMaintinance();
            String taskid = c.getString(c.getColumnIndex(taskID));
            String taskcost = c.getString(c.getColumnIndex(taskCost));
            String endtime = c.getString(c.getColumnIndex(eTime));
            temp.setId(taskid);
            temp.setCost(taskcost);
            temp.setEnd_date(endtime);
            routineMaintinances.add(temp);
        }
        return routineMaintinances;
    }

    public boolean isExistTask(String taskid) {
        String sql = "SELECT AHID FROM " + TblMaintenance + " WHERE task_id = '" + String.valueOf(taskid).trim() + "'";
        boolean bool = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            bool = true;
        }
        cursor.close();
        db.close();
        return bool;
    }

    public void removeTask(String taskid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DELETE FROM " + TblMaintenance + " WHERE task_id = '" + String.valueOf(taskid).trim() + "'";
        db.execSQL(sql);
        db.close();
    }

    public boolean insertTask(RoutineMaintinance data, String trackerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(taskID, data.getId());
        values.put(taskCost, data.getCost());
        values.put(eTime, data.getEnd_date());
        values.put(TrackerID, trackerID);
        long aa = db.insert(TblMaintenance, null, values);
        db.close();
        if (aa > 0) {
            return true;
        }
        return false;
    }
}