package Service;

import Handler.AbstractHandler;
import Handler.DefaultHandler;
import Handler.NotifyHandler;
import Handler.SystemHandler;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

    public MessageReceiver(Bot b){
        this.bot=b;
        parser=new Parser(bot.getBotUsername());
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
        } else log.warning("Cant operate type of object: " + object.toString());
    }

    private void analyzeForUpdateType(Update update) {
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();

        ParsedCommand parsedCommand = parser.getParsedCommand(inputText);
        System.out.print(parsedCommand.getCommand().toString());
        AbstractHandler handlerForCommand = getHandlerForCommand(parsedCommand.getCommand());

        String operationResult = handlerForCommand.operate(chatId.toString(), parsedCommand, update);

        if (!"".equals(operationResult)) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(operationResult);
            bot.sendQueue.add(message);
        }
    }
    private AbstractHandler getHandlerForCommand(Command command) {
        if (command == null) {
            log.warning("Null command accepted. This is not good scenario.");
            return new DefaultHandler(bot);
        }
        switch (command) {
            case START:
            case HELP:
            case ID:
                SystemHandler systemHandler = new SystemHandler(bot);
                log.info("Handler for command[" + command.toString() + "] is: " + systemHandler);
                return systemHandler;
            case NOTIFY:
                NotifyHandler notifyHandler = new NotifyHandler(bot);
                log.info("Handler for command[" + command.toString() + "] is: " + notifyHandler);
                return notifyHandler;
            default:
                log.info("Handler for command[" + command.toString() + "] not Set. Return DefaultHandler");
                return new DefaultHandler(bot);
        }
    }
}
