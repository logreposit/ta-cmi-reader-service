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
        final var value = cmiApiInput.getValue();

        RasState   rasState   = convertRasState(value.getRas());
        Unit       unit       = convertUnit(value.getUnit());
        SignalType signalType = convertSignalType(cmiApiInput.getAd());

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
        final var value = cmiApiOutput.getValue();

        SignalType signalType = convertSignalType(cmiApiOutput.getAd());
        Unit       unit       = convertUnit(value.getUnit());

        Output output = new Output();

        output.setNumber(cmiApiOutput.getNumber());
        output.setSignal(signalType);
        output.setUnit(unit);

        if (SignalType.DIGITAL.equals(signalType) && value.getValue() != null)
        {
            int state = value.getValue().intValue();

            output.setState(state);
        }
        else
        {
            output.setValue(value.getValue());
            output.setState(value.getState());
        }

        return output;
    }

    private static AnalogLoggingValue convertAnalogLoggingValue(CmiApiLoggingAnalog cmiApiLoggingAnalog)
    {
        final var value = cmiApiLoggingAnalog.getValue();

        SignalType signalType = convertSignalType(cmiApiLoggingAnalog.getAd());
        Unit       unit       = convertUnit(value.getUnit());

        AnalogLoggingValue analogLoggingValue = new AnalogLoggingValue();

        analogLoggingValue.setNumber(cmiApiLoggingAnalog.getNumber());
        analogLoggingValue.setSignal(signalType);
        analogLoggingValue.setUnit(unit);
        analogLoggingValue.setValue(value.getValue());

        return analogLoggingValue;
    }

    private static DigitalLoggingValue convertDigitalLoggingValue(CmiApiLoggingDigital cmiApiLoggingDigital)
    {
        final var value = cmiApiLoggingDigital.getValue();

        SignalType signalType = convertSignalType(cmiApiLoggingDigital.getAd());
        Unit       unit       = convertUnit(value.getUnit());

        DigitalLoggingValue digitalLoggingValue = new DigitalLoggingValue();

        digitalLoggingValue.setNumber(cmiApiLoggingDigital.getNumber());
        digitalLoggingValue.setSignal(signalType);
        digitalLoggingValue.setUnit(unit);
        digitalLoggingValue.setValue(value.getValue());

        return digitalLoggingValue;
    }

    private static SignalType convertSignalType(String taSignalType)
    {
        return switch (taSignalType)
                {
                    case "A" -> SignalType.ANALOG;
                    case "D" -> SignalType.DIGITAL;
                    default -> SignalType.UNKNOWN;
                };
    }

    private static RasState convertRasState(String taRasState)
    {
        if (StringUtils.isEmpty(taRasState)) {
            return null;
        }

        return switch (taRasState)
                {
                    case "0" -> RasState.TIME_AUTO;
                    case "1" -> RasState.STANDARD;
                    case "2" -> RasState.SETBACK;
                    case "3" -> RasState.STANDBY_FROST_PROTECTION;
                    default -> RasState.UNKNOWN;
                };
    }

    private static Unit convertUnit(String taUnit)
    {
        return switch (taUnit)
                {
                    case "0" -> Unit.UNKNOWN;
                    case "1" -> Unit.DEGREES_CELSIUS;
                    case "2" -> Unit.WATTS_PER_SQUARE_METER;
                    case "3" -> Unit.LITERS_PER_HOUR;
                    case "4" -> Unit.SECONDS;
                    case "5" -> Unit.MINUTES;
                    case "6" -> Unit.LITERS_PER_IMPULSE;
                    case "7" -> Unit.KELVIN;
                    case "8" -> Unit.PERCENT;
                    case "10" -> Unit.KILOWATTS;
                    case "11" -> Unit.KILOWATT_HOURS;
                    case "12" -> Unit.MEGAWATT_HOURS;
                    case "13" -> Unit.VOLTS;
                    case "14" -> Unit.MILLIAMPERES;
                    case "15" -> Unit.HOURS;
                    case "16" -> Unit.DAYS;
                    case "17" -> Unit.IMPULSES;
                    case "18" -> Unit.KILO_OHMS;
                    case "19" -> Unit.LITERS;
                    case "20" -> Unit.KILOMETERS_PER_HOUR;
                    case "21" -> Unit.HERTZ;
                    case "22" -> Unit.LITERS_PER_MINUTE;
                    case "23" -> Unit.BAR;
                    case "24" -> Unit.UNKNOWN;
                    case "25" -> Unit.KILOMETERS;
                    case "26" -> Unit.METERS;
                    case "27" -> Unit.MILLIMETERS;
                    case "28" -> Unit.CUBIC_METERS;
                    case "35" -> Unit.LITERS_PER_DAY;
                    case "36" -> Unit.METERS_PER_SECOND;
                    case "37" -> Unit.CUBIC_METERS_PER_MINUTE;
                    case "38" -> Unit.CUBIC_METERS_PER_HOUR;
                    case "39" -> Unit.CUBIC_METERS_PER_DAY;
                    case "40" -> Unit.MILLIMETERS_PER_MINUTE;
                    case "41" -> Unit.MILLIMETERS_PER_HOUR;
                    case "42" -> Unit.MILLIMETERS_PER_DAY;
                    case "43" -> Unit.ON_OFF;
                    case "44" -> Unit.YES_NO;
                    case "46" -> Unit.DEGREES_CELSIUS;
                    case "48" -> Unit.HEATING_CIRCUIT_CONTROL_OPERATING_MODE;
                    case "50" -> Unit.EUR;
                    case "51" -> Unit.USD;
                    case "52" -> Unit.GRAMS_PER_CUBIC_METER;
                    case "53" -> Unit.UNKNOWN;
                    case "54" -> Unit.DEGREES;
                    case "56" -> Unit.DEGREES;
                    case "57" -> Unit.SECONDS;
                    case "58" -> Unit.UNKNOWN;
                    case "59" -> Unit.PERCENT;
                    case "60" -> Unit.TIME;
                    case "63" -> Unit.AMPERES;
                    case "65" -> Unit.MILLIBAR;
                    case "66" -> Unit.PASCAL;
                    case "67" -> Unit.PARTS_PER_MILLION;
                    default -> Unit.UNKNOWN;
                };
    }

    private static DeviceType convertDeviceType(String taDeviceType)
    {
        return switch (taDeviceType)
                {
                    case "7F" -> DeviceType.COE;
                    case "80" -> DeviceType.UVR1611;
                    case "81" -> DeviceType.CAN_MT;
                    case "82" -> DeviceType.CAN_IO44;
                    case "83" -> DeviceType.CAN_IO35;
                    case "84" -> DeviceType.CAN_BC;
                    case "85" -> DeviceType.CAN_EZ;
                    case "86" -> DeviceType.CAN_TOUCH;
                    case "87" -> DeviceType.UVR16X2;
                    case "88" -> DeviceType.RSM610;
                    case "89" -> DeviceType.CAN_IO45;
                    case "8A" -> DeviceType.CMI;
                    case "8B" -> DeviceType.CAN_EZ2;
                    case "8C" -> DeviceType.CAN_MTX2;
                    case "8D" -> DeviceType.CAN_BC2;
                    case "A3" -> DeviceType.BL_NET;
                    default -> DeviceType.UNKNOWN;
                };
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
