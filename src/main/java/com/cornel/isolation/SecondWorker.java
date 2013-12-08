package com.cornel.isolation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SecondWorker implements Runnable {

    Connection connection;

    public SecondWorker(Connection connection) {
        this.connection = connection;
    }

    public void process() throws Exception {
        System.out.println("start write:"+Thread.currentThread().getId());
        try (Statement stmt = connection.createStatement()) {
            String sql = "insert into company(name,noReg,address,salary) values('wewewe',305050,'123412341241',313)";
            stmt.executeUpdate(sql);
            connection.commit();
        }
        System.out.println("end write:"+Thread.currentThread().getId());
    }

    @Override
    public void run() {
        try {
            process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}