package com.smarthome.sso.web.constants;

import java.util.HashMap;

public final class Task2Operator {

    private static Task2Operator instance = new Task2Operator();

    private HashMap<String,Integer> priority;

    private HashMap<String,String> uniqueName;

    /**
     * Singleton
     *
     * Last Modify: Frank at 19/03/19
     *
     * */
    public static Task2Operator getInstance(){
        return instance;
    }

    private Task2Operator(){
        priority = new HashMap<>();
        priority.put("And",1);
        priority.put("and",1);
        priority.put("AND",1);
        priority.put("&&",1);
        priority.put("Or",1);
        priority.put("or",1);
        priority.put("OR",1);
        priority.put("||",1);
        priority.put("(",99);
        priority.put(")",99);

        uniqueName = new HashMap<>();
        uniqueName.put("And","and");
        uniqueName.put("and","and");
        uniqueName.put("AND","and");
        uniqueName.put("&&","and");
        uniqueName.put("Or","or");
        uniqueName.put("or","or");
        uniqueName.put("OR","or");
        uniqueName.put("||","or");
        uniqueName.put("(","(");
        uniqueName.put(")",")");

    }

    public Integer getOperatorPriority(String input){
        if (priority.containsKey(input)){
            return priority.get(input);
        }
        return -1;
    }

    public String getOperatorUniqueName(String input){
        if (uniqueName.containsKey(input)){
            return uniqueName.get(input);
        }
        return "";
    }

}
