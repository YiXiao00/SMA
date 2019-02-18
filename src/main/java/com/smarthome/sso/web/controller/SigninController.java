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

    /**
     *
     * Debug function used for testing - returns username when given user id.
     *
     *
     *
     */

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

    /**
     *
     * Takes a username and password and check that username is not currently in use.
     * Creates new username with name + pwd given and adds to the userService.
     *
     *
     */


    @PostMapping("/user/signup")
    @ResponseBody
    public ResponseEntity<?> registerOneNewUser(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        if (userService.findOneUserByUsername(name) != null){
            return ResponseEntity.ok("The username has been used by another user.");
        }
        User newUser = new User(name,pwd);
        userService.addOneUser(newUser);
        return ResponseEntity.ok("Signed up successfully.");
    }

    /**
     *
     * Checks if the requests cookie matches any of the cookies attached to a session.
     * Failing that, takes the username and password and checks they are a match on the database.
     * If they are successful, a cookie is added to the session cookies for the if the user logs in again.
     *
     */

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
