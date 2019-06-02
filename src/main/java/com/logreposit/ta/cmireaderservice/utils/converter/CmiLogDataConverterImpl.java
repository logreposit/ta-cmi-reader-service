package com.logreposit.ta.cmireaderservice.utils.converter;

import com.logreposit.ta.cmireaderservice.configuration.ApplicationConfiguration;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiInput;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiLoggingAnalog;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiLoggingDigital;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiOutput;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.AnalogLoggingValue;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.CmiLogData;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.DigitalLoggingValue;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.Input;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.Output;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.DeviceType;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.RasState;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.SignalType;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.Unit;
import com.logreposit.ta.cmireaderservice.utils.converter.exceptions.CmiLogDataConverterException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;

@Component
public class CmiLogDataConverterImpl implements CmiLogDataConverter
{
    private final ApplicationConfiguration applicationConfiguration;

    @Autowired
    public CmiLogDataConverterImpl(ApplicationConfiguration applicationConfiguration)
    {
        this.applicationConfiguration = applicationConfiguration;
    }

    public CmiLogData convertCmiApiResponse(CmiApiResponse cmiApiResponse) throws CmiLogDataConverterException
    {
        throwExceptionIfCmiApiResponseIsNotValid(cmiApiResponse);

        Date correctedLogDate = this.getCorrectedDateInstanceForCmiTimestamp(cmiApiResponse.getHeader().getTimestamp());
        DeviceType deviceType = convertDeviceType(cmiApiResponse.getHeader().getDevice());

        CmiLogData cmiLogData = new CmiLogData();
        cmiLogData.setDate(correctedLogDate);
        cmiLogData.setDeviceType(deviceType);

        for (CmiApiInput cmiApiInput : cmiApiResponse.getData().getInputs())
        {
            Input input = convertInput(cmiApiInput);
            cmiLogData.getInputs().add(input);
        }

        for (CmiApiOutput cmiApiOutput : cmiApiResponse.getData().getOutputs())
        {
            Output output = convertOutput(cmiApiOutput);
            cmiLogData.getOutputs().add(output);
        }

        for (CmiApiLoggingAnalog cmiApiLoggingAnalog : cmiApiResponse.getData().getAnalogLoggingValues())
        {
            AnalogLoggingValue analogLoggingValue = convertAnalogLoggingValue(cmiApiLoggingAnalog);
            cmiLogData.getAnalogLoggingValues().add(analogLoggingValue);
        }

        for (CmiApiLoggingDigital cmiApiLoggingDigital : cmiApiResponse.getData().getDigitalLoggingValues())
        {
            DigitalLoggingValue digitalLoggingValue = convertDigitalLoggingValue(cmiApiLoggingDigital);
            cmiLogData.getDigitalLoggingValues().add(digitalLoggingValue);
        }

        return cmiLogData;
    }

    private Date getCorrectedDateInstanceForCmiTimestamp(long epochSeconds)
    {
        String deviceTimezone = this.applicationConfiguration.getDeviceTimezone();
        ZoneId deviceZoneId   = ZoneId.of(deviceTimezone);
        Date   correctDate    = TimeUtils.getCorrectDateInstanceForCmiTimestamp(epochSeconds, deviceZoneId);

        return correctDate;
    }

    private static Input convertInput(CmiApiInput cmiApiInput)
    {
        String ras = cmiApiInput.getValue().getRas();

        RasState rasState = null;
        if (StringUtils.isNotEmpty(ras))
        {
            rasState = convertRasState(cmiApiInput.getValue().getRas());
        }

        SignalType signalType = convertSignalType(cmiApiInput.getAd());
        Unit       unit       = convertUnit(cmiApiInput.getValue().getUnit());

        Input input = new Input();
        input.setRasState(rasState);
        input.setNumber(cmiApiInput.getNumber());
        input.setSignal(signalType);
        input.setUnit(unit);
        input.setValue(cmiApiInput.getValue().getValue());

        return input;
    }

    private static Output convertOutput(CmiApiOutput cmiApiOutput)
    {
        SignalType signalType = convertSignalType(cmiApiOutput.getAd());
        Unit       unit       = convertUnit(cmiApiOutput.getValue().getUnit());

        Output output = new Output();
        output.setNumber(cmiApiOutput.getNumber());
        output.setSignal(signalType);
        output.setUnit(unit);

        if (SignalType.DIGITAL.equals(signalType) && cmiApiOutput.getValue().getValue() != null)
        {
            int state = cmiApiOutput.getValue().getValue().intValue();
            output.setState(state);
        }
        else
        {
            output.setValue(cmiApiOutput.getValue().getValue());
            output.setState(cmiApiOutput.getValue().getState());
        }

        return output;
    }

    private static AnalogLoggingValue convertAnalogLoggingValue(CmiApiLoggingAnalog cmiApiLoggingAnalog)
    {
        SignalType signalType = convertSignalType(cmiApiLoggingAnalog.getAd());
        Unit       unit       = convertUnit(cmiApiLoggingAnalog.getValue().getUnit());

        AnalogLoggingValue analogLoggingValue = new AnalogLoggingValue();
        analogLoggingValue.setNumber(cmiApiLoggingAnalog.getNumber());
        analogLoggingValue.setSignal(signalType);
        analogLoggingValue.setUnit(unit);
        analogLoggingValue.setValue(cmiApiLoggingAnalog.getValue().getValue());

        return analogLoggingValue;
    }

    private static DigitalLoggingValue convertDigitalLoggingValue(CmiApiLoggingDigital cmiApiLoggingDigital)
    {
        SignalType signalType = convertSignalType(cmiApiLoggingDigital.getAd());
        Unit       unit       = convertUnit(cmiApiLoggingDigital.getValue().getUnit());

        DigitalLoggingValue digitalLoggingValue = new DigitalLoggingValue();
        digitalLoggingValue.setNumber(cmiApiLoggingDigital.getNumber());
        digitalLoggingValue.setSignal(signalType);
        digitalLoggingValue.setUnit(unit);
        digitalLoggingValue.setValue(cmiApiLoggingDigital.getValue().getValue());

        return digitalLoggingValue;
    }

    private static SignalType convertSignalType(String taSignalType)
    {
        switch (taSignalType)
        {
            case "A":
                return SignalType.ANALOG;
            case "D":
                return SignalType.DIGITAL;
            default:
                return SignalType.UNKNOWN;
        }
    }

    private static RasState convertRasState(String taRasState)
    {
        switch (taRasState)
        {
            case "0":
                return RasState.TIME_AUTO;
            case "1":
                return RasState.STANDARD;
            case "2":
                return RasState.SETBACK;
            case "3":
                return RasState.STANDBY_FROST_PROTECTION;
            default:
                return RasState.UNKNOWN;
        }
    }

    private static Unit convertUnit(String taUnit)
    {
        switch (taUnit)
        {
            case "0":
                return Unit.UNKNOWN;
            case "1":
                return Unit.DEGREES_CELSIUS;
            case "2":
                return Unit.WATTS_PER_SQUARE_METER;
            case "3":
                return Unit.LITERS_PER_HOUR;
            case "4":
                return Unit.SECONDS;
            case "5":
                return Unit.MINUTES;
            case "6":
                return Unit.LITERS_PER_IMPULSE;
            case "7":
                return Unit.KELVIN;
            case "8":
                return Unit.PERCENT;
            case "10":
                return Unit.KILOWATTS;
            case "11":
                return Unit.KILOWATT_HOURS;
            case "12":
                return Unit.MEGAWATT_HOURS;
            case "13":
                return Unit.VOLTS;
            case "14":
                return Unit.MILLIAMPERES;
            case "15":
                return Unit.HOURS;
            case "16":
                return Unit.DAYS;
            case "17":
                return Unit.IMPULSES;
            case "18":
                return Unit.KILO_OHMS;
            case "19":
                return Unit.LITERS;
            case "20":
                return Unit.KILOMETERS_PER_HOUR;
            case "21":
                return Unit.HERTZ;
            case "22":
                return Unit.LITERS_PER_MINUTE;
            case "23":
                return Unit.BAR;
            case "24":
                return Unit.UNKNOWN;
            case "25":
                return Unit.KILOMETERS;
            case "26":
                return Unit.METERS;
            case "27":
                return Unit.MILLIMETERS;
            case "28":
                return Unit.CUBIC_METERS;
            case "35":
                return Unit.LITERS_PER_DAY;
            case "36":
                return Unit.METERS_PER_SECOND;
            case "37":
                return Unit.CUBIC_METERS_PER_MINUTE;
            case "38":
                return Unit.CUBIC_METERS_PER_HOUR;
            case "39":
                return Unit.CUBIC_METERS_PER_DAY;
            case "40":
                return Unit.MILLIMETERS_PER_MINUTE;
            case "41":
                return Unit.MILLIMETERS_PER_HOUR;
            case "42":
                return Unit.MILLIMETERS_PER_DAY;
            case "43":
                return Unit.ON_OFF;
            case "44":
                return Unit.YES_NO;
            case "46":
                return Unit.DEGREES_CELSIUS;
            case "48":
                return Unit.HEATING_CIRCUIT_CONTROL_OPERATING_MODE;
            case "50":
                return Unit.EUR;
            case "51":
                return Unit.USD;
            case "52":
                return Unit.GRAMS_PER_CUBIC_METER;
            case "53":
                return Unit.UNKNOWN;
            case "54":
                return Unit.DEGREES;
            case "56":
                return Unit.DEGREES;
            case "57":
                return Unit.SECONDS;
            case "58":
                return Unit.UNKNOWN;
            case "59":
                return Unit.PERCENT;
            case "60":
                return Unit.TIME;
            case "63":
                return Unit.AMPERES;
            case "65":
                return Unit.MILLIBAR;
            case "66":
                return Unit.PASCAL;
            case "67":
                return Unit.PARTS_PER_MILLION;
            default:
                return Unit.UNKNOWN;
        }
    }

    private static DeviceType convertDeviceType(String taDeviceType)
    {
        switch (taDeviceType)
        {
            case "7F":
                return DeviceType.COE;
            case "80":
                return DeviceType.UVR1611;
            case "81":
                return DeviceType.CAN_MT;
            case "82":
                return DeviceType.CAN_IO44;
            case "83":
                return DeviceType.CAN_IO35;
            case "84":
                return DeviceType.CAN_BC;
            case "85":
                return DeviceType.CAN_EZ;
            case "86":
                return DeviceType.CAN_TOUCH;
            case "87":
                return DeviceType.UVR16X2;
            case "88":
                return DeviceType.RSM610;
            case "89":
                return DeviceType.CAN_IO45;
            case "8A":
                return DeviceType.CMI;
            case "8B":
                return DeviceType.CAN_EZ2;
            case "8C":
                return DeviceType.CAN_MTX2;
            case "8D":
                return DeviceType.CAN_BC2;
            case "A3":
                return DeviceType.BL_NET;
            default:
                return DeviceType.UNKNOWN;
        }
    }

    private static void throwExceptionIfCmiApiResponseIsNotValid(CmiApiResponse cmiApiResponse) throws CmiLogDataConverterException
    {
        if (cmiApiResponse == null)
        {
            throw new CmiLogDataConverterException("cmiApiResponse == null");
        }

        if (cmiApiResponse.getStatusCode() != 0 || !"OK".equals(cmiApiResponse.getStatus()))
        {
            throw new CmiLogDataConverterException("cmiApiResponse does not contain any useful data");
        }

        if (cmiApiResponse.getData() == null || cmiApiResponse.getData().getInputs() == null || cmiApiResponse.getData().getOutputs() == null)
        {
            throw new CmiLogDataConverterException("cmiApiResponse.data is corrupt");
        }
    }
}
