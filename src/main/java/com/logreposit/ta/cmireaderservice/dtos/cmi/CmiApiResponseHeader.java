package com.logreposit.ta.cmireaderservice.dtos.cmi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CmiApiResponseHeader
{
    @JsonProperty(value = "Version")
    private Integer version;

    @JsonProperty(value = "Device")
    private String device;

    @JsonProperty(value = "Timestamp")
    private Integer timestamp;
}
