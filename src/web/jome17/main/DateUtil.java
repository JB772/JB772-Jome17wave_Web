package web.jome17.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
    public static String getDateTimeId(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSSS");
        String dateTimeId = String.valueOf(sdf.format(new Date()));
        return dateTimeId;
    }

    public static String date2Str (Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return String.valueOf(sdf.format(date));
    }

    public static String date2StrHm (Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.valueOf(sdf.format(date));
    }

    public static Date str2Date(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = sdf.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
