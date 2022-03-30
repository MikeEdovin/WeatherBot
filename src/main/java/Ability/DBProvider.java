package Ability;

import Users.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DBProvider {
    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/weatherbotdb", "Mike E", "St@rt123");
        return connection;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public static void createTables(){
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
            statement= connection.createStatement();
            String users="CREATE TABLE USERS "+
                    ("(USER_ID serial PRIMARY KEY NOT NULL)");
            String cities = "CREATE TABLE CITIES " +
                    ("(ID serial references USERS(USER_ID), " +
                    "NAME TEXT   NOT NULL UNIQUE, " +
                    " LATITUDE    double precision     NOT NULL, " +
                    " LONGITUDE   double precision     NOT NULL, "+
                    " IS_CURRENT boolean NOT NULL)");
            String weather="CREATE TABLE WEATHER "+
                    ("(CITY TEXT references CITIES(NAME), "+
                     "DATE time with time zone NOT NULL, "+
                     "TEMPERATURE double precision NOT NULL, "+
                     "FEELS_LIKE double precision NOT NULL, "+
                     "PRESSURE smallserial NOT NULL, "+
                     "HUMIDITY smallserial NOT NULL, "+
                     "CLOUDS smallserial NOT NULL, "+
                     "TIME_OF_UPDATE time with time zone NOT NULL)");
            statement.executeUpdate(users);
            statement.executeUpdate(cities);
            statement.executeUpdate(weather);
            statement.close();
            connection.close();
            System.out.println("Table created successfully");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void addIsCurrent(){
        Connection connection;
        Statement statement;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String update=" ALTER TABLE CITIES  ADD IS_CURRENT boolean NOT NULL DEFAULT 'false'";
            statement.executeUpdate(update);
            connection.commit();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void addUserToDB(User user) {
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;

        try {
            connection = DBProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql="INSERT INTO USERS(USER_ID)"+"VALUES("+user.getUserID()+");";
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static void addCityToDB(CityData city, User user){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;

        try {
            connection = DBProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql="INSERT INTO CITIES(ID, NAME, LATITUDE, LONGITUDE)"+"VALUES("+user.getUserID()+
                    ",'"+city.getName()+"',"+city.getLatitude()+","+city.getLongitude()+");";
            System.out.println(sql);
            String sql2 = "INSERT INTO CITIES (ID,NAME,LATITUDE,LONGITUDE) "
                    + "VALUES (1, 'Paul', 32, 'California', 20000.00 );";
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static ArrayList<User> getUserFromDB(){
        Logger logger=Logger.getGlobal();
        ArrayList<User> users=new ArrayList<>();
        Connection connection;
        Statement statement;
        User user;
        CityData cityData;
        try{
            connection=getConnection();
            connection.setAutoCommit(false);
            statement= connection.createStatement();
            ResultSet userResult= statement.executeQuery("SELECT * FROM USERS;");
            while(userResult.next()){
                long id=userResult.getLong("USER_ID");
                user=new User(id);
                ResultSet cities=statement.executeQuery("SELECT * FROM CITIES");
                while(cities.next()){
                    String name=cities.getString("NAME");
                    double latitude=cities.getDouble("LATITUDE");
                    double longitude=cities.getDouble("LONGITUDE");
                    cityData=new CityData(name,longitude,latitude);
                    user.addCityDataToList(cityData);
                }
                users.add(user);
                cities.close();
            }
            userResult.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            logger.warning(e.getMessage());
        }
        return users;
    }


}
