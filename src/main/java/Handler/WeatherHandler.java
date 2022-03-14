package Handler;

import Ability.CityData;
import Ability.GeoProvider;
import Ability.WeatherData;
import Ability.WeatherProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

import java.util.logging.Logger;

public class WeatherHandler extends AbstractHandler{
    Logger logger= Logger.getLogger("Weather handler");
    private final String END_LINE = "\n";

    public WeatherHandler(Bot b) {
        super(b);
    }
    private SendMessage getCity(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        sendMessage.setText("Please, type the city name ");
        return sendMessage;
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        String geo= GeoProvider.getCoordinatesFromCityName("санкт-петербург");
        CityData cityData=GeoProvider.getCityData(geo);
        String wdata=WeatherProvider.getWeatherInformation("Saint Petersburg");
        WeatherData data=WeatherProvider.getWeatherData(wdata.toString());
        bot.sendQueue.add(sendCurrentForecast(chatId,data));
        bot.sendQueue.add(sendCityCoordinates(chatId,cityData));
        bot.sendQueue.add(sendImageFromUrl(chatId,data.getWeatherIcon()));
        return "";

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

    private SendMessage sendCityCoordinates(String chatID, CityData cityData){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("City "+cityData.getName()).append(END_LINE);
        text.append("longitude "+cityData.getLongitude()).append(END_LINE);
        text.append("latitude "+cityData.getLalitude()).append(END_LINE);
        message.setText(text.toString());
        return message;
    }
    public SendPhoto sendImageFromUrl(String chatId, String skyIcon) {
        String iconURL="https://openweathermap.org/img/wn/"+skyIcon+"@2x.png";
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setPhoto(new InputFile(iconURL));
        return sendPhotoRequest;
    }
}
