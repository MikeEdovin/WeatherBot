package Ability;

import Users.User;
import Users.UsersProvider;
import org.weatherBot.Bot;
import java.time.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Notify implements Runnable {
    private static final Logger log = Logger.getLogger("Notify");
    private static final long SLEEPING_TIME = 60000;

    Bot bot;
    UsersProvider usersProvider;
    Long userID;
    String chatID;
    CityData currentCityData;
    LocalTime notificationTime;
    WeatherData data;
    WeatherData[] forecast;
    String wdata;

    public Notify(Bot bot, UsersProvider up) {
        this.bot = bot;
        this.usersProvider = up;
    }

    @Override
    public void run() {
        log.info("Started " + this.getClass().toString());
        ArrayList<User> users=usersProvider.getUsers();
        String timeZone;
            while (true) {
                    try {
                        Thread.sleep(SLEEPING_TIME);
                    for (User user : users) {
                        currentCityData=user.getCurrentCityData();
                        notificationTime=user.getNotificationTime();
                        chatID=user.getChatID();
                        if (currentCityData != null && notificationTime != null) {
                                if (currentCityData.getCurrentWeather() != null) {
                                    timeZone = currentCityData.getCurrentWeather().getTimeZone();
                                } else {
                                    timeZone = ZoneId.systemDefault().getId();
                                }

                                ZonedDateTime zdtTimeOfUpdate = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), notificationTime), ZoneId.of(timeZone));
                                ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(timeZone));
                                if (zdtTimeOfUpdate.getHour() == zdtNow.getHour() && zdtTimeOfUpdate.getMinute() == zdtNow.getMinute()) {
                                    if (currentCityData.isFreshForecast()) {
                                        data = currentCityData.getCurrentWeather();
                                    } else {
                                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                                        data = WeatherProvider.getCurrentWeather(wdata);
                                        forecast = WeatherProvider.getForecast(wdata);
                                        currentCityData.setCurrentWeather(data);
                                        currentCityData.setForecastForSevenDays(forecast);
                                        usersProvider.refreshUser(userID, currentCityData);
                                    }
                                    bot.sendQueue.add(bot.sendCurrentWeather(chatID, data, currentCityData.getName()));
                                }
                        }
                    }
                    } catch (InterruptedException e) {
                        log.warning(e.getMessage());
                    }
            }
        }
    }






