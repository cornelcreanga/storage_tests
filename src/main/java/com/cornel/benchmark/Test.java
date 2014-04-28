package com.cornel.benchmark;

/**
 19825
 20480



 *
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        Benchmark benchmarkSqlLite = new SQLLiteBenchmark();
//        benchmarkSqlLite.init("jdbc:sqlite:/home/ccreanga/sqllitedb/test.db");
//        benchmarkSqlLite.createTables();
//        System.out.println(benchmarkSqlLite.insertBulkRows());
//        System.out.println(benchmarkSqlLite.updateBulkRows());
//        benchmarkSqlLite.cleanData();
//        benchmarkSqlLite.destroy();

        Benchmark benchmarkMySql = new MySqlBenchmark();
        benchmarkMySql.init("jdbc:mysql://localhost:3306/test?user=root&password=root");
        benchmarkMySql.createTables();
        benchmarkMySql.deleteRows();
        for (int i = 0; i < 10; i++) {
            System.out.println(benchmarkMySql.insertRows());
            System.out.println(benchmarkMySql.insertBulkRows());
            System.out.println(benchmarkMySql.insertRowsMultiValue());
        }
//        System.out.println(benchmarkMySql.updateBulkRows());
//        benchmarkMySql.cleanData();
        benchmarkMySql.destroy();



    }

}
