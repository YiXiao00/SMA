package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Task2 is set for demands of certain smart-home device and they are triggered when
 * property reached some value
 *
 * Last Modify: Frank at 18/03/19
 * */

@Data
@Builder
@NoArgsConstructor
@Document(collection = "task2")
public class Task2 implements ISmartHomeTask {

    @Id
    private String taskId;

    private String type;

    private String deviceId;

    /**
     * trigger format definition:
     *     term = [[PropertyName],[Operator],[Value1],[Value2]...]
     *     term -> ( term [And/Or] term )
     *     syntax = term
     *
     * for example:
     *  syntax = [Humidity,HigherThan,0.35] And [Power,LowerThan,200]
     *
     * */
    private String trigger;

    /**
     * When two or more tasks meet their conditions at the same time,
     * run the task with higher priority.
     *
     * If priority equals, run all these tasks.
     * */
    private Integer priority;

    public Task2(String inputType, String inputDeviceId, String conditions){
        type = inputType;
        deviceId = inputDeviceId;
        trigger = conditions;
        priority = 0;
    }

}
