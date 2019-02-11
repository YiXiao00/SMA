package com.smarthome.sso.web.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    Task deleteByTaskId(String id);
    void delete(Task t);
}
