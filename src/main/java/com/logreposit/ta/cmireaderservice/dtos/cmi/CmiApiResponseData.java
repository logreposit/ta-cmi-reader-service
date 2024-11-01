package com.logreposit.ta.cmireaderservice.dtos.cmi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.logreposit.ta.cmireaderservice.dtos.cmi.io.CmiApiIO;
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
    private List<CmiApiIO> inputs;

    @JsonProperty(value = "Outputs")
    private List<CmiApiOutput> outputs;

    @JsonProperty(value = "DL-Bus")
    private List<CmiApiIO> dlBus;

    @JsonProperty(value = "General")
    private List<Object> general;

    @JsonProperty(value = "Date")
    private List<Object> date;

    @JsonProperty(value = "Time")
    private List<Object> time;

    @JsonProperty(value = "Sun")
    private List<Object> sun;

    @JsonProperty(value = "Electrical power")
    private List<Object> electricalPower;

    @JsonProperty(value = "Network Analog")
    private List<CmiApiNetworkAnalog> analogNetworkValues;

    @JsonProperty(value = "Network Digital")
    private List<CmiApiNetworkDigital> digitalNetworkValues;

    @JsonProperty(value = "Modbus")
    private List<Object> modbus;

    @JsonProperty(value = "KNX")
    private List<Object> knx;

    @JsonProperty(value = "Logging Analog")
    private List<CmiApiLoggingAnalog> loggingAnalog;

    @JsonProperty(value = "Logging Digital")
    private List<CmiApiLoggingDigital> loggingDigital;

    public CmiApiResponseData()
    {
        this.inputs               = new ArrayList<>();
        this.outputs              = new ArrayList<>();
        this.loggingAnalog  = new ArrayList<>();
        this.loggingDigital = new ArrayList<>();
        this.analogNetworkValues  = new ArrayList<>();
        this.digitalNetworkValues = new ArrayList<>();
        this.dlBus          = new ArrayList<>();
    }
}
