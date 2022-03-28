package org.example;


import Ability.CityData;
import Ability.Notify;
import Service.MessageReceiver;
import Service.MessageSender;
import Users.User;
import Users.UsersProvider;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger("App.class");
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    public static void main( String[] args ) throws TelegramApiException {
        Bot bot = new Bot("EdovinWeatherBot", "5295256851:AAHhA46BhJUzvzIE29rpjE8KlVCsDobF-is");
        UsersProvider usersProvider = new UsersProvider();
        usersProvider.getUsersFromBase();
        ArrayList<User> users = usersProvider.getUsers();

        for (User user : users) {
            System.out.println(user.getUserID());
            if (user.getNotificationTime() != null) {
                System.out.println("not time " + user.getNotificationTime());
            } else {
                System.out.println("time not set");
            }
            for (CityData city : user.getCitiesData()) {
                if (city != null) {
                    System.out.println("City " + city.getName());
                    if (city.getCurrentWeather() != null) {
                        System.out.println(city.getCurrentWeather().getTimeOfUpdate());
                    }
                }
            }

        }
        System.out.println("local time " + LocalTime.now());


        MessageReceiver messageReceiver = new MessageReceiver(bot, usersProvider);
        MessageSender messageSender = new MessageSender(bot);
        Notify notify = new Notify(bot, usersProvider);
        bot.botConnect();
        sendStartReport(bot, "5277149986");

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
/*
        while (true) {
            String command = getCommand();
            switch (command) {
                case exit:
                    System.exit(0);

            }
        }

 */
    }


    private static void sendStartReport(Bot bot, String BOT_ADMIN) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }

    public static String getCommand(){
        String command;
        System.out.println("enter command start, save or exit: ");
        try(BufferedReader br=new BufferedReader(new InputStreamReader(System.in))){
            command=br.readLine();
            return command;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
