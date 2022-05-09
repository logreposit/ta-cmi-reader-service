package com.logreposit.ta.cmireaderservice.services.cmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.common.DeviceType;
import com.logreposit.ta.cmireaderservice.utils.http.HttpClient;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ObjectMapper.class})
public class CmiReaderServiceImplTests
{
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HttpClient httpClient;

    private CmiReaderService cmiReaderService;

    @BeforeEach
    public void setUp()
    {
        this.cmiReaderService = new CmiReaderServiceImpl(this.objectMapper, this.httpClient);
    }

    @Test
    public void testReadUvr16x2ValuesCallsCorrectUrl() throws Exception
    {
        DeviceType deviceType = DeviceType.UVR16X2;
        this.testReadValuesCallsCorrectUrl(deviceType);
    }

    @Test
    public void testReadUvr1611ValuesCallsCorrectUrl() throws Exception
    {
        DeviceType deviceType = DeviceType.UVR1611;
        this.testReadValuesCallsCorrectUrl(deviceType);
    }

    @Test
    public void testReadUvrCANEZ2ValuesCallsCorrectUrl() throws Exception
    {
        DeviceType deviceType = DeviceType.CAN_EZ2;
        this.testReadValuesCallsCorrectUrl(deviceType);
    }

    @Test
    public void testParseCMIResponsePayload() throws Exception
    {
        String cmiResponsePayload = IOUtils.toString(CmiReaderServiceImplTests.class.getResourceAsStream("/cmi_responses/uvr16x2/2017-10-21T17:05:23+02:00.json"), StandardCharsets.UTF_8);

        HttpClientResponse httpClientResponse = new HttpClientResponse();
        httpClientResponse.setHttpStatusCode(200);
        httpClientResponse.setResponseBody(cmiResponsePayload);

        int node = 1;
        String ip = "10.0.0.123";
        DeviceType deviceType = DeviceType.UVR16X2;
        String url = getExpectedUrl(ip, node, deviceType);

        Mockito.when(this.httpClient.get(Mockito.eq(url), Mockito.any(BasicAuthCredentials.class))).thenReturn(httpClientResponse);

        CmiApiResponse cmiApiResponse = this.cmiReaderService.read(ip, "admin", "admin", 1, deviceType);

        CmiApiResponseTestUtil.checkIfCmiApiResponseIsValid(cmiApiResponse);
    }

    private void testReadValuesCallsCorrectUrl(DeviceType deviceType) throws Exception
    {
        int node = 1;
        String ip = "10.0.0.98";

        String expectedUrl = getExpectedUrl(ip, node, deviceType);

        try
        {
            this.cmiReaderService.read(ip, "admin", "admin", 1, deviceType);
        }
        catch (Exception exception)
        {
        }

        Mockito.verify(this.httpClient).get(Mockito.eq(expectedUrl), Mockito.any(BasicAuthCredentials.class));
    }

    private static String getExpectedUrl(String ip, int node, DeviceType deviceType)
    {
        String requestedValues = getRequestedValues(deviceType);
        String expectedUrl = String.format("http://%s/INCLUDE/api.cgi?jsonnode=%s&jsonparam=%s", ip, node, requestedValues);

        return expectedUrl;
    }

    private static String getRequestedValues(DeviceType deviceType)
    {
        switch (deviceType)
        {
            case UVR1611:
                return "I,O,Na,Nd";
            case UVR16X2:
                return "I,O,D,La,Ld";
            case CAN_EZ2:
                return "I,O,Sp";
            default:
                throw new IllegalArgumentException("deviceType not recognized.");
        }
    }
}
