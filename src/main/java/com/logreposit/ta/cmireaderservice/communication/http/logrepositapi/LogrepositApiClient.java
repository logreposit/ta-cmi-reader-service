package com.logreposit.ta.cmireaderservice.communication.http.logrepositapi;

import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.CmiLogData;

public interface LogrepositApiClient
{
    void publishData(CmiLogData cmiLogData) throws LogrepositApiClientException;
}
