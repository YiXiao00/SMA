package com.smarthome.sso.web.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    Task deleteByTaskId(String id);

    void delete(Task t);

    List<Task> findByDeviceId(String deviceId);

    List<Task> findAllByOrderByCalendarAsc();

}
