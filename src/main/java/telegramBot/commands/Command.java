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
        SETTINGS("Settings "+Emojies.SETTINGS.getEmoji());
        //ID("id"),
        //STICKER("sticker"),
        //TEXT_CONTAIN_EMOJI("text contain emoji");
        String description;
        Command(String text){
            this.description=text;
        }
    }


