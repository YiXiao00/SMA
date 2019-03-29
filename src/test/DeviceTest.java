import com.smarthome.sso.MainApplication;
import com.smarthome.sso.web.controller.FiwareController;
import com.smarthome.sso.web.controller.SigninController;
import com.smarthome.sso.web.domain.FiwareInfo;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeviceTest {

    private MockMvc mvc;

    private String userSessionId = "";

    @Autowired
    private SigninController signinController;

    @Autowired
    private FiwareController fiwareController;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(signinController,fiwareController).build();
    }


    private void initializeUser() throws Exception {
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("name","test_default_username");
        params.add("pwd","test_default_password");
        request = MockMvcRequestBuilders.post("/user/signup").params(params);
        mvc.perform(request);
        request = MockMvcRequestBuilders.post("/user/signin").params(params);
        MvcResult mvcResult = mvc.perform(request).andReturn();
        userSessionId = mvcResult.getResponse().getCookie("sessionId").getValue();
    }

    private void disposeUser() throws Exception{
        signinController.innerDeleteUser("test_default_username");
    }

    @Test
    public void test_a_addDevice() throws Exception{
        initializeUser();
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("type","test_default_device_type");
        request = MockMvcRequestBuilders.post("/device/add").params(params);
        mvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void test_b_verifyDevice() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionID);
        params.add("type","test_default_device_type");
        request = MockMvcRequstBuilders.post("/device/verify").params(params);
        mvc.perform(request).andExcept(status().isOk());
    }

    @Test
    public void test_z_innerClean_UserDeviceTask() throws Exception{
        disposeUser();
    }

}
