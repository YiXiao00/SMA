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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "device")
public class Device {

    /** Id, unique */
    @Id
    private String userId;

    private String type;

    private String FiwareService;

    private String FiwareServicepath;

    private String SamsungID;

    /** Generate device without userId which will be generated automatically */
    public Device(String type, String FiS, String FiSP, String sID){
        this.type = type;
        this.FiwareService = FiS;
        this.FiwareServicepath = FiSP;
        this.SamsungID = sID;
    }

}
