package Ability;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;

public class WeatherProviderTest {
    String responce="{\"lat\":59.9387,\"lon\":30.3162,\"timezone\":\"Europe/Moscow\",\"timezone_offset\":10800,\"current\":{\"dt\":1647615521,\"sunrise\":1647576432,\"sunset\":1647619595,\"temp\":6.04,\"feels_like\":3.14,\"pressure\":1040,\"humidity\":31,\"dew_point\":-8.73,\"uvi\":0.15,\"clouds\":0,\"visibility\":10000,\"wind_speed\":4,\"wind_deg\":190,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}]},\"daily\":[{\"dt\":1647597600,\"sunrise\":1647576432,\"sunset\":1647619595,\"moonrise\":1647619980,\"moonset\":1647578760,\"moon_phase\":0.5,\"temp\":{\"day\":3.17,\"min\":-4.48,\"max\":6.04,\"night\":-2.04,\"eve\":4.78,\"morn\":-4.48},\"feels_like\":{\"day\":-0.26,\"night\":-5.22,\"eve\":2.62,\"morn\":-7.86},\"pressure\":1043,\"humidity\":67,\"dew_point\":-2.41,\"wind_speed\":3.89,\"wind_deg\":201,\"wind_gust\":8.24,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":3,\"pop\":0,\"uvi\":1.84},{\"dt\":1647684000,\"sunrise\":1647662649,\"sunset\":1647706140,\"moonrise\":1647712020,\"moonset\":1647665280,\"moon_phase\":0.54,\"temp\":{\"day\":3.51,\"min\":-2.59,\"max\":3.51,\"night\":-0.27,\"eve\":0.06,\"morn\":-2.15},\"feels_like\":{\"day\":0.58,\"night\":-4.87,\"eve\":-3.73,\"morn\":-4.91},\"pressure\":1045,\"humidity\":82,\"dew_point\":0.73,\"wind_speed\":4.37,\"wind_deg\":261,\"wind_gust\":14.48,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"clouds\":86,\"pop\":0,\"uvi\":1.37},{\"dt\":1647770400,\"sunrise\":1647748867,\"sunset\":1647792686,\"moonrise\":1647804180,\"moonset\":1647751860,\"moon_phase\":0.57,\"temp\":{\"day\":4.47,\"min\":-0.57,\"max\":4.91,\"night\":0.39,\"eve\":0.96,\"morn\":-0.57},\"feels_like\":{\"day\":0.91,\"night\":-2.72,\"eve\":-2.63,\"morn\":-5.54},\"pressure\":1039,\"humidity\":79,\"dew_point\":1.14,\"wind_speed\":5.45,\"wind_deg\":272,\"wind_gust\":15.6,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":5,\"pop\":0,\"uvi\":1.57},{\"dt\":1647856800,\"sunrise\":1647835084,\"sunset\":1647879231,\"moonrise\":0,\"moonset\":1647838440,\"moon_phase\":0.61,\"temp\":{\"day\":4.78,\"min\":-1.19,\"max\":5.56,\"night\":1.23,\"eve\":3.35,\"morn\":-1.19},\"feels_like\":{\"day\":1.36,\"night\":-3.2,\"eve\":-0.62,\"morn\":-5.26},\"pressure\":1039,\"humidity\":71,\"dew_point\":0.03,\"wind_speed\":4.8,\"wind_deg\":274,\"wind_gust\":14.3,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":33,\"pop\":0,\"uvi\":1.73},{\"dt\":1647943200,\"sunrise\":1647921302,\"sunset\":1647965776,\"moonrise\":1647896640,\"moonset\":1647925140,\"moon_phase\":0.65,\"temp\":{\"day\":3.69,\"min\":-0.55,\"max\":4.53,\"night\":0.47,\"eve\":2.71,\"morn\":-0.55},\"feels_like\":{\"day\":-0.17,\"night\":-3.28,\"eve\":-0.84,\"morn\":-5.27},\"pressure\":1035,\"humidity\":73,\"dew_point\":-0.88,\"wind_speed\":4.82,\"wind_deg\":284,\"wind_gust\":14.04,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":2,\"pop\":0,\"uvi\":1.78},{\"dt\":1648029600,\"sunrise\":1648007519,\"sunset\":1648052321,\"moonrise\":1647989340,\"moonset\":1648012080,\"moon_phase\":0.68,\"temp\":{\"day\":6.36,\"min\":-0.49,\"max\":7.52,\"night\":2.19,\"eve\":3.91,\"morn\":-0.39},\"feels_like\":{\"day\":4.51,\"night\":0.46,\"eve\":1.9,\"morn\":-3.28},\"pressure\":1026,\"humidity\":61,\"dew_point\":-0.68,\"wind_speed\":3.11,\"wind_deg\":265,\"wind_gust\":9.96,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":66,\"pop\":0,\"uvi\":2},{\"dt\":1648116000,\"sunrise\":1648093736,\"sunset\":1648138866,\"moonrise\":1648081980,\"moonset\":1648099560,\"moon_phase\":0.72,\"temp\":{\"day\":5,\"min\":-1.34,\"max\":6.74,\"night\":2.71,\"eve\":5.07,\"morn\":-1.34},\"feels_like\":{\"day\":1.72,\"night\":-0.42,\"eve\":1.49,\"morn\":-3.91},\"pressure\":1022,\"humidity\":67,\"dew_point\":-0.8,\"wind_speed\":5.12,\"wind_deg\":266,\"wind_gust\":12.39,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":6,\"pop\":0,\"uvi\":2},{\"dt\":1648202400,\"sunrise\":1648179953,\"sunset\":1648225411,\"moonrise\":1648173720,\"moonset\":1648188300,\"moon_phase\":0.75,\"temp\":{\"day\":5.4,\"min\":1.44,\"max\":5.4,\"night\":2.96,\"eve\":4.14,\"morn\":1.44},\"feels_like\":{\"day\":4.23,\"night\":0.62,\"eve\":1.19,\"morn\":-0.6},\"pressure\":1022,\"humidity\":84,\"dew_point\":2.87,\"wind_speed\":4.19,\"wind_deg\":282,\"wind_gust\":11.77,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":88,\"pop\":0.36,\"rain\":0.33,\"uvi\":2}]}";
/*
    @Test
    public void getCurrentWeather() {
        long unixDate=1647615521;
        String timezone="Europe/Moscow";
        LocalDate date= Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDate();
        LocalDateTime timeOfUpdate=Instant.ofEpochSecond(unixDate).atZone(ZoneId.of(timezone)).toLocalDateTime();
        double temp=6.04;
        double feelsLike=3.14;
        long pressure=1040;
        long humidity=31;
        long clouds=0;

        WeatherData weatherData=new WeatherData();
        weatherData.setMeasurements(date,temp,pressure,humidity,feelsLike,clouds,timeOfUpdate,timezone);
        WeatherData testData=WeatherProvider.getCurrentWeather(responce);
        assertEquals(weatherData.getDate(),testData.getDate());
        assertEquals(weatherData.getTemp(),testData.getTemp(),0);
        assertEquals(weatherData.getPressure(),testData.getPressure());
        assertEquals(weatherData.getHumidity(),testData.getHumidity());
        assertEquals(weatherData.getFeelsLikeTemp(),testData.getFeelsLikeTemp(),0);
        assertEquals(weatherData.getClouds(),testData.getClouds());
        assertEquals(weatherData.getTimeOfUpdate(),testData.getTimeOfUpdate());
        assertEquals(weatherData.getTimeZone(),testData.getTimeZone());

    }

 */

}