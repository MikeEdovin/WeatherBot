package Ability;

import Users.UsersProvider;
import org.example.Bot;
import java.time.*;
import java.util.logging.Logger;

public class Notify extends Thread {
    private static final Logger log = Logger.getLogger("Notify");
    private static final long SLEEPING_TIME = 60000;

    Bot bot;
    UsersProvider usersProvider;
    Long userID;
    String chatID;
    volatile CityData currentCityData;
    volatile LocalTime notificationTime;
    WeatherData data;
    WeatherData[] forecast;
    String wdata;
    private volatile boolean isStopped;

    public Notify(Bot bot, String chatID, LocalTime nTime, CityData city, UsersProvider up, Long userID) {
        this.bot = bot;
        this.chatID = chatID;
        this.notificationTime = nTime;
        this.currentCityData = city;
        this.usersProvider = up;
        this.userID = userID;
        this.isStopped = false;
    }

    public void setStopped() {this.isStopped = true;}
    public void setTime(LocalTime time) {this.notificationTime = time;}
    public void setCurrentCityData(CityData data){this.currentCityData=data;}
    @Override
    public void run() {
        log.info("Started " + this.getClass().toString());
        String timeZone;
            while (true) {
                if (!isStopped) {
                    if (currentCityData!=null&&notificationTime != null) {
                        try {
                            Thread.sleep(SLEEPING_TIME);
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
                        } catch (InterruptedException e) {
                            log.warning(e.getMessage());
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }






