package Ability;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class WeatherData implements Serializable {
    static final long serialVersionUID = 7588980448693010399L;
    private LocalDate date;
    private double latitude;
    private double longitude;
    private double temp;
    private long pressure;
    private long humidity;
    private String city;
    private double feelsLikeTemp;
    private long clouds;
    private LocalDateTime timeOfUpdate;
    private String timeZone;

    public LocalDate getDate(){return this.date;}
    public double getTemp(){
        return this.temp;
    }
    public long getPressure(){
        return this.pressure;
    }
    public long getHumidity(){
        return this.humidity;
    }
    public long getClouds(){return this.clouds;}
    public double getFeelsLikeTemp(){return this.feelsLikeTemp;}
    public double getLatitude(){return this.latitude;}
    public double getLongitude(){return this.longitude;}
    public LocalDateTime getTimeOfUpdate(){return this.timeOfUpdate;}
    public String getTimeZone(){return this.timeZone;}
    void setCurrentMeasurements(LocalDate date,double lat, double lon,
                                double temp, long pressure, long humidity,
                                double feelsLikeTemp, Long clouds,LocalDateTime timeOfUpdate, String timeZone){
        this.date=date;
        this.latitude=lat;
        this.longitude=lon;
        this.temp=temp;
        this.pressure=pressure;
        this.humidity=humidity;
        this.feelsLikeTemp=feelsLikeTemp;
        this.clouds=clouds;
        this.timeOfUpdate=timeOfUpdate;
        this.timeZone=timeZone;
    }
}
