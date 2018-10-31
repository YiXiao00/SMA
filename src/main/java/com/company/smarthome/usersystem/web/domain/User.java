package com.company.smarthome.usersystem.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

/**
 * User class
 *
 * Last modify: Frank, 31/10/2018
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_collection")
public class User {
    /**The Id of the user*/
    @Id
    private Integer userId;

    /**username*/
    private String username;

    /**password, should be encrypted*/
    private String password;

}
