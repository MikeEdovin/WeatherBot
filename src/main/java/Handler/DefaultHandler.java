package Handler;

import Ability.DBProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

public class DefaultHandler extends AbstractHandler{
    public DefaultHandler(Bot b, DBProvider provider){
        super(b,provider);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        return "";
    }
}
