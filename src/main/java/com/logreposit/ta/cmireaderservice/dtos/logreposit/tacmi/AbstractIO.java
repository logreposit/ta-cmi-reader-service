package com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.SignalType;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.Unit;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AbstractIO
{
    private Integer number;
    private SignalType signal;
    private Unit unit;
    private Double value;

    public Integer getNumber()
    {
        return this.number;
    }

    public void setNumber(Integer number)
    {
        this.number = number;
    }

    public SignalType getSignal()
    {
        return this.signal;
    }

    public void setSignal(SignalType signal)
    {
        this.signal = signal;
    }

    public Unit getUnit()
    {
        return this.unit;
    }

    public void setUnit(Unit unit)
    {
        this.unit = unit;
    }

    public Double getValue()
    {
        return this.value;
    }

    public void setValue(Double value)
    {
        this.value = value;
    }
}
