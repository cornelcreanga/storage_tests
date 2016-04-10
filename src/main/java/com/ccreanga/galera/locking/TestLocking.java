package com.ccreanga.galera.locking;

import com.ccreanga.FileUtil;
import com.ccreanga.ScriptRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestLocking {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s/%s?user=%s&password=%s&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=%s",
                        "172.17.0.4","test","test","test",true));

//        Connection connection = DriverManager.getConnection(
//                String.format("jdbc:mysql:loadbalance://%s/%s?user=%s&password=%s&zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=%s",
//                        "172.17.0.4,172.17.0.2,172.17.0.3","test","test","test",true));
        connection.setAutoCommit(false);


        ScriptRunner scriptRunner = new ScriptRunner(connection);
        InputStream in = FileUtil.classPathResource("galera_counter.sql");
        try {
            scriptRunner.runScript(new InputStreamReader(in, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        long deadlocks = 0;
        for (int i = 0; i < 10000; i++) {
            try (Statement stmt = connection.createStatement()) {
                String sql = "update counter set counter=counter+1";
                stmt.executeUpdate(sql);
                connection.commit();
            }catch(SQLException e){
                e.printStackTrace();
                if (e.getSQLState().equals("40001"))
                    deadlocks++;

            }
        }
        System.out.println(deadlocks);

    }

}
