package com.logreposit.ta.cmireaderservice.services.logreposit;

import com.logreposit.ta.cmireaderservice.configuration.ApplicationConfiguration;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiIO;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiOutput;
import com.logreposit.ta.cmireaderservice.dtos.common.RasState;
import com.logreposit.ta.cmireaderservice.dtos.common.SignalType;
import com.logreposit.ta.cmireaderservice.dtos.common.Unit;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.Field;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.FloatField;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.IngressData;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.IntegerField;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.Reading;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.StringField;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.data.Tag;
import com.logreposit.ta.cmireaderservice.utils.TimeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class LogrepositIngressDataMapper
{
    private final ApplicationConfiguration applicationConfiguration;

    public LogrepositIngressDataMapper(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public IngressData toLogrepositIngressData(CmiApiResponse cmiApiResponse)
    {
        final var correctedLogDate = this.getCorrectedDateInstanceForCmiTimestamp(cmiApiResponse.getHeader().getTimestamp());
        final var data = cmiApiResponse.getData();

        final var inputReadings = toLogrepositReading(data.getInputs(), correctedLogDate, "input");
        final var outputReadings = toLogrepositReading(data.getOutputs(), correctedLogDate, "output");
        final var dlBusReadings = toLogrepositReading(data.getDlBus(), correctedLogDate, "dl_bus");
        final var analogNetworkReadings = toLogrepositReading(data.getAnalogNetworkValues(), correctedLogDate, "analog_network");
        final var digitalNetworkReadings = toLogrepositReading(data.getDigitalNetworkValues(), correctedLogDate, "digital_network");
        final var analogLoggingReadings = toLogrepositReading(data.getLoggingAnalog(), correctedLogDate, "analog_logging");
        final var digitalLoggingReadings = toLogrepositReading(data.getLoggingDigital(), correctedLogDate, "digital_logging");

        final var readings = Stream.of(inputReadings,
                                       outputReadings,
                                       dlBusReadings,
                                       analogLoggingReadings,
                                       digitalLoggingReadings,
                                       analogNetworkReadings,
                                       digitalNetworkReadings)
                                   .flatMap(Collection::stream).toList();

        return new IngressData(readings);
    }

    private List<Reading> toLogrepositReading(List<? extends CmiApiIO> cmiApiIos, Instant date, String measurementName) {
        return Optional.ofNullable(cmiApiIos).orElse(List.of()).stream()
                .map(r -> convert(r, date, measurementName))
                .toList();
    }

    private Instant getCorrectedDateInstanceForCmiTimestamp(long epochSeconds)
    {
        final var deviceTimezone = this.applicationConfiguration.getDeviceTimezone();
        final var deviceZoneId = ZoneId.of(deviceTimezone);
        final var correctDate = TimeUtils.getCorrectDateInstanceForCmiTimestamp(epochSeconds, deviceZoneId);

        return correctDate.toInstant();
    }

    private Reading convert(CmiApiIO cmiApiIO, Instant date, String measurementName) {
        return new Reading(date, measurementName, getTags(cmiApiIO), getFields(cmiApiIO));
    }

    private List<Tag> getTags(CmiApiIO io) {
        return Stream.of(
                getNumberTag(io.getNumber()),
                getSignalTag(io.getAd()),
                getUnitTag(io.getValue().getUnit()))
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .toList();
    }

    private Optional<Tag> getNumberTag(Integer number) {
        return Optional.ofNullable(number)
                       .map(n -> new Tag("number", n.toString()));
    }

    private Optional<Tag> getSignalTag(String ad) {
        return Optional.ofNullable(ad)
                       .map(SignalType::of)
                       .map(s -> s.toString().toLowerCase(Locale.US))
                       .map(s -> new Tag("signal", s));
    }

    private Optional<Tag> getUnitTag(String unit) {
        return Optional.ofNullable(unit)
                       .map(Unit::of)
                       .map(u -> u.toString().toLowerCase(Locale.US))
                       .map(u -> new Tag("unit", u));
    }

    private List<Field<?>> getFields(CmiApiIO io) {
        final var value = io.getValue();

        if (io instanceof CmiApiOutput && "D".equals(io.getAd()) && value.getValue() != null) {
            return List.of(new IntegerField("state", value.getValue().longValue()));
        }

        final var fields = new ArrayList<Field<?>>();

        Optional.ofNullable(value.getValue())
                .map(v -> new FloatField("value", v))
                .ifPresent(fields::add);

        Optional.ofNullable(value.getRas())
                .map(r -> new StringField("ras_state", RasState.of(r).toString().toLowerCase(Locale.US)))
                .ifPresent(fields::add);

        Optional.ofNullable(value.getState())
                .map(s -> new IntegerField("state", s.longValue()))
                .ifPresent(fields::add);

        return fields;
    }
}
