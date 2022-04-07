package Handler;

import Ability.*;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;

public class WeatherHandler extends AbstractHandler{

    public WeatherHandler(Bot b, DBProvider provider) {
        super(b, provider);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Long userID=update.getMessage().getFrom().getId();
        if(!provider.userIsInDB(userID)){
            provider.addUserToDB(userID);
        }
        CityData currentCityData=provider.getCurrentCityDataFromDB(userID);
        WeatherData currentWeather;
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
                    currentWeather=provider.getCurrentWeatherFromDB(currentCityData);
                    if (provider.isFresh(currentWeather)&&currentWeather!=null) {
                        bot.sendQueue.add(bot.sendCurrentWeather(chatId, currentWeather, currentCityData.getName()));
                    } else {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast=WeatherProvider.getForecast(wdata);
                        provider.addCurrentWeatherToDB(data,currentCityData);
                        provider.addForecastToDB(forecast,currentCityData);
                        bot.sendQueue.add(bot.sendCurrentWeather(chatId, data, currentCityData.getName()));
                    }
                }
                break;
            case GET_CITY_FROM_INPUT:
                wdata=GeoProvider.getLocationFromCityName(update.getMessage().getText());
                CityData city = GeoProvider.getCityData(wdata);
                if(city!=null) {
                    provider.addCityToDB(city, userID);
                    provider.setCurrentCity(city, userID);
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
                CityData[] cities= provider.getLastThree(userID);
                bot.sendQueue.add(bot.sendLastThree(chatId,cities));
                break;
            case ADD_CITY_TO_USER:
                CityData addingCity = GeoProvider.getCityDataFromLocation(update.getMessage().getLocation());
                if(addingCity!=null) {
                    provider.addCityToDB(addingCity,userID);
                    provider.setCurrentCity(addingCity,userID);
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                break;
            case FOR_48_HOURS:
                nrOfDays=2;
                if(currentCityData==null){
                    bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                }
                else {
                    forecast=provider.getForecastFromDB(currentCityData);
                    if (forecast[0] == null || !provider.isFresh(forecast[0])) {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast = WeatherProvider.getForecast(wdata);
                        provider.addCurrentWeatherToDB(data, currentCityData);
                        provider.addForecastToDB(forecast, currentCityData);
                    }
                    bot.sendQueue.add(bot.sendForecast(chatId, forecast, nrOfDays, currentCityData.getName()));
                }
                break;
            case FOR_7_DAYS:
                nrOfDays=7;
                if(currentCityData==null){
                    bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                }
                else {
                    forecast=provider.getForecastFromDB(currentCityData);
                    if (forecast[0] == null || !provider.isFresh(forecast[0])) {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getCurrentWeather(wdata);
                        forecast = WeatherProvider.getForecast(wdata);
                        provider.addCurrentWeatherToDB(data, currentCityData);
                        provider.addForecastToDB(forecast, currentCityData);
                    }
                    bot.sendQueue.add(bot.sendForecast(chatId, forecast, nrOfDays, currentCityData.getName()));
                }
                break;
        }
        return "";
    }
}
