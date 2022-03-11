package Ability;

import java.util.ArrayList;

public class WeatherData {
    private double temp;
    private long pressure;
    private long humidity;
    private String city;
    private double feelsLikeTemp;


    void setTemp(double temperature){
        this.temp=temperature;
    }
    public double getTemp(){
        return this.temp;
    }
    void setPressure(long press){
        this.pressure=press;
    }
    public long getPressure(){
        return this.pressure;
    }
    void setHumidity(long hum){
        this.humidity=hum;
    }
    public long getHumidity(){
        return this.humidity;
    }
    void setCity(String city){this.city=city;}
    public String getCity(){return this.city;}
    void setFeelsLikeTemp(double temp){this.feelsLikeTemp=temp;}
    public double getFeelsLikeTemp(){return this.feelsLikeTemp;}
    void setMeasurements(String city,double temp, long pressure, long humidity,double feelsLikeTemp){
        this.city=city;
        this.temp=temp;
        this.pressure=pressure;
        this.humidity=humidity;
        this.feelsLikeTemp=feelsLikeTemp;
    }
}
