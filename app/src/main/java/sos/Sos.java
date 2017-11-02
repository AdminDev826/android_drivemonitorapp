package sos;

import org.json.JSONArray;

/**
 * Created by Farhan on 8/19/2016.
 */
public class Sos {

    public JSONArray getArr_Number() {
        return arr_Number;
    }

    public void setArr_Number(JSONArray arr_Number) {
        this.arr_Number = arr_Number;
    }

    public JSONArray getArr_Sms() {
        return arr_Sms;
    }

    public void setArr_Sms(JSONArray arr_Sms) {
        this.arr_Sms = arr_Sms;
    }

    public JSONArray getArr_Email() {
        return arr_Email;
    }

    public void setArr_Email(JSONArray arr_Email) {
        this.arr_Email = arr_Email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    JSONArray arr_Number;
    JSONArray arr_Sms;
    JSONArray arr_Email;
    String message;

}
