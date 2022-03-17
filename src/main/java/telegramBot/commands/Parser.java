package telegramBot.commands;

import com.vdurmont.emoji.EmojiParser;
import javafx.util.Pair;
import java.util.logging.Logger;

public class Parser {
    private static final Logger log = Logger.getLogger("Parser");
    private final String PREFIX_FOR_COMMAND = "/";
    private final String DELIMITER_COMMAND_BOTNAME = "@";
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
        else if(trimText.equalsIgnoreCase(EmojiParser.removeAllEmojis(Command.NOTIFY.description).trim())){
            result.setCommand(Command.NOTIFY);
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
        }
        else{
            result.setCommand(Command.GET_CITY_FROM_INPUT);
        }

        return result;
    }
/*
    public ParsedCommand getParsedCommand(String text) {
        String trimText = "";
        if (text != null) trimText = text.trim();
        ParsedCommand result = new ParsedCommand(Command.NONE, trimText);

        if ("".equals(trimText)) return result;
        Pair<String, String> commandAndText = getDelimitedCommandFromText(trimText);
        if (isCommand(commandAndText.getKey())) {
            if (isCommandForMe(commandAndText.getKey())) {
                String commandForParse = cutCommandFromFullText(commandAndText.getKey());
                Command commandFromText = getCommandFromText(commandForParse);
                result.setText(commandAndText.getValue());
                result.setCommand(commandFromText);
            } else {
                result.setCommand(Command.NOT_FOR_ME);
                result.setText(commandAndText.getValue());
            }

        }
        if (result.getCommand() == Command.NONE) {
            List<String> emojiContainsInText = EmojiParser.extractEmojis(result.getText());
            if (emojiContainsInText.size() > 0) result.setCommand(Command.TEXT_CONTAIN_EMOJI);
        }
        return result;
    }

 */

    private String cutCommandFromFullText(String text) {
        return text.contains(DELIMITER_COMMAND_BOTNAME) ?
                text.substring(1, text.indexOf(DELIMITER_COMMAND_BOTNAME)) :
                text.substring(1);
    }

    private Command getCommandFromText(String text) {
        String upperCaseText = text.toUpperCase().trim();
        Command command = Command.NONE;
        try {
            command = Command.valueOf(upperCaseText);
        } catch (IllegalArgumentException e) {
            log.info("Can't parse command: " + text);
        }
        return command;
    }

    private Pair<String, String> getDelimitedCommandFromText(String trimText) {
        Pair<String, String> commandText;

        if (trimText.contains(" ")) {
            int indexOfSpace = trimText.indexOf(" ");
            commandText = new Pair<>(trimText.substring(0, indexOfSpace), trimText.substring(indexOfSpace + 1));
        } else commandText = new Pair<>(trimText, "");
        return commandText;
    }

    private boolean isCommandForMe(String command) {
        if (command.contains(DELIMITER_COMMAND_BOTNAME)) {
            String botNameForEqual = command.substring(command.indexOf(DELIMITER_COMMAND_BOTNAME) + 1);
            return botName.equals(botNameForEqual);
        }
        return true;
    }

    private boolean isCommand(String text) {
        return text.startsWith(PREFIX_FOR_COMMAND);
    }
}
