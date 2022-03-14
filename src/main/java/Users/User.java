package Users;
import java.time.*;
public class User {
    long id;
    String currentCity;
    String[]cities;
    LocalTime notificationTime;
    int nrOfCities;

    public User(long id){
        this.id=id;
        nrOfCities=0;
    }
    public void setCurrentCity(String city){this.currentCity=city;}
    public void addCityToList(String city){
        cities[nrOfCities++]=city;
    }
    public void setNotificationTime(LocalTime time){
        this.notificationTime=time;
    }
    public String getCurrentCity(){return this.currentCity;}
    public String[]getCities(){return this.cities;}
    public LocalTime getNotificationTime(){return this.notificationTime;}

    public boolean isExist(){return this.id!=0;}


}
