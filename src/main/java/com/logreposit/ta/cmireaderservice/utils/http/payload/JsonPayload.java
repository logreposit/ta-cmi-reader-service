package com.logreposit.ta.cmireaderservice.utils.http.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonPayload implements Payload {
    private String body;
}
