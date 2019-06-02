package com.logreposit.ta.cmireaderservice.services.collector.exceptions;

import com.logreposit.ta.cmireaderservice.exceptions.LogrepositException;

public class ScheduledLogCollectorException extends LogrepositException
{
    public ScheduledLogCollectorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
