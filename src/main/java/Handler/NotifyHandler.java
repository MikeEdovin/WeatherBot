package Handler;

import Ability.Notify;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

import java.util.logging.Logger;

public class NotifyHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger("NotifyHandler");
    private final int MILLISEC_IN_SEC = 1000;
    private String WRONG_INPUT_MESSAGE = "Wrong input. Time must be specified as an integer greater than 0";

    public NotifyHandler(Bot b, UsersProvider up){
        super(b, up);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        String text = parsedCommand.getText();
        if ("".equals(text))
            return "You must specify the delay time. Like this:\n" +
                    "/notify 30";
        long timeInSec;
        try {
            timeInSec = Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return WRONG_INPUT_MESSAGE;
        }
        if (timeInSec > 0) {
            Thread thread = new Thread(new Notify(bot, chatId, timeInSec * MILLISEC_IN_SEC));
            thread.start();
        } else return WRONG_INPUT_MESSAGE;
        return "";
    }
}
