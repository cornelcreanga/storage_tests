package com.cornel.isolation;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * start write:10
 start write:11
 end write:10
 end write:11

 start write:10
 start write:11
 end write:11
 end write:10




 */

public class TestIsolationLevel {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection firstConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root&password=root");
        firstConnection.setAutoCommit(false);
        firstConnection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

        Connection secondConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root&password=root");
        secondConnection.setAutoCommit(false);
        secondConnection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

        FirstWorker r1 = new FirstWorker(firstConnection);
        SecondWorker w2 = new SecondWorker(secondConnection);
        new Thread(r1).start();
        Thread.sleep(1000);
        new Thread(w2).start();
    }
}
