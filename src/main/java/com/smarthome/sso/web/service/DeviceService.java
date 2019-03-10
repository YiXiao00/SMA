package com.smarthome.sso.web.service;



import com.smarthome.sso.web.constants.ServiceResult;
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

    public Device findDeviceByDeviceId(String deviceId){
        return deviceRespository.findByDeviceId(deviceId);
    }

    public List<Device> findDevicesByUserId(String userId){
        return deviceRespository.findAllByUserId(userId);
    }

    public List<Device> findAllDevices() { return deviceRespository.findAll();}

    public void deleteSelf() {deviceRespository.deleteAll();}

    public Integer getDeviceCountByUserId(String userId){
        return deviceRespository.getDeviceCountFromUserId(userId);
    }

    public ServiceResult toggleDevice(String deviceId){
        Device device = deviceRespository.findByDeviceId(deviceId);
        if (device == null){
            return ServiceResult.SERVICE_NOTFOUND;
        }
        device.toggle();
        deviceRespository.save(device);
        return ServiceResult.SERVICE_SUCCESS;
    }
}