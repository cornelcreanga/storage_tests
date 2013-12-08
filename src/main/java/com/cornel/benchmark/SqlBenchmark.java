package com.cornel.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SqlBenchmark implements Benchmark{

    public static final int COMMIT_SIZE = 1000;
    public static final int NO = 10;

    protected Connection connection;

    @Override
    public long insertBulkRows() throws SQLException {
        long t1 = System.currentTimeMillis();
        int counter = 0;
        try (PreparedStatement ps = connection.prepareStatement("insert into company(name,noReg,address,salary) values(?,?,?,?)")) {
            for (int i = 0; i < NO; i++) {
                for (int j = 0; j < COMMIT_SIZE; j++) {
                    ps.setString(1, "cornel creanga");
                    ps.setInt(2, counter++);
                    ps.setString(3, "Bucuresti, Sector 6, Aleea Lunca Siretului Bloc 42 scara 1a apartament "+j);
                    ps.setDouble(4, 45.25);
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
            }
        }
        return System.currentTimeMillis() - t1;
    }

    @Override
    public long deleteRows() throws SQLException {
        long t1 = System.currentTimeMillis();
        try (Statement stmt = connection.createStatement()) {
            String sql = "delete from company";
            stmt.executeUpdate(sql);
            connection.commit();
        }
        return System.currentTimeMillis() - t1;
    }

    @Override
    public long updateBulkRows() throws SQLException {
        long t1 = System.currentTimeMillis();
        int counter = 0;
        try (PreparedStatement ps = connection.prepareStatement("update company set salary=? where noReg=?")) {
            for (int i = 0; i < NO; i++) {
                for (int j = 0; j < COMMIT_SIZE; j++) {
                    ps.setDouble(1,32.4);
                    ps.setInt(2, counter++);
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
            }
        }
        return System.currentTimeMillis() - t1;
    }

    @Override
    public void destroy() throws Exception {
        if (connection != null)
            connection.close();
    }

}
