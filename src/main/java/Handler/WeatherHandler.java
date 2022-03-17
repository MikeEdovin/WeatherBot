package Handler;

import Ability.CityData;
import Ability.GeoProvider;
import Ability.WeatherData;
import Ability.WeatherProvider;
import Users.User;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
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
        String city=getCurrentCity(userID);
        User user= usersProvider.getUserByID(userID);
        Command command=parsedCommand.getCommand();
        switch (command){
            case WEATHER_NOW:
                if(city==null){
                    bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                }
                else {
                    String wdata = WeatherProvider.getWeatherInformation(city);
                    WeatherData data = WeatherProvider.getWeatherData(wdata);
                    bot.sendQueue.add(sendCurrentForecast(chatId, data));
                }
                break;
            case GET_CITY_FROM_INPUT:
                String data=GeoProvider.getLocationFromCityName(update.getMessage().getText());
                CityData cityData=GeoProvider.getCityData(data);
                usersProvider.refreshUser(userID,cityData.getName());
                bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                break;
            case SET_CITY:
                bot.sendQueue.add(getCity(chatId));
                break;
            case GET_FROM_LAST_THREE:
                String[] cities= user.getCities();
                bot.sendQueue.add(sendLastThree(chatId,cities));
                break;
            case ADD_CITY_TO_USER:
                String addingCity = GeoProvider.getCityDataFromLocation(update.getMessage().getLocation()).getName();
                usersProvider.refreshUser(userID, addingCity);
                bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                break;
        }
        return "";

    }
    private SendMessage sendLastThree(String chatID, String[]cities){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Choose from last three cities");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        for(String item:cities) {
            KeyboardRow row = new KeyboardRow();
            row.add(item);
            keyboard.add(row);
        }
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setOneTimeKeyboard(true);
        return message;
    }
    private String getCurrentCity(Long userID) {
        User foundedUser = usersProvider.getUserByID(userID);
        System.out.println(foundedUser.getUserID()+foundedUser.getCurrentCity());
        if(foundedUser.getCurrentCity()!=null){
            System.out.println("user " + foundedUser.getUserID()+foundedUser.getCurrentCity());
            return foundedUser.getCurrentCity();
        } else {
            return null;
        }
    }
    private SendMessage sendCurrentForecast(String chatID,WeatherData data){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("Current weather").append(END_LINE).append(END_LINE);
        text.append("City "+data.getCity()).append(END_LINE);
        text.append("Temperature "+data.getTemp()).append(END_LINE);
        text.append("Pressure "+data.getPressure()).append(END_LINE);
        text.append("Humidity "+data.getHumidity()).append(END_LINE);
        text.append("Feels like temperature "+data.getFeelsLikeTemp()).append(END_LINE);
        text.append("weather icon "+data.getWeatherIcon()).append(END_LINE);

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
