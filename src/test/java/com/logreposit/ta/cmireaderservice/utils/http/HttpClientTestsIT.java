package com.logreposit.ta.cmireaderservice.utils.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpClientTestsIT
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient httpClient;

    @Before
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
        Assert.assertEquals(200, response.getHttpStatusCode());
    }

    @Test
    public void testInvalidBasicAuthenticationWithGetCall()
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = String.format("https://httpbin.org/basic-auth/%s/%s", username, password + "invalid");

        try
        {
            HttpClientResponse response = this.httpClient.get(url, new BasicAuthCredentials(username, password));
            Assert.fail("Should never reach this code.");
        }
        catch (HttpClientException exception)
        {
            Assert.assertEquals(401, exception.getHttpClientResponse().getHttpStatusCode());
        }
    }

    @Test
    public void testSimpleGetCall() throws HttpClientException
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        HttpClientResponse response = this.httpClient.get(url, new BasicAuthCredentials(username, password));
        Assert.assertEquals(200, response.getHttpStatusCode());
        Assert.assertTrue(response.getResponseBody().length() > 1);
    }

    @Test
    public void testInvalidRequestMethodWithPostCall() throws JsonProcessingException
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("myKey1", "myValue1");
        hashMap.put("myKey2", "myValue2");

        try
        {
            HttpClientResponse response = this.httpClient.post(url, objectMapper.writeValueAsString(hashMap), new BasicAuthCredentials(username, password));
            Assert.fail("Should never reach this code.");
        }
        catch (HttpClientException exception)
        {
            Assert.assertEquals(405, exception.getHttpClientResponse().getHttpStatusCode());
        }
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

        try
        {
            this.httpClient.put(url, objectMapper.writeValueAsString(hashMap), new BasicAuthCredentials(username, password));

            Assert.fail("Should never reach this code.");
        }
        catch (HttpClientException exception)
        {
            Assert.assertEquals(405, exception.getHttpClientResponse().getHttpStatusCode());
        }
    }

    @Test
    public void testInvalidRequestMethodWithDeleteCall()
    {
        String username = "myusernameAwesome";
        String password = "X";
        String url      = "https://httpbin.org/get";

        try
        {
            HttpClientResponse response = this.httpClient.delete(url, new BasicAuthCredentials(username, password));
            Assert.fail("Should never reach this code.");
        }
        catch (HttpClientException exception)
        {
            Assert.assertEquals(405, exception.getHttpClientResponse().getHttpStatusCode());
        }
    }
}
