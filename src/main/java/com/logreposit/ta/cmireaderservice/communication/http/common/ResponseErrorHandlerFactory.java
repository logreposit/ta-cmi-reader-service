package com.logreposit.ta.cmireaderservice.communication.http.common;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class ResponseErrorHandlerFactory
{
    private ResponseErrorHandlerFactory()
    {
    }

    public static ResponseErrorHandler createWithoutHttpStatusErrorHandling()
    {
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler()
        {
            @Override
            public boolean hasError(ClientHttpResponse response)
            {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response)
            {
            }
        };

        return responseErrorHandler;
    }
}
