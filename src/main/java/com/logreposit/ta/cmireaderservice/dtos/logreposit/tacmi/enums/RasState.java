package com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums;

import java.util.Arrays;

public enum RasState
{
    TIME_AUTO("0"),
    STANDARD("1"),
    SETBACK("2"),
    STANDBY_FROST_PROTECTION("3"),
    UNKNOWN("UNKNOWN");

    private final String taValue;

    RasState(String taValue) {
        this.taValue = taValue;
    }

    public static RasState of(String taValue) {
        return Arrays.stream(RasState.values()).filter(s -> s.taValue.equals(taValue)).findFirst().orElse(UNKNOWN);
    }
}
