package com.logreposit.ta.cmireaderservice.configuration;

import com.logreposit.ta.cmireaderservice.dtos.common.DeviceType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@Configuration
@ConfigurationProperties(value = "cmireaderservice")
@Getter
@Setter
public class ApplicationConfiguration
{
    @NotBlank
    private String deviceToken;

    @NotBlank
    private String cmiAddress;

    @NotBlank
    private String cmiUsername;

    @NotBlank
    private String cmiPassword;

    @NotNull
    private DeviceType deviceType;

    @NotNull
    private Integer deviceCanNode;

    @NotBlank
    private String deviceTimezone;

    @NotBlank
    private String apiBaseUrl;

    @NotNull
    private Integer apiClientRetryCount;

    @NotNull
    private Long apiClientRetryInitialBackOffInterval;

    @NotNull
    private Double apiClientRetryBackOffMultiplier;

    @NotNull
    private Long collectInterval;
}
