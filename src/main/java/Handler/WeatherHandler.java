package Handler;

import Ability.*;
import Users.User;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WeatherHandler extends AbstractHandler{
    Logger logger= Logger.getLogger("Weather handler");
    private final String END_LINE = "\n";

    public WeatherHandler(Bot b, UsersProvider up) {
        super(b, up);
    }


    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Long userID=update.getMessage().getFrom().getId();
        CityData currentCityData=getCurrentCityData(userID);
        User user= usersProvider.getUserByID(userID);
        Command command=parsedCommand.getCommand();
        WeatherData data;
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
                        System.out.println("Saved current "+data.getTimeOfUpdate()+data.getTemp());
                        bot.sendQueue.add(sendCurrentWeather(chatId, data, currentCityData.getName()));
                    } else {
                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLalitude(), currentCityData.getLongitude());
                        data = WeatherProvider.getOneCallData(wdata);
                        WeatherData[]forecast=WeatherProvider.getForecast(wdata);
                        currentCityData.setCurrentWeather(data);
                        currentCityData.setForecastForSevenDays(forecast);
                        usersProvider.refreshUser(userID,currentCityData);
                        bot.sendQueue.add(sendCurrentWeather(chatId, data, currentCityData.getName()));
                    }
                }
                break;
            case GET_CITY_FROM_INPUT:
                wdata=GeoProvider.getLocationFromCityName(update.getMessage().getText());
                CityData city = GeoProvider.getCityData(wdata);
                if(city!=null) {
                    usersProvider.refreshUser(userID, city);
                    bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                }
                else{
                    bot.sendQueue.add(getCity(chatId));
                }
                break;
            case SET_CITY:
                bot.sendQueue.add(getCity(chatId));
                break;
            case GET_FROM_LAST_THREE:
                CityData[] cities= user.getCitiesData();
                bot.sendQueue.add(sendLastThree(chatId,cities));
                break;
            case ADD_CITY_TO_USER:
                CityData addingCity = GeoProvider.getCityDataFromLocation(update.getMessage().getLocation());
                usersProvider.refreshUser(userID, addingCity);
                bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                break;
            case FOR_48_HOURS:
                nrOfDays=2;
                if (currentCityData.isFreshForecast()) {
                    WeatherData[] forecast = currentCityData.getForecastForSevenDays();
                    WeatherData f=forecast[1];
                    System.out.println("Saved forecast "+f.getTimeOfUpdate());
                    bot.sendQueue.add(sendForecast(chatId, forecast,nrOfDays, currentCityData.getName()));
                } else {
                    wdata = WeatherProvider.getOneCallAPI(currentCityData.getLalitude(), currentCityData.getLongitude());
                    data  = WeatherProvider.getOneCallData(wdata);
                    WeatherData[]forecast=WeatherProvider.getForecast(wdata);
                    currentCityData.setCurrentWeather(data);
                    currentCityData.setForecastForSevenDays(forecast);
                    usersProvider.refreshUser(userID,currentCityData);
                    bot.sendQueue.add(sendForecast(chatId, forecast,nrOfDays, currentCityData.getName()));
                }
                break;
            case FOR_7_DAYS:
                nrOfDays=7;
                if (currentCityData.isFreshForecast()) {
                    WeatherData[] forecast = currentCityData.getForecastForSevenDays();
                    System.out.println("Saved forecast "+currentCityData.getCurrentWeather().getTimeOfUpdate());
                    bot.sendQueue.add(sendForecast(chatId, forecast,nrOfDays,currentCityData.getName()));
                } else {
                    wdata = WeatherProvider.getOneCallAPI(currentCityData.getLalitude(), currentCityData.getLongitude());
                    data  = WeatherProvider.getOneCallData(wdata);
                    WeatherData[]forecast=WeatherProvider.getForecast(wdata);
                    currentCityData.setCurrentWeather(data);
                    currentCityData.setForecastForSevenDays(forecast);
                    usersProvider.refreshUser(userID,currentCityData);
                    bot.sendQueue.add(sendForecast(chatId, forecast,nrOfDays,currentCityData.getName()));
                }
                break;

        }
        return "";

    }
    private SendMessage sendLastThree(String chatID, CityData[]cities){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Choose from last three cities");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        for(CityData item:cities) {
            if (item != null) {
                KeyboardRow row = new KeyboardRow();
                row.add(item.getName());
                keyboard.add(row);
            }
        }
        KeyboardRow row=new KeyboardRow();
        row.add("Back"+ Emojies.BACK.getEmoji());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setOneTimeKeyboard(true);
        return message;
    }
    private CityData getCurrentCityData(Long userID) {
        User foundedUser = usersProvider.getUserByID(userID);
        if(foundedUser.getCurrentCityData()!=null){
            return foundedUser.getCurrentCityData();
        } else {
            return null;
        }
    }
    private SendMessage sendCurrentWeather(String chatID, WeatherData data, String cityNamme){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("Current weather for "+cityNamme).append(END_LINE).append(END_LINE);
        text.append("Current date "+data.getDate()).append(END_LINE);
        text.append("Temperature "+data.getTemp()).append(END_LINE);
        text.append("Feels like temperature "+data.getFeelsLikeTemp()).append(END_LINE);
        text.append("Pressure "+data.getPressure()).append(END_LINE);
        text.append("Humidity "+data.getHumidity()).append(END_LINE);
        text.append("clouds "+data.getClouds()).append(END_LINE);
        text.append("Update time "+data.getTimeOfUpdate()).append(END_LINE);
        message.setText(text.toString());
        return message;
    }
    private SendMessage sendForecast(String chatID, WeatherData[] forecast, int nrOfDays, String cityName){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("Forecast for "+cityName).append(END_LINE).append(END_LINE);
        for(int i=0;i< nrOfDays;i++){
            WeatherData data=forecast[i];
            if(data!=null) {
                text.append("Date " + data.getDate()).append(END_LINE);
                text.append("Temperature " + data.getTemp()).append(END_LINE);
                text.append("Feels like temperature " + data.getFeelsLikeTemp()).append(END_LINE);
                text.append("Pressure " + data.getPressure()).append(END_LINE);
                text.append("Humidity " + data.getHumidity()).append(END_LINE);
                text.append("clouds " + data.getClouds()).append(END_LINE);
                text.append("Update time " + data.getTimeOfUpdate()).append(END_LINE).append(END_LINE);
            }
        }
        message.setText(text.toString());
        return message;
    }
    private SendMessage getCity(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        sendMessage.setText("Please, type the city name ");
        return sendMessage;
    }

}
