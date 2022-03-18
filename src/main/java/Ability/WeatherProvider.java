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
import java.time.ZoneId;
import java.util.logging.Logger;

public class WeatherProvider {
    public static String getOneCallAPI(Double latitude, Double longitude){
        Logger log= Logger.getLogger("One call API");
        //получение данных о погоде с сервера в формате json
        final String URL_API = "https://api.openweathermap.org/data/2.5/onecall?";
        final String APP_ID = "5a1a2ebae8f3c31263be33c36cdc727c";
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

    public static String getWeatherInformation(String city) {
        Logger log= Logger.getLogger("Weather provider");
        //получение данных о погоде с сервера в формате json
        final String URL_API = "Https://api.openweathermap.org/data/2.5/weather";
        final String APP_ID = "5a1a2ebae8f3c31263be33c36cdc727c";
        HttpsURLConnection connection = null;
        try {
            URL u = new URL(URL_API + "?q=" + city + "&appid=" + APP_ID + "&units=metric");
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
    public static String getWeatherInformation(Double latitude, Double longitude) {
        Logger log= Logger.getLogger("Weather provider");
        //получение данных о погоде с сервера в формате json
        final String URL_API = "https://api.openweathermap.org/data/2.5/weather?";
        final String APP_ID = "5a1a2ebae8f3c31263be33c36cdc727c";
        HttpsURLConnection connection = null;
        try {
            URL u = new URL(URL_API + "lat=" + latitude+"&lon="+longitude
                    + "&appid=" + APP_ID + "&units=metric");
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

/*
    public static WeatherData getWeatherData(String response) {
        Logger log=Logger.getLogger("get weather data");
        // парсинг ответа от сервера. Извлекаем четыре значения-город, температура,
        // давление и влажность. Возвращаем объект weatherData.
        WeatherData weatherData = new WeatherData();

        try {
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObject = (JSONObject) obj;
            String city = (String) jsonObject.get("name");
            JSONObject objMain = (JSONObject) jsonObject.get("main");
            double temp = (double) objMain.get("temp");
            long pressure = (long) objMain.get("pressure");
            long humidity = (long) objMain.get("humidity");
            double feelsLikeTemp = (double) objMain.get("feels_like");
            JSONArray arrWeather = (JSONArray) jsonObject.get("weather");
            JSONObject objWeather= (JSONObject) arrWeather.get(0);
            String skyIcon=(String)objWeather.get("icon");
            String iconURL="https://openweathermap.org/img/wn/"+skyIcon+"@2x.png";
            weatherData.setCurrentMeasurements(city, temp, pressure, humidity, feelsLikeTemp, skyIcon);
            return weatherData;
        } catch (ParseException e) {
            log.warning(e.getMessage());
        }
        return null;
    }

 */
    public static WeatherData getOneCallData(String response){
        long unixDate;
        LocalDate date;
        double temp;
        double feelsLike;
        long pressure;
        long humidity;
        long clouds;
        double latitude;
        double longitude;
        WeatherData weatherData=new WeatherData();
        try{
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject objCurrent = (JSONObject) jsonObject.get("current");
            latitude= (double) jsonObject.get("lat");
            longitude= (double) jsonObject.get("lon");
            String timezone= (String) jsonObject.get("timezone");
            unixDate= (long) objCurrent.get("dt");
            date= Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDate();
            temp= (double) objCurrent.get("temp");
            feelsLike= (double) objCurrent.get("feels_like");
            pressure = (long) objCurrent.get("pressure");
            humidity = (long) objCurrent.get("humidity");
            clouds= (long) objCurrent.get("clouds");
            weatherData.setCurrentMeasurements(date,latitude,longitude,temp,pressure,humidity,feelsLike,clouds);
            JSONArray daily= (JSONArray) jsonObject.get("daily");
            /*
            for(int i=0;i<daily.size();i++) {
                JSONObject day = (JSONObject) daily.get(i);
                unixDate = (long) day.get("dt");
                date = Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDate();
                JSONArray tempArray = (JSONArray) objCurrent.get("temp");
                JSONObject tempObject= (JSONObject) tempArray.get(0);
                temp= (double) tempObject.get("day");
                JSONArray feelsLikeArray = (JSONArray) objCurrent.get("feels_like");
                JSONObject feelsLikeObject= (JSONObject) feelsLikeArray.get(0);
                feelsLike= (double) feelsLikeObject.get("day");
                pressure = (long) day.get("pressure");
                humidity = (long) day.get("humidity");
                clouds = (long) day.get("clouds");
            }

             */
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weatherData;
    }




}
