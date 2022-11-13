package com.logreposit.ta.cmireaderservice.services.cmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.common.DeviceType;
import com.logreposit.ta.cmireaderservice.services.cmi.exceptions.CmiReaderServiceException;
import com.logreposit.ta.cmireaderservice.utils.http.HttpClient;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.AuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.SessionAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import com.logreposit.ta.cmireaderservice.utils.http.payload.FormDataPayload;
import com.logreposit.ta.cmireaderservice.utils.logging.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CmiReaderServiceImpl implements CmiReaderService
{
    private static final Logger logger = LoggerFactory.getLogger(CmiReaderServiceImpl.class);

    private static final String CMI_WEBPORTAL_PHP_SESSION_COOKIE_NAME = "PHPSESSID";

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
        if (!address.endsWith(".cmi.ta.co.at")) {
            return readLocal(address, username, password, node, deviceType);
        }

        return readFromWebPortal(address, username, password, node, deviceType);
    }

    private CmiApiResponse readLocal(String address, String username, String password, int node, DeviceType deviceType) throws CmiReaderServiceException
    {
        final var authCredentials = new BasicAuthCredentials(username, password);
        final var url = buildCmiUrl(address, node, buildSelector(deviceType));

        return readFromCmi(url, authCredentials);
    }

    private CmiApiResponse readFromWebPortal(String address, String username, String password, int node, DeviceType deviceType) throws CmiReaderServiceException {
        final var authCredentials = login(username, password);
        final var url = buildCmiUrl(address, node, buildSelector(deviceType));

        return readFromCmi(url, authCredentials);
    }

    private CmiApiResponse readFromCmi(String url, AuthCredentials authCredentials) throws CmiReaderServiceException
    {
        try
        {
            HttpClientResponse httpClientResponse = this.httpClient.get(url, authCredentials);

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

    private SessionAuthCredentials login(String username, String password) throws CmiReaderServiceException {
        try {
            final var sessionInitializationRequest = httpClient.get("https://cmi.ta.co.at/");

            checkStatusOrOtherwiseThrowException(sessionInitializationRequest);

            final var phpSessionIdCookie = httpClient.get("https://cmi.ta.co.at/")
                    .getResponseHeaders()
                    .getOrDefault("set-cookie", List.of())
                    .stream()
                    .filter(c -> c.contains(CMI_WEBPORTAL_PHP_SESSION_COOKIE_NAME + "="))
                    .findFirst()
                    .orElseThrow(() -> new CmiReaderServiceException("Could not initialize a new PHP Web Session."));

            final var phpSessionId = Arrays.stream(phpSessionIdCookie.split(";"))
                    .filter(c -> c.startsWith(CMI_WEBPORTAL_PHP_SESSION_COOKIE_NAME + "="))
                    .findFirst()
                    .orElseThrow(() -> new CmiReaderServiceException("Could not initialize a new PHP Web Session."))
                    .split("=")[1];

            final var sessionAuthCredentials = new SessionAuthCredentials();

            sessionAuthCredentials.setCookies(Map.of(CMI_WEBPORTAL_PHP_SESSION_COOKIE_NAME, phpSessionId));

            final var loginPayload = new FormDataPayload();

            loginPayload.getFormData().put("username", username);
            loginPayload.getFormData().put("passwort", password);

            final var loginResponse = httpClient.post("https://cmi.ta.co.at/portal/checkLogin.inc.php?mode=ta", loginPayload, sessionAuthCredentials);

            checkStatusOrOtherwiseThrowException(loginResponse);

            return sessionAuthCredentials;
        } catch (HttpClientException e) {
            logger.error("Caught HttpClientException while retrieving information from CMI over WebPortal", e);
            throw new CmiReaderServiceException("Caught HttpClientException while retrieving information from CMI over WebPortal", e);
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

        logger.info("Built CMI Url: {}", url);

        return url;
    }

    private static String buildCmiWebportalUrl(String address, int node, String selector) {
        // https://cmi123456.cmi.ta.co.at/webi/INCLUDE/api.cgi?jsonnode=2&jsonparam=I,O,La,Ld
        // https://<CMI_ADDRESS>/webi/INCLUDE/api.cgi?jsonnode=<JSON_NODE>&jsonparam=I,O,La,Ld

        final var url = String.format("https://%s/webi/INCLUDE/api.cgi?jsonnode=%d&jsonparam=%s", address, node, selector);

        logger.info("Built CMI WebPortal Url: {}", url);

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
