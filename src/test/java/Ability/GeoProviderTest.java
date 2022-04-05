package Ability;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Location;

import static org.junit.Assert.*;

public class GeoProviderTest {
    String geoResponse= "[{\"name\":\"Saint Petersburg\",\"local_names\":{\"en\":\"Saint Petersburg\",\"sl\":\"Sankt Peterburg\",\"lb\":\"Sankt Péitersbuerg\",\"ca\":\"Sant Petersburg\",\"uk\":\"Санкт-Петербург\",\"et\":\"Peterburi\",\"ar\":\"سانت بطرسبرغ\",\"de\":\"Sankt Petersburg\",\"os\":\"Бетъырбух\",\"da\":\"Sankt Petersborg\",\"ja\":\"サンクト ペテルブルク\",\"fy\":\"Sint-Petersburch\",\"mk\":\"Санкт Петербург\",\"be\":\"Санкт-Пецярбург\",\"fa\":\"سن پترزبورگ\",\"sv\":\"Sankt Petersburg\",\"sk\":\"Petrohrad\",\"ro\":\"Sankt Petersburg\",\"hr\":\"Sankt Petersburg\",\"es\":\"San Petersburgo\",\"lt\":\"Sankt Peterburgas\",\"eo\":\"Sankt-Peterburgo\",\"ml\":\"സെന്റ് പീറ്റേഴ്സ്ബർഗ്\",\"fr\":\"Saint-Pétersbourg\",\"pt\":\"São Petersburgo\",\"it\":\"San Pietroburgo\",\"el\":\"Αγία Πετρούπολη\",\"ab\":\"Санқт-Петербург\",\"vi\":\"Xanh Pê-téc-bua\",\"feature_name\":\"Sankt-Peterburg\",\"hu\":\"Szentpétervár\",\"nb\":\"Sankt Petersburg\",\"hy\":\"Սանկտ Պետերբուրգ\",\"nl\":\"Sint-Petersburg\",\"sr\":\"Санкт Петербург\",\"mr\":\"सेंट पीटर्सबर्ग\",\"kn\":\"ಸಂಕ್ತ್ ಪೇಟೆರ್ಬುಗ್\",\"hi\":\"सेंट पीटर्सबर्ग\",\"te\":\"సెయింట్ పీటర్స్\u200Cబర్గ్\",\"pl\":\"Petersburg\",\"gl\":\"San Petersburgo\",\"zh\":\"聖彼得堡\",\"fi\":\"Pietari\",\"ka\":\"სანქტ-პეტერბურგი\",\"ku\":\"Sankt Petersburg\",\"lv\":\"Sanktpēterburga\",\"cs\":\"Petrohrad\",\"ascii\":\"Sankt-Peterburg\",\"ru\":\"Санкт-Петербург\",\"oc\":\"Sant Petersborg\"},\"lat\":59.938732,\"lon\":30.316229,\"country\":\"RU\",\"state\":\"Saint Petersburg\"}]";


    @Test
    public void getCityData() {
        double latitude=59.9387;
        double longitude=30.3162;
        CityData cityData=new CityData();
        cityData.setCityData("Saint Petersburg",longitude,latitude);
        CityData result=GeoProvider.getCityData(geoResponse);
        assertEquals(cityData.getName(),result.getName());
        assertEquals(cityData.getLatitude(),result.getLatitude(),1);
        assertEquals(cityData.getLongitude(),cityData.getLongitude(),1);
    }

    @Test
    public void getLocationFromCityName() {
        String name="Санкт-Петербург";
        double latitude=59.9387;
        double longitude=30.3162;
        CityData cityData=new CityData();
        cityData.setCityData("Saint Petersburg",longitude,latitude);
        CityData result=GeoProvider.getCityData(GeoProvider.getLocationFromCityName(name));
        assertEquals(cityData.getName(),result.getName());
        assertEquals(cityData.getLatitude(),result.getLatitude(),0.01);
        assertEquals(cityData.getLongitude(),cityData.getLongitude(),0.01);


    }



    @Test
    public void getCityNameFromLocation() {
        double latitude=59.9387;
        double longitude=30.3162;
        CityData cityData=new CityData();
        cityData.setCityData("Saint Petersburg",longitude,latitude);
        CityData result=GeoProvider.getCityData(GeoProvider.getCityNameFromLocation(latitude,longitude));
        assertEquals(cityData.getName(),result.getName());
        assertEquals(cityData.getLatitude(),result.getLatitude(),0.01);
        assertEquals(cityData.getLongitude(),cityData.getLongitude(),0.01);

    }

    @Test
    public void getCityDataFromLocation() {
        double latitude=59.9387;
        double longitude=30.3162;
        Location location=new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        CityData cityData=new CityData();
        cityData.setCityData("Saint Petersburg",longitude,latitude);
        CityData result=GeoProvider.getCityDataFromLocation(location);
        assertEquals(cityData.getName(),result.getName());
        assertEquals(cityData.getLatitude(),result.getLatitude(),1);
        assertEquals(cityData.getLongitude(),cityData.getLongitude(),1);
    }
}