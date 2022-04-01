package Handler;

import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

public abstract class AbstractHandler {
    Bot bot;

    public AbstractHandler(Bot b){
        bot=b;

    }
    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);
}
