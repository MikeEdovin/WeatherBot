package Ability;

import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public enum Stickers {
    FUNNY_JIM_CARREY("CAACAgIAAxkBAANSYiNxRNZqkJTjE6wXiazPmLvDAy8AAn0TAAKjd6hLwlg7A4hvzeAjBA"),
    ;

    String stickerId;

    Stickers(String stickerId) {
        this.stickerId = stickerId;
    }

    public SendSticker getSendSticker(String chatId) {
        if ("".equals(chatId)) throw new IllegalArgumentException("ChatId cant be null");
        SendSticker sendSticker = getSendSticker();
        sendSticker.setChatId(chatId);
        return sendSticker;
    }

    public SendSticker getSendSticker() {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(new InputFile(stickerId));
        return sendSticker;
    }
}
