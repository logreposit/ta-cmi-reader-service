package com.logreposit.ta.cmireaderservice.utils.converter;

import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.CmiLogData;

public interface CmiLogDataConverter
{
    CmiLogData convertCmiApiResponse(CmiApiResponse cmiApiResponse);
}
