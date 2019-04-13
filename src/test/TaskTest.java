import com.smarthome.sso.MainApplication;
import com.smarthome.sso.web.controller.FiwareController;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MainApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskTest {

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
    private void initializeUsersAndDevices() throws Exception {

    }

    private void disposeUsersAndDevices() throws Exception{

    }

    @Test
    public void test_a_addTask1() throws Exception{

    }

    @Test
    public void test_b_viewTask1() throws Exception{

    }

    @Test
    public void test_c_deleteTask1() throws Exception{

    }

    @Test
    public void test_d_addTask1Failure() throws Exception{

    }

    @Test
    public void test_e_multipleTask1() throws Exception{

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
