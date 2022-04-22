package Service;


import DataBase.DBProvider;
import Handler.*;
import org.weatherBot.Bot;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;
import telegramBot.commands.Parser;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable{
    private static final Logger log = Logger.getLogger("MessageReceiver");
    Bot bot;
    DBProvider provider;
    Parser parser;


    public MessageReceiver(Bot b, DBProvider dbProvider){
        this.bot=b;
        this.provider=dbProvider;
        parser=new Parser(bot.getBotUsername());
    }
    @Override
    public void run() {
        log.info("[STARTED] MsgReceiver.  Bot class: " + bot.getBotUsername());
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                log.info("New object for analyze in queue " + object);
                analyze(object);
            }
            try {
                int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.info("Catch interrupt. Exit"+ e.getMessage());
                return;
            }
        }

    }
    private void analyze(Object object) {
        if (object instanceof Update) {
            Update update = (Update) object;
            log.info("Update received: " + update);
            if (update.getMessage().hasLocation()) {
                Location location = update.getMessage().getLocation();
                log.info("Location received " + location.getLatitude() + " " + location.getLongitude());
            }
            analyzeForUpdateType(update);
        } else log.warning("Cant operate type of object: " + object);
    }

    private void analyzeForUpdateType(Update update) {
        Message message= update.getMessage();
        Long chatId = update.getMessage().getChatId();
        ParsedCommand parsedCommand = new ParsedCommand(Command.NONE, "");
        if(message.hasText()) {
            parsedCommand=parser.getCommand(message.getText());
        }
        else if(message.hasLocation()){
            parsedCommand.setCommand(Command.ADD_CITY_TO_USER);
        }
        AbstractHandler handlerForCommand = getHandlerForCommand(parsedCommand.getCommand());
        handlerForCommand.operate(chatId.toString(), parsedCommand, update);

    }
    private AbstractHandler getHandlerForCommand(Command command) {
        switch (command) {
            case START:
            case HELP:
            case SETTINGS:
            case BACK:
            case SEND_NEW_VERSION_MESSAGE:
                return new SystemHandler(bot,provider);
            case NOTIFICATION:
            case SET_NOTIFICATION_TIME:
            case SEND_TIME_SETTING_MESSAGE:
            case RESET_NOTIFICATIONS:
                return new NotifyHandler(bot,provider);
            case WEATHER_NOW:
            case GET_CITY_FROM_INPUT:
            case ADD_CITY_TO_USER:
            case GET_FROM_LAST_THREE:
            case SET_CITY:
            case FOR_48_HOURS:
            case FOR_7_DAYS:
                return new WeatherHandler(bot,provider);
            default:
                return new DefaultHandler(bot,provider);
        }
    }
}
