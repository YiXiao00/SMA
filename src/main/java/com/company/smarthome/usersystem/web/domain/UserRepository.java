package com.company.smarthome.usersystem.web.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

/** mongodb persistence automatically done by Spring Boot */
public interface UserRepository extends MongoRepository<User, Integer> {

    User findByUserId(Integer id);

}
