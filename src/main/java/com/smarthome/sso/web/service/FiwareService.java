package com.smarthome.sso.web.service;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.domain.FiwareInfo;
import com.smarthome.sso.web.domain.FiwareInfoProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FiwareService {

    /**
     * This function send a GET message to the fiware api and wraps the result to FiwareInfo Class
     *
     * */
    public FiwareInfo fiwareApiRequest(String requestUrl, String arg_service, String arg_servicePath) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("fiware-service", arg_service);
        requestHeaders.add("fiware-servicepath", arg_servicePath);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> result;
        try{
            result = restTemplate.exchange(requestUrl, HttpMethod.GET ,requestEntity, String.class);
        }
        catch(Exception ex){
            return null;
        }
        String content = result.toString();

        //Parse entity
        FiwareInfo fiwareInfo = new FiwareInfo();
        Pattern pattern_id = Pattern.compile("\"id\":\"(.*?)\",");
        Pattern pattern_type = Pattern.compile("\"type\":\"(.*?)\",");

        Matcher matcher_id = pattern_id.matcher(content);
        Matcher matcher_type = pattern_type.matcher(content);

        if (matcher_id.find()) {
            //System.out.println("Found id: " + matcher_id.group(1));
            fiwareInfo.setId(matcher_id.group(1));
        } else {
            return null;
        }
        if (matcher_type.find()) {
            //System.out.println("Found type: " + matcher_type.group(1));
            fiwareInfo.setType(matcher_type.group(1));
        } else {
            return null;
        }
        Pattern pattern_property = Pattern.compile("\"(.*?)\":\\{\"type\":\"(.*?)\",\"value\":\"(.*?)\",\"metadata\":\\{(.*?)\\}\\},");
        Matcher matcher_property = pattern_property.matcher(content.replaceFirst("\"id\":\"(.*?)\",","").replaceFirst("\"type\":\"(.*?)\",",""));

        while (matcher_property.find()) {
            //System.out.println("Found property: " + matcher_property.group(1) + matcher_property.group(2) + matcher_property.group(3) + matcher_property.group(4));
            List<FiwareInfoProperty> list = fiwareInfo.getPropertyList();
            if (list == null){ list = new ArrayList<>();}
            FiwareInfoProperty tmpProperty = new FiwareInfoProperty();
            tmpProperty.setPropertyName(matcher_property.group(1));
            tmpProperty.setPropertyType(matcher_property.group(2));
            tmpProperty.setPropertyValue(matcher_property.group(3));
            tmpProperty.setPropertyMetadata(matcher_property.group(4));
            list.add(tmpProperty);
            fiwareInfo.setPropertyList(list);
        }
        return fiwareInfo;
    }

    /**
     * Condition format definition:
     *     term = ([PropertyName],[Operator],[Value1],[Value2]...)
     *     term -> (term [And/Or] term)
     *     syntax = term
     *
     * for example:
     *  syntax = (Humidity,HigherThan,0.35) And (Power,LowerThan,200)
     *
     * returns the result of syntax
     * */
    public ServiceResult TryCondition(FiwareInfo nowStatus, String syntax){
        // Interpreter
        // TODO
        return ServiceResult.SERVICE_FAIL;
    }

}
