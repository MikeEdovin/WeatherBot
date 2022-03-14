package Ability;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class WeatherProvider {

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
            weatherData.setMeasurements(city, temp, pressure, humidity, feelsLikeTemp, skyIcon);
            return weatherData;
        } catch (ParseException e) {
            log.warning(e.getMessage());
        }
        return null;
    }




}
