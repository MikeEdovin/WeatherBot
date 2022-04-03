package Ability;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DBProvider {
    public static Connection getConnection() {
        String userName=System.getenv("POSTGRE_USER_NAME");
        String psw=System.getenv("POSTGRE_PSW");
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/weatherbotdb", userName, psw);
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
            assert connection != null;
            statement= connection.createStatement();
            String drop="DROP TABLE IF EXISTS users,cities,current_weather,forecast;";
            String users="CREATE TABLE USERS "+
                    ("(USER_ID bigserial PRIMARY KEY NOT NULL," +
                            "NOTIFICATION_TIME time with time zone," +
                            "NOTIFICATION_CITY TEXT," +
                            "CHAT_ID TEXT)");
            String cities = "CREATE TABLE CITIES " +
                    ("(ID bigserial  NOT NULL, " +
                    "NAME TEXT  NOT NULL , " +
                    " LATITUDE    double precision     NOT NULL, " +
                    " LONGITUDE   double precision     NOT NULL, "+
                    " IS_CURRENT boolean NOT NULL,"+
                    "PRIMARY KEY(NAME), "+
                    "FOREIGN KEY(ID) REFERENCES USERS(USER_ID), "
                    +"UNIQUE(ID,NAME))");
            System.out.println(cities);
            String forecast="CREATE TABLE FORECAST "+
                    ("(CITY TEXT references CITIES(NAME) NOT NULL, "+
                     "DATE date NOT NULL, "+
                     "TEMPERATURE double precision NOT NULL, "+
                     "FEELS_LIKE double precision NOT NULL, "+
                     "PRESSURE smallserial NOT NULL, "+
                     "HUMIDITY smallserial NOT NULL, "+
                     "CLOUDS smallserial NOT NULL, "+
                     "TIME_OF_UPDATE timestamp without time zone NOT NULL, "+
                     "TIME_ZONE TEXT NOT NULL," +
                            "UNIQUE(CITY,DATE))");
            System.out.println(forecast);
            String currentWeather="CREATE TABLE CURRENT_WEATHER "+
                    ("(CITY TEXT references CITIES(NAME) UNIQUE NOT NULL, "+
                            "DATE date NOT NULL, "+
                            "TEMPERATURE double precision NOT NULL, "+
                            "FEELS_LIKE double precision NOT NULL, "+
                            "PRESSURE smallserial NOT NULL, "+
                            "HUMIDITY smallserial NOT NULL, "+
                            "CLOUDS smallserial NOT NULL, "+
                            "TIME_OF_UPDATE timestamp without time zone NOT NULL, "+
                            "TIME_ZONE TEXT NOT NULL)");
            statement.executeUpdate(drop);
            statement.executeUpdate(users);
            statement.executeUpdate(cities);
            statement.executeUpdate(forecast);
            statement.executeUpdate(currentWeather);
            statement.close();
            connection.close();
            System.out.println("Tables created successfully");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static CityData[] getLastThree(long userID){
        CityData[] last=new CityData[3];
        int nrOfCities=0;
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
            assert connection != null;
            statement= connection.createStatement();
            String query="SELECT NAME, LATITUDE,LONGITUDE,TIME_OF_UPDATE FROM CITIES " +
                    " JOIN CURRENT_WEATHER ON CITIES.NAME=CURRENT_WEATHER.CITY " +
                    " WHERE ID="+userID+
                    " ORDER BY TIME_OF_UPDATE DESC;";
            System.out.println(query);
            ResultSet result= statement.executeQuery(query);
            while(result.next()&&nrOfCities<3) {
                String name = result.getString("NAME");
                double lat = result.getDouble("LATITUDE");
                double lon = result.getDouble("LONGITUDE");
                CityData cityData = new CityData(name, lon, lat);
                last[nrOfCities++]=cityData;
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return last;
    }
    public static boolean userIsInDB(long userID){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String query="SELECT USER_ID FROM USERS WHERE USER_ID="+userID+";";
            ResultSet resultSet= statement.executeQuery(query);
            while(resultSet.next()){
                long id=resultSet.getLong("USER_ID");
                if(userID==id){
                    return true;
                }
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return false;
    }
    public static void addUserToDB(long userID) {
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String update="INSERT INTO USERS(USER_ID)"+"VALUES("+userID+")" +
                   "ON CONFLICT(USER_ID) DO NOTHING" +";";
            statement.executeUpdate(update);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static void setNotification(Long userID,String chatID, String city, LocalTime time){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        String update;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            if(time==null){
                update="UPDATE USERS SET NOTIFICATION_TIME="+time+",NOTIFICATION_CITY='"+
                        city+"'"+",CHAT_ID='"+chatID+"' WHERE USER_ID=" +userID+";";
            }
            else {
                update = "UPDATE USERS SET NOTIFICATION_TIME='" + time + "',NOTIFICATION_CITY='" +
                        city + "'" + ",CHAT_ID='" + chatID + "' WHERE USER_ID=" + userID + ";";
            }
            statement.executeUpdate(update);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static LocalTime getNotificationTime(long userID){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        LocalTime result;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String query="SELECT NOTIFICATION_TIME FROM USERS WHERE USER_ID="+userID+";";
            ResultSet resultSet= statement.executeQuery(query);
            while(resultSet.next()){
                Time time=resultSet.getTime("NOTIFICATION_TIME");
                if(time!=null) {
                    result = time.toLocalTime();
                    return result;
                }
            }
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public static CityData getNotificationCity(long userID){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String query="SELECT NOTIFICATION_CITY,LATITUDE,LONGITUDE FROM USERS " +
                    "JOIN CITIES ON USERS.NOTIFICATION_CITY=CITIES.NAME " +
                    "WHERE USER_ID="+userID+";";
            ResultSet resultSet= statement.executeQuery(query);
            while(resultSet.next()){
                String name=resultSet.getString("NOTIFICATION_CITY");
                double lat= resultSet.getDouble("LATITUDE");
                double lon=resultSet.getDouble("LONGITUDE");
                CityData city=new CityData(name,lon,lat);
                return city;
            }
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public static String getTimeZone(String city){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        String timeZone;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String query="SELECT TIME_ZONE FROM CURRENT_WEATHER WHERE CITY='"+city+"';";
            ResultSet resultSet= statement.executeQuery(query);
            while(resultSet.next()){
                timeZone= resultSet.getString("TIME_ZONE");
                return timeZone;
            }
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public static String getChatID(long userID){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        String chatID;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String query="SELECT CHAT_ID FROM USERS WHERE USER_ID="+userID+";";
            ResultSet resultSet= statement.executeQuery(query);
            while(resultSet.next()){
                chatID= resultSet.getString("CHAT_ID");
                return chatID;
            }
            statement.close();
            connection.close();

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }
    public static void setCurrentCity(CityData city,long userID){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            String defaultCurrent="UPDATE CITIES SET IS_CURRENT = 'false' WHERE ID="+userID;
            String setCurrent="UPDATE CITIES SET IS_CURRENT = 'true' WHERE ID="+userID+
                    " AND NAME='"+city.getName()+"'";
            statement.executeUpdate(defaultCurrent);
            statement.executeUpdate(setCurrent);
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
        WeatherData weatherData=null;
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
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
                weatherData=new WeatherData();
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
    public static WeatherData[] getForecastFromDB(CityData current){
        WeatherData[] forecast=new WeatherData[8];
        int nrOfItems=0;
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
            statement= connection.createStatement();
            String query="SELECT DATE, TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE FROM FORECAST WHERE CITY='"+current.getName()+
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
                WeatherData weatherData=new WeatherData();
                weatherData.setMeasurements(lDate,temp,pres,hum,feels,clouds,tOfUpd,tZone);
                forecast[nrOfItems++]=weatherData;
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return forecast;
    }
    public static boolean isFresh(WeatherData data){
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
    public static void addCityToDB(CityData city, long userID){
        Logger logger = Logger.getGlobal();
        Connection connection;
        Statement statement;
        try {
            connection = DBProvider.getConnection();
            statement = connection.createStatement();
            String sql="INSERT INTO CITIES(ID, NAME, LATITUDE, LONGITUDE,IS_CURRENT)"+"VALUES("+userID+
                    ",'"+city.getName()+"',"+city.getLatitude()+","+city.getLongitude()+",'FALSE'"+")" +
                    "ON CONFLICT(ID,NAME) DO NOTHING"+";";
            System.out.println(sql);
            statement.executeUpdate(sql);
            statement.close();
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
            statement = connection.createStatement();
            String current="INSERT INTO CURRENT_WEATHER(CITY,DATE,TEMPERATURE,FEELS_LIKE,PRESSURE,HUMIDITY,CLOUDS,TIME_OF_UPDATE,TIME_ZONE)"
                    +"VALUES("+"'"+cName+"','"+date+"',"+temp+","+feels_like+","+pres+","+hum+","+clouds+",'"
                    +tOfUpd+"','"+tZone+"')" +" ON CONFLICT(CITY) DO UPDATE SET DATE='"+date+"',"+"TEMPERATURE="+
            temp+",FEELS_LIKE="+feels_like+",PRESSURE="+pres+",HUMIDITY="+hum+",CLOUDS="+
            clouds+",TIME_OF_UPDATE='"+tOfUpd+"',TIME_ZONE='"+tZone+"';";
            statement.executeUpdate(current);
            statement.close();
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
                            +"VALUES("+"'"+cName+"','"+date+"',"+temp+","+feels_like+","+pres+","+hum+","+clouds+",'"
                            +tOfUpd+"','"+tZone+"')" +" ON CONFLICT(CITY,DATE) DO UPDATE SET DATE='"+date+"',"+"TEMPERATURE="+
                            temp+",FEELS_LIKE="+feels_like+",PRESSURE="+pres+",HUMIDITY="+hum+",CLOUDS="+
                            clouds+",TIME_OF_UPDATE='"+tOfUpd+"',TIME_ZONE='"+tZone+"';";
                    statement.executeUpdate(weather);
                }
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }
    public static ArrayList<Long> getUsersIDFromDB(){
        Logger logger=Logger.getGlobal();
        ArrayList<Long> users=new ArrayList<>();
        Connection connection;
        Statement statement;
        try{
            connection=getConnection();
            statement= connection.createStatement();
            ResultSet result= statement.executeQuery("SELECT * FROM USERS;");
            while(result.next()){
                long userID=result.getLong("USER_ID");
                users.add(userID);
                }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e){
            logger.warning(e.getMessage());
        }
        return users;
    }





}
