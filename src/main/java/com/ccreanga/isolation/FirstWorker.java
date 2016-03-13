package com.ccreanga.isolation;

import java.sql.Connection;
import java.sql.Statement;

public class FirstWorker implements Runnable {

    Connection connection;

    public FirstWorker(Connection connection) {
        this.connection = connection;
    }

    public void process() throws Exception {
        System.out.println("start write:"+Thread.currentThread().getId());
        try (Statement stmt = connection.createStatement()) {
            String sql = "update company set name=\"xxx\" where noReg>305000";
            stmt.executeUpdate(sql);
            Thread.sleep(5000);
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