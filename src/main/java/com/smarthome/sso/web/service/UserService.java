package com.smarthome.sso.web.service;


import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository uRepo;


    public void insertUser(User u){
        uRepo.save(u);
    }

    public void deleteUsersById(String id){
        User u = uRepo.findByUserId(id);
        if(u != null){
            uRepo.delete(u);
        }
    }

    public void deleteAllUsers(UserRepository uRepo){
        uRepo.deleteAll();
    }

    public User searchUsersByID(String id){
        User u = uRepo.findByUserId(id);
        return u;

    }




}
