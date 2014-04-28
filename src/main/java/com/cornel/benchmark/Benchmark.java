package com.cornel.benchmark;


import java.sql.SQLException;

public interface Benchmark {

    void createTables() throws SQLException;

    long insertRowsMultiValue() throws SQLException;

    long insertRows() throws SQLException;

    long insertBulkRows() throws SQLException;

    long deleteRows() throws SQLException;

    long updateBulkRows() throws SQLException;

    void cleanData() throws SQLException;

    void init(String url) throws Exception;

    void destroy() throws Exception;

}
