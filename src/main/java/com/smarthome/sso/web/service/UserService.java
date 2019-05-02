package com.smarthome.sso.web.service;


import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *  Service for user database
 *
 *  Last modify: 181202
 * */
@Service
public class UserService {

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private RedisTemplate redisTemplate;

    public User addOneUser(User u){
        return uRepo.save(u);
    }

    public void deleteUser(User u){ uRepo.delete(u); }

    public ServiceResult deleteOneUserByUserId(String id){
        User result = uRepo.deleteByUserId(id);
        if (result != null){
            return ServiceResult.SERVICE_SUCCESS;
        }
        return ServiceResult.SERVICE_NOTFOUND;
    }

    public void deleteOneUserByUsername(String username){
        uRepo.deleteByUsername(username);
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

    public List<User> findAllUsers() {  return uRepo.findAll(); }

    public void deleteSelf(){ uRepo.deleteAll(); }

    public ServiceResult tryLogIn(String username, String pwd){
        if (username == null){return ServiceResult.SERVICE_FAIL;}
        if ("".equals(username)){return ServiceResult.SERVICE_FAIL;}
        User tryUser = uRepo.findByUsername(username);
        if (tryUser == null){
            return ServiceResult.SERVICE_NOTFOUND;
        }
        if (tryUser.getPassword().equals(pwd)){
            return ServiceResult.SERVICE_SUCCESS;
        }
        return ServiceResult.SERVICE_FAIL;
    }

    public ServiceResult tryLogOut(String sessionId){
        return deleteToken(sessionId);
    }

    public String createToken(Integer timeSpan, String username){
        String sessionId = UUID.randomUUID().toString();
        try {
            redisTemplate.opsForValue().set(sessionId, username, timeSpan, TimeUnit.MINUTES);
        }
        catch(Exception ex){
            return "";
        }
        return sessionId;
    }

    private ServiceResult deleteToken(String key){
        try {
            redisTemplate.delete(key);
        }
        catch(Exception ex){
            return ServiceResult.SERVICE_FAIL;
        }
        return ServiceResult.SERVICE_SUCCESS;
    }

    public ServiceResult verifySessionId(String sessionId){
        String record = (String)redisTemplate.opsForValue().get(sessionId);
        if (!("".equals(record))){
            return ServiceResult.SERVICE_SUCCESS;
        }
        return ServiceResult.SERVICE_FAIL;
    }

    public String getUsernameFromSessionId(String sessionId){
        String record = (String)redisTemplate.opsForValue().get(sessionId);
        return record;
    }

    private String convertByte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String tmp = null;
        for (int i = 0; i <= bytes.length-1; i++){
            tmp = Integer.toHexString(bytes[i] & 0xFF);
            if (tmp.length() == 1){
                stringBuffer.append("0");
            }
            stringBuffer.append(tmp);
        }
        return stringBuffer.toString();
    }

    public String getSHA256(String content) throws Exception{
        MessageDigest messageDigest;
        String result = "";
        messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(content.getBytes("UTF-8"));
        result = convertByte2Hex(messageDigest.digest());
        return result;
    }

}
