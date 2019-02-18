package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SigninController {

    @Autowired
    private UserService userService;


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
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        if (userService.findOneUserByUsername(name) != null){
            return ResponseEntity.ok("The username has been used by another user.");
        }
        User newUser = User.builder().username(name).password(pwd).build();
        User addUser = userService.addOneUser(newUser);
        return ResponseEntity.ok("Signed up successfully.");
    }

    @PostMapping("/user/signin")
    @ResponseBody
    public ResponseEntity<?> logIn(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputSession = "";
        Cookie[] cookies = request.getCookies();
        if (!(cookies == null)){
            for (Cookie cookie : cookies){
                System.out.println("cookie getname "+cookie.getName());
                if ("sessionId".equals(cookie.getName())){
                    inputSession = cookie.getValue();
                    break;
                }
            }
            if (!("".equals(inputSession))){
                ServiceResult sessionVerify = userService.verifySessionId(inputSession);
                if (sessionVerify == ServiceResult.SERVICE_SUCCESS){
                    return ResponseEntity.ok("already signed in");
                }
            }
        }
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        ServiceResult result = userService.tryLogIn(username,pwd);
        if (result == ServiceResult.SERVICE_SUCCESS){
            System.out.println("Match found");
            String sessionId = userService.createToken(60);
            response.addCookie(new Cookie("sessionId",sessionId));

            return ResponseEntity.ok("succeeded");
        }
        return ResponseEntity.ok("failed");
    }

}
