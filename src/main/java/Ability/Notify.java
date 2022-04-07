package Ability;
import org.weatherBot.Bot;

import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Notify implements Runnable {
    private static final Logger log = Logger.getLogger("Notify");
    private static final long SLEEPING_TIME = 60000;

    Bot bot;
    DBProvider provider;
    String chatID;
    CityData notifyCity;
    LocalTime notificationTime;
    WeatherData[] forecast;
    String wdata;

    public Notify(Bot bot, DBProvider dbProvider) {
        this.bot = bot;
        this.provider=dbProvider;
    }

    @Override
    public void run() {
        log.info("Started " + this.getClass().toString());
        ArrayList<Long> usersID;
        String timeZone;
            while (true) {
                    try {
                        Thread.sleep(SLEEPING_TIME);
                        usersID=provider.getUsersIDFromDB();
                    for (Long userID : usersID) {
                        notifyCity=provider.getNotificationCity(userID);
                        notificationTime=provider.getNotificationTime(userID);
                        chatID=provider.getChatID(userID);
                        if (notifyCity != null && notificationTime != null) {
                            timeZone=provider.getTimeZone(notifyCity.getName());
                            WeatherData data=provider.getCurrentWeatherFromDB(notifyCity);
                                if (timeZone==null) {
                                    timeZone = ZoneId.systemDefault().getId();
                                }
                                    ZonedDateTime zdtNotificationTime = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), notificationTime), ZoneId.of(timeZone));
                                    ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(timeZone));
                                    if (zdtNotificationTime.getHour() == zdtNow.getHour() &&
                                            zdtNotificationTime.getMinute() == zdtNow.getMinute()) {
                                        if (!provider.isFresh(data)) {
                                            wdata = WeatherProvider.getOneCallAPI(notifyCity.getLatitude(), notifyCity.getLongitude());
                                            data = WeatherProvider.getCurrentWeather(wdata);
                                            forecast = WeatherProvider.getForecast(wdata);
                                            provider.addCurrentWeatherToDB(data, notifyCity);
                                            provider.addForecastToDB(forecast, notifyCity);
                                            }
                                        bot.sendQueue.add(bot.sendCurrentWeather(chatID, data, notifyCity.getName()));
                                        }
                                }
                    }
                    } catch (InterruptedException e) {
                        log.warning(e.getMessage());
                    }
            }
        }
    }






