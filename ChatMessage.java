import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents one message in the conversation, either from the
 * user or from the bot, along with the time it was sent.
 */
public class ChatMessage {

    public enum Sender { USER, BOT }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Sender sender;
    private final String text;
    private final String time;

    public ChatMessage(Sender sender, String text) {
        this.sender = sender;
        this.text = text;
        this.time = LocalDateTime.now().format(FORMATTER);
    }

    public Sender getSender() { return sender; }
    public String getText() { return text; }
    public String getTime() { return time; }
}
