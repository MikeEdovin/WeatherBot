package org.example;


import Service.MessageReceiver;
import Service.MessageSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger("App.class");
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    public static void main( String[] args ) throws TelegramApiException {
        Bot bot=new Bot();
        MessageReceiver messageReceiver=new MessageReceiver(bot);
        MessageSender messageSender=new MessageSender(bot);
        bot.botConnect();

        Thread receiver = new Thread(messageReceiver);
        receiver.setDaemon(true);
        receiver.setName("MsgReceiver");
        //receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        //sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();



    }
}
