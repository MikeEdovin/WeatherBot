package Handler;

import Users.UsersProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

public abstract class AbstractHandler {
    Bot bot;
    UsersProvider usersProvider;

    public AbstractHandler(Bot b, UsersProvider up){
        bot=b;
        usersProvider=up;
    }
    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);
}
