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


    public WeatherHandler(Bot b) {
        super(b);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Long userID=update.getMessage().getFrom().getId();
        if(DBProvider.userIsInDB(userID)==false){
            DBProvider.addUserToDB(userID);
        }
        CityData currentCityData=DBProvider.getCurrentCityDataFromDB(userID);
        WeatherData currentWeather=DBProvider.getCurrentWeatherFromDB(currentCityData);
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
                    if (DBProvider.isFresh(currentWeather)) {
                        System.out.println("from base");
                        bot.sendQueue.add(bot.sendCurrentWeather(chatId, currentWeather, currentCityData.getName()));
                    } else {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast=WeatherProvider.getForecast(wdata);
                        // db
                        DBProvider.addCurrentWeatherToDB(data,currentCityData);
                        DBProvider.addForecastToDB(forecast,currentCityData);
                        bot.sendQueue.add(bot.sendCurrentWeather(chatId, data, currentCityData.getName()));
                    }
                }
                break;
            case GET_CITY_FROM_INPUT:
                wdata=GeoProvider.getLocationFromCityName(update.getMessage().getText());
                CityData city = GeoProvider.getCityData(wdata);
                if(city!=null) {
                    DBProvider.addCityToDB(city, userID);
                    DBProvider.setCurrentCity(city, userID);
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
                CityData[] cities= DBProvider.getLastThree(userID);
                bot.sendQueue.add(bot.sendLastThree(chatId,cities));
                break;
            case ADD_CITY_TO_USER:
                CityData addingCity = GeoProvider.getCityDataFromLocation(update.getMessage().getLocation());
                if(addingCity!=null) {
                    DBProvider.addCityToDB(addingCity,userID);
                    DBProvider.setCurrentCity(addingCity,userID);
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                break;
            case FOR_48_HOURS:
                nrOfDays=2;
                if(currentCityData==null){
                    bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                }
                else {
                    if (DBProvider.isFresh(currentWeather)) {
                        forecast=DBProvider.getForecastFromDB(currentCityData);
                        bot.sendQueue.add(bot.sendForecast(chatId, forecast, nrOfDays, currentCityData.getName()));
                    } else {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast = WeatherProvider.getForecast(wdata);
                        //DB
                        DBProvider.addCurrentWeatherToDB(data,currentCityData);
                        DBProvider.addForecastToDB(forecast,currentCityData);
                        bot.sendQueue.add(bot.sendForecast(chatId, forecast, nrOfDays, currentCityData.getName()));
                    }
                }
                break;
            case FOR_7_DAYS:
                nrOfDays=7;
                if(currentCityData==null){
                    bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                }
                else {
                    if (DBProvider.isFresh(currentWeather)) {
                        forecast=DBProvider.getForecastFromDB(currentCityData);
                        bot.sendQueue.add(bot.sendForecast(chatId, forecast, nrOfDays, currentCityData.getName()));
                    } else {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast = WeatherProvider.getForecast(wdata);
                        //db
                        DBProvider.addCurrentWeatherToDB(data,currentCityData);
                        DBProvider.addForecastToDB(forecast,currentCityData);
                        bot.sendQueue.add(bot.sendForecast(chatId, forecast, nrOfDays, currentCityData.getName()));
                    }
                }
                break;
        }
        return "";
    }
}
