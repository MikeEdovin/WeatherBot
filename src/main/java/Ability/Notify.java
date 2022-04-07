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
    String chatID;
    CityData notifyCity;
    LocalTime notificationTime;
    WeatherData[] forecast;
    String wdata;

    public Notify(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info("Started " + this.getClass().toString());
        ArrayList<Long> usersID;
        String timeZone;
            while (true) {
                    try {
                        Thread.sleep(SLEEPING_TIME);
                        usersID=DBProvider.getUsersIDFromDB();
                    for (Long userID : usersID) {
                        notifyCity=DBProvider.getNotificationCity(userID);
                        notificationTime=DBProvider.getNotificationTime(userID);
                        chatID=DBProvider.getChatID(userID);
                        if (notifyCity != null && notificationTime != null) {
                            timeZone=DBProvider.getTimeZone(notifyCity.getName());
                            WeatherData data=DBProvider.getCurrentWeatherFromDB(notifyCity);
                                if (timeZone==null) {
                                    timeZone = ZoneId.systemDefault().getId();
                                }
                                    ZonedDateTime zdtNotificationTime = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), notificationTime), ZoneId.of(timeZone));
                                    ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(timeZone));
                                    if (zdtNotificationTime.getHour() == zdtNow.getHour() &&
                                            zdtNotificationTime.getMinute() == zdtNow.getMinute()) {
                                        if (!DBProvider.isFresh(data)) {
                                            wdata = WeatherProvider.getOneCallAPI(notifyCity.getLatitude(), notifyCity.getLongitude());
                                            data = WeatherProvider.getCurrentWeather(wdata);
                                            forecast = WeatherProvider.getForecast(wdata);
                                            DBProvider.addCurrentWeatherToDB(data, notifyCity);
                                            DBProvider.addForecastToDB(forecast, notifyCity);
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






