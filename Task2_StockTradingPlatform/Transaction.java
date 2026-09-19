import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents one BUY or SELL transaction, used for the
 * portfolio's transaction history log.
 */
public class Transaction {

    public enum Type { BUY, SELL }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double pricePerShare;
    private final double total;
    private final String formattedTime;

    /** Used when a new transaction happens right now. */
    public Transaction(Type type, String symbol, int quantity, double pricePerShare) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.total = quantity * pricePerShare;
        this.formattedTime = LocalDateTime.now().format(FORMATTER);
    }

    /** Used when re-loading a transaction from the saved file (keeps original time). */
    public Transaction(Type type, String symbol, int quantity, double pricePerShare, String formattedTime) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.total = quantity * pricePerShare;
        this.formattedTime = formattedTime;
    }

    public Type getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPricePerShare() { return pricePerShare; }
    public double getTotal() { return total; }
    public String getFormattedTime() { return formattedTime; }

    /** Serializes this transaction as one CSV line for saving to disk. */
    public String toCSV() {
        return "TRANSACTION," + type + "," + symbol + "," + quantity + ","
                + pricePerShare + "," + total + "," + formattedTime;
    }
}
