package com.cornel.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SqlBenchmark implements Benchmark{

    public static final int COMMIT_SIZE = 10000;
    public static final int NO = 10;

    protected Connection connection;


    @Override
    public long insertRowsMultiValue() throws SQLException {
        long t1 = System.currentTimeMillis();
        int counter = 0;
        try (Statement st = connection.createStatement()) {
            for (int i = 0; i < NO; i++) {
                StringBuilder sb = new StringBuilder(110000);
                sb.append("insert into company(name,noReg,address,salary) values");
                for (int j = 0; j < COMMIT_SIZE; j++) {
                    sb.append("('cornel creanga',");
                    sb.append(++counter);
                    sb.append(",'Bucuresti, Sector 6, Aleea Lunca Siretului Bloc 42 scara 1a apartament ");
                    sb.append(counter).append("'");
                    sb.append(",45.25),");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append(';');
                st.execute(sb.toString());
                connection.commit();
            }
        }
        return System.currentTimeMillis() - t1;
    }

    @Override
    public long insertRows() throws SQLException {
        long t1 = System.currentTimeMillis();
        int counter = 0;
        try (Statement st = connection.createStatement()) {
            for (int i = 0; i < NO; i++) {
                for (int j = 0; j < COMMIT_SIZE; j++) {
                    String s = "insert into company(name,noReg,address,salary) values('cornel creanga',"+
                            (++counter)+
                            ",'Bucuresti, Sector 6, Aleea Lunca Siretului Bloc 42 scara 1a apartament "+
                            counter+
                            "',45.25)";
                    st.execute(s);
                }
                connection.commit();
            }
        }
        return System.currentTimeMillis() - t1;
    }

    @Override
    public long insertBulkRows() throws SQLException {
        long t1 = System.currentTimeMillis();
        int counter = 0;
        try (PreparedStatement ps = connection.prepareStatement("insert into company(name,noReg,address,salary) values(?,?,?,?)")) {
            for (int i = 0; i < NO; i++) {
                for (int j = 0; j < COMMIT_SIZE; j++) {
                    ps.setString(1, "cornel creanga");
                    ps.setInt(2, ++counter);
                    ps.setString(3, "Bucuresti, Sector 6, Aleea Lunca Siretului Bloc 42 scara 1a apartament "+counter);
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
