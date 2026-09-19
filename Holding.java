/**
 * Represents the shares a user currently owns of one stock,
 * including the average price paid (cost basis) so profit/loss
 * can be calculated correctly across multiple buys.
 */
public class Holding {

    private final String symbol;
    private int quantity;
    private double averageCost;

    public Holding(String symbol, int quantity, double averageCost) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    /** Adds more shares and recalculates the weighted average cost. */
    public void addShares(int qty, double price) {
        double totalCost = (averageCost * quantity) + (price * qty);
        quantity += qty;
        averageCost = totalCost / quantity;
    }

    /** Removes shares after a sell (average cost stays the same). */
    public void removeShares(int qty) {
        quantity -= qty;
    }

    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getAverageCost() { return averageCost; }
}
