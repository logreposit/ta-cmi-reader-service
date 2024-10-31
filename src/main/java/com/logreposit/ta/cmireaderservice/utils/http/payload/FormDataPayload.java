package com.logreposit.ta.cmireaderservice.utils.http.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FormDataPayload implements Payload {
    private Map<String, String> formData = new HashMap<>();
}
