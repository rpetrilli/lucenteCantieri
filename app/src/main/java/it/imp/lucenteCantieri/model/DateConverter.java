package it.imp.lucenteCantieri.model;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        if (timestamp == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, (int)(timestamp / 10000)) ;
        cal.set(Calendar.MONTH, (int)(timestamp / 100) % 100);
        cal.set(Calendar.DAY_OF_MONTH, (int)(timestamp % 100));

        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if (date == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long  dt =  cal.get(Calendar.DAY_OF_MONTH) +
                cal.get(Calendar.MONTH) * 100 +
                cal.get(Calendar.YEAR) * 10000;
        return dt;
    }
}
