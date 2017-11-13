package util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Adam Yocum
 */
public class ExclusionTimePeriod {
    private String timeStart;
    private String timeEnd;

    /**
     * @return the timeStart
     */
    public String getTimeStart() {
        return timeStart;
    }

    /**
     * @param timeStart the timeStart to set
     */
    public void setTimeStart(String timeStart) {
        if (timeStart.matches("^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$"))
        {
            this.timeStart = timeStart;
        }
        else
        {
            throw new IllegalArgumentException(timeStart + " is not a valid time, expecting HH:MM:SS format");
        }

    }

    /**
     * @return the timeEnd
     */
    public String getTimeEnd() {
        return timeEnd;
    }

    /**
     * @param timeEnd the timeEnd to set
     */
    public void setTimeEnd(String timeEnd) {
        if (timeEnd.matches("^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$"))
        {
            this.timeEnd = timeEnd;
        }
        else
        {
            throw new IllegalArgumentException(timeEnd + " is not a valid time, expecting HH:MM:SS format");
        }
    }

    private Date toDate(String hhmmss){
        final String[] hms = hhmmss.split(":");
        final GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hms[0]));
        gc.set(Calendar.MINUTE, Integer.parseInt(hms[1]));
        gc.set(Calendar.SECOND, Integer.parseInt(hms[2]));
        gc.set(Calendar.MILLISECOND, 0);
        Date date = gc.getTime();
        return date;
    }

    public boolean isNowInPeriod()
    {
        final Date now = new Date();
        return now.after(toDate(getTimeStart())) && now.before(toDate(getTimeEnd()));
    }

    public static void main(String[] args){

        //Test All possible hours
        for(int hour=0;hour<=23;hour++){

            String hourStr = "";
            if(hour<=9){
                hourStr = "0"+hour;
            }else{
                hourStr = ""+hour;
            }

            for(int min=0;min<60;min++){
                String minStr = "";
                if(min<=9){
                    minStr = "0"+min;
                }else{
                    minStr = ""+min;
                }

                for(int sec=0;sec<60;sec++){
                    String secStr = "";
                    if(sec<=9){
                        secStr = "0"+sec;
                    }else{
                        secStr = ""+sec;
                    }

                    String hhmmss = hourStr+":"+minStr+":"+secStr;

                    ExclusionTimePeriod period = new ExclusionTimePeriod();
                    period.setTimeStart(hhmmss);
                    period.setTimeEnd(hhmmss);

                }
            }
        }

    }
}