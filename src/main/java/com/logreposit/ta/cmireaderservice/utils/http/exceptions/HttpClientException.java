package com.logreposit.ta.cmireaderservice.utils.http.exceptions;

import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;

public class HttpClientException extends Exception
{
    private final HttpClientResponse httpClientResponse;

    public HttpClientException(String message, HttpClientResponse httpClientResponse)
    {
        super(message);

        this.httpClientResponse = httpClientResponse;
    }

    public HttpClientException(String message, Throwable cause, HttpClientResponse httpClientResponse)
    {
        super(message, cause);

        this.httpClientResponse = httpClientResponse;
    }

    public HttpClientResponse getHttpClientResponse()
    {
        return this.httpClientResponse;
    }
}
