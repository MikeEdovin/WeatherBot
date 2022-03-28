package org.weatherBot;


import Ability.CityData;
import Ability.Notify;
import Service.MessageReceiver;
import Service.MessageSender;
import Users.User;
import Users.UsersProvider;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger("App.class");
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    public static void main( String[] args ) {
        UsersProvider usersProvider = new UsersProvider();
        usersProvider.getUsersFromBase();
        ArrayList<User> users = usersProvider.getUsers();

        while (true) {
            String command = getCommand();
            switch (Objects.requireNonNull(command)) {
                case "start":
                    String token=getBotToken();
                    Bot bot = new Bot("EdovinWeatherBot", token);
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
                    break;
                case "backup":
                    backup(users);
                    break;
                case "showUsers":
                    showUsers(users);
                    break;
                case "exit":
                    System.exit(0);
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
        System.out.print("enter command start, showUsers, backup or exit: ");
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
    public static void showUsers(ArrayList<User>users){
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
    }
    public static void backup(ArrayList<User>users){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("usersBackup.txt", false))) {
            oos.writeObject(users);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }
    public static String getBotToken(){
        String token;
        try(BufferedReader br=new BufferedReader(new FileReader("botToken.txt"))){
            token=br.readLine();
            System.out.println(token);
            return token;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
