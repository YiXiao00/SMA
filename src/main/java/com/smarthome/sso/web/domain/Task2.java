package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Task2 is set for demands of certain smart-home device and they are triggered when
 * property reached some value
 *
 * Last Modify: Frank at 13/03/19
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "task2")
public class Task2 implements ISmartHomeTask {

    @Id
    private String taskId;

    private String type;

    private String deviceId;

    private String propertyName;

    private String threshold;


}
