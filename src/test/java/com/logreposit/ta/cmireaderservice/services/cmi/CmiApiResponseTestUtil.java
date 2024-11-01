package com.logreposit.ta.cmireaderservice.services.cmi;

import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiIO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class CmiApiResponseTestUtil
{
    private CmiApiResponseTestUtil()
    {
    }

    public static void checkIfCmiApiResponseIsValid(CmiApiResponse cmiApiResponse)
    {
        assertSoftly(softly -> {
            softly.assertThat(cmiApiResponse.getStatus()).isEqualTo("OK");
            softly.assertThat(cmiApiResponse.getStatusCode()).isEqualTo(0);
            softly.assertThat(cmiApiResponse.getHeader()).isNotNull();

            var data = cmiApiResponse.getData();

            softly.assertThat(data.getInputs()).hasSize(16);
            softly.assertThat(data.getOutputs()).hasSize(10);
            softly.assertThat(data.getLoggingAnalog()).hasSize(13);
            softly.assertThat(data.getLoggingDigital()).hasSize(6);
            softly.assertThat(data.getDlBus()).hasSize(0);
        });

        for (final var cmiApiInput : cmiApiResponse.getData().getInputs())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiInput);

            if ("46".equals(cmiApiInput.getValue().getUnit()))
            {
                assertThat(cmiApiInput.getValue().getRas()).isNotNull();
            }

            assertThat(cmiApiInput.getValue().getState()).isNull();
        }

        for (final var cmiApiOutput : cmiApiResponse.getData().getOutputs())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiOutput);

            if ("D".equals(cmiApiOutput.getAd()))
            {
                assertThat(cmiApiOutput.getValue().getState()).isNull();
            }

            assertThat(cmiApiOutput.getValue().getRas()).isNull();
        }

        for (final var cmiApiLoggingAnalog : cmiApiResponse.getData().getLoggingAnalog())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiLoggingAnalog);
        }

        for (final var cmiApiLoggingDigital : cmiApiResponse.getData().getLoggingDigital())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiLoggingDigital);
        }
    }

    private static void checkIfCmiLoggingValuesArePresent(CmiApiIO cmiApiIO)
    {
        assertSoftly(softly -> {
            softly.assertThat(cmiApiIO.getNumber()).isNotNull();
            softly.assertThat(cmiApiIO.getAd()).isNotNull();
            softly.assertThat(cmiApiIO.getValue().getUnit()).isNotNull();
            softly.assertThat(cmiApiIO.getValue().getValue()).isNotNull();
        });
    }
}
