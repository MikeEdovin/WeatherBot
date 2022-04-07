package org.weatherBot;
import Ability.DBProvider;
import Ability.Notify;
import Service.MessageReceiver;
import Service.MessageSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.io.*;
import java.util.Objects;
import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger("App.class");
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    public static void main( String[] args ) {
        String botName=System.getenv("BOT_NAME");
        String token=System.getenv("BOT_TOKEN");
        String botAdmin=System.getenv("BOT_ADMIN");
        Bot bot = new Bot(botName, token);
        DBProvider dbProvider=new DBProvider();
        dbProvider.getConnection();
        MessageReceiver messageReceiver = new MessageReceiver(bot, dbProvider);
        MessageSender messageSender = new MessageSender(bot);
        Notify notify = new Notify(bot,dbProvider);
        bot.botConnect();
        //sendStartReport(bot, botAdmin);

        Thread receiver = new Thread(messageReceiver);
        receiver.setDaemon(true);
        receiver.setName("MsgReceiver");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();

        Thread notifyThread = new Thread(notify);
        notifyThread.setDaemon(true);
        notifyThread.setName("NotifyThread");
        notifyThread.start();


        while (true) {
            String command = getCommand();
            switch (Objects.requireNonNull(command)) {
                case "close":
                    dbProvider.closeConnection();
                    log.info("connection was closed");
                    break;
                case "exit":
                    System.exit(0);
                case "create":
                    dbProvider.createTables();
                    break;
                default:
                    System.out.println("Wrong input");
                    break;
            }
        }
    }

    private static void sendStartReport(Bot bot, String BOT_ADMIN) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }

    public static String getCommand(){
        String command;
        System.out.print("enter command create,close or exit: ");
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            command=br.readLine();
            return command;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
