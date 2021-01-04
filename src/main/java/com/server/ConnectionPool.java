package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class ConnectionPool extends WebSocketServer {

    private static ConnectionPool INSTANCE;

    @Autowired
    DatabaseUtility dbUtility;

    public  static ArrayList<Integer> connections;

    private ConnectionPool(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }
    private ConnectionPool(InetSocketAddress address) {
        super(address);
    }

    private ConnectionPool(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    public static ConnectionPool getConnectionPool(int port){
        if (INSTANCE==null) {
            try {
                INSTANCE = new ConnectionPool(port);
                connections = new ArrayList<>();
                System.out.println("new instance");
            } catch (UnknownHostException e) {
            }
        }
        return INSTANCE;
    }
    public static ConnectionPool getINSTANCE(){
        return INSTANCE;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast("new connection: " + handshake
                .getResourceDescriptor()); //This method sends a message to all clients connected
        System.out.println(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(conn + " has left the room!");
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}