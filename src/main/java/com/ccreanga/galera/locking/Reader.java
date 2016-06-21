package com.ccreanga.galera.locking;

import java.sql.Connection;

class Reader implements Runnable{

    private Connection connection;
    private String name;

    public Reader(String name, Connection connection) {
        this.name = name;
        this.connection = connection;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println("reader="+name+",counter="+ TestLocking.getCounter(connection));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }
}
