package com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.DeviceType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CmiLogData
{
    private Date date;
    private DeviceType deviceType;
    private List<Input> inputs;
    private List<Output> outputs;
    private List<AnalogLoggingValue> analogLoggingValues;
    private List<DigitalLoggingValue> digitalLoggingValues;

    public CmiLogData()
    {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.analogLoggingValues = new ArrayList<>();
        this.digitalLoggingValues = new ArrayList<>();
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public DeviceType getDeviceType()
    {
        return this.deviceType;
    }

    public void setDeviceType(DeviceType deviceType)
    {
        this.deviceType = deviceType;
    }

    public List<Input> getInputs()
    {
        return this.inputs;
    }

    public void setInputs(List<Input> inputs)
    {
        this.inputs = inputs;
    }

    public List<Output> getOutputs()
    {
        return this.outputs;
    }

    public void setOutputs(List<Output> outputs)
    {
        this.outputs = outputs;
    }

    public List<AnalogLoggingValue> getAnalogLoggingValues()
    {
        return this.analogLoggingValues;
    }

    public void setAnalogLoggingValues(List<AnalogLoggingValue> analogLoggingValues)
    {
        this.analogLoggingValues = analogLoggingValues;
    }

    public List<DigitalLoggingValue> getDigitalLoggingValues()
    {
        return this.digitalLoggingValues;
    }

    public void setDigitalLoggingValues(List<DigitalLoggingValue> digitalLoggingValues)
    {
        this.digitalLoggingValues = digitalLoggingValues;
    }
}
