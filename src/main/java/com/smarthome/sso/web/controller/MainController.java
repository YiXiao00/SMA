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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.task.TaskExecutor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Controller
@EnableScheduling
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private TaskService taskService;


    //Procedure to delete a user, all of their devices and associated tasks from the database
    @PostMapping("/user/delete")
    @ResponseBody
    public ResponseEntity<?> deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try {
            String name = String.valueOf(request.getParameter("name"));
            String pwd = String.valueOf(request.getParameter("pwd"));
            User foundUser = userService.findOneUserByUsername(name);
            if (foundUser == null) {
                return ResponseEntity.ok("No user found with username: " + name);
            }
            if (!foundUser.getPassword().equals(pwd)) {
                return ResponseEntity.ok("Password does not match for: " + name);
            }

            String id = foundUser.getUserId();
            List<Device> dList = deviceService.findAllDevices();
            for(int i=0;i<dList.size();i++){
                Device d = dList.get(i);
                if(d.getUserId().equals(id)) {
                    deviceService.deleteDevice(d);
                }
            }

            System.out.println("All devices associated with "+name+" have been deleted.");

//            List<Task> tList = taskService.findAllTasks();
//            for(int i=0;i<tList.size();i++){
//                Task t = tList.get(i);
//                if(t.getUserId().equals(name)) {
//                    taskService.deleteTask(t);
//                }
//            }

            System.out.println("All tasks associated with "+name+" have been deleted");

            System.out.println(name+" has been deleted.");

            userService.deleteOneUserByUserId(id);
            return ResponseEntity.ok("User " + name + " has been deleted.");
        }
        catch (Exception e){
            return ResponseEntity.ok("User has been deleted.");
        }
    }




    //Returns a string with all the usernames comma separated
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






    //Adds a new device associated with the user
    @PostMapping("/device/add")
    @ResponseBody
    public ResponseEntity<?> addDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);
        String type = String.valueOf(request.getParameter("type"));

        Device newDevice = Device.builder().userId(foundUser.getUserId()).type(type).build();
        deviceService.addOneDevice(newDevice);
        System.out.println("Added new device to user "+foundUser.getUserId()+" of type "+type);

        return ResponseEntity.ok("Added new device to user "+foundUser.getUserId()+" of type "+type);

    }







    //Deletes all devices, used for debugging
    @PostMapping("/deleteAllDevices")
    @ResponseBody
    public ResponseEntity<?> restartDeviceDB(HttpServletRequest request, HttpServletResponse response) throws Exception{
        deviceService.deleteSelf();
        System.out.println("All devices have been deleted");
        return ResponseEntity.ok("good");
    }






    //Returns list of all devices and their owners, requires admin permission
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





    //Deletes a device and all tasks associated with it
    @PostMapping("/device/delete")
    @ResponseBody
    public ResponseEntity<?> deleteDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        String deviceId = String.valueOf(request.getParameter("device"));
        Device target = deviceService.findDeviceByDeviceId(deviceId);

        if (!foundUser.getUserId().equals(target.getUserId())){
            return ResponseEntity.ok("device not belongs to the user");
        }

        deviceService.deleteDevice(target);

        List<Task> taskList = taskService.findTasksByDeviceId(deviceId);
        taskService.deleteTasks(taskList);

        System.out.println("Device deleted");
        return ResponseEntity.ok("Device deleted");

    }


    //Toggles on or off one device, must be owned by the user
    @PostMapping("/device/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        String deviceId = String.valueOf(request.getParameter("device"));
        Device target = deviceService.findDeviceByDeviceId(deviceId);

        if (!foundUser.getUserId().equals(target.getUserId())){
            return ResponseEntity.ok("device not belongs to the user");
        }

        target.setPoweredOn(!target.getPowerStatus());
        deviceService.addOneDevice(target);

        System.out.println("Device toggled");
        return ResponseEntity.ok("Device toggled");

    }





    //Shows all devices owned by the user
    @PostMapping("/device/user/all")
    @ResponseBody
    public ResponseEntity<?> showUserDevices(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        List<Device> deviceList = deviceService.findDevicesByUserId(foundUser.getUserId());
        if (deviceList == null){
            return ResponseEntity.ok("");
        }
        String s = "";
        for(int i=0; i<deviceList.size();i++){
            if(deviceList.get(i).getUserId().equals(foundUser.getUserId())) {
                Device d = deviceList.get(i);
                s = s + d.getType() + " deviceId " + d.getDeviceId() +" CurrentlyOn "+d.getPowerStatus() +" , ";
            }
        }
        return ResponseEntity.ok(s);
    }

    //Deletes user, device and task service databases
    @RequestMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteAllDatabases(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String admin = String.valueOf(request.getParameter("admin"));
        if(admin.equals("admin")) {
            userService.deleteSelf();
            deviceService.deleteSelf();
            taskService.deleteSelf();
            System.out.println("All databases reset");
            return ResponseEntity.ok("Deleted");
        }else{
            return ResponseEntity.badRequest().body("Needs admin permission");
        }
    }

    //Returns a list of all tasks with their associated user, device and time
    @RequestMapping("/task/view")
    @ResponseBody
    public ResponseEntity<?> findTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<Task> tList = taskService.findAllTasks();
        String s = "";
        for(int i=0; i<tList.size();i++){
                Task t = tList.get(i);
                s = s + t.getType() + " taskId " + t.getTaskId()+" atTime "+t.getCalendar().getTime() +" for "+t.getDuration()+", ";

        }
        return ResponseEntity.ok(s);

    }

    //Returns task data for all of the user's device's tasks
    @RequestMapping("/task/user/view")
    @ResponseBody
    public ResponseEntity<?> findUserTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        List<Device> deviceList = deviceService.findDevicesByUserId(foundUser.getUserId());
        String s = "";
        for (Device device : deviceList){
            List<Task> taskList = taskService.findTasksByDeviceId(device.getDeviceId());
            for (Task task : taskList){
                s = s + task.getType() + " forUser " + username + " device " + task.getDeviceId() + " atTime " + task.getCalendar().getTime() + " for " + task.getDuration() + " , ";
            }
        }
        return ResponseEntity.ok(s);

    }

    //Deletes a certain type of task for the current user
    @RequestMapping("/task/delete")
    @ResponseBody
    public ResponseEntity<?> deleteTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        List<Task> tList = taskService.findAllTasks();
//        String name = String.valueOf(request.getParameter("name"));
//        String pwd = String.valueOf(request.getParameter("pwd"));
//        String type = String.valueOf(request.getParameter("type"));
//        int ruid = Integer.valueOf(request.getParameter("relUID"));
//        User foundUser = userService.findOneUserByUsername(name);
//        if (foundUser == null) {
//            return ResponseEntity.badRequest().body("User does not exist.");
//        }
//        if (!foundUser.getPassword().equals(pwd)) {
//            return ResponseEntity.badRequest().body("The password given is not correct.");
//        }
//        int tasksDeleted=0;
//        for(int i=0;i<tList.size();i++){
//            Task t = tList.get(i);
//            if(t.getUserId().equals(name) && t.getType().equals(type) && t.getDeviceId()==(ruid)){
//                taskService.deleteTask(t);
//                tasksDeleted++;
//
//            }
//        }
//        if(tasksDeleted==0) return ResponseEntity.ok("No matching tasks found");
//        return ResponseEntity.ok("Deleted " +tasksDeleted+" tasks.");
        return ResponseEntity.ok("not implemented");
    }

    //Deletes task database
    @RequestMapping("/task/delete/all")
    @ResponseBody
    public ResponseEntity<?> deleteAllTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{
        taskService.deleteSelf();
        return ResponseEntity.ok("Deleted task list");

    }

    //Adds tasks to the database
    @RequestMapping("/task/add")
    @ResponseBody
    public ResponseEntity<?> addTasks(HttpServletRequest request, HttpServletResponse response) throws Exception{

        //    I have changed the two params into using sessionId:
        //String name = String.valueOf(request.getParameter("name"));
        //String pwd = String.valueOf(request.getParameter("pwd"));

        //    Use sessionId to get the username, then the full User Object:
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);
        String deviceId = String.valueOf(request.getParameter("device"));
        Device target = deviceService.findDeviceByDeviceId(deviceId);
        if (!foundUser.getUserId().equals(target.getUserId())){
            return ResponseEntity.ok("device not belongs to the user");
        }

        String type = String.valueOf(request.getParameter("type"));
        int inThisTime = Integer.valueOf(request.getParameter("in"));
        int duration = Integer.valueOf(request.getParameter("duration"));
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.SECOND,inThisTime);
        Task t = new Task(type,deviceId,c1,duration);

        taskService.addOneTask(t);
        return ResponseEntity.ok("Task added");

    }




    //maps /homepage to the home html file
    @RequestMapping(value="/homepage", method = {RequestMethod.POST, RequestMethod.GET})
    public String getHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/home.html";
    }

    //maps /loginLanding to the corresponding file
    @RequestMapping(value="/loginLanding", method = {RequestMethod.POST, RequestMethod.GET})
    public String getLoginlanding(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/loginLanding.html";
    }

    //maps /user/dashboard to the corresponding file
    @RequestMapping(value="/user/dashboard", method = {RequestMethod.POST, RequestMethod.GET})
    public String getRoom(HttpServletRequest request, HttpServletResponse response) throws Exception{
        return "templates/room.html";
    }

    //Constant checks every second to see whether a device needs to be toggled or tasks need to be updated
    @Scheduled(fixedRate= 1000)
    public void printOutStatement() {
        List<Task> tList = taskService.findAllTasksSortedByTime();
        Calendar c1 = Calendar.getInstance();
        Task task = tList.get(0);
        System.out.println(task.toString());


//
//        if(tList.size()!=0) {
//            Task t = tList.get(0);
//            if(c1.after(t.getCalendar())){
//                List<Device> dList = deviceService.findAllDevices();
//                User u = userService.findOneUserByUsername(t.getUserId());
//                for(int i=0; i<dList.size();i++){
//                    if(dList.get(i).getUserId().equals(u.getUserId())) {
//                        if(dList.get(i).getRelativeUserId()==t.getDeviceId()) {
//                            toggledDevice = true;
//                            Device d = dList.get(i);
//                            d.toggle();
//                            System.out.println("Toggled");
//                            bufferDS.addOneDevice(d);
//                            if(t.getDuration()!=0){
//                                taskService.deleteTask(t);
//
//                                Calendar newC = Calendar.getInstance();
//                                newC.add(Calendar.SECOND,t.getDuration());
//                                t.setCalendar(newC);
//                                taskService.deleteSelf();
//                                boolean placedNew = false;
//                                Task oldT;
//
//
//
//                                for(int j=0;j<tList.size()+1;j++){
//                                    if(tList.size()!=0) {
//                                        if (placedNew || (j == tList.size())) {
//                                            oldT = tList.get(j - 1);
//                                        } else {
//                                            oldT = tList.get(j);
//                                        }
//                                        if (c1.after(oldT.getCalendar()) || placedNew) {
//                                            taskService.addOneTask(oldT);
//                                        } else {
//                                            taskService.addOneTask(t);
//                                            placedNew = true;
//                                        }
//                                    }else{
//                                        taskService.addOneTask(t);
//                                    }
//
//                                }
//
//                            }
//                        }else {
//                            Device d = dList.get(i);
//                            bufferDS.addOneDevice(d);
//
//                        }}else{
//                        Device d = dList.get(i);
//                        bufferDS.addOneDevice(d);
//                    }
//                }
//                deviceService =bufferDS;
//
//
//
//            }
//
//        }
//


    }



}
