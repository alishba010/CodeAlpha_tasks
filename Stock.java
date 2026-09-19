import java.util.Random;

/**
 * Represents a single tradeable stock on the market.
 * Handles its own simulated price movement so the market
 * feels "live" while the app is running.
 */
public class Stock {

    private final String symbol;
    private final String companyName;
    private double currentPrice;
    private double previousClose;
    private final Random random;

    public Stock(String symbol, String companyName, double startingPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = startingPrice;
        this.previousClose = startingPrice;
        this.random = new Random();
    }

    /** Simulates a small random price movement, like a real market tick. */
    public void updatePrice() {
        previousClose = currentPrice;
        double changePercent = (random.nextDouble() * 6) - 3; // -3% .. +3%
        double newPrice = currentPrice * (1 + changePercent / 100.0);
        currentPrice = Math.max(1.0, round2(newPrice));
    }

    public double getChangeAmount() {
        return round2(currentPrice - previousClose);
    }

    public double getChangePercent() {
        if (previousClose == 0) return 0;
        return Math.round(((currentPrice - previousClose) / previousClose) * 10000.0) / 100.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public String getSymbol() { return symbol; }
    public String getCompanyName() { return companyName; }
    public double getCurrentPrice() { return currentPrice; }
    public double getPreviousClose() { return previousClose; }
}
