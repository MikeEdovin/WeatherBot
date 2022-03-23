package telegramBot.commands;

import com.vdurmont.emoji.EmojiParser;
import java.util.logging.Logger;

public class Parser {
    private static final Logger log = Logger.getLogger("Parser");
    private String botName;

    public Parser(String botName) {
        this.botName = botName;
    }

    public ParsedCommand getCommand(String text){
        String trimText="";
        if(text!=null){
            trimText=EmojiParser.removeAllEmojis(text).trim();
        }
        ParsedCommand result = new ParsedCommand(Command.NONE, trimText);

        if(trimText.equalsIgnoreCase(Command.START.description)){
            result.setCommand(Command.START);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.WEATHER_NOW.description).trim())){
            result.setCommand(Command.WEATHER_NOW);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.FOR_48_HOURS.description).trim())){
            result.setCommand(Command.FOR_48_HOURS);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.FOR_7_DAYS.description).trim())){
            result.setCommand(Command.FOR_7_DAYS);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.NOTIFICATION.description).trim())){
            result.setCommand(Command.NOTIFICATION);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.SETTINGS.description).trim())){
            result.setCommand(Command.SETTINGS);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.HELP.description).trim())){
            result.setCommand(Command.HELP);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.SET_CITY.description).trim())) {
            result.setCommand(Command.SET_CITY);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.GET_LOCATION.description).trim())) {
            result.setCommand(Command.GET_LOCATION);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.GET_FROM_LAST_THREE.description).trim())) {
            result.setCommand(Command.GET_FROM_LAST_THREE);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.BACK.description).trim())) {
            result.setCommand(Command.BACK);
        }else if(trimText.contains("Location")){
            result.setCommand(Command.ADD_CITY_TO_USER);
        }else if(trimText.matches("\\d{2}(\\:|\\s*|\\.*\\,*)\\d{2}")){
            result.setCommand(Command.SET_NOTIFICATION_TIME);
            result.setText(trimText);
        }
        else if(trimText.equalsIgnoreCase(Command.SET_NOTIFICATION_TIME.description)){
            result.setCommand(Command.SEND_TIME_SETTING_MESSAGE);
        }
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.RESET_NOTIFICATIONS.description))){
            result.setCommand(Command.RESET_NOTIFICATIONS);
        }
        else{
            result.setCommand(Command.GET_CITY_FROM_INPUT);
        }

        return result;
    }

}
