package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiwareInfoProperty {

    private String propertyName;

    private String propertyType;

    private String propertyValue;

    private String propertyMetadata;

}
