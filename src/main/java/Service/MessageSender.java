package Service;
import org.example.Bot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.logging.Logger;

public class MessageSender implements Runnable{
    private static final Logger log = Logger.getLogger("MessageSender.class");
    private final Bot bot;

    public MessageSender(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info("[STARTED] MsgSender.  Bot class: " + bot);
        try {
            while (true) {
                for (Object object = bot.sendQueue.poll(); object != null; object = bot.sendQueue.poll()) {
                    log.info("Get new msg to send " + object);
                    send(object);
                }
                try {
                    int SENDER_SLEEP_TIME = 1000;
                    Thread.sleep(SENDER_SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.warning("Take interrupt while operate msg list"+e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    private void send(Object object) {
        try {
            MessageType messageType = messageType(object);
            if (messageType == MessageType.EXECUTE) {
                BotApiMethod<Message> message = (BotApiMethod<Message>) object;
                log.info("Use Execute for " + object);
                bot.execute(message);
            } else {
                log.warning("Cant detect type of object. " + object);
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    private MessageType messageType(Object object) {
        if (object instanceof SendSticker) return MessageType.STICKER;
        if (object instanceof BotApiMethod) return MessageType.EXECUTE;
        return MessageType.NOT_DETECTED;
    }

    enum MessageType {
        EXECUTE, STICKER, NOT_DETECTED;
    }
}
