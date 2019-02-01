package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.sql.Date;


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

    private String userId;

    private int duration;

    private date date;


    //   private String FiwareService;

    //   private String FiwareServicepath;

    //   private String SamsungID;

    /** Generate task without taskId which will be generated automatically */
    public Task(String userId, date date1, int duration,String FiS, String FiSP, String sID){
        this.userId = userId;
        this.date = date1;
        this.duration = duration;
        //     this.FiwareService = FiS;
        //     this.FiwareServicepath = FiSP;
        //     this.SamsungID = sID;
    }


    public String getUserId(){
        return userId;
    }
    public String getTaskId(){
        return taskId;
    }


}
