package com.logreposit.ta.cmireaderservice.services.cmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.common.DeviceType;
import com.logreposit.ta.cmireaderservice.services.cmi.exceptions.CmiReaderServiceException;
import com.logreposit.ta.cmireaderservice.utils.http.HttpClient;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import com.logreposit.ta.cmireaderservice.utils.logging.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CmiReaderServiceImpl implements CmiReaderService
{
    private static final Logger logger = LoggerFactory.getLogger(CmiReaderServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final HttpClient   httpClient;

    @Autowired
    public CmiReaderServiceImpl(ObjectMapper objectMapper, HttpClient httpClient)
    {
        this.objectMapper = objectMapper;
        this.httpClient   = httpClient;
    }

    @Override
    public CmiApiResponse read(String address, String username, String password, int node, DeviceType deviceType) throws CmiReaderServiceException
    {
        BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(username, password);
        String               selector             = buildSelector(deviceType);
        String               url                  = buildCmiUrl(address, node, selector);

        try
        {
            HttpClientResponse httpClientResponse = this.httpClient.get(url, basicAuthCredentials);

            checkStatusOrOtherwiseThrowException(httpClientResponse);

            CmiApiResponse cmiApiResponse = this.parseCmiApiResponse(httpClientResponse.getResponseBody());

            checkStatusOrOtherwiseThrowException(cmiApiResponse);

            return cmiApiResponse;
        }
        catch (HttpClientException e)
        {
            logger.error("Caught HttpClientException while retrieving information from CMI [{}]", url, e);
            throw new CmiReaderServiceException("Caught HttpClientException while retrieving information from CMI", e);
        }
    }

    private CmiApiResponse parseCmiApiResponse(String response) throws CmiReaderServiceException
    {
        try
        {
            CmiApiResponse cmiApiResponse = this.objectMapper.readValue(response, CmiApiResponse.class);

            logger.info("Successfully parsed cmiApiResponse: {}", LoggingUtils.serialize(cmiApiResponse));

            return cmiApiResponse;
        }
        catch (IOException e)
        {
            logger.error("Caught IOException while parsing Response to CmiApiResponse instance", e);
            throw new CmiReaderServiceException("Unable to parse response to CmiApiResponse instance", e);
        }
    }

    private static String buildSelector(DeviceType deviceType)
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
                return "I,O";
        }
    }

    private static String buildCmiUrl(String address, int node, String selector)
    {
        // http://<IP>/INCLUDE/api.cgi?jsonnode=<JSON_NODE>&jsonparam=I,O

        String url = String.format("http://%s/INCLUDE/api.cgi?jsonnode=%d&jsonparam=%s", address, node, selector);

        logger.info("Built CMD Url: {}", url);

        return url;
    }

    private static void checkStatusOrOtherwiseThrowException(HttpClientResponse httpClientResponse) throws CmiReaderServiceException
    {
        if (httpClientResponse.getHttpStatusCode() < 200 || httpClientResponse.getHttpStatusCode() > 299)
        {
            logger.error("Unable to get Response from CMI: Got Response with HTTP Status Code {} and Body {}",
                    httpClientResponse.getHttpStatusCode(), httpClientResponse.getResponseBody());

            throw new CmiReaderServiceException(String.format("Unable to get Response from CMI: Got Response with HTTP Status Code %d",
                    httpClientResponse.getHttpStatusCode()));
        }
    }

    private static void checkStatusOrOtherwiseThrowException(CmiApiResponse cmiApiResponse) throws CmiReaderServiceException
    {
        if (cmiApiResponse.getStatusCode() != 0)
        {
            logger.error("Got response with Status Code != 0 ({}). Error: {}", cmiApiResponse.getStatusCode(), cmiApiResponse.getStatus());
            throw new CmiReaderServiceException("Unable to retrieve Data from CMI: Got CMI Error");
        }
    }
}
