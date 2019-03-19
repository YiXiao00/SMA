package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiwareInfo {

    private String id;

    private String type;

    private List<FiwareInfoProperty> propertyList;

    public String getPropertyValue(String input){
        for (FiwareInfoProperty property : propertyList){
            if (input.equals(property.getPropertyName())){
                return property.getPropertyValue();
            }
        }
        return "";
    }

}
