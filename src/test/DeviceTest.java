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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeviceTest {

    private MockMvc mvc;

    private static String userSessionId = "";

    private static String defaultDeviceId = "";

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

//    @Test
//    public void test_b1_verifyDevice() throws Exception{
//        RequestBuilder request;
//        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
//        params.add("token",userSessionId);
//        params.add("type","test_default_device_type");
//        request = MockMvcRequestBuilders.post("/device/verify").params(params);
//        mvc.perform(request).andExpect(status().isOk());
//    }

    @Test
    public void test_b2_getDeviceId() throws Exception{
        RequestBuilder request;
        request = MockMvcRequestBuilders.post("/device/user/all").param("token", userSessionId);
        MvcResult mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern deviceIdPattern = Pattern.compile("deviceId\":\"(.*?)\"");
        Matcher deviceIdMatcher = deviceIdPattern.matcher(deviceString);
        if (deviceIdMatcher.find()){
            String deviceIdSegment = deviceIdMatcher.group(1);
            assert(deviceIdSegment.length() > 0);
            defaultDeviceId = deviceIdSegment;
        }
        else{
            assert(false);
        }
    }

    @Test
    public void test_c1_toggleDevice() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/toggle").params(params);
        mvc.perform(request).andExpect(status().isOk())
                            .andExpect(content().string("Device toggled"));
    }

    @Test
    public void test_c2_checkDeviceStatus() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/get").params(params);
        MvcResult mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern statusPattern = Pattern.compile("poweredOn\":(.*?),");
        Matcher statusMatcher = statusPattern.matcher(deviceString);
        if (statusMatcher.find()){
            String status = statusMatcher.group(1);
            assert("true".equals(status));
            test_c1_toggleDevice();  // shut down device after test
        }
        else{
            assert(false);
        }
    }

    @Test
    public void test_d1_deviceChangeType() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        params.add("input","test_default_device_type2");
        request = MockMvcRequestBuilders.post("/device/change").params(params);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("finished"));
    }

    @Test
    public void test_d2_checkDeviceType() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/get").params(params);
        MvcResult mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern statusPattern = Pattern.compile("type\":\"(.*?)\"");
        Matcher statusMatcher = statusPattern.matcher(deviceString);
        if (statusMatcher.find()){
            String status = statusMatcher.group(1);
            assert("test_default_device_type2".equals(status));
        }
        else{
            assert(false);
        }
    }

    @Test
    public void test_e1_removeDeviceOfUser() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/delete").params(params);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("Device deleted"));
    }

    @Test
    public void test_e2_checkDeviceList() throws Exception{
        RequestBuilder request;
        request = MockMvcRequestBuilders.post("/device/user/all").param("token", userSessionId);
        MvcResult mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        assert(deviceString.length() <= 10);
    }

    @Test
    public void test_f_deviceOfMultipleUser() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("name","test_default_username2");
        params.add("pwd","test_default_password2");
        request = MockMvcRequestBuilders.post("/user/signup").params(params);
        mvc.perform(request);
        request = MockMvcRequestBuilders.post("/user/signin").params(params);
        MvcResult mvcResult = mvc.perform(request).andReturn();
        String user2SessionId = mvcResult.getResponse().getCookie("sessionId").getValue();

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",user2SessionId);
        params2.add("type","test_default_device_type2");
        request = MockMvcRequestBuilders.post("/device/add").params(params2);
        mvc.perform(request);

        String device2Id = "";
        request = MockMvcRequestBuilders.post("/device/user/all").param("token", user2SessionId);
        mvcResult = mvc.perform(request).andReturn();
        String device2String = mvcResult.getResponse().getContentAsString();
        Pattern device2IdPattern = Pattern.compile("deviceId\":\"(.*?)\"");
        Matcher device2IdMatcher = device2IdPattern.matcher(device2String);
        if (device2IdMatcher.find()){
            String deviceIdSegment = device2IdMatcher.group(1);
            assert(deviceIdSegment.length() > 0);
            device2Id = deviceIdSegment;
        }
        else{
            assert(false);
        }

        LinkedMultiValueMap<String,String> params3 = new LinkedMultiValueMap<>();
        params3.add("token",userSessionId);
        params3.add("device",device2Id);
        request = MockMvcRequestBuilders.post("/device/get").params(params3);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("not match"));

        LinkedMultiValueMap<String,String> params4 = new LinkedMultiValueMap<>();
        params4.add("token",user2SessionId);
        params4.add("device",device2Id);
        request = MockMvcRequestBuilders.post("/device/delete").params(params4);
        mvc.perform(request).andExpect(status().isOk());
        signinController.innerDeleteUser("test_default_username2");
    }

    @Test
    public void test_g_fiwareInfo() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        request = MockMvcRequestBuilders.post("/fiware/info").params(params);
        mvc.perform(request)
            .andExpect(status().isOk());
    }

    @Test
    public void test_z_innerClean_UserDevice() throws Exception{
        disposeUser();
    }

}
