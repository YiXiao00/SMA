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

    private String deviceId;

    private int duration;

    private Calendar calendar;

    private boolean[] repeated;

    /** Constructor, generates task without taskId which will be generated automatically */
    public Task(String type,String deviceId,int ruid, Calendar calendar, int duration){
        this.type = type;
        this.deviceId = deviceId;
        this.calendar = calendar;
        this.duration = duration;
        this.repeated = new boolean[]{false,false,false,false,false,false,false}; //setting all default values
        //     this.FiwareService = FiS;
        //     this.FiwareServicepath = FiSP;
        //     this.SamsungID = sID;
    }

    
    public void setCalendar(Calendar newCalendar){ this.calendar = newCalendar;}


}
