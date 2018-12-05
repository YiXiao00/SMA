package com.smarthome.sso;


import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.DeviceRespository;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Optional;

@EnableSwagger2
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MainApplication implements CommandLineRunner {

    @Autowired
    private UserRepository uRepo;
    @Autowired
    private DeviceRespository dRepo;
    public int idCounter;
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    public void run(String... args) throws Exception{

    }








}
