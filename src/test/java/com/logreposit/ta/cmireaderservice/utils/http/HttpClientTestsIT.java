package com.logreposit.ta.cmireaderservice.utils.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class HttpClientTestsIT
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient httpClient;

    @BeforeEach
    public void setUp()
    {
        this.httpClient = new HttpClientImpl();
    }

    @Test
    public void testBasicAuthenticationWithGetCall() throws HttpClientException
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = String.format("https://httpbin.org/basic-auth/%s/%s", username, password);

        HttpClientResponse response = this.httpClient.get(url, new BasicAuthCredentials(username, password));

        assertThat(response.getHttpStatusCode()).isEqualTo(200);
    }

    @Test
    public void testInvalidBasicAuthenticationWithGetCall()
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = String.format("https://httpbin.org/basic-auth/%s/%s", username, password + "invalid");

        var exception = catchThrowableOfType(() -> this.httpClient.get(url, new BasicAuthCredentials(username, password)), HttpClientException.class);

        assertThat(exception.getHttpClientResponse().getHttpStatusCode()).isEqualTo(401);
    }

    @Test
    public void testSimpleGetCall() throws HttpClientException
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        HttpClientResponse response = this.httpClient.get(url, new BasicAuthCredentials(username, password));

        assertThat(response.getHttpStatusCode()).isEqualTo(200);
        assertThat(response.getResponseBody()).hasSizeGreaterThan(1);
    }

    @Test
    public void testInvalidRequestMethodWithPostCall()
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("myKey1", "myValue1");
        hashMap.put("myKey2", "myValue2");

        var exception = catchThrowableOfType(() -> this.httpClient.post(url, objectMapper.writeValueAsString(hashMap), new BasicAuthCredentials(username, password)), HttpClientException.class);

        assertThat(exception.getHttpClientResponse().getHttpStatusCode()).isEqualTo(405);
    }

    @Test
    public void testInvalidRequestMethodWithPutCall() throws JsonProcessingException
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("myKey1", "myValue1");
        hashMap.put("myKey2", "myValue2");

        var exception = catchThrowableOfType(() -> this.httpClient.put(url, objectMapper.writeValueAsString(hashMap), new BasicAuthCredentials(username, password)), HttpClientException.class);

        assertThat(exception.getHttpClientResponse().getHttpStatusCode()).isEqualTo(405);
    }

    @Test
    public void testInvalidRequestMethodWithDeleteCall()
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        var exception = catchThrowableOfType(() -> this.httpClient.delete(url, new BasicAuthCredentials(username, password)), HttpClientException.class);

        assertThat(exception.getHttpClientResponse().getHttpStatusCode()).isEqualTo(405);
    }
}
