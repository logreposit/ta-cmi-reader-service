package com.logreposit.ta.cmireaderservice.services.logreposit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.configuration.ApplicationConfiguration;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.services.logreposit.dtos.ingress.DataType;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class LogrepositIngressDataMapperTests
{
    private static final String UVR16X2_SAMPLE_JSON_FILEPATH = "/cmi_responses/uvr16x2/2017-10-21T17:05:23+02:00.json";
    private static final String UVR1611_SAMPLE_JSON_FILEPATH = "/cmi_responses/uvr1611/2022-05-06T17:23:45Z.json";

    private ObjectMapper objectMapper;
    private LogrepositIngressDataMapper logrepositIngressDataMapper;

    @BeforeEach
    public void setUp() {
        final var applicationConfiguration = new ApplicationConfiguration();

        applicationConfiguration.setDeviceTimezone("Europe/Vienna");

        logrepositIngressDataMapper = new LogrepositIngressDataMapper(applicationConfiguration);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testConvertUvr16x2OutputFrom2017ToLogrepositIngressData() throws JsonProcessingException
    {
        final var cmiReponse = getSampleCmiApiResponse(UVR16X2_SAMPLE_JSON_FILEPATH);
        final var logrepositIngressData = logrepositIngressDataMapper.toLogrepositIngressData(cmiReponse);
        final var readings = logrepositIngressData.readings();

        assertThat(readings).hasSize(45);

        assertSoftly(softly -> {
            softly.assertThat(readings.get(0).date()).isEqualTo("2017-10-21T15:05:24Z");

            final var input01 = readings.get(0);

            softly.assertThat(input01.measurement()).isEqualTo("input");
            softly.assertThat(input01.tags()).hasSize(3);
            softly.assertThat(input01.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(input01.tags().get(0).value()).isEqualTo("1");
            softly.assertThat(input01.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(input01.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(input01.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(input01.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(input01.fields()).hasSize(1);
            softly.assertThat(input01.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(input01.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(input01.fields().get(0).getValue()).isEqualTo(26.1);

            final var input09 = readings.get(8);

            softly.assertThat(input09.measurement()).isEqualTo("input");
            softly.assertThat(input09.tags()).hasSize(3);
            softly.assertThat(input09.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(input09.tags().get(0).value()).isEqualTo("9");
            softly.assertThat(input09.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(input09.tags().get(1).value()).isEqualTo("digital");
            softly.assertThat(input09.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(input09.tags().get(2).value()).isEqualTo("on_off");
            softly.assertThat(input09.fields()).hasSize(1);
            softly.assertThat(input09.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(input09.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(input09.fields().get(0).getValue()).isEqualTo(0d);

            final var input15 = readings.get(14);

            softly.assertThat(input15.measurement()).isEqualTo("input");
            softly.assertThat(input15.tags()).hasSize(3);
            softly.assertThat(input15.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(input15.tags().get(0).value()).isEqualTo("15");
            softly.assertThat(input15.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(input15.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(input15.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(input15.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(input15.fields()).hasSize(2);
            softly.assertThat(input15.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(input15.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(input15.fields().get(0).getValue()).isEqualTo(14.8);
            softly.assertThat(input15.fields().get(1).getName()).isEqualTo("ras_state");
            softly.assertThat(input15.fields().get(1).getDatatype()).isEqualTo(DataType.STRING);
            softly.assertThat(input15.fields().get(1).getValue()).isEqualTo("standby_frost_protection");

            final var input16 = readings.get(15);

            softly.assertThat(input16.measurement()).isEqualTo("input");
            softly.assertThat(input16.tags()).hasSize(3);
            softly.assertThat(input16.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(input16.tags().get(0).value()).isEqualTo("16");
            softly.assertThat(input16.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(input16.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(input16.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(input16.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(input16.fields()).hasSize(2);
            softly.assertThat(input16.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(input16.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(input16.fields().get(0).getValue()).isEqualTo(19.9);
            softly.assertThat(input16.fields().get(1).getName()).isEqualTo("ras_state");
            softly.assertThat(input16.fields().get(1).getDatatype()).isEqualTo(DataType.STRING);
            softly.assertThat(input16.fields().get(1).getValue()).isEqualTo("standard");

            final var output01 = readings.get(16);

            softly.assertThat(output01.measurement()).isEqualTo("output");
            softly.assertThat(output01.tags()).hasSize(3);
            softly.assertThat(output01.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(output01.tags().get(0).value()).isEqualTo("1");
            softly.assertThat(output01.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(output01.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(output01.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(output01.tags().get(2).value()).isEqualTo("unknown");
            softly.assertThat(output01.fields()).hasSize(2);
            softly.assertThat(output01.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(output01.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(output01.fields().get(0).getValue()).isEqualTo(22.0);
            softly.assertThat(output01.fields().get(1).getName()).isEqualTo("state");
            softly.assertThat(output01.fields().get(1).getDatatype()).isEqualTo(DataType.INTEGER);
            softly.assertThat(output01.fields().get(1).getValue()).isEqualTo(1L);

            final var output04 = readings.get(19);

            softly.assertThat(output04.measurement()).isEqualTo("output");
            softly.assertThat(output04.tags()).hasSize(3);
            softly.assertThat(output04.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(output04.tags().get(0).value()).isEqualTo("4");
            softly.assertThat(output04.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(output04.tags().get(1).value()).isEqualTo("digital");
            softly.assertThat(output04.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(output04.tags().get(2).value()).isEqualTo("on_off");
            softly.assertThat(output04.fields()).hasSize(1);
            softly.assertThat(output04.fields().get(0).getName()).isEqualTo("state");
            softly.assertThat(output04.fields().get(0).getDatatype()).isEqualTo(DataType.INTEGER);
            softly.assertThat(output04.fields().get(0).getValue()).isEqualTo(1L);

            final var loggingAnalog03 = readings.get(28);

            softly.assertThat(loggingAnalog03.measurement()).isEqualTo("analog_logging");
            softly.assertThat(loggingAnalog03.tags()).hasSize(3);
            softly.assertThat(loggingAnalog03.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(loggingAnalog03.tags().get(0).value()).isEqualTo("3");
            softly.assertThat(loggingAnalog03.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(loggingAnalog03.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(loggingAnalog03.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(loggingAnalog03.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(loggingAnalog03.fields()).hasSize(1);
            softly.assertThat(loggingAnalog03.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(loggingAnalog03.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(loggingAnalog03.fields().get(0).getValue()).isEqualTo(57.7);

            final var loggingDigital02 = readings.get(40);

            softly.assertThat(loggingDigital02.measurement()).isEqualTo("digital_logging");
            softly.assertThat(loggingDigital02.tags()).hasSize(3);
            softly.assertThat(loggingDigital02.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(loggingDigital02.tags().get(0).value()).isEqualTo("2");
            softly.assertThat(loggingDigital02.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(loggingDigital02.tags().get(1).value()).isEqualTo("digital");
            softly.assertThat(loggingDigital02.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(loggingDigital02.tags().get(2).value()).isEqualTo("on_off");
            softly.assertThat(loggingDigital02.fields()).hasSize(1);
            softly.assertThat(loggingDigital02.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(loggingDigital02.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(loggingDigital02.fields().get(0).getValue()).isEqualTo(1d);
        });
    }

    @Test
    public void testConvertUvr1611OutputToLogrepositIngressData() throws JsonProcessingException
    {
        final var cmiReponse = getSampleCmiApiResponse(UVR1611_SAMPLE_JSON_FILEPATH);
        final var logrepositIngressData = logrepositIngressDataMapper.toLogrepositIngressData(cmiReponse);
        final var readings = logrepositIngressData.readings();

        assertThat(readings).hasSize(61);

        assertSoftly(softly -> {
            softly.assertThat(readings.get(0).date()).isEqualTo("2022-05-06T17:23:45Z");

            final var input01 = readings.get(0);

            softly.assertThat(input01.measurement()).isEqualTo("input");
            softly.assertThat(input01.tags()).hasSize(3);
            softly.assertThat(input01.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(input01.tags().get(0).value()).isEqualTo("1");
            softly.assertThat(input01.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(input01.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(input01.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(input01.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(input01.fields()).hasSize(1);
            softly.assertThat(input01.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(input01.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(input01.fields().get(0).getValue()).isEqualTo(21.3);

            final var output01 = readings.get(16);

            softly.assertThat(output01.measurement()).isEqualTo("output");
            softly.assertThat(output01.tags()).hasSize(3);
            softly.assertThat(output01.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(output01.tags().get(0).value()).isEqualTo("1");
            softly.assertThat(output01.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(output01.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(output01.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(output01.tags().get(2).value()).isEqualTo("unknown");
            softly.assertThat(output01.fields()).hasSize(2);
            softly.assertThat(output01.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(output01.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(output01.fields().get(0).getValue()).isEqualTo(30d);
            softly.assertThat(output01.fields().get(1).getName()).isEqualTo("state");
            softly.assertThat(output01.fields().get(1).getDatatype()).isEqualTo(DataType.INTEGER);
            softly.assertThat(output01.fields().get(1).getValue()).isEqualTo(1L);

            final var output03 = readings.get(18);

            softly.assertThat(output03.measurement()).isEqualTo("output");
            softly.assertThat(output03.tags()).hasSize(3);
            softly.assertThat(output03.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(output03.tags().get(0).value()).isEqualTo("3");
            softly.assertThat(output03.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(output03.tags().get(1).value()).isEqualTo("digital");
            softly.assertThat(output03.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(output03.tags().get(2).value()).isEqualTo("unknown");
            softly.assertThat(output03.fields()).hasSize(1);
            softly.assertThat(output03.fields().get(0).getName()).isEqualTo("state");
            softly.assertThat(output03.fields().get(0).getDatatype()).isEqualTo(DataType.INTEGER);
            softly.assertThat(output03.fields().get(0).getValue()).isEqualTo(0L);

            final var networkAnalog01 = readings.get(29);

            softly.assertThat(networkAnalog01.measurement()).isEqualTo("analog_network");
            softly.assertThat(networkAnalog01.tags()).hasSize(3);
            softly.assertThat(networkAnalog01.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(networkAnalog01.tags().get(0).value()).isEqualTo("1");
            softly.assertThat(networkAnalog01.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(networkAnalog01.tags().get(1).value()).isEqualTo("analog");
            softly.assertThat(networkAnalog01.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(networkAnalog01.tags().get(2).value()).isEqualTo("degrees_celsius");
            softly.assertThat(networkAnalog01.fields()).hasSize(1);
            softly.assertThat(networkAnalog01.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(networkAnalog01.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(networkAnalog01.fields().get(0).getValue()).isEqualTo(9.9);

            final var networkDigital01 = readings.get(45);

            softly.assertThat(networkDigital01.measurement()).isEqualTo("digital_network");
            softly.assertThat(networkDigital01.tags()).hasSize(3);
            softly.assertThat(networkDigital01.tags().get(0).name()).isEqualTo("number");
            softly.assertThat(networkDigital01.tags().get(0).value()).isEqualTo("1");
            softly.assertThat(networkDigital01.tags().get(1).name()).isEqualTo("signal");
            softly.assertThat(networkDigital01.tags().get(1).value()).isEqualTo("digital");
            softly.assertThat(networkDigital01.tags().get(2).name()).isEqualTo("unit");
            softly.assertThat(networkDigital01.tags().get(2).value()).isEqualTo("on_off");
            softly.assertThat(networkDigital01.fields()).hasSize(1);
            softly.assertThat(networkDigital01.fields().get(0).getName()).isEqualTo("value");
            softly.assertThat(networkDigital01.fields().get(0).getDatatype()).isEqualTo(DataType.FLOAT);
            softly.assertThat(networkDigital01.fields().get(0).getValue()).isEqualTo(0d);
        });
    }

    private CmiApiResponse getSampleCmiApiResponse(String resourceFilePath) throws JsonProcessingException {
        final var json = getResourceFile(resourceFilePath);

        return objectMapper.readValue(json, CmiApiResponse.class);
    }

    @SneakyThrows
    private String getResourceFile(String filePath)
    {
        final var clazz = Objects.requireNonNull(getClass());
        final var resourceStream = clazz.getResourceAsStream(filePath);

        if (resourceStream == null) {
            throw new RuntimeException(String.format("Unable to load resource file %s", filePath));
        }

        return IOUtils.toString(resourceStream, StandardCharsets.UTF_8);
    }
}
