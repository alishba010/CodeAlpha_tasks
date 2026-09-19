import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents the overall market: the list of stocks available to
 * trade and the ability to tick all their prices forward.
 */
public class StockMarket {

    private final Map<String, Stock> stocks;

    public StockMarket() {
        stocks = new LinkedHashMap<>();
        initializeStocks();
    }

    private void initializeStocks() {
        addStock(new Stock("AAPL", "Apple Inc.", 185.50));
        addStock(new Stock("GOOGL", "Alphabet Inc.", 142.30));
        addStock(new Stock("MSFT", "Microsoft Corp.", 378.90));
        addStock(new Stock("AMZN", "Amazon.com Inc.", 155.20));
        addStock(new Stock("TSLA", "Tesla Inc.", 242.60));
        addStock(new Stock("NVDA", "NVIDIA Corp.", 495.80));
        addStock(new Stock("META", "Meta Platforms Inc.", 352.40));
        addStock(new Stock("NFLX", "Netflix Inc.", 445.10));
    }

    private void addStock(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }

    public Collection<Stock> getAllStocks() {
        return stocks.values();
    }

    /** Advances every stock's price by one simulated market tick. */
    public void updateAllPrices() {
        for (Stock s : stocks.values()) {
            s.updatePrice();
        }
    }
}
