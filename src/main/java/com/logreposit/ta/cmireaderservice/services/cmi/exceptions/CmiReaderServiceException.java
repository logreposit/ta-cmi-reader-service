package com.logreposit.ta.cmireaderservice.services.cmi.exceptions;

import com.logreposit.ta.cmireaderservice.exceptions.LogrepositException;

public class CmiReaderServiceException extends LogrepositException
{
    public CmiReaderServiceException(String message)
    {
        super(message);
    }

    public CmiReaderServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
