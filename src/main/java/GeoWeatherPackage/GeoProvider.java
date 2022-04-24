package GeoWeatherPackage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.meta.api.objects.Location;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class GeoProvider {
    public static CityData getCityData(String response){
        Logger log=Logger.getLogger("getCityData");
        try {
            Object obj = new JSONParser().parse(response);
            JSONArray jsonArray=(JSONArray) obj;
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            String city = (String) jsonObject.get("name");
            double lat=(double)jsonObject.get("lat");
            double lon=(double)jsonObject.get("lon");
            return new CityData(city,lat,lon);
        } catch (ParseException | IndexOutOfBoundsException e) {
            log.warning(e.getMessage());
        }
        return null;
    }
    public static String getLocationFromCityName(String city) {
        Logger log= Logger.getLogger("Geo provider");
        final String URL_API = "http://api.openweathermap.org/geo/1.0/direct?q=";
        String APP_ID=System.getenv("WEATHER_MAP_APP_ID");
        HttpURLConnection connection = null;
        try {
            URL u = new URL(URL_API+city+",ru_RU"+"&limit=1&appid="+APP_ID);
            connection = (HttpURLConnection) u.openConnection();
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
    public static String getCityNameFromLocation(Double latitude, Double longitude) {
        Logger log= Logger.getLogger("Geo provider");
        final String URL_API = "http://api.openweathermap.org/geo/1.0/reverse?lat=";
        final String APP_ID=System.getenv("WEATHER_MAP_APP_ID");
        HttpURLConnection connection = null;
        try {
            URL u = new URL(URL_API+latitude+"&lon="+longitude+"&limit=1&appid="+APP_ID);
            connection = (HttpURLConnection) u.openConnection();
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

    public static CityData getCityDataFromLocation(Location location){
        Logger log=Logger.getLogger("getCityDataFromLocation");
        Double lat= location.getLatitude();
        Double lon=location.getLongitude();
        String response=getCityNameFromLocation(lat,lon);
        try {
            Object obj = new JSONParser().parse(response);
            JSONArray jsonArray=(JSONArray) obj;
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            String city = (String) jsonObject.get("name");
            return new CityData(city,lat,lon);
        } catch (ParseException e) {
            log.warning(e.getMessage());
        }
        return null;
    }
}
