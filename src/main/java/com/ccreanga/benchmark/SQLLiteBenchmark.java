package com.ccreanga.benchmark;

import java.sql.*;

public class SQLLiteBenchmark extends SqlBenchmark {


    @Override
    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql =  "create table if not exists company(id integer primary key autoincrement not null,name text not null,noReg int not null,address text,salary real)";
            stmt.executeUpdate(sql);
            sql="create index if not exists  noReg_idx ON company(noReg)";
            stmt.executeUpdate(sql);
            connection.commit();
        }
    }


    @Override
    public void cleanData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = "drop table company";
            stmt.executeUpdate(sql);
            connection.commit();
        }
    }

    @Override
    public void init(String url) throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
    }

}
