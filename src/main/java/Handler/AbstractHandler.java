package Handler;

import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;
import Ability.DBProvider;
public abstract class AbstractHandler {
    Bot bot;
    DBProvider provider;

    public AbstractHandler(Bot b, DBProvider dbProvider){
        bot=b;
        provider=dbProvider;
    }
    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);
}
