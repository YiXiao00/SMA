package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.Device;
import com.smarthome.sso.web.domain.FiwareInfo;
import com.smarthome.sso.web.domain.Task2;
import com.smarthome.sso.web.service.DeviceService;
import com.smarthome.sso.web.service.FiwareService;
import com.smarthome.sso.web.service.TaskService;
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
