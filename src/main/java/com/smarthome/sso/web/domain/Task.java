package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Calendar;


/**
 * Task class
 *
 * Last Modify: Amar 01/02/19
 * */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "task")
public class Task {

    /** Id, unique */
    @Id
    private String taskId;

    private String type;

    private String userId;

    private int relativeDeviceId;

    private int duration;

    private Calendar calendar;

    private boolean[] repeated;


    //   private String FiwareService;

    //   private String FiwareServicepath;

    //   private String SamsungID;

    /** Constructor, generates task without taskId which will be generated automatically */
    public Task(String type,String userId,int ruid, Calendar calendar, int duration){
        this.type = type;
        this.userId = userId;
        this.calendar = calendar;
        this.duration = duration;
        this.relativeDeviceId = ruid;
        this.repeated = new boolean[]{false,false,false,false,false,false,false}; //setting all default values
        //     this.FiwareService = FiS;
        //     this.FiwareServicepath = FiSP;
        //     this.SamsungID = sID;
    }

    //getters for all relevant attributes
    public Calendar getCalendar() { return calendar; } 
    public String getUserId(){ 
        return userId;
    }
    public String getTaskId(){ 
        return taskId;
    }
    public int getDeviceId() { return relativeDeviceId;} 
    public String getType() { return type;}
    public int getDuration(){ return this.duration;}

    //Decrements device id, used for when devices are deleted to shift the rest of the devices in the device service
    public void decrementDeviceId() { this.relativeDeviceId--;}
    
    public void setCalendar(Calendar newCalendar){ this.calendar = newCalendar;}


}
