package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Smart device class
 *
 * Last Modify: Frank at 181202
 * */
@Data
@Document(collection = "device")
public class Device {

    /** Id, unique */
    @Id
    private String deviceId;

    private String userId;

    private String type;

    private boolean poweredOn = false;

    private String fiwareService;

    private String fiwareServicePath;

    private String samsungID;

    /** Generate device without deviceId which will be generated automatically */
    public Device(String userId, String type, String fiwareService, String fiwareServicePath, String samsungID){
        this.userId = userId;
        this.type = type;
        this.fiwareService = fiwareService;
        this.fiwareServicePath = fiwareServicePath;
        this.samsungID = samsungID;
    }
    //getters for all relevant attributes
    public boolean getPowerStatus(){
        return poweredOn;
    }

    public void toggle(){
        poweredOn = !poweredOn;
    }

}
