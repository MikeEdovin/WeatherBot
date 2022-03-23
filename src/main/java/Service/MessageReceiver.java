package Service;

import Handler.*;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.Command;
import telegramBot.commands.ParsedCommand;
import telegramBot.commands.Parser;

import java.util.logging.Logger;

public class MessageReceiver implements Runnable{
    private static final Logger log = Logger.getLogger("MessageReceiver");
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;
    Bot bot;
    Parser parser;
    UsersProvider usersProvider;

    public MessageReceiver(Bot b, UsersProvider up){
        this.bot=b;
        parser=new Parser(bot.getBotUsername());
        usersProvider=up;
    }
    @Override
    public void run() {
        log.info("[STARTED] MsgReceiver.  Bot class: " + bot.getBotUsername());
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                if (object != null) {
                    log.info("New object for analyze in queue " + object.toString());
                    analyze(object);
                }
            }
            try {
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
            log.info("Update received: " + update.toString());
            analyzeForUpdateType(update);
            if (update.getMessage().hasLocation()) {
                Location location = update.getMessage().getLocation();
                log.info("Location received " + location.getLatitude() + " " + location.getLongitude());
            } else log.warning("Cant operate type of object: " + object.toString());
        }
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

        String operationResult = handlerForCommand.operate(chatId.toString(), parsedCommand, update);

        if (!"".equals(operationResult)) {
            SendMessage messageOut = new SendMessage();
            messageOut.setChatId(String.valueOf(chatId));
            messageOut.setText(operationResult);
            bot.sendQueue.add(messageOut);
        }
    }
    private AbstractHandler getHandlerForCommand(Command command) {
        if (command == null) {
            log.warning("Null command accepted. This is not good scenario.");
            return new DefaultHandler(bot, usersProvider);
        }
        switch (command) {
            case START:
            case HELP:
            case SETTINGS:
            case BACK:
                SystemHandler systemHandler = new SystemHandler(bot,usersProvider);
                log.info("Handler for command[" + command.toString() + "] is: " + systemHandler);
                return systemHandler;
            case NOTIFICATION:
            case SET_NOTIFICATION_TIME:
            case SEND_TIME_SETTING_MESSAGE:
            case RESET_NOTIFICATIONS:
                NotifyHandler notifyHandler = new NotifyHandler(bot, usersProvider);
                log.info("Handler for command[" + command.toString() + "] is: " + notifyHandler);
                return notifyHandler;
            case WEATHER_NOW:
            case GET_CITY_FROM_INPUT:
            case ADD_CITY_TO_USER:
            case GET_FROM_LAST_THREE:
            case SET_CITY:
            case FOR_48_HOURS:
            case FOR_7_DAYS:
                WeatherHandler weatherHandler=new WeatherHandler(bot,usersProvider);
                log.info("Handler for command[" + command.toString() + "] is: " + weatherHandler);
                return weatherHandler;

            default:
                log.info("Handler for command[" + command.toString() + "] not Set. Return DefaultHandler");
                return new DefaultHandler(bot, usersProvider);
        }
    }
}
