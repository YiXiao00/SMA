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
@AllArgsConstructor
@Document(collection = "user")
public class User {

    // use Lombok
//    public String getUserId() {
//        return userId;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public String getPassword() {
//        return password;
//    }

    /** Id, unique */
    @Id
    private String userId;

    /** username, unique */
    private String username;

    /** pwd */
    private String password;

    private int devicesOwned = 0;

    public void addDevice(){
        this.devicesOwned++;
    }
    public void removeDevice(){
        if(this.devicesOwned!=0){
            this.devicesOwned--;
        }else{
            System.out.println("Attempted to remove devices when there aren't any");
        }
    }

    // use Lombok - AllArgsConstructor
//    public User(String userID, String username, String password){
//        this.userId = userID;
//        this.username = username;
//        this.password = password;
//    }

}
