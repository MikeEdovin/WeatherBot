package Ability;

import org.example.Bot;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;
import telegramBot.commands.Parser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WeatherProvider {

    public static String getWeatherInformation(String city) {
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
                System.err.println("Server returned status code: " + responseCode);
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    stringBuilder.append(s);
                    System.out.println(stringBuilder.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.disconnect();
            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return null;
    }


    public static WeatherData getWeatherData(String response) {
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
            weatherData.setMeasurements(city, temp, pressure, humidity, feelsLikeTemp);
            return weatherData;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }




}
