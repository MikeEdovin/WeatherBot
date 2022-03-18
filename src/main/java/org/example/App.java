package org.example;


import Ability.CityData;
import Service.MessageReceiver;
import Service.MessageSender;
import Users.User;
import Users.UsersProvider;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger("App.class");
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    public static void main( String[] args ) throws TelegramApiException {
        Bot bot=new Bot("EdovinWeatherBot", "5295256851:AAHhA46BhJUzvzIE29rpjE8KlVCsDobF-is");
        UsersProvider usersProvider=new UsersProvider();
        usersProvider.getUsersFromBase();
        ArrayList<User>users= usersProvider.getUsers();
        for(User user: users){
            System.out.println(user.getUserID());
            for(CityData city:user.getCitiesData()) {
                if (city != null) {
                    System.out.println("City " + city.getName());
                }
            }

        }
        MessageReceiver messageReceiver=new MessageReceiver(bot, usersProvider);
        MessageSender messageSender=new MessageSender(bot);
        bot.botConnect();
        sendStartReport(bot, "5277149986");

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

    public void exit(){

    }
    private static void sendStartReport(Bot bot, String BOT_ADMIN) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }
}
