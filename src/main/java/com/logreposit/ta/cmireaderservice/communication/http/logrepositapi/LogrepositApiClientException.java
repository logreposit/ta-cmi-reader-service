package com.logreposit.ta.cmireaderservice.communication.http.logrepositapi;

import com.logreposit.ta.cmireaderservice.exceptions.LogrepositException;

public class LogrepositApiClientException extends LogrepositException
{
    public LogrepositApiClientException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
