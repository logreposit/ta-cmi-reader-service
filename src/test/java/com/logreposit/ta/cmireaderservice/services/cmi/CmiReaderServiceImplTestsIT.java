package com.logreposit.ta.cmireaderservice.services.cmi;

import com.logreposit.ta.cmireaderservice.dtos.cmi.CmiApiResponse;
import com.logreposit.ta.cmireaderservice.dtos.common.DeviceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CmiReaderServiceImplTestsIT
{
    @Autowired
    private CmiReaderService cmiReaderService;

    @Test
    public void testRead() throws Exception
    {
        String ip = "10.0.0.98";
        int node = 1;
        String username = "admin";
        String password = "admin";
        DeviceType deviceType = DeviceType.UVR16X2;

        CmiApiResponse cmiApiResponse = this.cmiReaderService.read(ip, username, password, node, deviceType);

        CmiApiResponseTestUtil.checkIfCmiApiResponseIsValid(cmiApiResponse);
    }
}
