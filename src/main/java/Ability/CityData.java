package Ability;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

public class CityData implements Serializable {
    static final long serialVersionUID = 7588980448693010399L;
    private String name;
    private double longitude;
    private double lalitude;
    private WeatherData currentWeather;
    private WeatherData[] forecastForSevenDays =new WeatherData[10];
    public CityData(){}

    public CityData(String name,double lon,double lat){
        this.name=name;
        this.longitude=lon;
        this.lalitude=lat;
    }
    public void setCityData(String name,double lon,double lat){
        this.name=name;
        this.longitude=lon;
        this.lalitude=lat;
    }
    public void setCurrentWeather(WeatherData data){this.currentWeather=data;}
    public void setForecastForSevenDays(WeatherData[] forecast){this.forecastForSevenDays =forecast;}
    public String getName(){return this.name;}
    public double getLongitude(){return this.longitude;}
    public double getLalitude(){return this.lalitude;}
    public WeatherData getCurrentWeather(){return this.currentWeather;}
    public WeatherData[] getForecastForSevenDays(){return this.forecastForSevenDays;}
    public boolean isFreshForecast(){
        if(currentWeather!=null) {
            String zone= currentWeather.getTimeZone();
            if(zone!=null) {
                ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(currentWeather.getTimeZone()));
                Long now = zdtNow.toEpochSecond();
                LocalDateTime timeOfUpdate = currentWeather.getTimeOfUpdate();
                ZonedDateTime zdtTimeOfUpdate = ZonedDateTime.of(timeOfUpdate, ZoneId.of(currentWeather.getTimeZone()));
                Long tOfUpdate = zdtTimeOfUpdate.toEpochSecond();
                if (now - tOfUpdate < 3600) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
