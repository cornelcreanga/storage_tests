package com.ccreanga.benchmark;



import java.sql.*;

public class MySqlBenchmark extends SqlBenchmark{


    @Override
    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String sql = "create table if not exists company (id int NOT NULL AUTO_INCREMENT,name varchar(256) not null,noReg int not null,address varchar(512),salary double, PRIMARY KEY (id), index noReg_idx (noReg) ) ENGINE=InnoDB DEFAULT CHARSET=utf8";
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
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
                //"jdbc:mysql://localhost/feedback?user=sqluser&password=sqluserpw";
    }

}
