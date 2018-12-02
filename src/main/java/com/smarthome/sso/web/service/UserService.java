package com.smarthome.sso.web.service;


import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  Service for user database
 *
 *  Last modify: 181202
 * */
@Service
public class UserService {

    @Autowired
    private UserRepository uRepo;

    public User addOneUser(User u){
        return uRepo.save(u);
    }

    public ServiceResult deleteOneUserByUserId(String id){
        User result = uRepo.deleteByUserId(id);
        if (result != null){
            return ServiceResult.SERVICE_SUCCESS;
        }
        return ServiceResult.SERVICE_NOTFOUND;
    }

//    public void deleteAllUsers(UserRepository uRepo){
//        uRepo.deleteAll();
//    }

    public User findOneUserByUserID(String id){
        return uRepo.findByUserId(id);
    }

    public User findOneUserByUsername(String name){
        return uRepo.findByUsername(name);
    }



}
