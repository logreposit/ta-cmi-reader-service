package com.logreposit.ta.cmireaderservice.utils.http;

import com.logreposit.ta.cmireaderservice.utils.http.authentication.AuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.common.HttpClientResponse;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import com.logreposit.ta.cmireaderservice.utils.http.payload.Payload;

public interface HttpClient
{
    HttpClientResponse get(String url) throws HttpClientException;

    HttpClientResponse get(String url, AuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse post(String url) throws HttpClientException;

    HttpClientResponse post(String url, AuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse post(String url, Payload payload) throws HttpClientException;

    HttpClientResponse post(String url, Payload payload, AuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse put(String url, Payload payload) throws HttpClientException;

    HttpClientResponse put(String url, Payload payload, AuthCredentials basicAuthCredentials) throws HttpClientException;

    HttpClientResponse delete(String url) throws HttpClientException;

    HttpClientResponse delete(String url, AuthCredentials basicAuthCredentials) throws HttpClientException;
}