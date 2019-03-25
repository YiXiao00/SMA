
import com.smarthome.sso.MainApplication;
import com.smarthome.sso.web.controller.SigninController;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import sun.reflect.annotation.ExceptionProxy;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAccessTest {

    private MockMvc mvc;

    private String receivedSessionId = "";

    @Autowired
    private SigninController signinController;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(signinController).build();
    }

    @Test
    public void test_a_signUpUser() throws Exception {
        RequestBuilder request;
        request = MockMvcRequestBuilders.post("/user/signup")
                .param("name","test_default_username")
                .param("pwd","test_default_password");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("Signed up successfully."));

    }

    @Test
    public void test_b_signUpDuplicateUser() throws Exception {
        RequestBuilder request;
        request = MockMvcRequestBuilders.post("/user/signup")
                .param("name","test_default_username")
                .param("pwd","test_default_password");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("The username has been used by another user."));

    }

    @Test
    public void test_c_signInEmptyUser() throws Exception {
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("name","test_empty_username");
        params.add("pwd","test_empty_password");
        request = post("/user/signin").params(params);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("failed"));
    }

    @Test
    public void test_d_signInWrongPassword() throws Exception {
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("name","test_default_username");
        params.add("pwd","test_wrong_password");
        request = post("/user/signin").params(params);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("failed"));
    }

    @Test
    public void test_e_signInCorrectUser() throws Exception {
        RequestBuilder request;
        LinkedMultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("name","test_default_username");
        params.add("pwd","test_default_password");
        request = post("/user/signin").params(params);
        MvcResult mvcResult = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("succeeded"))
                .andExpect(cookie().exists("sessionId"))
                .andReturn();
        String tmpSessionId = mvcResult.getResponse().getCookie("sessionId").getValue();
        assert(tmpSessionId.length() > 0);
        receivedSessionId = tmpSessionId;
    }

    @Test
    public void test_f_innerCleanUser() throws Exception{
        signinController.innerDeleteUser("test_default_username");

    }


}
