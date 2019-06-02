package com.logreposit.ta.cmireaderservice.utils.http.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class BasicAuthUtils
{
    private BasicAuthUtils()
    {
    }

    private static final Logger logger = LoggerFactory.getLogger(BasicAuthUtils.class);

    public static String buildBasicAuthenticationHeaderValue(BasicAuthCredentials basicAuthCredentials)
    {
        String usernameAndPasswordInUriNotation       = String.format("%s:%s", basicAuthCredentials.getUsername(), basicAuthCredentials.getPassword());
        byte[] base64EncodedAuthenticationInformation = Base64.getEncoder().encode(usernameAndPasswordInUriNotation.getBytes());
        String basicAuthenticationValue               = String.format("Basic %s", new String(base64EncodedAuthenticationInformation));

        logger.info("Built basicAuthenticationValue: {}", basicAuthenticationValue);

        return basicAuthenticationValue;
    }
}
