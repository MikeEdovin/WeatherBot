package Ability;

import Users.UsersProvider;
import org.example.Bot;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Logger;

public class Notify extends Thread {
    private static final Logger log = Logger.getLogger("Notify");
    private static final long SLEEPING_TIME = 60000;

    Bot bot;
    UsersProvider usersProvider;
    Long userID;
    String chatID;
    CityData currentCityData;
    volatile LocalTime notificationTime;
    WeatherData data;
    WeatherData[] forecast;
    String wdata;
    volatile boolean isStopped;

    public Notify(Bot bot, String chatID, LocalTime nTime, CityData city, UsersProvider up, Long userID) {
        this.bot = bot;
        this.chatID = chatID;
        this.notificationTime = nTime;
        this.currentCityData = city;
        this.usersProvider = up;
        this.userID = userID;
        this.isStopped = false;
    }

    public void setStopped() {
        this.isStopped = true;
    }

    public void setTime(LocalTime time) {
        this.notificationTime = time;
    }




    @Override
    public void run() {
        log.info("Started " + this.getClass().toString());

        while(true) {
            if (!isStopped) {
                try {
                    Thread.sleep(SLEEPING_TIME/2 );//сделать зонедтайм и от него брать часы и минуты
                    String timeZone = currentCityData.getCurrentWeather().getTimeZone();
                    System.out.println("time " + this.notificationTime);
                    DateTimeFormatter formatter=DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
                    notificationTime.format(formatter);
                    LocalDateTime notTime = LocalDateTime.of(LocalDate.now(), notificationTime);
                    ZonedDateTime zdtTimeOfUpdate = ZonedDateTime.of(notTime, ZoneId.of(timeZone));
                    Long notify = zdtTimeOfUpdate.toEpochSecond();
                    ZonedDateTime zdtNow = ZonedDateTime.now(ZoneId.of(timeZone));
                    //Long now = zdtNow.toEpochSecond();
                    LocalTime now=LocalTime.now();
                    now.format(formatter);
                    System.out.println(notificationTime.compareTo(now));
                    if (notificationTime.getHour()==now.getHour()&&notificationTime.getMinute()==now.getMinute()) {
                        if (currentCityData.isFreshForecast()) {
                            data = currentCityData.getCurrentWeather();
                            bot.sendQueue.add(bot.sendCurrentWeather(chatID, data, currentCityData.getName()));
                            setStopped();
                        } else {
                            wdata = WeatherProvider.getOneCallAPI(currentCityData.getLalitude(), currentCityData.getLongitude());
                            data = WeatherProvider.getCurrentWeather(wdata);
                            forecast = WeatherProvider.getForecast(wdata);
                            currentCityData.setCurrentWeather(data);
                            currentCityData.setForecastForSevenDays(forecast);
                            usersProvider.refreshUser(userID, currentCityData);
                            bot.sendQueue.add(bot.sendCurrentWeather(chatID, data, currentCityData.getName()));
                            setStopped();
                        }
                    }
                } catch (InterruptedException e) {
                    log.warning(e.getMessage());
                }
            }
            else{
                return;
            }
        }

    }
}





