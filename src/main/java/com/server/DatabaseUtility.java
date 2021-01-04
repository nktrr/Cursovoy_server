package com.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.sql.*;
import java.text.SimpleDateFormat;

@Component
public class DatabaseUtility {

    private  String db_url = "jdbc:postgresql://127.0.0.1:5432/JALP";
    private Connection connection;


    @Autowired
    private DatabaseUtility(){
        //db_url = Configuration.getDBAddress();
        String login = new String("postgres");
        String pass = new String("4eJ7sKwB");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = null;
        try {
            connection = DriverManager.getConnection(db_url, login, pass);
            System.out.println(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String checkUserBlock(String login){
        Statement statement;
        String sqlForSelectUser = "SELECT id, status FROM users WHERE login = '" + login + "'";
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlForSelectUser);
            resultSet.next();
            System.out.println(sqlForSelectUser);
            int userId = resultSet.getInt("id");
            int userStatus = resultSet.getInt("status");
            // 0 - unblocked | 1 - blocked
            if (userStatus == 0){
                return "Can connect";
            }
            else{
                return getUserBlockTime(statement, userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    void blockUser(int id){
        try {
            String sqlForChangeStatus = "UPDATE users SET status = 1 WHERE id = " + id + "";
            Statement statement = connection.createStatement();
            statement.execute(sqlForChangeStatus);
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            String dataFormatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(currentTimestamp);
            System.out.println("INSERT INTO user_blocks (id, block_time) " +
                    "VALUES (" + id +",'" + dataFormatted + "')");
            //statement.execute("INSERT INTO user_blocks (id, block_time) " +
                   // "VALUES (" + userId +",'" + dataFormatted + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private String getUserBlockTime(Statement statement, int userId) throws SQLException {
        String sqlForCheckBlock = "SELECT * FROM user_blocks WHERE id = " + userId;
        ResultSet resultSet = statement.executeQuery(sqlForCheckBlock);
        resultSet.next();
        if (resultSet.getTimestamp(2).getTime() < System.currentTimeMillis() - 86400000){
            statement.execute("DELETE FROM user_blocks WHERE id = " + userId);
            return "Can connect";
        }

        else{
            long expirationDate = resultSet.getTimestamp(2).getTime() + 86400000;
            String expirationDateFormatted = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z")
                    .format(expirationDate);
            return "Blocked:" + expirationDateFormatted;
        }
    }

}
