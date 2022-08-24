package io.mosip.compliance.toolkit.controllers;

import io.mosip.compliance.toolkit.dto.ErrorDto;
import io.mosip.compliance.toolkit.dto.SbiProjectDto;
import io.mosip.compliance.toolkit.service.SbiProjectService;
import io.mosip.compliance.toolkit.util.RequestValidator;
import io.mosip.kernel.authcodeflowproxy.api.constants.Errors;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
@WebMvcTest
public class SbiProjectControllerTest {

    @InjectMocks
    private SbiProjectController sbiProjectController;

    @Mock
    private SbiProjectService sbiProjectService;

    @Mock
    private RequestValidator requestValidator;


    @Test(expected = Exception.class)
    public void addSbiProject() throws Exception {
        RequestWrapper<SbiProjectDto> value = new RequestWrapper<>();
        SbiProjectDto sbiProjectDto = new SbiProjectDto();
        value.setRequest(sbiProjectDto);
        ResponseWrapper<SbiProjectDto> sbiProjectDtoResponseWrapper = new ResponseWrapper<>();
        Mockito.when(sbiProjectService.addSbiProject(sbiProjectDto)).thenReturn(sbiProjectDtoResponseWrapper);
        sbiProjectController.addSbiProject(value, null);
    }

    @Test
    public void getProjectByIdTest(){
        ResponseWrapper<SbiProjectDto> sbiProjectDtoResponseWrapper = new ResponseWrapper<>();
        Mockito.when(sbiProjectService.getSbiProject(Mockito.anyString())).thenReturn(sbiProjectDtoResponseWrapper);
    }
}