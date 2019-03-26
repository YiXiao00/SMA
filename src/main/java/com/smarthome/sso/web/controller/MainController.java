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
import java.io.BufferedReader;
import java.io.FileReader;
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
        //System.out.println(s);
        return ResponseEntity.ok(s);

    }

    //Deletes a certain type of task for the current user
    @PostMapping("/task/delete")
    @ResponseBody
    public ResponseEntity<?> deleteTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        String taskId = String.valueOf(request.getParameter("taskid"));
        Task task = taskService.findTaskByTaskId(taskId);
        if (task == null){
            return ResponseEntity.ok("invalid taskId");
        }
        Device device = deviceService.findDeviceByDeviceId(task.getDeviceId());
        if (!foundUser.getUserId().equals(device.getUserId())){
            return ResponseEntity.ok("device of the task does not belong to the user");
        }

        taskService.deleteTask(task);
        return ResponseEntity.ok("finished");

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

    @RequestMapping("/file/add")
    @ResponseBody
    public ResponseEntity<?> AddFromFile(HttpServletRequest request, HttpServletResponse response) throws Exception{
        try(BufferedReader br = new BufferedReader(new FileReader("newData.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                String split[] = line.split("\\s+");
                if(split[0].equals("USER")){
                    String name = split[1];
                    String pwd = split[2];
                    if (userService.findOneUserByUsername(name) == null) {
                        User newUser = new User(name, pwd);
                        userService.addOneUser(newUser);
                        System.out.println("Added user name " + split[1] + " pwd " + split[2]);
                    }

                }
                if(split[0].equals("DEVICE")) {
                    String username = split[1];
                    String type = split[2];
                    User foundUser = userService.findOneUserByUsername(username);

                    Device newDevice = Device.builder().userId(foundUser.getUserId()).type(type).build();
                    deviceService.addOneDevice(newDevice);
                    System.out.println("Added new device to user "+foundUser.getUserId()+" of type "+type);
                }

                if(split[0].equals("TASK")){
                    String deviceId = split[1];
                    String type = split[2];
                    int inThisTime = Integer.valueOf(split[3]);
                    int duration = Integer.valueOf(split[4]);
                    Calendar c1 = Calendar.getInstance();
                    c1.add(Calendar.SECOND,inThisTime);
                    Task t = new Task(type,deviceId,c1,duration);

                    List<Task> tList = taskService.findAllTasks();
                    taskService.addOneTask(t);

                    System.out.println("Added task");

                }
            }
        }
        return ResponseEntity.ok("Added from file");

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
        List<Task> taskList = taskService.findAllTasksSortedByTime();
        Calendar now = Calendar.getInstance();
        for (Task task : taskList){
            if (now.after(task.getCalendar())){
                String deviceId = task.getDeviceId();
                ServiceResult result = deviceService.toggleDevice(deviceId);
                if (result == ServiceResult.SERVICE_SUCCESS){
                    System.out.println(deviceId + " toggled");
                }

                taskService.deleteTask(task);
                if (task.getDuration() != 0){
                    Calendar shutdownTime = task.getCalendar();
                    shutdownTime.add(Calendar.SECOND,task.getDuration());
                    Task shutdownTask = new Task(task.getType(),task.getDeviceId(),shutdownTime,0);
                    taskService.addOneTask(shutdownTask);
                }
            }
            else{
                return;
            }
        }


    }



}
