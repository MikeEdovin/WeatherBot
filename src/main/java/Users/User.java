package Users;
import Ability.CityData;
import java.io.Serializable;
import java.time.*;

public class User implements Serializable {
    static final long serialVersionUID = 7588980448693010399L;
    private final Long id;
    private LocalTime notificationTime;
    private int nrOfCities;
    private CityData currentCityData;
    private final CityData [] cityDates=new CityData[3];
    private String chatID;


    public User(Long id){
        this.id=id;
        nrOfCities=0;
    }
    public void setCurrentCityData(CityData data){this.currentCityData=data;}
    public void setChatID(String chatID){this.chatID=chatID;}

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
                if(cityDates[i]!=null&&cityDates[i].getName().equals(data.getName())){
                    cityDates[i]=data;
                }
            }
        }
    }
    public boolean notContainCityInList(String cityName){
        for (CityData cityDate : cityDates) {
            if (cityDate != null && cityDate.getName().equals(cityName)) {
                return false;
            }
        }
        return true;
    }
    public CityData getCityDataByName(String cityName){
        for (CityData cityDate : cityDates) {
            if (cityDate.getName().equals(cityName)) {
                return cityDate;
            }
        }
        return null;
    }

    public void setNotificationTime(LocalTime time){this.notificationTime=time;}
    public Long getUserID(){return this.id;}
    public String getChatID(){return this.chatID;}
    public LocalTime getNotificationTime(){return this.notificationTime;}
    public CityData getCurrentCityData(){return this.currentCityData;}
    public CityData[] getCitiesData(){return this.cityDates;}
}
