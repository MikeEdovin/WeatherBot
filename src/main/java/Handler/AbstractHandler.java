package Handler;

import DataBase.DBProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;
public abstract class AbstractHandler {
    Bot bot;
    DBProvider provider;

    public AbstractHandler(Bot b, DBProvider dbProvider){
        bot=b;
        provider=dbProvider;
    }
    public abstract void operate(String chatId, ParsedCommand parsedCommand, Update update);
}
