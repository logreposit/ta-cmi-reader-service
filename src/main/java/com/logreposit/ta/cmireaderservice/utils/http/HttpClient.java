package com.logreposit.ta.cmireaderservice.utils.http;

import com.logreposit.ta.cmireaderservice.utils.http.authentication.BasicAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;

public interface HttpClient
{
    HttpClientResponse get(String url) throws HttpClientException;

    HttpClientResponse get(String url, BasicAuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse post(String url) throws HttpClientException;

    HttpClientResponse post(String url, BasicAuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse post(String url, String payload) throws HttpClientException;

    HttpClientResponse post(String url, String payload, BasicAuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse put(String url, String payload) throws HttpClientException;

    HttpClientResponse put(String url, String payload, BasicAuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse delete(String url) throws HttpClientException;

    HttpClientResponse delete(String url, BasicAuthCredentials basicAuthCredentials) throws HttpClientException;
}