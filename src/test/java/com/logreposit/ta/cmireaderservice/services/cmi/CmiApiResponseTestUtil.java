package com.logreposit.ta.cmireaderservice.services.cmi;

import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiIO;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiInput;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiLoggingDigital;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiOutput;

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
            softly.assertThat(data.getAnalogLoggingValues()).hasSize(13);
            softly.assertThat(data.getDigitalLoggingValues()).hasSize(6);
            softly.assertThat(data.getDlBusValues()).hasSize(0);
        });

        for (CmiApiInput cmiApiInput : cmiApiResponse.getData().getInputs())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiInput);

            if ("46".equals(cmiApiInput.getValue().getUnit()))
            {
                assertThat(cmiApiInput.getValue().getRas()).isNotNull();
            }

            assertThat(cmiApiInput.getValue().getState()).isNull();
        }

        for (CmiApiOutput cmiApiOutput : cmiApiResponse.getData().getOutputs())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiOutput);

            if ("D".equals(cmiApiOutput.getAd()))
            {
                assertThat(cmiApiOutput.getValue().getState()).isNotNull();
            }

            assertThat(cmiApiOutput.getValue().getRas()).isNull();
        }

        for (CmiApiIO cmiApiLoggingAnalog : cmiApiResponse.getData().getAnalogLoggingValues())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiLoggingAnalog);
        }

        for (CmiApiLoggingDigital cmiApiLoggingDigital : cmiApiResponse.getData().getDigitalLoggingValues())
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
