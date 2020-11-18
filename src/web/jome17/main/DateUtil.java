package web.jome17.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    
    //TimeStamp轉字串
    public static String date2Str4Swift (Date date){
    	TimeZone gmtTZ = TimeZone.getTimeZone("GMT+8");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN);
        sdf.setTimeZone(gmtTZ);
        return sdf.format(date);
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
    
    //加2分鐘
    public static String getGroupEndTime(String assembleStr){
        Date assembleTime = str2Date(assembleStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(assembleTime);
        calendar.add(Calendar.MINUTE, 2);
        return date2StrHm (calendar.getTime());
    }
    
    //減15分鐘
    public static String getSignUpEnd(String assembleStr){
        Date assembleTime = str2Date(assembleStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(assembleTime);
        calendar.add(Calendar.MINUTE, -15);
        return date2StrHm(calendar.getTime());
    }
}
