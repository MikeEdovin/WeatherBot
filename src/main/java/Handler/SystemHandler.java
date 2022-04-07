package Handler;
import Ability.DBProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;


public class SystemHandler extends AbstractHandler{

    public SystemHandler(Bot b, DBProvider provider){
        super(b, provider);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command=parsedCommand.getCommand();
        long userID=update.getMessage().getFrom().getId();
        provider.addUserToDB(userID);
        if(!provider.userIsInDB(userID)){
            provider.userIsInDB(userID);
        }
        switch (command) {
            case START:
            case BACK:
                bot.sendQueue.add(bot.sendMenuKeyboard(chatId));
                break;
            case HELP:
                bot.sendQueue.add(bot.getMessageHelp(chatId));
                break;
            case SETTINGS:
                bot.sendQueue.add(bot.sendSettingsKeyBoard(chatId));
                break;
        }
        return "";
    }
}
