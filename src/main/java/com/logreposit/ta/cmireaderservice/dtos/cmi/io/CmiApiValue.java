package com.logreposit.ta.cmireaderservice.dtos.cmi.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CmiApiValue
{
    @JsonProperty(value = "Value")
    private Double value;

    @JsonProperty(value = "Unit")
    private String unit;

    @JsonProperty(value = "RAS")
    private String ras;

    @JsonProperty(value = "State")
    private Integer state;
}
