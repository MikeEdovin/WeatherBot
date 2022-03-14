package Ability;

public class CityData {
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
