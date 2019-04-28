package com.smarthome.sso.web.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Task2Repository extends MongoRepository<Task2, String> {

    List<Task2> findAll();

    List<Task2> findTask2sByDeviceId(String deviceId);

    Task2 findByTaskId(String taskId);

}
