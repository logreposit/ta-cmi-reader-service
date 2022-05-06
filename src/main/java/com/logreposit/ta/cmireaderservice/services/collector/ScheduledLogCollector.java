package com.logreposit.ta.cmireaderservice.services.collector;

import com.logreposit.ta.cmireaderservice.communication.http.logrepositapi.LogrepositApiClient;
import com.logreposit.ta.cmireaderservice.configuration.ApplicationConfiguration;
import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.CmiLogData;
import com.logreposit.ta.cmireaderservice.dtos.logreposit.tacmi.enums.DeviceType;
import com.logreposit.ta.cmireaderservice.services.cmi.CmiReaderService;
import com.logreposit.ta.cmireaderservice.services.cmi.exceptions.CmiReaderServiceException;
import com.logreposit.ta.cmireaderservice.services.collector.exceptions.ScheduledLogCollectorException;
import com.logreposit.ta.cmireaderservice.utils.converter.CmiLogDataConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ScheduledLogCollector
{
    private static final Logger logger = LoggerFactory.getLogger(ScheduledLogCollector.class);

    private final ApplicationConfiguration applicationConfiguration;
    private final CmiReaderService         cmiReaderService;
    private final CmiLogDataConverter      cmiLogDataConverter;
    private final LogrepositApiClient      logrepositApiClient;

    @Autowired
    public ScheduledLogCollector(ApplicationConfiguration applicationConfiguration,
                                 CmiReaderService cmiReaderService,
                                 CmiLogDataConverter cmiLogDataConverter,
                                 LogrepositApiClient logrepositApiClient)
    {
        this.applicationConfiguration = applicationConfiguration;
        this.cmiReaderService         = cmiReaderService;
        this.cmiLogDataConverter      = cmiLogDataConverter;
        this.logrepositApiClient      = logrepositApiClient;
    }

    @Scheduled(fixedDelayString = "${cmireaderservice.collect-interval}")
    public void collect() throws ScheduledLogCollectorException
    {
        CmiLogData cmiLogData = this.retrieveCmiLogData();

        this.publishData(cmiLogData);
    }

    private CmiLogData retrieveCmiLogData() throws ScheduledLogCollectorException
    {
        logger.debug("Started collecting log values.");

        Date begin = new Date();

        CmiApiResponse cmiApiResponse = this.collectCmiLogdata();

        throwExceptionIfCmiApiResponseIsNotValid(cmiApiResponse);

        final var cmiLogData = this.cmiLogDataConverter.convertCmiApiResponse(cmiApiResponse);

        long logFetchDuration = ((new Date()).getTime() - begin.getTime()) / 1000;
        logger.info("Finished collecting and converting log values. Operation took {} seconds.", logFetchDuration);

        return cmiLogData;
    }

    private CmiApiResponse collectCmiLogdata() throws ScheduledLogCollectorException
    {
        String     cmiAddress  = this.applicationConfiguration.getCmiAddress();
        String     cmiUsername = this.applicationConfiguration.getCmiUsername();
        String     cmiPassword = this.applicationConfiguration.getCmiPassword();
        Integer    deviceNode  = this.applicationConfiguration.getDeviceCanNode();
        DeviceType deviceType  = this.applicationConfiguration.getDeviceType();

        try
        {
            logger.debug("Retrieving actual data from {} CAN Device identified by node number {} connected to CMI with address {}.", deviceType, deviceNode, cmiAddress);
            CmiApiResponse cmiApiResponse = this.cmiReaderService.read(cmiAddress, cmiUsername, cmiPassword, deviceNode, deviceType);
            return cmiApiResponse;
        }
        catch (CmiReaderServiceException e)
        {
            logger.error("Caught CmiReaderServiceException while retrieving log data", e);
            throw new ScheduledLogCollectorException("Caught CmiReaderException while retrieving log data", e);
        }
    }

    private void publishData(CmiLogData cmiLogData) throws ScheduledLogCollectorException
    {
        try
        {
            this.logrepositApiClient.publishData(cmiLogData);
        }
        catch (Exception e)
        {
            logger.error("Unable to publish cmiLogData", e);
            throw new ScheduledLogCollectorException("Unable to publish cmiLogData", e);
        }
    }

    private void throwExceptionIfCmiApiResponseIsNotValid(CmiApiResponse cmiApiResponse) throws ScheduledLogCollectorException
    {
        if (cmiApiResponse == null)
        {
            throw new ScheduledLogCollectorException("cmiApiResponse == null");
        }

        if (cmiApiResponse.getStatusCode() != 0 || !"OK".equals(cmiApiResponse.getStatus()))
        {
            throw new ScheduledLogCollectorException("cmiApiResponse does not contain any useful data");
        }

        if (cmiApiResponse.getData() == null || cmiApiResponse.getData().getInputs() == null || cmiApiResponse.getData().getOutputs() == null)
        {
            throw new ScheduledLogCollectorException("cmiApiResponse.data is corrupt");
        }
    }
}
