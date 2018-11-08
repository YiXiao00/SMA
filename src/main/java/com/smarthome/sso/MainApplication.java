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
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    public void run(String... args) throws Exception{
        System.out.println("Working!");


    }

    public void insertDevice(DeviceRespository dRepo, Device d){
        dRepo.save(d);
    }

    public void insertUser(UserRepository uRepo, User u){
        uRepo.save(u);
    }

    public Device searchDevicesById(DeviceRespository dRepo, String id){
        Optional<Device> d = dRepo.findById(id);
        if(d.isPresent()) return d.get();
        return null;
    }

    public User searchUsersByID(UserRepository uRepo, String id){
        Optional<User> u = uRepo.findById(id);
        if(u.isPresent()) return u.get();
        return null;
    }

    public void deleteDevicesById(DeviceRespository dRepo, String id){
        Optional<Device> d = dRepo.findById(id);
        if(d.isPresent()){
            dRepo.delete(d.get());
        }
    }

    public void deleteUsersById(UserRepository uRepo, String id){
        Optional<User> u = uRepo.findById(id);
        if(u.isPresent()){
            uRepo.delete(u.get());
        }
    }
}
