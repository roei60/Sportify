package com.example.Sportify.room;
import androidx.room.TypeConverter;

import com.example.Sportify.utils.DateTimeUtils;
import com.google.firebase.Timestamp;

@SuppressWarnings("WeakerAccess")
public class TimestampConverters {
    @TypeConverter
    public static Timestamp fromTimestamp(Long value) {
        return value == null ? null : DateTimeUtils.getTimestampFromLong(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toDate().getTime();
    }
}