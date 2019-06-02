package com.logreposit.ta.cmireaderservice.services.cmi;

import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.DeviceType;
import com.logreposit.ta.cmireaderservice.services.cmi.exceptions.CmiReaderServiceException;

public interface CmiReaderService
{
    CmiApiResponse read(String address, String username, String password, int node, DeviceType deviceType) throws CmiReaderServiceException;
}
