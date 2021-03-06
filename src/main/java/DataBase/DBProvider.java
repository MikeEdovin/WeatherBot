package DataBase;

import GeoWeatherPackage.CityData;
import GeoWeatherPackage.WeatherData;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DBProvider {
private DataSource dataSource;


    public DBProvider(DataSource dSource) {
          this.dataSource=dSource;
    }

    public  void createTables(){
        Logger logger=Logger.getLogger("create tables");
        Statement statement;
        try{
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                String drop = "DROP TABLE IF EXISTS users,cities,last_cities,current_weather,forecast;";
                String users = "CREATE TABLE USERS " +
                        ("(USER_ID bigserial PRIMARY KEY NOT NULL," +
                                "NOTIFICATION_TIME time with time zone," +
                                "NOTIFICATION_CITY TEXT," +
                                "CHAT_ID TEXT," +
                                "NOTIFICATION_DAYS INT [6] DEFAULT '{1,2,3,4,5,0,0}')");
                String cities = "CREATE TABLE CITIES " +
                        ("(NAME TEXT PRIMARY KEY NOT NULL UNIQUE , " +
                                " LATITUDE    double precision     NOT NULL, " +
                                " LONGITUDE   double precision     NOT NULL)");
                String lastCities = "CREATE TABLE LAST_CITIES" +
                        ("(ID bigserial REFERENCES USERS(USER_ID) NOT NULL," +
                                "NAME TEXT REFERENCES CITIES(NAME)," +
                                "IS_CURRENT boolean NOT NULL," +
                                "UNIQUE(ID,NAME))");
                String forecast = "CREATE TABLE FORECAST " +
                        ("(CITY TEXT  NOT NULL, " +
                                "DATE date NOT NULL, " +
                                "TEMPERATURE double precision NOT NULL, " +
                                "FEELS_LIKE double precision NOT NULL, " +
                                "PRESSURE smallserial NOT NULL, " +
                                "HUMIDITY smallserial NOT NULL, " +
                                "CLOUDS smallserial NOT NULL, " +
                                "TIME_OF_UPDATE timestamp without time zone NOT NULL, " +
                                "TIME_ZONE TEXT NOT NULL," +
                                "FOREIGN KEY (CITY) REFERENCES CITIES, " +
                                "UNIQUE(CITY,DATE))");
                String currentWeather = "CREATE TABLE CURRENT_WEATHER " +
                        ("(CITY TEXT references CITIES(NAME) UNIQUE NOT NULL, " +
                                "DATE date NOT NULL, " +
                                "TEMPERATURE double precision NOT NULL, " +
                                "FEELS_LIKE double precision NOT NULL, " +
                                "PRESSURE smallserial NOT NULL, " +
                                "HUMIDITY smallserial NOT NULL, " +
                                "CLOUDS smallserial NOT NULL, " +
                                "TIME_OF_UPDATE timestamp without time zone NOT NULL, " +
                                "TIME_ZONE TEXT NOT NULL)");
                statement.executeUpdate(drop);
                statement.executeUpdate(users);
                statement.executeUpdate(cities);
                statement.executeUpdate(lastCities);
                statement.executeUpdate(forecast);
                statement.executeUpdate(currentWeather);
                statement.close();
                connection.commit();
            }
            logger.info("Tables created successfully");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addNotificationsDay(long userID,int day){
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String update = "UPDATE USERS SET NOTIFICATION_DAYS["+day+"]='"+day+"' WHERE USER_ID=" + userID + ";";
                System.out.println(update);
                statement.executeUpdate(update);
                statement.close();

            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public void deleteNotificationsDay(long userID,int day) {
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if (connection != null) {
                statement = connection.createStatement();
                String update = "UPDATE USERS SET NOTIFICATION_DAYS[" + day + "]='" + 0 + "' WHERE USER_ID=" + userID + ";";
                System.out.println(update);
                statement.executeUpdate(update);
                statement.close();

            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public Integer[] getNotificationDays(long userID){
        Array array=null;
        Integer[]days=new Integer[7];
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if (connection != null) {
                statement = connection.createStatement();
                String query = "select notification_days from users where user_id=" + userID + ";";
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    array = result.getArray("NOTIFICATION_DAYS");
                }
                if(array!=null) {
                    days = (Integer[]) array.getArray();
                }
                    result.close();
                    statement.close();
                }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return days;
    }
    public boolean isNotificationDay(int day,long userID) {
        Integer[] days = getNotificationDays(userID);
        for (int item : days) {
            if (day == item) {
                return true;
            }
        }
        return false;
    }
    public boolean hasAtLeastOneNotDay(long userID){
        Integer[] days=getNotificationDays(userID);
        for(int item:days){
            if(item!=0){
                return true;
            }
        }
        return false;
    }

    public CityData[] getLastThree(long userID){
        CityData[] last=new CityData[3];
        int nrOfCities=0;

        Statement statement;
        try{
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT CITIES.NAME, LATITUDE,LONGITUDE,TIME_OF_UPDATE FROM CITIES " +
                        " JOIN CURRENT_WEATHER ON CITIES.NAME=CURRENT_WEATHER.CITY " +
                        "JOIN LAST_CITIES ON LAST_CITIES.NAME=CITIES.NAME" +
                        " WHERE ID=" + userID +
                        " ORDER BY TIME_OF_UPDATE DESC;";
                ResultSet result = statement.executeQuery(query);
                while (result.next() && nrOfCities < 3) {
                    String name = result.getString("NAME");
                    double lat = result.getDouble("LATITUDE");
                    double lon = result.getDouble("LONGITUDE");
                    CityData cityData = new CityData(name, lat, lon);
                    last[nrOfCities++] = cityData;
                }
                result.close();
                statement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return last;
    }
    public  boolean userIsInDB(long userID){
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT USER_ID FROM USERS WHERE USER_ID=" + userID + ";";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    long id = resultSet.getLong("USER_ID");
                    if (userID == id) {
                        return true;
                    }
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return false;
    }
    public void addUserToDB(long userID) {
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String update = "INSERT INTO USERS(USER_ID)" + "VALUES(" + userID + ")" +
                        "ON CONFLICT(USER_ID) DO NOTHING;";
                statement.executeUpdate(update);
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public  void setNotification(Long userID,String chatID, String city, LocalTime time){
        Logger logger = Logger.getGlobal();
        Statement statement;
        String update;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                if (time == null) {
                    update = "UPDATE USERS SET NOTIFICATION_TIME=" + time + ",NOTIFICATION_CITY='" +
                            city + "'" + ",CHAT_ID='" + chatID + "' WHERE USER_ID=" + userID + ";";
                } else {
                    update = "UPDATE USERS SET NOTIFICATION_TIME='" + time + "',NOTIFICATION_CITY='" +
                            city + "'" + ",CHAT_ID='" + chatID + "' WHERE USER_ID=" + userID + ";";
                }
                statement.executeUpdate(update);
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public  LocalTime getNotificationTime(long userID){
        Logger logger = Logger.getGlobal();
        Statement statement;
        LocalTime result;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT NOTIFICATION_TIME FROM USERS WHERE USER_ID=" + userID + ";";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Time time = resultSet.getTime("NOTIFICATION_TIME");
                    if (time != null) {
                        result = time.toLocalTime();
                        return result;
                    }
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public  CityData getNotificationCity(long userID){
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT NOTIFICATION_CITY,LATITUDE,LONGITUDE FROM USERS " +
                        "JOIN CITIES ON USERS.NOTIFICATION_CITY=CITIES.NAME " +
                        "WHERE USER_ID=" + userID + ";";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String name = resultSet.getString("NOTIFICATION_CITY");
                    double lat = resultSet.getDouble("LATITUDE");
                    double lon = resultSet.getDouble("LONGITUDE");
                    return new CityData(name, lat, lon);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public String getTimeZone(String city){
        Logger logger = Logger.getGlobal();
        Statement statement;
        String timeZone;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT TIME_ZONE FROM CURRENT_WEATHER WHERE CITY='" + city + "';";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    timeZone = resultSet.getString("TIME_ZONE");
                    return timeZone;
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public  String getChatID(long userID){
        Logger logger = Logger.getGlobal();
        Statement statement;
        String chatID;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT CHAT_ID FROM USERS WHERE USER_ID=" + userID + ";";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    chatID = resultSet.getString("CHAT_ID");
                    return chatID;
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public void setCurrentCity(CityData city,long userID){
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String defaultCurrent = "UPDATE LAST_CITIES SET IS_CURRENT = 'false' WHERE ID=" + userID;
                String setCurrent = "UPDATE LAST_CITIES SET IS_CURRENT = 'true' WHERE ID=" + userID +
                        " AND NAME='" + city.getName() + "'";
                statement.executeUpdate(defaultCurrent);
                statement.executeUpdate(setCurrent);
                statement.close();
            }
        }
        catch (SQLException e){
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
    }
    public  CityData getCurrentCityDataFromDB(long userID){
        CityData cityData=null;
        Statement statement;
        try{
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT CITIES.NAME, LATITUDE,LONGITUDE FROM CITIES JOIN LAST_CITIES  ON LAST_CITIES.NAME=CITIES.NAME  WHERE ID=" + userID +
                        " AND IS_CURRENT='true' ";
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    String name = result.getString("NAME");
                    double lat = result.getDouble("LATITUDE");
                    double lon = result.getDouble("LONGITUDE");
                    cityData = new CityData(name, lat, lon);
                }
                result.close();
                statement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return cityData;
    }
    public WeatherData getCurrentWeatherFromDB(CityData current){
        WeatherData weatherData=null;
        Statement statement;
        try{
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT DATE, TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE FROM CURRENT_WEATHER WHERE CITY='" + current.getName() +
                        "'";
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    String tZone = result.getString("TIME_ZONE");
                    Date date = result.getDate("DATE");
                    LocalDate lDate = date.toLocalDate();
                    double temp = result.getDouble("TEMPERATURE");
                    double feels = result.getDouble("FEELS_LIKE");
                    long pres = result.getLong("PRESSURE");
                    long hum = result.getLong("HUMIDITY");
                    long clouds = result.getLong("CLOUDS");
                    Timestamp timestamp = result.getTimestamp("TIME_OF_UPDATE");
                    LocalDateTime tOfUpd = timestamp.toLocalDateTime();
                    weatherData = new WeatherData();
                    weatherData.setMeasurements(lDate, temp, pres, hum, feels, clouds, tOfUpd, tZone);
                }
                result.close();
                statement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return weatherData;
    }
    public  WeatherData[] getForecastFromDB(CityData current){
        WeatherData[] forecast=new WeatherData[10];
        int nrOfItems=0;
        Statement statement;
        try{
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String query = "SELECT DATE, TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE FROM FORECAST WHERE CITY='" + current.getName() +
                        "'";
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    String tZone = result.getString("TIME_ZONE");
                    Date date = result.getDate("DATE");
                    LocalDate lDate = date.toLocalDate();
                    double temp = result.getDouble("TEMPERATURE");
                    double feels = result.getDouble("FEELS_LIKE");
                    long pres = result.getLong("PRESSURE");
                    long hum = result.getLong("HUMIDITY");
                    long clouds = result.getLong("CLOUDS");
                    Timestamp timestamp = result.getTimestamp("TIME_OF_UPDATE");
                    LocalDateTime tOfUpd = timestamp.toLocalDateTime();
                    WeatherData weatherData = new WeatherData();
                    weatherData.setMeasurements(lDate, temp, pres, hum, feels, clouds, tOfUpd, tZone);
                    forecast[nrOfItems++] = weatherData;
                }
                result.close();
                statement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return forecast;
    }
    public  boolean isFresh(WeatherData data){
        if(data!=null) {
            String zone = data.getTimeZone();
            if (zone != null) {
                ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(data.getTimeZone()));
                Long now = zdtNow.toEpochSecond();
                LocalDateTime timeOfUpdate = data.getTimeOfUpdate();
                ZonedDateTime zdtTimeOfUpdate = ZonedDateTime.of(timeOfUpdate, ZoneId.of(data.getTimeZone()));
                Long tOfUpdate = zdtTimeOfUpdate.toEpochSecond();
                return now - tOfUpdate < 3600;
            }
        }
        return false;
    }
    public void addCityToDB(CityData city, long userID){
        Logger logger = Logger.getGlobal();
        Statement statement;
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String insertCityToCities = "INSERT INTO CITIES(NAME, LATITUDE, LONGITUDE)" +
                        "VALUES('" + city.getName() + "'," + city.getLatitude() + "," + city.getLongitude() +
                        ") ON CONFLICT(NAME) DO NOTHING" + ";";
                String insertIntoLastCities = "INSERT INTO LAST_CITIES(ID,NAME,IS_CURRENT)" + "VALUES(" + userID +
                        ",'" + city.getName() + "'," + "'FALSE'" + ") ON CONFLICT(ID,NAME) DO NOTHING ;";
                statement.executeUpdate(insertCityToCities);
                statement.executeUpdate(insertIntoLastCities);
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public  void addCurrentWeatherToDB(WeatherData data, CityData city){
        Logger logger = Logger.getGlobal();
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
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String current = "INSERT INTO CURRENT_WEATHER(CITY,DATE,TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE)"
                        + "VALUES(" + "'" + cName + "','" + date + "'," + temp + "," + feels_like + "," + pres + "," + hum + "," + clouds + ",'"
                        + tOfUpd + "','" + tZone + "')" + " ON CONFLICT(CITY) DO UPDATE SET DATE='" + date + "'," + "TEMPERATURE=" +
                        temp + ",FEELS_LIKE=" + feels_like + ",PRESSURE=" + pres + ",HUMIDITY=" + hum + ",CLOUDS=" +
                        clouds + ",TIME_OF_UPDATE='" + tOfUpd + "',TIME_ZONE='" + tZone + "';";
                statement.executeUpdate(current);
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public  void addForecastToDB(WeatherData[]forecast, CityData city){//ok
        Logger logger = Logger.getGlobal();
        Statement statement;
        String cName= city.getName();
        try {
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                String clear="DELETE FROM FORECAST WHERE CITY= '"+city.getName()+"';";
                statement.executeUpdate(clear);
                for (WeatherData data : forecast) {
                    if (data != null) {
                        LocalDate date = data.getDate();
                        double temp = data.getTemp();
                        double feels_like = data.getFeelsLikeTemp();
                        long pres = data.getPressure();
                        long hum = data.getHumidity();
                        long clouds = data.getClouds();
                        String tZone = data.getTimeZone();
                        LocalDateTime tOfUpd = data.getTimeOfUpdate();
                        String weather = "INSERT INTO FORECAST(CITY,DATE,TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE)"
                                + "VALUES(" + "'" + cName + "','" + date + "'," + temp + "," + feels_like + "," + pres + "," + hum + "," + clouds + ",'"
                                + tOfUpd + "','" + tZone + "')" + " ON CONFLICT(CITY,DATE) DO UPDATE SET DATE='" + date + "'," + "TEMPERATURE=" +
                                temp + ",FEELS_LIKE=" + feels_like + ",PRESSURE=" + pres + ",HUMIDITY=" + hum + ",CLOUDS=" +
                                clouds + ",TIME_OF_UPDATE='" + tOfUpd + "',TIME_ZONE='" + tZone + "';";

                        statement.executeUpdate(weather);
                    }
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public  ArrayList<Long> getUsersIDFromDB(){
        Logger logger=Logger.getGlobal();
        ArrayList<Long> users=new ArrayList<>();

        Statement statement;
        try{
            Connection connection= dataSource.getConnection();
            if(connection!=null) {
                statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM USERS;");
                while (result.next()) {
                    long userID = result.getLong("USER_ID");
                    users.add(userID);
                }
                result.close();
                statement.close();
            }
        }
        catch (SQLException e){
            logger.warning(e.getMessage());
        }
        return users;
    }
public void closeConnection(){
        Logger logger = Logger.getLogger("Close connection");
        try{
            Connection connection= dataSource.getConnection();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
}




}
