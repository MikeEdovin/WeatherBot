package Handler;

import Ability.WeatherData;
import Ability.WeatherProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

import java.util.logging.Logger;

public class WeatherHandler extends AbstractHandler{
    Logger logger= Logger.getLogger("Weather handler");

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
        String wdata=WeatherProvider.getWeatherInformation("Saint Petersburg");
        String response = wdata.toString();
        bot.sendQueue.add(response);
return response;

    }
}
