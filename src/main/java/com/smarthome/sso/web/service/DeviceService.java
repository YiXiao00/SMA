package com.smarthome.sso.web.service;



import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.DeviceRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRespository deviceRespository;

    public void addOneDevice(Device d){
        deviceRespository.save(d);
    }
    public void deleteDevice(Device d){
        deviceRespository.delete(d);
    }

    public List<Device> findAllDevices() { return deviceRespository.findAll();}

    public void deleteSelf() {deviceRespository.deleteAll();}

    public Integer getDeviceCountByUserId(String userId){
        return deviceRespository.getDeviceCountFromUserId(userId);
    }

}