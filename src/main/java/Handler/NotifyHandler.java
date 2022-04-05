package Handler;

import Ability.CityData;
import Ability.DBProvider;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;
import java.time.LocalTime;
import java.util.logging.Logger;

public class NotifyHandler extends AbstractHandler{
    private static final Logger log = Logger.getLogger("NotifyHandler");

    public NotifyHandler(Bot b){
        super(b);
    }
    @Override
    public String operate(String chatID, ParsedCommand parsedCommand, Update update) {
        Long userID=update.getMessage().getFrom().getId();
        if(DBProvider.userIsInDB(userID)==false){
            DBProvider.addUserToDB(userID);
        }
        CityData city=DBProvider.getCurrentCityDataFromDB(userID);;
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
                   DBProvider.setNotification(userID,chatID,city.getName(),time);
                   bot.sendQueue.add(bot.sendNotificationWasSet(chatID,
                           DBProvider.getCurrentCityDataFromDB(userID),
                           DBProvider.getNotificationTime(userID)));
               }
               else{
                   bot.sendQueue.add(bot.sendWrongInputMessage(chatID));
               }
                break;
            case RESET_NOTIFICATIONS:
                DBProvider.setNotification(userID,chatID,null,null);
                bot.sendQueue.add(bot.sendResetNotificationsMessage(chatID));
                break;
        }
        return "";
    }




}
