package com.logreposit.ta.cmireaderservice.utils.http.authentication;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BasicAuthUtilsTests
{
    @Test
    public void testBuildBasicAuthenticationHeaderValue()
    {
        String username                = "My-testing-Username-Max-Mustermann";
        String password                = "3795124b-ec75-4e89-bf3e-704a104f23e7";
        String authHeaderValueExpected = "Basic TXktdGVzdGluZy1Vc2VybmFtZS1NYXgtTXVzdGVybWFubjozNzk1MTI0Yi1lYzc1LTRlODktYmYzZS03MDRhMTA0ZjIzZTc=";

        BasicAuthCredentials basicAuthCredentials     = new BasicAuthCredentials(username, password);
        String               authHeaderValueGenerated = BasicAuthUtils.buildBasicAuthenticationHeaderValue(basicAuthCredentials);

        assertThat(authHeaderValueGenerated).isEqualTo(authHeaderValueExpected);
    }
}
