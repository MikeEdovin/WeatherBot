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
    String chatID;
    CityData currentCityData;
    LocalTime notificationTime;
    WeatherData[] forecast;
    String wdata;

    public Notify(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info("Started " + this.getClass().toString());
        ArrayList<Long> usersID=DBProvider.getUsersIDFromDB();
        String timeZone;
            while (true) {
                    try {
                        Thread.sleep(SLEEPING_TIME);
                    for (Long userID : usersID) {
                        currentCityData=DBProvider.getCurrentCityDataFromDB(userID);
                        notificationTime=DBProvider.getNotificationTime(userID);
                        if (currentCityData != null && notificationTime != null) {
                            WeatherData data=DBProvider.getCurrentWeatherFromDB(currentCityData);
                                if (data!=null) {
                                    timeZone = currentCityData.getCurrentWeather().getTimeZone();
                                } else {
                                    timeZone = ZoneId.systemDefault().getId();
                                }

                                ZonedDateTime zdtTimeOfUpdate = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), notificationTime), ZoneId.of(timeZone));
                                ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(timeZone));
                                if (zdtTimeOfUpdate.getHour() == zdtNow.getHour() && zdtTimeOfUpdate.getMinute() == zdtNow.getMinute()) {
                                    if (!DBProvider.isFresh(data)) {
                                        wdata = WeatherProvider.getOneCallAPI(currentCityData.getLatitude(), currentCityData.getLongitude());
                                        data = WeatherProvider.getCurrentWeather(wdata);
                                        forecast = WeatherProvider.getForecast(wdata);
                                        DBProvider.addCurrentWeatherToDB(data,currentCityData);
                                        DBProvider.addForecastToDB(forecast,currentCityData);
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






