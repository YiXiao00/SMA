package com.smarthome.sso.web.domain;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    void delete(User u);

    User findByUserId(String id);

    User findByUsername(String name);

    User deleteByUserId(String id);

    void deleteByUsername(String username);


}
