package Users;
import java.io.Serializable;
import java.time.*;

public class User implements Serializable {
    static final long serialVersionUID = 7588980448693010399L;
    private Long id;
    String currentCity;
    String [] cities=new String[3];
    LocalTime notificationTime;
    int nrOfCities;


    public User(Long id){
        this.id=id;
        nrOfCities=0;
    }
    public void setCurrentCity(String city){this.currentCity=city;}
    public void addCityToList(String city) {
        if (nrOfCities<3) {
            cities[nrOfCities++]=city;
        }
        else{
            cities[0]=cities[1];
            cities[1]=cities[2];
            cities[2]=city;
        }


    }
    public void setNotificationTime(LocalTime time){
        this.notificationTime=time;
    }
    public Long getUserID(){return this.id;}
    public String getCurrentCity(){return this.currentCity;}
    public String[]getCities(){return this.cities;}
    public LocalTime getNotificationTime(){return this.notificationTime;}


}
