package com.smarthome.sso.web.controller;

import com.smarthome.sso.web.domain.User;
import com.smarthome.sso.web.service.FiwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class FiwareController {

    @Autowired
    private FiwareService fiwareService;

    @RequestMapping(value = "/fiware/info", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> GetApiInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String token = String.valueOf(request.getParameter("sessionId"));
        //User tmpUser = getUserFromSessionId(token);
        //TODO
        return ResponseEntity.ok("");
    }

}
