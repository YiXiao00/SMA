package com.smarthome.sso.web.service;



import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.DeviceRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRespository drepo;

    public void insertDevice(DeviceRespository dRepo, Device d){
        dRepo.save(d);
    }


    public Device searchDevicesById(DeviceRespository dRepo, String id){
        Optional<Device> d = dRepo.findById(id);
        if(d.isPresent()) return d.get();
        return null;
    }


    public void deleteDevicesById(DeviceRespository dRepo, String id){
        Optional<Device> d = dRepo.findById(id);
        if(d.isPresent()){
            dRepo.delete(d.get());
        }
    }

}