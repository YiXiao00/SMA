package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.Task;
import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.service.DeviceService;
import com.smarthome.sso.web.service.TaskService;
import com.smarthome.sso.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;


@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceService bufferDS;
    @Autowired
    private TaskService taskService;

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

    @PostMapping("/user/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            String name = String.valueOf(request.getParameter("name"));
            String pwd = String.valueOf(request.getParameter("pwd"));
            User foundUser = userService.findOneUserByUsername(name);
            if (foundUser == null) {
                return ResponseEntity.ok("This user does not exist.");
            }
            if (!foundUser.getPassword().equals(pwd)) {
                return ResponseEntity.ok("The password given is not correct. Cannot delete");
            }

            String id = foundUser.getUserId();
            List<Device> dList = deviceService.findAllDevices();
            for(int i=0;i<dList.size();i++){
                Device d = dList.get(i);
                if(d.getUserId().equals(id)) {
                    deviceService.deleteDevice(d);
                }
            }

            List<Task> tList = taskService.findAllTasks();
            for(int i=0;i<tList.size();i++){
                Task t = tList.get(i);
                if(t.getUserId().equals(name)) {
                    taskService.deleteTask(t);
                }
            }

            userService.deleteOneUserByUserId(id);
            return ResponseEntity.ok("User " + name + " has been deleted.");
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
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        String type = String.valueOf(request.getParameter("type"));
        User foundUser = userService.findOneUserByUsername(name);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct. Cannot add device");
        }
        Device newDevice = Device.builder().userId(foundUser.getUserId()).type(type).relativeUserId(foundUser.getDevicesOwned()).build();
        foundUser.addDevice();
        userService.deleteUser(foundUser);
        userService.addOneUser(foundUser);
        deviceService.addOneDevice(newDevice);
        return ResponseEntity.ok("Added new device to user "+foundUser.getUserId()+" of type "+type);

    }

    @PostMapping("/deleteAllDevices")
    @ResponseBody
    public ResponseEntity<?> restartDeviceDB(HttpServletRequest request, HttpServletResponse response) throws Exception{
        deviceService.deleteSelf();
        return ResponseEntity.ok("good");
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
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(name);
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

                }}else{
                Device d = dList.get(i);
                bufferDS.addOneDevice(d);
            }
        }

        List<Task> tList = taskService.findAllTasks();
        for(int i=0;i<tList.size();i++){
            Task t = tList.get(i);
            if(t.getUserId().equals(name) && t.getDeviceId()==relativeID) {
                taskService.deleteTask(t);
            }
            if(t.getUserId().equals(name) && t.getDeviceId()>relativeID){
                taskService.deleteTask(t);
                t.decrementDeviceId();
                taskService.addOneTask(t);
            }
        }

        foundUser.removeDevice();
        userService.deleteUser(foundUser);
        userService.addOneUser(foundUser);
        deviceService =bufferDS;
        return ResponseEntity.ok("Device deleted");

    }

    @PostMapping("/device/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(name);
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
                    bufferDS.addOneDevice(d);

                }}else{
                Device d = dList.get(i);
                bufferDS.addOneDevice(d);
            }
            List<Device> bufferList = bufferDS.findAllDevices();
        }
        deviceService =bufferDS;
        return ResponseEntity.ok("Device toggled");

    }
    @PostMapping("/device/user/all")
    @ResponseBody
    public ResponseEntity<?> showUserDevices(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
                    System.out.println("Cookie found");
                }
            }
        }
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(name);
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

    @RequestMapping("/task/view")
    @ResponseBody
    public ResponseEntity<?> findTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Task> tList = taskService.findAllTasks();
        String s = "";
        for(int i=0; i<tList.size();i++){
                Task t = tList.get(i);
                s = s + t.getType() + " forUser " + t.getUserId() +" device "+t.getRelativeDeviceId()+" atTime "+t.getCalendar().getTime() +" for "+t.getDuration()+", ";

        }
        return ResponseEntity.ok(s);

    }
    @RequestMapping("/task/user/view")
    @ResponseBody
    public ResponseEntity<?> findUserTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Task> tList = taskService.findAllTasks();
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(name);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct.");
        }
        String s = "";
        for(int i=0; i<tList.size();i++){
            Task t = tList.get(i);
            if(t.getUserId().equals(name)) {
                s = s + t.getType() + " forUser " + t.getUserId() + " device " + t.getRelativeDeviceId() + " atTime " + t.getCalendar().getTime() + " for " + t.getDuration() + " , ";
            }
        }
        return ResponseEntity.ok(s);

    }

    @RequestMapping("/task/delete")
    @ResponseBody
    public ResponseEntity<?> deleteTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Task> tList = taskService.findAllTasks();
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        String type = String.valueOf(request.getParameter("type"));
        int ruid = Integer.valueOf(request.getParameter("relUID"));
        User foundUser = userService.findOneUserByUsername(name);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct.");
        }
        int tasksDeleted=0;
        for(int i=0;i<tList.size();i++){
            Task t = tList.get(i);
            if(t.getUserId().equals(name) && t.getType().equals(type) && t.getDeviceId()==(ruid)){
                taskService.deleteTask(t);
                tasksDeleted++;

            }
        }
        if(tasksDeleted==0) return ResponseEntity.ok("No matching tasks found");
        return ResponseEntity.ok("Deleted " +tasksDeleted+" tasks.");

    }


    @RequestMapping("/task/delete/all")
    @ResponseBody
    public ResponseEntity<?> deleteAllTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        taskService.deleteSelf();
        return ResponseEntity.ok("Deleted task list");

    }

    @RequestMapping("/task/add")
    @ResponseBody
    public ResponseEntity<?> addTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String s = "";
        String name = String.valueOf(request.getParameter("name"));
        String pwd = String.valueOf(request.getParameter("pwd"));
        User foundUser = userService.findOneUserByUsername(name);
        if (foundUser == null) {
            return ResponseEntity.badRequest().body("User does not exist.");
        }
        if (!foundUser.getPassword().equals(pwd)) {
            return ResponseEntity.badRequest().body("The password given is not correct. Cannot add Task");
        }
        int ruid = Integer.valueOf(request.getParameter("relUID"));
        if(!(ruid >=0) || !(ruid < foundUser.getDevicesOwned()) )
            return ResponseEntity.badRequest().body("Device does not exist");
        String type = String.valueOf(request.getParameter("type"));
        int inThisTime = Integer.valueOf(request.getParameter("in"));
        int duration = Integer.valueOf(request.getParameter("duration"));
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.MINUTE,inThisTime);
        Task t = new Task(type,name,ruid,c1,duration);

        List<Task> tList = taskService.findAllTasks();

        taskService.deleteSelf();
        boolean placedNew = false;
        Task oldT;

        System.out.println("tasks in list "+tList.size());

        for(int i=0;i<tList.size()+1;i++){
            if(tList.size()!=0) {
                if (placedNew || (i == tList.size())) {
                    oldT = tList.get(i - 1);
                } else {
                    oldT = tList.get(i);
                }
                if (c1.after(oldT.getCalendar()) || placedNew) {
                    taskService.addOneTask(oldT);
                } else {
                    taskService.addOneTask(t);
                    placedNew = true;
                }
            }else{
                taskService.addOneTask(t);
            }

        }

        taskService.addOneTask(t);
        return ResponseEntity.ok("Task added");

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
