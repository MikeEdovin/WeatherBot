package org.weatherBot;

import Ability.*;
import DataBase.DBProvider;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
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
import java.util.Objects;
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
    private final String botName;
    private final String botToken;
    private DBProvider provider;
    public Bot(String name, String token){
        this.botName=name;
        this.botToken=token;
    }
    public void setProvider(DBProvider p){
        this.provider=p;
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
        if(update.hasMessage()) {
            logger.info("update " + update.getMessage().getText());
            receiveQueue.add(update);
            if (update.getMessage().hasLocation()) {
                Location location = update.getMessage().getLocation();
                logger.info("location " + location.getLongitude() + " " + location.getLatitude());
                receiveQueue.add(location);
            }
        }else if(update.hasCallbackQuery()){
            Message message=update.getCallbackQuery().getMessage();
            String chatID=String.valueOf(update.getCallbackQuery().getMessage().getChatId());
            AnswerCallbackQuery answerCallbackQuery=new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            int messageID=message.getMessageId();
            CallbackQuery query=update.getCallbackQuery();
            EditMessageReplyMarkup editMessageReplyMarkup=new EditMessageReplyMarkup();
            InlineKeyboardMarkup keyboardMarkup=updateKeyBoard(query);
            editMessageReplyMarkup.setReplyMarkup(keyboardMarkup);
            editMessageReplyMarkup.setMessageId(messageID);
            editMessageReplyMarkup.setChatId(chatID);
            try{
                execute(answerCallbackQuery);
                execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    private InlineKeyboardMarkup updateKeyBoard(CallbackQuery query){
        long userID=query.getMessage().getChatId();
        InlineKeyboardMarkup keyboardMarkup=query.getMessage().getReplyMarkup();
        List<List<InlineKeyboardButton>>keyboard=keyboardMarkup.getKeyboard();
        for(List<InlineKeyboardButton>row:keyboard) {
            for (InlineKeyboardButton button : row) {
                    if (Objects.equals(button.getCallbackData(), query.getData()) &&
                            !button.getText().contains(Emojies.DONE.getEmoji())) {
                        button.setText(button.getText() + " " + Emojies.DONE.getEmoji());
                        provider.addNotificationsDay(userID, Integer.parseInt(query.getData()));
                    } else if (Objects.equals(button.getCallbackData(), query.getData()) &&
                            button.getText().contains(Emojies.DONE.getEmoji())) {
                        button.setText(EmojiParser.removeAllEmojis(button.getText()));
                        provider.deleteNotificationsDay(userID, Integer.parseInt(query.getData()));
                    }
                }
        }
        return keyboardMarkup;
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
        row = new KeyboardRow();
        row.add("For " +Emojies.FOR_7_DAYS.getEmoji()+ " days");
        row.add("Notifications "+Emojies.NOTIFICATIONS.getEmoji());
        keyboard.add(row);
        row=new KeyboardRow();
        row.add("Settings "+Emojies.SETTINGS.getEmoji());
        row.add("Help "+Emojies.HELP.getEmoji());
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }
    public SendMessage sendSettingsKeyBoard(String chatID){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Set city");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
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
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }
    public SendMessage sendCurrentWeather(String chatID, WeatherData data, String cityName){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append(Emojies.CURRENT.getEmoji()).append(" Current weather for ").append(cityName).append(END_LINE).append(END_LINE);
        text.append(Emojies.TEMPERATURE.getEmoji()).append(" Temperature ").append(data.getTemp()).append(" °C").append(END_LINE);
        text.append(Emojies.TEMPERATURE.getEmoji()).append(" Feels like temperature ").append(data.getFeelsLikeTemp()).append(" °C").append(END_LINE);
        text.append(Emojies.PRESSURE.getEmoji()).append(" Pressure ").append(data.getPressure()).append(" hPa").append(END_LINE);
        text.append(Emojies.HUMIDITY.getEmoji()).append(" Humidity ").append(data.getHumidity()).append(" %").append(END_LINE);
        text.append(chooseCloudsIcon(data.getClouds())).append(END_LINE);
        text.append("Update time ").append(data.getTimeOfUpdate()).append(END_LINE);
        message.setText(text.toString());
        return message;
    }
    public SendMessage sendForecast(String chatID, WeatherData[] forecast, int nrOfDays, String cityName){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append(Emojies.DATE.getEmoji()).append(" Forecast for ").append(cityName).append(END_LINE).append(END_LINE);
        for(int i=0;i< nrOfDays;i++){
            WeatherData data=forecast[i];
            if(data!=null) {
                text.append(Emojies.DATE.getEmoji()).append(" Date ").append(data.getDate()).append(END_LINE);
                text.append(Emojies.TEMPERATURE.getEmoji()).append(" Temperature ").append(data.getTemp()).append(" °C").append(END_LINE);
                text.append(Emojies.TEMPERATURE.getEmoji()).append(" Feels like temperature ").append(data.getFeelsLikeTemp()).append(" °C").append(END_LINE);
                text.append(Emojies.PRESSURE.getEmoji()).append(" Pressure ").append(data.getPressure()).append(" hPa").append(END_LINE);
                text.append(Emojies.HUMIDITY.getEmoji()).append(" Humidity ").append(data.getHumidity()).append(" %").append(END_LINE);
                text.append(chooseCloudsIcon(data.getClouds())).append(END_LINE);
                text.append("Update time ").append(data.getTimeOfUpdate()).append(END_LINE).append(END_LINE);
            }
        }
        message.setText(text.toString());
        return message;
    }
    private String chooseCloudsIcon(long clouds){
        if(clouds<25){
            return "Sunny "+Emojies.SUNNY.getEmoji();
        }
        else if(clouds<75){
            return "Partly cloud "+Emojies.PARTLY_SUNNY.getEmoji();
        }
        else{
            return "Cloudy "+Emojies.CLOUDY.getEmoji();
        }
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

    public SendMessage sendNotificationWasSet(long userID,String chatID, CityData currentCity, LocalTime time) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        Days[] days=Days.values();
        StringBuilder builder=new StringBuilder();
        if(provider.hasAtLeastOneNotDay(userID)) {
            for (Days day : days) {
                if (provider.isNotificationDay(day.getDay(), userID)) {
                    builder.append(day.name()).append(", ");
                }
            }
            int lastCommaIndex = builder.lastIndexOf(",");
            builder.deleteCharAt(lastCommaIndex);
            message.setText("Notifications was set for " + currentCity.getName() + " for " + builder + " at " + time);

        }else{
            message.setText("Please, choose at least one day for notifications");
        }
        return message;
    }
    public SendMessage sendSetTime(String chatID,long userID){
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("Choose days and enter notifications time in hh : mm");
        InlineKeyboardMarkup keyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard= new ArrayList<>();
        List<InlineKeyboardButton>row=new ArrayList<>();
        List<InlineKeyboardButton>row2=new ArrayList<>();
        Days[] days=Days.values();
        for(int i=0;i<4;i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
            if (!provider.isNotificationDay(i+1, userID)) {
                button.setText(days[i].name());
            }else{
                button.setText(days[i].name()+" "+Emojies.DONE.getEmoji());
            }
                button.setCallbackData(String.valueOf(days[i].getDay()));
                row.add(button);
            }
        for(int i=4;i<7;i++){
            InlineKeyboardButton button=new InlineKeyboardButton();
            if (!provider.isNotificationDay(i+1, userID)) {
                button.setText(days[i].name());
            }else{
                button.setText(days[i].name()+" "+Emojies.DONE.getEmoji());
            }
            button.setCallbackData(String.valueOf(days[i].getDay()));
            row2.add(button);
        }
        keyboard.add(row);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
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

    public SendMessage sendTimeSettingsError(String chatID) {
        SendMessage message=new SendMessage();
        message.setChatId(chatID);
        message.setText("At first you need to choose city");
        return message;
    }
    public SendMessage getMessageHelp(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        String text = "*This is help message*" + END_LINE + END_LINE +
                "[/start](/start) - show main menu" + END_LINE +
                "Help " + Emojies.HELP.getEmoji() + " - show help message" + END_LINE +
                "Weather now " + Emojies.PARTLY_SUNNY.getEmoji() + " - show current weather " + END_LINE +
                "For " + Emojies.FOR_48_HOURS.getEmoji() + " hours - show weather forecast for 48 hours " + END_LINE +
                "For " + Emojies.FOR_7_DAYS.getEmoji() + " days - show weather forecast for 7 days " + END_LINE +
                "Notifications " + Emojies.NOTIFICATIONS.getEmoji() + " - set weather notifications " + END_LINE +
                "Settings " + Emojies.SETTINGS.getEmoji() + " - show settings " + END_LINE;
        sendMessage.setText(text);
        return sendMessage;
    }
    public  SendMessage sendNewVersionMessage(long userID){
        SendMessage message=new SendMessage();
            message.setChatId(String.valueOf(userID));
            message.setText("Release v 1.1. Now you can choose days for notifications, " +
                    "by default were set working days");
            return message;
    }
}
