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

    private String FiwareService;

    private String FiwareServicePath;

    private String SamsungID;

    /** Generate device without deviceId which will be generated automatically */
    public Device(String userId, String type, String FiS, String FiSP, String sID){
        this.userId = userId;
        this.type = type;
        this.FiwareService = FiS;
        this.FiwareServicePath = FiSP;
        this.SamsungID = sID;
    }

    //getters for all relevant attributes
    public boolean getPowerStatus(){
        return poweredOn;
    }

    public void toggle(){
        poweredOn = !poweredOn;
    }

}
