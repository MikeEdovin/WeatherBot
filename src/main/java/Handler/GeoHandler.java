package Handler;

import Ability.CityData;
import Ability.GeoProvider;
import Users.UsersProvider;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegramBot.commands.ParsedCommand;

import java.util.logging.Logger;

public class GeoHandler extends AbstractHandler {
    private final String END_LINE = "\n";
    Logger log = Logger.getLogger("Geo handler");

    public GeoHandler(Bot b, UsersProvider up) {
        super(b, up);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        CityData cityData = GeoProvider.getCityDataFromLocation(getLocation(update));
        bot.sendQueue.add(sendCityCoordinates(chatId,cityData));
        return "";
    }

    public Location getLocation(Update update) {
        if (update.getMessage().hasLocation()) {
            Location location = update.getMessage().getLocation();
            return location;
        }
        return null;
    }

    private SendMessage sendCityCoordinates(String chatID, CityData cityData) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        StringBuilder text = new StringBuilder();
        text.append("City " + cityData.getName()).append(END_LINE);
        text.append("longitude " + cityData.getLongitude()).append(END_LINE);
        text.append("latitude " + cityData.getLalitude()).append(END_LINE);
        message.setText(text.toString());
        return message;

    }
}
