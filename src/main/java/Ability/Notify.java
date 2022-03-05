package Ability;

import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.logging.Logger;

public class Notify implements Runnable{
    private static final Logger log = Logger.getLogger("Notify");
    private static final int MILLISEC_IN_SEC = 1000;

    Bot bot;
    long delayInMillisec;
    String chatID;

    public Notify(Bot bot, String chatID, long delayInMillisec) {
        this.bot = bot;
        this.chatID = chatID;
        this.delayInMillisec = delayInMillisec;
        log.info("CREATE. " + toString());
    }
    @Override
    public void run() {
        log.info("Started "+this.getClass().toString());
        bot.sendQueue.add(getFirstMessage());
        try {
            Thread.sleep(delayInMillisec);
            bot.sendQueue.add(getSecondSticker());
        } catch (InterruptedException e) {
            log.warning(e.getMessage());
        }
        log.info("FIHISH. " + toString());
    }

    private SendMessage getFirstMessage() {
        return new SendMessage(chatID, "I will send you notify after " + delayInMillisec / MILLISEC_IN_SEC + "sec");
    }

    private SendSticker getSecondSticker() {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(new InputFile("CAACAgIAAxkBAANSYiNxRNZqkJTjE6wXiazPmLvDAy8AAn0TAAKjd6hLwlg7A4hvzeAjBA"));
        sendSticker.setChatId(chatID);
        return sendSticker;
    }

    private SendMessage getSecondMessage() {
        return new SendMessage(chatID, "This is notify message. Thanks for using :)");
    }

    }

