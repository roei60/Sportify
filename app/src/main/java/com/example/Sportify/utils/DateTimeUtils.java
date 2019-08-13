package com.example.Sportify.utils;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {

    public static Timestamp getTimeStamp(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        return new Timestamp(calendar.getTime());
    }

    public static Timestamp getTimestampFromLong(Long time){
        Date date = new Date(time);
        return new Timestamp(date);
    }

    public static long getLongFromTimeStamp(Timestamp timestamp){
        return timestamp.toDate().getTime();
    }
}
