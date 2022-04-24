package Handler;

import Ability.CityData;
import DataBase.DBProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;
import java.time.LocalTime;
import java.util.logging.Logger;

public class NotifyHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger("NotifyHandler");

    public NotifyHandler(Bot b, DBProvider provider){
        super(b,provider);
    }
    @Override
    public void operate(String chatID, ParsedCommand parsedCommand, Update update) {
        long userID=update.getMessage().getFrom().getId();
        if(!provider.userIsInDB(userID)){
            provider.addUserToDB(userID);
        }
        CityData city=provider.getCurrentCityDataFromDB(userID);
        Command command=parsedCommand.getCommand();
        String timeInput= parsedCommand.getText();
        LocalTime time=null;

        switch (command) {
            case NOTIFICATION:
                if(city!=null) {
                    bot.sendQueue.add(bot.sendTimeSettingMessage(chatID));
                }else{
                    bot.sendQueue.add(bot.sendTimeSettingsError(chatID));
                }
                break;
            case SEND_TIME_SETTING_MESSAGE:
                if(city!=null) {
                    bot.sendQueue.add(bot.sendSetTime(chatID,userID));
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
                   provider.setNotification(userID,chatID,city.getName(),time);
                   bot.sendQueue.add(bot.sendNotificationWasSet(userID,chatID,
                           provider.getCurrentCityDataFromDB(userID),
                           provider.getNotificationTime(userID)));
               }
               else{
                   bot.sendQueue.add(bot.sendWrongInputMessage(chatID));
               }
                break;
            case RESET_NOTIFICATIONS:
                provider.setNotification(userID,chatID,null,null);
                for(int i=1;i<8;i++){
                    provider.deleteNotificationsDay(userID,i);
                }
                bot.sendQueue.add(bot.sendResetNotificationsMessage(chatID));
                break;
        }
    }




}
