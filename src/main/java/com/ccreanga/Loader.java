package com.ccreanga;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Loader {

    public static void prepareMongoDb(MongoDatabase db, int rows){
        MongoCollection<Document> collection = db.getCollection("ld2");

        collection.deleteMany(new Document());
//db.getCollection('ld2').createIndex( { firstName: 1 } ) todo -if not exists
        Random random = new Random();
        List<WriteModel<Document>> list = new ArrayList<>(10000);
        int counter = 1;
        long t1 = System.currentTimeMillis(), t2,start=t1;
        for (int k = 0; k < rows; k++) {
            String firstName = RandomUtils.randomAlphabetic(15);
            String lastName = RandomUtils.randomAlphabetic(15);

            Document document = new Document()
                    .append("lead_id",k)
                    .append("salutation","Herr")
                    .append("firstName",firstName)
                    .append("lastName",lastName)
                    .append("street",RandomUtils.randomAlphabetic(15))
                    .append("streetNumber",RandomUtils.randomAlphabetic(3))
                    .append("zipCode", RandomUtils.random(7,0,0,true,false,new char[]{'A','B','C','D','E','F'},random))
                    .append("city",RandomUtils.random(15,0,0,true,false,new char[]{'A','B','C','D'},random))
                    .append("country","AT")
                    .append("agbAccepted",true)
                    .append("email",firstName+"."+lastName+"@fer.at")
                    .append("campaignURL","ABCDEFGH"+ RandomUtils.randomLong(0,100))
                    .append("newsletterAccepted",RandomUtils.randomLong(0,4)==1)
                    .append("birthDay",new Date(RandomUtils.randomDate(-70*365,-18*365)))
                    .append("source","Source")
                    .append("singleOptInDate",new Timestamp(RandomUtils.randomDate(-365,0)))
                    .append("singleOptInIp","10.124."+ RandomUtils.randomLong(0,255)+"."+ RandomUtils.randomLong(0,255))
                    .append("formCaptchaValue",RandomUtils.randomAlphabetic(7))
                    .append("expectedCaptcha",RandomUtils.randomAlphabetic(7))
                    .append("answer",RandomUtils.randomAlphabetic(7))
                    .append("winningPoints",(int) RandomUtils.randomLong(0,100));
            insertWithProbability(document,"hobby",RandomUtils.random(10,0,0,true,false,new char[]{'A','B','C',},random),0.25d);
            insertWithProbability(document,"team",RandomUtils.random(10,0,0,true,false,new char[]{'A','B','C',},random),0.25d);
            insertWithProbability(document,"salary",""+RandomUtils.randomLong(200,4000),0.75d);
            insertWithProbability(document,"kids",""+RandomUtils.randomLong(0,4),0.5d);

            list.add(new InsertOneModel(document));
            if (counter % 10000 == 0){
                collection.withWriteConcern(WriteConcern.JOURNALED).bulkWrite(list,new BulkWriteOptions().ordered(false));
                list= new ArrayList<>();
                t2 = System.currentTimeMillis();
                System.out.println("inserted 10k in:" + (t2 - t1));
                t1 = t2;
            }
            counter++;
        }
        if (!list.isEmpty())
            collection.withWriteConcern(WriteConcern.JOURNALED).bulkWrite(list,new BulkWriteOptions().ordered(false));
        System.out.printf("inserted %s rows in %s\n",rows ,System.currentTimeMillis()-start);
    }

    private static void insertWithProbability(Document document,String key,String value,double probability){
        if (Math.random()>probability){
            document.append(key,value);
        }
    }


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
//        preparePostgreSqlTable(connection,1000*1000);
        connection.close();//126648

        MongoCredential credential = MongoCredential.createCredential("test", "local", "test".toCharArray());
        //MongoClient mongoClient = new MongoClient(new ServerAddress("localhost" , 27017), Arrays.asList(credential));
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost" , 27017));
        MongoDatabase mongoDatabase = mongoClient.getDatabase( "local" );
        prepareMongoDb(mongoDatabase,70*1000*1000);
    }
}
