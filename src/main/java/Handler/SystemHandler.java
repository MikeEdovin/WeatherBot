package Handler;
import Users.User;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;


public class SystemHandler extends AbstractHandler{

    public SystemHandler(Bot b, UsersProvider up){
        super(b,up);
    }
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command=parsedCommand.getCommand();
        Long userID=update.getMessage().getFrom().getId();
        User user= usersProvider.getUserByID(userID);
        if(user==null){
            usersProvider.addUserToList(new User(userID));
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
