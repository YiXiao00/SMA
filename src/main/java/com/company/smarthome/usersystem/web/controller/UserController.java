package com.company.smarthome.usersystem.web.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class UserController {

    @ApiOperation(value = "Sign up a new user")
    @GetMapping("/user/signup")
    public ResponseEntity<?> userSignUp(HttpServletRequest request, HttpServletResponse response) throws Exception{

        throw new NotImplementedException();

    }

}
