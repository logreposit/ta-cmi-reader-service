package com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.RasState;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Input extends AbstractIO
{
    private RasState rasState;

    public RasState getRasState()
    {
        return this.rasState;
    }

    public void setRasState(RasState rasState)
    {
        this.rasState = rasState;
    }
}
