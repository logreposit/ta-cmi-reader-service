package com.logreposit.ta.cmireaderservice.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class TimeUtils
{
    private TimeUtils()
    {
    }

    public static Date getCorrectDateInstanceForCmiTimestamp(long cmiEpochSeconds, ZoneId cmiZoneId)
    {
        Date date = new Date();

        LocalDateTime utcDate   = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        LocalDateTime zonedDate = LocalDateTime.ofInstant(date.toInstant(), cmiZoneId);

        long utcTimestamp        = utcDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        long targetZoneTimestamp = zonedDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        long differenceInMillis  = utcTimestamp - targetZoneTimestamp;

        long cmiEpochMillis   = cmiEpochSeconds * 1000;
        long newCorrectMillis = cmiEpochMillis + differenceInMillis;

        Date newDate = new Date(newCorrectMillis);

        return newDate;
    }
}
