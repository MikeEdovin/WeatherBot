package Users;
import Ability.CityData;

import java.io.Serializable;
import java.time.*;

public class User implements Serializable {
    static final long serialVersionUID = 7588980448693010399L;
    private Long id;
    private LocalTime notificationTime;
    private int nrOfCities;
    private CityData currentCityData;
    private CityData [] cityDates=new CityData[3];


    public User(Long id){
        this.id=id;
        nrOfCities=0;
    }
    public void setCurrentCityData(CityData data){this.currentCityData=data;}

    public void addCityDataToList(CityData data){
        if (nrOfCities<3) {
            cityDates[nrOfCities++]=data;
        }
        else{
            cityDates[0]=cityDates[1];
            cityDates[1]=cityDates[2];
            cityDates[2]=data;
        }
    }

    public void setNotificationTime(LocalTime time){
        this.notificationTime=time;
    }
    public Long getUserID(){return this.id;}
    public LocalTime getNotificationTime(){return this.notificationTime;}
    public CityData getCurrentCityData(){return this.currentCityData;}
    public CityData[] getCitiesData(){return this.cityDates;}


}
