package com.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

@SpringBootApplication
public class JalpserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(JalpserverApplication.class, args);
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
        }
        ConnectionPool s = null;
        //s = new ConnectionPool(port);
        s = ConnectionPool.getConnectionPool(port);
        s.start();
        System.out.println("ChatServer started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = null;
            try {
                in = sysin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            s.broadcast(in);
            if (in.equals("exit")) {
                try {
                    s.stop(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

}
