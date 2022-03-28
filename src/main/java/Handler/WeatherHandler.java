package Handler;

import Ability.*;
import Users.User;
import Users.UsersProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;
import java.util.logging.Logger;

public class WeatherHandler extends AbstractHandler{
    Logger logger= Logger.getLogger("Weather handler");


    public WeatherHandler(Bot b, UsersProvider up) {
        super(b, up);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Long userID=update.getMessage().getFrom().getId();
        if(usersProvider.getUserByID(userID)==null){
            usersProvider.addUserToList(new User(userID));
        }
        User user= usersProvider.getUserByID(userID);
        CityData currentCityData=getCurrentCityData(userID);
        Command command=parsedCommand.getCommand();
        WeatherData data;
        WeatherData[] forecast;
        String wdata;
        int nrOfDays;

        switch (command){
            case WEATHER_NOW:
                if(currentCityData==null){
                    bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                }
                else {
                    if (currentCityData.isFreshForecast()) {
                        data = currentCityData.getCurrentWeather();
                        bot.sendQueue.add(bot.sendCurrentWeather(chatId, data, currentCityData.getName()));
                    } else {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast=WeatherProvider.getForecast(wdata);
                        currentCityData.setCurrentWeather(data);
                        currentCityData.setForecastForSevenDays(forecast);
                        usersProvider.refreshUser(userID,currentCityData);
                        bot.sendQueue.add(bot.sendCurrentWeather(chatId, data, currentCityData.getName()));
                    }
                }
                break;
            case GET_CITY_FROM_INPUT:
                wdata=GeoProvider.getLocationFromCityName(update.getMessage().getText());
                CityData city = GeoProvider.getCityData(wdata);
                if(city!=null&&user.notContainCityInList(city.getName())) {
                    usersProvider.refreshUser(userID, city);
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                else if(city!=null&&!user.notContainCityInList(city.getName())){
                    usersProvider.refreshUser(userID, user.getCityDataByName(city.getName()));
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                else{
                    bot.sendQueue.add(bot.getCity(chatId));
                }
                break;
            case SET_CITY:
                bot.sendQueue.add(bot.getCity(chatId));
                break;
            case GET_FROM_LAST_THREE:
                CityData[] cities= user.getCitiesData();
                bot.sendQueue.add(bot.sendLastThree(chatId,cities));
                break;
            case ADD_CITY_TO_USER:
                CityData addingCity = GeoProvider.getCityDataFromLocation(update.getMessage().getLocation());
                if(addingCity!=null&&user.notContainCityInList(addingCity.getName())) {
                    usersProvider.refreshUser(userID, addingCity);
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                else if(addingCity!=null&&!user.notContainCityInList(addingCity.getName())){
                    usersProvider.refreshUser(userID, user.getCityDataByName(addingCity.getName()));
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                break;
            case FOR_48_HOURS:
                nrOfDays=2;
                if (currentCityData.isFreshForecast()) {
                    forecast = currentCityData.getForecastForSevenDays();
                    WeatherData f=forecast[1];
                    bot.sendQueue.add(bot.sendForecast(chatId, forecast,nrOfDays, currentCityData.getName()));
                } else {
                    wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                    data  = WeatherProvider.getCurrentWeather(wdata);
                    forecast=WeatherProvider.getForecast(wdata);
                    currentCityData.setCurrentWeather(data);
                    currentCityData.setForecastForSevenDays(forecast);
                    usersProvider.refreshUser(userID,currentCityData);
                    bot.sendQueue.add(bot.sendForecast(chatId, forecast,nrOfDays, currentCityData.getName()));
                }
                break;
            case FOR_7_DAYS:
                nrOfDays=7;
                if (currentCityData.isFreshForecast()) {
                    forecast = currentCityData.getForecastForSevenDays();
                    bot.sendQueue.add(bot.sendForecast(chatId, forecast,nrOfDays,currentCityData.getName()));
                } else {
                    wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                    data  = WeatherProvider.getCurrentWeather(wdata);
                    forecast=WeatherProvider.getForecast(wdata);
                    currentCityData.setCurrentWeather(data);
                    currentCityData.setForecastForSevenDays(forecast);
                    usersProvider.refreshUser(userID,currentCityData);
                    bot.sendQueue.add(bot.sendForecast(chatId, forecast,nrOfDays,currentCityData.getName()));
                }
                break;
        }
        return "";
    }

    private CityData getCurrentCityData(Long userID) {
        User foundedUser = usersProvider.getUserByID(userID);
        if(foundedUser.getCurrentCityData()!=null){
            return foundedUser.getCurrentCityData();
        } else {
            return null;
        }
    }


}
