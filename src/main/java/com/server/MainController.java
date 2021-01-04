package com.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet6Address;
import java.util.Arrays;

@SpringBootApplication
@RestController
public class MainController {

    @Autowired
    DatabaseUtility dbUtility;

    @RequestMapping("/api/getUserStatus/{login}")
    private String getUserStatus(@PathVariable String login){
        String userStatus = dbUtility.checkUserBlock(login);
        if (userStatus.equals("Can connect")){
            return "Can connect";
        }
        else if (userStatus.contains(":")){
            return userStatus;
        }
        else {
            return userStatus;
        }
    }

    @RequestMapping("/api/checkUserConnection/{id}")
    private String isUserConnected(@PathVariable int id){
        System.out.println("Connections: ");
        for (Integer i : ConnectionPool.connections){
            System.out.println(i);
        }
        System.out.println("End connections");
        if (ConnectionPool.connections.contains(id)) {
            ConnectionPool.getINSTANCE().broadcast("Block:" + id);
            System.out.println("Broadcast: block: " + id);
            dbUtility.blockUser(id);
            return "Connected";
        }
        else{
            ConnectionPool.connections.add(id);
            return "Can connect";
        }
    }
}
