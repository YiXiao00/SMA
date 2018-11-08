package com.smarthome.sso.web.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRespository extends MongoRepository<Device, String> {

    User findByUserId(Integer id);


}
