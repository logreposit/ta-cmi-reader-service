package com.logreposit.ta.cmireaderservice.utils.http.authentication;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SessionAuthCredentials implements AuthCredentials {
    private Map<String, String> cookies;
}
