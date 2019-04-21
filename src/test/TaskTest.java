import com.smarthome.sso.MainApplication;
import com.smarthome.sso.web.controller.FiwareController;
import com.smarthome.sso.web.controller.MainController;
import com.smarthome.sso.web.controller.SigninController;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskTest {

    private MockMvc mvc;

    private static String userSessionId = "";
    private static String defaultDeviceId = "";
    private static String task1Id = "";

    @Autowired
    private MainController mainController;

    @Autowired
    private SigninController signinController;

    @Autowired
    private FiwareController fiwareController;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(mainController, signinController, fiwareController).build();
    }
    private void initializeUserDevice() throws Exception {
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("name","test_default_username");
        params.add("pwd","test_default_password");
        request = MockMvcRequestBuilders.post("/user/signup").params(params);
        mvc.perform(request);
        request = MockMvcRequestBuilders.post("/user/signin").params(params);
        MvcResult mvcResult = mvc.perform(request).andReturn();
        userSessionId = mvcResult.getResponse().getCookie("sessionId").getValue();

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",userSessionId);
        params2.add("type","test_default_device_type");
        request = MockMvcRequestBuilders.post("/device/add").params(params2);
        mvc.perform(request);
        request = MockMvcRequestBuilders.post("/device/user/all").param("token", userSessionId);
        mvcResult = mvc.perform(request)
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern deviceIdPattern = Pattern.compile("deviceId\":\"(.*?)\"");
        Matcher deviceIdMatcher = deviceIdPattern.matcher(deviceString);
        if (deviceIdMatcher.find()){
            defaultDeviceId = deviceIdMatcher.group(1);
        }
    }

    private void disposeUserDeviceTask() throws Exception{

    }

    @Test
    public void test_a1_addTask1_instant() throws Exception{
        initializeUserDevice();
        mainController.innerDisableTask1Scheduler();

        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        params.add("type","test_default_task1_type");
        params.add("in","0");
        params.add("duration","0");
        request = MockMvcRequestBuilders.post("/task/add").params(params);
        mvc.perform(request).andExpect(status().isOk())
                            .andExpect(content().string("Task added"));
    }

    @Test
    public void test_a2_viewTask1_instant() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        request = MockMvcRequestBuilders.post("/task/user/view").params(params);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String taskString = mvcResult.getResponse().getContentAsString();
        String[] segments = taskString.split(" ");
        assert(segments[2].length() > 0);
        task1Id = segments[2];

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",userSessionId);
        params2.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/get").params(params2);
        mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern statusPattern = Pattern.compile("poweredOn\":(.*?),");
        Matcher statusMatcher = statusPattern.matcher(deviceString);
        if (statusMatcher.find()){
            String status = statusMatcher.group(1);
            assert("false".equals(status));
        }
        else{
            assert(false);
        }
    }

    @Test
    public void test_a3_invokeTask1_instance() throws Exception{
        mainController.innerEnableTask1Scheduler();
        mainController.printOutStatement();

        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("device",defaultDeviceId);
        params.add("task",task1Id);
        request = MockMvcRequestBuilders.post("/task/get").params(params);
        mvc.perform(request)
                        .andExpect(status().isOk())
                        .andExpect(content().string("not found"));

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",userSessionId);
        params2.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/get").params(params2);
        MvcResult mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern statusPattern = Pattern.compile("poweredOn\":(.*?),");
        Matcher statusMatcher = statusPattern.matcher(deviceString);
        if (statusMatcher.find()){
            String status = statusMatcher.group(1);
            assert("true".equals(status));

            request = MockMvcRequestBuilders.post("/device/toggle").params(params2);
            mvc.perform(request).andExpect(status().isOk())
                    .andExpect(content().string("Device toggled"));  // shut down device
        }
        else{
            assert(false);
        }
    }

    @Test
    public void test_b1_addTask1_duration() throws Exception{
        mainController.innerDisableTask1Scheduler();

        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        params.add("type","test_default_task1_type");
        params.add("in","0");
        params.add("duration","5");
        request = MockMvcRequestBuilders.post("/task/add").params(params);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("Task added"));

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",userSessionId);
        request = MockMvcRequestBuilders.post("/task/user/view").params(params2);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String taskString = mvcResult.getResponse().getContentAsString();
        String[] segments = taskString.split(" ");
        assert(segments[2].length() > 0);
        task1Id = segments[2];
    }

    @Test
    public void test_b2_invokeTask1_duration() throws Exception{
        mainController.innerEnableTask1Scheduler();
        mainController.printOutStatement();
        mainController.innerDisableTask1Scheduler();

        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("device",defaultDeviceId);
        params.add("task",task1Id);
        request = MockMvcRequestBuilders.post("/task/get").params(params);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("not found"));

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",userSessionId);
        request = MockMvcRequestBuilders.post("/task/user/view").params(params2);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String taskString = mvcResult.getResponse().getContentAsString();
        String[] segments = taskString.split(" ");
        assert(segments[2].length() > 0);
        assert(!(task1Id.equals(segments[2])));
        task1Id = segments[2];

        LinkedMultiValueMap<String,String> params3 = new LinkedMultiValueMap<>();
        params3.add("token",userSessionId);
        params3.add("device",defaultDeviceId);
        request = MockMvcRequestBuilders.post("/device/get").params(params3);
        mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String deviceString = mvcResult.getResponse().getContentAsString();
        Pattern statusPattern = Pattern.compile("poweredOn\":(.*?),");
        Matcher statusMatcher = statusPattern.matcher(deviceString);
        if (statusMatcher.find()){
            String status = statusMatcher.group(1);
            assert("true".equals(status));
        }
        else{
            assert(false);
        }

    }


    @Test
    public void test_c_deleteTask1() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("taskid",task1Id);
        request = MockMvcRequestBuilders.post("/task/delete").params(params);
        mvc.perform(request).andExpect(status().isOk())
                            .andExpect(content().string("finished"));

        request = MockMvcRequestBuilders.post("/task/user/view").params(params);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String taskString = mvcResult.getResponse().getContentAsString();
        assert(taskString.length() <= 10);
    }

    @Test
    public void test_d_addTask1Failure() throws Exception{
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
        params2.add("device",defaultDeviceId);
        params2.add("type","test_default_task1_type");
        params2.add("in","0");
        params2.add("duration","0");
        request = MockMvcRequestBuilders.post("/task/add").params(params2);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("device not belongs to the user"));

        signinController.innerDeleteUser("test_default_username2");
        LinkedMultiValueMap<String,String> params3 = new LinkedMultiValueMap<>();
        params3.add("name","test_default_username");
        params3.add("pwd","test_default_password");
        request = MockMvcRequestBuilders.post("/user/signin").params(params3);
        mvc.perform(request);
    }

    @Test
    public void test_e_multipleTask1() throws Exception{
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("token",userSessionId);
        params.add("device",defaultDeviceId);
        params.add("type","test_default_task1_type_late");
        params.add("in","5");
        params.add("duration","0");
        request = MockMvcRequestBuilders.post("/task/add").params(params);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("Task added"));

        LinkedMultiValueMap<String,String> params2 = new LinkedMultiValueMap<>();
        params2.add("token",userSessionId);
        params2.add("device",defaultDeviceId);
        params2.add("type","test_default_task1_type_early");
        params2.add("in","0");
        params2.add("duration","0");
        request = MockMvcRequestBuilders.post("/task/add").params(params2);
        mvc.perform(request).andExpect(status().isOk())
                .andExpect(content().string("Task added"));

        LinkedMultiValueMap<String,String> params3 = new LinkedMultiValueMap<>();
        params3.add("token",userSessionId);
        request = MockMvcRequestBuilders.post("/task/user/view").params(params3);
        MvcResult mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
        String taskString = mvcResult.getResponse().getContentAsString();
        String[] lines = taskString.split(",");
        assert(lines.length == 3);

        mainController.innerEnableTask1Scheduler();
        mainController.printOutStatement();

        request = MockMvcRequestBuilders.post("/task/user/view").params(params3);
        mvcResult = mvc.perform(request).andExpect(status().isOk()).andReturn();
        taskString = mvcResult.getResponse().getContentAsString();
        lines = taskString.split(",");
        assert(lines.length == 2);
        String[] segments = lines[0].split(" ");
        assert("test_default_task1_type_late".equals(segments[0]));
    }

    @Test
    public void test_f_addTask2() throws Exception{

    }

    @Test
    public void test_g_viewTask2() throws Exception{

    }

    @Test
    public void test_h_changeTask2() throws Exception{

    }

    @Test
    public void test_i_deleteTask2() throws Exception{

    }

    @Test
    public void test_j_multipleTask2() throws Exception{

    }

    @Test
    public void test_k_addTask2Failure() throws Exception{

    }

    @Test
    public void test_l_task2ContradictSyntax() throws Exception{

    }

    @Test
    public void test_m_task1AndTask2() throws Exception{

    }

    @Test
    public void test_z_innerClean_UserDeviceAllTasks() throws Exception{

    }

}
