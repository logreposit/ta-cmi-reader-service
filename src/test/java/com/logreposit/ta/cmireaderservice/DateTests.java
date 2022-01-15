package com.logreposit.ta.cmireaderservice;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTests
{
    @Test
    public void testDateConversion()
    {
        // 1508618047       20:34 Europe/Vienna
        long uvrTime = 1508618047;

        uvrTime *= 1000;

        Date date = new Date(uvrTime);

        LocalDateTime utcDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        LocalDateTime zonedDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Europe/Vienna"));

        long zoned1Time = utcDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        long zoned2Time = zonedDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        long difference = zoned1Time - zoned2Time;
        long newTimestamp = uvrTime + difference;

        assertThat(newTimestamp).isEqualTo(1508610847000L);
    }
}
