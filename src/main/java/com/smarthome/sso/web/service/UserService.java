package com.smarthome.sso.web.service;


import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository uRepo;


    public void insertUser(User u){
        uRepo.save(u);
    }

    public void deleteUsersById(String id){
        Optional<User> u = uRepo.findById(id);
        if(u.isPresent()){
            uRepo.delete(u.get());
        }
    }

    public void deleteAllUsers(UserRepository uRepo){
        uRepo.deleteAll();
    }



    public User searchUsersByID(String id){
        Optional<User> u = uRepo.findById(id);
        if(u.isPresent()) return u.get();
        return null;
    }




}
