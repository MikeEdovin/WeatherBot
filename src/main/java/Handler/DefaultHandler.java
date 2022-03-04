package Handler;

import org.example.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

import java.util.logging.Logger;

public class DefaultHandler extends AbstractHandler{
    Logger log= Logger.getLogger("DefaultHandler");
    public DefaultHandler(Bot b){
        super(b);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        return "";
    }
}
