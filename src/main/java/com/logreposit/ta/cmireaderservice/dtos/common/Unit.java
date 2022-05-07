package com.logreposit.ta.cmireaderservice.dtos.common;

import java.util.Arrays;

public enum Unit
{
    DEGREES("54", "56"),
    DEGREES_CELSIUS("1", "46"),
    KELVIN("7", "74"),
    PERCENT("8", "59"),
    SECONDS("4", "57"),
    MINUTES("5"),
    HOURS("15"),
    DAYS("16"),
    WATTS("69"),
    KILOWATTS("10"),
    KILOWATT_HOURS("11"),
    MEGAWATT_HOURS("12"),
    WATTS_PER_SQUARE_METER("2"),
    LITERS("19"),
    LITERS_PER_IMPULSE("6"),
    LITERS_PER_MINUTE("22"),
    LITERS_PER_HOUR("3"),
    LITERS_PER_DAY("35"),
    IMPULSES("17"),
    VOLTS("13"),
    MILLIAMPERES("14"),
    AMPERES("63"),
    KILO_OHMS("18"),
    MILLIMETERS("27"),
    MILLIMETERS_PER_MINUTE("40"),
    MILLIMETERS_PER_HOUR("41"),
    MILLIMETERS_PER_DAY("42"),
    CENTIMETER("73"),
    METERS("26"),
    METERS_PER_SECOND("36"),
    KILOMETERS("25"),
    KILOMETERS_PER_HOUR("20"),
    CUBIC_METERS("28"),
    CUBIC_METERS_PER_MINUTE("37"),
    CUBIC_METERS_PER_HOUR("38"),
    CUBIC_METERS_PER_DAY("39"),
    GRAMS_PER_CUBIC_METER("52"),
    GRAMS("72"),
    KILOGRAMS("71"),
    TONS("70"),
    HERTZ("21"),
    MILLIBAR("65"),
    BAR("23"),
    PASCAL("66"),
    PARTS_PER_MILLION("67"),
    EUR("50"),
    USD("51"),
    TIME("60"),
    ON_OFF("43"),
    YES_NO("44"),
    RAS,
    HEATING_CIRCUIT_CONTROL_OPERATING_MODE("48"),
    LUX("75"),
    UNKNOWN("0", "24", "53", "58", "68");

    private final String[] taValues;

    Unit(String... taValues) {
        this.taValues = taValues;
    }

    public static Unit of(String taValue) {
        return Arrays.stream(Unit.values()).filter(s -> Arrays.asList(s.taValues).contains(taValue)).findFirst().orElse(UNKNOWN);
    }
}
