package Ability;

import Users.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
            connection.setAutoCommit(false);
            String users="CREATE TABLE USERS "+
                    ("(USER_ID bigserial PRIMARY KEY NOT NULL)");
            String cities = "CREATE TABLE CITIES " +
                    ("(ID bigserial references USERS(USER_ID), " +
                    "NAME TEXT   NOT NULL UNIQUE, " +
                    " LATITUDE    double precision     NOT NULL, " +
                    " LONGITUDE   double precision     NOT NULL, "+
                    " IS_CURRENT boolean NOT NULL)");
            String forecast="CREATE TABLE FORECAST "+
                    ("(CITY TEXT references CITIES(NAME), "+
                     "DATE date NOT NULL, "+
                     "TEMPERATURE double precision NOT NULL, "+
                     "FEELS_LIKE double precision NOT NULL, "+
                     "PRESSURE smallserial NOT NULL, "+
                     "HUMIDITY smallserial NOT NULL, "+
                     "CLOUDS smallserial NOT NULL, "+
                     "TIME_OF_UPDATE timestamp without time zone NOT NULL, "+
                     "TIME_ZONE TEXT NOT NULL)");
            String currentWeather="CREATE TABLE CURRENT_WEATHER "+
                    ("(CITY TEXT references CITIES(NAME) UNIQUE, "+
                            "DATE date NOT NULL, "+
                            "TEMPERATURE double precision NOT NULL, "+
                            "FEELS_LIKE double precision NOT NULL, "+
                            "PRESSURE smallserial NOT NULL, "+
                            "HUMIDITY smallserial NOT NULL, "+
                            "CLOUDS smallserial NOT NULL, "+
                            "TIME_OF_UPDATE timestamp without time zone NOT NULL, "+
                            "TIME_ZONE TEXT NOT NULL)");
            statement.executeUpdate(users);
            statement.executeUpdate(cities);
            statement.executeUpdate(forecast);
            statement.executeUpdate(currentWeather);
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Tables created successfully");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void updateCurrentUnique(){
        Connection connection;
        Statement statement;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String delete="DELETE FROM CURRENT_WEATHER WHERE CITY='Nazareth'";
            String update=" ALTER TABLE CURRENT_WEATHER ADD UNIQUE(CITY)";
            statement.executeUpdate(delete);
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
            String sql="INSERT INTO USERS(USER_ID)"+"VALUES("+user.getUserID()+")" +
                   "ON CONFLICT(USER_ID) DO NOTHING" +";";
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static void setCurrentCity(CityData city,User user){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String defaultCurrent="UPDATE CITIES SET IS_CURRENT = 'false' WHERE ID="+user.getUserID();
            String setCurrent="UPDATE CITIES SET IS_CURRENT = 'true' WHERE ID="+user.getUserID()+
                    " AND NAME='"+city.getName()+"'";
            statement.executeUpdate(defaultCurrent);
            statement.executeUpdate(setCurrent);
            connection.commit();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
    }
    public static CityData getCurrentCityDataFromDB(long userID){
        CityData cityData=null;
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
            connection.setAutoCommit(false);
            statement= connection.createStatement();
            String query="SELECT NAME, LATITUDE,LONGITUDE FROM CITIES WHERE ID="+userID+
                    " AND IS_CURRENT='true' ";
            ResultSet result= statement.executeQuery(query);
            while(result.next()) {
                String name = result.getString("NAME");
                double lat = result.getDouble("LATITUDE");
                double lon = result.getDouble("LONGITUDE");
                cityData = new CityData(name, lon, lat);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return cityData;
    }
    public static WeatherData getCurrentWeatherFromDB(CityData current){
        WeatherData weatherData=new WeatherData();
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
            connection.setAutoCommit(false);
            statement= connection.createStatement();
            String query="SELECT DATE, TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE FROM CURRENT_WEATHER WHERE CITY='"+current.getName()+
                    "'";
            ResultSet result= statement.executeQuery(query);
            while(result.next()) {
                String tZone=result.getString("TIME_ZONE");
                Date date=result.getDate("DATE");
                LocalDate lDate =  date.toLocalDate();
                double temp = result.getDouble("TEMPERATURE");
                double feels = result.getDouble("FEELS_LIKE");
                long pres= result.getLong("PRESSURE");
                long hum=result.getLong("HUMIDITY");
                long clouds=result.getLong("CLOUDS");
                Timestamp timestamp=result.getTimestamp("TIME_OF_UPDATE");
                LocalDateTime tOfUpd= timestamp.toLocalDateTime();
                weatherData.setMeasurements(lDate,temp,pres,hum,feels,clouds,tOfUpd,tZone);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return weatherData;
    }
    public static boolean isFresh(WeatherData data){
        String zone= data.getTimeZone();
        if(zone!=null) {
            ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(data.getTimeZone()));
            Long now = zdtNow.toEpochSecond();
            LocalDateTime timeOfUpdate = data.getTimeOfUpdate();
            ZonedDateTime zdtTimeOfUpdate = ZonedDateTime.of(timeOfUpdate, ZoneId.of(data.getTimeZone()));
            Long tOfUpdate = zdtTimeOfUpdate.toEpochSecond();
            return now - tOfUpdate < 3600;
        }
        return false;
    }
    public static void addCityToDB(CityData city, User user){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = DBProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql="INSERT INTO CITIES(ID, NAME, LATITUDE, LONGITUDE,IS_CURRENT)"+"VALUES("+user.getUserID()+
                    ",'"+city.getName()+"',"+city.getLatitude()+","+city.getLongitude()+",'FALSE'"+")" +
                    "ON CONFLICT(NAME) DO NOTHING"+";";
            System.out.println(sql);
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static void addCurrentWeatherToDB(WeatherData data, CityData city){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        String cName= city.getName();
        LocalDate date=data.getDate();
        double temp= data.getTemp();
        double feels_like= data.getFeelsLikeTemp();
        long pres= data.getPressure();
        long hum= data.getHumidity();
        long clouds= data.getClouds();
        String tZone= data.getTimeZone();
        LocalDateTime tOfUpd=data.getTimeOfUpdate();
        try {
            connection = DBProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String current="INSERT INTO CURRENT_WEATHER(CITY,DATE,TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE)"
                    +"VALUES("+"'"+cName+"','"+date+"',"+temp+","+feels_like+","+pres+","+hum+","+clouds+",'"
                    +tOfUpd+"','"+tZone+"')" +" ON CONFLICT(CITY) DO UPDATE SET DATE='"+date+"',"+"TEMPERATURE="+
            temp+",FEELS_LIKE="+feels_like+",PRESSURE="+pres+",HUMIDITY="+hum+",CLOUDS="+
            clouds+",TIME_OF_UPDATE='"+tOfUpd+"',TIME_ZONE='"+tZone+"';";
            System.out.println(current);
            statement.executeUpdate(current);
            statement.close();
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static void addForecastToDB(WeatherData[]forecast, CityData city){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        String cName= city.getName();
        try {
            connection = DBProvider.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            for(WeatherData data:forecast) {
                if(data!=null) {
                    LocalDate date = data.getDate();
                    double temp = data.getTemp();
                    double feels_like = data.getFeelsLikeTemp();
                    long pres = data.getPressure();
                    long hum = data.getHumidity();
                    long clouds = data.getClouds();
                    String tZone = data.getTimeZone();
                    LocalDateTime tOfUpd = data.getTimeOfUpdate();
                    String weather = "INSERT INTO FORECAST(CITY,DATE,TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE)"
                            + "VALUES(" + "'" + cName + "','" + date + "'," + temp + "," + feels_like + "," + pres + "," + hum + "," + clouds + ",'" + tOfUpd + "','" + tZone + "');";
                    System.out.println(weather);
                    statement.executeUpdate(weather);
                }
            }
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
