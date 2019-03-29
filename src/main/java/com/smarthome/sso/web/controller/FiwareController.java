package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.*;
import com.smarthome.sso.web.service.DeviceService;
import com.smarthome.sso.web.service.FiwareService;
import com.smarthome.sso.web.service.TaskService;
import com.smarthome.sso.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class FiwareController {

    @Autowired
    private FiwareService fiwareService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserService userService;

    public boolean innerMatchUserDevice(String inputToken, String deviceId){
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        Device target = deviceService.findDeviceByDeviceId(deviceId);

        if (!foundUser.getUserId().equals(target.getUserId())){
            return false;
        }
        return true;
    }

    //Adds a new device associated with the user
    @PostMapping("/device/add")
    @ResponseBody
    public ResponseEntity<?> addDevice(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);
        String type = String.valueOf(request.getParameter("type"));

        Device newDevice = new Device(foundUser.getUserId(), type,"","","");
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
    @PostMapping("/device/verify")
    @ResponseBody
    public ResponseEntity<?> verifyDevices(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputToken = String.valueOf(request.getParameter("token"));
        String username = userService.getUsernameFromSessionId(inputToken);
        User foundUser = userService.findOneUserByUsername(username);

        String deviceId = String.valueOf(request.getParameter("device"));
        Device target = deviceService.findDeviceByDeviceId(deviceId);
        String type = String.valueOf(request.getParameter("type"));

        if(target==null){
            return ResponseEntity.badRequest().body("Device ID not found");
        }

        if (!foundUser.getUserId().equals(target.getUserId())){
            return ResponseEntity.badRequest().body("device does not belong to the user");
        }

        if(!type.equals(target.getType())){
            return ResponseEntity.badRequest().body("Device type mismatch");
        }

        return ResponseEntity.ok("Device exists");




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
            return ResponseEntity.ok("device does not belong to the user");
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
        String deviceId = String.valueOf(request.getParameter("device"));
        if (!innerMatchUserDevice(inputToken,deviceId)){
            return ResponseEntity.ok("device does not belong to the user");
        }

        ServiceResult result = deviceService.toggleDevice(deviceId);
        if (result == ServiceResult.SERVICE_SUCCESS){
            System.out.println(deviceId + " toggled");
        }

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
        List<Device> result = new ArrayList<>();
        //String s = "";
        for(int i=0; i<deviceList.size();i++){
            if(deviceList.get(i).getUserId().equals(foundUser.getUserId())) {
                Device d = deviceList.get(i);
                result.add(d);
                //s = s + d.getType() + " deviceId " + d.getDeviceId() +" CurrentlyOn "+d.getPowerStatus() +" , ";
            }
        }
        //return ResponseEntity.ok(s);
        return ResponseEntity.ok(result);
    }




    @RequestMapping(value = "/fiware/info", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> getApiInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        //String token = String.valueOf(request.getParameter("sessionId"));
        //User tmpUser = getUserFromSessionId(token);
        //TODO

        FiwareInfo fiwareInfo = fiwareService.fiwareApiRequest("http://137.222.204.81:1026/v2/entities","testLuft2019","/testLuft2019");
        return ResponseEntity.ok(fiwareInfo);
    }

    @PostMapping("/fiware/task/add")
    @ResponseBody
    public ResponseEntity<?> addTask2(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputType = String.valueOf(request.getParameter("type"));
        String deviceId = String.valueOf(request.getParameter("device"));
        String conditionString = String.valueOf(request.getParameter("condition"));
        Task2 task2 = new Task2(inputType,deviceId,conditionString);
        taskService.addTask2(task2);
        return ResponseEntity.ok("task2 added");
    }

    @Scheduled(fixedRate= 15000)
    public void checkFiwareTasks() throws Exception{
        List<Task2> taskList = taskService.findAllTask2s();
        HashMap<String,FiwareInfo> deviceHashMap = new HashMap<>();
        for (Task2 task2 : taskList){
            if (!deviceHashMap.containsKey(task2.getDeviceId())){
                Device device = deviceService.findDeviceByDeviceId(task2.getDeviceId());
                deviceHashMap.put(task2.getDeviceId(),fiwareService.fiwareApiRequest("http://137.222.204.81:1026/v2/entities","testLuft2019","/testLuft2019"));
            }
        }
        for (Task2 task2 : taskList){
            ServiceResult result = fiwareService.trySyntax(deviceHashMap.get(task2.getDeviceId()),task2.getTrigger());
            if (result == ServiceResult.SERVICE_SUCCESS){
                String taskType = task2.getType();
                if ("DefaultType".equals(taskType) || "Toggle".equals(taskType)){
                    deviceService.toggleDevice(task2.getDeviceId());
                }
                else if ("TurnOn".equals(taskType)){
                    deviceService.turnOnDevice(task2.getDeviceId());
                }
                else if ("TurnOff".equals(taskType)){
                    deviceService.turnOffDevice(task2.getDeviceId());
                }
            }
        }
        System.out.println("---Task2: Refreshed---");
    }

}
