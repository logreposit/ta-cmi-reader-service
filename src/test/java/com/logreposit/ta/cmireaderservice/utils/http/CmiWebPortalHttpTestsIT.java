package com.logreposit.ta.cmireaderservice.utils.http;

import com.logreposit.ta.cmireaderservice.utils.http.authentication.SessionAuthCredentials;
import com.logreposit.ta.cmireaderservice.utils.http.exceptions.HttpClientException;
import com.logreposit.ta.cmireaderservice.utils.http.payload.FormDataPayload;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CmiWebPortalHttpTestsIT {
    @Test
    public void testLogInAndRetrieveX2Data() throws HttpClientException {
        final var httpClient = new HttpClientImpl();

        // 1 - get cookie
        final var firstResponse = httpClient.get("https://cmi.ta.co.at/");
        final var phpSessionIdCookie = firstResponse.getResponseHeaders().getOrDefault("set-cookie", List.of()).stream().filter(c -> c.contains("PHPSESSID=")).findFirst().orElseThrow();

        // 2 - login
        final var sessionIdCookie = Arrays.stream(phpSessionIdCookie.split(";")).filter(c -> c.startsWith("PHPSESSID=")).findFirst().orElseThrow().split("=");

        assertThat(sessionIdCookie).hasSize(2);
        final var cookieName = sessionIdCookie[0];
        final var cookieValue = sessionIdCookie[1];

        final var loginPayload = new FormDataPayload();
        loginPayload.getFormData().put("username", "MyUsername");
        loginPayload.getFormData().put("passwort", "MyPassword");

        final var sessionAuthCredentials = new SessionAuthCredentials();

        sessionAuthCredentials.setCookies(Map.of(cookieName, cookieValue));

        final var loginResponse = httpClient.post("https://cmi.ta.co.at/portal/checkLogin.inc.php?mode=ta", loginPayload, sessionAuthCredentials);

        assertThat(loginResponse.getHttpStatusCode()).isEqualTo(200);

        // 3 - read data from CMI

        // https://CMI_NAME.cmi.ta.co.at/webi/INCLUDE/api.cgi?jsonnode=2&jsonparam=I,O,La,Ld
        //

        final var cmiJson = httpClient.get("https://CMI_NAME.cmi.ta.co.at/webi/INCLUDE/api.cgi?jsonnode=2&jsonparam=I,O,La,Ld", sessionAuthCredentials);

        assertThat(cmiJson).isNotNull();
    }
}
