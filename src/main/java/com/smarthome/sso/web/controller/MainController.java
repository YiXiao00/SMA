package com.smarthome.sso.web.controller;

import com.mongodb.BasicDBObject;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.service.DeviceService;
import com.smarthome.sso.web.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.bson.BasicBSONObject;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/user/info")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String id = String.valueOf(request.getParameter("id"));
        User user = userService.findOneUserByUserID(id);
        if (user == null){
            return ResponseEntity.badRequest().body("No Result Found!");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/user/signup")
    @ResponseBody
    public ResponseEntity<?> registerOneNewUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        if (userService.findOneUserByUsername(username) != null){
            return ResponseEntity.badRequest().body("The username has been used by another user.");
        }
        User newUser = User.builder().username(username).password(pwd).build();
        User addUser = userService.addOneUser(newUser);
        return ResponseEntity.ok("Signed up successfully.");
    }

    @PostMapping("/user/signin")
    @ResponseBody
    public ResponseEntity<?> logIn(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User tryUser = userService.findOneUserByUsername(username);
        if (tryUser == null){
            return ResponseEntity.badRequest().body("Username or password not correct.");
        }
        if (tryUser.getPassword().equals(pwd)){
            return ResponseEntity.ok("Logged in.");
        }
        return ResponseEntity.badRequest().body("Username or password not correct.");
    }

    @RequestMapping(value="/homepage", method = {RequestMethod.POST, RequestMethod.GET})
    public String getHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/home.html";
    }

}
