package com.logreposit.ta.cmireaderservice.services.cmi;

import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiIO;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiInput;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiLoggingDigital;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiOutput;
import org.junit.Assert;

public class CmiApiResponseTestUtil
{
    private CmiApiResponseTestUtil()
    {
    }

    public static void checkIfCmiApiResponseIsValid(CmiApiResponse cmiApiResponse)
    {
        Assert.assertNotNull(cmiApiResponse);
        Assert.assertNotNull(cmiApiResponse.getStatus());
        Assert.assertNotNull(cmiApiResponse.getStatusCode());
        Assert.assertNotNull(cmiApiResponse.getHeader());
        Assert.assertNotNull(cmiApiResponse.getData());
        Assert.assertNotNull(cmiApiResponse.getData().getInputs());
        Assert.assertNotNull(cmiApiResponse.getData().getOutputs());
        Assert.assertNotNull(cmiApiResponse.getData().getAnalogLoggingValues());
        Assert.assertNotNull(cmiApiResponse.getData().getDigitalLoggingValues());
        Assert.assertNotNull(cmiApiResponse.getData().getDlBusValues());

        Assert.assertEquals("OK", cmiApiResponse.getStatus());
        Assert.assertEquals(0, cmiApiResponse.getStatusCode(), 0);
        Assert.assertEquals(0, cmiApiResponse.getData().getDlBusValues().size());
        Assert.assertEquals(16, cmiApiResponse.getData().getInputs().size());
        Assert.assertEquals(10, cmiApiResponse.getData().getOutputs().size());
        Assert.assertEquals(13, cmiApiResponse.getData().getAnalogLoggingValues().size());
        Assert.assertEquals(6, cmiApiResponse.getData().getDigitalLoggingValues().size());

        for (CmiApiInput cmiApiInput : cmiApiResponse.getData().getInputs())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiInput);

            if ("46".equals(cmiApiInput.getValue().getUnit()))
            {
                Assert.assertNotNull(cmiApiInput.getValue().getRas());
            }

            Assert.assertNull(cmiApiInput.getValue().getState());
        }

        for (CmiApiOutput cmiApiOutput : cmiApiResponse.getData().getOutputs())
        {
            checkIfCmiLoggingValuesArePresent(cmiApiOutput);

            if ("D".equals(cmiApiOutput.getAd()))
            {
                Assert.assertNull(cmiApiOutput.getValue().getState());
            }

            Assert.assertNull(cmiApiOutput.getValue().getRas());
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
        Assert.assertNotNull(cmiApiIO);
        Assert.assertNotNull(cmiApiIO.getNumber());
        Assert.assertNotNull(cmiApiIO.getAd());
        Assert.assertNotNull(cmiApiIO.getValue());
        Assert.assertNotNull(cmiApiIO.getValue().getUnit());
        Assert.assertNotNull(cmiApiIO.getValue().getValue());
    }
}
