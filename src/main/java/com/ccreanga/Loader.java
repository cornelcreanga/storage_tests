package com.ccreanga;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Random;

public class Loader {

    public static void preparePostgreSqlTable(Connection connection, int rows) throws SQLException {

        ScriptRunner scriptRunner = new ScriptRunner(connection);

        InputStream in = FileUtil.classPathResource("pg.sql");
        try {
            scriptRunner.runScript(new InputStreamReader(in, "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int counter = 1;
        long t1 = System.currentTimeMillis(), t2,start=t1;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO ld2(lead_id ,salutation,firstName,lastName,street,streetNumber,zipCode,city,country,agbAccepted,email,campaignURL,newsletterAccepted,birthDay," +
                        "  source,singleOptInDate,singleOptInIp,formCaptchaValue,expectedCaptcha,answer,winningPoints)" +
                        " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
        ) {
            Random random = new Random();

            for (int k = 0; k < rows; k++) {
                ps.setInt(1,k);
                ps.setString(2,"Herr");
                String firstName = RandomUtils.randomAlphabetic(15);
                String lastName = RandomUtils.randomAlphabetic(15);

                ps.setString(3,firstName);
                ps.setString(4,lastName);
                ps.setString(5, RandomUtils.randomAlphabetic(15));
                ps.setString(6, RandomUtils.randomAlphabetic(3));
                ps.setString(7, RandomUtils.random(7,0,0,true,false,new char[]{'A','B','C','D','E','F'},random));
                ps.setString(8, RandomUtils.random(15,0,0,true,false,new char[]{'A','B','C','D'},random));
                ps.setString(9,"AT");
                ps.setBoolean(10,true);
                ps.setString(11,firstName+"."+lastName+"@fer.at");
                ps.setString(12,"ABCDEFGH"+ RandomUtils.randomLong(0,100));
                ps.setBoolean(13, RandomUtils.randomLong(0,4)==1);
                ps.setDate(14,new Date(RandomUtils.randomDate(-70*365,-18*365)));
                ps.setString(15,"Source");
                ps.setTimestamp(16,new Timestamp(RandomUtils.randomDate(-365,0)));
                ps.setString(17,"10.124."+ RandomUtils.randomLong(0,255)+"."+ RandomUtils.randomLong(0,255));
                ps.setString(18, RandomUtils.randomAlphabetic(7));
                ps.setString(19, RandomUtils.randomAlphabetic(7));
                ps.setString(20, RandomUtils.randomAlphabetic(7));
                ps.setInt(21,(int) RandomUtils.randomLong(0,100));

                ps.addBatch();
                if (counter % 500 == 0)
                    ps.executeBatch();
                if (counter % 1000 == 0)
                    connection.commit();
                if (counter % 10000==0) {
                    t2 = System.currentTimeMillis();
                    System.out.println("inserted 10k in:" + (t2 - t1));
                    t1 = t2;
                }
                counter++;
            }
            ps.executeBatch();
            connection.commit();
            System.out.printf("inserted %s rows in %s\n",rows ,System.currentTimeMillis()-start);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(
                String.format("jdbc:postgresql://%s/%s?user=%s&password=%s","localhost:5432","test","test","test"));
        connection.setAutoCommit(false);
        preparePostgreSqlTable(connection,1000*1000);
        connection.close();

    }
}
