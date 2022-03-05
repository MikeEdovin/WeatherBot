package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Bot extends TelegramLongPollingBot {
    private static final Logger logger=Logger.getLogger("Bot logger");

    int RECONNECT_PAUSE = 10000;
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();
    private String botName;
    private String botToken;
    public Bot(String name, String token){
        this.botName=name;
        this.botToken=token;
    }

    public void setBotName(String name){
        this.botName=name;
    }
    public void setBotToken(String token){
        this.botToken=token;
    }

    @Override
    public String getBotUsername() {
        logger.info("Bot name: "+this.botName);
        return this.botName;
    }

    @Override
    public String getBotToken() {
        logger.info("Bot token: "+this.botToken);
        return this.botToken;
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
            logger.info("[STARTED] TelegramAPI. Bot Connected. Bot class: " + this);
            botsApi.registerBot(this);
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




}
