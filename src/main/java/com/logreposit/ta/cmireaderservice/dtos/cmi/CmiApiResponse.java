package com.logreposit.ta.cmireaderservice.dtos.cmi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CmiApiResponse
{
    @JsonProperty(value = "Header")
    private CmiApiResponseHeader header;

    @JsonProperty(value = "Data")
    private CmiApiResponseData data;

    @JsonProperty(value = "Status")
    private String status;

    @JsonProperty(value = "Status code")
    private Integer statusCode;
}
