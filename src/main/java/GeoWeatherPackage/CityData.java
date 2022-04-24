package GeoWeatherPackage;
public class CityData {
    private final String name;
    private final double longitude;
    private final double lalitude;

    public CityData(String name, double lat, double lon){
        this.name=name;
        this.longitude=lon;
        this.lalitude=lat;
    }

    public String getName(){return this.name;}
    public double getLongitude(){return this.longitude;}
    public double getLatitude(){return this.lalitude;}

}
