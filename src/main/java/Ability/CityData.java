package Ability;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    public WeatherData getCurrentWeather(){
        if(isFreshForecast()) {
            return this.currentWeather;
        }else{
            return null;
        }
    }
    public WeatherData[] getForecastForSevenDays(){return this.forecastForSevenDays;}
    public boolean isFreshForecast(){
        if(currentWeather!=null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime timeOfUpdate = currentWeather.getTimeOfUpdate();
            if (now.getHour() - timeOfUpdate.getHour() < 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
