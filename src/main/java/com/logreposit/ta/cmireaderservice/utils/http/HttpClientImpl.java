package com.logreposit.ta.cmireaderservice.utils.http;

import com.logreposit.ta.cmireaderservice.utils.http.authentication.AuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthUtils;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.SessionAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientRequestMethod;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import com.logreposit.ta.cmireaderservice.utils.http.payload.FormDataPayload;
import com.logreposit.ta.cmireaderservice.utils.http.payload.JsonPayload;
import com.logreposit.ta.cmireaderservice.utils.http.payload.Payload;
import com.logreposit.ta.cmireaderservice.utils.logging.LoggingUtils;
import com.logreposit.ta.cmireaderservice.utils.retry.RetryTemplateFactory;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class HttpClientImpl implements HttpClient
{
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private static final String    HTTP_HEADER_CONTENT_TYPE_KEY   = "Content-Type";
    private static final String    HTTP_HEADER_CONTENT_TYPE_VALUE = "application/json; charset=utf-8";
    private static final String    HTTP_HEADER_AUTHORIZATION_KEY  = "Authorization";
    private static final Charset   CHARSET_UTF_8                  = StandardCharsets.UTF_8;
    private static final MediaType MEDIA_TYPE_JSON                = MediaType.parse(HTTP_HEADER_CONTENT_TYPE_VALUE);

    private final OkHttpClient okHttpClient;

    @Autowired
    public HttpClientImpl()
    {
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public HttpClientResponse get(String url) throws HttpClientException
    {
        HttpClientResponse httpClientResponse = this.get(url, null);
        return httpClientResponse;
    }

    @Override
    public HttpClientResponse get(String url, AuthCredentials basicAuthCredentials) throws HttpClientException
    {
        HttpClientResponse httpClientResponse = this.call(HttpClientRequestMethod.GET, url, null, basicAuthCredentials);
        return httpClientResponse;
    }

    @Override
    public HttpClientResponse post(String url) throws HttpClientException
    {
        return this.post(url, null, null);
    }

    @Override
    public HttpClientResponse post(String url, Payload payload) throws HttpClientException
    {
        return this.post(url, payload, null);
    }

    @Override
    public HttpClientResponse post(String url, AuthCredentials basicAuthCredentials) throws HttpClientException
    {
        return this.post(url, null, basicAuthCredentials);
    }

    @Override
    public HttpClientResponse post(String url, Payload payload, AuthCredentials basicAuthCredentials) throws HttpClientException
    {
        HttpClientResponse httpClientResponse = this.call(HttpClientRequestMethod.POST, url, payload, basicAuthCredentials);

        return httpClientResponse;
    }

    @Override
    public HttpClientResponse put(String url, Payload payload) throws HttpClientException
    {
        return this.put(url, payload, null);
    }

    @Override
    public HttpClientResponse put(String url, Payload payload, AuthCredentials basicAuthCredentials) throws HttpClientException
    {
        HttpClientResponse httpClientResponse = this.call(HttpClientRequestMethod.PUT, url, payload, basicAuthCredentials);

        return httpClientResponse;
    }


    @Override
    public HttpClientResponse delete(String url) throws HttpClientException
    {
        return this.delete(url, null);
    }

    @Override
    public HttpClientResponse delete(String url, AuthCredentials basicAuthCredentials) throws HttpClientException
    {
        HttpClientResponse httpClientResponse = this.call(HttpClientRequestMethod.DELETE, url, null, basicAuthCredentials);

        return httpClientResponse;
    }

    private HttpClientResponse call(HttpClientRequestMethod method, String url, Payload payload, AuthCredentials basicAuthCredentials) throws HttpClientException
    {
        HttpClientResponse httpClientResponse = new HttpClientResponse();
        httpClientResponse.setRequestMethod(method);
        httpClientResponse.setRequestUrl(url);
        httpClientResponse.setRequestBody(payload);

        Request.Builder requestBuilder = this.getBasicRequestBuilder(url, basicAuthCredentials);
        Request         request        = null;

        switch (method)
        {
            case GET:
                request = requestBuilder.get().build();
                break;
            case DELETE:
                request = requestBuilder.delete().build();
                break;
            case PUT:
                request = this.buildPutRequest(requestBuilder, payload);
                break;
            case POST:
                request = this.buildPostRequest(requestBuilder, payload);
                break;
            default:
                throw new HttpClientException(String.format("UNKNOWN Request Method %s", method), httpClientResponse);
        }

        if (request != null && request.headers() != null && request.headers().toMultimap().size() > 0)
        {
            httpClientResponse.getRequestHeaders().putAll(request.headers().toMultimap());
        }

        logger.info("Executing Request: URL: {}", url);

        this.executeRequest(request, httpClientResponse);

        logger.info("Executed Request: final HttpClientResponse object: {}", LoggingUtils.serialize(httpClientResponse));

        return httpClientResponse;
    }

    private Request buildPostRequest(Request.Builder requestBuilder, Payload payload)
    {
        RequestBody requestBody = this.buildRequestBody(payload);

        if (payload == null)
        {
            requestBuilder.addHeader("Content-Length", "0");
        }

        Request request = requestBuilder.post(requestBody).build();

        return request;
    }

    private Request buildPutRequest(Request.Builder requestBuilder, Payload payload)
    {
        RequestBody requestBody = this.buildRequestBody(payload);

        if (payload == null)
            requestBuilder.addHeader("Content-Length", "0");

        Request request = requestBuilder.put(requestBody).build();

        return request;
    }

    private RequestBody buildRequestBody(Payload payload)
    {
        if (payload == null)
        {
            return RequestBody.create(null, new byte[0]);
        }

        if (payload instanceof JsonPayload jsonPayload) {
            return RequestBody.create(MEDIA_TYPE_JSON, jsonPayload.getBody());
        }

        if (payload instanceof FormDataPayload formDataPayload) {
            final var formBodyBuilder = new FormBody.Builder();

            formDataPayload.getFormData().forEach(formBodyBuilder::add);

            return formBodyBuilder.build();
        }

        throw new RuntimeException("Invalid payload!");
    }

    private void executeRequest(Request request, HttpClientResponse httpClientResponse) throws HttpClientException
    {
        Response      response      = null;
        RetryTemplate retryTemplate = RetryTemplateFactory.getRetryTemplateWithExponentialBackOffForGivenException(5, 150, 2.3, Collections.singletonList(IOException.class));

        try
        {
            response = retryTemplate.execute(retryContext -> {
                Call call = this.okHttpClient.newCall(request);
                return call.execute();
            });

            if (response == null)
            {
                logger.error("response object is null!");
                throw new HttpClientException("response == null", httpClientResponse);
            }

            httpClientResponse.setSentRequestAtMillis(response.sentRequestAtMillis());
            httpClientResponse.setReceivedResponseAtMillis(response.receivedResponseAtMillis());

            if (response.headers() != null && response.headers().toMultimap().size() > 0)
            {
                httpClientResponse.getResponseHeaders().putAll(response.headers().toMultimap());
            }

            if (response.body() != null)
            {
                byte[] responseBytes = response.body().bytes();
                httpClientResponse.setResponseBodyAsBytes(responseBytes);
                httpClientResponse.setResponseBody(new String(responseBytes, CHARSET_UTF_8));
            }

            httpClientResponse.setHttpStatusMessage(response.message());
            httpClientResponse.setHttpStatusCode(response.code());

            if (!response.isSuccessful())
            {
                logger.error("Response returned with HTTP Status [{}] {}", response.code(), response.message());
                throw new HttpClientException(String.format("Response returned with HTTP Status [%s] %s", response.code(), response.message()), httpClientResponse);
            }
        }
        catch (IOException e)
        {
            logger.error("Got unexpected IOException while executing HTTP(s) request", e);
            throw new HttpClientException("Got unexpected IOException while executing HTTP(s) request.", e, httpClientResponse);
        }
        finally
        {
            if (response != null)
            {
                response.close();
            }
        }
    }

    private Request.Builder getBasicRequestBuilder(String url, AuthCredentials authCredentials)
    {
        Request.Builder requestBuilder = new Request.Builder().url(url);

        if (authCredentials instanceof BasicAuthCredentials basicAuthCredentials) {
            this.addHeadersToRequestBuilder(requestBuilder, basicAuthCredentials);
        }

        if (authCredentials instanceof SessionAuthCredentials sessionAuthCredentials) {
            this.addHeadersToRequestBuilder(requestBuilder, sessionAuthCredentials);
        }

        return requestBuilder;
    }

    private void addHeadersToRequestBuilder(Request.Builder requestBuilder, BasicAuthCredentials basicAuthCredentials)
    {
        if (requestBuilder == null)
        {
            return;
        }

        Map<String, String> headers = buildHeaders(basicAuthCredentials);

        for (Map.Entry<String, String> header : headers.entrySet())
        {
            requestBuilder.addHeader(header.getKey(), header.getValue());
        }
    }

    private void addHeadersToRequestBuilder(Request.Builder requestBuilder, SessionAuthCredentials sessionAuthCredentials) {
        if (requestBuilder == null || sessionAuthCredentials == null || sessionAuthCredentials.getCookies() == null || sessionAuthCredentials.getCookies().isEmpty()) {
            return;
        }

        requestBuilder.addHeader("Cookie", sessionAuthCredentials.getCookies().entrySet().stream().map(e -> String.format("%s=%s", e.getKey(), e.getValue())).collect(Collectors.joining("; ")));
    }

    private static Map<String, String> buildHeaders(BasicAuthCredentials basicAuthCredentials)
    {
        Map<String, String> headers = new HashMap<>();
        headers.put(HTTP_HEADER_CONTENT_TYPE_KEY, HTTP_HEADER_CONTENT_TYPE_VALUE);

        if (basicAuthCredentials != null)
        {
            String basicAuthHeaderValue = BasicAuthUtils.buildBasicAuthenticationHeaderValue(basicAuthCredentials);
            headers.put(HTTP_HEADER_AUTHORIZATION_KEY, basicAuthHeaderValue);
        }

        return headers;
    }
}
