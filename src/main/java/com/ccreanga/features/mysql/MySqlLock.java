package com.ccreanga.features.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySqlLock {

    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s/%s?user=%s&password=%s&zeroDateTimeBehavior=convertToNull",
                        "localhost:3306","test","test","test"));
        Statement st = connection.createStatement();
    }

}
