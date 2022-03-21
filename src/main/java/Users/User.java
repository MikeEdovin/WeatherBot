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
        if(notContainCityInList(data.getName())) {
            if (nrOfCities < 3) {
                cityDates[nrOfCities++] = data;
            } else {
                cityDates[0] = cityDates[1];
                cityDates[1] = cityDates[2];
                cityDates[2] = data;
            }
        }else{
            for(int i=0;i< cityDates.length;i++){
                if(cityDates[i].getName().equals(data.getName())){
                    cityDates[i]=data;
                }
            }
        }
    }
    public boolean notContainCityInList(String cityName){
        for(int i=0;i<cityDates.length;i++){
            if(cityDates[i].getName().equals(cityName)){
                return false;
            }
        }
        return true;
    }
    public CityData getCityDataByName(String cityName){
        for(int i=0;i<cityDates.length;i++){
            if(cityDates[i].getName().equals(cityName)){
                return cityDates[i];
            }
        }
        return null;
    }

    public void setNotificationTime(LocalTime time){
        this.notificationTime=time;
    }
    public Long getUserID(){return this.id;}
    public LocalTime getNotificationTime(){return this.notificationTime;}
    public CityData getCurrentCityData(){return this.currentCityData;}
    public CityData[] getCitiesData(){return this.cityDates;}


}
