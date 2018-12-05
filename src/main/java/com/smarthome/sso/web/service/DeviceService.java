package com.smarthome.sso.web.service;



import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.DeviceRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRespository dRepo;

    public void addOneDevice(Device d){
        dRepo.save(d);
    }

}