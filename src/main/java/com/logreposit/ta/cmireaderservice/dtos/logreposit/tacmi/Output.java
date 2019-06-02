package com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Output extends AbstractIO
{
    private Integer state;

    public Integer getState()
    {
        return this.state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }
}
