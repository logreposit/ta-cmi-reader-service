package com.logreposit.ta.cmireaderservice.utils.http.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpClientResponse
{
    private Integer                   httpStatusCode;
    private String                    httpStatusMessage;
    private Long                      sentRequestAtMillis;
    private Long                      receivedResponseAtMillis;
    private HttpClientRequestMethod   requestMethod;
    private String                    requestUrl;
    private String                    requestBody;
    private byte[]                    responseBodyAsBytes;
    private String                    responseBody;
    private Map<String, List<String>> requestHeaders;
    private Map<String, List<String>> responseHeaders;

    public HttpClientResponse()
    {
        this.requestHeaders  = new HashMap<>();
        this.responseHeaders = new HashMap<>();
    }

    public HttpClientRequestMethod getRequestMethod()
    {
        return this.requestMethod;
    }

    public void setRequestMethod(HttpClientRequestMethod requestMethod)
    {
        this.requestMethod = requestMethod;
    }

    public int getHttpStatusCode()
    {
        return this.httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode)
    {
        this.httpStatusCode = httpStatusCode;
    }

    public String getRequestUrl()
    {
        return this.requestUrl;
    }

    public void setRequestUrl(String requestUrl)
    {
        this.requestUrl = requestUrl;
    }

    public String getHttpStatusMessage()
    {
        return this.httpStatusMessage;
    }

    public void setHttpStatusMessage(String httpStatusMessage)
    {
        this.httpStatusMessage = httpStatusMessage;
    }

    public Long getSentRequestAtMillis()
    {
        return this.sentRequestAtMillis;
    }

    public void setSentRequestAtMillis(Long sentRequestAtMillis)
    {
        this.sentRequestAtMillis = sentRequestAtMillis;
    }

    public Long getReceivedResponseAtMillis()
    {
        return this.receivedResponseAtMillis;
    }

    public void setReceivedResponseAtMillis(Long receivedResponseAtMillis)
    {
        this.receivedResponseAtMillis = receivedResponseAtMillis;
    }

    public String getRequestBody()
    {
        return this.requestBody;
    }

    public void setRequestBody(String requestBody)
    {
        this.requestBody = requestBody;
    }

    public String getResponseBody()
    {
        return this.responseBody;
    }

    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }

    public Map<String, List<String>> getRequestHeaders()
    {
        return this.requestHeaders;
    }

    public void setRequestHeaders(Map<String, List<String>> requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, List<String>> getResponseHeaders()
    {
        return this.responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders)
    {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getResponseBodyAsBytes()
    {
        return this.responseBodyAsBytes;
    }

    public void setResponseBodyAsBytes(byte[] responseBodyAsBytes)
    {
        this.responseBodyAsBytes = responseBodyAsBytes;
    }
}
