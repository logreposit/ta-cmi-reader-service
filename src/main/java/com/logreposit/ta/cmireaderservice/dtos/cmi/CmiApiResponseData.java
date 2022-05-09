package com.logreposit.ta.cmireaderservice.dtos.cmi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiInput;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiLoggingAnalog;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiLoggingDigital;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiNetworkAnalog;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiNetworkDigital;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiOutput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CmiApiResponseData
{
    @JsonProperty(value = "Inputs")
    private List<CmiApiInput> inputs;

    @JsonProperty(value = "Outputs")
    private List<CmiApiOutput> outputs;

    @JsonProperty(value = "Logging Analog")
    private List<CmiApiLoggingAnalog> analogLoggingValues;

    @JsonProperty(value = "Logging Digital")
    private List<CmiApiLoggingDigital> digitalLoggingValues;

    @JsonProperty(value = "Network Analog")
    private List<CmiApiNetworkAnalog> analogNetworkValues;

    @JsonProperty(value = "Network Digital")
    private List<CmiApiNetworkDigital> digitalNetworkValues;

    @JsonProperty(value = "DL-Bus")
    private List<Object> dlBusValues;

    public CmiApiResponseData()
    {
        this.inputs               = new ArrayList<>();
        this.outputs              = new ArrayList<>();
        this.analogLoggingValues  = new ArrayList<>();
        this.digitalLoggingValues = new ArrayList<>();
        this.analogNetworkValues  = new ArrayList<>();
        this.digitalNetworkValues = new ArrayList<>();
        this.dlBusValues          = new ArrayList<>();
    }
}
