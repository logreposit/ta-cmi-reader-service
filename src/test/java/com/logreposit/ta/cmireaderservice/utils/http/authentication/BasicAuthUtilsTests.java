package com.logreposit.ta.cmireaderservice.utils.http.authentication;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthUtilsTests
{
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthUtils.class);

    @Test
    public void testBuildBasicAuthenticationHeaderValue()
    {
        String username                = "My-testing-Username-Max-Mustermann";
        String password                = "3795124b-ec75-4e89-bf3e-704a104f23e7";
        String authHeaderValueExpected = "Basic TXktdGVzdGluZy1Vc2VybmFtZS1NYXgtTXVzdGVybWFubjozNzk1MTI0Yi1lYzc1LTRlODktYmYzZS03MDRhMTA0ZjIzZTc=";

        BasicAuthCredentials basicAuthCredentials     = new BasicAuthCredentials(username, password);
        String               authHeaderValueGenerated = BasicAuthUtils.buildBasicAuthenticationHeaderValue(basicAuthCredentials);

        Assert.assertEquals(authHeaderValueExpected, authHeaderValueGenerated);
    }
}
