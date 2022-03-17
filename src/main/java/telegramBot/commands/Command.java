package telegramBot.commands;


import Ability.Emojies;

public enum Command{
        NONE(""),
        START("/start"),
        HELP("Help "+Emojies.HELP.getEmoji()),
        WEATHER_NOW("Weather now "+ Emojies.PARTLY_SUNNY.getEmoji()),
        FOR_48_HOURS("\"For \" +Emojies.FOR_48_HOURS.getEmoji()+\" hours\""),
        FOR_7_DAYS("For " +Emojies.FOR_7_DAYS.getEmoji()+ " days"),
        NOTIFY("Notifications "+Emojies.NOTIFICATIONS.getEmoji()),
        SETTINGS("Settings "+Emojies.SETTINGS.getEmoji()),
        SET_CITY("Set city"+Emojies.SET_CITY.getEmoji()),
        GET_LOCATION("Get location"+Emojies.GET_LOCATION.getEmoji()),
        GET_FROM_LAST_THREE("Get from last 3"+Emojies.LAST_THREE.getEmoji()),
        BACK("Back"+Emojies.BACK.getEmoji()),
        ADD_CITY_TO_USER("Add city to user"),
        GET_CITY_FROM_INPUT("Get city from input");

        String description;
        Command(String text){
            this.description=text;
        }
    }


