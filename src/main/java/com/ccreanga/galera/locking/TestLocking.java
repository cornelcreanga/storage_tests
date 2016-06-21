package com.ccreanga.galera.locking;

import com.ccreanga.FileUtil;
import com.ccreanga.ScriptRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class TestLocking {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
//        Connection connection = DriverManager.getConnection(
//                String.format("jdbc:mysql://%s/%s?user=%s&password=%s&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=%s",
//                        "172.17.0.4","","root","",true));

//        Connection connection = DriverManager.getConnection(
//                String.format("jdbc:mysql:loadbalance://%s/%s?user=%s&password=%s&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=%s",
//                        "172.17.0.4,172.17.0.5,172.17.0.3","","root","",true));


        Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql:replication://%s/%s?user=%s&password=%s&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=%s",
                        "172.17.0.3,172.17.0.4,172.17.0.5","","root","",true));

        connection.setAutoCommit(false);

//        jdbc:mysql:replication://writer:3306,reader:3307/database
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        InputStream in = FileUtil.classPathResource("galera_counter.sql");
        try {
            scriptRunner.runScript(new InputStreamReader(in, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        long deadlocks = 0;
        for (int i = 0; i < 10000; i++) {
            try {
                incrementCounter(connection);
            }catch(SQLException e){
                if (e.getSQLState().equals("40001")) {//deadlock
                    System.out.println(e.getMessage());
                    deadlocks++;
                }

            }
        }

        System.out.println("deadlocks:"+deadlocks);

        for (int i = 0; i < 10; i++) {
            connection.setReadOnly(true);
            System.out.println(getCounter(connection));
            connection.commit();
            connection.setReadOnly(false);
        }


//        for (int i = 0; i < 10000; i++) {
//            boolean retry = true;
//            int interval = 100;
//            for(int r=0;r<3 && retry;r++){
//                try {
//                    incrementCounter(connection);
//                    retry = false;
//                }catch(SQLException e){
//                    if (e.getSQLState().equals("40001")) {//deadlock
//                        System.out.println("retrying..");
//                        retry = true;
//                        Thread.sleep(interval);
//                        interval*=2;
//                    }
//                }
//            }
//        }


    }

    public static void incrementCounter(Connection connection) throws SQLException{
        try (Statement stmt = connection.createStatement()) {
            String sql = "update test.counter set counter=counter+1";
            stmt.executeUpdate(sql);
            connection.commit();
        }
    }

    public static int getCounter(Connection connection){
        try (Statement stmt = connection.createStatement()) {
            String sql = "select max(counter) from test.counter";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()){
                return rs.getInt(1);
            }else
                throw new RuntimeException("no counter found");
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

}
