package Ability;

import java.io.Serializable;

public class CityData implements Serializable {
    static final long serialVersionUID = 7588980448693010399L;
    private String name;
    private double longitude;
    private double lalitude;
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
    public String getName(){return this.name;}
    public double getLongitude(){return this.longitude;}
    public double getLalitude(){return this.lalitude;}
}
