package com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums;

import java.util.Arrays;

public enum SignalType
{
    ANALOG("A"),
    DIGITAL("D"),
    UNKNOWN("UNKNOWN");

    private final String taValue;

    SignalType(String taValue) {
        this.taValue = taValue;
    }

    public static SignalType of(String taValue) {
        return Arrays.stream(SignalType.values()).filter(s -> s.taValue.equals(taValue)).findFirst().orElse(UNKNOWN);
    }
}
