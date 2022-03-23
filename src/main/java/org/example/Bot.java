package org.example;

import Ability.CityData;
import Ability.Emojies;
import Ability.WeatherData;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Bot extends TelegramLongPollingBot {
    private static final Logger logger=Logger.getLogger("Bot logger");

    int RECONNECT_PAUSE = 10000;
    private final String END_LINE = "\n";
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
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
            logger.info("update " + update.getMessage().getText());
            receiveQueue.add(update);
        if(update.getMessage().hasLocation()){
            Location location=update.getMessage().getLocation();
            logger.info("location "+location.getLongitude()+" "+location.getLatitude());
            receiveQueue.add(location);
        }
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
        //keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);
        return message;

    }
    public SendMessage sendSettingsKeyBoard(String chatID){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Set city");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();//создание объекта клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();//список рядов кнопок
        KeyboardRow row = new KeyboardRow();//ряд кнопок
        row.add("Set city "+ Emojies.SET_CITY.getEmoji());
        keyboard.add(row);
        row=new KeyboardRow();
        KeyboardButton getLocButton=new KeyboardButton("Get location " +Emojies.GET_LOCATION.getEmoji());
        getLocButton.setRequestLocation(true);
        getLocButton.getRequestLocation();
        row.add(getLocButton);
        keyboard.add(row);
        row=new KeyboardRow();
        row.add("Get from last 3"+Emojies.LAST_THREE.getEmoji());
        keyboard.add(row);
        row =new KeyboardRow();
        row.add("Back"+Emojies.BACK.getEmoji());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        return message;

    }
    public SendMessage sendCurrentWeather(String chatID, WeatherData data, String cityNamme){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("Current weather for "+cityNamme).append(END_LINE).append(END_LINE);
        text.append("Current date "+data.getDate()).append(END_LINE);
        text.append("Temperature "+data.getTemp()).append(END_LINE);
        text.append("Feels like temperature "+data.getFeelsLikeTemp()).append(END_LINE);
        text.append("Pressure "+data.getPressure()).append(END_LINE);
        text.append("Humidity "+data.getHumidity()).append(END_LINE);
        text.append("clouds "+data.getClouds()).append(END_LINE);
        text.append("Update time "+data.getTimeOfUpdate()).append(END_LINE);
        message.setText(text.toString());
        return message;
    }
    public SendMessage sendForecast(String chatID, WeatherData[] forecast, int nrOfDays, String cityName){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("Forecast for "+cityName).append(END_LINE).append(END_LINE);
        for(int i=0;i< nrOfDays;i++){
            WeatherData data=forecast[i];
            if(data!=null) {
                text.append("Date " + data.getDate()).append(END_LINE);
                text.append("Temperature " + data.getTemp()).append(END_LINE);
                text.append("Feels like temperature " + data.getFeelsLikeTemp()).append(END_LINE);
                text.append("Pressure " + data.getPressure()).append(END_LINE);
                text.append("Humidity " + data.getHumidity()).append(END_LINE);
                text.append("clouds " + data.getClouds()).append(END_LINE);
                text.append("Update time " + data.getTimeOfUpdate()).append(END_LINE).append(END_LINE);
            }
        }
        message.setText(text.toString());
        return message;
    }
    public SendMessage getCity(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        sendMessage.setText("Please, type the city name ");
        return sendMessage;
    }
    public SendMessage sendLastThree(String chatID, CityData[]cities){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Choose from last three cities");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        for(CityData item:cities) {
            if (item != null) {
                KeyboardRow row = new KeyboardRow();
                row.add(item.getName());
                keyboard.add(row);
            }
        }
        KeyboardRow row=new KeyboardRow();
        row.add("Back"+ Emojies.BACK.getEmoji());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setOneTimeKeyboard(true);
        return message;
    }
    public SendMessage sendTimeSettingMessage(String chatID){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Notifications settings");
        ReplyKeyboardMarkup keyboardMarkup=new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row=new KeyboardRow();
        row.add("Set notification time");
        keyboard.add(row);
        row=new KeyboardRow();
        row.add("Reset notification time");
        keyboard.add(row);
        row=new KeyboardRow();
        row.add("Back"+Emojies.BACK.getEmoji());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
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

    public SendMessage sendNotificationWasSetted(String chatID,CityData currentCity, LocalTime time) {
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Notifications was set for "+currentCity.getName()+" "+"at "+time);
        return message;
    }
    public SendMessage sendTimeSettingsMessage(String chatID){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Enter notifications time in hh : mm");
        return message;
    }

    public SendMessage sendResetNotificationsMessage(String chatID) {
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Notifications time was reset");
        return message;
    }

    public SendMessage sendWrongInputMessage(String chatID) {
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Wrong input, please try again");
        return message;
    }
}
