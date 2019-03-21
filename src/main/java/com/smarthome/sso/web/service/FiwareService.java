package com.smarthome.sso.web.service;

import com.smarthome.sso.web.constants.ServiceResult;
import com.smarthome.sso.web.constants.Task2Operator;
import com.smarthome.sso.web.domain.FiwareInfo;
import com.smarthome.sso.web.domain.FiwareInfoProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FiwareService {

    private Task2Operator task2Operator = Task2Operator.getInstance();

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
        Pattern pattern_property = Pattern.compile("\"(.*?)\":\\{\"type\":\"(.*?)\",\"value\":\"(.*?)\",\"metadata\":\\{(.*?)\\}\\}");
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
     *     term = [[PropertyName],[Operator],[Value1],[Value2]...]
     *     term -> ( term [And/Or] term )
     *     syntax = term
     *
     * for example:
     *  syntax = [Humidity,HigherThan,0.35] And ( [Power,LowerThan,200] Or [Light,HigherThan,0.5] )
     *
     * returns the result of syntax
     * */
    public ServiceResult trySyntax(FiwareInfo nowStatus, String syntax){
        // syntax analysis
        Stack<String> operatorStack = new Stack<>();
        Stack<String> ruleStack = new Stack<>();

        String[] args = syntax.split(" ");
        for (String arg : args){
            if (arg.contains("[") && arg.contains("]")){
                ruleStack.push(arg);
            }
            else {
                Integer rank = task2Operator.getOperatorPriority(arg);
                Boolean recurrence = true;
                while (recurrence){
                    if (rank == -1){
                        return ServiceResult.SERVICE_ERROR;
                    }
                    if (operatorStack.isEmpty()){
                        operatorStack.push(arg);
                        recurrence = false;
                    }
                    else if (")".equals(arg) ){
                        while (!"(".equals(operatorStack.peek())){
                            ruleStack.push(operatorStack.pop());
                        }
                        recurrence = false;
                    }
                    else if ("(".equals(arg) || rank <= task2Operator.getOperatorPriority(operatorStack.peek())){
                        operatorStack.push(arg);
                        recurrence = false;
                    }
                    else {
                        ruleStack.push(operatorStack.pop());
                        recurrence = true;
                    }
                }

            }
        }
        while(!operatorStack.isEmpty()){
            ruleStack.push(operatorStack.pop());
        }
        //run
        String[] runArgs = new String[ruleStack.size()];
        ruleStack.toArray(runArgs);
        Stack<Boolean> result = new Stack<>();
        for (String arg : runArgs){
            if (arg.contains("[") && arg.contains("]")){
                result.push(tryOneRule(nowStatus, arg));
            }
            else{
                String op = task2Operator.getOperatorUniqueName(arg);
                if ("and".equals(op)){
                    Boolean arg1 = result.pop();
                    Boolean arg2 = result.pop();
                    Boolean tmpResult = arg1 && arg2;
                    result.push(tmpResult);
                }
                else if ("or".equals(op)){
                    Boolean arg1 = result.pop();
                    Boolean arg2 = result.pop();
                    Boolean tmpResult = arg1 || arg2;
                    result.push(tmpResult);
                }
            }
        }
        Boolean finalResult = result.pop();
        if (finalResult){
            return ServiceResult.SERVICE_SUCCESS;
        }
        return ServiceResult.SERVICE_FAIL;
    }

    private Boolean tryOneRule(FiwareInfo nowStatus, String rule){
        String content = rule.substring(1,rule.length()-1);
        String[] args = content.split(",");
        String property = args[0];
        String operator = args[1];
        String fiwareValue = nowStatus.getPropertyValue(property);
        if ("".equals(fiwareValue)){return false;}
        if ("=".equals(operator)){
            return (Float.valueOf(args[2]) == Float.valueOf(fiwareValue));
        }
        if (">".equals(operator)){
            return (Float.valueOf(fiwareValue) > Float.valueOf(args[2]));
        }
        if (">=".equals(operator)){
            return (Float.valueOf(fiwareValue) >= Float.valueOf(args[2]));
        }
        if ("<".equals(operator)){
            return (Float.valueOf(fiwareValue) < Float.valueOf(args[2]));
        }
        if ("<=".equals(operator)){
            return (Float.valueOf(fiwareValue) <= Float.valueOf(args[2]));
        }
        return false;
    }

}
