package org.example;


import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App
{
    public static void main( String[] args ) throws TelegramApiException {

        WeatherBot bot=new WeatherBot();
        bot.botConnect();
    }
}
