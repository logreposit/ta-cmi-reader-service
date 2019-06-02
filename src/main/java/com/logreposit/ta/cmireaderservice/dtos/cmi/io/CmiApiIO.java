package com.logreposit.ta.cmireaderservice.dtos.cmi.io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public abstract class CmiApiIO
{
    @JsonProperty(value = "Number")
    private Integer number;

    @JsonProperty(value = "AD")
    private String ad;

    @JsonProperty(value = "Value")
    private CmiApiValue value;
}
