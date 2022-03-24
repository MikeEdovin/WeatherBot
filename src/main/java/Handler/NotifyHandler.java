package Handler;

import Ability.CityData;
import Ability.Notify;
import Users.User;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;
import java.time.LocalTime;
import java.util.logging.Logger;

public class NotifyHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger("NotifyHandler");

    public NotifyHandler(Bot b, UsersProvider up){
        super(b, up);
    }
    @Override
    public String operate(String chatID, ParsedCommand parsedCommand, Update update) {
        Long userID=update.getMessage().getFrom().getId();
        if(usersProvider.getUserByID(userID)==null){
            usersProvider.addUserToList(new User(userID));
        }
        User user= usersProvider.getUserByID(userID);
        CityData city=null;
        Command command=parsedCommand.getCommand();
        String timeInput= parsedCommand.getText();
        LocalTime time=null;
        Notify notify=new Notify(bot,chatID,time,city,usersProvider,userID);
        notify.setName("timer thread");
        notify.setDaemon(true);
        notify.start();

        switch (command) {
            case NOTIFICATION:
                city=user.getCurrentCityData();
                if(city!=null) {
                    bot.sendQueue.add(bot.sendTimeSettingMessage(chatID));
                }else{
                    bot.sendQueue.add(bot.sendTimeSettingsError(chatID));
                }
                break;
            case SEND_TIME_SETTING_MESSAGE:
                if(user.getCurrentCityData()!=null) {
                    bot.sendQueue.add(bot.sendTimeSettingsMessage(chatID));
                }
                else{
                    bot.sendQueue.add(bot.sendTimeSettingsError(chatID));
                }
                break;
            case SET_NOTIFICATION_TIME:
                try {
                    time = LocalTime.parse(timeInput);
                }catch (Exception e) {
                    log.warning(e.getMessage());
                }
               if(time!=null) {
                   usersProvider.refreshNotificationTime(userID, time);
                   notify.setTime(usersProvider.getUserByID(userID).getNotificationTime());
                   notify.setCurrentCityData(user.getCurrentCityData());
                   bot.sendQueue.add(bot.sendNotificationWasSet(chatID, usersProvider.getUserByID(userID).getCurrentCityData(), usersProvider.getUserByID(userID).getNotificationTime()));
               }
               else{
                   bot.sendQueue.add(bot.sendWrongInputMessage(chatID));
               }
                break;
            case RESET_NOTIFICATIONS:
                usersProvider.refreshNotificationTime(userID,null);
                notify.setTime(usersProvider.getUserByID(userID).getNotificationTime());
                notify.setStopped();
                bot.sendQueue.add(bot.sendResetNotificationsMessage(chatID));
                break;
        }
        return "";
    }




}
