package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Bot extends TelegramLongPollingBot {
    private static final Logger logger=Logger.getLogger("Bot logger");
    String BOT_ADMIN="";
    int RECONNECT_PAUSE = 10000;
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();


    @Override
    public String getBotUsername() {
        return "EdovinWeatherBot";
    }

    @Override
    public String getBotToken() {
        return "5295256851:AAHhA46BhJUzvzIE29rpjE8KlVCsDobF-is";
    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("update "+update.getMessage().getText());
        receiveQueue.add(update);
    }


    public void botConnect() {
        logger.setLevel(Level.ALL);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            logger.info("TelegramAPI started. Look for messages");
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            logger.warning("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();

        }
    }
    /*
    private static void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(BOT_ADMIN);
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }

     */
}
