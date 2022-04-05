package Handler;

import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

public class DefaultHandler extends AbstractHandler{
    public DefaultHandler(Bot b){
        super(b);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        return "";
    }
}
