package Ability;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;

public enum Emojies {
    PARTLY_SUNNY(":partly_sunny:"),
    SUNNY("☀️"),
    CLOUDY("☁️"),
    RAINY("\uD83C\uDF27️"),
    WIND("\uD83C\uDF2C️"),
    FOR_48_HOURS("4️⃣8️⃣"),
    FOR_7_DAYS("7️⃣"),
    NOTIFICATIONS(":alarm_clock:"),
    SETTINGS(":gear:"),
    HELP("ℹ️")

    ;
    String emojiUnicode;
    Emojies(String code){
        this.emojiUnicode=code;
    }
    public String getEmoji(){
        return EmojiParser.parseToUnicode(emojiUnicode);
    }
}