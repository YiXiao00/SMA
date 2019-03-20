package com.smarthome.sso.web.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Task2Repository extends MongoRepository<Task2, String> {

    List<Task2> findAll();

}
