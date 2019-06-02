package com.logreposit.ta.cmireaderservice.communication.http.logrepositapi.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.CmiLogData;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class LogIngressRequestDto
{
    private DeviceType deviceType;
    private CmiLogData data;

    public LogIngressRequestDto(DeviceType deviceType, CmiLogData cmiLogData)
    {
        this.deviceType = deviceType;
        this.data       = cmiLogData;
    }
}
