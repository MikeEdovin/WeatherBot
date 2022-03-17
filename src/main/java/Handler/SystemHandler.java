package Handler;

import Ability.GeoProvider;
import Users.User;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SystemHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger("SystemHandler");
    private final String END_LINE = "\n";

    public SystemHandler(Bot b, UsersProvider up){
        super(b,up);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command=parsedCommand.getCommand();
        Long userID=update.getMessage().getFrom().getId();
        User user= usersProvider.getUserByID(userID);
        switch (command) {

            case START:
                if(user==null){
                    usersProvider.addUserToList(new User(userID));
                }
                bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                break;
            case BACK:
                bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                break;
            case HELP:
                bot.sendQueue.add(getMessageHelp(chatId));
                break;
            case SETTINGS:
                bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                break;
        }
        return "";
    }

    private SendMessage getMessageHelp(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        StringBuilder text = new StringBuilder();
        text.append("*This is help message*").append(END_LINE).append(END_LINE);
        text.append("[/start](/start) - show main menu").append(END_LINE);
        text.append("[/help](/help) - show help message").append(END_LINE);
        text.append("[/weather now](/weather now) - show current weather ").append(END_LINE);
        text.append("[/for 48 hours](/for 48 hours) - show weather forecast for 48 hours ").append(END_LINE);
        text.append("[/for 7 days](/for 7 days) - show weather forecast for 7 days ").append(END_LINE);
        text.append("[/notifications](/notifications) - set weather notifications ").append(END_LINE);
        text.append("[/settings](/settings) - show settings ").append(END_LINE);
        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageStart(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        StringBuilder text = new StringBuilder();
        text.append("Hello. I'm  *").append(bot.getBotUsername()).append("*").append(END_LINE);
        text.append("I'll inform you about weather").append(END_LINE);
        text.append("All that I can do - you can see calling the command [/help](/help)");
        sendMessage.setText(text.toString());
        return sendMessage;
    }
}
