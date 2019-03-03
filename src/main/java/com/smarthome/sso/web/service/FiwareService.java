package com.smarthome.sso.web.service;

import com.smarthome.sso.web.domain.FiwareInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

@Service
public class FiwareService {

    public FiwareInfo fiwareApiRequest(String requestUrl, String arg_service, String arg_servicePath){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(requestUrl, String.class);
        //Parse entity here
        //TODO
        return null;
    }

}
