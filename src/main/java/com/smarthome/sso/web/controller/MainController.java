package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.service.DeviceService;
import com.smarthome.sso.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceService bufferDS;

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
            return ResponseEntity.ok("The username has been used by another user.");
        }
        User newUser = User.builder().username(username).password(pwd).build();
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
            String sessionId = userService.createToken(60);
            response.addCookie(new Cookie("sessionId",sessionId));
            return ResponseEntity.ok("succeeded");
        }
        return ResponseEntity.ok("failed");
    }

    @PostMapping("/user/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            String username = String.valueOf(request.getParameter("name"));
            String pwd = String.valueOf(request.getParameter("pwd"));
            User foundUser = userService.findOneUserByUsername(username);
            if (foundUser == null) {
                return ResponseEntity.ok("This user does not exist.");
            }
            if (!foundUser.getPassword().equals(pwd)) {
                return ResponseEntity.ok("The password given is not correct. Cannot delete");
            }

            String id = foundUser.getUserId();
            userService.deleteOneUserByUserId(id);
            return ResponseEntity.ok("User " + username + " has been deleted.");
        }
        catch (Exception e){
            return ResponseEntity.ok("User has been deleted.");
        }
    }

    @PostMapping("/user/all")
    @ResponseBody
    public ResponseEntity<?> showUsers(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<User> uList = userService.findAllUsers();
        String s = "";
        for(int i=0; i<uList.size();i++){
            User u = uList.get(i);
            s = s + u.getUsername() +", ";
        }
        return ResponseEntity.ok(s);
    }

    @PostMapping("/device/add")
    @ResponseBody
    public ResponseEntity<?> addDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        String type = String.valueOf(request.getParameter("type"));
        User foundUser = userService.findOneUserByUsername(username);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct. Cannot add device");
        }
        Device newDevice = Device.builder().userId(foundUser.getUserId()).type(type).relativeUserId(foundUser.getDevicesOwned()).build();
        foundUser.addDevice();
        deleteUser(request,response);
        userService.addOneUser(foundUser);
        deviceService.addOneDevice(newDevice);
        return ResponseEntity.ok("Added new device to user "+foundUser.getUserId()+" of type "+type);

    }

    @PostMapping("/device/all")
    @ResponseBody
    public ResponseEntity<?> showAllDevices(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String admin = String.valueOf(request.getParameter("admin"));
        if(admin.equals("admin")) {
            List<Device> dList = deviceService.findAllDevices();
            String s = "";
            for (int i = 0; i < dList.size(); i++) {
                Device d = dList.get(i);
                s = s + d.getType() + " owned by " + d.getUserId() + " ,";
            }
            return ResponseEntity.ok(s);
        }else{
        return ResponseEntity.badRequest().body("Needs admin permission");
    }
    }

    @PostMapping("/device/delete")
    @ResponseBody
    public ResponseEntity<?> deleteDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(username);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct. Cannot add device");
        }
        int relativeID = Integer.valueOf(request.getParameter("relUID"));
        List<Device> dList = deviceService.findAllDevices();
        bufferDS.deleteSelf();
        boolean removedDevice = false;
        for(int i=0; i<dList.size();i++){
            if(dList.get(i).getUserId().equals(foundUser.getUserId())) {
                if(dList.get(i).getRelativeUserId()==relativeID) {
                    removedDevice = true;
                }else {
                    Device d = dList.get(i);
                    if(removedDevice) d.setRelativeUserId(d.getRelativeUserId()-1);
                    bufferDS.addOneDevice(d);

                }}
        }
        deviceService =bufferDS;
        return ResponseEntity.ok("Device deleted");

    }

    @PostMapping("/device/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(username);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct. Cannot add device");
        }
        int relativeID = Integer.valueOf(request.getParameter("relUID"));
        List<Device> dList = deviceService.findAllDevices();
        bufferDS.deleteSelf();
        boolean toggledDevice = false;
        for(int i=0; i<dList.size();i++){
            if(dList.get(i).getUserId().equals(foundUser.getUserId())) {
                if(dList.get(i).getRelativeUserId()==relativeID) {
                    toggledDevice = true;
                    Device d = dList.get(i);
                    d.toggle();
                    bufferDS.addOneDevice(d);
                }else {
                    Device d = dList.get(i);
                    if(toggledDevice) d.setRelativeUserId(d.getRelativeUserId()-1);
                    bufferDS.addOneDevice(d);

                }}
        }
        deviceService =bufferDS;
        return ResponseEntity.ok("Device toggled");

    }
    @PostMapping("/device/user/all")
    @ResponseBody
    public ResponseEntity<?> showUserDevices(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(username);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct. Cannot add device");
        }
        List<Device> dList = deviceService.findAllDevices();
        String s = "";
        for(int i=0; i<dList.size();i++){
            if(dList.get(i).getUserId().equals(foundUser.getUserId())) {
                Device d = dList.get(i);
                s = s + d.getType() + " relativeUID " + d.getRelativeUserId() +" CurrentlyOn "+d.getPowerStatus() +" , ";
            }
        }
        return ResponseEntity.ok(s);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteAllDatabases(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String admin = String.valueOf(request.getParameter("admin"));
        if(admin.equals("admin")) {
            userService.deleteSelf();
            deviceService.deleteSelf();
            return ResponseEntity.ok("Deleted");
        }else{
            return ResponseEntity.badRequest().body("Needs admin permission");
        }
    }



    @RequestMapping(value="/homepage", method = {RequestMethod.POST, RequestMethod.GET})
    public String getHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/home.html";
    }

    @RequestMapping(value="/loginLanding", method = {RequestMethod.POST, RequestMethod.GET})
    public String getLoginlanding(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/loginLanding.html";
    }

    @RequestMapping(value="/room", method = {RequestMethod.POST, RequestMethod.GET})
    public String getRoom(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/room.html";
    }


}
