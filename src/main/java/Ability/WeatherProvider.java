package Ability;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

public class WeatherProvider {
    public static String getOneCallAPI(Double latitude, Double longitude){//получение данных о погоде с сервера
        Logger log= Logger.getLogger("One call API");
        String APP_ID=System.getenv("WEATHER_MAP_APP_ID");
        final String URL_API = "https://api.openweathermap.org/data/2.5/onecall?";
        HttpsURLConnection connection = null;
        try {
            URL u = new URL(URL_API + "lat=" + latitude+"&lon="+longitude
                    +"&exclude=minutely,hourly,alerts"+"&appid=" + APP_ID + "&units=metric");
            connection = (HttpsURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                log.warning("Server returned status code: " + responseCode);
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    stringBuilder.append(s);
                }
            } catch (Exception e) {
                log.warning(e.getMessage());
            }
            connection.disconnect();
            return stringBuilder.toString();

        } catch (IOException e) {
            log.warning(e.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    public static WeatherData getCurrentWeather(String response){
        long unixDate;
        LocalDate date;
        LocalDateTime timeOfUpdate;
        double temp=0d;
        double feelsLike=0d;
        long pressure;
        long humidity;
        long clouds;
        WeatherData currentWeatherData=new WeatherData();
        try{
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject objCurrent = (JSONObject) jsonObject.get("current");
            String timezone= (String) jsonObject.get("timezone");
            unixDate= (long) objCurrent.get("dt");
            date= Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDate();
            timeOfUpdate=Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDateTime();
            Object t=objCurrent.get("temp");
            if(t instanceof Double) {
                temp = (double) t;
            }else if(t instanceof Long) {
                temp= ((Long) t).doubleValue();
            }
            Object fl=objCurrent.get("feels_like");
            if(fl instanceof Double){
                feelsLike= (double) fl;
            }
            else if(fl instanceof Long){
                feelsLike=((Long) fl).doubleValue();
            }
            pressure = (long) objCurrent.get("pressure");
            humidity = (long) objCurrent.get("humidity");
            clouds= (long) objCurrent.get("clouds");
            currentWeatherData.setMeasurements(date, temp,pressure,humidity,feelsLike,clouds,timeOfUpdate,timezone);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentWeatherData;
    }
    public static WeatherData[]getForecast(String response) {
        long unixUpdateTime;
        long unixDate;
        LocalDate date;
        LocalDateTime timeOfUpdate;
        double temp=0d;
        double feelsLike=0d;
        long pressure;
        long humidity;
        long clouds;
        String timezone;
        WeatherData[]forecast=new WeatherData[10];
        try {
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject objCurrent = (JSONObject) jsonObject.get("current");
            unixUpdateTime= (long) objCurrent.get("dt");
            timezone= (String) jsonObject.get("timezone");
            timeOfUpdate = Instant.ofEpochSecond(unixUpdateTime).atZone(ZoneId.of(timezone)).toLocalDateTime();
            JSONArray daily = (JSONArray) jsonObject.get("daily");
            for (int i = 0; i < daily.size(); i++) {
                WeatherData weatherData = new WeatherData();
                JSONObject day = (JSONObject) daily.get(i);
                unixDate = (long) day.get("dt");
                date = Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDate();
                JSONObject tempObject = (JSONObject) day.get("temp");
                Object t=tempObject.get("day");
                if(t instanceof Double){
                    temp=(double) t;
                }
                else if(t instanceof Long){
                    temp=((Long) t).doubleValue();
                }
                JSONObject feelsLikeObject = (JSONObject) day.get("feels_like");
                Object fl=feelsLikeObject.get("day");
                if(fl instanceof Double){
                    feelsLike = (double) fl;
                }
                else if(fl instanceof Long){
                    feelsLike=((Long) fl).doubleValue();
                }
                pressure = (long) day.get("pressure");
                humidity = (long) day.get("humidity");
                clouds = (long) day.get("clouds");
                weatherData.setMeasurements(date, temp, pressure, humidity, feelsLike, clouds, timeOfUpdate,timezone);
                forecast[i]=weatherData;
            }
        } catch (ParseException|ClassCastException e) {
        e.printStackTrace();
    }
        return forecast;
    }




}
