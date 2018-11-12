package com.smarthome.sso.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * User Class
 *
 * Last Modify: Frank at 181107
 * */
@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
@Document(collection = "user")
public class User {

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /** Id, unique */
    @Id
    private String userId;

    /** username, can be duplicated */
    private String username;

    /** pwd */
    private String password;

    public User(String userID, String username, String password){
        this.userId = userID;
        this.username = username;
        this.password=password;
    }

}
