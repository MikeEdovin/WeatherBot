package org.example;

import Ability.Emojies;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;
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
    public void sendSticker(SendSticker sendSticker){
        try {
            this.execute(sendSticker);
        }catch (TelegramApiException e){
            logger.warning(e.getMessage());
        }
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
    public SendMessage sendMenuKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Main menu");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();//создание объекта клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();//список рядов кнопок
        KeyboardRow row = new KeyboardRow();//ряд кнопок
        row.add("Weather now "+ Emojies.PARTLY_SUNNY.getEmoji());
        row.add("For " +Emojies.FOR_48_HOURS.getEmoji()+" hours");
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        row.add("For " +Emojies.FOR_7_DAYS.getEmoji()+ " days");
        row.add("Notifications "+Emojies.NOTIFICATIONS.getEmoji());
        keyboard.add(row);
        row=new KeyboardRow();
        row.add("Settings "+Emojies.SETTINGS.getEmoji());
        row.add("Help "+Emojies.HELP.getEmoji());
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);
        return message;

    }


    public void sendPhoto(SendPhoto sendPhoto) {
        try{
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            logger.warning(e.getMessage());
        }
    }
    public void saveLocation(Update update){
        Message message=update.getMessage();
        User user=update.getChatMember().getFrom();

    }
}
