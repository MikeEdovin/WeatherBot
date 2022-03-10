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
        bot.sendQueue.add(getCity(chatId));
        try {
            String city = parsedCommand.getText();
            logger.info(update.getMessage().getText());
        }catch(Exception e){
            logger.warning(e.getMessage());
        }

        return null;
    }
}
