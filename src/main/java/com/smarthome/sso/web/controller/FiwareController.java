package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.domain.FiwareInfo;
import com.smarthome.sso.web.domain.Task2;
import com.smarthome.sso.web.service.FiwareService;
import com.smarthome.sso.web.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class FiwareController {

    @Autowired
    private FiwareService fiwareService;
    @Autowired
    private TaskService taskService;

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
    public ResponseEntity<?> AddTask2(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String inputType = String.valueOf(request.getParameter("type"));
        String deviceId = String.valueOf(request.getParameter("device"));
        String conditionString = String.valueOf(request.getParameter("condition"));
        Task2 task2 = new Task2(inputType,deviceId,conditionString);
        taskService.addTask2(task2);
        return ResponseEntity.ok("task2 added");
    }

}
