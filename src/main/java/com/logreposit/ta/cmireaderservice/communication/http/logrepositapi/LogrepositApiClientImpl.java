package com.logreposit.ta.cmireaderservice.communication.http.logrepositapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.communication.http.common.ResponseErrorHandlerFactory;
import com.logreposit.ta.cmireaderservice.communication.http.logrepositapi.dtos.request.DeviceType;
import com.logreposit.ta.cmireaderservice.communication.http.logrepositapi.dtos.request.LogIngressRequestDto;
import com.logreposit.ta.cmireaderservice.configuration.ApplicationConfiguration;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.CmiLogData;
import com.logreposit.ta.cmireaderservice.utils.retry.RetryTemplateFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class LogrepositApiClientImpl implements LogrepositApiClient
{
    private static final Logger logger = LoggerFactory.getLogger(LogrepositApiClientImpl.class);

    private static final String DEVICE_TOKEN_HEADER_NAME = "x-device-token";

    private final RestTemplate             restTemplate;
    private final ApplicationConfiguration applicationConfiguration;
    private final ObjectMapper             objectMapper;

    public LogrepositApiClientImpl(RestTemplateBuilder restTemplateBuilder, ApplicationConfiguration applicationConfiguration, ObjectMapper objectMapper)
    {
        ResponseErrorHandler responseErrorHandler = ResponseErrorHandlerFactory.createWithoutHttpStatusErrorHandling();

        this.restTemplate = restTemplateBuilder.errorHandler(responseErrorHandler)
                                               .setConnectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                                               .setReadTimeout(Duration.of(10, ChronoUnit.SECONDS))
                                               .build();

        this.applicationConfiguration = applicationConfiguration;
        this.objectMapper             = objectMapper;
    }

    @Override
    public void publishData(CmiLogData cmiLogData) throws LogrepositApiClientException
    {
        try
        {
            URL                  ingressUrl           = new URL(this.getApiBaseUrl(), "ingress");
            LogIngressRequestDto logIngressRequestDto = new LogIngressRequestDto(DeviceType.TECHNISCHE_ALTERNATIVE_CMI, cmiLogData);
            String               payload              = this.objectMapper.writeValueAsString(logIngressRequestDto);
            String               response             = this.requestWithRetries(ingressUrl.toString(), HttpMethod.POST, payload);

            logger.info("Successfully published log data: {}", response);
        }
        catch (Exception e)
        {
            logger.error("Unable to publish log data", e);
            throw new LogrepositApiClientException("Unable to publish log data", e);
        }
    }

    private URL getApiBaseUrl() throws MalformedURLException
    {
        String apiBaseUrl = this.applicationConfiguration.getApiBaseUrl();

        apiBaseUrl = StringUtils.appendIfMissing(apiBaseUrl, "/");

        return new URL(apiBaseUrl);
    }

    private String requestWithRetries(String uriAsString, HttpMethod httpMethod, String requestPayload) throws Exception
    {
        int           maxAttempts            = this.applicationConfiguration.getApiClientRetryCount();
        long          initialBackOffInterval = this.applicationConfiguration.getApiClientRetryInitialBackOffInterval();
        double        backOffMulitplier      = this.applicationConfiguration.getApiClientRetryBackOffMultiplier();

        RetryTemplate retryTemplate = RetryTemplateFactory.getRetryTemplateWithExponentialBackOffForAllExceptions(
                maxAttempts,
                initialBackOffInterval,
                backOffMulitplier
        );

        String successResponse = retryTemplate.execute(retryContext -> {
            logger.info("Retry {}/{}: {} '{}' ... ", retryContext.getRetryCount(), maxAttempts, httpMethod.toString(), uriAsString);
            return this.request(uriAsString, httpMethod, requestPayload);
        });

        return successResponse;
    }

    private String request(String uriAsString, HttpMethod httpMethod, String requestPayload) throws Exception
    {
        try
        {
            URI uri = new URI(uriAsString);

            logger.info("Sending {} Request to '{}' with payload {} ...", httpMethod.toString(), uri.toString(), requestPayload);

            ResponseEntity<String> responseEntity = this.restTemplate.exchange(
                    uri,
                    httpMethod,
                    new HttpEntity<>(requestPayload, this.buildHeaders()),
                    String.class
            );

            String responseBody = responseEntity.getBody();

            if (!responseEntity.getStatusCode().is2xxSuccessful())
            {
                logger.error("Request was not successful: Got HTTP status code '{}': Body: {}", responseEntity.getStatusCodeValue(), responseBody);

                String errorMessage = String.format("Request to '%s' was not successful: HTTP %d", uri.toString(), responseEntity.getStatusCodeValue());
                throw new Exception(errorMessage);
            }

            logger.debug("Request was successful: HTTP status code '{}': Body: {}", responseEntity.getStatusCodeValue(), responseBody);

            return responseBody;
        }
        catch (Exception e)
        {
            logger.error("Caught unexpected Exception while executing {} '{}'", httpMethod.toString(), uriAsString, e);
            throw e;
        }
    }

    private HttpHeaders buildHeaders()
    {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set(DEVICE_TOKEN_HEADER_NAME, this.applicationConfiguration.getDeviceToken());

        return httpHeaders;
    }
}
